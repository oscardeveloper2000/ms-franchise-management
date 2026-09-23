package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CrearProductoRequest(
        @NotBlank(message = "El nombre del producto es obligatorio") String nombre,
        @PositiveOrZero(message = "La cantidad de stock no puede ser negativa") int cantidadStock) {
}
