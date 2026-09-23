package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.exception.ProductoNoEncontradoException;
import com.epam.franquicias.application.exception.SucursalNoEncontradaException;
import com.epam.franquicias.application.port.in.ModificarStockUseCase;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import com.epam.franquicias.domain.model.Producto;
import com.epam.franquicias.domain.model.Sucursal;
import java.util.Objects;
import java.util.UUID;
import reactor.core.publisher.Mono;

public class ModificarStockUseCaseImpl implements ModificarStockUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;

    public ModificarStockUseCaseImpl(FranquiciaRepositoryPort franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Mono<Franquicia> ejecutar(UUID franquiciaId, UUID sucursalId, UUID productoId,
            int nuevaCantidadStock) {
        return franquiciaRepository.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(() -> new FranquiciaNoEncontradaException(franquiciaId)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = franquicia.getSucursales().stream()
                            .filter(sucursalEncontrada -> Objects.equals(sucursalEncontrada.getId(), sucursalId))
                            .findFirst()
                            .orElseThrow(() -> new SucursalNoEncontradaException(sucursalId));

                    Producto producto = sucursal.getProductos().stream()
                            .filter(productoEncontrado -> Objects.equals(productoEncontrado.getId(), productoId))
                            .findFirst()
                            .orElseThrow(() -> new ProductoNoEncontradoException(productoId));

                    producto.actualizarStock(nuevaCantidadStock);
                    return franquiciaRepository.guardar(franquicia);
                });
    }
}