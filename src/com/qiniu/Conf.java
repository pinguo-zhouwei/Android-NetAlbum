package com.qiniu;

import android.text.format.DateFormat;
import android.util.Log;
import org.json.JSONException;

import java.util.Date;

/**
 * 生成七牛上传Token
 */
public class Conf {
    public static String ACCESS_KEY = "lS5-gzH7KjyRGbRmFDuTjSoKOdIi9Z4W_icJ3JgM";
    public static String SECRET_KEY = "s6VbxlwR9vDtv9c6gKQ-97EWpovahDCMRDnD-Am0";
    // 指定上传所用的七牛空间名称
    public static String BUCKET_NAME = "qiuniu-zhouwei";

    public static final String USER_AGENT = "qiniu android-sdk v6.0.0";
    public static final String UP_HOST = "http://up.qiniu.com";

    // get qiniu upload token
    public static String getToken() {
        String uptoken;
        Mac mac = new Mac(Conf.ACCESS_KEY, Conf.SECRET_KEY);
        PutPolicy putPolicy = new PutPolicy(Conf.BUCKET_NAME);
        try {
            uptoken = putPolicy.token(mac);
            Log.e("qiniu uptoken:", uptoken);
            return uptoken;
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUploadFileName() {
        DateFormat dateformat = new DateFormat();
        String today = dateformat.format("yyyy-MM-dd hh:mm:ss", new Date()).toString();
        return today;
    }
}
