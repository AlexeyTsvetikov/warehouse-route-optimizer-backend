package ru.tsvetikov.warehouse.router.testdata;

import ru.tsvetikov.warehouse.router.model.db.entity.Location;
import ru.tsvetikov.warehouse.router.model.dto.request.LocationRequest;
import ru.tsvetikov.warehouse.router.model.dto.response.LocationResponse;
import ru.tsvetikov.warehouse.router.model.enums.LocationType;

public final class LocationTestData {

    private LocationTestData() {
    }

    public static LocationRequest request() {
        return new LocationRequest("recv-01", LocationType.RECEIVING,
                1.0, 2.0, 3.0, 100.0, 10.0, 15.0, "Receiving");
    }

    public static LocationRequest request(String code) {
        return new LocationRequest(code, LocationType.RECEIVING,
                1.0, 2.0, 3.0, 100.0, 10.0, 15.0, "Receiving");
    }

    public static Location.LocationBuilder entityBuilder() {
        return Location.builder()
                .type(LocationType.RECEIVING)
                .width(1.0).height(2.0).depth(3.0).maxWeight(100.0)
                .coordX(10.0).coordY(15.0)
                .description("Receiving");
    }

    public static LocationResponse response() {
        return new LocationResponse(1L, "RECV-01", LocationType.RECEIVING,
                1.0, 2.0, 3.0, 6.0, 100.0, 10.0, 15.0, "Receiving", true);
    }
}
