package com.swordy.library.android.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class CameraPreview extends SurfaceView implements Callback {
	public static final String	TAG			= "CameraPreview.CameraPreview";

	private static final float	PRECISION	= 0.05f;

	private Context				mContext;

	private Camera				mCamera;

	private boolean				mAutoPreview;

	private boolean				mPreviewing;

	private Point				mPreviewSize;

	private Point				mSurfaceSize;

	private int					mOrientation;

	public CameraPreview(Context context) {
		super(context);
		onCreate(context);
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate(context);
	}

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate(context);
	}

	private void onCreate(Context context) {
		mAutoPreview = true;
		mPreviewing = false;
		mSurfaceSize = new Point();
		mPreviewSize = new Point();

		mOrientation = Surface.ROTATION_0;

		getHolder().addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mSurfaceSize.x = width;
		mSurfaceSize.y = height;

		configCamera();
		
		if (mAutoPreview) {
			startPreview();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mAutoPreview) {
			stopPreview();
		}
	}

	public void setCamera(Camera camera) {
		stopPreview();
		
		mCamera = camera;
		if (mAutoPreview && isSurfaceValid()) {
			configCamera();
			startPreview();
		}
	}

	public Camera getCamera() {
		return mCamera;
	}

	public void startPreview() {
		if (mCamera != null) {
			if (!mPreviewing) {
				mPreviewing = true;
				mCamera.startPreview();
			}
		}
	}

	public void stopPreview() {
		if (mCamera != null && mPreviewing) {
			mPreviewing = false;
			mCamera.stopPreview();
		}
	}

	public boolean isPreviewing() {
		return mPreviewing;
	}

	public boolean isAutoPreview() {
		return mAutoPreview;
	}

	public void setAutoPreview(boolean autoPreview) {
		mAutoPreview = autoPreview;

		if (mCamera != null && isAutoPreview() && isSurfaceValid()) {
			stopPreview();
			startPreview();
		}
	}

	public int getOrientation() {
		return mOrientation;
	}

	public void setOrientation(int orientation) {
		if (orientation < Surface.ROTATION_0 || orientation > Surface.ROTATION_270)
			throw new IllegalArgumentException(
					"orientation must be Surface.ROTATION_0, Surface.ROTATION_90, Surface.ROTATION_180 or Surface.ROTATION_270.");

		mOrientation = orientation;

		if (mCamera != null && isAutoPreview() && isSurfaceValid()) {
			stopPreview();
			startPreview();
		}
	}

	public Point getPreviewSize() {
		return mPreviewSize;
	}

	public Point getSurfaceSize() {
		return mSurfaceSize;
	}

	/**
	 * is surface holder valid.
	 */
	public boolean isSurfaceValid() {
		SurfaceHolder holder = getHolder();
		if (holder != null && holder.isCreating()) {
			Surface surface = holder.getSurface();
			if (surface != null && surface.isValid()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 1. set preview display <br>
	 * 2. set preview size <br>
	 * 3. set surface size
	 */
	private void configCamera() {
		if (mCamera == null)
			return;

		if (isSurfaceValid()) {
			try {
				mCamera.setPreviewDisplay(getHolder());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		CameraManager.instance().setCameraOrientation(mOrientation);

		determinePreviewSize(mSurfaceSize.x, mSurfaceSize.y);
	}

	private void determinePreviewSize(final int surfaceWidth, final int surfaceHeight) {
		Log.v(TAG, "surfaceSize#  width: " + surfaceWidth + ",  height" + surfaceHeight);
		if (mCamera == null)
			return;

		if (surfaceWidth == 0 || surfaceHeight == 0)
			return;

		// already has best preview size
		if (mPreviewSize.y != 0) {
			float ratioPreview = (float) mPreviewSize.x / mPreviewSize.y;
			float ratioSurface = (float) surfaceWidth / surfaceHeight;
			if (Math.abs(ratioPreview - ratioSurface) < PRECISION)
				return;
		}

		boolean portrait = (mOrientation == Surface.ROTATION_0 || mOrientation == Surface.ROTATION_180);

		int max = surfaceWidth;
		if (portrait) {
			max = surfaceWidth >= surfaceHeight ? surfaceHeight : surfaceWidth;
		} else {
			max = surfaceWidth >= surfaceHeight ? surfaceWidth : surfaceHeight;
		}

		int minhDiff = Integer.MAX_VALUE;
		int minDiffIndex = 0;

		List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
		int count = previewSizes.size();
		for (int i = 0; i != count; i++) {
			Camera.Size s = previewSizes.get(i);
			Log.v(TAG, "supported previewSize# w: " + s.width + ", h: " + s.height);
			int wDiff = Math.abs(s.width - max);
			if (wDiff < minhDiff) {
				minhDiff = wDiff;
				minDiffIndex = i;
			}
		}

		Camera.Size s = previewSizes.get(minDiffIndex);
		mPreviewSize.x = portrait ? s.height : s.width;
		mPreviewSize.y = portrait ? s.width : s.height;

		Log.v(TAG, "determine PreviewSize# width: " + mPreviewSize.x + ",  height: "
				+ mPreviewSize.y);
		mCamera.getParameters().setPreviewSize(mPreviewSize.x, mPreviewSize.y);
		adjustSurfaceSize(surfaceWidth, surfaceHeight, mPreviewSize.x, mPreviewSize.y);
	}

	/**
	 * adjust surface size to keep same ratio to preview size
	 */
	private void adjustSurfaceSize(final int surfaceWidth, final int surfaceHeight,
			final int previewWidth, final int previewHeight) {
		if (surfaceHeight == 0 || previewHeight == 0)
			return;

		float ratioSurface = (float) surfaceWidth / surfaceHeight;
		float ratioPreview = (float) previewWidth / previewHeight;
		if (Math.abs(ratioSurface - ratioPreview) < PRECISION) {
			return;
		}

		int sw = surfaceWidth;
		int sh = surfaceHeight;

		sw = (int) (surfaceHeight * ratioPreview);
		if (sw > surfaceWidth) {
			sw = surfaceWidth;
			sh = (int) (surfaceWidth / ratioPreview);
		}

		Log.v(TAG, "new surfaceSize# width: " + sw + ",  height: " + sh);
		LayoutParams params = getLayoutParams();
		params.width = sw;
		params.height = sh;
		setLayoutParams(params);
	}
}
