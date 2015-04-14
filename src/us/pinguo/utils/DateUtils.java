package us.pinguo.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mr 周先森 on 2015/4/6.
 */
public class DateUtils {
    public static String getFormatDate(long timestamp) {
        Log.i("DateUtils", "timestamp：" + timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        String date = simpleDateFormat.format(new Date(timestamp*1000L));
        return date;
    }
}
