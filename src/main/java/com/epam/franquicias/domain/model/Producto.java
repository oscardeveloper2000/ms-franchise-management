package com.epam.franquicias.domain.model;

import com.epam.franquicias.domain.exception.DomainValidationException;
import java.util.UUID;

public class Producto {

    private UUID id;
    private String nombre;
    private int cantidadStock;

    public Producto(UUID id, String nombre, int cantidadStock) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre;
        actualizarStock(cantidadStock);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainValidationException("El nombre del producto no puede ser nulo ni vacío");
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void actualizarNombre(String nuevoNombre) {
        validarNombre(nuevoNombre);
        this.nombre = nuevoNombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadStock() {
        return cantidadStock;
    }

    public void actualizarStock(int nuevaCantidadStock) {
        if (nuevaCantidadStock < 0) {
            throw new DomainValidationException("La cantidad de stock no puede ser negativa");
        }
        this.cantidadStock = nuevaCantidadStock;
    }

    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }
}