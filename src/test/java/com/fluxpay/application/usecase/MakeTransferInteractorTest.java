package com.fluxpay.application.usecase;

import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeTransferInteractorTest {

    @Mock
    private TransferRepositoryPort transferRepositoryPort;

    @InjectMocks
    private MakeTransferInteractor makeTransferInteractor;

    @Test
    @DisplayName("Debe asignar estado PENDING y timestamp al ejecutar la transferencia")
    void shouldSetPendingStatusAndTimestampWhenExecutingTransfer() {
        // 1. ARRANGE (Preparar el escenario)
        Transfer transferInput = Transfer.builder()
                .id("test-uuid-123")
                .originAccount("111111111")
                .destinationAccount("222222222")
                .amount(new BigDecimal("250.00"))
                .build();

        // Le indicamos a Mockito que al guardar, devuelva el mismo objeto que recibe
        when(transferRepositoryPort.save(any(Transfer.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // 2. ACT (Ejecutar el interactor)
        Mono<Transfer> resultMono = makeTransferInteractor.execute(transferInput);

        // 3. ASSERT (Verificar reactivamente con StepVerifier)
        StepVerifier.create(resultMono)
                .assertNext(transfer -> {
                    assertNotNull(transfer);
                    assertEquals("test-uuid-123", transfer.getId());
                    assertEquals(TransferStatus.PENDING, transfer.getStatus());
                    assertNotNull(transfer.getCreatedAt(), "El timestamp debe asignarse en el interactor");
                })
                .verifyComplete();
    }
}