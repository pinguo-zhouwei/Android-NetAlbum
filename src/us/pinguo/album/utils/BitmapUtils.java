package us.pinguo.album.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Mr 周先森 on 2015/4/20.
 */
public class BitmapUtils {
    public static boolean saveBitmap(String path, Bitmap bitmap, int quality) throws IOException, IllegalArgumentException {
        if (TextUtils.isEmpty(path) || bitmap == null) {
            return false;
        }

        boolean flag = false;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();

                flag = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            close(out);
        }

        return flag;
    }

    public static void close(OutputStream out) throws IOException {
        if (out != null) {
            out.close();
            out = null;
        }
    }
}
