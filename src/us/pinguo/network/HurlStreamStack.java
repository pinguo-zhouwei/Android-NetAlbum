package us.pinguo.network;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mr 周先森 on 2015/4/24.
 */
public class HurlStreamStack extends HurlStack {
    private int mContentLength = 0;

    public HurlStreamStack(int contentLength) {
        mContentLength = contentLength;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection httpURLConnection = super.createConnection(url);
        httpURLConnection.setFixedLengthStreamingMode(mContentLength);
        return httpURLConnection;
    }
}
