package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by huangwei on 15-3-4.
 */
public class StickyHeaderLayout extends RelativeLayout {

    private ImageView mCircleImg;

    public StickyHeaderLayout(Context context) {
        super(context);
    }

    public StickyHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyHeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
/*
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ProgressCircleImageView) {
                mCircleImg = (ProgressCircleImageView) child;
                break;
            }
        }
    }

    public void onPull(float scaleOfLayout) {
        float rate = scaleOfLayout / PullToRefreshListView.StickyItemListView.REFRESH_POINT;
        if (rate < 1 && mCircleImg.getState() != ProgressCircleImageView.STATE_CIRCLING) {
            mCircleImg.setState(ProgressCircleImageView.STATE_PROGRESS);
        }
        rate = rate > 1 ? 1 : rate;
        mCircleImg.setProgress(rate);
    }

    public void pullToRefresh() {
        mCircleImg.setState(ProgressCircleImageView.STATE_PROGRESS);
    }

    public void refreshing() {
        mCircleImg.start();
    }

    public void releaseToRefresh() {
    }

    public void reset() {
        mCircleImg.reset();
    }

    public void triggerRefresh() {
        mCircleImg.start();
    }*/
}
