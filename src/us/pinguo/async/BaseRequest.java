package us.pinguo.async;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by Mr 周先森 on 2015/4/25.
 */
public abstract class BaseRequest<T> extends Request<T> {
    private Response.Listener<T> mListener;

    public BaseRequest(int method, String url, Response.Listener<T> listener) {
        super(method, url, null);
        mListener = listener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {

        return parseResponse(networkResponse);
    }

    @Override
    protected void deliverResponse(T t) {
        // mListener.onResponse(t);
        this.onResponse(t);
    }

    @Override
    public void deliverError(VolleyError error) {
        this.onErrorResponse(error);
    }

    protected abstract Response<T> parseResponse(NetworkResponse response);

    public void execute() {
        HttpRequestQueue.getInstance().add(this);
    }

    protected abstract void onResponse(T t);

    protected abstract void onErrorResponse(VolleyError error);
}
