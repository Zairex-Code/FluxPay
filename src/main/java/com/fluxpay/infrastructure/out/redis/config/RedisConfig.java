package com.fluxpay.infrastructure.out.redis.config;

import com.fluxpay.domain.model.Transfer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Spring Configuration for Redis reactive infrastructure
 *
 * Customizes key/value serialization strategies using UTF-8 strings for keys
 * and Jackson JSON serializer for Transfer domain objects
 */

@Configuration
public class RedisConfig {

    /**
     * Configures a ReactiveRedisTemplate typed for Transfer entities
     *
     * @param factory Non-blocking Redis connection factory
     * @return A fully initialized reactive Redis template
     *
     */
    @Bean
    public ReactiveRedisTemplate<String, Transfer> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory){
        Jackson2JsonRedisSerializer<Transfer> serializer = new Jackson2JsonRedisSerializer<>(Transfer.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Transfer> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Transfer> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
