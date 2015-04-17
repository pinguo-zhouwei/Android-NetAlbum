package us.pinguo.album.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * Created by Mr 周先森 on 2015/4/17.
 */
public class TimeLineLayout extends LinearLayout {
    private static final String TAG = TimeLineLayout.class.getSimpleName();
    private Context mContext;
    private AddHeaderListener mListener;
    private View mHeaderView;

    private int mHeaderHeight = 150;
    /**
     * ，表示不把移动的事件传递给子控件,默认为false
     */
    private boolean mInterceptAllMoveEvents = false;

    /**
     * 默认不允许拦截（即，往子view传递事件），该属性只有在interceptAllMoveEvents为false的时候才有效
     */
    private boolean disallowIntercept = true;

    private float mDownY = 0;

    public TimeLineLayout(Context context) {
        super(context);
        init(context);
    }

    public TimeLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeLineLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        this.setOrientation(VERTICAL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener == null) {
            return;
        }

        mHeaderView = mListener.addHeaderView();
        this.removeView(mHeaderView);
        this.addView(mHeaderView, 0);

        // 计算HeadView尺寸
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        mHeaderView.measure(width, expandSpec);

        Log.d(TAG, "[onSizeChanged]w: " + w + ", h: " + h);
        Log.d(TAG, "[onSizeChanged]oldw: " + oldw + ", oldh: " + oldh);
        Log.d(TAG, "[onSizeChanged]child counts: " + this.getChildCount());
        mHeaderHeight = mHeaderView.getMeasuredHeight();

        changeViewHeight(mHeaderView, mHeaderHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int fromHeight = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                Log.d(TAG, "Down --> downY: " + mDownY);
                requestDisallowInterceptTouchEvent(true); // 保证事件可往下传递
                break;
            case MotionEvent.ACTION_MOVE:
                float curY = event.getY();
                float deltaY = curY - mDownY;
                // 是否是有效的往下拖动事件（则需要显示加载header）
                boolean isDropDownValidate = 0 != mDownY;
                /**
                 * 修改拦截设置
                 * 如果是有效往下拖动事件，则事件需要在本ViewGroup中处理，所以需要拦截不往子控件传递，即不允许拦截设为false
                 * 如果不是有效往下拖动事件，则事件传递给子控件处理，所以不需要拦截，并往子控件传递，即不允许拦截设为true
                 */
                requestDisallowInterceptTouchEvent(!isDropDownValidate);
                int toHeight = (int) Math.abs(deltaY);
                //  if(toHeight>0 && toHeight<150 ){
                changeViewHeight(mHeaderView, toHeight);
                startHeightAnimation(mHeaderView, fromHeight, (int) deltaY);
                fromHeight = (int) deltaY;
                mDownY = curY;
                // }
                break;

            case MotionEvent.ACTION_UP:
                requestDisallowInterceptTouchEvent(true); // 保证事件可往下传递
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mInterceptAllMoveEvents) {
            return !disallowIntercept;
        }
        // 如果设置了拦截所有move事件，即interceptAllMoveEvents为true
        if (MotionEvent.ACTION_MOVE == ev.getAction()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.disallowIntercept == disallowIntercept) {
            return;
        }
        this.disallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /**
     * 改变某控件的高度
     *
     * @param view
     * @param height
     */
    private void changeViewHeight(View view, int height) {
        Log.d(TAG, "[changeViewHeight]change Height: " + height);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = height;
        view.setLayoutParams(lp);
    }

    /**
     * 改变某控件的高度动画
     *
     * @param view
     * @param fromHeight
     * @param toHeight
     */
    private void startHeightAnimation(final View view, int fromHeight, int toHeight) {
        startHeightAnimation(view, fromHeight, toHeight, null);
    }

    private void startHeightAnimation(final View view, int fromHeight, int toHeight, Animator.AnimatorListener animatorListener) {
        if (toHeight == view.getMeasuredHeight()) {
            return;
        }
        ValueAnimator heightAnimator = ValueAnimator.ofInt(fromHeight, toHeight);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                if (null == value) return;
                changeViewHeight(view, value);
            }
        });
        if (null != animatorListener) {
            heightAnimator.addListener(animatorListener);
        }
        heightAnimator.setInterpolator(new LinearInterpolator());
        heightAnimator.setDuration(300/*ms*/);
        heightAnimator.start();
    }

    public void setListener(AddHeaderListener mListener) {
        this.mListener = mListener;
    }

    public void setInterceptAllMoveEvents(boolean mInterceptAllMoveEvents) {
        this.mInterceptAllMoveEvents = mInterceptAllMoveEvents;
    }

    public interface AddHeaderListener {
        /**
         * 获取headerView
         *
         * @return
         */
        public View addHeaderView();
    }
}
