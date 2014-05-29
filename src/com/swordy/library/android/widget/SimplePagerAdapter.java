package com.swordy.library.android.widget;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

public class SimplePagerAdapter extends PagerAdapter {
	private LayoutInflater mInflater;
	private List<? extends Map<String, ?>> mData;
	private ViewBinder mViewBinder;
	private String[] mFrom;
	private int[] mTo;
	private int mLayoutId;

	public SimplePagerAdapter(Context context,
			List<? extends Map<String, ?>> data, int layoutId, String[] from,
			int[] to) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mData = data;
		mFrom = from;
		mTo = to;
		mLayoutId = layoutId;
	}
	
	public void setViewBinder(ViewBinder viewBinder) {
		mViewBinder = viewBinder;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return getView(container, position);
	}
	
	public View getView(ViewGroup container, int position){
		return createViewFromResource(container, position);
	}

	private View createViewFromResource(ViewGroup container, int position) {
		View v = null;

		v = mInflater.inflate(mLayoutId, null);
		bindView(position, v);
		container.addView(v, 0);

		return v;
	}

	private void bindView(int position, View view) {
		final Map dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = mViewBinder;
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}

				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, data, text);
				}

				if (!bound) {
					if (v instanceof Checkable) {
						if (data instanceof Boolean) {
							((Checkable) v).setChecked((Boolean) data);
						} else if (v instanceof TextView) {
							// Note: keep the instanceof TextView check at the
							// bottom of these
							// ifs since a lot of views are TextViews (e.g.
							// CheckBoxes).
							((TextView) v).setText(text);
						} else {
							throw new IllegalStateException(v.getClass()
									.getName()
									+ " should be bound to a Boolean, not a "
									+ (data == null ? "<unknown type>"
											: data.getClass()));
						}
					} else if (v instanceof TextView) {
						// Note: keep the instanceof TextView check at the
						// bottom of these
						// ifs since a lot of views are TextViews (e.g.
						// CheckBoxes).
						((TextView) v).setText(text);
					} else if (v instanceof ImageView) {
						if (data instanceof Integer) {
							((ImageView) v).setImageResource((Integer) data);
						} else {
							try {
								((ImageView) v).setImageResource(Integer
										.parseInt((String) data));
							} catch (NumberFormatException nfe) {
								((ImageView) v).setImageURI(Uri
										.parse((String) data));
							}
						}
					} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ " view that can be bounds by this SimpleAdapter");
					}
				}
			}
		}
	}
	
	

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	public static interface ViewBinder {
		/**
		 * Binds the specified data to the specified view.
		 * 
		 * When binding is handled by this ViewBinder, this method must return
		 * true. If this method returns false, SimpleAdapter will attempts to
		 * handle the binding on its own.
		 * 
		 * @param view
		 *            the view to bind the data to
		 * @param data
		 *            the data to bind to the view
		 * @param textRepresentation
		 *            a safe String representation of the supplied data: it is
		 *            either the result of data.toString() or an empty String
		 *            but it is never null
		 * 
		 * @return true if the data was bound to the view, false otherwise
		 */
		boolean setViewValue(View view, Object data, String textRepresentation);
	}
}