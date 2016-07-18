package com.daiyan.handwork.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SystemUtils;

/**
 * 关于代言
 * @author AA
 * @Date 2014-11-26
 */
public class AboutUs extends BaseActivity implements OnClickListener{

	private Activity mContext;
	
	
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private TextView versionTextView; // 版本号
	private LinearLayout welcomeLinearLayout; // 欢迎页
	private LinearLayout serviceTermLinearLayout; // 服务条款
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_about_us);
		mContext = AboutUs.this;
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		
		versionTextView = (TextView) findViewById(R.id.versionTextView);
		welcomeLinearLayout = (LinearLayout) findViewById(R.id.welcomeLinearLayout);
		serviceTermLinearLayout = (LinearLayout) findViewById(R.id.serviceTermLinearLayout);
		
		welcomeLinearLayout.setOnClickListener(this);
		serviceTermLinearLayout.setOnClickListener(this);
		
		versionTextView.setText(SystemUtils.getVersion(this));
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.setting_about_us));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		case R.id.welcomeLinearLayout:
			UIHelper.showWelcomePage(mContext, false);
			break;
		case R.id.serviceTermLinearLayout:
			String token = LocationUtil.readInit(mContext, Consts.TOKEN, "");
			String webUrl = Consts.URL_SERVICE_TERM_BASE + token;
			UIHelper.showWebView(mContext, webUrl, Consts.WEBVIEW_PAGE_SERVICE_TERM);
			break;
		}
	}
}