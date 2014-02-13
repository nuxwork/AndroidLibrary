package com.swordy.library.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class IndicatorAdapter extends BaseAdapter {
	private Context mContext;
	private int mLayoutResID;
	private int mCount;
	private LayoutInflater mInflater;

	public IndicatorAdapter(Context context, int layoutResID, int count) {
		mContext = context;
		mLayoutResID = layoutResID;
		mCount = count;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(mLayoutResID, parent, false);
		return v;
	}

}