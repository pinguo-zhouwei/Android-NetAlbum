package us.pinguo.album.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Mr 周先森 on 2015/4/17.
 */
public class PictureSpecialEffects {
    /**
     * 怀旧特效
     * 原理如下 ---> 算法公式:
     * R = 0.393r + 0.769g + 0.189b
     * G = 0.349r + 0.686g + 0.168b
     * B = 0.272r + 0.534g + 0.131b
     * 将每一个像素点的RGB 拆分出来，然后根据上面的公式算出新的RGB 值，作为当前像素点
     * 的RGB 值
     *
     * @param bitmap 待处理的bitmap
     * @return 处理后的bitmap
     */
    public static Bitmap getRememberEffect(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int pixColor = 0;
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int pixels[] = new int[width * height];
        //获取原Bitmap 的所有像素点
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        //根据公式得到新的 RGB值
        for (int i = 0; i < height; i++) {
            for (int k = 0; k < width; k++) {
                pixColor = pixels[width * i + k];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                //计算新的香色值
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
                pixels[width * i + k] = newColor;
            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp;
    }
}
