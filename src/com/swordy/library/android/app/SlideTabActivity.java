package com.swordy.library.android.app;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Adapter;
import android.widget.TabWidget;

import com.swordy.library.android.R;
import com.swordy.library.android.widget.PageIndicator;

/**
 * a slidable tab activity, implemented by using
 * {@link android.widget.TabWidget}, {@link android.support.v4.view.ViewPager}
 * and {@link android.support.v4.app.FragmentActivity}
 * 
 * @author swordy
 * 
 */
public class SlideTabActivity extends FragmentActivity implements
		ViewPager.OnPageChangeListener {
	public static final String TAG = "AETAndroid.SlideTabActivity";

	private TabWidget mTabWidget;
	private ViewPager mViewPager;
	private PageIndicator mIndicator;

	private boolean mSmoothScroll = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		mTabWidget = (TabWidget) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pagers);
		mIndicator = (PageIndicator) findViewById(R.id.indicator);

		if (mTabWidget == null || mViewPager == null) {
			throw new RuntimeException(
					"Your content must have a TabWidget whose id attribute is "
							+ "'R.id.tabs' and a ViewPager whose id attribute is "
							+ "'R.id.pagers'");
		}

//		onCreateTabs(mTabWidget);
//		onCreatePagers(mViewPager, mIndicator);

		handTabListener();
		mViewPager.setOnPageChangeListener(this);

		mTabWidget.setCurrentTab(0);
		mViewPager.setCurrentItem(0);
	}

//	/**
//	 * create tabs on content changed, or call {@link #getTabWidget()} to get
//	 * reference of tabWidget, and then create tabs anywhere.
//	 * 
//	 * @param tabWidget
//	 *            TabWidget in your layout whose id attribute is 'R.id.tabs'.
//	 */
//	protected void onCreateTabs(TabWidget tabWidget) {
//	}
//
//	/**
//	 * create viewPager and indicator on content changed, or call
//	 * {@link #getViewPager()} and {@link {@link #getIndicator()} to get
//	 * reference, and then create them anywhere.
//	 * 
//	 * @param viewPager
//	 *            ViewPager in your layout whose id attribute is
//	 *            'R.id.viewPager'.
//	 * @param indicator
//	 *            PageIndicator in your layout whose id attribute is
//	 *            'R.id.indicator'. a indicator is unnecessary, maybe is null if
//	 *            not appear in your layout.
//	 */
//	protected void onCreatePagers(ViewPager viewPager, PageIndicator indicator) {
//	}

	public void setTabAdapter(Adapter adapter) {
		if (mTabWidget != null) {
			mTabWidget.removeAllViews();

			int count = adapter.getCount();
			for (int i = 0; i != count; i++) {
//				adapter.getView(i, null, mTabWidget);
				mTabWidget.addView(adapter.getView(i, null, null));
			}
			
			mTabWidget.setCurrentTab(0);
		}
	}

	public void setPagerAdapter(PagerAdapter adapter) {
		if (mViewPager != null) {
			mViewPager.setAdapter(adapter);
			mViewPager.setCurrentItem(0);
		}
	}

	public void setIndicatorAdapter(Adapter adapter) {
		if (mIndicator != null) {
			mIndicator.removeAllViews();

			int count = adapter.getCount();
			for (int i = 0; i != count; i++) {
				mIndicator.addView(adapter.getView(i, null, null));
			}
			
			mIndicator.setCurrentItem(0);
		}
	}

	/**
	 * set tab change listener to TabWidget using reflect.
	 */
	@SuppressWarnings("rawtypes")
	private void handTabListener() {
		Class<TabWidget> cls = TabWidget.class;
		try {
			Class listener = null;
			for (Class c : cls.getDeclaredClasses()) {
				if (c.getSimpleName().equals("OnTabSelectionChanged")) {
					listener = c;
				}
			}

			Method method = cls.getDeclaredMethod("setTabSelectionListener",
					listener);

			Object listenerImpl = Proxy.newProxyInstance(
					listener.getClassLoader(), new Class[] { listener },
					new OnTabSelectionChangedImpl());

			method.setAccessible(true);
			method.invoke(mTabWidget, listenerImpl);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		int curItem = mViewPager.getCurrentItem();
		outState.putInt("CurrentItem", curItem);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int curItem = savedInstanceState.getInt("CurrentItem", 0);

		mViewPager.setCurrentItem(curItem, mSmoothScroll);
	}

	public void setCurrentItem(int index) {
		setCurrentItem(index, mSmoothScroll);
	}

	public void setCurrentItem(int index, boolean smoothScroll) {
		mViewPager.setCurrentItem(index, smoothScroll);
	}

	public int getCurrentItem() {
		return mViewPager.getCurrentItem();
	}

	public void setSmoothScrollEnabled(boolean smoothScroll) {
		mSmoothScroll = smoothScroll;
	}

	public boolean isEnableSmoothScroll() {
		return mSmoothScroll;
	}

	public TabWidget getTabWidget() {
		return mTabWidget;
	}

	public ViewPager getViewPager() {
		return mViewPager;
	}

	public PageIndicator getIndicator() {
		return mIndicator;
	}

	@Override
	public void onPageSelected(int index) {
		Log.i(TAG, "@Method --> onPageSelected: " + index);

		mTabWidget.setCurrentTab(index);
		if (mIndicator != null) {
			mIndicator.setCurrentItem(index);
		}
	}

	@Override
	public void onPageScrolled(int index, float arg1, int arg2) {
		// do nothing
	}

	@Override
	public void onPageScrollStateChanged(int index) {
		// do nothing
	}

	/**
	 * implements interface "TabWidget.OnTabSelectionChanged" using reflection
	 * 
	 * @author swordy
	 */
	private class OnTabSelectionChangedImpl implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			if ("onTabSelectionChanged".equals(method.getName())
					&& args != null && args.length == 2) {
				int tabIndex = (Integer) args[0];
				boolean clicked = (Boolean) args[1];

				Log.i(TAG, "@Method --> OnTabSelectionChanged: " + tabIndex);

				mViewPager.setCurrentItem(tabIndex);
				if (mIndicator != null) {
					mIndicator.setCurrentItem(tabIndex);
				}
			}

			return null/* void */;
		}

	}
}
