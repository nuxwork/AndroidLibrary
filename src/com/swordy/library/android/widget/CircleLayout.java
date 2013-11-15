package com.swordy.library.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.swordy.library.android.R;

public class CircleLayout extends ViewGroup
{
    
    private static final String TAG = "CircleLayout";
    
    private static final double PI_DIV_180 = 0.017453292519943295;
    
    /**
     * 旋转偏移量
     */
    private float mRotationOffset;
    
    private float mStartAngle;
    
    private int mVisibleChildCount;
    
    public CircleLayout(Context context)
    {
        super(context);
        mRotationOffset = 0f;
        mStartAngle = 0f;
    }
    
    public CircleLayout(Context context, AttributeSet attrs)
    {
        //        super(context, attrs);
        this(context, attrs, 0);
    }
    
    public CircleLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        
        mRotationOffset = 0f;
        mStartAngle = 0f;
        
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleLayout, defStyle, 0);
        try
        {
            mRotationOffset = a.getFloat(R.styleable.CircleLayout_rotationOffset, 0f);
            mStartAngle = a.getFloat(R.styleable.CircleLayout_startAngle, 0f);
        }
        finally
        {
            a.recycle();
        }
    }
    
    public float getRotationOffset()
    {
        return mRotationOffset;
    }
    
    public void setRotationOffset(float rotationOffset)
    {
        mRotationOffset = rotationOffset;
    }
    
    public float getStartAngle()
    {
        return mStartAngle;
    }
    
    public void setStartAngle(float startAngle)
    {
        mStartAngle = startAngle;
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        // see onMeasure method
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // 1. get self size
        // 2. calculate circle size include pivot and radius(min border size)
        // 3. get max child size
        // 4. calculate max child circle size include pivot and radius(max border size)
        // 5. the inside circle radius is sub of self circle radius between max child radius
        // 6. calculate and set each child position
        // 7. layout children at onLayout method
        
        int selfWidth = 0;
        int selfHeight = 0;
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        if (widthMode != MeasureSpec.UNSPECIFIED)
        {
            selfWidth = 0;
        }
        
        if (heightMode != MeasureSpec.UNSPECIFIED)
        {
            selfHeight = 0;
        }
        
        if (widthMode == MeasureSpec.EXACTLY)
        {
            selfWidth = widthSize;
        }
        
        if (heightMode == MeasureSpec.EXACTLY)
        {
            selfHeight = heightSize;
        }
        
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // get the max size in children
        int childrenWidth = 0;
        int childrenHeight = 0;
        
        int w, h; // temp width and height
        int count = getChildCount();
        mVisibleChildCount = 0;
        for (int i = 0; i != count; i++)
        {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE)
            {
                w = child.getMeasuredWidth();
                h = child.getMeasuredHeight();
                childrenWidth = w > childrenWidth ? w : childrenWidth;
                childrenHeight = h > childrenHeight ? h : childrenHeight;
                mVisibleChildCount ++;
            }
            
        }
        int parentWidth = selfWidth - getPaddingLeft() - getPaddingRight();
        int parentHeight = selfHeight - getPaddingTop() - getPaddingBottom();
        positionChildren(parentWidth, parentHeight, childrenWidth, childrenHeight);
        
        setMeasuredDimension(selfWidth, selfHeight);
    }
    
    /**
     *设置每个子View的位置
     */
    private void positionChildren(int parentWdith, int parentHeight, int childrenWidth, int childrenHeight)
    {
        float childrenRadius = childrenWidth > childrenHeight ? childrenWidth / 2f : childrenHeight / 2f;
        
        PointF parentPivot = new PointF(parentWdith / 2f, parentHeight / 2f);
        float parentRadius = (parentPivot.x < parentPivot.y ? parentPivot.x : parentPivot.y) - childrenRadius;
        parentPivot.x += getPaddingLeft();
        parentPivot.y += getPaddingTop();
        
        int childLeft, childTop, childRight, childBottom;
        int count = getChildCount();
        double angleStep = (Math.PI * 2) / mVisibleChildCount;
        double angle = mStartAngle * PI_DIV_180 + mRotationOffset * angleStep;
        for (int i = 0; i != count; i++)
        {
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE)
                continue;
            childLeft = (int)(parentPivot.x + Math.cos(angle) * parentRadius - childrenWidth / 2f);
            childTop = (int)(parentPivot.y + Math.sin(angle) * parentRadius - childrenHeight / 2f);
            childRight = childLeft + child.getMeasuredWidth();
            childBottom = childTop + child.getMeasuredHeight();
            child.layout(childLeft, childTop, childRight, childBottom);
            angle += angleStep;
        }
    }
    
}
