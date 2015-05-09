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
    public static final String TABLE_PHOTO = "photo";

    public DBPhotoTable(SandBoxSql sandBoxSql) {
        this.mSqlOpenHelper = sandBoxSql;
    }

    /**
     * 判断本条记录在表中是否已经存在
     * @param item
     * @return true 存在，false 不存在
     */
    public boolean isInTable(PhotoItem item) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor = db.rawQuery("SELECT * FROM photo where photoId=?", new String[]{String.valueOf(item.photoId)});
            if (cursor.moveToFirst()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
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

    public long updateById(PhotoItem item) {
        SQLiteDatabase db = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }

            ContentValues values = getContentValues(item);
            return db.update(TABLE_PHOTO, values, "id = ?", new String[]{String.valueOf(item.id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据照片id 更新
     *
     * @param item
     */
    public long updateByPhotoId(PhotoItem item) {
        SQLiteDatabase db = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }

            ContentValues values = getContentValues(item);
            return db.update(TABLE_PHOTO, values, "photoId = ?", new String[]{String.valueOf(item.photoId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 更新一组照片
     *
     * @param photoItems
     */
    public void update(List<PhotoItem> photoItems) {
        for (int i = 0; i < photoItems.size(); i++) {
            PhotoItem item = photoItems.get(i);
            if (isInTable(item)) {//存在，更新
                updateByPhotoId(item);
            } else {//不存在，插入
                insert(item);
            }
        }
    }

    public void insert(List<PhotoItem> items) {
        for (PhotoItem item : items) {
            insert(item);
        }
    }

    /**
     * 获取照片的数量
     *
     * @return
     */
    public int getPhotoCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor = db.rawQuery("SELECT * FROM photo", null);
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public synchronized List<PhotoItem> queryPhoto() {
        int len = getPhotoCount();
        List<PhotoItem> items = new ArrayList<PhotoItem>(len);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor = db.rawQuery("SELECT * FROM photo", null);
            while (cursor.moveToNext()) {
                PhotoItem item = cursorToPhotoItem(cursor);
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;
    }

    /**
     * 查询没有上传的照片
     *
     * @return
     */
    public synchronized List<PhotoItem> queryNotUploadPhoto() {
        int len = getPhotoCount();
        List<PhotoItem> items = new ArrayList<PhotoItem>(len);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor = db.rawQuery("SELECT * FROM photo where isUpload=?", new String[]{"0"});
            while (cursor.moveToNext()) {
                PhotoItem item = cursorToPhotoItem(cursor);
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;
    }

    private ContentValues getContentValues(PhotoItem item) {
        ContentValues values = new ContentValues();
        values.put("path", item.url);
        values.put("time", item.time);
        values.put("headerId", item.headerId);
        values.put("isUpload", item.isUpload);
        values.put("photoId", item.photoId);
        return values;
    }

    private PhotoItem cursorToPhotoItem(Cursor cursor) {
        PhotoItem item = new PhotoItem();
        item.time = cursor.getString(cursor.getColumnIndex("time"));
        item.headerId = cursor.getInt(cursor.getColumnIndex("headerId"));
        item.url = cursor.getString(cursor.getColumnIndex("path"));
        item.id = cursor.getInt(cursor.getColumnIndex("id"));
        item.photoId = cursor.getString(cursor.getColumnIndex("photoId"));
        return item;
    }

}
