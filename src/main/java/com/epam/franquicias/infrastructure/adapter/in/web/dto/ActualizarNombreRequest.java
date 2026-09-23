package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarNombreRequest(@NotBlank(message = "El nombre no puede ser nulo ni vacío") String nombre) {
}
