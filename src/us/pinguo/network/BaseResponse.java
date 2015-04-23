package us.pinguo.network;

/**
 * Created by MR on 2014/4/22.
 */
@SuppressWarnings("checkstyle:membername")
public class BaseResponse<T> {
    public static final int STATUS_OK = 200;

    public int status;
    public String message;
    public T data;

    public boolean isSuccess() {
        return status == STATUS_OK;
    }
}
