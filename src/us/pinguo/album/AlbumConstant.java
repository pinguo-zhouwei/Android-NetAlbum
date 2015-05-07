package us.pinguo.album;

import android.os.Environment;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

/**
 * Created by Mr 周先森 on 2015/4/13.
 */
public class AlbumConstant {
    // SD卡根路径
    public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String NETALBUM_ROOT = SDCARD_ROOT + "/NetAlbum";

    public static final String SAND_BOX = NETALBUM_ROOT + "/TempData/.sandbox";

    public static final String SAND_B0X_ROOT = SAND_BOX + "/";

    /**
     * 数据库文件路径
     */
    public static final String SAND_B0X_DB_PATH = SAND_B0X_ROOT + "sandbox.db";
    /**
     * 特效 照片保存路径
     */
    public static final String EFFECT_PIC_PATH = NETALBUM_ROOT + "/TempData/effect_pic";

    public static final String HEADER = "http://192.168.191.1:8080/";

    public static String PHOTO_HEADER = "http://7xiyob.com1.z0.glb.clouddn.com/";

    // 接口超时值与重试次数
    public static final int DEFAULT_TIMEOUT_MS = 15000;
    public static final int DEFAULT_MAX_RETRIES = 0;
    public static final int DEFAULT_BACKOFF_MULT = 0;

    // 超时重试策略
    public static RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
                DEFAULT_MAX_RETRIES,
                DEFAULT_BACKOFF_MULT);
    }
}
