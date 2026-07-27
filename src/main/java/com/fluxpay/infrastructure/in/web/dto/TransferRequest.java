package com.fluxpay.infrastructure.in.web.dto;


import java.math.BigDecimal;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Immutable HTTP Data Transfer Object (DTO) for creating new transfers
 *
 * @param originAccount             Source account identifier (mandatory)
 * @param destinationAccount        Target account identifier (mandatory)
 * @param amount                    Monetary amount to transfer (must be strictly positive)
 * @param description               optional transactional reference note
 */
public record TransferRequest(
    @NotBlank(message= "Origin account is mandatory")
    String originAccount,

    @NotBlank(message="Destination account is mandatory")
    String destinationAccount,

    @NotNull(message = "Amount cannot be null")
    @Positive( message= "Transfer amount must be greater than Zero")
    BigDecimal amount,

    String description
) {
    
}
