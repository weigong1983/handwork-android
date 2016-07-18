package com.daiyan.handwork.app.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.app.LaunchActivity;
import com.daiyan.handwork.bean.LoginUsers;
import com.daiyan.handwork.common.DBManager;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.ToastUtils;

/**
 * 修改密码界面
 * 
 * @author 魏工
 * @Date 2015年05月10日
 */
public class ModifyPassword extends BaseActivity implements OnClickListener {        
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;

	private Activity mContext;
	private Resources mResources;

	private EditText mOldPasswordEdit;
	private String mOldPassword;
	private EditText mNewPwdEdit;
	private String mNewPwd;
	private EditText mConfirmPwdEdit;
	private String mConfirmPwd;
	
	private ImageButton mOldPasswordEditClear;
	private ImageButton mNewPasswordEditClear;
	private ImageButton mConfirmPasswordEditClear;
	
	
	/** 确定按钮 */
	private Button mConfirmBtn;

	/** 从服务器接收的数据 */
	private HashMap<String, Object> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_modify_password);
		mContext = ModifyPassword.this;
		initView();
		DataServer.getInstance().initialize(mContext);
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		initTitleBar();

		mOldPasswordEdit = (EditText) findViewById(R.id.id_et_old_password);
		mNewPwdEdit = (EditText) findViewById(R.id.id_et_new_pwd);
		mConfirmPwdEdit = (EditText) findViewById(R.id.id_et_confirm_pwd);

		mOldPasswordEditClear = (ImageButton) findViewById(R.id.id_ib_old_password_clear);
		mOldPasswordEditClear.setOnClickListener(this);
		
		mNewPasswordEditClear = (ImageButton) findViewById(R.id.id_ib_new_pwd_clear);
		mNewPasswordEditClear.setOnClickListener(this);
		
		mConfirmPasswordEditClear = (ImageButton) findViewById(R.id.id_ib_confirm_pwd_clear);
		mConfirmPasswordEditClear.setOnClickListener(this);
		
		mConfirmBtn = (Button) findViewById(R.id.id_btn_confirm);
		mConfirmBtn.setOnClickListener(this);
		
		//监听编辑框焦点变化
		mOldPasswordEdit.setOnFocusChangeListener(onFocusChangeListener);
		mNewPwdEdit.setOnFocusChangeListener(onFocusChangeListener);
		mConfirmPwdEdit.setOnFocusChangeListener(onFocusChangeListener);
		
		mOldPasswordEdit.addTextChangedListener(oldPasswordTextWatcher);
		mNewPwdEdit.addTextChangedListener(newPasswordTextWatcher);
		mConfirmPwdEdit.addTextChangedListener(confirmPasswordTextWatcher);
	}

	/**
	 * 编辑框焦点监听器
	 */
	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			EditText focusEditText = (EditText)v;
			switch (v.getId()) {
			case R.id.id_et_old_password:
				if (hasFocus && focusEditText.length() > 0)
					mOldPasswordEditClear.setVisibility(View.VISIBLE);
				else
					mOldPasswordEditClear.setVisibility(View.GONE);
				break;
			case R.id.id_et_new_pwd:
				if (hasFocus && focusEditText.length() > 0)
					mNewPasswordEditClear.setVisibility(View.VISIBLE);
				else
					mNewPasswordEditClear.setVisibility(View.GONE);
				break;
			case R.id.id_et_confirm_pwd:
				if (hasFocus && focusEditText.length() > 0)
					mConfirmPasswordEditClear.setVisibility(View.VISIBLE);
				else
					mConfirmPasswordEditClear.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 编辑框内容变化监听器
	 */
	private  TextWatcher oldPasswordTextWatcher = new TextWatcher(){
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	boolean hasContent = (boolean)(s.length() > 0);
	        	mOldPasswordEditClear.setVisibility(hasContent ? View.VISIBLE : View.GONE);
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	        }
	    };
	    
	    private  TextWatcher newPasswordTextWatcher = new TextWatcher(){
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	boolean hasContent = (boolean)(s.length() > 0);
	        	mNewPasswordEditClear.setVisibility(hasContent ? View.VISIBLE : View.GONE);
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	        }
	    };
	    
	    private  TextWatcher confirmPasswordTextWatcher = new TextWatcher(){
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	boolean hasContent = (boolean)(s.length() > 0);
	        	mConfirmPasswordEditClear.setVisibility(hasContent ? View.VISIBLE : View.GONE);
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	        }
	    };
	
	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources()
				.getString(R.string.setting_modify_password));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	/**
	 * 忘记密码
	 */
	private void modifyPassword() {
		mOldPassword = mOldPasswordEdit.getText().toString();
		mNewPwd = mNewPwdEdit.getText().toString();
		mConfirmPwd = mConfirmPwdEdit.getText().toString();
		
		// 验证用户输入
		if(StringUtils.isEmpty(mOldPassword) ){
			ToastUtils.show(mContext, mResources.getString(R.string.input_old_pwd_hint));
			mOldPasswordEdit.setFocusable(true);
			mOldPasswordEdit.setFocusableInTouchMode(true);
			mOldPasswordEdit.requestFocus();
			return;
		} 
		else if (StringUtils.isEmpty(mNewPwd)) {
			ToastUtils.show(mContext, mResources
					.getString(R.string.input_new_pwd_hint));
			mNewPwdEdit.setFocusable(true);
			mNewPwdEdit.setFocusableInTouchMode(true);
			mNewPwdEdit.requestFocus();
			return;
		}
		else if (StringUtils.isEmpty(mConfirmPwd)) {
			ToastUtils.show(mContext, mResources
					.getString(R.string.input_confirm_pwd_hint));
			mConfirmPwdEdit.setFocusable(true);
			mConfirmPwdEdit.setFocusableInTouchMode(true);
			mConfirmPwdEdit.requestFocus();
			return;
		}
		else if (!mNewPwd.equalsIgnoreCase(mConfirmPwd)) {
			ToastUtils.show(mContext, mResources
					.getString(R.string.input_pwd_not_differ_wrong_hint));
			return;
		}
		
		new ModifyPasswordTask().execute();
	}

	/**
	 * 调用修改密码接口
	 * @author 魏工
	 * @Date 2015-05-13
	 */
	private class ModifyPasswordTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext,
					mResources.getString(R.string.modify_loading_text), false);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//Thread.sleep(500);
				mDatas = DataServer.getInstance().updatePassword(mOldPassword, mNewPwd);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			if (isSuccess) {
				// 清除缓存旧密码，重新登录
				ToastUtils.show(mContext, mResources.getString(R.string.modify_success_tips));
				mContext.finish();
				LocationUtil.writeInit(mContext, Consts.PASSWORD, "");
				UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_MODIFY_PASSWORD);	
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_old_password_clear:
			mOldPasswordEdit.setText("");
			mOldPasswordEditClear.setVisibility(View.INVISIBLE);
			break;
		case R.id.id_ib_new_pwd_clear:
			mNewPwdEdit.setText("");
			mNewPasswordEditClear.setVisibility(View.INVISIBLE);
			break;
		case R.id.id_ib_confirm_pwd_clear:
			mConfirmPwdEdit.setText("");
			mConfirmPasswordEditClear.setVisibility(View.INVISIBLE);
			break;
		case R.id.id_ib_title_left:
			finish();
			break;
		case R.id.id_btn_confirm:
			// 隐藏键盘
			UIHelper.hideSoftInput(this, mOldPasswordEdit);
			UIHelper.hideSoftInput(this, mNewPwdEdit);
			UIHelper.hideSoftInput(this, mConfirmPwdEdit);

			modifyPassword();
			break;
		}
	}
}
