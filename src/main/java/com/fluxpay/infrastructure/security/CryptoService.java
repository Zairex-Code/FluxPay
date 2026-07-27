package com.fluxpay.infrastructure.security;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service providing AES-GCM (Authenticated Encryption with Associated Data) symmetric encryption
 * and decryption capabilities for sensitive financial data.
 */
@Service
public class CryptoService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * Thread-safe, cryptographically secure random number generator instance.
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Encrypts a plain text string using AES-256 in GCM mode.
     * <p>
     * Generates a 12-byte random Initialization Vector (IV) and prepends it to the cipher output
     * before encoding to Base64.
     *
     * @param plainText         The raw unencrypted string.
     * @param secretKeyBase64 The Base64 encoded AES key.
     * @return Base64 encoded string containing [IV (12 bytes) + CipherText + GCM Tag].
     * @throws IllegalStateException if encryption fails.
     */
    public String encrypt(String plainText, String secretKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedPayload = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedPayload, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedPayload, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedPayload);

        } catch (Exception ex) {
            throw new IllegalStateException("Crypto error: Failed to encrypt payload", ex);
        }
    }

    /**
     * Decrypts an AES-GCM encrypted Base64 payload and verifies authentication tag integrity.
     *
     * @param encryptedTextBase64 Base64 string containing [IV + CipherText + GCM Tag].
     * @param secretKeyBase64     The Base64 encoded AES key.
     * @return Decrypted UTF-8 plain text string.
     * @throws IllegalStateException if decryption fails or data payload was tampered.
     */
    public String decrypt(String encryptedTextBase64, String secretKeyBase64) {
        try {
            byte[] decodePayload = Base64.getDecoder().decode(encryptedTextBase64);
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decodePayload, 0, iv, 0, iv.length);

            int cipherTextLength = decodePayload.length - GCM_IV_LENGTH;
            byte[] cipherText = new byte[cipherTextLength];
            System.arraycopy(decodePayload, GCM_IV_LENGTH, cipherText, 0, cipherTextLength);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            throw new IllegalStateException("Crypto error: Failed to Decrypt or payload tampered", ex);
        }
    }
}