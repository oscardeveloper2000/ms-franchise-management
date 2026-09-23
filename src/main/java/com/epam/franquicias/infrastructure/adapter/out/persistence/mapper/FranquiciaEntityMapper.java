package com.epam.franquicias.infrastructure.adapter.out.persistence.mapper;

import com.epam.franquicias.domain.model.Franquicia;
import com.epam.franquicias.domain.model.Producto;
import com.epam.franquicias.domain.model.Sucursal;
import com.epam.franquicias.infrastructure.adapter.out.persistence.entity.FranquiciaEntity;
import com.epam.franquicias.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.epam.franquicias.infrastructure.adapter.out.persistence.entity.SucursalEntity;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class FranquiciaEntityMapper {

    private FranquiciaEntityMapper() {
    }

    public static FranquiciaEntity toEntity(Franquicia franquicia) {
        FranquiciaEntity entity = new FranquiciaEntity();
        entity.setId(franquicia.getId().toString());
        entity.setNombre(franquicia.getNombre());
        entity.setSucursales(franquicia.getSucursales().stream()
                .map(FranquiciaEntityMapper::toEntity)
                .collect(Collectors.toList()));
        return entity;
    }

    public static Franquicia toDomain(FranquiciaEntity entity) {
        List<Sucursal> sucursales = safeList(entity.getSucursales()).stream()
                .map(FranquiciaEntityMapper::toDomain)
                .collect(Collectors.toList());
        return new Franquicia(UUID.fromString(entity.getId()), entity.getNombre(), sucursales);
    }

    private static SucursalEntity toEntity(Sucursal sucursal) {
        SucursalEntity entity = new SucursalEntity();
        entity.setId(sucursal.getId().toString());
        entity.setNombre(sucursal.getNombre());
        entity.setProductos(sucursal.getProductos().stream()
                .map(FranquiciaEntityMapper::toEntity)
                .collect(Collectors.toList()));
        return entity;
    }

    private static ProductoEntity toEntity(Producto producto) {
        ProductoEntity entity = new ProductoEntity();
        entity.setId(producto.getId().toString());
        entity.setNombre(producto.getNombre());
        entity.setCantidadStock(producto.getCantidadStock());
        return entity;
    }

    private static Sucursal toDomain(SucursalEntity entity) {
        List<Producto> productos = safeList(entity.getProductos()).stream()
                .map(FranquiciaEntityMapper::toDomain)
                .collect(Collectors.toList());
        return new Sucursal(UUID.fromString(entity.getId()), entity.getNombre(), productos);
    }

    private static Producto toDomain(ProductoEntity entity) {
        return new Producto(UUID.fromString(entity.getId()), entity.getNombre(), entity.getCantidadStock());
    }

    private static <T> List<T> safeList(List<T> values) {
        return values == null ? Collections.emptyList() : values;
    }
}