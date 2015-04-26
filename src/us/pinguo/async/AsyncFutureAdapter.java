package us.pinguo.async;

import us.pinguo.network.AsyncFuture;
import us.pinguo.network.AsyncResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by yinyu on 2014/6/26.
 * 任务结果适配器，用于对异步任务进行中间处理后再返回给上层调用者
 */
public abstract class AsyncFutureAdapter<DST, SRC> implements AsyncFuture<DST> {
    private AsyncFuture<SRC> mTaskFuture;

    public AsyncFutureAdapter(AsyncFuture<SRC> taskFuture) {
        mTaskFuture = taskFuture;
    }

    public AsyncFuture<SRC> getSource() {
        return mTaskFuture;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return mTaskFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return mTaskFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return mTaskFuture.isDone();
    }

    @Override
    public DST get() throws InterruptedException, ExecutionException {
        try {
            return adapt(mTaskFuture.get());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public DST get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return adapt(mTaskFuture.get(l, timeUnit));
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public void get(final AsyncResult<DST> taskResult) {
        mTaskFuture.get(new AsyncResult<SRC>() {
            @Override
            public void onSuccess(SRC v) {
                try {
                    DST dst = adapt(v);
                    postResponse(taskResult, dst);
                } catch (Exception e) {
                    onError(e);
                }
            }

            @Override
            public void onError(Exception e) {
                Result<DST> result = adaptError(e);
                if (result.isSuccess()) {
                    postResponse(taskResult, result.result);
                } else {
                    postError(taskResult, result.error);
                }
            }

            @Override
            public void onProgress(int rate) {
                if (taskResult != null) {
                    taskResult.onProgress(rate);
                }
                super.onProgress(rate);
            }
        });
    }

    /**
     * 适配异步结果
     *
     * @param src 异步任务源结果，等待中间处理
     * @return 处理后的适配结果
     */
    public abstract DST adapt(SRC src) throws Exception;

    /**
     * 适配异步结果中发生的错误，也类可以进行错误处理，返回可用的结果或者重新生成错误类
     *
     * @param e 错误，等待错误处理
     * @return 处理后的适配结果
     */
    public Result<DST> adaptError(Exception e) {
        return Result.error(e);
    }

    protected void postResponse(AsyncResult<DST> result, DST v) {
        if (result != null) {
            result.onSuccess(v);
        }
    }

    protected void postError(AsyncResult<DST> result, Exception e) {
        if (result != null) {
            result.onError(e);
        }
    }
}
