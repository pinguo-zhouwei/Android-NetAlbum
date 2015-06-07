package us.pinguo.model;

import android.content.Context;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.qiniu.Conf;
import us.pinguo.album.AlbumConstant;
import us.pinguo.api.ApiAddPhoto;
import us.pinguo.api.ApiUploadPhoto;
import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;
import us.pinguo.network.AsyncResult;
import us.pinguo.network.HurlStreamStack;

import java.util.List;

/**
 * Created by Mr 周先森 on 2015/5/6.
 */
public class PhotoUploadTask implements Runnable {
    private static final String TAG = "PhotoUploadTask";
    // 接口超时值与重试次数
    public static final int DEFAULT_TIMEOUT_MS = 5 * 60 * 1000;
    public static final int DEFAULT_MAX_RETRIES = 0;
    public static final int DEFAULT_BACKOFF_MULT = 0;
    private Context mContext;

    private boolean isStart = false;

    public PhotoUploadTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void run() {
        upload();
    }

    public void upload() {
        try {
            DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
            List<PhotoItem> photoItemList = dbPhotoTable.queryNotUploadPhoto();
            if (photoItemList == null || photoItemList.size() == 0) {
                return;
            }
            setStart(true);
            for (int i = 0; i < photoItemList.size(); i++) {
                final PhotoItem item = photoItemList.get(i);
                String url = item.url;
                String upToken = Conf.getToken();
                ApiUploadPhoto.Data data = upload(url, upToken);
                if (data != null) {
                    Log.i(TAG, "eTag：" + data.key);
                    String photoUrl = AlbumConstant.PHOTO_HEADER + data.key;
                    item.url = photoUrl;
                    //上传照片
                    AlbumManager manager = new AlbumManager(mContext);
                    manager.uploadPhoto(item).get(new AsyncResult<ApiAddPhoto.PhotoRes>() {
                        @Override
                        public void onSuccess(ApiAddPhoto.PhotoRes res) {
                            Log.i(TAG, "上传成功。");
                            item.time = res.time;
                            item.url = res.url;
                            item.isUpload = 1;
                            item.photoId = res.photoId;
                            try {
                                DBPhotoTable table = new DBPhotoTable(SandBoxSql.getInstance());
                                table.updateById(item);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "上传失败。");
                        }
                    });
                }
            }
            setStart(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApiUploadPhoto.Data upload(String uri, String token) throws Exception {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
        ApiUploadPhoto apiUploadFile = new ApiUploadPhoto(mContext, token, uri, "image/*");
        apiUploadFile.setRetryPolicy(retryPolicy);

        // 流式上传，避免将图片数据缓存到内存中再发送
        HttpStack streamStack = new HurlStreamStack((int) apiUploadFile.getSafeMultipartEntity().getContentLength());
        BasicNetwork uploadNetwork = new BasicNetwork(streamStack);

        Response<ApiUploadPhoto.Data> uploadResponse = apiUploadFile.executeSync(uploadNetwork);

        // 上传成功返回picId
        if (uploadResponse.isSuccess()) {
            return uploadResponse.result;
        } else {
            Log.i(TAG, uri + " failed to upload.\n" + uploadResponse.error);
        }

        // 上传失败返回空字符串
        return null;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public boolean isStart() {
        return isStart;
    }
}
