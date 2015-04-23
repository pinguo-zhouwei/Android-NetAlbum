package us.pinguo.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import us.pinguo.album.view.TimeLineLayout;
import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;
import us.pinguo.login.LoginActivity;
import us.pinguo.model.ImageScanner;
import us.pinguo.model.PhotoItem;
import us.pinguo.stickygridheaders.StickyGridHeadersGridView;
import us.pinguo.utils.PhotoCompator;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/4.
 */
public class TimeLineActivity extends Activity implements View.OnClickListener, ImageScanner.ScanImageListener {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PhotoItem> photoItemList = null;
        try {
            DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
            photoItemList = dbPhotoTable.queryPhoto();
            Log.i("TimeLineActivity", "TimeLineActivity size:" + photoItemList.size());
            if (photoItemList == null || photoItemList.size() == 0) {
                ImageScanner imageScanner = new ImageScanner(this);
                imageScanner.setmScanImageListener(this);
                new Thread(imageScanner).start();
            } else {
                Collections.sort(photoItemList, new PhotoCompator());

                PhotoItem item = photoItemList.get(0);
                ImageLoader.getInstance().displayImage("file://" + item.photoUri, mTimeLineTopBg);

                mTimeLineAdapter.setPhotoItemList(photoItemList);
                //
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setVisibility(View.GONE);
                        mTimeLineAdapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onScanImageComplete(final List<PhotoItem> photoItemList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setVisibility(View.GONE);
                mTimeLineAdapter.setPhotoItemList(photoItemList);
                mTimeLineAdapter.notifyDataSetChanged();
            }
        });

    }

}
