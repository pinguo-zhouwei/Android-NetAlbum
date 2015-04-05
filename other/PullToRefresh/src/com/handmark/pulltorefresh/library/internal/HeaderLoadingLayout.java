package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.R;

/**
 * Created by huangwei on 15-1-14.
 */
public class HeaderLoadingLayout extends LoadingLayout {

    private ViewGroup mInnerLayout;

    private ImageView mRefreshView;
    private ImageView mSloganImg;
    private TextView mTextView;

    protected final PullToRefreshBase.Mode mMode;
    protected final PullToRefreshBase.Orientation mScrollDirection;

    private String mRefreshingStr;
    private String mPullRefreshStr;
    private String mReleashRefreshStr;


    public HeaderLoadingLayout(Context context, final PullToRefreshBase.Mode mode, final PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        mMode = mode;
        mScrollDirection = scrollDirection;

        mRefreshingStr = context.getString(R.string.pull_to_refresh_header_refreshing);
        mPullRefreshStr = context.getString(R.string.pull_to_refresh_header_pull_refresh);
        mReleashRefreshStr = context.getString(R.string.pull_to_refresh_header_release_refresh);

        switch (scrollDirection) {
            case HORIZONTAL:
                //TODO
            case VERTICAL:
            default:
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);
                break;
        }

        mInnerLayout = (ViewGroup) findViewById(R.id.header_fl_inner);

        mRefreshView = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_header_circlerefresh);
        mSloganImg = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_header_slogan);
        mTextView = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_header_text);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        lp.gravity = scrollDirection == PullToRefreshBase.Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;
        lp.height = getResources().getDimensionPixelOffset(R.dimen.header_height);

        reset();
    }

    public final void hideAllViews() {
        if (mSloganImg.getVisibility() == View.VISIBLE) {
            mSloganImg.setVisibility(View.INVISIBLE);
        }
        if (mRefreshView.getVisibility() == View.VISIBLE) {
            mRefreshView.setVisibility(View.INVISIBLE);
        }
        if (mTextView.getVisibility() == View.VISIBLE) {
            mTextView.setVisibility(View.INVISIBLE);
        }
    }

    public final void onPull(float scaleOfLayout) {

    }

    public final void pullToRefresh() {
        if (mRefreshView.getBackground() instanceof AnimationDrawable) {
            ((AnimationDrawable) mRefreshView.getBackground()).stop();
        }
        mTextView.setText(mPullRefreshStr);
    }

    public final void refreshing() {
//        mRefreshView.start();
        if (mRefreshView.getBackground() instanceof AnimationDrawable) {
            ((AnimationDrawable) mRefreshView.getBackground()).start();
        }
        mTextView.setText(mRefreshingStr);
    }

    public final void releaseToRefresh() {
        mTextView.setText(mReleashRefreshStr);
    }

    public final void reset() {
//        mRefreshView.reset();
        if (mRefreshView.getBackground() instanceof AnimationDrawable) {
            ((AnimationDrawable) mRefreshView.getBackground()).stop();
        }
        mTextView.setText(mPullRefreshStr);
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        setSubHeaderText(label);
    }

    public void setLoadingDrawable(Drawable imageDrawable) {
    }

    public void setPullLabel(CharSequence pullLabel) {

    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    public void setReleaseLabel(CharSequence releaseLabel) {

    }

    @Override
    public void setTextTypeface(Typeface tf) {

    }

    @Override
    public int getContentSize() {
        switch (mScrollDirection) {
            case HORIZONTAL:
                return mInnerLayout.getWidth();
            case VERTICAL:
            default:
                return mInnerLayout.getHeight();
        }
    }

    public final void showInvisibleViews() {
        if (View.INVISIBLE == mSloganImg.getVisibility()) {
            mSloganImg.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mRefreshView.getVisibility()) {
            mRefreshView.setVisibility(View.VISIBLE);
        }
        if (mTextView.getVisibility() == View.INVISIBLE) {
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setSubHeaderText(CharSequence label) {
    }

    private void setSubTextAppearance(int value) {
    }

    private void setSubTextColor(ColorStateList color) {
    }

    private void setTextAppearance(int value) {
    }

    private void setTextColor(ColorStateList color) {
    }

    @Override
    public void setLoadingImageViewVisible(boolean visible) {
    }

    @Override
    public void setLoadingProgressVisible(boolean visible) {
    }

}
