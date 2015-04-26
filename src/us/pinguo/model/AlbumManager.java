package us.pinguo.model;

import android.content.Context;
import us.pinguo.api.ApiLogin;
import us.pinguo.async.AsyncFutureAdapter;
import us.pinguo.network.AsyncFuture;
import us.pinguo.network.BaseResponse;

/**
 * Created by Mr 周先森 on 2015/4/24.
 */
public class AlbumManager {

    private static final String TAG = AlbumManager.class.getSimpleName();
    private Context mContext;

    public AlbumManager(Context mContext) {
        this.mContext = mContext;
    }

    //登录
    public AsyncFuture<User> userLogin(String userName, String pass) {
        ApiLogin apiLogin = new ApiLogin(userName, pass, String.valueOf(System.currentTimeMillis()), mContext);
        return new AsyncFutureAdapter<User, BaseResponse<User>>(apiLogin) {

            @Override
            public User adapt(BaseResponse<User> userBaseResponse) throws Exception {
                //数据库操作


                return userBaseResponse.data;
            }
        };
    }


    //获取照片

    //上传照片
}
