package com.fluxpay.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main entry point of the FluxPay Transfer Engine
 * Clean, simple and delegates all configuration to the  'config' package
 */


@SpringBootApplication
public class TransferEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransferEngineApplication.class, args);
    }
}



