package com.daiyan.handwork.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;

/**
 * 帮助中心
 * @author AA
 * @Date 2014-11-27
 */
public class HelpCenter extends BaseActivity implements OnClickListener {

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_help_center);
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		
		
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.help_center_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
			
		}
		
	}
	
}