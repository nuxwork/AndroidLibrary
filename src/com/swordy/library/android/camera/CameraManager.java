package com.swordy.library.android.camera;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.view.Surface;

public class CameraManager {

	public static final int	FACE_BACK	= 0;
	public static final int	FACE_FRONT	= 1;

	private int				mCameraId;
	private int				mCameraFace;
	private Camera			mCamera;

	List<PreviewCallback>	mPreviewCallbacks;

	private static class CameraManagerHolder {
		private static final CameraManager	instance	= new CameraManager();
	}

	public static CameraManager instance() {
		return CameraManagerHolder.instance;
	}

	private CameraManager() {
		mCameraId = 0;
		mCameraFace = FACE_BACK;
	}

	public int getCameraId() {
		if (mCamera == null)
			throw new NullPointerException("camera is null.");
		return mCameraId;
	}

	public int getCameraFace() {
		if (mCamera == null)
			throw new NullPointerException("camera is null.");
		return mCameraFace;
	}

	public Camera getCamera() {
		return mCamera;
	}

	/**
	 * @param face
	 *            {@link #FACE_BACK}, {@link #FACE_FRONT}
	 */
	@SuppressLint("NewApi")
	public Camera openCamera(int face) {
		closeCamera();

		int cameraId = -1;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			int numberOfCameras = Camera.getNumberOfCameras();
			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < numberOfCameras; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == face) {
					cameraId = i;
					break;
				}
			}
		}

		if (cameraId == -1) {
			mCamera = Camera.open();
			mCameraId = FACE_BACK;
			mCameraFace = FACE_BACK;
		} else {
			mCamera = Camera.open(cameraId);
			mCameraId = cameraId;
			mCameraFace = face;
		}

		return mCamera;
	}

	public void closeCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	public void registPreviewCallback(PreviewCallback cb) {
		if (cb == null)
			throw new NullPointerException("PreviewCallback is null");

		if (mPreviewCallbacks == null)
			mPreviewCallbacks = new ArrayList<Camera.PreviewCallback>(2);

		if (mPreviewCallbacks.contains(cb)) {
			throw new RuntimeException("the callback has already registed.");
		}
		mPreviewCallbacks.add(cb);
		setPreviewCallbackIfNeed();
	}

	public void unregistPreviewCallback(PreviewCallback cb) {
		if (cb == null)
			throw new NullPointerException("PreviewCallback is null");

		if (!mPreviewCallbacks.contains(cb)) {
			throw new RuntimeException("the callback is not registed.");
		}
		mPreviewCallbacks.remove(cb);
		setPreviewCallbackIfNeed();
	}

	private void setPreviewCallbackIfNeed() {
		if (mCamera != null) {
			if (mPreviewCallbacks != null && mPreviewCallbacks.size() > 0) {
				mCamera.setPreviewCallback(new PreviewCallback() {

					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {
						for (PreviewCallback cb : mPreviewCallbacks) {
							cb.onPreviewFrame(data, camera);
						}
					}

				});
			} else {
				mCamera.setPreviewCallback(null);
			}
		}
	}

	public void setCameraOrientation(Activity activity) {
		setCameraOrientation(activity.getWindowManager().getDefaultDisplay().getRotation());
	}

	/**
	 * @param rotation
	 *            {@link Surface#ROTATION_0}, {@link Surface#ROTATION_90},
	 *            {@link Surface#ROTATION_180}, {@link Surface#ROTATION_270}
	 * 
	 * @see Activity.getWindowManager().getDefaultDisplay().getRotation();
	 */
	@SuppressLint("NewApi")
	public void setCameraOrientation(int rotation) {
		if (mCamera == null)
			return;

		if (rotation < Surface.ROTATION_0 || rotation > Surface.ROTATION_270) {
			throw new IllegalArgumentException("illegal rotation");
		}

		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(mCameraId, info);

		int degrees = rotation * 90;

		int result;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
		} else {
			if (mCameraId == FACE_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else {
				result = (info.orientation - degrees + 360) % 360;
			}
		}
		mCamera.setDisplayOrientation(result);
	}
}
