package com.daiyan.handwork.app.widget;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Gallery;

public class GuideGallery extends Gallery implements OnGestureListener {
	private ViewPager mPager;

	public ViewPager getmPager() {
		return mPager;
	}

	public void setmPager(ViewPager mPager) {
		this.mPager = mPager;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public GuideGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 *            &nbsp;&nbsp;&nbsp; &nbsp; &nbsp;&nbsp;&nbsp;
	 */
	public GuideGallery(Context context, AttributeSet attrs) {
		super(context, attrs); // TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mPager != null)
			mPager.requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mPager != null)
			mPager.requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mPager != null)
			mPager.requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(event);
	}
}
