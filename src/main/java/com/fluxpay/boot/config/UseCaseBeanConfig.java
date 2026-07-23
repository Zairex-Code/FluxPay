package com.fluxpay.boot.config;

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
     * Creates de MakeTransferUseCase bean
     * Spring automatically resolves and injects the TransferRepositoryPort
     * (Which is implemented by our TransferRepositoryAdapter in the Infrastructure)
     * 
     * @param transferRepositoryPort the outbound port injected by Spring
     * @return the pure Java interact ready to be used by the Inbound Adapters
     */
    
    @Bean
    public MakeTransferUseCase makeTransferUseCase(TransferRepositoryPort transferRepositoryPort) {
        return new MakeTransferInteractor(transferRepositoryPort);
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
