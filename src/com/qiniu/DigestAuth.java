package com.qiniu;

public class DigestAuth {
    public static String signWithData(Mac mac, byte[] data) throws AuthException {
        if (mac == null) {
            mac = new Mac(Conf.ACCESS_KEY, Conf.SECRET_KEY);
        }
        return mac.signWithData(data);
    }
}
