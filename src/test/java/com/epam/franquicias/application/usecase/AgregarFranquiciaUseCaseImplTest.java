package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarFranquiciaUseCaseImplTest {

    @Mock
    private FranquiciaRepositoryPort franquiciaRepository;

    @Test
    void shouldCreateAndPersistFranchise() {
        Franquicia persisted = new Franquicia(UUID.randomUUID(), "Franquicia Test", new ArrayList<>());
        when(franquiciaRepository.guardar(any(Franquicia.class))).thenReturn(Mono.just(persisted));
        AgregarFranquiciaUseCaseImpl useCase = new AgregarFranquiciaUseCaseImpl(franquiciaRepository);

        StepVerifier.create(useCase.ejecutar("Franquicia Test"))
                .expectNext(persisted)
                .verifyComplete();

        ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
        verify(franquiciaRepository).guardar(captor.capture());
        assertEquals("Franquicia Test", captor.getValue().getNombre());
        assertEquals(0, captor.getValue().getSucursales().size());
    }

    @Test
    void shouldPropagateDomainValidationError() {
        AgregarFranquiciaUseCaseImpl useCase = new AgregarFranquiciaUseCaseImpl(franquiciaRepository);

        StepVerifier.create(useCase.ejecutar(""))
                .expectErrorMessage("El nombre de la franquicia no puede ser nulo ni vacío")
                .verify();
    }
}
