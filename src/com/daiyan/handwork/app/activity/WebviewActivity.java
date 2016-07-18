package com.daiyan.handwork.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.framework.PlatformActionListener;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.constant.Consts;

/**
 * 通用浏览器页面
 * @author 魏工
 * @Date 2015-05-06
 */
public class WebviewActivity extends BaseActivity implements OnClickListener
{
	private int mWebPage; // 页面类型
	private String mWebUrl;
	
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	private ImageView mTitleRightBtn;
	
	private WebView detailWebView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_webview);
		
		mWebPage = getIntent().getIntExtra(Consts.EXTRA_WEB_PAGE, Consts.WEBVIEW_PAGE_ACTIVITY_DETAIL);
		mWebUrl = getIntent().getStringExtra(Consts.EXTRA_WEB_URL);
		
		initView();
		detailWebView.loadUrl(mWebUrl);
	}
	
	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		
		detailWebView = (WebView) findViewById(R.id.detailWebView);

		detailWebView.setVerticalScrollBarEnabled(true);
		detailWebView.setHorizontalScrollBarEnabled(false);
		detailWebView.getSettings().setSupportZoom(false);
		detailWebView.getSettings().setBuiltInZoomControls(false);
		detailWebView.getSettings().setJavaScriptEnabled(true);
		detailWebView.requestFocus();
		detailWebView.getSettings().setUseWideViewPort(true);
		detailWebView.getSettings().setLoadWithOverviewMode(true);
		detailWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		// 设置标题文字
		switch (mWebPage)
		{
		case Consts.WEBVIEW_PAGE_ACTIVITY_DETAIL: // 活动详情
			mTitleTextView.setText(getResources().getString(R.string.activity_detail_title));
			break;
		case Consts.WEBVIEW_PAGE_PRODUCTION_PROCESS: // 工艺制作流程
			mTitleTextView.setText(getResources().getString(R.string.works_production_process_title));
			break;
		case Consts.WEBVIEW_PAGE_ART_STANDARD: // 工艺美术标准
			mTitleTextView.setText(getResources().getString(R.string.title_market_standard));
			break;
		case Consts.WEBVIEW_PAGE_SERVICE_TERM: // 服务条款
			mTitleTextView.setText(getResources().getString(R.string.service_term));
			break;
		case Consts.WEBVIEW_PAGE_SHARE_APP: // 分享APP
			mTitleTextView.setText(getResources().getString(R.string.setting_share_app));
			break;
		case Consts.WEBVIEW_PAGE_BUS_GUIDE: // 乘车指引
			mTitleTextView.setText(getResources().getString(R.string.institute_bus));
			break;
		}
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
		
		// 分享按钮
		mTitleRightBtn = (ImageView) findViewById(R.id.id_iv_title_right);
		mTitleRightBtn.setImageResource(R.drawable.title_share);
		mTitleRightBtn.setOnClickListener(this);
		
		// 暂时只允许分享软件下载地址
		if (mWebPage == Consts.WEBVIEW_PAGE_SHARE_APP)
			mTitleRightBtn.setVisibility(View.VISIBLE);
		else
			mTitleRightBtn.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		case R.id.id_iv_title_right: // 分享
			String shareTitle = this.getString(R.string.app_name);
			String shareContent = "我正在使用手作品App，它是国内领先的手工艺O2O平台。";
			String shareUrl = mWebUrl;
			String []imageArray = new String[]{""};
			UIHelper.showShare(this, shareTitle, shareUrl, shareContent, imageArray, 23.103969f, 113.375496f);
			break;
		}
	}
}