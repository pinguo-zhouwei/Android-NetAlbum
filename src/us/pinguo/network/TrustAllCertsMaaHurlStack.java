package us.pinguo.network;

//import com.mato.sdk.proxy.Address;
//import com.mato.sdk.proxy.Proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//import java.net.InetSocketAddress;

/**
 * Created by yinyu on 2014/6/20.
 */
public class TrustAllCertsMaaHurlStack extends TrustAllCertsHurlStack {
    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return createMaaConnection(url);
    }

    public HttpURLConnection createMaaConnection(URL url) throws IOException {
//        Address address = Proxy.getAddress();
//        if(address != null) {
//            String host = address.getHost();
//            int port = address.getPort();
//            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
//                    new InetSocketAddress(host,port));
//            return (HttpURLConnection)url.openConnection(proxy);
//        }

        return (HttpURLConnection) url.openConnection();
    }
}
