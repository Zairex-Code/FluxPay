package com.fluxpay.infrastructure.security;

import org.apache.kafka.common.protocol.types.Field;

public final class MaskingUtils {
    private MaskingUtils(){}

    public static String maskAccount(String accountNumber){
        if (accountNumber == null || accountNumber.length() < 6){
            return "****";
        }
        int length = accountNumber.length();
        String start = accountNumber.substring(0,3);
        String end = accountNumber.substring(length - 3);
        return start + "-****-" + end;

    }


}
