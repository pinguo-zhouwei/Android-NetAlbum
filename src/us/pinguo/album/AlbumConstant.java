package us.pinguo.album;

import android.os.Environment;

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
}
