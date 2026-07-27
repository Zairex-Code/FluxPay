package com.fluxpay.infrastructure.in.web.dto;

import com.fluxpay.domain.model.Transfer;
import com.fluxpay.infrastructure.security.MaskingUtils;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable HTTP Response Data Transfer Object (DTO) from bank transfers
 *
 * Masks sensitive account information (PII/PCI-DSS compliance) before returning
 * data to external clients
 *
 * @param id                    unique transfer identifier
 * @param originAccount         Masked origin account number
 * @param destinationAccount    Masked destination account number
 * @param amount                Transferred monetary amount
 * @param status                Current lifecycle status string
 * @param createdAt             UTC timestamp of creation
 */

public record TransferResponse(
    String id,
    String originAccount,
    String destinationAccount,
    BigDecimal amount,
    String status,
    Instant createdAt
) {
    /**
     * Static factory method to map a Domain model into an immutable DTO with PII masking
     * @param domain The domain transfer model
     * @return A new TransferResponse instance with masked account numbers
     */
    public static TransferResponse fromDomain(Transfer domain){
        return new TransferResponse(
                domain.getId(),
                MaskingUtils.maskAccount(domain.getOriginAccount()),
                MaskingUtils.maskAccount(domain.getDestinationAccount()),
                domain.getAmount(),
                domain.getStatus() != null ? domain.getStatus().name() : null,
                domain.getCreatedAt()
        );
    }
}
