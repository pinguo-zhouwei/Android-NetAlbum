package us.pinguo.album;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mr 周先森 on 2015/4/13.
 */
public class MyAlbum {
    private static Context sAppContext;
    private static SharedPreferences mSharedPreferences;

    public static void createInstance(Context context) {
        sAppContext = context;
        // mSharedPreferences = sAppContext.getSharedPreferences("album", Context.MODE_PRIVATE);
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static SharedPreferences getSharedPreferences() {
        return sAppContext.getSharedPreferences("album", Context.MODE_PRIVATE);
    }
}
