package us.pinguo.album;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.TextView;
import org.w3c.dom.Text;
import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;
import us.pinguo.model.ImageScanner;
import us.pinguo.model.PhotoItem;
import us.pinguo.stickygridheaders.StickyGridHeadersGridView;

import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/4.
 */
public class TimeLineActivity extends Activity implements View.OnClickListener,ImageScanner.ScanImageListener{
   // private PullToRefreshListView  mPullToRefreshListView;
    private TimeLineAdapter mTimeLineAdapter;
    private StickyGridHeadersGridView mGridView;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_line_layout);
        initView();

    }

    public void initView(){
       /* mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_dynamic_comment);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setScrollingWhileRefreshingEnabled(true);
        mPullToRefreshListView.post(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshListView.setFooterText(getString(R.string.action_settings));
            }
        });*/
        mGridView = (StickyGridHeadersGridView) findViewById(R.id.timeline_gridView);
        mTextView = (TextView) findViewById(R.id.welcom_text);
        mTimeLineAdapter = new TimeLineAdapter();
        mGridView.setAdapter(mTimeLineAdapter);
        mTimeLineAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PhotoItem> photoItemList = null;
        try {
            DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
            photoItemList = dbPhotoTable.queryPhoto();
            Log.i("TimeLineActivity","TimeLineActivity size:"+photoItemList.size());
            if(photoItemList==null||photoItemList.size() == 0){
                ImageScanner imageScanner = new ImageScanner(this);
                imageScanner.setmScanImageListener(this);
                new Thread(imageScanner).start();
            }else{
              mTextView.setVisibility(View.GONE);
              mTimeLineAdapter.setPhotoItemList(photoItemList);
              mTimeLineAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {

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
