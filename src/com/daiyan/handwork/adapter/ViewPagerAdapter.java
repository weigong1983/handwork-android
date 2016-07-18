package com.daiyan.handwork.adapter;

import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 发提问适配器
 * @author AA
 *
 */
public class ViewPagerAdapter extends PagerAdapter {

	private List<View> mList; 
	
	public ViewPagerAdapter(List<View> list) {
		this.mList = list;
		
	}
	
	@Override
	public int getCount() {
		
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0 == (arg1);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(mList.get(position));
		return mList.get(position);
	}

	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView(mList.get(position));		
	}
}
