package us.pinguo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mr 周先森 on 2015/4/13.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        crearePhotoTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      //数据库升级，在这里写升级操作
    }

    private void crearePhotoTable(SQLiteDatabase db){
        final String sql = "create table if not exists photo(" +
                "id integer primary key," +
                "photoId varchar(32),"+
                "time varchar(20),"+
                "headerId integer,"+
                "path varchar(100))"
                ;
        db.execSQL(sql);
    }
}
