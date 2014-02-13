package com.swordy.library.android.widget;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * tab页指示器
 * @author swordy
 *
 */
public class PageIndicator extends LinearLayout {

	private int mCount;
	private int mSelectedIndex = -1;
	private int mItemLayout = View.NO_ID;
	private LayoutParams mItemLayoutParams;
	private OnSelectionChangedListener mSelectionChangedListener;

	private ArrayList<View> mItems = new ArrayList<View>(8);

	public PageIndicator(Context context) {
		super(context);
	}

	public PageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCurrentItem(int index) {
		int count = mCount;
		int oldSelectedIndex = mSelectedIndex;
		ArrayList<View> items = mItems;
		OnSelectionChangedListener listener = mSelectionChangedListener;

		if (index < 0 || index >= count || index == oldSelectedIndex) {
			return;
		}

		if (oldSelectedIndex != -1) {
			items.get(oldSelectedIndex).setSelected(false);
		}
		items.get(index).setSelected(true);

		mSelectedIndex = index;

		if (listener != null) {
			listener.onSelectionChanged(oldSelectedIndex, index);
		}
	}

	public void setItemLayout(int resId, LayoutParams params) {
		int oldLayoutId = mItemLayout;
		LayoutParams oldParams = mItemLayoutParams;
		ArrayList<View> items = mItems;

		if (oldLayoutId == resId && oldParams == params) {
			return;
		}

		mItemLayout = resId;
		mItemLayoutParams = params;

		int size = items.size();
		if (size == 0) {
			return;
		}

		if (oldLayoutId != resId) {
			for (int i = 0; i != size; i++) {
				View v = items.get(i);
				items.remove(v);
				removeView(v);
			}

			add(size);
		} else if (oldParams != params) {
			for (int i = 0; i != size; i++) {
				View v = items.get(i);
				v.setLayoutParams(params);
			}
		}
	}

	public void setmSelectionChangedListener(OnSelectionChangedListener listener) {
		this.mSelectionChangedListener = listener;
	}

	private void checkItemLayout() {
		if (mItemLayout == -1 || mItemLayoutParams == null) {
			throw new RuntimeException(
					"set item layout first by call setItemLayout().");
		}
	}

	/**
	 * get the count of indicators
	 */
	public int getCount() {
		return mItems.size();
	}

	/**
	 * get indicator by index
	 */
	public View getItem(int index) {
		ArrayList<View> items = mItems;
		int count = items.size();

		if (index < 0 || index >= count) {
			return null;
		}

		return items.get(index);
	}

	/**
	 * add a indicator
	 */
	public void add() {
		add(1);
	}

	public void add(int count) {
		checkItemLayout();

		int index = mSelectedIndex;
		int layoutId = mItemLayout;
		LayoutParams params = mItemLayoutParams;
		ArrayList<View> items = mItems;

		if (count <= 0) {
			return;
		}

		for (int i = 0; i != count; i++) {
			View v = View.inflate(getContext(), layoutId, null);
			addView(v, params);
			items.add(v);
		}

		mCount += count;

		if (index == -1) {
			setCurrentItem(0);
		}
	}

	/**
	 * remove a indicator
	 */
	public void remove() {
		remove(1);
	}

	public void remove(int count) {
		checkItemLayout();

		ArrayList<View> items = mItems;
		int size = items.size();

		if (count <= 0 || count > size) {
			return;
		}

		for (int i = 0; i != count; i++) {
			View v = items.get(i);
			items.remove(v);
			removeView(v);
		}

		mCount -= count;
	}

	public static interface OnSelectionChangedListener {
		void onSelectionChanged(int oldIndex, int curIndex);
	}
}
