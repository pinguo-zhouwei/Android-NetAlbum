package us.pinguo.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import us.pinguo.album.view.CommonAlertDialog;
import us.pinguo.album.view.TimeLineLayout;
import us.pinguo.login.LoginActivity;
import us.pinguo.model.ImageScanner;
import us.pinguo.model.PhotoInfoCache;
import us.pinguo.model.PhotoItem;
import us.pinguo.model.UserInfoCache;
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

    }

    public void initView() {
        mTimeLineTopBg = (ImageView) findViewById(R.id.time_line_top_bg);
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
                PhotoItem item = photoItemList.get(0);
                ImageLoader.getInstance().displayImage("file://" + item.url, mTimeLineTopBg);
            }
        });

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
}
