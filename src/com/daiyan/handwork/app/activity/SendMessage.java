package com.daiyan.handwork.app.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
 * 发送留言
 * @author 魏工
 * @Date 2015年05月08日
 */
public class SendMessage extends BaseActivity implements OnClickListener{

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private Activity mContext;
	private Resources mResources;
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	private EditText messageContentEditText; // 留言内容输入框
	private String messageContent; // 留言内容
	
	private Button confirmBtn; // 确定按钮
	
	private String mWorksId = "";// 作品ID 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_send_message);
		mContext = SendMessage.this;
		mWorksId = getIntent().getStringExtra(Consts.EXTRA_WORKS_ID);
		
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		initTitleBar();
		
		messageContentEditText = (EditText) findViewById(R.id.id_et_message_content);
		confirmBtn = (Button) findViewById(R.id.id_btn_confirm);
		confirmBtn.setOnClickListener(this);
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.send_message_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	/**
	 * 发送留言
	 */
	private void publish() {
		//获取并验证用户输入
		messageContent = messageContentEditText.getText().toString();
		if(StringUtils.isEmpty(messageContent)) {
			ToastUtils.show(mContext, mResources.getString(R.string.send_message_content_empty_tips));
			messageContentEditText.requestFocus();
			messageContentEditText.setFocusable(true);
			messageContentEditText.setFocusableInTouchMode(true);
			return;
		}
		
		new LeaveMessageTask().execute();
	}
	
	private class LeaveMessageTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext,
					mResources.getString(R.string.send_message_loading_text), false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(500);
				mDatas = DataServer.getInstance().leaveMessage(mWorksId, messageContent);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			if(isSuccess) {
				ToastUtils.show(mContext, mResources.getString(R.string.send_message_success_tips));
				finish();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);				
			}
		}		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		case R.id.id_btn_confirm:
			publish();
			break;
		}
	}
}
