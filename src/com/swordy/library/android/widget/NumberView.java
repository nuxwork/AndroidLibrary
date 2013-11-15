package com.swordy.library.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.swordy.library.android.R;

public class NumberView extends View
{
    public static final String TAG = NumberView.class.getSimpleName();
    
    private static final int COLOR_BG_NORMAL = 0xff212228;
    
    private static final int COLOR_BG_PRESSED = 0xff2d7dfa;
    
    private static final int COLOR_TEXT_NORMAL = 0xff9b9fa5;
    
    private static final int COLOR_TEXT_PRESSED = 0xffffffff;
    
    private Paint mPaint;
    
    private String mText;
    
    private int mTextSize;
    
    private Rect mDrawingRect;
    
    private RectF mDrawingRectF;
    
    private Drawable mTextColor;
    
    private float mAngle;
    
    public NumberView(Context context)
    {
        super(context);
        onCreate(context, null, 0);
    }
    
    public NumberView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        onCreate(context, attrs, 0);
    }
    
    public NumberView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        onCreate(context, attrs, defStyle);
    }
    
    protected void onCreate(Context context, AttributeSet attrs, int defStyle)
    {
        mDrawingRect = new Rect();
        mDrawingRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.MONOSPACE);
        mPaint.setTextAlign(Align.CENTER);
        
        setClickable(true);
        
        if (attrs == null)
        {
            mTextSize = 70;
            mAngle = 0f;
        }
        else
        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberView, defStyle, 0);
            mText = a.getString(R.styleable.NumberView_text);
            mTextSize = a.getDimensionPixelSize(R.styleable.NumberView_textSize, 70);
            mTextColor = a.getDrawable(R.styleable.NumberView_textColor);
            mAngle = a.getFloat(R.styleable.NumberView_angle, 0f);
        }
        
        mPaint.setTextSize(mTextSize);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                setPressed(false);
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        else
        {
            
        }
        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        else
        {
            
        }
        
        setMeasuredDimension(width, height);
    }
    
    @SuppressLint("NewApi")
    @Override
    public void draw(Canvas canvas)
    {
        canvas.save();
        
        getDrawingRect(mDrawingRect);
        mDrawingRectF.set(mDrawingRect);
        
        //-------  draw background -------
        boolean canDrawBg = false;
        Drawable background = getBackground();
        if (background == null)
        {
            if (isPressed())
            {
                mPaint.setColor(COLOR_BG_PRESSED);
            }
            else
            {
                mPaint.setColor(COLOR_BG_NORMAL);
            }
            canDrawBg = true;
        }
        else
        {
            background.setState(getDrawableState());
            if (background instanceof StateListDrawable)
            {
                Drawable current = background.getCurrent();
                if (current instanceof ColorDrawable)
                {
                    mPaint.setColor(((ColorDrawable)current).getColor());
                    canDrawBg = true;
                }
                else
                {
                    current.draw(canvas);
                }
            }
            else if (background instanceof ColorDrawable)
            {
                mPaint.setColor(((ColorDrawable)background).getColor());
                canDrawBg = true;
            }
            else
            {
                background.setBounds(mDrawingRect);
                background.draw(canvas);
            }
        }
        
        if (canDrawBg)
        {
            canvas.drawRoundRect(mDrawingRectF, mAngle, mAngle, mPaint);
        }
        
        // ------------ draw text ------------
        if (mText == null || "".equals(mText))
            return;
        
        boolean canDrawText = false;
        if (mTextColor == null)
        {
            if (isPressed())
            {
                mPaint.setColor(COLOR_TEXT_PRESSED);
            }
            else
            {
                mPaint.setColor(COLOR_TEXT_NORMAL);
            }
            canDrawText = true;
            
        }
        else if (mTextColor instanceof StateListDrawable)
        {
            mTextColor.setState(getDrawableState());
            Drawable current = mTextColor.getCurrent();
            if (current instanceof ColorDrawable)
            {
                mPaint.setColor(((ColorDrawable)current).getColor());
                canDrawText = true;
            }
            else
            {
                current.draw(canvas);
            }
        }
        else if (mTextColor instanceof ColorDrawable)
        {
            mPaint.setColor(((ColorDrawable)mTextColor).getColor());
            canDrawText = true;
        }
        else
        {
            mTextColor.draw(canvas);
        }
        
        if (canDrawText)
        {
            canvas.drawText(mText,
                mDrawingRectF.centerX(),
                (float)(mDrawingRectF.centerY() + mTextSize * 0.75 / 2),
                mPaint);
        }
        
        canvas.restore();
    }
}
