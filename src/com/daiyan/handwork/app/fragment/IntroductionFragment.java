package com.daiyan.handwork.app.fragment;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.app.activity.Homepage;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 主页-简介
 * @author 魏工
 * @Date 2015-05-06
 */
public class IntroductionFragment extends BaseFragment {

	private Activity mContext;
	private Resources mResources;
	
	/** 首页布局视图 */
	private View mParentView;
	
	private WebView detailWebView;
	
	
	
	public IntroductionFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mParentView == null) {
			mParentView = inflater.inflate(R.layout.fragment_introduction, container, false);
			mContext = getActivity();
			mResources = getResources();

			initViews();
		}
		
		ViewGroup parent = (ViewGroup) mParentView.getParent();
		if (parent != null) {
			parent.removeView(mParentView);
		}
		return mParentView;
	}

	private void initViews()
	{
		detailWebView = (WebView) mParentView.findViewById(R.id.detailWebView);
		
		detailWebView.setVerticalScrollBarEnabled(true);
		detailWebView.setHorizontalScrollBarEnabled(false);
		detailWebView.getSettings().setSupportZoom(false);
		detailWebView.getSettings().setBuiltInZoomControls(false);
		detailWebView.getSettings().setJavaScriptEnabled(true);
		detailWebView.requestFocus();
		
		detailWebView.getSettings().setUseWideViewPort(true);
		detailWebView.getSettings().setLoadWithOverviewMode(true);
		detailWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		//detailWebView.loadUrl(Consts.URL_INTRODUCE_BASE + Homepage.mCurrentUserInfo.token);
	}

	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onVisible() {
		// TODO Auto-generated method stub
		super.onVisible();
		
		// 没加载完毕跳转过来避免空指针
		if (Homepage.mCurrentUserInfo != null
			&& !StringUtils.isEmpty(Homepage.mCurrentUserInfo.introduce))
		{
			detailWebView.loadUrl(Homepage.mCurrentUserInfo.introduce);
		}
	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
	}
}
