package us.pinguo.album;

import android.content.Context;

/**
 * Created by Mr 周先森 on 2015/4/13.
 */
public class MyAlbum {
    private  static Context sAppContext;

    public static void createInstance(Context context){
        sAppContext = context;
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
