package com.fluxpay.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fluxpay.domain.port.in.MakeTransferUseCase;
import com.fluxpay.domain.port.out.TransferRepositoryPort;
import com.fluxpay.domain.usecase.MakeTransferInteractor;

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
}
