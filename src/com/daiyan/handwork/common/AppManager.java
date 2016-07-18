package com.daiyan.handwork.common;

import java.util.Stack;
import android.app.Activity;

/**
 * 用于Activity管理和应用程序退出
 * @author AA
 * @date 2014-10-23
 *
 */
public class AppManager {

	private static Stack<Activity> activityStack;
	private static AppManager instance;
	
	private AppManager() {}
	
	
	/**
	 * 获得单一实例
	 * @return
	 */
	public static AppManager getInstance() {
		if(instance == null) {
			instance = new AppManager();
		}
		return instance;
	}
	
	
	/**
	 * 结束当前activity（堆栈中最后一个压入的）
	 */
	public void popActivity() {
		Activity activity = activityStack.lastElement();
		if(activity != null) {
			activity.finish();
			activity = null;
		}
	}
	
	
	/**
	 * 结束指定Activity
	 * @param activity
	 */
	public void popActivity(Activity activity) {
		if(activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}
	
	
	/**
	 * 获得当前Activity（堆栈中最后一个压入的）
	 * @return
	 */
	public Activity currentActivity() {
		if(activityStack.size() == 0) {
			return null;
		}
		return activityStack.lastElement();
	}
	
	
	/**
	 * 添加Activity到堆栈
	 * @param activity
	 */
	public void pushActivity(Activity activity) {
		if(activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	
	/**
	 * 结束所有Activity除了指定一个
	 * @param cls
	 */
	public void popAllActivityExceptOne(Class cls) {
		while(true) {
			Activity activity = currentActivity();
			if(activity == null) {
				break;
			}
			if(activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}
	
	
	/**
	 * 退出应用程序
	 */
	public void exitApp() {
		while(true) {
			Activity activity = currentActivity();
			if(activity == null) {
				break;
			}
			popActivity(activity);
		}
	}
}
