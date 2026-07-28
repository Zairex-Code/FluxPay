package com.fluxpay.infrastructure.out.kafka;

import com.fluxpay.application.port.out.TransferEventPort;
import com.fluxpay.domain.event.TransferCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferKafkaAdapter implements TransferEventPort {
    private  final KafkaTemplate<String, TransferCreatedEvent> kafkaTemplate;

    @Value("${fluxpay.kafka.topic.transfer-created:fluxpay.transfer.created}")
    private String topic;

    @Override
    public Mono<Void> publishTransferCreated(TransferCreatedEvent event) {
        return Mono.fromFuture(() -> kafkaTemplate.send(topic, event.id(), event))
                .doOnSuccess(result -> log.info(
                        "Successfully published TransferCreatedEvent to kafka topic [{}] for trnasfer ID [{}]",
                        topic, event.id()
                ))
                .doOnError(ex -> log.error(
                        "Failed to publish TransferCreatedEvent to Kafka topic [{}] for transfer ID [{}]",
                        topic, event.id()
                ))
                .doOnError(ex -> log.error(
                        "Failed to pushing TransferCreatedEvent to kafka topic [{}] for transfer ID [{}]",
                        topic, event.id(), ex
                ))
                .then();

    }


}
