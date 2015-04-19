package us.pinguo.album.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 【图片处理之 黑白效果】
 * Created by Mr 周先森 on 2015/4/19.
 */
public class BlackImageEffect {
    /**
     * 原理: 将当前像素值的RGB之和 除以3求均值后作为当前的RGB
     * 如:
     * B
     * B.r = B.g = B.b = （B.r+B.g+B.b）/3
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getBlackBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[width * height];
        int[] newPixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            int pixColor = pixels[i];
            int curR = Color.red(pixColor);
            int curG = Color.green(pixColor);
            int curB = Color.blue(pixColor);
            int avgRGB = (int) ((curR + curG + curB) / 3f);

            newPixels[i] = Color.argb(255, avgRGB, avgRGB, avgRGB);
        }
        bmp.setPixels(newPixels, 0, width, 0, 0, width, height);

        return bmp;
    }
}
