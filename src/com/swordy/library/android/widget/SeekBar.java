package com.swordy.library.android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.swordy.library.android.R;

public class SeekBar extends View
{
    
    private static final String TAG = "SeekBar";
    
    public static final int HORIZONTAL = 0;
    
    public static final int VERTICAL = 1;
    
    private Context mContext;
    
    private Drawable mProgressDrawable;
    
    private Drawable mThumb;
    
    private int mProgressHeight;
    
    private int mThumbWidth;
    
    private int mThumbHeight;
    
    private int mThumbOffset;
    
    private int mAngle;
    
    private int mOrientation;
    
    private int mMax;
    
    private int mProgress;
    
    private Rect mContentRect;
    
    private Rect mProgressRect;
    
    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    
    public SeekBar(Context context)
    {
        this(context, null);
    }
    
    public SeekBar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    
    public SeekBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        onCreate(context, attrs, defStyle);
    }
    
    protected void onCreate(Context context, AttributeSet attrs, int defStyle)
    {
        mContext = context;
        
        mContentRect = new Rect();
        mProgressRect = new Rect();
        setFocusable(true);
        setClickable(true);
        
        if (attrs == null)
        {
            mMax = 100;
            mProgress = 0;
            mOrientation = HORIZONTAL;
            mProgressHeight = LayoutParams.WRAP_CONTENT;
            mThumbWidth = LayoutParams.WRAP_CONTENT;
            mThumbHeight = LayoutParams.WRAP_CONTENT;
            return;
        }
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBar, defStyle, 0);
        mProgress = a.getInt(R.styleable.SeekBar_progress, 0);
        mMax = a.getInt(R.styleable.SeekBar_max, 100);
        mOrientation = a.getInt(R.styleable.SeekBar_orientation, HORIZONTAL);
        
        mProgressDrawable = a.getDrawable(R.styleable.SeekBar_progressDrawable);
        mProgressHeight = a.getDimensionPixelSize(R.styleable.SeekBar_progressHeight, LayoutParams.WRAP_CONTENT);
        mThumb = a.getDrawable(R.styleable.SeekBar_thumb);
        mThumbWidth = a.getDimensionPixelSize(R.styleable.SeekBar_thumbWidth, LayoutParams.WRAP_CONTENT);
        mThumbHeight = a.getDimensionPixelSize(R.styleable.SeekBar_thumbHeight, LayoutParams.WRAP_CONTENT);
        mThumbOffset = a.getDimensionPixelSize(R.styleable.SeekBar_thumbOffset, 0);
        a.recycle();
    }
    
    public int getAngle()
    {
        return mAngle;
    }
    
    public void setAngle(int angle)
    {
        if (mAngle == angle)
            return;
        
        mAngle = angle;
    }
    
    public Drawable getProgressDrawable()
    {
        return mProgressDrawable;
    }
    
    public void setProgressDrawable(Drawable progressDrawable)
    {
        if (mProgressDrawable == progressDrawable)
            return;
        
        Drawable oldDrawable = mProgressDrawable;
        Drawable newDrawable = progressDrawable;
        mProgressDrawable = progressDrawable;
        
        if (oldDrawable.getIntrinsicWidth() != newDrawable.getIntrinsicWidth()
            || oldDrawable.getIntrinsicHeight() != newDrawable.getIntrinsicHeight())
        {
            requestLayout();
        }
        else
        {
            invalidate();
        }
    }
    
    public void setProgressDrawable(int ResId)
    {
        setProgressDrawable(mContext.getResources().getDrawable(ResId));
    }
    
    public Drawable getThumb()
    {
        return mThumb;
    }
    
    public void setThumb(Drawable thumb)
    {
        if (mThumb == thumb)
            return;
        
        Drawable oldDrawable = mThumb;
        Drawable newDrawable = thumb;
        mThumb = thumb;
        
        if (oldDrawable.getIntrinsicWidth() != newDrawable.getIntrinsicWidth()
            || oldDrawable.getIntrinsicHeight() != newDrawable.getIntrinsicHeight())
        {
            requestLayout();
        }
        else
        {
            invalidate();
        }
    }
    
    public void setThumb(int resId)
    {
        setThumb(mContext.getResources().getDrawable(resId));
    }
    
    public int getMax()
    {
        return mMax;
    }
    
    public void setMax(int max)
    {
        if (mMax == max)
            return;
        
        mMax = max;
    }
    
    public int getProgress()
    {
        return mProgress;
    }
    
    public void setProgress(int progress)
    {
        setProgress(progress, false);
    }
    
    private void setProgress(int progress, boolean fromUser)
    {
        
        Log.v(TAG, "progress: " + progress);
        if (progress < 0)
            progress = 0;
        else if (progress > mMax)
            progress = mMax;
        
        if (mProgress == progress)
            return;
        
        mProgress = progress;
        invalidate();
        
        if (mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onProgressChanged(this, mProgress, fromUser);
    }
    
    public OnSeekBarChangeListener getOnSeekBarChangeListener()
    {
        return mOnSeekBarChangeListener;
    }
    
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener)
    {
        if (mOnSeekBarChangeListener == onSeekBarChangeListener)
            return;
        
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }
    
    @Override
    protected void drawableStateChanged()
    {
        super.drawableStateChanged();
        
        if (mThumb != null && mThumb.isStateful())
        {
            mThumb.setState(getDrawableState());
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return procVerticalTouchEvent(event);
    }
    
    public boolean procHorizontalTouchEvent(MotionEvent event)
    {
        return false;
    }
    
    public boolean procVerticalTouchEvent(MotionEvent event)
    {
        float dis = 0f;
        int progress = 0;
        
        if (mOrientation == VERTICAL)
        {
            float y = event.getY();
            dis = y;
            
            if (y < mProgressRect.top)
                dis = 0;
            else if (y > mProgressRect.bottom)
                dis = mProgressRect.height();
            else
                dis = y - mProgressRect.top;
            dis = mProgressRect.height() - dis;
            progress = (int)(dis / mProgressRect.height() * mMax);
            
        }
        else
        {
            float x = event.getX();
            dis = x;
            
            if (x < mProgressRect.left)
                dis = 0;
            else if (x > mProgressRect.right)
                dis = mProgressRect.width();
            else
                dis = x - mProgressRect.left;
            progress = (int)(dis / mProgressRect.width() * mMax);
        }
        
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                setProgress(progress, true);
                if (mOnSeekBarChangeListener != null)
                    mOnSeekBarChangeListener.onStartTrackingTouch(this);
                return true;
            case MotionEvent.ACTION_MOVE:
                setProgress(progress, true);
                
                return true;
            case MotionEvent.ACTION_UP:
                setPressed(false);
                setProgress(progress, true);
                if (mOnSeekBarChangeListener != null)
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                return true;
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                setProgress(progress, true);
                if (mOnSeekBarChangeListener != null)
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                return true;
        }
        return false;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Log.v(TAG, "onMeasure");
        
        int width = 0, height = 0;
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        Drawable[] drawables = {mProgressDrawable, mThumb};
        
        if (widthMode != MeasureSpec.UNSPECIFIED)
        {
            width = maxSize(drawables).width;
        }
        
        if (heightMode != MeasureSpec.UNSPECIFIED)
        {
            height = maxSize(drawables).height;
        }
        
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        }
        
        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        }
        
        measureDrawables(width, height);
        
        setMeasuredDimension(width, height);
        
        getDrawingRect(mContentRect);
        mContentRect.left += getPaddingLeft();
        mContentRect.top += getPaddingTop();
        mContentRect.right -= getPaddingRight();
        mContentRect.bottom -= getPaddingBottom();
        
        mProgressRect.set(mContentRect);
        if (mOrientation == VERTICAL)
        {
            mProgressRect.top += mThumbOffset;
            mProgressRect.bottom -= mThumbOffset;
        }
        else
        {
            mProgressRect.left += mThumbOffset;
            mProgressRect.right -= mThumbOffset;
        }
        
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        Log.v(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);
    }
    
    private void measureDrawables(int width, int height)
    {
        if (mThumb != null)
        {
            if (mThumbWidth == LayoutParams.MATCH_PARENT)
                mThumbWidth = width;
            else if (mThumbWidth == LayoutParams.WRAP_CONTENT)
                mThumbWidth = mThumb.getIntrinsicWidth();
            
            if (mThumbHeight == LayoutParams.MATCH_PARENT)
                mThumbHeight = height;
            else if (mThumbHeight == LayoutParams.WRAP_CONTENT)
                mThumbHeight = mThumb.getIntrinsicHeight();
        }
        
        if (mProgressHeight == LayoutParams.MATCH_PARENT)
        {
            mProgressHeight = mOrientation == VERTICAL ? width : height;
        }
        else if (mProgressHeight == LayoutParams.WRAP_CONTENT)
        {
            Size size = maxSize(new Drawable[] {mProgressDrawable});
            mProgressHeight = mOrientation == VERTICAL ? size.width : size.height;
        }
    }
    
    @Override
    protected void onConfigurationChanged(Configuration newConfig)
    {
        Log.v(TAG, "onConfigurationChanged");
        invalidate();
    }
    
    @Override
    public synchronized void onDraw(Canvas canvas)
    {
        Log.v(TAG, "onDraw");
        Drawable background = getBackground();
        if (background != null)
        {
            background.setBounds(0, 0, getWidth(), getHeight());
            background.draw(canvas);
        }
        
        if (mOrientation == VERTICAL)
            drawVertical(canvas);
        else
            drawHorizental(canvas);
    }
    
    private void drawHorizental(Canvas canvas)
    {
        final Rect rect = mProgressRect;
        int centerY = rect.centerY();
        int halfSize = 0;
        int halfThumbHeight = mThumbHeight / 2;
        int halfThumbWidth = mThumbWidth / 2;
        int progressSpec = (int)((float)mProgress / mMax * rect.width());
        
        if (mProgressDrawable instanceof LayerDrawable)
        {
            Drawable background = ((LayerDrawable)mProgressDrawable).findDrawableByLayerId(android.R.id.background);
            Drawable progress = ((LayerDrawable)mProgressDrawable).findDrawableByLayerId(android.R.id.progress);
            // Drawable secondaryProgress =
            // ((LayerDrawable)mProgressDrawable).findDrawableByLayerId(android.R.id.secondaryProgress);
            
            if (background != null)
            {
                halfSize = mProgressHeight / 2;
                background.setBounds(rect.left, centerY - halfSize, rect.right, centerY + halfSize);
                background.draw(canvas);
            }
            
            if (progress != null)
            {
                halfSize = mProgressHeight / 2;
                progress.setBounds(rect.left, centerY - halfSize, rect.left + progressSpec, centerY + halfSize);
                progress.draw(canvas);
            }
        }
        
        if (mThumb != null)
        {
            halfSize = halfThumbHeight;
            int offset = rect.left + progressSpec;
            mThumb.setBounds(offset - halfThumbWidth, centerY - halfSize, offset + halfThumbWidth, centerY + halfSize);
            mThumb.draw(canvas);
        }
    }
    
    private void drawVertical(Canvas canvas)
    {
        final Rect rect = mProgressRect;
        int centerX = rect.centerX();
        int halfSize = 0;
        int halfThumbHeight = mThumbHeight / 2;
        int halfThumbWidth = mThumbWidth / 2;
        int progressSpec = (int)((float)mProgress / mMax * rect.height());
        
        if (mProgressDrawable instanceof LayerDrawable)
        {
            Drawable background = ((LayerDrawable)mProgressDrawable).findDrawableByLayerId(android.R.id.background);
            Drawable progress = ((LayerDrawable)mProgressDrawable).findDrawableByLayerId(android.R.id.progress);
            // Drawable secondaryProgress =
            // ((LayerDrawable)mProgressDrawable).findDrawableByLayerId(android.R.id.secondaryProgress);
            
            if (background != null)
            {
                halfSize = mProgressHeight / 2;
                background.setBounds(centerX - halfSize, rect.top, centerX + halfSize, rect.bottom);
                background.draw(canvas);
            }
            
            if (progress != null)
            {
                halfSize = mProgressHeight / 2;
                progress.setBounds(centerX - halfSize, rect.bottom - progressSpec, centerX + halfSize, rect.bottom);
                progress.draw(canvas);
            }
        }
        
        if (mThumb != null)
        {
            halfSize = halfThumbWidth;
            int offset = rect.bottom - progressSpec;
            mThumb.setBounds(centerX - halfSize, offset - halfThumbHeight, centerX + halfSize, offset + halfThumbHeight);
            mThumb.draw(canvas);
        }
    }
    
    private Size maxSize(Drawable[] drawables)
    {
        Size max = new Size();
        for (Drawable d : drawables)
        {
            if (d != null)
            {
                if (d.getIntrinsicHeight() > max.height)
                    max.height = d.getIntrinsicHeight();
                
                if (d.getIntrinsicWidth() > max.width)
                    max.width = d.getIntrinsicWidth();
            }
        }
        return max;
    }
    
    private class Size
    {
        public int width;
        
        public int height;
        
        public Size()
        {
            width = 0;
            height = 0;
        }
        
        public Size(int width, int height)
        {
            this.width = width;
            this.height = height;
        }
    }
    
    /**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were
     * initiated programmatically.
     */
    public interface OnSeekBarChangeListener
    {
        
        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         * 
         * @param seekBar
         *            The SeekBar whose progress has changed
         * @param progress
         *            The current progress level. This will be in the range
         *            0..max where max was set by
         *            {@link ProgressBar#setMax(int)}. (The default value for
         *            max is 100.)
         * @param fromUser
         *            True if the progress change was initiated by the user.
         */
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
        
        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         * 
         * @param seekBar
         *            The SeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(SeekBar seekBar);
        
        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the seekbar.
         * 
         * @param seekBar
         *            The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(SeekBar seekBar);
    }
    
}
