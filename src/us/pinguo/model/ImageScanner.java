package us.pinguo.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import us.pinguo.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/6.
 */
public class ImageScanner implements Runnable{
    private Context mContext;
    private ScanImageListener mScanImageListener;
    private List<PhotoItem> mPhotoItems;

    public ImageScanner(Context mContext) {
        this.mContext = mContext;
        mPhotoItems = new ArrayList<PhotoItem>();
    }

    private void scanImage(){
        //先发送广播扫描下整个sd卡
        mContext.sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();

        Cursor cursor = mContentResolver.query(mImageUri, null, null, null, MediaStore.Images.Media.DATE_ADDED);
        parsePhotoInfo(cursor);

        //回调
        if(mScanImageListener!=null){
            mScanImageListener.onScanImageComplete(mPhotoItems);
        }
    }

    @Override
    public void run() {
        scanImage();
    }


    private void parsePhotoInfo(Cursor cursor){
        while (cursor.moveToNext()) {
            // 获取图片的路径
            String path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片的添加到系统的毫秒数
            long times = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

            String photoTime = DateUtils.getFormatDate(times);
            PhotoItem item = new PhotoItem(path,photoTime);
            mPhotoItems.add(item);
        }
        cursor.close();
    }
    public void setmScanImageListener(ScanImageListener mScanImageListener) {
        this.mScanImageListener = mScanImageListener;
    }

    public interface ScanImageListener{
        /**
         * 扫描图片的回调，当图片扫描完成时，会返回一个
         * {@link us.pinguo.model.PhotoItem}的List
         * @param photoItemList
         */
        public void onScanImageComplete(List<PhotoItem> photoItemList);
    }
}
