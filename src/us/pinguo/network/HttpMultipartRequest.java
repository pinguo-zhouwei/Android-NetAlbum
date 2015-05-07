package us.pinguo.network;

import com.android.volley.AuthFailureError;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinyu on 15-2-11
 */
public abstract class HttpMultipartRequest<T> extends HttpJsonRequest<T> {
    private MultipartEntity mMultipartEntity;

    public HttpMultipartRequest(String url) {
        super(url);
    }

    public HttpMultipartRequest(int method, String url) {
        super(method, url);
    }

    @Override
    public String getBodyContentType() {
        try {
            return getSafeMultipartEntity().getContentType().getValue();
        } catch (Exception e) {
            return super.getBodyContentType();
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return null;
    }

    @Override
    public void getBody(OutputStream out) throws AuthFailureError {
        try {
            getSafeMultipartEntity().writeTo(out);
        } catch (IOException e) {
            throw new AuthFailureError(e.getMessage());
        } catch (Exception e) {
            throw new AuthFailureError(e.getMessage());
        }
    }

    protected abstract MultipartEntity getMultipartEntity() throws Exception;

    // 避免多次调用getMultipartEntity，使得请求头与body内容不一致
    public MultipartEntity getSafeMultipartEntity() throws Exception {
        if (mMultipartEntity == null) {
            mMultipartEntity = getMultipartEntity();
        }
        return mMultipartEntity;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        // headers.put("Content-Type", "multipart/form-data; charset=utf-8");
        headers.put("User-agent", "My useragent");
        headers.put("WWW-Authenticate", "None");
        return headers;
    }
}
