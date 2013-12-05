package com.swordy.library.android.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class FlingDetecer
{
    private static final String TAG = "AndroidLibrary.FlingDetecer";
    
    public static final int SINGLE_TAP = 0;
    
    public static final int DOUBLE_TAP = 1;
    
    public static final int FLING_UP = 2;
    
    public static final int FLING_DOWN = 3;
    
    public static final int FLING_LEFT = 4;
    
    public static final int FLING_RIGHT = 5;
    
    public interface OnFlingListener
    {
        void onFling(int direction/*, float velocity*/);
    }
    
    private OnFlingListener mFlingListener;
    
    private float mLastMotionY;
    
    private float mLastMotionX;
    
    private int mTouchSlopSquare;
    
    private int mDoubleTapSlopSquare;
    
    private int mMinimumFlingVelocity;
    
    private int mMaximumFlingVelocity;
    
    public FlingDetecer(Context context, OnFlingListener listener)
    {
        mFlingListener = listener;
        init(context, true);
    }
    
    private void init(Context context, boolean ignoreMultitouch)
    {
        if (mFlingListener == null)
        {
            throw new NullPointerException("OnFlingListener must not be null");
        }
        // Fallback to support pre-donuts releases
        int touchSlop, doubleTapSlop;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        doubleTapSlop = configuration.getScaledDoubleTapSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mTouchSlopSquare = touchSlop * touchSlop;
        mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }
    
    private int GESTURES_CURSOR_DISTANCE = 80;
    
    public void onFlingChanged(int derection)
    {
        
    }
    
    public void detect(MotionEvent event)
    {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                
                break;
            case MotionEvent.ACTION_MOVE:
                final float scrollX = mLastMotionX - x;
                final float scrollY = mLastMotionY - y;
                final float distanceX = Math.abs(scrollX);
                final float distanceY = Math.abs(scrollY);
                
                if (distanceX >= GESTURES_CURSOR_DISTANCE || distanceY >= GESTURES_CURSOR_DISTANCE)
                {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    
                    if (distanceY > distanceX)
                    {
                        if (scrollY < 0)
                        {
                            onFlingChanged(FLING_UP);
                        }
                        else
                        {
                            onFlingChanged(FLING_DOWN);
                        }
                    }
                    else
                    {
                        if (scrollX < 0)
                        {
                            onFlingChanged(FLING_LEFT);
                        }
                        else
                        {
                            onFlingChanged(FLING_RIGHT);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                
                break;
        }
        
    }
    
    private Handler mHandler = new Handler()
    {
        
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    
                    break;
                
                default:
                    break;
            }
        }
        
    };
}
