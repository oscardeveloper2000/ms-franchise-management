package com.epam.franquicias.domain.model;

import com.epam.franquicias.domain.exception.DomainValidationException;
import java.util.List;
import java.util.UUID;

public class Sucursal {

    private UUID id;
    private String nombre;
    private List<Producto> productos;

    public Sucursal(UUID id, String nombre, List<Producto> productos) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre;
        this.productos = productos;
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainValidationException("El nombre de la sucursal no puede ser nulo ni vacío");
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

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}