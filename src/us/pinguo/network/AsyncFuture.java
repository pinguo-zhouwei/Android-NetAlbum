package us.pinguo.network;

import java.util.concurrent.Future;

/**
 * Created by yinyu on 2014/6/26.
 */
public interface AsyncFuture<V> extends Future<V> {
    void get(AsyncResult<V> result);
}
