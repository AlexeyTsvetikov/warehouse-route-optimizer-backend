package ru.tsvetikov.warehouse.router.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tsvetikov.warehouse.router.model.db.entity.Location;
import ru.tsvetikov.warehouse.router.model.dto.response.RouteResponse;
import ru.tsvetikov.warehouse.router.model.dto.response.WarehouseTaskResponse;
import ru.tsvetikov.warehouse.router.testdata.LocationTestData;
import ru.tsvetikov.warehouse.router.testdata.WarehouseTaskTestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RoutingServiceTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private RoutingService routingService;

    @Test
    void shouldReturnEmptyRouteWhenNoTasks() {
        RouteResponse result = routingService.calculateCombinedRoute(List.of(), 20.0, 40.0);

        assertThat(result.fifoOrder()).isEmpty();
        assertThat(result.tspOrder()).isEmpty();
        assertThat(result.fifoDistance()).isEqualTo(0);
        assertThat(result.tspDistance()).isEqualTo(0);
        verifyNoInteractions(locationService);
    }

    @Test
    void shouldReturnSameDistanceForOneWarehouseTask() {
        WarehouseTaskResponse response = WarehouseTaskTestData.response().toBuilder()
                .sourceLocationCode("RECV-01").targetLocationCode("RECV-04").build();
        Location location = LocationTestData.entityBuilder().id(1L).code("RECV-01").build();

        when(locationService.getLocationsByCodes(anyCollection()))
                .thenReturn(Map.of("RECV-01", location));

        RouteResponse result = routingService.calculateCombinedRoute(List.of(response), 20.0, 40.0);

        assertThat(result.fifoOrder()).containsExactly(response);
        assertThat(result.tspOrder()).containsExactly(response);
        assertThat(result.fifoDistance()).isEqualTo(35.0);
        assertThat(result.tspDistance()).isEqualTo(35.0);
        verify(locationService, times(1)).getLocationsByCodes(Set.of("RECV-01"));
    }

    @Test
    void shouldReorderTasksWhenTspFindsShorterRoute() {
        WarehouseTaskResponse task1 = WarehouseTaskTestData.response().toBuilder()
                .id(1L).sourceLocationCode("A").targetLocationCode(null).build();
        WarehouseTaskResponse task2 = WarehouseTaskTestData.response().toBuilder()
                .id(2L).sourceLocationCode("B").targetLocationCode(null).build();

        Location locA = LocationTestData.entityBuilder().id(1L).code("A").coordX(100.0).coordY(100.0).build();
        Location locB = LocationTestData.entityBuilder().id(2L).code("B").coordX(10.0).coordY(10.0).build();

        when(locationService.getLocationsByCodes(anyCollection()))
                .thenReturn(Map.of("A", locA, "B", locB));

        RouteResponse result = routingService.calculateCombinedRoute(List.of(task1, task2), 0.0, 0.0);

        assertThat(result.fifoOrder()).containsExactly(task1, task2);
        assertThat(result.tspOrder()).containsExactly(task2, task1);
        assertThat(result.fifoDistance()).isEqualTo(380.0);
        assertThat(result.tspDistance()).isEqualTo(200.0);
        verify(locationService, times(1)).getLocationsByCodes(Set.of("A", "B"));
    }

    @Test
    void shouldSkipTaskWithoutLocationInDistanceCalculation() {
        WarehouseTaskResponse task1 = WarehouseTaskTestData.response().toBuilder()
                .id(1L).sourceLocationCode("A").targetLocationCode(null).build();
        WarehouseTaskResponse task2 = WarehouseTaskTestData.response().toBuilder()
                .id(2L).sourceLocationCode(null).targetLocationCode(null).build();

        Location locA = LocationTestData.entityBuilder().id(1L).code("A").coordX(100.0).coordY(100.0).build();

        when(locationService.getLocationsByCodes(anyCollection()))
                .thenReturn(Map.of("A", locA));

        RouteResponse result = routingService.calculateCombinedRoute(List.of(task1, task2), 0.0, 0.0);

        assertThat(result.fifoOrder()).containsExactly(task1, task2);
        assertThat(result.tspOrder()).containsExactly(task1, task2);
        assertThat(result.fifoDistance()).isEqualTo(200.0);
        assertThat(result.tspDistance()).isEqualTo(200.0);
        verify(locationService, times(1)).getLocationsByCodes(Set.of("A"));
    }

    @Test
    void shouldReturnRouteWhenSourceLocationIsNull() {
        WarehouseTaskResponse task1 = WarehouseTaskTestData.response().toBuilder()
                .id(1L).sourceLocationCode(null).targetLocationCode("A").build();
        WarehouseTaskResponse task2 = WarehouseTaskTestData.response().toBuilder()
                .id(2L).sourceLocationCode(null).targetLocationCode("B").build();

        Location locA = LocationTestData.entityBuilder().id(1L).code("A").coordX(100.0).coordY(100.0).build();
        Location locB = LocationTestData.entityBuilder().id(2L).code("B").coordX(10.0).coordY(10.0).build();

        when(locationService.getLocationsByCodes(anyCollection()))
                .thenReturn(Map.of("A", locA, "B", locB));

        RouteResponse result = routingService.calculateCombinedRoute(List.of(task1, task2), 0.0, 0.0);

        assertThat(result.fifoOrder()).containsExactly(task1, task2);
        assertThat(result.tspOrder()).containsExactly(task2, task1);
        assertThat(result.fifoDistance()).isEqualTo(380.0);
        assertThat(result.tspDistance()).isEqualTo(200.0);
        verify(locationService, times(1)).getLocationsByCodes(Set.of("A", "B"));
    }
}
