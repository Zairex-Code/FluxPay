package com.fluxpay.application.usecase;


import com.fluxpay.application.port.out.TransferCachePort;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.exception.InvalidTransferException;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetTransferInteractorTest {
    @Mock
    private TransferRepositoryPort transferRepositoryPort;

    @Mock
    private TransferCachePort transferCachePort;

    @InjectMocks
    private GetTransferInteractor getTransferInteractor;

    private Transfer mockTransfer;

    @BeforeEach
    void setUp(){
        mockTransfer = Transfer.builder()
                .id("TRF-100")
                .originAccount("ACC-100")
                .destinationAccount("ACC-200")
                .amount(new BigDecimal("300.00"))
                .status(TransferStatus.COMPLETED)
                .description("Test transfer")
                .createdAt(Instant.now())
                .build();
    }


    @Test
    @DisplayName("Should return transfer directly from redis cache when cache HIT occurs")
    void execute_WhenCacheHIt_ShouldReturnFromCacheWithoutQueryingRepository(){
        // ARRANGE: El dato existe en el puerto de chache (Redis)
        when(transferCachePort.get("TRF-100")).thenReturn(Mono.just(mockTransfer));

        // ACT & ASSERT
        StepVerifier.create(getTransferInteractor.execute("TRF-100"))
                .expectNextMatches(result ->
                                            "TRF-100".equals(result.getId()) &&
                                            "ACC-100".equals(result.getOriginAccount())
                ).verifyComplete();


        // VERIFY: Se consulto a Redis, pero NUNCA a la base de datos R2DBC
        verify(transferCachePort, times(1)).get("TRF-100");
        verifyNoInteractions(transferRepositoryPort);
        verify(transferCachePort, never()).put(any());

    }

    @Test
    @DisplayName("Should fallbacj to DB and populate Redis when Cache Miss occurs")
    void execute_WhenCacheMiss_ShouldFetchFromRepositoryAndPopulateCache(){
        // ARRANGE: Redis retorna Mono.empty() (Cache Miss), pero R2DBC si tiene el registro
        when(transferCachePort.get("TRF-100")).thenReturn(Mono.empty());
        when(transferRepositoryPort.findById("TRF-100")).thenReturn(Mono.just(mockTransfer));
        when(transferCachePort.put(mockTransfer)).thenReturn(Mono.just(true));


        // ACT & ASSERT
        StepVerifier.create(getTransferInteractor.execute("TRF-100"))
                .expectNextMatches(result -> "TRF-100".equals(result.getId()))
                .verifyComplete();


        // VERIFY: se busco en cache, luego en BD y finalmente se guardo el resultado en Redis
        verify(transferCachePort, times(1)).get("TRF-100");
        verify(transferRepositoryPort, times(1)).findById("TRF-100");
        verify(transferCachePort, times(1)).put(mockTransfer);

    }


    @Test
    @DisplayName("Should emit InvalidTransferException when ID is null or blank")
    void execute_WhenIdISBlank_ShouldEmitInvalidTransferException(){
        // ACT & ASSERT
        StepVerifier.create(getTransferInteractor.execute("   "))
                .expectErrorSatisfies(throwable ->
                            assertThat(throwable)
                                    .isInstanceOf(InvalidTransferException.class)
                                    .hasMessageContaining("cannot be null or empty")


                ).verify();
        verifyNoInteractions(transferCachePort, transferRepositoryPort);
    }


    @Test
    @DisplayName("Should return empty Mono when transfer is not found in cache or DB ")
    void execute_WhenNotFoundInCacheOrDb_ShouldReturnEmptyMono(){

        // ARRANGE: Ni Redis ni PostgreSQL tiene la transferencia
        when(transferCachePort.get("TRF-UNKNOWN")).thenReturn(Mono.empty());
        when(transferRepositoryPort.findById("TRF-UNKNOWN")).thenReturn(Mono.empty());

        // ACT && ASSERT
        StepVerifier.create(getTransferInteractor.execute("TRF-UNKNOWN"))
                .verifyComplete();

        verify(transferCachePort, times(1)).get("TRF-UNKNOWN");
        verify(transferRepositoryPort, times(1)).findById("TRF-UNKNOWN");
        verify(transferCachePort, never()).put(any());


    }
}
