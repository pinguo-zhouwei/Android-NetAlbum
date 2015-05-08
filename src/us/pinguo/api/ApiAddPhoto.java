package us.pinguo.api;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import us.pinguo.album.AlbumConstant;
import us.pinguo.async.ApiAsyncTaskBase;
import us.pinguo.async.HttpJsonRequest;
import us.pinguo.model.PhotoItem;
import us.pinguo.network.AsyncResult;
import us.pinguo.network.BaseResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr 周先森 on 2015/4/23.
 */
public class ApiAddPhoto extends ApiAsyncTaskBase<BaseResponse<ApiAddPhoto.PhotoRes>> {
    public static final String url = AlbumConstant.HEADER + "YouthDrinking/servlet/PhotoUploadServlet";
    private PhotoItem mPhotoItem;
    private String mUserId;

    public ApiAddPhoto(PhotoItem item, String userId, Context context) {
        super(context);
        mPhotoItem = item;
        mUserId = userId;
    }


    @Override
    public void get(final AsyncResult<BaseResponse<ApiAddPhoto.PhotoRes>> result) {
        execute(new HttpJsonRequest<BaseResponse<ApiAddPhoto.PhotoRes>>(Request.Method.POST, url, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("url", mPhotoItem.url);
                params.put("uploadTime", mPhotoItem.time);
                params.put("userId", mUserId);
                return params;
            }

            @Override
            protected void onResponse(BaseResponse<ApiAddPhoto.PhotoRes> response) {
                if (response.status == 200) {
                    postResponse(result, response);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                postError(result, new Exception(error));
            }
        });
    }

    public class PhotoRes {
        public String url;
        public String photoId;
        public String time;
        public String userId;
    }
}


