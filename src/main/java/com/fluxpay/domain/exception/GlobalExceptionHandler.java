package com.fluxpay.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.net.URI;
import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
     @ExceptionHandler(InvalidTransferException.class)
    public ProblemDetail handleInvalidTransferException(InvalidTransferException ex){
         log.warn("Business domain violation intercepted: {}", ex.getMessage());

         ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                 HttpStatus.BAD_REQUEST, ex.getMessage());
         problemDetail.setTitle("Invalid Transfer Request");
         problemDetail.setType(URI.create("https://api.fluxpay.com/errors/invalid-transfer"));
         problemDetail.setProperty("timestamp", Instant.now());

         return problemDetail;
     }

    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationException(WebExchangeBindException ex) {
        log.warn("Payload validation failed on reactive endpoint: {}", ex.getMessage());

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((first, second) -> first + ", " + second)
                .orElse("Invalid request payload");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://api.fluxpay.com/errors/validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
