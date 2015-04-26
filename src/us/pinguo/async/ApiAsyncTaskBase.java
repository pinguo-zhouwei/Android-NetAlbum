package us.pinguo.async;

import android.content.Context;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import us.pinguo.album.AlbumConstant;
import us.pinguo.network.AsyncFuture;
import us.pinguo.network.AsyncResult;

/**
 * Created by yinyu on 2014/6/26.
 */
public abstract class ApiAsyncTaskBase<V> implements AsyncFuture<V> {
    protected Context mContext;
    protected BaseRequest<?> mHttpRequest;
    protected RequestFuture<V> mHttpRequestFuture = RequestFuture.newFuture();

    protected ApiAsyncTaskBase(Context context) {
        mContext = context;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return mHttpRequestFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return mHttpRequestFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return mHttpRequestFuture.isDone();
    }

    /**
     * 阻塞式得获取异步任务结果，不能在主线程中调用
     *
     * @return 异步任务结果
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    @Override
    public V get() throws InterruptedException, java.util.concurrent.ExecutionException {
        get(new AsyncResult<V>() {
            @Override
            public void onSuccess(V v) {
                mHttpRequestFuture.onResponse(v);
            }

            @Override
            public void onError(Exception e) {
                mHttpRequestFuture.onErrorResponse(new VolleyError(e));
            }
        });
        return mHttpRequestFuture.get();
    }

    /**
     * 阻塞式得获取异步任务结果，不能在主线程中调用
     *
     * @param l        阻塞超时时间
     * @param timeUnit 时间单位
     * @return
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     */
    @Override
    public V get(long l, java.util.concurrent.TimeUnit timeUnit) throws InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        get(new AsyncResult<V>() {
            @Override
            public void onSuccess(V v) {
                mHttpRequestFuture.onResponse(v);
            }

            @Override
            public void onError(Exception e) {
                mHttpRequestFuture.onErrorResponse(new VolleyError(e));
            }
        });
        return mHttpRequestFuture.get(l, timeUnit);
    }

    public abstract void get(AsyncResult<V> result);

    public Context getContext() {
        return mContext;
    }

    protected void execute(BaseRequest<?> httpRequest) {
        if (mHttpRequest != null && mHttpRequest != httpRequest) {
            mHttpRequest.cancel(); // 取消上一次的异步调用
        }
        mHttpRequest = httpRequest;
        mHttpRequestFuture.setRequest(mHttpRequest);

        //设置超时策略
        mHttpRequest.setRetryPolicy(AlbumConstant.getRetryPolicy());
        mHttpRequest.execute();
    }

    protected void postResponse(AsyncResult<V> result, V v) {
        if (result != null) {
            result.onSuccess(v);
        }
    }

    protected void postError(AsyncResult<V> result, Exception e) {
        if (result != null) {
            result.onError(e);
        }
    }
}
