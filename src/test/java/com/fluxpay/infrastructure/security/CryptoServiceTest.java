package com.fluxpay.infrastructure.security;

import io.netty.handler.codec.base64.Base64Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CryptoServiceTest {

    private CryptoService cryptoService;
    private static final String VALID_SECRET_KEY_BASE64 = Base64.getEncoder()
            .encodeToString("12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8));



    @BeforeEach
    void setUp(){
        cryptoService = new CryptoService();
    }

    @Test
    @DisplayName("Should encrypt and decrypt plaintext payload successfully with valid Base64 key")
    void shouldEncryptAndDecryptSuccessfully(){

        // ARRANGE
        String plainText = "Sensitive-Finantial-Payload-9988";


        // ACT
        String encryptedText = cryptoService.encrypt(plainText, VALID_SECRET_KEY_BASE64);
        String decryptedText = cryptoService.decrypt(encryptedText, VALID_SECRET_KEY_BASE64);

        // ASSERT
        assertThat(encryptedText).isNotNull().isNotEqualTo(plainText);
        assertThat(decryptedText).isEqualTo(plainText);
    }



    @Test
    @DisplayName("Should throw IllegalStateException when decriptng with tampered data or wrong key")
    void shouldThrowExceptionWhenDecryptingTamperedData(){
        String invalidCiphertextBase64 = Base64.getEncoder()
                .encodeToString("TamperedInvalidDataPayload".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> cryptoService.decrypt(invalidCiphertextBase64, VALID_SECRET_KEY_BASE64))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Crypto error");

    }


    @Test
    @DisplayName("Should throw IllegalStateException when secret key Base64 is invalid")
    void shouldThrowExceptionWhenKeyIsInvalid(){
        String plainText = "Hello World";
        String invalidKeyBase64 = "InvalidKeyFormat";


        assertThatThrownBy(() -> cryptoService.encrypt(plainText, invalidKeyBase64))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Crypto error");


    }
}
