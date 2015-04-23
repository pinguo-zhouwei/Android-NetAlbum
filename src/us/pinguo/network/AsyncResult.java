package us.pinguo.network;

/**
 * Created by yinyu on 2014/6/26.
 */
public abstract class AsyncResult<T> {
    public abstract void onSuccess(T t);

    public abstract void onError(Exception e);

    public void onProgress(int rate) {
    }
}
