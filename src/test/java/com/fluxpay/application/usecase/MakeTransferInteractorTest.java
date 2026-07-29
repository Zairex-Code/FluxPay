package com.fluxpay.application.usecase;

import com.fluxpay.application.port.out.ExternalBankPort;
import com.fluxpay.application.port.out.TransferEventPort;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.event.TransferCreatedEvent;
import com.fluxpay.domain.exception.InvalidTransferException;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MakeTransferInteractor verifying reactive execution pipelines
 * and domain validation error scenarios.
 */
@ExtendWith(MockitoExtension.class)
class MakeTransferInteractorTest {

    @Mock
    private TransferRepositoryPort transferRepositoryPort;

    @Mock
    private ExternalBankPort externalBankPort;

    @Mock
    private TransferEventPort transferEventPort;

    private MakeTransferInteractor makeTransferInteractor;

    @BeforeEach
    void setUp() {
        makeTransferInteractor = new MakeTransferInteractor(
                transferRepositoryPort,
                externalBankPort,
                transferEventPort
        );
    }

    @Test
    @DisplayName("Should execute transfer successfully when payload is valid")
    void execute_WhenValidTransfer_ShouldReturnSavedTransfer() {
        // 1. ARRANGE
        Transfer inputTransfer = Transfer.builder()
                .originAccount("ACC-100")
                .destinationAccount("ACC-200")
                .amount(new BigDecimal("250.00"))
                .status(TransferStatus.PENDING)
                .build();

        Transfer savedTransfer = Transfer.builder()
                .id("TR-999")
                .originAccount("ACC-100")
                .destinationAccount("ACC-200")
                .amount(new BigDecimal("250.00"))
                .status(TransferStatus.COMPLETED)
                .build();

        // Mocks de los puertos externos
        when(externalBankPort.verifyExternalAccount(anyString(), any(BigDecimal.class)))
                .thenReturn(Mono.just(true));

        when(transferRepositoryPort.save(any(Transfer.class)))
                .thenReturn(Mono.just(savedTransfer));

        when(transferEventPort.publishTransferCreated(any(TransferCreatedEvent.class)))
                .thenReturn(Mono.empty());

        // 2. ACT AND ASSERT
        StepVerifier.create(makeTransferInteractor.execute(inputTransfer))
                .expectNextMatches(result ->
                        "TR-999".equals(result.getId()) &&
                                result.getStatus() == TransferStatus.COMPLETED &&
                                "ACC-100".equals(result.getOriginAccount()) &&
                                "ACC-200".equals(result.getDestinationAccount()))
                .verifyComplete();

        verify(externalBankPort).verifyExternalAccount("ACC-100", new BigDecimal("250.00"));
        verify(transferRepositoryPort).save(argThat(transfer ->
                transfer.getStatus() == TransferStatus.COMPLETED &&
                        "ACC-100".equals(transfer.getOriginAccount())
        ));
        verify(transferEventPort).publishTransferCreated(any(TransferCreatedEvent.class));
    }

    @Test
    @DisplayName("Should emit InvalidTransferException when request payload is null")
    void execute_WhenPayloadIsNull_ShouldEmitError() {
        // ACT AND ASSERT
        StepVerifier.create(makeTransferInteractor.execute(null))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(InvalidTransferException.class)
                            .hasMessageContaining("payload cannot be null");
                }).verify();

        verify(transferRepositoryPort, never()).save(any());
        verify(externalBankPort, never()).verifyExternalAccount(any(), any());
        verify(transferEventPort, never()).publishTransferCreated(any());
    }

    @Test
    @DisplayName("Should emit InvalidTransferException when amount is Zero or negative")
    void execute_WhenAmountIsInvalid_ShouldEmitError() {
        // ARRANGE
        Transfer invalidTransfer = Transfer.builder()
                .originAccount("ACC-100")
                .destinationAccount("ACC-200")
                .amount(BigDecimal.ZERO)
                .build();

        // ACT & ASSERT
        StepVerifier.create(makeTransferInteractor.execute(invalidTransfer))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(InvalidTransferException.class)
                            .hasMessageContaining("strictly greater than zero");
                }).verify();

        verify(transferRepositoryPort, never()).save(any());
        verify(externalBankPort, never()).verifyExternalAccount(any(), any());
        verify(transferEventPort, never()).publishTransferCreated(any());
    }

    @Test
    @DisplayName("Should emit InvalidTransferException when origin and destination account are identical")
    void execute_WhenSameAccounts_ShouldEmitError() {
        // ARRANGE
        Transfer invalidTransfer = Transfer.builder()
                .originAccount("ACC-100")
                .destinationAccount("ACC-100")
                .amount(new BigDecimal("250.00"))
                .build();

        // ACT AND ASSERT
        StepVerifier.create(makeTransferInteractor.execute(invalidTransfer))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(InvalidTransferException.class)
                            .hasMessageContaining("cannot be the same");
                }).verify();

        verify(transferRepositoryPort, never()).save(any());
        verify(externalBankPort, never()).verifyExternalAccount(any(), any());
        verify(transferEventPort, never()).publishTransferCreated(any());
    }

    @Test
    @DisplayName("Should emit error when external bank verification fails")
    void execute_WhenExternalBankFails_ShouldEmitError() {
        // ARRANGE
        Transfer inputTransfer = Transfer.builder()
                .originAccount("ACC-100")
                .destinationAccount("ACC-200")
                .amount(new BigDecimal("250.00"))
                .build();

        when(externalBankPort.verifyExternalAccount(anyString(), any(BigDecimal.class)))
                .thenReturn(Mono.error(new InvalidTransferException("Insufficient funds in external account")));

        // ACT AND ASSERT
        StepVerifier.create(makeTransferInteractor.execute(inputTransfer))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(InvalidTransferException.class)
                            .hasMessageContaining("Insufficient funds in external account");
                }).verify();

        verify(externalBankPort).verifyExternalAccount("ACC-100", new BigDecimal("250.00"));
        verify(transferRepositoryPort, never()).save(any());
        verify(transferEventPort, never()).publishTransferCreated(any());
    }
}