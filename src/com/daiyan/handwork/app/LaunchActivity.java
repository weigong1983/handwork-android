package com.daiyan.handwork.app;

import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SystemBarTintManager;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * 启动界面
 * @author AA
 *
 */
public class LaunchActivity extends Activity {

	private Activity mContext;
	private boolean mIsFirstLoad = true;
	private boolean mIsAutoLogin = false;
	private boolean mIsGuest = false;
	private HashMap<String, Object> mDatas;
	
	// 本地记录的登录账号和密码
	private String mLocalUserName;
	private String mLocalPassword;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		//不显示系统的标题栏          
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                              WindowManager.LayoutParams.FLAG_FULLSCREEN );
		
		mContext = LaunchActivity.this;
		DataServer.getInstance().initialize(mContext);
		
		mIsFirstLoad = LocationUtil.readInit(mContext, Consts.KEY_APP_FIRST_LOAD, true);
		mIsAutoLogin = LocationUtil.readInit(mContext, Consts.KEY_AUTO_LOGIN, false);
		mIsGuest = Consts.IS_GUEST(mContext);
		
		
		/**
		 * 程序启动处理逻辑
		 */
		// 1. 如果程序是首次运行，将跳转至引导页并且将首次运行标志设置为false, 并以游客身份自动登录
		if(mIsFirstLoad) 
		{
			LocationUtil.writeInit(mContext, Consts.KEY_APP_FIRST_LOAD, false);
			new ShowWelcomeTask().execute();
		}
		else 
		{
			// 游客登录
			if (mIsGuest)
			{
				new GuestLoginTask().execute();
				return ;
			}
			
			// 2. 如果程序非首次运行且记住密码自动登录，将调用登录接口并且保存返回数据，最后跳过登录界面直接进入首页
			if(mIsAutoLogin)
			{
				mLocalUserName = LocationUtil.readInit(mContext, Consts.UNAME, "");
				mLocalPassword = LocationUtil.readInit(mContext, Consts.PASSWORD, "");
				new LoginTask().execute();
			}
			else
			{
				new LaunchLoginTask().execute();
			}
		}
    }
    
    /**
     * 游客登录
     * @author weigong
     *
     */
    private class GuestLoginTask extends AsyncTask<Void, Void, Boolean> {
    	@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				//mDatas = DataServer.getInstance().login(Consts.GUEST_UNAME, Consts.GUEST_PASSWORD);
				String deviceId = SystemUtils.getDeviceId(mContext);
				mDatas = DataServer.getInstance().loginGuest(deviceId);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			// 获取数据成功并且当前为自动登录
			if (isSuccess) {
				// 将接口返回数据保存至本地
				LocationUtil.writeInit(
						mContext,
						new String[] { Consts.SECRET, Consts.TOKEN, Consts.UID,
								Consts.PHOTO, Consts.PHONE, Consts.NICKNAME,
								Consts.REALNAME, Consts.SIGNATURE,
								Consts.PROVINCE, Consts.CITY, Consts.DISTINCT,
								Consts.JOB, Consts.CALLNAME, Consts.CATEGORY,
								Consts.IS_AUTH, Consts.INTANGIBLEHERITAGE,
								Consts.WORK_AGE, Consts.AID,
								Consts.ASSOCIATION, Consts.INTRODUCE,
								Consts.VOICE_PATH },

						new String[] {
								mDatas.get(Consts.SECRET).toString(),
								mDatas.get(Consts.TOKEN).toString(),
								mDatas.get(Consts.UID).toString(),
								mDatas.get(Consts.PHOTO).toString(),
								mDatas.get(Consts.PHONE).toString(),
								mDatas.get(Consts.NICKNAME).toString(),
								mDatas.get(Consts.REALNAME).toString(),
								mDatas.get(Consts.SIGNATURE).toString(),
								mDatas.get(Consts.PROVINCE).toString(),
								mDatas.get(Consts.CITY).toString(),
								mDatas.get(Consts.DISTINCT).toString(),
								mDatas.get(Consts.JOB).toString(),
								mDatas.get(Consts.CALLNAME).toString(),
								mDatas.get(Consts.CATEGORY).toString(),
								mDatas.get(Consts.IS_AUTH).toString(),
								mDatas.get(Consts.INTANGIBLEHERITAGE)
										.toString(),
								mDatas.get(Consts.WORK_AGE).toString(),
								mDatas.get(Consts.AID).toString(),
								mDatas.get(Consts.ASSOCIATION).toString(),
								mDatas.get(Consts.INTRODUCE).toString(),
								mDatas.get(Consts.VOICE_PATH).toString() });

				//将用户名和密码保存到本地
				LocationUtil.writeInit(mContext, Consts.UNAME, "");
				LocationUtil.writeInit(mContext, Consts.PASSWORD, "");
				LocationUtil.writeInit(mContext, Consts.KEY_AUTO_LOGIN, false); // 游客不自动登录
				
				//进入首页
				UIHelper.showMain(mContext);
				
				// 读取所有作品分类
				CategoryManager.getInstance().loadCategoryData();
			}
		}
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
    	@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				mDatas = DataServer.getInstance().login(mLocalUserName, mLocalPassword);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {

			// 获取数据成功并且当前为自动登录
			if (mIsAutoLogin && isSuccess) {
				// 将接口返回数据保存至本地
				LocationUtil.writeInit(
						mContext,
						new String[] { Consts.SECRET, Consts.TOKEN, Consts.UID,
								Consts.PHOTO, Consts.PHONE, Consts.NICKNAME,
								Consts.REALNAME, Consts.SIGNATURE,
								Consts.PROVINCE, Consts.CITY, Consts.DISTINCT,
								Consts.JOB, Consts.CALLNAME, Consts.CATEGORY,
								Consts.IS_AUTH, Consts.INTANGIBLEHERITAGE,
								Consts.WORK_AGE, Consts.AID,
								Consts.ASSOCIATION, Consts.INTRODUCE,
								Consts.VOICE_PATH },

						new String[] {
								mDatas.get(Consts.SECRET).toString(),
								mDatas.get(Consts.TOKEN).toString(),
								mDatas.get(Consts.UID).toString(),
								mDatas.get(Consts.PHOTO).toString(),
								mDatas.get(Consts.PHONE).toString(),
								mDatas.get(Consts.NICKNAME).toString(),
								mDatas.get(Consts.REALNAME).toString(),
								mDatas.get(Consts.SIGNATURE).toString(),
								mDatas.get(Consts.PROVINCE).toString(),
								mDatas.get(Consts.CITY).toString(),
								mDatas.get(Consts.DISTINCT).toString(),
								mDatas.get(Consts.JOB).toString(),
								mDatas.get(Consts.CALLNAME).toString(),
								mDatas.get(Consts.CATEGORY).toString(),
								mDatas.get(Consts.IS_AUTH).toString(),
								mDatas.get(Consts.INTANGIBLEHERITAGE)
										.toString(),
								mDatas.get(Consts.WORK_AGE).toString(),
								mDatas.get(Consts.AID).toString(),
								mDatas.get(Consts.ASSOCIATION).toString(),
								mDatas.get(Consts.INTRODUCE).toString(),
								mDatas.get(Consts.VOICE_PATH).toString() });

				//进入首页
				UIHelper.showMain(mContext);
				
				// 读取所有作品分类
				CategoryManager.getInstance().loadCategoryData();
			}
		}
    }
    
    private class ShowWelcomeTask extends AsyncTask<Void, Void, Boolean> {
    	@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				Thread.sleep(1000);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			mContext.finish();
			UIHelper.showWelcomePage(mContext, true);
		}
    }
    
    private class LaunchLoginTask extends AsyncTask<Void, Void, Boolean> {
    	@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				Thread.sleep(1000);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			mContext.finish();
			UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_LAUNCH);
		}
    }
}
