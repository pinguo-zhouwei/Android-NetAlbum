package us.pinguo.async;

/**
 * Created by yinyu on 2014/11/25.
 *
 * @SuppressWarnings("checkstyle:membername")
 */
public class Result<T> {
    public final T result;
    public final Exception error;

    public static <T> Result<T> success(T result) {
        return new Result<T>(result);
    }

    public static <T> Result<T> error(Exception error) {
        return new Result<T>(error);
    }

    public boolean isSuccess() {
        return result != null;
    }

    private Result(T result) {
        this.result = result;
        this.error = null;
    }

    private Result(Exception error) {
        this.result = null;
        this.error = error;
    }
}
