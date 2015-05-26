package us.pinguo.album;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import us.pinguo.album.view.CommonAlertDialog;
import us.pinguo.album.view.TimeLineLayout;
import us.pinguo.login.LoginActivity;
import us.pinguo.model.*;
import us.pinguo.stickygridheaders.StickyGridHeadersGridView;

import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/4.
 */
public class TimeLineActivity extends Activity implements View.OnClickListener, ImageScanner.ScanImageListener, AdapterView.OnItemClickListener {
    // private PullToRefreshListView  mPullToRefreshListView;
    private TimeLineAdapter mTimeLineAdapter;
    private StickyGridHeadersGridView mGridView;
    private TextView mTextView;
    private ImageView mTimeLineTopBg;

    private TimeLineLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_line_layout);
        initView();
        initData();
    }

    public void initView() {
        mTimeLineTopBg = (ImageView) findViewById(R.id.time_line_top_bg);
        findViewById(R.id.time_line_top_layout).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        mGridView = (StickyGridHeadersGridView) findViewById(R.id.timeline_gridView);
        mTextView = (TextView) findViewById(R.id.welcom_text);
        mTimeLineAdapter = new TimeLineAdapter(this);
        mGridView.setAdapter(mTimeLineAdapter);
        //  mTimeLineAdapter.notifyDataSetChanged();
        findViewById(R.id.btn_my_album).setOnClickListener(this);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageScanner imageScanner = new ImageScanner(this);
        imageScanner.setmScanImageListener(this);
        new Thread(imageScanner).start();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_camera:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_my_album:
                UserInfoCache userInfoCache = new UserInfoCache();
                if (userInfoCache.isLogin()) {
                    intent = new Intent(this, NetAlbumActivity.class);
                    startActivity(intent);
                } else {
                    showDialog();
                }
                break;
            case R.id.time_line_top_layout:
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            dialog.dismiss();
                            // Toast.makeText(TimeLineActivity.this,"照片封面",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);
                        } else if (which == 1) {
                            dialog.dismiss();
                            //   Toast.makeText(TimeLineActivity.this,"模板封面",Toast.LENGTH_SHORT).show();
                            startActivityForResult(new Intent(TimeLineActivity.this, SelectSkinActivity.class), 2);
                        }
                    }
                };
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("更换封面");
                dialog.setItems(new String[]{"照片封面", "模板封面"}, listener);
                dialog.create().show();
                break;
        }
    }


    private void showDialog() {
        CommonAlertDialog alertDialog = new CommonAlertDialog(this, getString(R.string.login_tips));
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
        CommonAlertDialog.PositiveOnClickLister positiveOnClickLister = new CommonAlertDialog.PositiveOnClickLister() {
            @Override
            public void onClick(CommonAlertDialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(TimeLineActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };
        alertDialog.setPositiveOnClickLister(positiveOnClickLister);
        alertDialog.show();
    }

    @Override
    public void onScanImageComplete(final List<PhotoItem> photoItemList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setVisibility(View.GONE);
                PhotoInfoCache.setLocalPhoto(photoItemList);
                mTimeLineAdapter.setPhotoItemList(photoItemList);
                mTimeLineAdapter.notifyDataSetChanged();
                //展示banner
                //   PhotoItem item = photoItemList.get(0);
                //  ImageLoader.getInstance().displayImage("file://" + item.url, mTimeLineTopBg);
            }
        });

    }

    public void initData() {
        CoverCache coverCache = new CoverCache(this);
        final Cover cover = coverCache.getCover();
        if (cover.color == 0 && TextUtils.isEmpty(cover.coverUrl)) {
            mTimeLineTopBg.setImageResource(R.color.time_line_top_default);
            return;
        }
        if (TextUtils.isEmpty(cover.coverUrl)) {
            if (cover.color != 0) {
                Drawable drawable = new Drawable() {
                    @Override
                    public void draw(Canvas canvas) {
                        canvas.drawColor(cover.color);
                    }

                    @Override
                    public void setAlpha(int alpha) {

                    }

                    @Override
                    public void setColorFilter(ColorFilter cf) {

                    }

                    @Override
                    public int getOpacity() {
                        return 0;
                    }
                };
                mTimeLineTopBg.setImageDrawable(drawable);
            }
        } else {
            ImageLoader.getInstance().displayImage("file://" + cover.coverUrl, mTimeLineTopBg);
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", position);
        bundle.putInt("flag", PhotoDetailActivity.FLAG_LOCAL);
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Uri uri = data.getData();
                //好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                String path = cursor.getString(column_index);

                Cover cover = new Cover();
                cover.coverUrl = path;
                CoverCache coverCache = new CoverCache(this);
                coverCache.saveCover(cover);
                // ImageLoader.getInstance().displayImage("file://"+path,mTimeLineTopBg);
                ImageLoader.getInstance().loadImage("file://" + path, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (loadedImage != null) {
                            mTimeLineTopBg.setImageBitmap(loadedImage);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                final int color = data.getExtras().getInt("color");
                Drawable drawable = new Drawable() {
                    @Override
                    public void draw(Canvas canvas) {
                        canvas.drawColor(color);
                    }

                    @Override
                    public void setAlpha(int alpha) {

                    }

                    @Override
                    public void setColorFilter(ColorFilter cf) {

                    }

                    @Override
                    public int getOpacity() {
                        return 0;
                    }
                };
                mTimeLineTopBg.setImageDrawable(drawable);

                Cover cover = new Cover();
                cover.color = color;
                CoverCache coverCache = new CoverCache(this);
                coverCache.saveCover(cover);
            }
        }
    }
}
