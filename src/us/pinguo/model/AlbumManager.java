package us.pinguo.model;

import android.content.Context;
import android.text.TextUtils;
import us.pinguo.album.MyAlbum;
import us.pinguo.api.ApiAddPhoto;
import us.pinguo.api.ApiGetPhoto;
import us.pinguo.api.ApiLogin;
import us.pinguo.api.ApiRegister;
import us.pinguo.async.AsyncFutureAdapter;
import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;
import us.pinguo.network.AsyncFuture;
import us.pinguo.network.BaseResponse;

import java.util.ArrayList;
import java.util.List;

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
    public AsyncFuture<List<PhotoItem>> sncPhoto() {
        String userId = MyAlbum.getSharedPreferences().getString("userId", "");
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        ApiGetPhoto apiGetPhoto = new ApiGetPhoto(userId, mContext);
        return new AsyncFutureAdapter<List<PhotoItem>, BaseResponse<List<ApiGetPhoto.PhotoRes>>>(apiGetPhoto) {

            @Override
            public List<PhotoItem> adapt(BaseResponse<List<ApiGetPhoto.PhotoRes>> photosBaseResponse) throws Exception {
                //保存数据库
                List<ApiGetPhoto.PhotoRes> photosList = photosBaseResponse.data;
                List<PhotoItem> photoItems = new ArrayList<PhotoItem>();
                if (photosList != null && photosList.size() > 0) {
                    for (int i = 0; i < photosList.size(); i++) {
                        PhotoItem item = new PhotoItem();
                        ApiGetPhoto.PhotoRes photoRes = photosList.get(i);
                        item.photoId = photoRes.photoId;
                        item.isUpload = 1;
                        item.url = photoRes.url;
                        item.time = photoRes.time;
                        photoItems.add(item);
                    }
                }
                //更新
                DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
                dbPhotoTable.update(photoItems);

                return photoItems;
            }
        };
    }
    //上传照片
    public AsyncFuture<ApiAddPhoto.PhotoRes> uploadPhoto(final PhotoItem item) {
        String userId = MyAlbum.getSharedPreferences().getString("userId", "");
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        ApiAddPhoto apiAddPhoto = new ApiAddPhoto(item, userId, mContext);
        return new AsyncFutureAdapter<ApiAddPhoto.PhotoRes, BaseResponse<ApiAddPhoto.PhotoRes>>(apiAddPhoto) {

            @Override
            public ApiAddPhoto.PhotoRes adapt(BaseResponse<ApiAddPhoto.PhotoRes> photoRes) throws Exception {
                return photoRes.data;
            }
        };
    }
}
