package com.fluxpay.infrastructure.out.redis;


import com.fluxpay.application.port.out.TransferCachePort;
import com.fluxpay.domain.model.Transfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Outbound infrastructure adapter implementing TransferCachePort using Redis
 *
 * Provides non-blocking cache operations backed by ReactiveRedisTemplate
 * with an automatic Time-To-Live (TTL) strategy to optimize query latencies.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferRedisAdapter implements TransferCachePort {
    private final ReactiveRedisTemplate<String, Transfer> reactiveRedisTemplate;
    private static final String CACHE_PREFIX = "transfer:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);


    /**
     * Fetches a cached transfer record non-blocking from redis
     *
     * @param id Target Transfer unique ID
     * @return Mono emitting the Transfer if present in redis, or empty  Mono on miss.
     */
    @Override
    public Mono<Transfer> get(String id) {
        String key = CACHE_PREFIX + id;
        return reactiveRedisTemplate.opsForValue().get(key)
                .doOnNext(transfer -> log.info("Cache HIT for transfer ID [{}]", id))
                .doOnError(ex -> log.warn("Error reading from Redis cache for key [{}]: {}", key, ex.getMessage()));
    }

    /**
     * Persists a transfer record into Redis with a 10-minutes expiration window
     *
     * @param transfer Domain model instance to cache
     * @return Mono emitting Boolean indicating storage outcome
     */
    @Override
    public Mono<Boolean> put(Transfer transfer) {
        String key = CACHE_PREFIX + transfer.getId();
        return reactiveRedisTemplate.opsForValue().set(key, transfer, CACHE_TTL)
                .doOnSuccess(saved -> log.info("Cache PUT successful for transfer ID [{}]", transfer.getId()))
                .doOnError(ex -> log.warn("Error Writing toRedis cache for key[{}]: {}", key, ex.getMessage()));
    }
}
