package com.fluxpay.application.port.out;

import com.fluxpay.domain.event.TransferCreatedEvent;
import reactor.core.publisher.Mono;


/**
 * Outbound port interface for publishing transfer domain events
 */

public interface TransferEventPort {

    /**
     * Reactively publishes a transfer event to the external messaging system (kafka)
     *
     * @param event The immutable transfer domain event
     * @return Mono<Void> Completing when the event is sent
     */
    Mono<Void> publishTransferCreated(TransferCreatedEvent event);
}
