package us.pinguo.album.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 【图片处理特效之 -- 底片效果】
 * Created by Mr 周先森 on 2015/4/19.
 */
public class NegativeImageEffect {
    /**
     * 算法原理：将当前像素点的RGB值分别与255之差后的值作为当前点的RGB值。
     * 例：
     * ABC
     * 求B点的底片效果：
     * B.r = 255 - B.r;
     * B.g = 255 - B.g;
     * B.b = 255 - B.b;
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getReliefBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] oldPixels = new int[width * height];
        int[] newPixels = new int[width * height];

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.getPixels(oldPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            //获取当前点的RGB
            int curColor = oldPixels[i];
            int curR = Color.red(curColor);
            int curG = Color.green(curColor);
            int curB = Color.blue(curColor);
            int curA = Color.alpha(curColor);
            //计算新的像素
            int newR = 255 - curR;
            int newG = 255 - curG;
            int newB = 255 - curB;
            newPixels[i] = Color.argb(curA, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
        }
        bmp.setPixels(newPixels, 0, width, 0, 0, width, height);

        return bmp;
    }
}
