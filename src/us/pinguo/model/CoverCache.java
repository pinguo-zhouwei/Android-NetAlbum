package us.pinguo.model;

import android.content.Context;
import android.content.SharedPreferences;
import us.pinguo.album.MyAlbum;

/**
 * Created by Mr 周先森 on 2015/5/24.
 */
public class CoverCache {
    public static final String COVER_URL = "COVER_URL";
    public static final String COVER_BG_COLOR = "COVER_BG_COLOR";
    private Context mContext;

    public CoverCache(Context context) {
        mContext = context;
    }

    /**
     * 获取Cover
     *
     * @return
     */
    public Cover getCover() {
        SharedPreferences sharedPreferences = MyAlbum.getSharedPreferences();
        String url = sharedPreferences.getString(COVER_URL, "");
        int color = sharedPreferences.getInt(COVER_BG_COLOR, 0);
        Cover cover = new Cover();
        cover.color = color;
        cover.coverUrl = url;

        return cover;
    }

    /**
     * 保存Cover
     *
     * @param cover
     */
    public void saveCover(Cover cover) {
        SharedPreferences.Editor editor = MyAlbum.getSharedPreferences().edit();
        editor.putString(COVER_URL, cover.coverUrl);
        editor.putInt(COVER_BG_COLOR, cover.color);
        editor.commit();
    }
}
