package com.apploidxxx.crosssessioncookies.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author Arthur Kupriyanov
 */
public class TokenCodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generateToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(generateWord()).append("-");
        }

        String code = sb.toString();
        return code.substring(0, code.length() - 1);
    }

    private static String generateWord() {
        byte[] randomBytes = new byte[3];
        secureRandom.nextBytes(randomBytes);
        String word = base64Encoder.encodeToString(randomBytes);
        return word.replace('-', 'j').replace('_', 'q').replace('i', 't');
    }


}