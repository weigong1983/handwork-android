package com.daiyan.handwork.app.activity;

import java.util.HashMap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

/**
 * 设置
 * @author AA
 * @Date 2015-02-03
 */
public class Setting extends BaseActivity implements OnClickListener{
	private Activity mContext;
	
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private LinearLayout personalInfoLinearLayout;
	private LinearLayout modifyPasswordLinearLayout;
	private LinearLayout shareAppLinearLayout;
	private LinearLayout aboutUsLinearLayout;
	private LinearLayout logoutLinearLayout;
	
	private TextView logoutTextView;
	
	/** 调用退出登录接口返回数据 */
	private HashMap<String, Object> mLogoutDatas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_setting);
		mContext = Setting.this;
		DataServer.getInstance().initialize(mContext);
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		personalInfoLinearLayout = (LinearLayout)findViewById(R.id.personalInfoLinearLayout);
		personalInfoLinearLayout.setOnClickListener(this);
		modifyPasswordLinearLayout = (LinearLayout)findViewById(R.id.modifyPasswordLinearLayout);
		modifyPasswordLinearLayout.setOnClickListener(this);
		shareAppLinearLayout = (LinearLayout)findViewById(R.id.shareAppLinearLayout);
		shareAppLinearLayout.setOnClickListener(this);
		aboutUsLinearLayout = (LinearLayout)findViewById(R.id.aboutUsLinearLayout);
		aboutUsLinearLayout.setOnClickListener(this);
		logoutLinearLayout = (LinearLayout)findViewById(R.id.logoutLinearLayout);
		logoutLinearLayout.setOnClickListener(this);
		logoutTextView = (TextView)findViewById(R.id.logoutTextView);
		
//		// 判断游客身份
//		if (Consts.IS_GUEST(mContext))
//			logoutTextView.setText(R.string.guest_register_right_now);
//		else
			logoutTextView.setText(R.string.setting_logout);
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.setting_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		case R.id.personalInfoLinearLayout: // 个人资料
			int isAuth = Integer.parseInt(LocationUtil.readInit(mContext, Consts.IS_AUTH, "0"));
			if (isAuth == 1)
			{
				UIHelper.showPersonalInfo(mContext, 1);
			}
			else 
			{
				UIHelper.showPersonalInfo(mContext, 0);
			}
			break;
		case R.id.modifyPasswordLinearLayout: // 修改密码
			// 游客禁止修改密码
			if (Consts.IS_GUEST(mContext))
				return ;
			
			UIHelper.showModifyPassword(mContext);
			break;
		case R.id.aboutUsLinearLayout: // 关于我们
			UIHelper.showAboutUs(mContext);
			break;
		case R.id.logoutLinearLayout: // 退出登录
//			// 游客检测
//			if (Consts.IS_GUEST(mContext))
//			{
//				UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_NORMAL);
//			}
//			else
			{
				String tips = this.getResources().getString(R.string.main_exit_app_tips);
				UIHelper.showDialogForSingleText(mContext, tips, logoutDialogClickListener);
			}
			break;
		case R.id.shareAppLinearLayout: // 分享软件
			UIHelper.showWebView(mContext, Consts.URL_APP_DOWNLOAD, Consts.WEBVIEW_PAGE_SHARE_APP);
			break;
		}
	}
	
	private OnClickListener logoutDialogClickListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
			UIHelper.hideDialogForSingleText();
			new LogoutTask().execute();
			//UIHelper.showLogin(mContext);
		}};
		
		/**
		 * 调用注销登录接口
		 * @author AA
		 * @Date 2014-12-29
		 */
		private class LogoutTask extends AsyncTask<Void, Void, Boolean> {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					mLogoutDatas = DataServer.getInstance().logout();
					return mLogoutDatas != null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean isSuccess) {
				if(isSuccess) {
					// 注销并跳转到登录界面
					//LocationUtil.writeInit(mContext, Consts.KEY_AUTO_LOGIN, false);
					UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_LOGOUT);
				} else {
					ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
				}
			}
		}
	
}
