package com.fluxpay.boot.config;

import com.fluxpay.application.port.out.ExternalBankPort;
import com.fluxpay.application.port.out.TransferEventPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fluxpay.application.port.in.GetAllTransfersUseCase;
import com.fluxpay.application.port.in.GetTransferUseCase;
import com.fluxpay.application.port.in.MakeTransferUseCase;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.application.usecase.GetAllTransferInteractor;
import com.fluxpay.application.usecase.GetTransferInteractor;
import com.fluxpay.application.usecase.MakeTransferInteractor;

@Configuration
public class UseCaseBeanConfig {

    /**
     * Creates the MakeTransferUseCase bean
     * Spring automatically resolves and injects_
     * - TransferRepository (implemented by TransferRepositoryAdapter)
     * - ExternalBankPort (implemented by ExternalBankAdapter)
     * - TransEventPort (implemented byTransferKafkaAdapter)
     *
     * @param transferRepositoryPort the persistence outbound port
     * @param externalBankPort the external verification outbound port
     * @param transferEventPort the messaging event outbound port
     * @return the pure Java interactor ready to be used by Inbound Adapters
     */
    
    @Bean
    public MakeTransferUseCase makeTransferUseCase(TransferRepositoryPort transferRepositoryPort,
                                                   ExternalBankPort externalBankPort,
                                                   TransferEventPort transferEventPort) {
        return new MakeTransferInteractor(transferRepositoryPort, externalBankPort, transferEventPort);
    }

    @Bean
    public GetTransferUseCase getTransferUseCase(TransferRepositoryPort transferRepositoryPort){
        return new GetTransferInteractor(transferRepositoryPort);
    }

    @Bean
    public GetAllTransfersUseCase getAllTransfersUseCase(TransferRepositoryPort transferRepositoryPort){
        return new GetAllTransferInteractor(transferRepositoryPort);
    }
}
