package com.fluxpay.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
/**
 * Main entry point of the application.
 * 
 * scanBasePackages = "com.fluxpay": Forces Spring to scan both 'application' and 'infrastructure' packages.
 * @EnableR2dbcRepositories: Explicitly tells Spring where to find the database interfaces.
 */
@SpringBootApplication(scanBasePackages = "com.fluxpay")
@EnableR2dbcRepositories(basePackages = "com.fluxpay.infrastructure.out.r2dbc.repository")
public class TransferEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransferEngineApplication.class, args);
	}

}
