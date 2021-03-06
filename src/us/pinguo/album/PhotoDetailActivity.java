package us.pinguo.album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import us.pinguo.album.utils.BitmapUtils;
import us.pinguo.album.utils.FileUtils;
import us.pinguo.album.utils.ZoomOutPageTransformer;
import us.pinguo.album.view.SharePicViewDialog;
import us.pinguo.model.PhotoInfoCache;
import us.pinguo.model.PhotoItem;
import us.pinguo.model.Storage;
import us.pinguo.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/15.
 */
public class PhotoDetailActivity extends AsyncTaskActivity implements SharePicViewDialog.ShareClickListener, View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = PhotoDetailActivity.class.getSimpleName();
    public static final int FLAG_NET = 0;
    public static final int FLAG_LOCAL = 1;
    public static final int SAVE_PIC = 2;
    public static final int SHARE_PIC = 3;
    private ViewPager mViewPager;
    private List<PhotoItem> mPhotoItemList;
    private PhotoPagerAdapter mAdapter;
    private SharePicViewDialog mSharePicViewDialog;

    private PhotoItem mCurrentPhotoItem;
    private int mCurrentIndex = 0;
    private int mFlag = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == SAVE_PIC) {
                final Bitmap bitmap = (Bitmap) msg.obj;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();

                        String path = FileUtils.generatePicPath();
                        if (path == null) {
                            return;
                        }
                        try {
                            BitmapUtils.saveBitmap(path, bitmap, 100);
                            Toast.makeText(PhotoDetailActivity.this, R.string.save_pic_path, Toast.LENGTH_SHORT).show();
                            Storage.addStorage(PhotoDetailActivity.this, path);//插入媒体数据库
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


            } else if (msg.what == SHARE_PIC) {
                final Bitmap bitmap = (Bitmap) msg.obj;
                final int positon = msg.arg1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();

                        String path = FileUtils.generatePicPath();
                        if (path == null) {
                            return;
                        }
                        try {
                            BitmapUtils.saveBitmap(path, bitmap, 100);
                            //    Toast.makeText(PhotoDetailActivity.this, R.string.save_pic_path, Toast.LENGTH_SHORT).show();
                            Storage.addStorage(PhotoDetailActivity.this, path);//插入媒体数据库
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //分享
                        doShare(Uri.fromFile(new File(path)), positon);

                    }
                });


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail_layout);
        initData();
        initView();
    }

    public void initView() {
        TextView tvTitile = (TextView) findViewById(R.id.title_text_title);
        tvTitile.setText(R.string.big_pic);
        mViewPager = (ViewPager) findViewById(R.id.photo_detail_viewPager);
        //设置ViewPager的切换动画
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mAdapter = new PhotoPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentIndex);
        mAdapter.notifyDataSetChanged();
        mViewPager.setOnPageChangeListener(this);
        findViewById(R.id.photo_bottom_view).setOnClickListener(this);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.title_back_btn).setOnClickListener(this);
    }

    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCurrentIndex = bundle.getInt("index");
            mFlag = bundle.getInt("flag");
        }

        if (mFlag == FLAG_LOCAL) {
            mPhotoItemList = PhotoInfoCache.getLocalPhoto();
        } else if (mFlag == FLAG_NET) {
            PhotoInfoCache photoInfoCache = new PhotoInfoCache();
            mPhotoItemList = photoInfoCache.getPhoto();
        }
        mCurrentPhotoItem = mPhotoItemList.get(mCurrentIndex);
        Log.i("zhouwei", "PhotoDetailActivity " + mPhotoItemList.size());
    }

    public void generatePhotoUri(final String path, final int sharePosition) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(path);

                Message msg = new Message();
                msg.what = SHARE_PIC;
                msg.obj = bitmap;
                msg.arg1 = sharePosition;
                mHandler.handleMessage(msg);
            }
        }).start();

    }

    @Override
    public void onShareItemClick(SharePicViewDialog dialog, int position) {
        Uri uri = null;
        if (mCurrentPhotoItem != null) {
            if (mCurrentPhotoItem.url.startsWith("http://")) {
                // File file = ImageLoader.getInstance().getDiskCache().get(mCurrentPhotoItem.url);
                //  uri = Uri.fromFile(file);
                generatePhotoUri(mCurrentPhotoItem.url, position);
                return;
            } else {
                uri = Uri.fromFile(new File(mCurrentPhotoItem.url));
            }

        }
        if (uri == null) {
            return;
        }
        if (!NetworkUtils.hasNet(this)) {
            Toast.makeText(this, R.string.can_not_share, Toast.LENGTH_SHORT).show();
            return;
        }
        doShare(uri,position);
    }

    private void doShare(Uri uri, int position) {
        boolean isShareWebExist;
        switch (position) {
            case SharePicViewDialog.POSITION_WEIXIN:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.WEIXIN_FRIENDS);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.WEIXIN_FRIENDS);
                }
                break;
            case SharePicViewDialog.POSITION_WEIXIN_FRIEND:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.WEIXIN_FRIEDNS_LINES);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.WEIXIN_FRIEDNS_LINES);
                }
                break;
            case SharePicViewDialog.POSITION_QZINE:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.QZONE);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.QZONE);
                }
                break;
            case SharePicViewDialog.POSITION_WEIBO:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.WEIBO);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.WEIBO);
                }
                break;
            case SharePicViewDialog.POSITION_QQ:
                isShareWebExist = mSharePicViewDialog.checkSSOIsExist(this, SharePicViewDialog.ShareWebName.QQ);
                if (isShareWebExist) {
                    mSharePicViewDialog.shareBabyPicToWebSite(this, uri,
                            SharePicViewDialog.ShareWebName.QQ);
                }
                break;
            case SharePicViewDialog.POSITION_SAVE:
                if (!mCurrentPhotoItem.url.startsWith("http://")) {
                    Toast.makeText(this, "本地已经有该照片了哟~", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(mCurrentPhotoItem.url);

                        Message msg = new Message();
                        msg.what = SAVE_PIC;
                        msg.obj = bitmap;
                        mHandler.handleMessage(msg);
                    }
                }).start();

                break;
            case SharePicViewDialog.POSITION_MORE:
                systemShare("更多分享", uri);
                break;
        }
    }

    private void systemShare(String activityTitle, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_bottom_view:
                showShareDialog();
                break;
            case R.id.btn_edit:
                Bundle bundle = new Bundle();
                bundle.putString("path", mCurrentPhotoItem.url);
                Intent intent = new Intent(this, EditPicActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.title_back_btn:
                finish();
                break;
        }
    }

    private void showShareDialog() {
        if (mSharePicViewDialog == null || !mSharePicViewDialog.isShowing()) {
            mSharePicViewDialog = new SharePicViewDialog(this);
            mSharePicViewDialog.setShareClickListener(this);
            mSharePicViewDialog.show();
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        Log.i(TAG, "position：" + i);
        mCurrentPhotoItem = mPhotoItemList.get(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    class PhotoPagerAdapter extends PagerAdapter {
        DisplayImageOptions displayImageOptions;

        public PhotoPagerAdapter() {
            displayImageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)
                    .build();
        }

        @Override
        public int getCount() {
            return mPhotoItemList == null ? 0 : mPhotoItemList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoItem photoItem = mPhotoItemList.get(position);
            // mCurrentPhotoItem = photoItem;
            Log.i("zhouwei", "url:" + photoItem.url);
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.photo_detail_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.photo_detail_img);
            String url = "";
            if (photoItem.url.startsWith("http://")) {
                url = photoItem.url;
            } else {
                url = "file://" + photoItem.url;
            }
            ImageLoader.getInstance().displayImage(url, new ImageViewAware(imageView), displayImageOptions);
            ((ViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // ImageView imageView = (ImageView) object;
            // imageView = null;
        }
    }
}
