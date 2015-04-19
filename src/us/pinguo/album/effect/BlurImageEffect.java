package us.pinguo.album.effect;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * 图片处理----模糊特效
 * Created by Mr 周先森 on 2015/4/18.
 */
public class BlurImageEffect {
    /**
     * 算法:--将像素点周围八个点包括自身一共九个点的RGB值分别相加后平均，作为当前像素点的RGB值，即可实现效果。
     * 如:
     * A B C
     * D E F
     * G H I
     * 如果以E 作为当前点的话，那么E的R值为:
     * E.r = （A.r+B.r+C.r+D.r+E.r+F.r+G.r+H.r+I.r）/9,E点的BG值算法类似
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getBlurImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixelColor = 0;
        int newColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;

        int[][] pixels = new int[9][3];//数组用来记录九个点的R,G,B 值
        //bitmap.getPixel(0,0);
        for (int i = 1, length = width - 1; i < length; i++) {
            for (int k = 1, len = height - 1; k < len; k++) {
                for (int m = 0; m < 9; m++) {
                    int x = 0;
                    int y = 0;
                    switch (m) {//获取当前点周围九个点（包括自己）的坐标位置
                        case 0:
                            x = i - 1;
                            y = k - 1;
                            break;
                        case 1:
                            x = i;
                            y = k - 1;
                            break;
                        case 2:
                            x = i + 1;
                            y = k - 1;
                            break;
                        case 3:
                            x = i - 1;
                            y = k;
                            break;
                        case 4:
                            x = i;
                            y = k;
                            break;
                        case 5:
                            x = i + 1;
                            y = k;
                            break;
                        case 6:
                            x = i - 1;
                            y = k + 1;
                            break;
                        case 7:
                            x = i;
                            y = k + 1;
                            break;
                        case 8:
                            x = i + 1;
                            y = k + 1;
                            break;
                    }
                    pixelColor = bitmap.getPixel(x, y);
                    pixels[m][0] = Color.red(pixelColor);
                    pixels[m][1] = Color.green(pixelColor);
                    pixels[m][2] = Color.blue(pixelColor);
                }
                //将九个点的像素值相加求和
                for (int m = 0; m < 9; m++) {
                    newR += pixels[m][0];
                    newG += pixels[m][1];
                    newB += pixels[m][2];
                }
                //在求平均值
                newR = (int) (newR / 9f);
                newG = (int) (newG / 9f);
                newB = (int) (newB / 9f);

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                newColor = Color.argb(255, newR, newG, newB);
                bmp.setPixel(i, k, newColor);

                //清零
                newR = 0;
                newG = 0;
                newB = 0;
            }

        }
        return bmp;
    }

    /**
     * 柔化效果(高斯模糊)(优化后比上面快三倍)
     *
     * @param bmp
     * @return
     */
    public static Bitmap blurImageAmeliorate(Bitmap bmp) {
        long start = System.currentTimeMillis();
        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int delta = 16; // 值越小图片会越亮，越大则越暗

        int idx = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + m) * width + k + n];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * gauss[idx]);
                        newG = newG + (int) (pixG * gauss[idx]);
                        newB = newB + (int) (pixB * gauss[idx]);
                        idx++;
                    }
                }

                newR /= delta;
                newG /= delta;
                newB /= delta;

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);

                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.d("FFF", "used time=" + (end - start));
        return bitmap;
    }
}
