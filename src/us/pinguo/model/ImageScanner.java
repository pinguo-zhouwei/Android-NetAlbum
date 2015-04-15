package us.pinguo.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;
import us.pinguo.utils.DateUtils;
import us.pinguo.utils.PhotoCompator;

import java.util.*;

/**
 * 图片扫描器
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
        Log.i("zhouwei","cursor size:"+cursor.getCount());
        parsePhotoInfo(cursor);

        List<PhotoItem> photoItemList = addHeaderId(mPhotoItems);
        //排序
        Collections.sort(photoItemList,new PhotoCompator());
        try {
            DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
            dbPhotoTable.insert(photoItemList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //回调
        if(mScanImageListener!=null){
            mScanImageListener.onScanImageComplete(photoItemList);
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
            //为每一个Item 加上headerId


            mPhotoItems.add(item);
        }
        cursor.close();
    }

    /**
     * 为每个张照片生成一个headerId
     * @param items
     * @return
     */
    private List<PhotoItem> addHeaderId(List<PhotoItem> items){
        Map<String,Integer> headerIdMaps = new HashMap<String, Integer>();
        List<PhotoItem> photoItemList = new ArrayList<PhotoItem>();
        int headerId = 1;
        for(PhotoItem item:items){
            if(!headerIdMaps.containsKey(item.time)){
                headerIdMaps.put(item.time,headerId);
                item.headerId =  headerId;
                headerId++;
            }else{
                item.headerId = headerIdMaps.get(item.time);
            }
            photoItemList.add(item);
        }
     return photoItemList;
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
