package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
public class FooterLoadingLayout extends LoadingLayout {
    private ViewGroup mInnerLayout;

    private ImageView mFooterRefreshView;
    private TextView mFooterTxt;

    protected final PullToRefreshBase.Mode mMode;
    protected final PullToRefreshBase.Orientation mScrollDirection;

    public FooterLoadingLayout(Context context, final PullToRefreshBase.Mode mode, final PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        mMode = mode;
        mScrollDirection = scrollDirection;

        switch (scrollDirection) {
            case HORIZONTAL:
                //TODO
            case VERTICAL:
            default:
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer_vertical, this);
                break;
        }

        mInnerLayout = (ViewGroup) findViewById(R.id.footer_fl_inner);
        mFooterRefreshView = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_footer_circlerefresh);
        mFooterTxt = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_footer_text);
        ((AnimationDrawable) mFooterRefreshView.getBackground()).start();

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        lp.gravity = scrollDirection == PullToRefreshBase.Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;
        lp.height = getResources().getDimensionPixelOffset(R.dimen.footer_height);

        reset();
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

    public final void hideAllViews() {
        if (mFooterRefreshView.getVisibility() == View.VISIBLE) {
            mFooterRefreshView.setVisibility(View.INVISIBLE);
        }
        if (mFooterTxt.getVisibility() == View.VISIBLE) {
            mFooterTxt.setVisibility(View.INVISIBLE);
        }
    }

    public final void onPull(float scaleOfLayout) {
    }

    public final void pullToRefresh() {
    }

    public final void refreshing() {
    }

    public final void releaseToRefresh() {
    }

    public final void reset() {
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        setSubHeaderText(label);
    }

    public void setLoadingDrawable(Drawable imageDrawable) {
    }

    public void setPullLabel(CharSequence pullLabel) {
        mFooterTxt.setText(pullLabel);
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    public void setReleaseLabel(CharSequence releaseLabel) {

    }

    @Override
    public void setTextTypeface(Typeface tf) {

    }

    public final void showInvisibleViews() {
        if (View.INVISIBLE == mFooterRefreshView.getVisibility()) {
            mFooterRefreshView.setVisibility(View.VISIBLE);
        }
        if (mFooterTxt.getVisibility() == View.INVISIBLE) {
            mFooterTxt.setVisibility(View.VISIBLE);
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
