package com.epam.franquicias.application.exception;

import java.util.UUID;

public class SucursalNoEncontradaException extends RuntimeException {

    public SucursalNoEncontradaException(UUID sucursalId) {
        super("No se encontró la sucursal con id: " + sucursalId);
    }
}