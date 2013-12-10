package com.swordy.library.android.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.swordy.library.android.util.AndroidUnit;

public class FlingDetector
{
    private static final String TAG = "AndroidLibrary.FlingDetector";
    
    public interface OnFlingListener
    {
        void onDown(View v, float x, float y);
        
        void onUp(View v, float x, float y);
        
        void onPress(View v, float x, float y);
        
        void onMove(View v, float x, float y);
        
        void onClick(View v, float x, float y);
        
        void onFling(View v, int direction);
        
        void onSerialFling(View v, int direction);
    }
    
    private static final int CAN_PRESS_TIMEOUT = 120;
    
    private static final int PRESSED_STATE_DURATION = 40;
    
    private static final int PRESS_SAFE_MOVE_RANGE = 15/*dp*/;
    
    public static final int DIRECTION_UNKNOW = 0;
    
    public static final int DIRECTION_UP = 1;
    
    public static final int DIRECTION_DOWN = 2;
    
    public static final int DIRECTION_LEFT = 3;
    
    public static final int DIRECTION_RIGHT = 4;
    
    private static final int MSG_PRESS = 0;
    
    private static final int MSG_CLICK = 1;
    
    private static final int FLAG_CAN_PRESS = 0x00000001;
    
    private static final int FLAG_MOVED = 0x00000002;
    
    private static final int FLAG_PRESSED = 0x00000004;
    
    private int mFlags = 0;
    
    private int mSerialFlingDistance = 80;
    
    private float mLastMotionY;
    
    private float mLastMotionX;
    
    private float mFirstMotionY;
    
    private float mFirstMotionX;
    
    private int mPressStateMoveRange;
    
    private OnFlingListener mFlingListener;
    
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
        mPressStateMoveRange = AndroidUnit.px2dip(PRESS_SAFE_MOVE_RANGE);
    }
    
    public boolean detect(View v, MotionEvent event)
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
                onDown(v, x, y);
                
                Message msg = new Message();
                msg.what = MSG_PRESS;
                msg.obj = v;
                mHandler.sendMessageDelayed(msg, CAN_PRESS_TIMEOUT);
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                final float scrollX = mLastMotionX - x;
                final float scrollY = mLastMotionY - y;
                final float distanceX = AndroidUnit.px2dip(Math.abs(scrollX));
                final float distanceY = AndroidUnit.px2dip(Math.abs(scrollY));
                
                if (distanceX >= mPressStateMoveRange || distanceY >= mPressStateMoveRange)
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
                    
                    onMove(v, x, y);
                    mFlags |= FLAG_MOVED;
                }
                
                if (distanceX >= mSerialFlingDistance || distanceY >= mSerialFlingDistance)
                {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    
                    int direction = getDirection(scrollX, scrollY, distanceX, distanceY);
                    onSerialFling(v, direction);
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                mHandler.removeMessages(MSG_PRESS);
                
                if ((mFlags & FLAG_CAN_PRESS) == FLAG_CAN_PRESS)
                {
                    // can press but not pressed
                    if ((mFlags & FLAG_PRESSED) != FLAG_PRESSED)
                    {
                        onPress(v, x, y);
                    }
                }
                
                onUp(v, x, y);
                
                if ((mFlags & FLAG_MOVED) == FLAG_MOVED)
                {
                    final float scrollX = mFirstMotionX - x;
                    final float scrollY = mFirstMotionY - y;
                    final float distanceX = AndroidUnit.px2dip(Math.abs(scrollX));
                    final float distanceY = AndroidUnit.px2dip(Math.abs(scrollY));
                    int direction = getDirection(scrollX, scrollY, distanceX, distanceY);
                    onFling(v, direction);
                }
                
                if ((mFlags & FLAG_PRESSED) == FLAG_PRESSED)
                {
                    onClick(v, x, y);
                }
                return true;
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
                    onPress((View)msg.obj, mLastMotionX, mLastMotionY);
                    break;
                case MSG_CLICK:
                    onClick((View)msg.obj, mLastMotionX, mLastMotionY);
                    break;
                default:
                    break;
            }
        }
        
    };
    
    void onDown(View v, float x, float y)
    {
        Log.v(TAG, "~~~~~~~~~~~begin~~~~~~~~~~~~onDown");
        if (mFlingListener != null)
        {
            mFlingListener.onDown(v, x, y);
        }
    }
    
    void onUp(View v, float x, float y)
    {
        Log.v(TAG, "~~~~~~~~~~~~~end~~~~~~~~~~~~~~onUp");
        if (mFlingListener != null)
        {
            mFlingListener.onUp(v, x, y);
        }
    }
    
    private long mPressedTime;
    
    void onPress(View v, float x, float y)
    {
        Log.v(TAG, "  $$$$$$   onPress");
        mPressedTime = System.currentTimeMillis();
        mFlags |= FLAG_PRESSED;
        if (mFlingListener != null)
        {
            mFlingListener.onPress(v, x, y);
        }
    }
    
    void onMove(View v, float x, float y)
    {
        Log.v(TAG, "???????  onMove");
        if (mFlingListener != null)
        {
            mFlingListener.onMove(v, x, y);
        }
    }
    
    void onClick(View v, float x, float y)
    {
        long now = System.currentTimeMillis();
        long diff = now - mPressedTime;
        if (diff < PRESSED_STATE_DURATION)
        {
            Message msg = new Message();
            msg.what = MSG_CLICK;
            msg.obj = v;
            mHandler.sendMessageDelayed(msg, PRESSED_STATE_DURATION - diff);
            return;
        }
        
        Log.v(TAG, "********  onClick");
        if (mFlingListener != null)
        {
            mFlingListener.onClick(v, x, y);
        }
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
    void onSerialFling(View v, int direction)
    {
        Log.v(TAG, "------  onSerialFling");
        if (mFlingListener != null)
        {
            mFlingListener.onSerialFling(v, direction);
        }
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
    void onFling(View v, int direction)
    {
        Log.v(TAG, "+++ onFling");
        if (mFlingListener != null)
        {
            mFlingListener.onFling(v, direction);
        }
    }
    
    public static class SimpleOnFlingListener implements OnFlingListener
    {
        
        @Override
        public void onDown(View v, float x, float y)
        {
            return;
        }
        
        @Override
        public void onPress(View v, float x, float y)
        {
            return;
        }
        
        @Override
        public void onMove(View v, float x, float y)
        {
            return;
        }
        
        @Override
        public void onClick(View v, float x, float y)
        {
            return;
        }
        
        @Override
        public void onFling(View v, int direction)
        {
            return;
        }
        
        @Override
        public void onSerialFling(View v, int direction)
        {
            return;
        }
        
        @Override
        public void onUp(View v, float x, float y)
        {
            return;
        }
        
    }
}
