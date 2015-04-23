package us.pinguo.network;

import com.android.volley.*;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;

/**
 * Created by yinyu on 2015/1/15.
 */
public abstract class HttpRequestBase<T> extends Request<T> {
    private Handler<T> mBackgroundAdapter;
    private int mStatusCode;

    public HttpRequestBase(String url) {
        super(0, url, null);
        this.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 1.0F));
    }

    public HttpRequestBase(int method, String url) {
        super(method, url, null);
        this.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 1.0F));
    }

    public void setHandlerInBackground(Handler<T> handler) {
        mBackgroundAdapter = handler;
    }

    // 提交结果前处理返回结果，该方法运行在子线程中
    protected T handleInBackground(T t) throws Exception {
        if (mBackgroundAdapter != null) {
            return mBackgroundAdapter.adapt(t);
        }

        return t;
    }

    protected abstract void onResponse(T var1);

    protected abstract void onErrorResponse(Exception var1);

    protected void deliverResponse(T t) {
        this.onResponse(t);
    }

    public void deliverError(VolleyError error) {
        this.onErrorResponse(error);
    }

    public void execute() {
        HttpRequestQueue.getInstance().add(this);
    }

    public Response<T> executeSync(BasicNetwork network) {
        try {
            NetworkResponse volleyError = network.performRequest(this);
            mStatusCode = volleyError.statusCode;
            return this.parseNetworkResponse(volleyError);
        } catch (VolleyError var3) {
            return Response.error(var3);
        }
    }

    public Response<T> executeSync() {
        // HttpStack httpStack = Debug.MAA ? new TrustAllCertsMaaHurlStack() : new TrustAllCertsHurlStack();
        HttpStack httpStack = new TrustAllCertsMaaHurlStack();
        BasicNetwork basicNetwork = new BasicNetwork(httpStack);
        return executeSync(basicNetwork);
    }

    public int getStatusCode() {
        return mStatusCode;
    }
}
