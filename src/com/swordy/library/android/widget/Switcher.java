package com.swordy.library.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;

import com.swordy.library.android.R;

public class Switcher extends View implements Checkable
{
    private static final String TAG = "Switcher";
    
    private Context mContext;
    
    private Drawable mThumb;
    
    private String mTextOn;
    
    private String mTextOff;
    
    private Drawable mTextColor;
    
    private int mTextSize;
    
    private Rect mDrawingRect;
    
    boolean mChecked;
    
    private OnCheckedChangeListener mOnCheckedChangeListener;
    
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
        setClickable(true);
        
        if (attrs == null)
        {
            mChecked = false;
            return;
        }
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Switcher, defStyle, 0);
        mThumb = a.getDrawable(R.styleable.Switcher_thumb);
        mTextOn = a.getString(R.styleable.Switcher_textOn);
        mTextOff = a.getString(R.styleable.Switcher_textOff);
        mTextColor = a.getDrawable(R.styleable.Switcher_textColor);
        mTextSize = a.getDimensionPixelSize(R.styleable.Switcher_textSize, 0);
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
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        
        Drawable background = getBackground();
        Drawable[] drawables = new Drawable[] {background, mThumb};
        
        if (widthMode == MeasureSpec.UNSPECIFIED)
        {
            width = getMaxSize(drawables, true);
            //TODO calculate text size
        }
        else
        {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        
        if (heightMode == MeasureSpec.UNSPECIFIED)
        {
            height = getMaxSize(drawables, false);
        }
        else
        {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        
        setMeasuredDimension(width, height);
        
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
        //TODO 屏蔽
        return super.onTouchEvent(event);
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
    }
    
    @Override
    public boolean performClick()
    {
        toggle();
        return super.performClick();
    }
    
    private void setChecked(boolean checked, boolean fromUser)
    {
        int[] state = {};
        if (checked)
        {
            state = new int[] {android.R.attr.state_checked};
        }
        else
        {
            //            state = new int[]{android.R.attr.state_}
        }
    }
    
    @Override
    public void setChecked(boolean checked)
    {
        
    }
    
    @Override
    public boolean isChecked()
    {
        return false;
    }
    
    @Override
    public void toggle()
    {
        setChecked(!mChecked);
    }
    
    public static interface OnCheckedChangeListener
    {
        void onCheckedChanged(SwitcherOld switcher, boolean isChecked);
    }
}
