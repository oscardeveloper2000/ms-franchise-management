package com.epam.franquicias.application.exception;

import java.util.UUID;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(UUID productoId) {
        super("No se encontró el producto con id: " + productoId);
    }
}