package us.pinguo.model;

import android.content.Context;
import android.text.TextUtils;
import us.pinguo.api.ApiLogin;
import us.pinguo.api.ApiRegister;
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
        ApiLogin apiLogin = new ApiLogin(userName, pass, mContext);
        return new AsyncFutureAdapter<User, BaseResponse<User>>(apiLogin) {

            @Override
            public User adapt(BaseResponse<User> userBaseResponse) throws Exception {
                //数据库操作
                UserInfoCache userInfoCache = new UserInfoCache();
                userInfoCache.saveUser(userBaseResponse.data);

                return userBaseResponse.data;
            }
        };
    }

    //注册
    public AsyncFuture<String> userRegister(String userName, String password) {
        ApiRegister apiRegister = new ApiRegister(mContext, userName, password, String.valueOf(System.currentTimeMillis()));
        return new AsyncFutureAdapter<String, ApiRegister.Res>(apiRegister) {
            @Override
            public String adapt(ApiRegister.Res res) throws Exception {
                if (TextUtils.isEmpty(res.userName) || TextUtils.isEmpty(res.pass)) {
                    return "";
                }
                return res.userName;
            }
        };
    }
    //获取照片

    //上传照片
}
