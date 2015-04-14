package us.pinguo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import us.pinguo.album.AlbumConstant;
import us.pinguo.album.MyAlbum;

/**
 * Created by Mr 周先森 on 2015/4/13.
 */
public class SandBoxSql {
    private static SandBoxSql sandBoxSql;
    private SqliteHelper mSQLOpenHelper;
    private SQLiteDatabase mWriteSQLData;
    private SQLiteDatabase mReadSQLData;
    private String TAG = SandBoxSql.class.getSimpleName();
    private Context mContext;

    /**
     * 私有构造函数
     */
    private SandBoxSql() {
        // 创建数据库
        mSQLOpenHelper = new SqliteHelper(MyAlbum.getAppContext(), AlbumConstant.SAND_B0X_DB_PATH, null, SqliteHelper.VERSION);
        mWriteSQLData = mSQLOpenHelper.getWritableDatabase();// 写数据库
        mReadSQLData = mSQLOpenHelper.getReadableDatabase();// 读数据库

        // 允许单个数据库连接，并发读写
        mWriteSQLData.enableWriteAheadLogging();
    }

    public synchronized SQLiteDatabase getWriteSQLDB() {
        return mWriteSQLData;
    }

    public synchronized SQLiteDatabase getReadSQLDB() {
        return mReadSQLData;
    }

    public synchronized static SandBoxSql getInstance() throws Exception {
        if (sandBoxSql == null) {
            try {
                sandBoxSql = new SandBoxSql();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("无法打开数据库", e);
            }
        }
        return sandBoxSql;
    }
}
