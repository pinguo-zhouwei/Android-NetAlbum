package us.pinguo.album.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 【图片处理特效之 -- 浮雕效果】
 * <p/>
 * Created by Mr 周先森 on 2015/4/19.
 */
public class ReliefImageEffect {
    /**
     * 算法原理：用前一个像素点的RGB值分别减去当前像素点的RGB值并加上127作为当前像素点的RGB值。
     * 如:
     * ABC
     * 求B点的浮雕效果如下
     * B.r = A.r-B.r+127
     * B.g = A.g-B.g+127
     * B.b = A.b-B.b+127
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getReliefBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] oldPixels = new int[width * height];
        int[] newPixels = new int[width * height];

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.getPixels(oldPixels, 0, width, 0, 0, width, height);
        for (int i = 1; i < width * height; i++) {
            //获取前一个点的像素值
            int pixColor = oldPixels[i - 1];
            int pixR = Color.red(pixColor);
            int pixG = Color.green(pixColor);
            int pixB = Color.blue(pixColor);
            //获取当前点的RGB
            int curColor = oldPixels[i];
            int curR = Color.red(curColor);
            int curG = Color.green(curColor);
            int curB = Color.blue(curColor);
            //计算新的像素
            int newR = pixR - curR + 127;
            int newG = pixG - curG + 127;
            int newB = pixB - curB + 127;
            newPixels[i] = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
        }
        bmp.setPixels(newPixels, 0, width, 0, 0, width, height);

        return bmp;
    }
}
