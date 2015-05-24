package us.pinguo.api;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import us.pinguo.album.AlbumConstant;
import us.pinguo.async.ApiAsyncTaskBase;
import us.pinguo.async.HttpJsonRequest;
import us.pinguo.model.UserInfo;
import us.pinguo.network.AsyncResult;
import us.pinguo.network.BaseResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr 周先森 on 2015/4/23.
 */
public class ApiLogin extends ApiAsyncTaskBase<BaseResponse<UserInfo>> {
    public static final String url = AlbumConstant.HEADER + "YouthDrinking/servlet/LoginServlet";
    private String mUserName;
    private String mPassword;

    public ApiLogin(String userName, String password, Context context) {
        super(context);
        this.mUserName = userName;
        mPassword = password;
    }


    @Override
    public void get(final AsyncResult<BaseResponse<UserInfo>> result) {
        execute(new HttpJsonRequest<BaseResponse<UserInfo>>(Request.Method.POST, url, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userName", mUserName);
                params.put("password", mPassword);
                return params;
            }

            @Override
            protected void onResponse(BaseResponse<UserInfo> userBaseResponse) {
                if (userBaseResponse.status == 200) {
                    postResponse(result, userBaseResponse);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                postError(result, new Exception(error));
            }
        });
    }
}


