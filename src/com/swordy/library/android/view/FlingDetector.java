package com.swordy.library.android.view;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.swordy.library.android.util.AndroidUnit;

public class FlingDetector
{
    private static final String TAG = "AndroidLibrary.FlingDetector";
    
    public interface OnFlingListener
    {
        void onShowPress(float x, float y);
        
        void onSingleTap(float x, float y);
        
        void onFling(int direction/*, float velocity*/);
    }
    
    public static final int SHOW_PRESS = 0;
    
    public static final int SINGLE_TAP = 1;
    
    public static final int DOUBLE_TAP = 2;
    
    public static final int DIRECTION_UP = 3;
    
    public static final int DIRECTION_DOWN = 4;
    
    public static final int DIRECTION_LEFT = 5;
    
    public static final int DIRECTION_RIGHT = 6;
    
    private static final int SINGLE_TAP_TIMEOUT = 180;//ViewConfiguration.getTapTimeout();
    
    private static final int MSG_SINGLE_TAP = 0;
    
    private static final int MSG_DOUBLE_TAP = 1;
    
    private int GESTURES_CURSOR_DISTANCE = 80;
    
    private float mLastMotionY;
    
    private float mLastMotionX;
    
    private int mTouchSlopSquare;
    
    private int mDoubleTapSlopSquare;
    
    private int mMinimumFlingVelocity;
    
    private int mMaximumFlingVelocity;
    
    private OnFlingListener mFlingListener;
    
    private boolean mHasFling;
    
    private long mLastPressTime = System.currentTimeMillis();
    
    public FlingDetector(Context context, OnFlingListener listener)
    {
        mFlingListener = listener;
        AndroidUnit.init(context);
        init(context, true);
    }
    
    private void init(Context context, boolean ignoreMultitouch)
    {
        if (mFlingListener == null)
        {
            throw new NullPointerException("OnFlingListener must not be null");
        }
        int touchSlop, doubleTapSlop;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        doubleTapSlop = configuration.getScaledDoubleTapSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mTouchSlopSquare = touchSlop * touchSlop;
        mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }
    
    public void onShowPress(float x, float y)
    {
        Log.v(TAG, "onShowPress x: " + x + " ,y: " + y);
        mHasFling = false;
        if (mFlingListener != null)
            mFlingListener.onShowPress(x, y);
    }
    
    public void onSingleTap(float x, float y)
    {
        Log.v(TAG, "onSingleTap x: " + x + " ,y: " + y);
        mHasFling = false;
        if (mFlingListener != null)
        {
            mFlingListener.onSingleTap(x, y);
        }
    }
    
    public void onFling(int direction)
    {
        Log.v(TAG, "onFling: " + direction);
        mHasFling = true;
        if (mFlingListener != null)
            mFlingListener.onFling(direction);
    }
    
    public void detect(MotionEvent event)
    {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mLastPressTime = System.currentTimeMillis();
                mLastMotionX = x;
                mLastMotionY = y;
                onShowPress(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                final float scrollX = mLastMotionX - x;
                final float scrollY = mLastMotionY - y;
                final float distanceX = AndroidUnit.px2dip(Math.abs(scrollX));
                final float distanceY = AndroidUnit.px2dip(Math.abs(scrollY));
                
                if (distanceX >= GESTURES_CURSOR_DISTANCE || distanceY >= GESTURES_CURSOR_DISTANCE)
                {
                    Log.v(TAG, "distance px: " + scrollX + " dp: " + distanceX);
                    mLastMotionX = x;
                    mLastMotionY = y;
                    
                    if (distanceY > distanceX)
                    {
                        if (scrollY < 0)
                        {
                            onFling(DIRECTION_UP);
                        }
                        else
                        {
                            onFling(DIRECTION_DOWN);
                        }
                    }
                    else
                    {
                        if (scrollX < 0)
                        {
                            onFling(DIRECTION_LEFT);
                        }
                        else
                        {
                            onFling(DIRECTION_RIGHT);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                long current = System.currentTimeMillis();
                if (!mHasFling && current - mLastPressTime < SINGLE_TAP_TIMEOUT)
                {
                    onSingleTap(x, y);
                }
                break;
        }
        
    }
    
    /*    private Handler mHandler = new Handler()
        {
            
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    default:
                        break;
                }
            }
            
        };*/
}
