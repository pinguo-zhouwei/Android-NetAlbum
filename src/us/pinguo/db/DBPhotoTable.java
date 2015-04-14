package us.pinguo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import us.pinguo.album.AlbumConstant;
import us.pinguo.model.PhotoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/13.
 */
public class DBPhotoTable {
    private SandBoxSql mSqlOpenHelper;
    public  static final String TABLE_PHOTO = "photo";

    public DBPhotoTable(SandBoxSql sandBoxSql){
        this.mSqlOpenHelper = sandBoxSql;
    }
    /**
     * @param item
     * @return
     */
    public synchronized long insert(PhotoItem item) {
        SQLiteDatabase db = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }

            ContentValues values = getContentValues(item);
            return db.insert(TABLE_PHOTO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insert(List<PhotoItem> items){
        for(PhotoItem item:items){
            insert(item);
        }
    }

    /**
     * 获取照片的数量
     * @return
     */
    public int getPhotoCount(){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor =  db.rawQuery("SELECT * FROM photo", null);
            if(cursor!=null){
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return 0;
    }

    public synchronized List<PhotoItem> queryPhoto(){
        int len = getPhotoCount();
        List<PhotoItem> items = new ArrayList<PhotoItem>(len);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor =  db.rawQuery("SELECT * FROM photo", null);
            while (cursor.moveToNext()){
              PhotoItem item= cursorToPhotoItem(cursor);
              items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return items;
    }
    private ContentValues getContentValues(PhotoItem item){
       ContentValues values = new ContentValues();
       values.put("path",item.photoUri);
       values.put("time",item.time);
       values.put("headerId",item.headerId);
      return values;
    }

    private PhotoItem cursorToPhotoItem(Cursor cursor){
        PhotoItem item = new PhotoItem();
        item.time = cursor.getString(cursor.getColumnIndex("time"));
        item.headerId = cursor.getInt(cursor.getColumnIndex("headerId"));
        item.photoUri = cursor.getString(cursor.getColumnIndex("path"));
        return item;
    }

}
