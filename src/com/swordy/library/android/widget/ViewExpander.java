package com.swordy.library.android.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ListView;

public class ViewExpander {

	/**
	 * 将ListView展开，解决ListView和ScrollView共存的问题
	 * @param listView
	 */
	public static void expandListView(ListView listView) {
		Adapter adapter = listView.getAdapter();
		if (adapter == null)
			return;

		int totalHeight = 0;
		int count = adapter.getCount();

		for (int i = 0; i != count; i++) {
			View item = adapter.getView(i, null, listView);
			item.measure(0, 0);
			totalHeight += item.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 将GridView展开，解决GridView和ScrollView共存的问题
	 * 
	 * @param gridView
	 * @param divideHeight verticalSpacing
	 * @param columnNums number of column
	 */
	public static void expandGridView(GridView gridView, int divideHeight,
			int columnNums) {
		Adapter adapter = gridView.getAdapter();
		if (adapter == null)
			return;

		int totalHeight = 0;
		int c = adapter.getCount();
		int count = c / columnNums + (c % columnNums == 0 ? 0 : 1);

		for (int i = 0; i != count; i++) {
			View item = adapter.getView(i, null, gridView);
			item.measure(0, 0);
			totalHeight += item.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (divideHeight * (count - 1));
		gridView.setLayoutParams(params);
	}
}