package com.fluxpay.infrastructure.in.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fluxpay.application.port.in.GetAllTransfersUseCase;
import com.fluxpay.application.port.in.GetTransferUseCase;
import com.fluxpay.application.port.in.MakeTransferUseCase;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.infrastructure.in.web.dto.TransferRequest;
import com.fluxpay.infrastructure.in.web.dto.TransferResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;

/**
 * Reactive REST controller exposing HTTP endpoints for bank transfer operations
 *
 * Implements Dependency Inversion by depending strictly on application inbound ports
 */


@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    // Dependency Inversion: Depending strictly on the Inbound port (Interface)
    private final MakeTransferUseCase makeTransferUseCase;
    private final GetTransferUseCase getTransferUseCase;
    private final GetAllTransfersUseCase getAllTransfersUseCase;


    /**
     * Initiates a new money transfer transaction reactively.
     *
     * @param request Validated HTTP request payload containing transfer details
     * @return A Mono emitting the created TransferResponse with HTTP status 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransferResponse> makeTransfer(@Valid @RequestBody TransferRequest request){
        return Mono.just(request)
                .map(req -> Transfer.createInitial(
                                req.originAccount(),
                                req.destinationAccount(),
                                req.amount(),
                                req.description()
                ))
                .flatMap(makeTransferUseCase::execute)
                .map(TransferResponse::fromDomain);

    }


    /**
     * Retrieves a single transfer by its unique string identifier
     *
     * @param id The transfer unique identifier
     * @return  A Mono emitting ResponseEntity with HTTP 200 ok and payload if found, or HTTP 404 Not Found if missing
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransferResponse>> getTransferById(@PathVariable String id) {
        return getTransferUseCase.execute(id)
                .map(TransferResponse::fromDomain)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a reactive stream of all recorded bank transfer.
     *
     * @return A Flux emitting TransferResponse items
     */
    @GetMapping
    public  Flux<TransferResponse> getAllTransfers(){
        return getAllTransfersUseCase.execute()
                .map(TransferResponse::fromDomain);
    }



}
