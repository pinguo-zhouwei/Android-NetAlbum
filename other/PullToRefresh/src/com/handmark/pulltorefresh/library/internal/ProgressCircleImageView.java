package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import us.pinguo.uilext.view.CircleImageView;

/**
 * Created by huangwei on 15-3-4.
 */
public class ProgressCircleImageView extends CircleImageView implements Runnable {

    public static final int STATE_DEFAULT = 0;
    public static final int STATE_PROGRESS = 1;
    public static final int STATE_CIRCLING = 2;

    private static final int CIRCUIT_DURATION = 900;

    private static final int START_DEGREE = 0;

    private int mState;

    private float mProgress;

    private RectF mDstRect = new RectF();
    private Paint mPaint;

    private int mStartDegree = START_DEGREE;
    private int mMaxSweepDegree = 360;

    private boolean mStop;
    private long mStartTime;

    private SweepGradient mSweepGradient;

    public ProgressCircleImageView(Context context) {
        super(context);
    }

    public ProgressCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void setState(int state) {
        mState = state;
        invalidate();
    }

    public int getState() {
        return mState;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getDrawable() != null) {
            Rect rect = getDrawable().getBounds();
            int borderWidth = getBorderWidth();
            mDstRect.set(rect.left + borderWidth, rect.top + borderWidth, rect.right - borderWidth, rect.bottom - borderWidth);
            SweepGradient sweepGradient = new SweepGradient(mDstRect.centerX(), mDstRect.centerY(), new int[]{0xFFA1A1A1, 0xFFFFFFFF, 0xFFA1A1A1},
                    new float[]{0f, 0.9f, 1f});
            mPaint.setShader(sweepGradient);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null || mDstRect == null) {
            return;
        }
        mPaint.setStrokeWidth(getBorderWidth());
//        SweepGradient gradient =  new SweepGradient(mDstRect.centerX(),mDstRect.centerY(), new int[] {
//                Color.RED,
//                0xFFFF6100,
//                Color.YELLOW,
//                Color.GREEN,
//                Color.CYAN,
//                Color.BLUE,
//                Color.MAGENTA}, null);
//        mPaint.setShader(gradient);
        if (mState == STATE_PROGRESS) {
            canvas.drawArc(mDstRect, mStartDegree, mMaxSweepDegree * mProgress, false, mPaint);
        } else if (mState == STATE_CIRCLING) {
            canvas.save();
            canvas.rotate(mStartDegree, mDstRect.centerX(), mDstRect.centerY());
            canvas.drawArc(mDstRect, mStartDegree, mMaxSweepDegree, false, mPaint);
            canvas.restore();
        }
    }

    public void start() {
        mStop = false;
        mStartTime = 0;
        mState = STATE_CIRCLING;
        post(this);
    }

    public void reset() {
        mStop = true;
    }

    @Override
    public void run() {
        if (mStartTime == 0) {
            mStartTime = System.currentTimeMillis();
        }

        long curTime = System.currentTimeMillis();
        //只有至少转完一圈后才能停止
        if (mStop && curTime - mStartTime >= CIRCUIT_DURATION) {
            mState = STATE_DEFAULT;
            mStartDegree = START_DEGREE;
            invalidate();
            return;
        }

        float rate = (curTime - mStartTime) % CIRCUIT_DURATION / (float) CIRCUIT_DURATION;
        mStartDegree = (int) (rate * 360);
        invalidate();
        postDelayed(this, 30);
    }

    @Override
    protected void onDetachedFromWindow() {
        mStop = true;
        removeCallbacks(this);
        reset();
        super.onDetachedFromWindow();
    }
}
