package com.fluxpay.infrastructure.in.web.dto;


import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransferRequest(
    @NotBlank(message= "Origin account is mandatory")
    String originAccount,

    @NotBlank(message="Destination account is mandatory")
    String destinationAccount,

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message= "Transfer amount must be greater than 0")
    BigDecimal amount
) {
    
}
