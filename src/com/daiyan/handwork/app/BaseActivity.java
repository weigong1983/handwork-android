package com.daiyan.handwork.app;

import com.daiyan.handwork.R;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.utils.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class BaseActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState, int layoutResID) {
		
		// 设置沉浸式状态栏
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// 创建状态栏的管理实例  
		    SystemBarTintManager tintManager = new SystemBarTintManager(this);  
		    // 激活状态栏设置  
		    tintManager.setStatusBarTintEnabled(true);  
		    // 激活导航栏设置  
		    tintManager.setNavigationBarTintEnabled(true); 
		    // 设置一个颜色给系统栏  
		    tintManager.setTintColor(this.getResources().getColor(R.color.black));  
		}
		
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
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	
	
}
