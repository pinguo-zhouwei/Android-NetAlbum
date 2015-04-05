/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.StickyHeaderLayout;
import us.pinguo.uilext.util.DisplayUtil;

public class PullToRefreshListView extends PullToRefreshAdapterViewBase<ListView> {

    private LoadingLayout mHeaderLoadingView;
    private LoadingLayout mFooterLoadingView;

    private FrameLayout mLvFooterLoadingFrame;

    private boolean mListViewExtrasEnabled;

    public PullToRefreshListView(Context context) {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected void onRefreshing(final boolean doScroll) {
        /**
         * If we're not showing the Refreshing view, or the list is empty, the
         * the header/footer views won't show so we use the normal method.
         */
        ListAdapter adapter = mRefreshableView.getAdapter();
        if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.onRefreshing(doScroll);
            return;
        }

        super.onRefreshing(false);

        final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
        final int selection, scrollToY;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                origLoadingView = getFooterLayout();
                listViewLoadingView = mFooterLoadingView;
                oppositeListViewLoadingView = mHeaderLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToY = getScrollY() - getFooterSize();
                break;
            case PULL_FROM_START:
            default:
                origLoadingView = getHeaderLayout();
                listViewLoadingView = mHeaderLoadingView;
                oppositeListViewLoadingView = mFooterLoadingView;
                selection = 0;
                scrollToY = getScrollY() + getHeaderSize();
                break;
        }

        // Hide our original Loading View
        origLoadingView.reset();
        origLoadingView.hideAllViews();

        // Make sure the opposite end is hidden too
        oppositeListViewLoadingView.setVisibility(View.GONE);

        // Show the ListView Loading View and set it to refresh.
        listViewLoadingView.setVisibility(View.VISIBLE);
        listViewLoadingView.refreshing();

        if (doScroll) {
            // We need to disable the automatic visibility changes for now
            disableLoadingLayoutVisibilityChanges();

            // We scroll slightly so that the ListView's header/footer is at the
            // same Y position as our normal header/footer
            setHeaderScroll(scrollToY);

            // Make sure the ListView is scrolled to show the loading
            // header/footer
            mRefreshableView.setSelection(selection);

            // Smooth scroll as normal
            smoothScrollTo(0);
        }
    }

    @Override
    protected void onReset() {
        /**
         * If the extras are not enabled, just call up to super and return.
         */
        if (!mListViewExtrasEnabled) {
            super.onReset();
            return;
        }

        final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
        final int scrollToHeight, selection;
        final boolean scrollLvToEdge;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                originalLoadingLayout = getFooterLayout();
                listViewLoadingLayout = mFooterLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToHeight = getFooterSize();
                scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
                break;
            case PULL_FROM_START:
            default:
                originalLoadingLayout = getHeaderLayout();
                listViewLoadingLayout = mHeaderLoadingView;
                scrollToHeight = -getHeaderSize();
                selection = 0;
                scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
                break;
        }

        // If the ListView header loading layout is showing, then we need to
        // flip so that the original one is showing instead
        if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

            // Set our Original View to Visible
            originalLoadingLayout.showInvisibleViews();

            // Hide the ListView Header/Footer
            listViewLoadingLayout.setVisibility(View.GONE);

            /**
             * Scroll so the View is at the same Y as the ListView
             * header/footer, but only scroll if: we've pulled to refresh, it's
             * positioned correctly
             */
            if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                mRefreshableView.setSelection(selection);
                setHeaderScroll(scrollToHeight);
            }
        }

        // Finally, call up to super
        super.onReset();
    }

    @Override
    protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

        if (mListViewExtrasEnabled) {
            final Mode mode = getMode();

            if (includeStart && mode.showHeaderLoadingLayout()) {
                proxy.addLayout(mHeaderLoadingView);
            }
            if (includeEnd && mode.showFooterLoadingLayout()) {
                proxy.addLayout(mFooterLoadingView);
            }
        }

        return proxy;
    }

    protected ListView createListView(Context context, AttributeSet attrs) {
        final ListView lv;
//		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
//			lv = new InternalListViewSDK9(context, attrs);
//		} else {
//			lv = new InternalListView(context, attrs);
//		}
        lv = new StickyItemListView(context, attrs);

        return lv;
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView lv = createListView(context, attrs);

        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }

    @Override
    protected void handleStyledAttributes(TypedArray a) {
        super.handleStyledAttributes(a);

        mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

        if (mListViewExtrasEnabled) {
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

            // Create Loading Views ready for use later
            FrameLayout frame = new FrameLayout(getContext());
            mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
            mHeaderLoadingView.setVisibility(View.GONE);
            frame.addView(mHeaderLoadingView, lp);
            mRefreshableView.addHeaderView(frame, null, false);

            mLvFooterLoadingFrame = new FrameLayout(getContext());
            mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
            mFooterLoadingView.setVisibility(View.GONE);
            mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

            /**
             * If the value for Scrolling While Refreshing hasn't been
             * explicitly set via XML, enable Scrolling While Refreshing.
             */
            if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
                setScrollingWhileRefreshingEnabled(true);
            }
        }
    }

    @TargetApi(9)
    class InternalListViewSDK9 extends InternalListView {

        public InternalListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

            return returnValue;
        }
    }

    protected class InternalListView extends ListView implements EmptyViewMethodAccessor {

        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }

            super.setAdapter(adapter);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshListView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }

    }

    public class StickyItemListView extends InternalListViewSDK9 {

        private static final int REBOUND_DUTARION = 300;

        public static final float REFRESH_POINT = 0.5f;

        private State mState = State.RESET;

        private StickyHeaderLayout mStickyHeaderView;

        private boolean mIsDragging;

        private boolean mIsCancelled;

        private int mMaxHeight;

        private int mMinTranslation;

        private int mHeaderHeight;

        private float mPreY;
        /**
         * 拉伸超过此高度，即视为可以开始刷新
         */
        private float mRefreshH;

        private OnStickyRefreshListener mOnStickyRefreshListener;

        private boolean mFirstMove = true;

        private ValueAnimator mReboundAnimator;

        private final int DP5 = DisplayUtil.dpToPx(getContext(), 5);

        public StickyItemListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (mStickyHeaderView == null || mStickyHeaderView.getParent() == null) {
                return super.onTouchEvent(ev);
            }
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                    if (isFirstItemVisible()) {
//                        mIsDragging = true;
//                        mPreY = ev.getY();
//                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    //ACTION_DOWN貌似被上层拦截了，只能以此作为状态起始
                    float disY;
                    if (isRebounding()) {
                        mIsDragging = true;
                        if (mReboundAnimator != null && mReboundAnimator.isRunning()) {
                            mReboundAnimator.cancel();
                        }
                    } else if (mState == State.REFRESHING) {
                        mIsDragging = true;
                    } else {
                        if (mPreY == 0) {
                            mState = State.RESET;
                            mPreY = ev.getY();
                            super.onTouchEvent(ev);
                            return true;
                        }
                        disY = ev.getY() - mPreY;
                        //判断是否要启动下拉模式
                        if (mFirstMove) {
                            mFirstMove = false;
                            if (mStickyHeaderView.getTop() < -DP5) {
                                mIsDragging = false;
                            } else if (disY < 0) {
                                mIsDragging = false;
                            } else {
                                mIsDragging = true;
                            }
                        }
                    }
                    if (mPreY == 0) {
                        mPreY = ev.getY();
                    }
                    disY = ev.getY() - mPreY;

                    mPreY = ev.getY();
                    if (mIsDragging) {
                        doDrag(disY);
//                        if (mStickyHeaderView.getTop() <= 0) {
//                            return super.onTouchEvent(ev);
//                        } else {
//                            return true;
//                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mPreY = 0;
                    mFirstMove = true;
                    mIsDragging = false;
                    if (shouldRebound()) {
                        rebound();
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return super.onTouchEvent(ev);
        }

        private boolean shouldRebound() {
//            if (mIsDragging) {
//                return true;
//            }
            ViewGroup.LayoutParams lpHeader = mStickyHeaderView.getLayoutParams();
            if (lpHeader.height > mHeaderHeight) {
                return true;
            }
            return false;
        }

        private boolean isRebounding() {
            if (mReboundAnimator != null && mReboundAnimator.isRunning()) {
                return true;
            }
            return false;
        }

        private void doDrag(float disY) {
//            if(disY < 2f){
//                return;
//            }
            if (mHeaderHeight == 0) {
                return;
            }
            ViewGroup.LayoutParams lpHeader = mStickyHeaderView.getLayoutParams();
            lpHeader.height += (int) (disY);
            if (lpHeader.height > mMaxHeight) {
                lpHeader.height = mMaxHeight;
            } else if (lpHeader.height < mHeaderHeight) {
                lpHeader.height = mHeaderHeight;
            }
            mStickyHeaderView.setLayoutParams(lpHeader);
            if (mState == State.REFRESHING) {
                return;
            }
            if (mState == State.PULL_TO_REFRESH && lpHeader.height > mRefreshH) {
                mStickyHeaderView.releaseToRefresh();
                mState = State.RELEASE_TO_REFRESH;
            } else if (mState != State.PULL_TO_REFRESH && lpHeader.height <= mRefreshH) {
                mStickyHeaderView.pullToRefresh();
                mState = State.PULL_TO_REFRESH;
            }
            mStickyHeaderView.onPull((lpHeader.height - mHeaderHeight) / (float) (mMaxHeight - mHeaderHeight));
        }

        public void setItemSticky(StickyHeaderLayout view, int normalHeight, int maxHeight, int minTranslation) {
            if (view != null) {
                mStickyHeaderView = view;
                mMinTranslation = minTranslation;
                mMaxHeight = maxHeight;
                mHeaderHeight = normalHeight;
                mRefreshH = (mMaxHeight - mHeaderHeight) / 2f + mHeaderHeight;
            }
        }

        public void triggerRefresh() {
            if (mStickyHeaderView != null) {
                mStickyHeaderView.triggerRefresh();
                if (mOnStickyRefreshListener != null) {
                    mOnStickyRefreshListener.onStickyRefresh();
                }
            }
        }

        public StickyHeaderLayout getStickyHeader() {
            return mStickyHeaderView;
        }

        public void removeStickyHeader(View view) {
            mStickyHeaderView = null;
        }

        //        protected void measureHeaderHeight() {
//            int height = mStickyHeaderView.getHeight();
//
//            if (height == 0) {
//                //waiting for the height
//                mStickyHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        int height = mStickyHeaderView.getHeight();
//                        if (height > 0) {
//                            mStickyHeaderView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                            setHeightHeader(height);
//                        }
//                    }
//                });
//            } else {
//                setHeightHeader(height);
//            }
//
//        }

        private void rebound() {
            if (mReboundAnimator != null && mReboundAnimator.isRunning()) {
                mReboundAnimator.cancel();
            }
            final int height = mStickyHeaderView.getHeight();
            mReboundAnimator = ValueAnimator.ofFloat(1f, 0f);
            mReboundAnimator.setDuration(REBOUND_DUTARION);
            mReboundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = (Float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams lpHeader = mStickyHeaderView.getLayoutParams();
                    lpHeader.height = (int) (mHeaderHeight + (height - mHeaderHeight) * rate);
                    mStickyHeaderView.setLayoutParams(lpHeader);
                    if (mState != State.REFRESHING && mState != State.RESET) {
                        mStickyHeaderView.onPull((lpHeader.height - mHeaderHeight) / (float) (mMaxHeight - mHeaderHeight));
                    }
                }
            });
            mReboundAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mState == State.RELEASE_TO_REFRESH) {
                        mStickyHeaderView.refreshing();
                    }
                    mIsCancelled = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mIsCancelled) {
                        return;
                    }
                    if (mState == State.RELEASE_TO_REFRESH) {
                        mState = State.REFRESHING;
//                        mStickyHeaderView.refreshing();
                        if (mOnStickyRefreshListener != null) {
                            mOnStickyRefreshListener.onStickyRefresh();
                        }
                    }
                    if (mState != State.REFRESHING) {
                        mStickyHeaderView.reset();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsCancelled = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mReboundAnimator.start();
        }

        public void setOnStickyRefreshListener(OnStickyRefreshListener onStickyRefreshListener) {
            this.mOnStickyRefreshListener = onStickyRefreshListener;
        }

        public final void onRefreshComplete() {
//            if (mState == State.REFRESHING || mState == State.MANUAL_REFRESHING) {
            mState = State.RESET;
            if (mStickyHeaderView != null) {
                mStickyHeaderView.reset();
            }
//            }
        }

        private boolean isFirstItemVisible() {
            final Adapter adapter = mRefreshableView.getAdapter();

            if (null == adapter || adapter.isEmpty()) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
                }
                return true;

            } else {

                /**
                 * This check should really just be:
                 * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
                 * internally use a HeaderView which messes the positions up. For
                 * now we'll just add one to account for it and rely on the inner
                 * condition which checks getTop().
                 */
                if (mRefreshableView.getFirstVisiblePosition() <= 1) {
                    final View firstVisibleChild = mRefreshableView.getChildAt(0);
                    if (firstVisibleChild != null) {
                        return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                    }
                }
            }

            return false;
        }
    }

    public interface OnStickyRefreshListener {
        void onStickyRefresh();
    }

    public void setFooterText(String text) {
        if (mFooterLoadingView != null) {
            mFooterLoadingView.setPullLabel(text);
        }
        if (mFooterLayout != null) {
            mFooterLayout.setPullLabel(text);
        }
    }
}


//protected class InternalListView extends ListView implements EmptyViewMethodAccessor {
//
//    private boolean mAddedLvFooter = false;
//
//    public InternalListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        /**
//         * This is a bit hacky, but Samsung's ListView has got a bug in it
//         * when using Header/Footer Views and the list is empty. This masks
//         * the issue so that it doesn't cause an FC. See Issue #66.
//         */
//        try {
//            super.dispatchDraw(canvas);
//        } catch (IndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        /**
//         * This is a bit hacky, but Samsung's ListView has got a bug in it
//         * when using Header/Footer Views and the list is empty. This masks
//         * the issue so that it doesn't cause an FC. See Issue #66.
//         */
//        try {
//            return super.dispatchTouchEvent(ev);
//        } catch (IndexOutOfBoundsException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    @Override
//    public void setAdapter(ListAdapter adapter) {
//        // Add the Footer View at the last possible moment
//        if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
//            addFooterView(mLvFooterLoadingFrame, null, false);
//            mAddedLvFooter = true;
//        }
//
//        super.setAdapter(adapter);
//    }
//
//    @Override
//    public void setEmptyView(View emptyView) {
//        PullToRefreshListView.this.setEmptyView(emptyView);
//    }
//
//    @Override
//    public void setEmptyViewInternal(View emptyView) {
//        super.setEmptyView(emptyView);
//    }
//
//}