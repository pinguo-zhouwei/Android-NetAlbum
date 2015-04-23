package us.pinguo.network;


import com.android.volley.Response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by yinyu on 15-2-11
 */
public abstract class HttpFutureRequest<T> extends HttpRequestBase<T> implements AsyncFuture<T> {
    private boolean mResultReceived = false;
    private T mResult;
    private Exception mException;
    private AsyncResult<T> mAsyncResultListener;

    public HttpFutureRequest(String url) {
        super(url);
    }

    public HttpFutureRequest(int method, String url) {
        super(method, url);
    }


    @Override
    public boolean cancel(boolean b) {
        super.cancel();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return super.isCanceled();
    }

    @Override
    public synchronized boolean isDone() {
        return this.mResultReceived || this.mException != null || this.isCancelled();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        Response<T> response = executeSync();
        if (!response.isSuccess()) {
            throw new ExecutionException(response.error);
        }
        return response.result;
    }

    @Override
    public T get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.doGet(Long.valueOf(TimeUnit.MILLISECONDS.convert(timeout, timeUnit)));
    }

    private synchronized T doGet(Long timeoutMs) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.mException != null) {
            throw new ExecutionException(this.mException);
        } else if (this.mResultReceived) {
            return this.mResult;
        } else {
            execute();

            if (timeoutMs == null) {
                this.wait(0L);
            } else if (timeoutMs.longValue() > 0L) {
                this.wait(timeoutMs.longValue());
            }

            if (this.mException != null) {
                throw new ExecutionException(this.mException);
            } else if (!this.mResultReceived) {
                throw new TimeoutException();
            } else {
                return this.mResult;
            }
        }
    }

    @Override
    public void get(AsyncResult<T> result) {
        mAsyncResultListener = result;
        execute();
    }

    @Override
    protected synchronized void onResponse(T response) {
        this.mResultReceived = true;
        this.mResult = response;

        if (mAsyncResultListener != null) {
            mAsyncResultListener.onSuccess(response);
        } else {
            this.notifyAll();
        }
    }

    @Override
    protected synchronized void onErrorResponse(Exception error) {
        this.mException = error;

        if (mAsyncResultListener != null) {
            mAsyncResultListener.onError(error);
        } else {
            this.notifyAll();
        }
    }
}
