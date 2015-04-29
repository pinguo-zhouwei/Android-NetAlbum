package us.pinguo.api;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import us.pinguo.album.AlbumConstant;
import us.pinguo.async.ApiAsyncTaskBase;
import us.pinguo.async.HttpJsonRequest;
import us.pinguo.model.User;
import us.pinguo.network.AsyncResult;
import us.pinguo.network.BaseResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr 周先森 on 2015/4/23.
 */
public class ApiLogin extends ApiAsyncTaskBase<BaseResponse<User>> {
    public static final String url = AlbumConstant.HEADER + "YouthDrinking/servlet/LoginServlet";
    private String mUserName;
    private String mPassword;

    public ApiLogin(String userName, String password, Context context) {
        super(context);
        this.mUserName = userName;
        mPassword = password;
    }


    @Override
    public void get(final AsyncResult<BaseResponse<User>> result) {
        execute(new HttpJsonRequest<BaseResponse<User>>(Request.Method.POST, url, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userName", mUserName);
                params.put("password", mPassword);
                return params;
            }

            @Override
            protected void onResponse(BaseResponse<User> userBaseResponse) {
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
}/* @Override
    protected MultipartEntity getMultipartEntity() throws Exception {
        MultipartEntity entity = new MultipartEntity();
        Map<String,String> params = new HashMap<String, String>();

        params.put("userName",mUserName);
        params.put("password",mPassword);
        params.put("createTime",mCreateTime);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            entity.addPart(entry.getKey(), StringBody.create(entry.getValue(), "text/plain", Charset.defaultCharset()));
        }

        return entity;
    }*/


