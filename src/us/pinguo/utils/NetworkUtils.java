package us.pinguo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Mr 周先森 on 2015/5/29.
 */
public class NetworkUtils {
    /**
     * 是否有网.
     *
     * @param context
     * @return
     */
    public static boolean hasNet(Context context) {

        boolean b = false;
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) { // 注意，这个判断一定要的哦，要不然会出错
            b = true;
        }
        return b;
    }
}
