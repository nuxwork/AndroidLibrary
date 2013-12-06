package com.swordy.library.android.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.swordy.library.android.util.AndroidUnit;

public class FlingDetector
{
    private static final String TAG = "AndroidLibrary.FlingDetector";
    
    public interface OnFlingListener
    {
        boolean onDown(float x, float y);
        
        boolean onUp(float x, float y);
        
        boolean onPress(float x, float y);
        
        boolean onMove(float x, float y);
        
        boolean onClick(float x, float y);
        
        boolean onFling(int direction);
        
        boolean onSerialFling(int direction);
    }
    
    public static final int DIRECTION_UNKNOW = 0;
    
    public static final int DIRECTION_UP = 1;
    
    public static final int DIRECTION_DOWN = 2;
    
    public static final int DIRECTION_LEFT = 3;
    
    public static final int DIRECTION_RIGHT = 4;
    
    private static final int MSG_PRESS = 0;
    
    private static final int PRESS_SAFE_MOVE_RANGE = 30;
    
    private static final int CAN_PRESS_TIMEOUT = 135;
    
    private static final int FLAG_CAN_PRESS = 0x00000001;
    
    private static final int FLAG_MOVED = 0x00000002;
    
    private static final int FLAG_PRESSED = 0x00000004;
    
    private int mFlags = 0;
    
    private int mGestureCursorDistance = 80;
    
    private float mLastMotionY;
    
    private float mLastMotionX;
    
    private float mFirstMotionY;
    
    private float mFirstMotionX;
    
    private int mTouchSlopSquare;
    
    private int mDoubleTapSlopSquare;
    
    private int mMinimumFlingVelocity;
    
    private int mMaximumFlingVelocity;
    
    private OnFlingListener mFlingListener;
    
    private boolean mHasFling;
    
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
    
    public boolean detect(MotionEvent event)
    {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mLastMotionX = x;
                mLastMotionY = y;
                mFlags |= FLAG_CAN_PRESS;
                mFlags &= ~FLAG_PRESSED;
                mFlags &= ~FLAG_MOVED;
                boolean isHandled = false;
                isHandled |= onDown(x, y);
                mHandler.sendEmptyMessageDelayed(MSG_PRESS, CAN_PRESS_TIMEOUT);
                return isHandled;
            }
            case MotionEvent.ACTION_MOVE:
            {
                final float scrollX = mLastMotionX - x;
                final float scrollY = mLastMotionY - y;
                final float distanceX = AndroidUnit.px2dip(Math.abs(scrollX));
                final float distanceY = AndroidUnit.px2dip(Math.abs(scrollY));
                boolean isHandled = false;
                
                if (distanceX >= PRESS_SAFE_MOVE_RANGE || distanceY >= PRESS_SAFE_MOVE_RANGE)
                {
                    mFlags &= ~FLAG_CAN_PRESS;
                    mHandler.removeMessages(MSG_PRESS);
                }
                
                if ((mFlags & FLAG_CAN_PRESS) != FLAG_CAN_PRESS)
                {
                    if ((mFlags & FLAG_MOVED) != FLAG_MOVED)
                    {
                        mFirstMotionX = x;
                        mFirstMotionY = y;
                    }
                    
                    mFlags |= FLAG_MOVED;
                    isHandled |= onMove(x, y);
                }
                
                if (distanceX >= mGestureCursorDistance || distanceY >= mGestureCursorDistance)
                {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    
                    int direction = getDirection(scrollX, scrollY, distanceX, distanceY);
                    onSerialFling(direction);
                }
                return isHandled;
            }
            case MotionEvent.ACTION_UP:
            {
                mHandler.removeMessages(MSG_PRESS);
                
                boolean isHandled = false;
                
                if ((mFlags & FLAG_CAN_PRESS) == FLAG_CAN_PRESS)
                {
                    // can press but not pressed
                    if ((mFlags & FLAG_PRESSED) != FLAG_PRESSED)
                    {
                        isHandled |= onPress(x, y);
                    }
                }
                
                isHandled |= onUp(x, y);
                
                if ((mFlags & FLAG_MOVED) == FLAG_MOVED)
                {
                    final float scrollX = mFirstMotionX - x;
                    final float scrollY = mFirstMotionY - y;
                    final float distanceX = AndroidUnit.px2dip(Math.abs(scrollX));
                    final float distanceY = AndroidUnit.px2dip(Math.abs(scrollY));
                    int direction = getDirection(scrollX, scrollY, distanceX, distanceY);
                    isHandled |= onFling(direction);
                }
                
                if ((mFlags & FLAG_PRESSED) == FLAG_PRESSED)
                {
                    isHandled |= onClick(x, y);
                }
                return isHandled;
            }
        }
        return false;
    }
    
    private static int getDirection(float scrollX, float scrollY, float distanceX, float distanceY)
    {
        if (distanceY > distanceX)
        {
            if (scrollY > 0)
            {
                Log.v(TAG, "fling: DIRECTION_UP");
                return DIRECTION_UP;
            }
            else
            {
                Log.v(TAG, "fling: DIRECTION_DOWN");
                return DIRECTION_DOWN;
            }
        }
        else
        {
            if (scrollX > 0)
            {
                Log.v(TAG, "fling: DIRECTION_LEFT");
                return DIRECTION_LEFT;
            }
            else
            {
                Log.v(TAG, "fling: DIRECTION_RIGHT");
                return DIRECTION_RIGHT;
            }
        }
    }
    
    private Handler mHandler = new Handler()
    {
        
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_PRESS:
                    onPress(mLastMotionX, mLastMotionY);
                    break;
                default:
                    break;
            }
        }
        
    };
    
    boolean onDown(float x, float y)
    {
        Log.v(TAG, "~~~~~~~~~~~begin~~~~~~~~~~~~onDown");
        if (mFlingListener != null)
        {
            return mFlingListener.onDown(x, y);
        }
        return false;
    }
    
    boolean onUp(float x, float y)
    {
        Log.v(TAG, "~~~~~~~~~~~~~end~~~~~~~~~~~~~~onUp");
        if (mFlingListener != null)
        {
            return mFlingListener.onUp(x, y);
        }
        return false;
    }
    
    boolean onPress(float x, float y)
    {
        Log.v(TAG, "  $$$$$$   onPress");
        mFlags |= FLAG_PRESSED;
        if (mFlingListener != null)
        {
            return mFlingListener.onPress(x, y);
        }
        return false;
    }
    
    boolean onMove(float x, float y)
    {
        Log.v(TAG, "???????  onMove");
        if (mFlingListener != null)
        {
            return mFlingListener.onMove(x, y);
        }
        return false;
    }
    
    boolean onClick(float x, float y)
    {
        Log.v(TAG, "********  onClick");
        if (mFlingListener != null)
        {
            return mFlingListener.onClick(x, y);
        }
        return false;
    }
    
    /**
     * one up-down event can generate serial fling event
     * @param direction 
     * @return True if the event was handled, false otherwise.
     * @see {@link #DIRECTION_UP}
     * @see {@link #DIRECTION_DOWN}
     * @see {@link #DIRECTION_LEFT}
     * @see {@link #DIRECTION_RIGHT}
     */
    boolean onSerialFling(int direction)
    {
        Log.v(TAG, "------  onSerialFling");
        if (mFlingListener != null)
        {
            return mFlingListener.onSerialFling(direction);
        }
        return false;
    }
    
    /**
     * one up-down event can generate only one fling event
     * @param direction 
     * @return True if the event was handled, false otherwise.
     * @see {@link #DIRECTION_UP}
     * @see {@link #DIRECTION_DOWN}
     * @see {@link #DIRECTION_LEFT}
     * @see {@link #DIRECTION_RIGHT}
     */
    boolean onFling(int direction)
    {
        Log.v(TAG, "+++ onFling");
        if (mFlingListener != null)
        {
            return mFlingListener.onFling(direction);
        }
        return false;
    }
    
    public class SimpleOnFlingListener implements OnFlingListener
    {
        
        @Override
        public boolean onDown(float x, float y)
        {
            return false;
        }
        
        @Override
        public boolean onPress(float x, float y)
        {
            return false;
        }
        
        @Override
        public boolean onMove(float x, float y)
        {
            return false;
        }
        
        @Override
        public boolean onClick(float x, float y)
        {
            return false;
        }
        
        @Override
        public boolean onFling(int direction)
        {
            return false;
        }
        
        @Override
        public boolean onSerialFling(int direction)
        {
            return false;
        }
        
        @Override
        public boolean onUp(float x, float y)
        {
            return false;
        }
        
    }
}
