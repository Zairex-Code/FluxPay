package com.fluxpay.boot.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
@ComponentScan(basePackages = "com.fluxpay.infrastructure")
@EnableR2dbcRepositories(basePackages = "com.fluxpay.infrastructure.out.r2dbc.repository")
public class InfrastructureConfig {

    /**
     * Bean corporativo para forzar la ejecución del schema.sql en R2DBC.
     * Garantiza que la tabla 'transfers' exista antes de procesar cualquier transacción.
     */
    @Bean
    public ConnectionFactoryInitializer databaseInitializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        
        // Busca el archivo schema.sql en la carpeta src/main/resources
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        initializer.setDatabasePopulator(populator);
        
        return initializer;
    }
}