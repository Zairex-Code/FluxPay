package com.fluxpay.infrastructure.in.web;


import com.fluxpay.application.port.in.GetAllTransfersUseCase;
import com.fluxpay.application.port.in.GetTransferUseCase;
import com.fluxpay.application.port.in.MakeTransferUseCase;
import com.fluxpay.domain.exception.InvalidTransferException;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;
import com.fluxpay.infrastructure.in.web.TransferController;
import com.fluxpay.infrastructure.in.web.dto.TransferRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * Unit test suite for TransferController using WebTestClient
 *
 * Verifies HTTP status codes, JSON payload mapping, and reactive exception handling
 * for the REST web adapter
 */
@WebFluxTest(TransferController.class)
public class TransferControllerTest {

    @Autowired // in testing autowired is allowed
    private WebTestClient webTestClient;

    @MockBean
    private MakeTransferUseCase makeTransferUseCase;

    @MockBean
    private GetTransferUseCase getTransferUseCase;

    @MockBean
    private GetAllTransfersUseCase getAllTransfersUseCase;


    @Test
    @DisplayName("POST /api/v1/transfer - Should create transfer successfully (HTTP 201 Created)")
    void shouldCreateTransferSuccessfully(){
        TransferRequest request = new TransferRequest(
                "ACC-1001",
                "ACC-2002",
                new BigDecimal("150.00"),
                "Payment for services"
        );

        Transfer expectedTransfer = Transfer.builder()
                .id("TRF-999")
                .originAccount("ACC-1001")
                .destinationAccount("ACC-2002")
                .amount(new BigDecimal("150.00"))
                .status(TransferStatus.COMPLETED)
                .description("Payment for services")
                .createdAt(Instant.now())
                .build();

        when(makeTransferUseCase.execute(any(Transfer.class))).thenReturn(Mono.just(expectedTransfer));

        webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("TRF-999")
                .jsonPath("$.status").isEqualTo("COMPLETED")
                .jsonPath("$.originAccount").isEqualTo("ACC-1001")
                .jsonPath("$.destinationAccount").isEqualTo("ACC-2002");

    }

    @Test
    @DisplayName("GET /api/v1/transfers/{1} - Should return 400 Bad Request when transfer ID does not exist")
    void shouldReturnBadRequestWhenTransferNotFound(){
        when(getTransferUseCase.execute("INVALID-ID"))
                .thenReturn(Mono.error(new InvalidTransferException("Transfer not found with ID: INVALID-ID")));

        webTestClient.get()
                .uri("/api/v1/transfers/INVALID-ID")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Transfer not found with ID: INVALID-ID");
    }
}