package us.pinguo.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Mr 周先森 on 2015/5/9.
 */
public class SplashActivity extends Activity implements View.OnClickListener {
    private int[] mRes = new int[]{R.drawable.login_home_bg1, R.drawable.login_home_bg2, R.drawable.login_home_bg3};
    private ViewPager mViewPager;
    private SplashPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        initView();
    }

    public void initView() {
        mViewPager = (ViewPager) findViewById(R.id.splash_view_pager);
        mAdapter = new SplashPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        findViewById(R.id.btn_local_album).setOnClickListener(this);
        findViewById(R.id.btn_net_album).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_local_album:
                startActivity(new Intent(this, TimeLineActivity.class));
                break;
            case R.id.btn_net_album:
                break;
        }
    }

    class SplashPagerAdapter extends PagerAdapter {
        DisplayImageOptions displayImageOptions;

        public SplashPagerAdapter() {
            displayImageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)
                    .build();
        }

        @Override
        public int getCount() {
            return mRes.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.photo_detail_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.photo_detail_img);
            ImageLoader.getInstance().displayImage("drawable://" + mRes[position], imageView);
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
