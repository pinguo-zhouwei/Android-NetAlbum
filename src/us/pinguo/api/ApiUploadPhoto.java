package us.pinguo.api;

import android.content.Context;
import com.android.volley.Request;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import us.pinguo.network.BaseResponse;
import us.pinguo.network.HttpMultipartRequest;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by yinyu on 2014/11/10.
 *
 * @SuppressWarnings("checkstyle:membername")
 */
public class ApiUploadPhoto extends HttpMultipartRequest<ApiUploadPhoto.Data> {
    private static final String URL = "http://upload.qiniu.com";
    private Context mContext;
    private String mToken;
    private String mPhotoUri;
    private String mMimeType;

    public ApiUploadPhoto(Context context, String token, String uri, String mimeType) {
        super(Request.Method.POST, URL);
        mContext = context;
        mToken = token;
        mPhotoUri = uri;
        mMimeType = mimeType;
    }

    @Override
    protected MultipartEntity getMultipartEntity() throws Exception {
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("token", StringBody.create(mToken, "text/plain", Charset.defaultCharset()));
        entity.addPart("file", new FileBody(new File(mPhotoUri), mMimeType));
        return entity;
    }

    @SuppressWarnings("checkstyle:membername")
    public static class Data {
        public String key;
        public String hash;
    }

    public static class Response extends BaseResponse<Data> {

    }

}
