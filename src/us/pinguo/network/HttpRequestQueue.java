package us.pinguo.network;

import com.android.volley.RequestQueue;

/**
 * Created by yinyu on 2015/2/9.
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
