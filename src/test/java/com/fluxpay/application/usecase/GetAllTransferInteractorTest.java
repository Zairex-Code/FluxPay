package com.fluxpay.application.usecase;


import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetAllTransferInteractorTest {

    @Mock
    private TransferRepositoryPort transferRepositoryPort;


    private GetAllTransferInteractor getAllTransferInteractor;

    private Transfer transfer1;
    private Transfer transfer2;

    @BeforeEach
    void setUp(){
        getAllTransferInteractor = new GetAllTransferInteractor(transferRepositoryPort);

        transfer1 = Transfer.builder()
                .id("TR-101")
                .originAccount("ACC-100")
                .destinationAccount("ACC-200")
                .amount(new BigDecimal("150.00"))
                .status(TransferStatus.COMPLETED)
                .description("First transfer")
                .createdAt(Instant.now())
                .build();

        transfer2 = Transfer.builder()
                .id("TR-102")
                .originAccount("ACC-300")
                .destinationAccount("ACC-400")
                .amount(new BigDecimal("500.00"))
                .status(TransferStatus.COMPLETED)
                .description("Second transfer")
                .createdAt(Instant.now())
                .build();

    }

    @Test
    @DisplayName("Should return Flux of all transfers when repository contains records")
    void execute_WhenTransferExist_ShouldReturnAllTransfers(){
        // ARRANGE
        when(transferRepositoryPort.findAll()).thenReturn(Flux.just(transfer1, transfer2));

        // ACT & ASSERT
        StepVerifier.create(getAllTransferInteractor.execute())
                .expectNextMatches(transfer -> "TR-101".equals(transfer.getId()))
                .expectNextMatches(transfer -> "TR-102".equals(transfer.getId()))
                .verifyComplete();


        verify(transferRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty Flux when repository contains no records")
    void execute_WhenNoTransferExist_ShouldReturnEmptyFlux(){

        // ARRANGE
        when(transferRepositoryPort.findAll()).thenReturn(Flux.empty());


        // ACT & ASSERT
        StepVerifier.create(getAllTransferInteractor.execute())
                .verifyComplete();

        verify(transferRepositoryPort, times(1)).findAll();

    }

    @Test
    @DisplayName("Should propage error reactively when repository query fails")
    void execute_WhenRepositoryFails_ShouldEmitError() {
        // ARRANGE
        when(transferRepositoryPort.findAll())
                .thenReturn(Flux.error(new RuntimeException("Database connection failure ")));

        // ACT & ASSERT
        StepVerifier.create(getAllTransferInteractor.execute())
                .expectErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Database connection failure")
                ).verify();

        verify(transferRepositoryPort,times(1)).findAll();


    }

}
