package com.daiyan.handwork.app;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

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
