package com.fluxpay.infrastructure.in.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.port.in.MakeTransferUseCase;
import com.fluxpay.infrastructure.in.web.dto.TransferRequest;
import com.fluxpay.infrastructure.in.web.dto.TransferResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    // Dependency Inversion: Depending strictly on the Inbound port (Interface)
    private final MakeTransferUseCase makeTransferUseCase;

    /**
     * Endpoint to initiate a new transfer
     * we use WebFlux to handle request in a non-blocking, message-driven way
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<TransferResponse> initiateTransfer(@Valid @RequestBody Mono<TransferRequest> requestMono ) {
        
        return requestMono
                        .map(this::toDomain) // Convert DTO to Domain (Translate)
                        .flatMap(makeTransferUseCase::execute) // Pass to Use Case (non-blocking)
                        .map(this::toDto); // Convert Domain to DTO (Translate back)
        
    }

    /**
     * Helper method to translate Request DTO to pure Domain Model.
     */

    private Transfer toDomain(TransferRequest request) {
        return Transfer.builder()
                .id(UUID.randomUUID().toString())
                .originAccount(request.originAccount())
                .destinationAccount(request.destinationAccount())
                .amount(request.amount())
                .build();
    }


    /**
     * Helper method to translate Domain Model to Response DTO
     */

    private TransferResponse toDto(Transfer domain){
        return new TransferResponse(
            domain.getId(),
            domain.getOriginAccount(),
            domain.getDestinationAccount(),
            domain.getAmount(),
            domain.getStatus() != null ? domain.getStatus().name() : "PENDING",
            domain.getCreatedAt()
        );
    }
    
    
}
