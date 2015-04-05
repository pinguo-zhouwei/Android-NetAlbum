package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

/**
 * Created by huangwei on 15-1-5.
 */
public class BabyRefreshView extends View implements Runnable {

    private static final float SPEED_MIN = 1;
    private static final float SPEED_MAX = 16;

    private Bitmap mBitmap;

    private Matrix mMatrix;
    private Matrix mShadowMatrix;

    private Paint mPaint;
    private Paint mShadowPaint;
    private boolean mIsRunning;
    private float mDegree;
    private float mSpeed;
    private float mPx, mPy;

    private LinkedList<Float> mDegreeList = new LinkedList<Float>();
    private int mShadowNum = 3;

    private int mDirection = 1;
    private float mTransX, mTransY;


    public BabyRefreshView(Context context) {
        super(context);
        init();
    }

    public BabyRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BabyRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMatrix = new Matrix();
        mShadowMatrix = new Matrix();

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pull_refresh_icon);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setFilterBitmap(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBitmap != null) {
            int width = mBitmap.getHeight() * 2;
            int height = mBitmap.getHeight() + mBitmap.getWidth();
            setMeasuredDimension(width, height);
            mTransX = getMeasuredWidth() / 2f - mBitmap.getWidth() / 2f;
            mTransY = getMeasuredHeight() / 2f - mBitmap.getHeight() / 2f;
            mPx = width / 2f;
            mPy = mTransY;
            mMatrix.setTranslate(mTransX, mTransY);
            mMatrix.postRotate(mDegree, mPx, mPy);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        if (mIsRunning) {
            //残影
            drawShadow(canvas);
            if (mDegreeList.size() == 0) {
                mDegreeList.add(mDegree);
            } else if (Math.abs(mDegreeList.getLast() - mDegree) >= 20) {
                if (mDegreeList.size() < mShadowNum) {
                    mDegreeList.addLast(mDegree);
                } else if (mDegreeList.size() == mShadowNum) {
                    mDegreeList.remove(0);
                    mDegreeList.addLast(mDegree);
                }
            }
        }
    }

    private void drawShadow(Canvas canvas) {
        int size = mDegreeList.size();
        for (int i = 0; i < size; i++) {
            mShadowMatrix.setTranslate(mTransX, mTransY);
            mShadowMatrix.postRotate(mDegreeList.get(i), mPx, mPy);
            mShadowPaint.setAlpha((int) ((i + 1) / (float) (size + 1) * 255));
            canvas.drawBitmap(mBitmap, mShadowMatrix, mShadowPaint);
        }
    }

    public void start() {
        if (mIsRunning) {
            return;
        }
        mIsRunning = true;
        mDegree = 0;
        mSpeed = getSpeed();
        mDegreeList.clear();
        post(this);
    }

    public void reset() {
        stop();
        mMatrix.reset();
        mDegree = 0;
        mMatrix.setTranslate(mTransX, mTransY);
        mMatrix.postRotate(mDegree, mPx, mPy);
        invalidate();
    }

    public void stop() {
        if (!mIsRunning) {
            return;
        }
        mIsRunning = false;
        removeCallbacks(this);
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mMatrix.reset();
//                invalidate();
//            }
//        },200);
    }

    @Override
    public void run() {
        if (!mIsRunning) {
            return;
        }

        mSpeed = getSpeed();
        mDegree += mSpeed;
        mMatrix.setTranslate(mTransX, mTransY);
        mMatrix.postRotate(mDegree, mPx, mPy);
        invalidate();
        postDelayed(this, 40);
    }

    private float getSpeed() {
        float speed = SPEED_MIN + (1 - Math.abs(mDegree) / 90f) * (SPEED_MAX - SPEED_MIN);
        if (mDegree > 75) {
            mDirection = -1;
        } else if (mDegree < -75) {
            mDirection = 1;
        }
        return speed * mDirection;
    }
}
