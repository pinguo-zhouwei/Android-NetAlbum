package us.pinguo.model;

import android.content.SharedPreferences;
import android.text.TextUtils;
import us.pinguo.album.MyAlbum;
import us.pinguo.db.DBUserTable;
import us.pinguo.db.SandBoxSql;

/**
 * Created by Mr 周先森 on 2015/4/28.
 */
public class UserInfoCache {
    /**
     * 添加用户信息
     *
     * @param user
     */
    public void saveUser(User user) {
        try {
            DBUserTable table = new DBUserTable(SandBoxSql.getInstance());
            if (!table.userHasInTable(user.userId)) {//如果表中没有该用户的数据，则插入
                table.insert(user);
            }
            //保存登录态
            SharedPreferences.Editor editor = MyAlbum.getSharedPreferences().edit();
            editor.putString("userId", user.userId);
            editor.putString("userName", user.userName);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据Id 获取用户信息
     *
     * @param userId
     * @return
     */
    public User getUser(String userId) {
        try {
            DBUserTable table = new DBUserTable(SandBoxSql.getInstance());
            return table.findUserById(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否登录
     *
     * @return
     */
    public boolean isLogin() {

        return TextUtils.isEmpty(MyAlbum.getSharedPreferences().getString("userId", "")) ? false : true;
    }

    /**
     * 注销
     */
    public void loginOut() {
        SharedPreferences.Editor editor = MyAlbum.getSharedPreferences().edit();
        editor.putString("userId", "");
        editor.commit();
    }
}
