package com.fluxpay.infrastructure.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
    String transferId,
    String originAccount,
    String destinationAccount,
    BigDecimal amount,
    String status,
    LocalDateTime processedAt
) {
    
}
