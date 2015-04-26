package us.pinguo.async;

import com.android.volley.RequestQueue;

/**
 * Created by Mr 周先森 on 2015/4/25.
 */
public class HttpRequestQueue {
    private static RequestQueue mRequestQueue;

    public HttpRequestQueue() {
    }

    public static RequestQueue getInstance() {
        return mRequestQueue;
    }

    public static void setInstance(RequestQueue rq) {
        mRequestQueue = rq;
    }
}
