package com.swordy.library.android.widget;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TabWidget;

import com.swordy.library.android.R;

/**
 * 可滑动的TabHost
 * 
 * @author swordy
 * 
 */
public class SlideTabPager extends LinearLayout implements
		ViewPager.OnPageChangeListener {
	private static final String TAG = "SlideTabPager";

	private TabWidget mTabWidget;
	private ViewPager mViewPager;
	private boolean mSmoothScroll = true;
	private OnPageChangedListener mOnPageChangedListener;

	public interface OnPageChangedListener {
		void onPageChanged(int position);
	}

	/* TODO: indicator */

	public SlideTabPager(Context context) {
		this(context, null, 0);
	}

	public SlideTabPager(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideTabPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setOrientation(VERTICAL);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTabWidget = (TabWidget) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pagers);

		if (mTabWidget == null || mViewPager == null) {
			throw new RuntimeException(
					"Your content must have a TabWidget whose id attribute is "
							+ "'R.id.tabs' and a ViewPager whose id attribute is "
							+ "'R.id.pagers'");
		}

		handTabListener();
		mViewPager.setOnPageChangeListener(this);
	}
	
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		

	}

	public void setTabAdapter(Adapter adapter) {
		if (mTabWidget != null) {
			mTabWidget.removeAllViews();

			int count = adapter.getCount();
			for (int i = 0; i != count; i++) {
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

	public void setOnPageChangedListener(OnPageChangedListener listener) {
		mOnPageChangedListener = listener;
		
		if(listener != null){
			listener.onPageChanged(mViewPager.getCurrentItem());
		}
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

	@Override
	public void onPageSelected(int index) {
		Log.i(TAG, "@Method --> onPageSelected: " + index);

		mTabWidget.setCurrentTab(index);

		if (mOnPageChangedListener != null) {
			mOnPageChangedListener.onPageChanged(index);
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

	/**
	 * implements interface "TabWidget.OnTabSelectionChanged" using reflection
	 * 
	 * @author swordy
	 */
	private class OnTabSelectionChangedImpl implements InvocationHandler {

		@SuppressWarnings("unused")
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			if ("onTabSelectionChanged".equals(method.getName())
					&& args != null && args.length == 2) {
				int tabIndex = (Integer) args[0];
				boolean clicked = (Boolean) args[1];

				Log.i(TAG, "@Method --> OnTabSelectionChanged: " + tabIndex);

				mViewPager.setCurrentItem(tabIndex);
			}

			return null/* void */;
		}

	}
}
