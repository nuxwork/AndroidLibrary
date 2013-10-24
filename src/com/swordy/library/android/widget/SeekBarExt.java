package com.swordy.library.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.swordy.library.android.R;

public class SeekBarExt extends SeekBar {
	private static final String TAG = SeekBarExt.class.getSimpleName();

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private int mOrientation;

	private Context mContext;

	public SeekBarExt(Context context) {
		super(context);
		onCreate(context, null, 0);
	}

	public SeekBarExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate(context, attrs, 0);
	}

	public SeekBarExt(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate(context, attrs, defStyle);
	}

	protected void onCreate(Context context, AttributeSet attrs, int defStyle) {
		mContext = context;
		if (attrs == null) {
			mOrientation = HORIZONTAL;

			return;
		}

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekBarExt,
				defStyle, 0);
		mOrientation = a.getInt(R.styleable.SeekBarExt_orientation, HORIZONTAL);
		a.recycle();
	}

	public void setOrientation(int orientation) {
		mOrientation = orientation;
		requestLayout();
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mOrientation == HORIZONTAL)
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		else {
			Drawable d = getProgressDrawable();

			Drawable mThumb = getThumb();
			int thumbWidth = mThumb == null ? 0 : mThumb.getIntrinsicWidth();
			int thumbHeight = mThumb == null ? 0 : mThumb.getIntrinsicHeight();
			int dw = 0;
			int dh = 0;
			if (d != null) {
				dw = Math.max(thumbWidth, d.getIntrinsicWidth());
				dh = Math.max(thumbHeight, d.getIntrinsicHeight());
			}
			dw += getPaddingLeft() + getPaddingRight();
			dh += getPaddingTop() + getPaddingBottom();

			setMeasuredDimension(resolveSizeAndState(dw, widthMeasureSpec, 0),
					resolveSizeAndState(dh, heightMeasureSpec, 0));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		if (mOrientation == HORIZONTAL)
			return super.onTouchEvent(event);

		// TODO
//		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (mOrientation == HORIZONTAL)
			return super.onKeyDown(keyCode, event);

		// TODO
//		return true;
	}
}
