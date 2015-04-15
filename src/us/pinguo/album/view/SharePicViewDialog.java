package us.pinguo.album.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import us.pinguo.album.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouwei on 15-1-9.
 */
public class SharePicViewDialog extends BaseShareView {
    private static final String TAG = "BabyPicShareView";
    private Context mContext;
    // 微信朋友包名和处理类名
    private static final String WEIXIN_FRIENDS_PACKAGE_NAME = "com.tencent.mm";
    private static final String WEIXIN_FRIENDS_ACTIVITY_NAME = "com.tencent.mm.ui.tools.ShareImgUI";

    // 微信朋友圈包名和处理类名
    private static final String WEIXIN_FRIENDS_LINES_PACKAGE_NAME = "com.tencent.mm";
    private static final String WEIXIN_FRIENDS_LINES_ACTIVITY_NAME = "com.tencent.mm.ui.tools.ShareToTimeLineUI";

    // QQ空间朋友包名和处理类名com.qzone.ui.operation.QZonePublishMoodActivity
    private  String QZONE_PACKAGE_NAME = "com.qzone";
    private  String QZONE_ACTIVITY_NAME = "QZonePublishMoodActivity";
    // QQ包名和处理类名
    private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    private static final String QQ_ACTIVITY_NAME = "com.tencent.mobileqq.activity.JumpActivity";

    //QQ平板
    private static final String QQ_PACKAGE_NAME_PAD = "com.tencent.mobileqqi";
    private static final String QQ_ACTIVITY_NAME_PAD = "com.tencent.mobileqq.activity.JumpActivity";

    // 微博包名和处理类名
    private static final String WEIBO_PACKAGE_NAME = "com.sina.weibo";
    private static final String WEIBO_ACTIVITY_NAME = "com.sina.weibo.ComposerDispatchActivity";

    public static final int POSITION_WEIXIN = 0;
    public static final int POSITION_WEIXIN_FRIEND = 1;
    public static final int POSITION_WEIBO = 3;
    public static final int POSITION_QZINE = 2;
    public static final int POSITION_QQ = 4;
    public static final int POSITION_SAVE = 5;
    public static final int POSITION_MORE = 6;

    // 点击事件回调器
    private ShareClickListener mOnClickListener;

    public SharePicViewDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void initData() {
        mItems = new ArrayList<PicItem>();
        PicItem picItem = new PicItem(R.drawable.album_photo_share_by_weixin, "微信好友");
        mItems.add(picItem);
        picItem = new PicItem(R.drawable.album_photo_share_by_weixin_timeline, "朋友圈");
        mItems.add(picItem);

        picItem = new PicItem(R.drawable.album_share_pohto_by_qq_zone, "QQ空间");
        mItems.add(picItem);

        picItem = new PicItem(R.drawable.album_share_photo_by_sina_weibo, "新浪微博");
        mItems.add(picItem);

        picItem = new PicItem(R.drawable.album_photo_share_by_qq, "QQ好友");
        mItems.add(picItem);
        picItem = new PicItem(R.drawable.album_photo_share_save, "保存至本地");
        mItems.add(picItem);
        picItem = new PicItem(R.drawable.album_photo_share_more, "更多");
        mItems.add(picItem);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mOnClickListener!=null){
            mOnClickListener.onShareItemClick(this, position);
        }

    }

    /**
     * 分享照片接口
     *
     * @param context 应用上下文
     * @param uri     分享图片的URI
     * @param name    分享web站点的名字
     */
    public void shareBabyPicToWebSite(Context context, Uri uri, ShareWebName name) {
        String packageName = null;
        String handleIntentActivityName = null;
        switch (name) {
            case WEIXIN_FRIENDS:
                packageName = WEIXIN_FRIENDS_PACKAGE_NAME;
                handleIntentActivityName = WEIXIN_FRIENDS_ACTIVITY_NAME;
                break;
            case WEIXIN_FRIEDNS_LINES:
                packageName = WEIXIN_FRIENDS_LINES_PACKAGE_NAME;
                handleIntentActivityName = WEIXIN_FRIENDS_LINES_ACTIVITY_NAME;
                break;
            case QZONE:
                packageName = QZONE_PACKAGE_NAME;
                handleIntentActivityName = QZONE_ACTIVITY_NAME;
                break;
            case QQ:
               /* if (CommonUtils.isTablet(mContext)) {
                    packageName = QQ_PACKAGE_NAME_PAD;
                    handleIntentActivityName = QQ_ACTIVITY_NAME_PAD;
                } else {*/
                    packageName = QQ_PACKAGE_NAME;
                    handleIntentActivityName = QQ_ACTIVITY_NAME;
              //  }
                break;
            case WEIBO:
                packageName = WEIBO_PACKAGE_NAME;
                handleIntentActivityName = WEIBO_ACTIVITY_NAME;
                break;
        }

        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(packageName,
                    handleIntentActivityName);
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (packageName.equals(WEIXIN_FRIENDS_PACKAGE_NAME) && handleIntentActivityName.equals(WEIXIN_FRIENDS_ACTIVITY_NAME)) {
                //分享图片到微信要新开一个任务栈，不然不能从微信返回本应用
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (packageName.equals(WEIBO_PACKAGE_NAME) && handleIntentActivityName.equals(WEIBO_ACTIVITY_NAME)) {//if share to weibo,we need add default content
                intent.putExtra(Intent.EXTRA_TEXT, "@" + mContext.getString(R.string.weibo_share_content));
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查该分享站点是否存在
     *
     * @param context 应用上下文
     * @param name    站点名字
     * @return true 存在该站点  false 不存在该站点
     */
    public boolean checkSSOIsExist(Context context, ShareWebName name) {
        String packageName = null;
        String handleIntentActivityName = null;
        switch (name) {
            case WEIXIN_FRIENDS:
                packageName = "com.tencent.mm";
                handleIntentActivityName = "com.tencent.mm.ui.tools.ShareImgUI";
                break;
            case WEIXIN_FRIEDNS_LINES:
                packageName = "com.tencent.mm";
                handleIntentActivityName = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
                break;
            case QZONE:
                packageName = QZONE_PACKAGE_NAME;
                handleIntentActivityName = QZONE_ACTIVITY_NAME;
                break;
            case QQ:
               /* if (CommonUtils.isTablet(mContext)) {
                    packageName = QQ_PACKAGE_NAME_PAD;
                    handleIntentActivityName = QQ_ACTIVITY_NAME_PAD;
                } else {*/
                    packageName = "com.tencent.mobileqq";
                    handleIntentActivityName = "com.tencent.mobileqq.activity.JumpActivity";
              //  }
                break;
            case WEIBO:
                packageName = WEIBO_PACKAGE_NAME;
                handleIntentActivityName = WEIBO_ACTIVITY_NAME;
                break;
        }
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("image/*");
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(it, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                ActivityInfo activityInfo = info.activityInfo;
                Log.i(TAG, "activityInfo.packageName = " + activityInfo.packageName + " activityInfo.name = " + activityInfo.name);
                if (activityInfo.packageName.contains(packageName) && activityInfo.name.contains(handleIntentActivityName)) {
                    if (activityInfo.name.contains(QZONE_PACKAGE_NAME)) {
                        QZONE_ACTIVITY_NAME = activityInfo.name;
                    }
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 自定义的代表分享站点的名字
     */
    public enum ShareWebName {
        // 微信朋友
        WEIXIN_FRIENDS,
        // 微信朋友圈
        WEIXIN_FRIEDNS_LINES,
        // QQ空间
        QZONE,
        // QQ
        QQ,
        // 微博
        WEIBO
    }

    /**
     * 设置回调监听器
     *
     * @param shareClickListener 点击事件监听器
     */
    public void setShareClickListener(ShareClickListener shareClickListener) {
        mOnClickListener = shareClickListener;
    }

    public interface ShareClickListener {
        /**
         * 分享回调接口
         *
         * @param position 当前view的位置
         */
        public void onShareItemClick(SharePicViewDialog dialog, int position);
    }
}
