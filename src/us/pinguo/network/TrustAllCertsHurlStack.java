package us.pinguo.network;

import com.android.volley.toolbox.HurlStack;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by yinyu on 2015/2/9.
 */
public class TrustAllCertsHurlStack extends HurlStack {
    public TrustAllCertsHurlStack() {
        super(null, getSocketFactory());
    }

    public static SSLSocketFactory getSocketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                return myTrustedAnchors;
            }
        }};

        try {
            SSLContext e = SSLContext.getInstance("TLS");
            e.init(null, trustAllCerts, new SecureRandom());
            return e.getSocketFactory();
        } catch (Exception var2) {
            return null;
        }
    }
}
