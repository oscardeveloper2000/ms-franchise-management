package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearSucursalRequest(@NotBlank(message = "El nombre de la sucursal es obligatorio") String nombre) {
}
