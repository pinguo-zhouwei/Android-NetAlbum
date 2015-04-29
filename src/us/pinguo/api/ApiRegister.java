package us.pinguo.api;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import us.pinguo.album.AlbumConstant;
import us.pinguo.async.ApiAsyncTaskBase;
import us.pinguo.async.HttpJsonRequest;
import us.pinguo.network.AsyncResult;
import us.pinguo.network.BaseResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr 周先森 on 2015/4/28.
 */
public class ApiRegister extends ApiAsyncTaskBase<ApiRegister.Res> {
    public static final String url = AlbumConstant.HEADER + "YouthDrinking/servlet/RegisterServlet";
    private String mUserName;
    private String mPassword;
    private String mCreateTime;

    public ApiRegister(Context context, String userName, String pass, String createTime) {
        super(context);
        this.mUserName = userName;
        this.mPassword = pass;
        this.mCreateTime = createTime;
    }

    @Override
    public void get(final AsyncResult<Res> result) {
        execute(new HttpJsonRequest<Res>(Request.Method.POST, url, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", mUserName);
                params.put("password", mPassword);
                params.put("createTime", mCreateTime);
                return params;
            }

            @Override
            protected void onResponse(Res res) {
                if (res.status == 200) {
                    postResponse(result, res);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                postError(result, new Exception(error));
            }
        });
    }

    public static class Res extends BaseResponse {
        public String userName;
        public String pass;
    }
}
