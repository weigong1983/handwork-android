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
 * 评论界面
 * @author AA
 * @Date 2014-12-23
 */
public class CommentPub extends BaseActivity implements OnClickListener{

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private Activity mContext;
	private Resources mResources;
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	private String mItemID;
	private EditText mCommentEdit;
	private String mComment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_comment_pub);
		mContext = CommentPub.this;
		mItemID = getIntent().getStringExtra(Consts.ITEMID);
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
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
		mTitleTextView.setText(getResources().getString(R.string.comment_pub_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setText(getResources().getString(R.string.comment_pub_right_text));
		mTitleRightTextView.setOnClickListener(this);
	}

	/**
	 * 发表评论
	 */
	private void publish() {
		//获取并验证用户输入
		mCommentEdit = (EditText)findViewById(R.id.id_et_edit_comment);
		mComment = mCommentEdit.getText().toString();
		if(StringUtils.isEmpty(mComment)) {
			ToastUtils.show(mContext, mResources.getString(R.string.comment_pub_empty_tips));
			mCommentEdit.requestFocus();
			mCommentEdit.setFocusable(true);
			mCommentEdit.setFocusableInTouchMode(true);
			return;
		}
		
		new ATask().execute();
	}
	
	private class ATask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.comment_pub_loading_text), false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(500);
				//mDatas = DataServer.getInstance().comment(mItemID, mComment);
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
				setResult(Consts.RESULT_CODE_COMMENT_PUB_OK);
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
		
		case R.id.id_tv_title_right:
			publish();
			break;
		}
	}
}
