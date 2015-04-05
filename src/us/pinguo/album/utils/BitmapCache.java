package us.pinguo.album.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 *
 * 以软引用的方式缓存图片，提高性能
 * Created by zhouwei on 14-10-11.
 */
public class BitmapCache {
    public final String TAG = "BitmapCache";
    static BitmapCache cache;
    /** 用于cache内容的存储**/
    Hashtable bitmapRefs;
    /** 保存SoftReference的垃圾队列，（当所引用的对象被回收时，则将该软引用存入该队列）*/
    ReferenceQueue q;

   class BitmapRef extends SoftReference{
       String _key="";
       public BitmapRef(Bitmap bm,ReferenceQueue q,String key){
           super(bm,q);
           _key = key;
       }
   }
  BitmapCache(){
      bitmapRefs = new Hashtable();
      q = new ReferenceQueue();
  }

    /**
     * 获取bitmapCache实例
     * @return
     */
 public static BitmapCache getCacheInstance(){
     if(cache==null){
         cache = new BitmapCache();
     }

     return cache;
 }

    /**
     * 以软引用的方式对一个bitmap对象的实例进行引用并保存到该引用中
     * @param bmp
     * @param key
     */
   private void addCacheBitmap(Bitmap bmp,String key){
       Log.i(TAG,"----> add cache");
       //清楚垃圾引用
       cleanCache();
       BitmapRef ref = new BitmapRef(bmp,q,key);
       bitmapRefs.put(key,ref);
   }

    public Bitmap getBitmap(String path){
        Bitmap bitmap = null;
        //先判断缓存中是否有该bitmap实例，如果有，直接从缓存中取出
        if(bitmapRefs.containsKey(path)){
            Log.i(TAG,"缓存有bitmap。。。。。");
            BitmapRef ref = (BitmapRef) bitmapRefs.get(path);
            bitmap = (Bitmap) ref.get();
        }
        //如果没有该bitmap软引用，或者从软引用中得到的是列是null，成哦给你新够在一个bitmap实例
        //并把这个新建的软引应是列保存到缓存
        if(bitmap==null){
            Log.i(TAG,"缓存中没有有bitmap。。。。。");
            bitmap = BitmapFactory.decodeFile(path);
            this.addCacheBitmap(bitmap,path);
        }

        return bitmap;
    }

    /**
     * 获取按比列缩放的图片
     * @param path
     * @param width
     * @param height
     * @return
     */
    public Bitmap getBitmap(String path,int width,int height){
        Bitmap bitmap = null;
        //先判断缓存中是否有该bitmap实例，如果有，直接从缓存中取出
        if(bitmapRefs.containsKey(path)){
            Log.i(TAG,"缓存有bitmap。。。。。");
            BitmapRef ref = (BitmapRef) bitmapRefs.get(path);
            bitmap = (Bitmap) ref.get();
        }
        //如果没有该bitmap软引用，或者从软引用中得到的是列是null，成哦给你新够在一个bitmap实例
        //并把这个新建的软引应是列保存到缓存
        if(bitmap==null){
            Log.i(TAG,"缓存中没有有bitmap。。。。。");
            bitmap = BitmapFactory.decodeFile(path);
            //做缩放处理
            bitmap = zoomImg(bitmap,width,height);
            this.addCacheBitmap(bitmap,path);
        }

        return bitmap;
    }

    /**
     * 清除引用队列中的引用
     */
    private void cleanCache(){
        BitmapRef ref = null;
        while((ref= (BitmapRef) q.poll())!=null){
          bitmapRefs.remove(ref._key);
        }
    }

    /**
     * 清除cache的全部内容
     */
   public void clearCache(){
       cleanCache();
       bitmapRefs.clear();
       System.gc();
       System.runFinalization();
   }

    /**
     *  缩放图片
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return 返回按比列缩放后的图片
     */
    private Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        System.out.println("newW:"+newWidth+"\n newH:"+newHeight+"\nW:"+width+"\nH:"+height);
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}



















