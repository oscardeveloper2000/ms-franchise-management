package com.epam.franquicias.application.exception;

import java.util.UUID;

public class FranquiciaNoEncontradaException extends RuntimeException {

    public FranquiciaNoEncontradaException(UUID franquiciaId) {
        super("No se encontró la franquicia con id: " + franquiciaId);
    }
}