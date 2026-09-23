package com.epam.franquicias.infrastructure.config;

import com.epam.franquicias.application.port.in.AgregarFranquiciaUseCase;
import com.epam.franquicias.application.port.in.AgregarProductoUseCase;
import com.epam.franquicias.application.port.in.AgregarSucursalUseCase;
import com.epam.franquicias.application.port.in.ActualizarNombreFranquiciaUseCase;
import com.epam.franquicias.application.port.in.ActualizarNombreProductoUseCase;
import com.epam.franquicias.application.port.in.ActualizarNombreSucursalUseCase;
import com.epam.franquicias.application.port.in.EliminarProductoUseCase;
import com.epam.franquicias.application.port.in.ModificarStockUseCase;
import com.epam.franquicias.application.port.in.ObtenerProductoMasStockPorSucursalUseCase;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.application.usecase.AgregarFranquiciaUseCaseImpl;
import com.epam.franquicias.application.usecase.AgregarProductoUseCaseImpl;
import com.epam.franquicias.application.usecase.AgregarSucursalUseCaseImpl;
import com.epam.franquicias.application.usecase.ActualizarNombreFranquiciaUseCaseImpl;
import com.epam.franquicias.application.usecase.ActualizarNombreProductoUseCaseImpl;
import com.epam.franquicias.application.usecase.ActualizarNombreSucursalUseCaseImpl;
import com.epam.franquicias.application.usecase.EliminarProductoUseCaseImpl;
import com.epam.franquicias.application.usecase.ModificarStockUseCaseImpl;
import com.epam.franquicias.application.usecase.ObtenerProductoMasStockPorSucursalUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public AgregarFranquiciaUseCase agregarFranquiciaUseCase(FranquiciaRepositoryPort repository) {
        return new AgregarFranquiciaUseCaseImpl(repository);
    }

    @Bean
    public AgregarSucursalUseCase agregarSucursalUseCase(FranquiciaRepositoryPort repository) {
        return new AgregarSucursalUseCaseImpl(repository);
    }

    @Bean
    public AgregarProductoUseCase agregarProductoUseCase(FranquiciaRepositoryPort repository) {
        return new AgregarProductoUseCaseImpl(repository);
    }

    @Bean
    public EliminarProductoUseCase eliminarProductoUseCase(FranquiciaRepositoryPort repository) {
        return new EliminarProductoUseCaseImpl(repository);
    }

    @Bean
    public ModificarStockUseCase modificarStockUseCase(FranquiciaRepositoryPort repository) {
        return new ModificarStockUseCaseImpl(repository);
    }

    @Bean
    public ObtenerProductoMasStockPorSucursalUseCase obtenerProductoMasStockPorSucursalUseCase(
            FranquiciaRepositoryPort repository) {
        return new ObtenerProductoMasStockPorSucursalUseCaseImpl(repository);
    }

    @Bean
    public ActualizarNombreFranquiciaUseCase actualizarNombreFranquiciaUseCase(FranquiciaRepositoryPort repository) {
        return new ActualizarNombreFranquiciaUseCaseImpl(repository);
    }

    @Bean
    public ActualizarNombreSucursalUseCase actualizarNombreSucursalUseCase(FranquiciaRepositoryPort repository) {
        return new ActualizarNombreSucursalUseCaseImpl(repository);
    }

    @Bean
    public ActualizarNombreProductoUseCase actualizarNombreProductoUseCase(FranquiciaRepositoryPort repository) {
        return new ActualizarNombreProductoUseCaseImpl(repository);
    }
}