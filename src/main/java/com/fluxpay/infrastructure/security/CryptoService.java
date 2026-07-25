package com.fluxpay.infrastructure.security;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public String encrypt(String plainText, String secretKeyBase64){
        try {
            // Step 1: Decode the Base64 key into raw bytes and build the AES SecretKey
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");

            // Step 2: Generate a cryptographically secure random Initialization Vector (IV)
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // Step 3: Initialize the Cipher engine in ENCRYPT_MODE
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            // Step 4: Execute encryption (Generates cipherText + GCM Authentication Tag)
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Step 5: Prepends the IV to the encrypted ciphertext [IV (12 bytes) + CipherText]
            byte[] encryptedPayload = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedPayload, 0, iv.length);
            System.arraycopy(cipherText, 0 , encryptedPayload, iv.length, cipherText.length);

            // Step 6: Encode the combined payload to Base64 for safe network transmission
            return Base64.getEncoder().encodeToString(encryptedPayload);

        } catch (Exception ex) {
            throw new IllegalStateException("Crypto error: Failed to encrypt payload", ex);
        }
    }

    public String decrypt(String encryptedTextBase64, String secretKeyBase64){
        try {

            // Step 1: Decode the combined payload and secret key from Base64
            byte[] decodePayload = Base64.getDecoder().decode(encryptedTextBase64);
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");

            // Step 2: Extract the original 12-byte IV from the beginning of the payload
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decodePayload, 0 , iv,0, iv.length);

            // Step 3: Extract the remaining ciphertext + tag bytes
            int cipherTextLength = decodePayload.length - GCM_IV_LENGTH;
            byte[] cipherText = new byte[cipherTextLength];
            System.arraycopy(decodePayload, GCM_IV_LENGTH, cipherText,0,cipherTextLength);

            // Step 4: Initialize Cipher in DECRYPT_MODE with extracted IV
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH,iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            // Step 5: Decrypt and verify tag integrity
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText,StandardCharsets.UTF_8);

        }catch (Exception ex){
            throw new IllegalStateException("Crypto error: Failed to Decrypt or payload tampered", ex);
        }
    }

}
