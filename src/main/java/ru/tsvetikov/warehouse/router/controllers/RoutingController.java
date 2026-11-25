package ru.tsvetikov.warehouse.router.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tsvetikov.warehouse.router.model.dto.response.RouteResponse;
import ru.tsvetikov.warehouse.router.model.dto.response.WarehouseTaskResponse;
import ru.tsvetikov.warehouse.router.model.enums.WarehouseTaskStatus;
import ru.tsvetikov.warehouse.router.service.RoutingService;
import ru.tsvetikov.warehouse.router.service.WarehouseTaskService;

import java.util.List;

@Tag(name = "Routing", description = "Расчёт оптимального маршрута обхода склада")
@RestController
@RequestMapping("/api/routing")
@RequiredArgsConstructor
public class RoutingController {

    private final WarehouseTaskService taskService;
    private final RoutingService routingService;

    @Operation(summary = "Рассчитать оптимальный маршрут для активных задач текущего пользователя")
    @GetMapping("/my-route")
    public RouteResponse getMyRoute(
            @RequestParam(defaultValue = "0.0") double startX,
            @RequestParam(defaultValue = "0.0") double startY,
            Authentication authentication
    ) {
        String username = authentication.getName();
        List<WarehouseTaskResponse> tasks = taskService
                .getTasksByUserAndStatus(
                        username,
                        List.of(WarehouseTaskStatus.ASSIGNED, WarehouseTaskStatus.IN_PROGRESS),
                        1, 100, "createdAt", Sort.Direction.ASC
                )
                .getContent();

        return routingService.calculateCombinedRoute(tasks, startX, startY);
    }
}
