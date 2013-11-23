package com.swordy.library.android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Checkable;

import com.swordy.library.android.R;

public class Switcher extends View implements Checkable
{
    public static final String TAG = "Switcher";
    
    private static final int TOUCH_EVENT_FILTER_DURATION = 30;
    
    private static final int FLAG_THUMB_PRESSED = 0x00000001;
    
    private int mFlag = 0;
    
    private Context mContext;
    
    private Drawable mThumb;
    
    private int mThumbOffset;
    
    private String mTextOn;
    
    private String mTextOff;
    
    private float mTextOnOffset;
    
    private float mTextOffOffset;
    
    private int mTextSize;
    
    private Rect mDrawingRect;
    
    private Rect mThumbRect;
    
    boolean mChecked;
    
    private Paint mTextPaint;
    
    private ColorStateList mTextColor;
    
    private OnCheckedChangeListener mOnCheckedChangeListener;
    
    private long mPressedTime = System.currentTimeMillis();
    
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    
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
        mContext = context;
        mDrawingRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        setClickable(true);
        
        if (attrs == null)
        {
            // TODO attrs = default attrs
            mChecked = false;
            return;
        }
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Switcher, defStyle, 0);
        mThumb = a.getDrawable(R.styleable.Switcher_thumb);
        mTextOn = a.getString(R.styleable.Switcher_textOn);
        mTextOff = a.getString(R.styleable.Switcher_textOff);
        mTextColor = a.getColorStateList(R.styleable.Switcher_textAppearance);
        mTextSize = a.getDimensionPixelSize(R.styleable.Switcher_textSize, 0);
        mTextPaint.setTextSize(mTextSize);
        boolean checked = a.getBoolean(R.styleable.Switcher_checked, false);
        setChecked(checked);
        a.recycle();
    }
    
    @Override
    protected int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
        {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
    
    public String getTextOn()
    {
        return mTextOn;
    }
    
    public void setTextOn(String textOn)
    {
        mTextOn = textOn;
        requestLayout();
    }
    
    public String getTextOff()
    {
        return mTextOff;
    }
    
    public void setTextOff(String textOff)
    {
        mTextOff = textOff;
        requestLayout();
    }
    
    public void setThumb(Drawable thumb)
    {
        mThumb = thumb;
        requestLayout();
    }
    
    public void setTextColor(ColorStateList textColor)
    {
        mTextColor = textColor;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;
        
        //TODO calculate text size + padding size
        //        int textOnWidth = mTextOn == null ? 0 : (int)mTextPaint.measureText(mTextOn);
        //        int textOffWidth = mTextOff == null ? 0 : (int)mTextPaint.measureText(mTextOff);
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        
        Drawable background = getBackground();
        Drawable[] drawables = new Drawable[] {background, mThumb};
        
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        else
        {
            width = getMaxSize(drawables, true);
        }
        
        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        else
        {
            height = getMaxSize(drawables, false);
        }
        
        setMeasuredDimension(width, height);
        
        //        mThumbRect.set(src)
        
        if (isChecked())
        {
            // mThumbOffset = max;
        }
    }
    
    
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        getDrawingRect(mDrawingRect);
    }

    /**
     * @param tag true is pointer to width, and false is pointer to height
     */
    private int getMaxSize(Drawable[] drawables, boolean tag)
    {
        int max = 0;
        int cur = 0;
        for (Drawable d : drawables)
        {
            if (d != null)
            {
                if (tag)
                    cur = d.getIntrinsicWidth();
                else
                    cur = d.getIntrinsicHeight();
                
                if (cur > max)
                    max = cur;
            }
        }
        return max;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                int x = (int)event.getX();
                int y = (int)event.getY();
                if (mThumb != null && mThumb.getBounds().contains(x, y))
                {
                    mFlag |= FLAG_THUMB_PRESSED;
                }
                setPressed(true);
                
                break;
            
            case MotionEvent.ACTION_UP:
                mFlag &= ~FLAG_THUMB_PRESSED;
                break;
            
            case MotionEvent.ACTION_MOVE:
                break;
        }
        //TODO 屏蔽
        return super.onTouchEvent(event);
    }
    
    @Override
    public void draw(Canvas canvas)
    {
        Drawable background = getBackground();
        if (background != null)
        {
            background.setBounds(mDrawingRect);
            background.draw(canvas);
        }
        
        if (mThumb != null)
        {
            mThumb.setBounds(0, 0, mThumb.getIntrinsicWidth(), mThumb.getIntrinsicHeight());
            mThumb.draw(canvas);
        }
        
        if (mTextColor != null)
            mTextPaint.setColor(mTextColor.getColorForState(getDrawableState(), 0));
        float textTop = (float)(mTextSize * 0.375);
        if (mTextOn != null && !"".equals(mTextOn))
        {
            canvas.drawText(mTextOn, mTextOnOffset, textTop, mTextPaint);
        }
        if (mTextOff != null && !"".equals(mTextOff))
        {
            canvas.drawText(mTextOff, mTextOffOffset, textTop, mTextPaint);
        }
    }
    
    @Override
    public boolean performClick()
    {
        toggle();
        return super.performClick();
    }
    
    @Override
    protected void drawableStateChanged()
    {
        super.drawableStateChanged();
        
        int[] myDrawableState = getDrawableState();
        
        if (mThumb != null && mThumb.isStateful() && (mFlag | FLAG_THUMB_PRESSED) == FLAG_THUMB_PRESSED)
        {
            mThumb.setState(myDrawableState);
        }
        
        long duration = System.currentTimeMillis() - mPressedTime;
        if (isPressed())
        {
            invalidate();
            mPressedTime = System.currentTimeMillis();
        }
        else
        {
            postInvalidateDelayed(ViewConfiguration.getPressedStateDuration() - duration);
        }
    }
    
    private void setChecked(boolean checked, boolean fromUser)
    {
        if (mChecked == checked)
            return;
        
        mChecked = checked;
        refreshDrawableState();
        
        OnCheckedChangeListener l = mOnCheckedChangeListener;
        if (l != null)
        {
            l.onCheckedChanged(this, checked, fromUser);
        }
        //TODO setChecked
    }
    
    @Override
    public void setChecked(boolean checked)
    {
        setChecked(checked, false);
    }
    
    @Override
    public boolean isChecked()
    {
        return mChecked;
    }
    
    @Override
    public void toggle()
    {
        setChecked(!mChecked);
    }
    
    public static interface OnCheckedChangeListener
    {
        void onCheckedChanged(Switcher switcher, boolean isChecked, boolean fromUser);
    }
}
