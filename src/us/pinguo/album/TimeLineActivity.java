package us.pinguo.album;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Mr 周先森 on 2015/4/4.
 */
public class TimeLineActivity extends Activity implements View.OnClickListener{
    private PullToRefreshListView  mPullToRefreshListView;
    private TimeLineAdapter mTimeLineAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView(){
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_dynamic_comment);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setScrollingWhileRefreshingEnabled(true);
        mPullToRefreshListView.post(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshListView.setFooterText(getString(R.string.action_settings));
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
