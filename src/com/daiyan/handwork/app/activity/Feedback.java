package com.daiyan.handwork.app.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.ToastUtils;

/**
 * 意见反馈
 * @author AA
 * @Date 2014-11-26
 */
public class Feedback extends BaseActivity implements OnClickListener{

	//标题栏
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private Activity mContext;
	private Resources mResources;
	
	private EditText mContentEdit;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_feedback);
		mContext = Feedback.this;
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		DataServer.getInstance().initialize(mContext);
		initTitleBar();
		mContentEdit = (EditText)findViewById(R.id.id_et_feedback);
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.feedback_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setText(getResources().getString(R.string.feedback_title_right_text));
		mTitleRightTextView.setOnClickListener(this);
	}
	
	/**
	 * 发表意见反馈
	 */
	private void publish() {
		final String content = mContentEdit.getText().toString();
		if(StringUtils.isEmpty(content)) {
			ToastUtils.show(mContext, R.string.feedback_empty_tips);
			mContentEdit.requestFocus();
			mContentEdit.setFocusable(true);
			mContentEdit.setFocusableInTouchMode(true);
			return;
		}
		
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.feedback_loading_text), false);				
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					//Thread.sleep(500);
					HashMap<String, Object> data = DataServer.getInstance().feedback(content);
					return data != null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean isSuccess) {
				UIHelper.hideDialogForLoading();
				if(isSuccess) {
					ToastUtils.show(Feedback.this, "反馈成功，感谢您的宝贵意见！");
					finish();
				} else {
					ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
				}
			}
			
			
			
		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		
		case R.id.id_tv_title_right:
			publish();
			break;
		}
	}
}