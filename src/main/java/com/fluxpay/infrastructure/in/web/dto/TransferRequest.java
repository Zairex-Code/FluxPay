package com.fluxpay.infrastructure.in.web.dto;

import java.math.BigDecimal;

public record TransferRequest(
    String originAccount,
    String destinationAccount,
    BigDecimal amount
) {
    
}
