package us.pinguo.album;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import org.w3c.dom.Text;
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
        ImageScanner imageScanner = new ImageScanner(this);
        new Thread(imageScanner).start();
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
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onScanImageComplete(List<PhotoItem> photoItemList) {
        mTextView.setVisibility(View.GONE);
        mTimeLineAdapter = new TimeLineAdapter(photoItemList);
        mGridView.setAdapter(mTimeLineAdapter);
        mTimeLineAdapter.notifyDataSetChanged();
    }
}
