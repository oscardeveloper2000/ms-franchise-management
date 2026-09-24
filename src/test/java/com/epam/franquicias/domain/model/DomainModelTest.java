package com.epam.franquicias.domain.model;

import com.epam.franquicias.domain.exception.DomainValidationException;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainModelTest {

    @Test
    void shouldRejectBlankFranchiseName() {
        assertThrows(DomainValidationException.class,
                () -> new Franquicia(UUID.randomUUID(), " ", new ArrayList<>()));
    }

    @Test
    void shouldRejectBlankBranchName() {
        assertThrows(DomainValidationException.class,
                () -> new Sucursal(UUID.randomUUID(), "", new ArrayList<>()));
    }

    @Test
    void shouldRejectNegativeProductStock() {
        assertThrows(DomainValidationException.class,
                () -> new Producto(UUID.randomUUID(), "Producto", -1));
    }

    @Test
    void shouldUpdateProductStockWhenValueIsValid() {
        Producto producto = new Producto(UUID.randomUUID(), "Producto", 10);

        producto.actualizarStock(25);

        assertEquals(25, producto.getCantidadStock());
    }

    @Test
    void shouldRejectNegativeStockWhenUpdating() {
        Producto producto = new Producto(UUID.randomUUID(), "Producto", 10);

        assertThrows(DomainValidationException.class, () -> producto.actualizarStock(-1));
    }
}
