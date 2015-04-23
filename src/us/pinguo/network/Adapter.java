package us.pinguo.network;

/**
 * Created by yinyu on 2014/6/26.
 * 任务结果适配器，用于对异步任务进行中间处理后再返回给上层调用者
 */
public interface Adapter<DST, SRC> {
    /**
     * 适配异步结果
     *
     * @param src 异步任务源结果，等待中间处理
     * @return 处理后的适配结果
     */
    public abstract SRC adapt(SRC src) throws Exception;

    /**
     * 适配异步结果中发生的错误，也类可以进行错误处理，返回可用的结果或者重新生成错误类
     *
     * @param e 错误，等待错误处理
     * @return 处理后的适配结果
     */
    //public Result<DST> adaptError(Exception e);
}

