package ru.tsvetikov.warehouse.router.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tsvetikov.warehouse.router.model.db.entity.Location;
import ru.tsvetikov.warehouse.router.model.dto.response.RouteResponse;
import ru.tsvetikov.warehouse.router.model.dto.response.WarehouseTaskResponse;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutingService {

    private final LocationService locationService;

    public RouteResponse calculateCombinedRoute(List<WarehouseTaskResponse> allTasks, double startX, double startY) {
        if (allTasks.isEmpty()) {
            return new RouteResponse(List.of(), List.of(), 0, 0);
        }

        Set<String> codes = allTasks.stream()
                .map(this::getLocationCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Location> locationsMap = locationService.getLocationsByCodes(codes);

        double fifoDistance = calculateTotalDistance(allTasks, startX, startY, locationsMap);

        List<WarehouseTaskResponse> remaining = new ArrayList<>(allTasks);
        List<WarehouseTaskResponse> tspOrder = new ArrayList<>();
        double currentX = startX, currentY = startY;

        while (!remaining.isEmpty()) {
            double finalCurrentX = currentX, finalCurrentY = currentY;
            WarehouseTaskResponse nearest = remaining.stream()
                    .min(Comparator.comparingDouble(t -> distanceFromPoint(t, finalCurrentX, finalCurrentY, locationsMap)))
                    .orElseThrow();

            tspOrder.add(nearest);
            remaining.remove(nearest);

            Location loc = getLocation(nearest, locationsMap);
            if (loc != null) {
                currentX = loc.getCoordX();
                currentY = loc.getCoordY();
            }
        }

        double tspDistance = calculateTotalDistance(tspOrder, startX, startY, locationsMap);

        log.info("Combined route: FIFO={}m, TSP={}m, Saved={}m ({}%)",
                fifoDistance, tspDistance,
                Math.round((fifoDistance - tspDistance) * 100.0) / 100.0,
                fifoDistance > 0 ? (int) Math.round((1 - tspDistance / fifoDistance) * 100) : 0);

        return new RouteResponse(allTasks, tspOrder, fifoDistance, tspDistance);
    }

    private double distanceFromPoint(WarehouseTaskResponse task, double x, double y, Map<String, Location> locationsMap) {
        Location loc = getLocation(task, locationsMap);
        if (loc == null) return Double.MAX_VALUE;
        return Math.abs(loc.getCoordX() - x) + Math.abs(loc.getCoordY() - y);
    }

    private double calculateTotalDistance(List<WarehouseTaskResponse> tasks, double startX, double startY, Map<String, Location> locationsMap) {
        double total = 0;
        double prevX = startX, prevY = startY;

        for (WarehouseTaskResponse task : tasks) {
            Location loc = getLocation(task, locationsMap);
            if (loc != null) {
                total += Math.abs(loc.getCoordX() - prevX) + Math.abs(loc.getCoordY() - prevY);
                prevX = loc.getCoordX();
                prevY = loc.getCoordY();
            }
        }

        return Math.round(total * 100.0) / 100.0;
    }

    private Location getLocation(WarehouseTaskResponse task, Map<String, Location> locationsMap) {
        String code = getLocationCode(task);
        return code != null ? locationsMap.get(code.toUpperCase()) : null;
    }

    private String getLocationCode(WarehouseTaskResponse task) {
        if (task.sourceLocationCode() != null) return task.sourceLocationCode();
        if (task.targetLocationCode() != null) return task.targetLocationCode();
        return null;
    }
}