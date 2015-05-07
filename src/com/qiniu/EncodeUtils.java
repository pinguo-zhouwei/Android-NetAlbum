/*
 * @creator Storm
 * @created_at Oct 16, 2013 3:20:23 PM
 * Copyright (C) 2013 BOOHEE. All rights reserved.
 */

package com.qiniu;

import org.apache.commons.codec.binary.Base64;

public class EncodeUtils {
    public static byte[] urlsafeEncodeBytes(byte[] src) {
        if (src.length % 3 == 0) {
            return encodeBase64Ex(src);
        }

        byte[] b = encodeBase64Ex(src);
        if (b.length % 4 == 0) {
            return b;
        }

        int pad = 4 - b.length % 4;
        byte[] b2 = new byte[b.length + pad];
        System.arraycopy(b, 0, b2, 0, b.length);
        b2[b.length] = '=';
        if (pad > 1) {
            b2[b.length + 1] = '=';
        }
        return b2;
    }

    // replace '/' with '_', '+" with '-'
    private static byte[] encodeBase64Ex(byte[] src) {
        // urlsafe version is not supported in version 1.4 or lower.
        byte[] b64 = Base64.encodeBase64(src);

        for (int i = 0; i < b64.length; i++) {
            if (b64[i] == '/') {
                b64[i] = '_';
            } else if (b64[i] == '+') {
                b64[i] = '-';
            }
        }
        return b64;
    }
}
