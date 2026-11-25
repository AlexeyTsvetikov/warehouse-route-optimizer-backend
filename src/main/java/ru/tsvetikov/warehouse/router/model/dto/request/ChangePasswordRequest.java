package ru.tsvetikov.warehouse.router.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        String oldPassword,
        @NotBlank @Size(min = 6)
        String newPassword
) {}
