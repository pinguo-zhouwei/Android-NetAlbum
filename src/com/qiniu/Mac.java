/*
 * @creator Storm
 * @created_at Oct 16, 2013 3:13:01 PM
 * Copyright (C) 2013 BOOHEE. All rights reserved.
 */

package com.qiniu;

import javax.crypto.spec.SecretKeySpec;

public class Mac {
    public String accessKey;
    public String secretKey;

    public Mac(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * makes a upload token.
     *
     * @param data
     * @return
     * @throws AuthException
     */
    public String signWithData(byte[] data) throws AuthException {
        byte[] accessKey = this.accessKey.getBytes();
        byte[] secretKey = this.secretKey.getBytes();

        try {
            byte[] policyBase64 = EncodeUtils.urlsafeEncodeBytes(data);

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "HmacSHA1");
            mac.init(keySpec);

            byte[] digest = mac.doFinal(policyBase64);
            byte[] digestBase64 = EncodeUtils.urlsafeEncodeBytes(digest);
            byte[] token = new byte[accessKey.length + 30 + policyBase64.length];

            System.arraycopy(accessKey, 0, token, 0, accessKey.length);
            token[accessKey.length] = ':';
            System.arraycopy(digestBase64, 0, token, accessKey.length + 1, digestBase64.length);
            token[accessKey.length + 29] = ':';
            System.arraycopy(policyBase64, 0, token, accessKey.length + 30, policyBase64.length);

            return new String(token);
        } catch (Exception e) {
            throw new AuthException("Fail to sign with data!", e);
        }
    }
}
