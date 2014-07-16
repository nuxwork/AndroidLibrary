package com.swordy.library.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.swordy.library.android.R;
import com.swordy.library.android.util.ALog;

public class RoundLayout extends FrameLayout {
	public static final String TAG = "YunyuCamera.RoundLayout";

	private String mRadiiStr;
	private float[] mRadii;
	private RectF mRectF;

	public RoundLayout(Context context) {
		super(context);
		onCreate(context, null, 0);
	}

	public RoundLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate(context, attrs, 0);
	}

	public RoundLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate(context, attrs, defStyle);
	}

	@SuppressLint("NewApi")
	private void onCreate(Context context, AttributeSet attrs, int defStyle) {
		mRectF = new RectF();

		if (attrs == null) {
			mRadiiStr = null;
			mRadii = null;
		} else {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.RoundLayout, defStyle, defStyle);
			mRadiiStr = a.getString(R.styleable.RoundLayout_radii);
			a.recycle();
		}

		if (mRadiiStr != null) {
			String[] rs = mRadiiStr.split(",");
			mRadii = new float[rs.length];
			for (int i = 0; i != rs.length; i++) {
				mRadii[i] = Integer.parseInt(rs[i]);
			}
			fixRadii();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				setLayerType(LAYER_TYPE_SOFTWARE, null);
			} else {
				ALog.e(TAG, "setLayerType unsupported this platform.");
			}
			setBackgroundColor(1);
		}
	}

	public float[] getRadii() {
		return mRadii;
	}

	public void setRadii(float[] radii) {
		mRadii = radii;
		fixRadii();
	}

	private void fixRadii() {
		if (mRadii == null) {
			return;
		}

		int len = mRadii.length;
		float[] a = mRadii;
		switch (len) {
		case 0:
			mRadii = null;
			throw new IllegalArgumentException("empty array.");
		case 1:
			mRadii = new float[] { a[0], a[0], a[0], a[0], a[0], a[0], a[0],
					a[0] };
			break;
		case 4:
			mRadii = new float[] { a[0], a[0], a[1], a[1], a[2], a[2], a[3],
					a[3] };
			break;
		case 8:
			break;
		default:
			mRadii = null;
			throw new IllegalArgumentException("array size must be 1, 4 or 8.");
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
	}

	@Override
	public void draw(Canvas canvas) {
		if (mRadii == null) {
			super.draw(canvas);
		} else {
			canvas.save();
			Path path = new Path();
			path.addRoundRect(mRectF, mRadii, Direction.CCW);
			canvas.clipPath(path, Op.REPLACE);
			super.draw(canvas);
			canvas.restore();
		}
	}
}
