package com.swordy.library.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class Switcher extends View
{
    private static final String TAG = "Switcher";
    
    private Context mContext;
    
    //---------- text ------------
    private String mTextOn;
    
    private String mTextOff;
    
    private Paint mTextPaint;
    
    private int mTextSize;
    
    private int mTextTop;
    
    private int mTextOnLeft;
    
    private int mTextOffLeft;
    
    private int mCheckedTextColor;
    
    private int mUnCheckedTextColor;
    
    //-------- thumb -------
    
    private Drawable mThumb;
    
    private int mThumbId;
    
    private int mThumbWidthMeasureSpec;
    
    private int mThumbHeightMeasureSpec;
    
    private int mThumbWidth;
    
    private int mThumbHeight;
    
    private int mPreTouchX;
    
    private int mThumbOffset;
    
    //------ background -------
    
    private int mDrawableWidth;
    
    private int mDrawableHeight;
    
    private boolean mChecked;
    
    //---------- listener ----------
    private OnCheckedChangeListener mOnCheckedChangeListener;
    
    public Switcher(Context context)
    {
        this(context, null);
    }
    
    public Switcher(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    
    public Switcher(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        onCreate(context, attrs, defStyle);
    }
    
    protected void onCreate(Context context, AttributeSet attrs, int defStyle)
    {
        // TODO init variables
        mContext = context;
        
        mThumbOffset = 0;
        mThumbWidth = -1;
        mThumbHeight = -1;
        mThumbWidthMeasureSpec = LayoutParams.WRAP_CONTENT;
        mThumbHeightMeasureSpec = LayoutParams.WRAP_CONTENT;
        
        mCheckedTextColor = 0xffeeeeee;
        mUnCheckedTextColor = 0xff666666;
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        
        setFocusable(true);
        
        if (attrs == null)
            return;
        
        TypedArray a = null;// context.obtainStyledAttributes(attrs, attrs,
                            // defStyleAttr, defStyleRes)
        // TODO init from attrs
    }
    
    public boolean isChecked()
    {
        return mChecked;
    }
    
    public void setChecked(boolean isChecked)
    {
        if (mChecked == isChecked)
            return;
        
        updateCheckState(isChecked, false);
    }
    
    private void updateCheckState(boolean isChecked, boolean redraw)
    {
        if (redraw || mChecked != isChecked)
        {
            int offset = 0;
            
            if (isChecked)
                offset = mDrawableWidth - mThumbWidth;
            
            if (mThumbOffset != offset)
            {
                mThumbOffset = offset;
                invalidate();
            }
        }
        
        if (mChecked == isChecked)
            return;
        
        mChecked = isChecked;
        
        if (mOnCheckedChangeListener != null)
            mOnCheckedChangeListener.onCheckedChanged(this, isChecked);
    }
    
    public OnCheckedChangeListener getOnCheckedChangeListener()
    {
        return mOnCheckedChangeListener;
    }
    
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener)
    {
        if (mOnCheckedChangeListener == listener)
            return;
        
        mOnCheckedChangeListener = listener;
        
        boolean clickable = !(listener == null);
        setClickable(clickable);
    }
    
    public String getTextOn()
    {
        return mTextOn;
    }
    
    public void setTextOn(String textOn)
    {
        if (mTextOn == textOn)
            return;
        
        if (textOn != null && textOn.equals(mTextOn))
            return;
        
        mTextOn = textOn;
        
        requestLayout();
    }
    
    public String getTextOff()
    {
        return mTextOff;
    }
    
    public void setTextOff(String textOff)
    {
        if (mTextOff == textOff)
            return;
        
        if (textOff != null && textOff.equals(mTextOff))
            return;
        
        mTextOff = textOff;
        
        requestLayout();
    }
    
    public int getTextSize()
    {
        return mTextSize;
    }
    
    public void setTextSize(int textSize)
    {
        if (mTextSize == textSize)
            return;
        
        mTextSize = textSize;
        mTextPaint.setTextSize(textSize);
        
        requestLayout();
    }
    
    /**
     * @param state
     *            is switcher checked or not
     * @param color
     *            text color
     */
    public void setTextColor(boolean state, int color)
    {
        if (state)
        {
            if (mCheckedTextColor == color)
                return;
            
            mCheckedTextColor = color;
        }
        else
        {
            if (mUnCheckedTextColor == color)
                return;
            
            mUnCheckedTextColor = color;
        }
        
        invalidate();
    }
    
    public int getTextColor(boolean state)
    {
        if (state)
            return mCheckedTextColor;
        else
            return mUnCheckedTextColor;
        
    }
    
    public Drawable getThumb()
    {
        return mThumb;
    }
    
    /**
     * 
     * @param thumb
     */
    public void setThumb(Drawable thumb)
    {
        if (mThumb == thumb)
        {
            return;
        }
        
        if (mThumb != null)
        {
            mThumb.setCallback(null);
            unscheduleDrawable(mThumb);
        }
        
        mThumb = thumb;
        
        if (thumb != null)
        {
            thumb.setCallback(this);
            if (thumb.isStateful())
            {
                thumb.setState(getDrawableState());
            }
        }
        
        requestLayout();
    }
    
    /**
     * 
     * @param resId
     * @see #setThumb(Drawable thumb)
     */
    public void setThumbResources(int resId)
    {
        if (mThumbId == resId)
            return;
        
        mThumbId = resId;
        setThumb(mContext.getResources().getDrawable(resId));
    }
    
    public void setThumbSize(int width, int height)
    {
        mThumbWidthMeasureSpec = width;
        mThumbHeightMeasureSpec = height;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mPreTouchX = (int)event.getX();
                
                if (mPreTouchX < mThumbOffset || mPreTouchX > mThumbWidth + mThumbOffset)
                {
                    mPreTouchX = -1;
                    
                    updateCheckState(!mChecked, true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPreTouchX == -1)
                    break;
                
                int x = (int)event.getX();
                
                int offset = x - mPreTouchX + mThumbOffset;
                mPreTouchX = x;
                if (offset > 0 && offset < mDrawableWidth - mThumbWidth)
                {
                    mThumbOffset = offset;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mThumbOffset + mThumbWidth / 2f <= mDrawableWidth / 2f)
                {
                    updateCheckState(false, true);
                }
                else
                {
                    updateCheckState(true, true);
                }
                break;
        }
        
        return super.onTouchEvent(event);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        measureThumbSize(mThumbWidthMeasureSpec, mThumbHeightMeasureSpec);
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        
        FontMetrics fm = mTextPaint.getFontMetrics();
        int textHeight = (int)Math.ceil(fm.descent - fm.ascent);
        int textOnWidth = mTextOn == null ? -1 : (int)mTextPaint.measureText(mTextOn);
        int textOffWidth = mTextOff == null ? -1 : (int)mTextPaint.measureText(mTextOff);
        
        Drawable background = getBackground();
        if (widthMode != MeasureSpec.EXACTLY)
        {
            int bgWidth = background.getIntrinsicWidth();
            width = Math.max(bgWidth, mThumbWidth);
            width = Math.max(width, (textOnWidth + textOffWidth));
        }
        
        if (heightMode != MeasureSpec.EXACTLY)
        {
            int bgHeight = background.getIntrinsicHeight();
            height = Math.max(bgHeight, mThumbHeight);
            height = Math.max(height, textHeight);
        }
        
        mDrawableWidth = width;
        mDrawableHeight = height;
        
        mTextTop = (int)((height + mTextSize) / 2f);
        mTextOffLeft = (int)((mThumbWidth - textOffWidth) / 2f);
        mTextOnLeft = (int)((mThumbWidth - textOnWidth) / 2f) + width - mThumbWidth;
        
        Log.v(TAG, "text top: " + mTextTop + " textHeight: " + textHeight + " height: " + height);
        
        setMeasuredDimension(width, height);
    }
    
    private void measureThumbSize(int width, int height)
    {
        //------ width ------
        if (width == LayoutParams.WRAP_CONTENT)
        {
            if (mThumb != null)
                mThumbWidth = mThumb.getIntrinsicWidth();
        }
        else if (width == LayoutParams.MATCH_PARENT)
        {
            mThumbWidth = mDrawableWidth;
        }
        else
        {
            mThumbWidth = width;
        }
        
        //------ height ------
        if (height == LayoutParams.WRAP_CONTENT)
        {
            if (mThumb != null)
                mThumbHeight = mThumb.getIntrinsicHeight();
        }
        else if (height == LayoutParams.MATCH_PARENT)
        {
            mThumbHeight = mDrawableHeight;
        }
        else
        {
            mThumbHeight = height;
        }
    }
    
    @Override
    public void draw(Canvas canvas)
    {
        Drawable background = getBackground();
        if (background != null)
        {
            background.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
            background.draw(canvas);
        }
        
        if (mThumb != null)
        {
            mThumb.setBounds(mThumbOffset, 0, mThumbWidth + mThumbOffset, mThumbHeight);
            mThumb.draw(canvas);
        }
        
        int textColor = 0;
        // left
        if (mTextOff != null && !mTextOff.equals(""))
        {
            textColor = !mChecked ? mCheckedTextColor : mUnCheckedTextColor;
            mTextPaint.setColor(textColor);
            canvas.drawText(mTextOff, mTextOffLeft, mTextTop, mTextPaint);
        }
        
        // right
        if (mTextOn != null && !mTextOn.equals(""))
        {
            textColor = mChecked ? mCheckedTextColor : mUnCheckedTextColor;
            mTextPaint.setColor(textColor);
            canvas.drawText(mTextOn, mTextOnLeft, mTextTop, mTextPaint);
        }
        
    }
    
    public static interface OnCheckedChangeListener
    {
        void onCheckedChanged(Switcher switcher, boolean isChecked);
    }
    
}
