package us.pinguo.network;

import android.text.TextUtils;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yinyu on 15-2-11
 */
public abstract class HttpJsonRequest<T> extends HttpFutureRequest<T> {
    private static final String TAG = HttpJsonRequest.class.getSimpleName();

    public HttpJsonRequest(String url) {
        super(url);
    }

    public HttpJsonRequest(int method, String url) {
        super(method, url);
    }

    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }

        Log.i(TAG, "json response:" + parsed);
        if (TextUtils.isEmpty(parsed)) {
            return Response.error(new VolleyError(new Exception("no-content")));
        }

        Type entityType = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];

        Gson gson = new Gson();
        try {
            T t = gson.fromJson(parsed, entityType);
            return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            return Response.error(new VolleyError(e));
        }
    }
}