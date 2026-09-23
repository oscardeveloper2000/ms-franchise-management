package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearFranquiciaRequest(@NotBlank(message = "El nombre de la franquicia es obligatorio") String nombre) {
}
