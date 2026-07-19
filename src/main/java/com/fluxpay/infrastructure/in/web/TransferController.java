package com.fluxpay.infrastructure.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.port.in.MakeTransferUseCase;

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
    public Mono<Transfer> initiateTransfer(@RequestBody Mono<Transfer> transferRequestMono ) {
        
        // The controller simply recives the stram (Mono) and flatMaps it
        // to pass the actual data to the Domain Layer.
        return transferRequestMono.flatMap(transfer-> makeTransferUseCase.execute(transfer));
    }
    
    
}
