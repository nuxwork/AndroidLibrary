package com.swordy.library.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;

import com.swordy.library.android.R;

public class CheckButton extends ImageButton implements Checkable
{
    private static final String TAG = "AndroidLibrary.CheckButton";
    
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    
    private boolean mChecked;
    
    private OnCheckedChangeListener mOnCheckedChangeListener;
    
    public CheckButton(Context context)
    {
        super(context);
        onCreate(context, null, 0);
    }
    
    public CheckButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        onCreate(context, attrs, 0);
    }
    
    public CheckButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        onCreate(context, attrs, defStyle);
    }
    
    protected void onCreate(Context context, AttributeSet attrs, int defStyle)
    {
        if (attrs == null)
        {
            mChecked = false;
            return;
        }
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckButton, defStyle, 0);
        mChecked = a.getBoolean(R.styleable.CheckButton_checked, false);
        a.recycle();
    }
    
    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
        {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
    
    @Override
    public boolean performClick()
    {
        toggle();
        return super.performClick();
    }
    
    private void setChecked(boolean checked, boolean fromUser)
    {
        if (mChecked != checked)
        {
            mChecked = checked;
            refreshDrawableState();
            
            if (mOnCheckedChangeListener != null)
            {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
        }
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
    
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener)
    {
        mOnCheckedChangeListener = listener;
    }
    
    @Override
    public String toString()
    {
        return "CheckButton.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + mChecked
            + "}";
    }
    
    public static interface OnCheckedChangeListener
    {
        void onCheckedChanged(CheckButton checkButton, boolean isChecked);
    }
}
