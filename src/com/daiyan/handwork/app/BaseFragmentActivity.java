package com.daiyan.handwork.app;

import com.daiyan.handwork.common.AppManager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity{

	protected void onCreate(Bundle savedInstanceState, int layoutResID) {
		super.onCreate(savedInstanceState);
		setContentView(layoutResID);
		
		//入栈
		AppManager.getInstance().pushActivity(this);
	}
	
	
	@Override
	protected void onDestroy() {
		//退栈
		AppManager.getInstance().popActivity(this);
		
		super.onDestroy();
	}
}
