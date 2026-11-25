package ru.tsvetikov.warehouse.router.model.dto.response;

import java.util.List;

public record AuthUserResponse(
        String username,
        List<String> roles
) {
}
