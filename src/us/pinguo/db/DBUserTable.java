package us.pinguo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import us.pinguo.album.AlbumConstant;
import us.pinguo.model.User;

/**
 * Created by Mr 周先森 on 2015/4/28.
 */
public class DBUserTable {
    private SandBoxSql mSqlOpenHelper;
    public static final String TABLE_USER = "user";

    public DBUserTable(SandBoxSql sandBoxSql) {
        this.mSqlOpenHelper = sandBoxSql;
    }

    public boolean userHasInTable(String userId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor = db.rawQuery("SELECT * FROM user WHERE userId = ?", new String[]{userId});
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
     * @param user
     * @return
     */
    public synchronized long insert(User user) {
        SQLiteDatabase db = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }

            ContentValues values = getContentValues(user);
            return db.insert(TABLE_USER, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public User findUserById(String userId) {
        SQLiteDatabase db = null;
        User user = null;
        Cursor cursor = null;
        try {
            db = mSqlOpenHelper.getWriteSQLDB();
            if (db == null) {
                throw new IllegalStateException("Couldn't open database of " + AlbumConstant.SAND_B0X_DB_PATH);
            }
            cursor = db.rawQuery("SELECT * FROM user", new String[]{userId});
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }

    public ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put("userId", user.userId);
        values.put("userName", user.userName);
        values.put("password", user.password);
        values.put("createTime", user.createTime);
        return values;
    }

    public User cursorToUser(Cursor cursor) {
        User user = new User();
        user.userId = cursor.getString(cursor.getColumnIndex("userId"));
        user.userName = cursor.getString(cursor.getColumnIndex("userName"));
        user.password = cursor.getString(cursor.getColumnIndex("password"));
        user.createTime = cursor.getString(cursor.getColumnIndex("createTime"));
        return user;
    }
}
