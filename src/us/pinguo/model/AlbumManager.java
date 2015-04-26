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
    // 接口超时值与重试次数
    public static final int DEFAULT_TIMEOUT_MS = 5 * 60 * 1000;
    public static final int DEFAULT_MAX_RETRIES = 0;
    public static final int DEFAULT_BACKOFF_MULT = 0;
    private static final String TAG = AlbumManager.class.getSimpleName();
    private Context mContext;

    public AlbumManager(Context mContext) {
        this.mContext = mContext;
    }

    /* //登录

     public boolean login(String userName,String pass){
         try {
             User user = userLogin(userName,pass);
             if(user!=null){
                 Log.i(TAG,"login success !"+" userId："+user.userId);
                 return true;
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         return false;
     }*/
    public AsyncFuture<User> userLogin(String userName, String pass) {
       /* final User[] user = new User[1];
        Response.Listener<BaseResponse<User>> listener = new Response.Listener<BaseResponse<User>>() {

            @Override
            public void onResponse(BaseResponse<User> userBaseResponse) {
               user[0] = userBaseResponse.data;
            }
        };
        ApiLogin apiLogin = new ApiLogin(userName,pass,String.valueOf(System.currentTimeMillis()),listener);
        apiLogin.execute();
        return user[0];*/
        ApiLogin apiLogin = new ApiLogin(userName, pass, String.valueOf(System.currentTimeMillis()), mContext);
        return new AsyncFutureAdapter<User, BaseResponse<User>>(apiLogin) {

            @Override
            public User adapt(BaseResponse<User> userBaseResponse) throws Exception {
                return userBaseResponse.data;
            }
        };
    }

}
