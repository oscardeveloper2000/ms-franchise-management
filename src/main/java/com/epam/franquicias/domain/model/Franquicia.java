package com.epam.franquicias.domain.model;

import com.epam.franquicias.domain.exception.DomainValidationException;
import java.util.List;
import java.util.UUID;

public class Franquicia {

    private UUID id;
    private String nombre;
    private List<Sucursal> sucursales;

    public Franquicia(UUID id, String nombre, List<Sucursal> sucursales) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre;
        this.sucursales = sucursales;
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainValidationException("El nombre de la franquicia no puede ser nulo ni vacío");
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

    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursal> sucursales) {
        this.sucursales = sucursales;
    }
}