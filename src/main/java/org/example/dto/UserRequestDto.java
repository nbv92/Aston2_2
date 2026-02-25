package org.example.dto;

import jakarta.validation.constraints.*;

public record UserRequestDto(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull @Min(0) Integer age
) {}