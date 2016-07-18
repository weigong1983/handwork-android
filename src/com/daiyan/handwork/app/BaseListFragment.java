package com.daiyan.handwork.app;

import android.support.v4.app.ListFragment;

public abstract class BaseListFragment extends ListFragment {

	protected boolean isVisible;
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if(getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
		
	}

	protected void onVisible() {
		lazyLoad();
	}
	
	protected abstract void lazyLoad();
	
	protected void onInvisible() {}	
}
