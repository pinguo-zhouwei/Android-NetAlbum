package us.pinguo.album.utils;

import android.util.Log;
import us.pinguo.album.AlbumConstant;

import java.io.File;

/**
 * Created by Mr 周先森 on 2015/5/11.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    public static String generatePicPath() {
        File file = new File(AlbumConstant.EFFECT_PIC_PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.i(TAG, "create file failed");
                return null;
            }
        }
        return file.getAbsolutePath() + "/" + "effect_img_" + System.currentTimeMillis() + ".jpg";
    }
}
