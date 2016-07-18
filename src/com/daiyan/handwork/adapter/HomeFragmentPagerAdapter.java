package com.daiyan.handwork.adapter;

import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
	private static final int TAB_COUNT = 3;

	private List<Fragment> mFragmentList;
	
	
	public HomeFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.mFragmentList = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return (mFragmentList == null || mFragmentList.size() < TAB_COUNT) ? null : mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList == null ? 0 : mFragmentList.size();
	}

}
