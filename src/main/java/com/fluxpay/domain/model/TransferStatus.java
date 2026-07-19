package com.fluxpay.domain.model;

/**
 * Represents the lifecycle stages of a bank transfer in a distributed system.
 * Essential for SAGA pattern compensations and event-driven architectures.
 */
public enum TransferStatus {
    PENDING,            // The transfer is registered but not yet validated.
    FUNDS_RESERVED,     // The origin account has been successfully debited.
    COMPLETED,          // The destination account has been credited. All done.
    FAILED,             // An error occurred during the initial validation.
    REVERSED            // SAGA Compensation: The destination failed, money returned to origin.
}