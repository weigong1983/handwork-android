package com.daiyan.handwork.app.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
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
 * 编辑保存单行内容
 * @author 魏工
 * @Date 2015年05月08日
 */
public class EditSinglelineContent extends BaseActivity implements OnClickListener{

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private Activity mContext;
	private Resources mResources;
	
	private int mEditContentType;
	private String mOldContent;
	private int resultCode;
	
	private String mTitleString;
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	private EditText contentEditText; // 内容输入框
	private String content; // 内容
	
	private Button saveBtn; // 保存按钮

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_edit_sl_content);
		mContext = EditSinglelineContent.this;
		
		mEditContentType = getIntent().getIntExtra(Consts.EXTRA_CONTENT_TYPE, Consts.CONTENT_TYPE_NAME);
		if (mEditContentType == Consts.INFO_TYPE_NAME)
		{
			mTitleString = getResources().getString(R.string.personal_info_name);
			resultCode = Consts.RESULT_CODE_EDIT_NAME;
		}
		else if (mEditContentType == Consts.INFO_TYPE_SIGNATURE)
		{
			mTitleString = getResources().getString(R.string.personal_info_signature);
			resultCode = Consts.RESULT_CODE_EDIT_SIGNATURE;
		}
		else if  (mEditContentType == Consts.INFO_TYPE_ASSOCIATION)
		{
			mTitleString = getResources().getString(R.string.personal_info_association); 
			resultCode = Consts.RESULT_CODE_EDIT_ASSOCIATION;
		}
		
		mOldContent = getIntent().getStringExtra(Consts.EXTRA_OLD_CONTENT);
		initView();
	}
	
	

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		initTitleBar();
		
		contentEditText = (EditText) findViewById(R.id.id_et_content);
		saveBtn = (Button) findViewById(R.id.id_btn_save);
		saveBtn.setOnClickListener(this);
		
		contentEditText.setText(mOldContent);
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(mTitleString);
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			onEditComplete();
			break;
		case R.id.id_btn_save:
			onEditComplete();
			break;
		}
	}
	
	private void onEditComplete()
	{
		Intent intent=new Intent();
        intent.putExtra(Consts.EXTRA_EDIT_CONENT, contentEditText.getText().toString());
        setResult(resultCode, intent);
        finish();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK 
			&& event.getAction() == KeyEvent.ACTION_UP) 
		{
			onEditComplete();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
}
