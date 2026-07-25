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




@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    // Dependency Inversion: Depending strictly on the Inbound port (Interface)
    private final MakeTransferUseCase makeTransferUseCase;
    private final GetTransferUseCase getTransferUseCase;
    private final GetAllTransfersUseCase getAllTransfersUseCase;

    /**
     * Endpoint to initiate a new transfer
     * we use WebFlux to handle request in a non-blocking, message-driven way
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

    @GetMapping("/{id}")
    public Mono<TransferResponse> getTransferById(@PathVariable String id) {
        return getTransferUseCase.execute(id)
                .map(TransferResponse::fromDomain);
    }

    @GetMapping
    public  Flux<TransferResponse> getAllTransfers(){
        return getAllTransfersUseCase.execute()
                .map(TransferResponse::fromDomain);
    }



}
