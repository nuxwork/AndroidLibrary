package com.swordy.library.android.app;

import java.util.Stack;

import android.app.Activity;
import android.util.Log;

public class ActivityStack {
	private static final String TAG = "ITService.ActivityStack";

	private Stack<Activity> mStack;

	private static ActivityStack mInstance;

	public static ActivityStack create() {
		if (mInstance != null)
			throw new RuntimeException("ActivityStack is already instanced");

		mInstance = new ActivityStack();
		return mInstance;
	}

	public static ActivityStack instance() {
		if (mInstance == null)
			throw new RuntimeException("ActivityStack is not instanced");

		return mInstance;
	}

	private ActivityStack() {
		mStack = new Stack<Activity>();
	}

	public void push(Activity activity) {
		mStack.add(activity);
//		Log.i(TAG, "push ###  " + activity + "  \n" + mStack.toString());
	}

	public Activity pop() {
		Activity act = mStack.pop();
//		Log.e(TAG, "pop ###  " + act + " \n" + mStack.toString());
		return act;
	}

	public Activity peek() {
		Activity act = mStack.peek();
//		Log.v(TAG, "peek ###  " + act + " \n" + mStack.toString());
		return act;
	}

	public boolean inTop(Activity activity) {
		if (mStack.isEmpty())
			return false;

		Activity act = mStack.peek();
		boolean in = act == activity;
//		Log.w(TAG, "inTop ###  " + activity + "  == " + in + "  \n" + mStack.toString());
		return in;
	}

	public boolean remove(Activity activity) {
		boolean b = mStack.remove(activity);
//		Log.e(TAG, "remove ###  " + activity + "  == " + b + "  \n" + mStack.toString());
		return b;
	}

	public void backTo(Class<? extends Activity> cls) {
		int s = mStack.size();
		for (int i = 0; i != s; i++) {
			Activity act = mStack.pop();
			if(act.getClass() == cls)
				break;
			
			act.finish();
//			Log.e(TAG, "pop && finish ###  " + act + " \n" + mStack.toString());
		}
	}

	public void exit() {
		int s = mStack.size();
		for (int i = 0; i != s; i++) {
			Activity act = mStack.pop();
			act.finish();
//			Log.e(TAG, "pop && finish ###  " + act + " \n" + mStack.toString());
		}
	}
}
