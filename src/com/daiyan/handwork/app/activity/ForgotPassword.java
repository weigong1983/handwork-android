package com.daiyan.handwork.app.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.SystemBarTintManager;
import com.daiyan.handwork.utils.ToastUtils;

/**
 * 忘记密码界面
 * 
 * @author AA
 * @Date 2014-11-23
 */
public class ForgotPassword extends BaseActivity implements OnClickListener {        
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;

	private Activity mContext;
	private Resources mResources;

	private EditText mMobileEdit;
	private String mMobile;
	private EditText mValidCodeEdit;
	private String mValidCode;
	private EditText mNewPwdEdit;
	private String mNewPwd;
	private EditText mConfirmPwdEdit;
	private String mConfirmPwd;
	
	private ImageButton mMobileEditClear;
	private ImageButton mNewPasswordEditClear;
	private ImageButton mConfirmPasswordEditClear;
	
	/** 获取验证码按钮 */
	private Button mGetValidCodeBtn;
	
	/** 确定按钮 */
	private Button mConfirmBtn;

	/** 从服务器接收的数据 */
	private HashMap<String, Object> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_forgot_password);
		// 设置沉浸式状态栏
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// 创建状态栏的管理实例  
		    SystemBarTintManager tintManager = new SystemBarTintManager(this);  
		    // 激活状态栏设置  
		    tintManager.setStatusBarTintEnabled(true);  
		    // 激活导航栏设置  
		    tintManager.setNavigationBarTintEnabled(true); 
		    // 设置一个颜色给系统栏  
		    tintManager.setTintColor(this.getResources().getColor(R.color.black));  
		}
		mContext = ForgotPassword.this;
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		DataServer.getInstance().initialize(mContext);
		initTitleBar();

		mMobileEdit = (EditText) findViewById(R.id.id_et_mobile);
		mValidCodeEdit = (EditText) findViewById(R.id.id_et_validcode);
		mNewPwdEdit = (EditText) findViewById(R.id.id_et_new_pwd);
		mConfirmPwdEdit = (EditText) findViewById(R.id.id_et_confirm_pwd);

		mMobileEditClear = (ImageButton) findViewById(R.id.id_ib_mobile_clear);
		mMobileEditClear.setOnClickListener(this);
		
		mNewPasswordEditClear = (ImageButton) findViewById(R.id.id_ib_new_pwd_clear);
		mNewPasswordEditClear.setOnClickListener(this);
		
		mConfirmPasswordEditClear = (ImageButton) findViewById(R.id.id_ib_confirm_pwd_clear);
		mConfirmPasswordEditClear.setOnClickListener(this);
		
		mConfirmBtn = (Button) findViewById(R.id.id_btn_confirm);
		mConfirmBtn.setOnClickListener(this);
		
		mGetValidCodeBtn = (Button) findViewById(R.id.id_ib_get_validcode);
		mGetValidCodeBtn.setOnClickListener(this);
		
		//监听编辑框焦点变化
		mMobileEdit.setOnFocusChangeListener(onFocusChangeListener);
		mNewPwdEdit.setOnFocusChangeListener(onFocusChangeListener);
		mConfirmPwdEdit.setOnFocusChangeListener(onFocusChangeListener);
		
		mMobileEdit.addTextChangedListener(mobileTextWatcher);
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
			case R.id.id_et_mobile:
				if (hasFocus && focusEditText.length() > 0)
					mMobileEditClear.setVisibility(View.VISIBLE);
				else
					mMobileEditClear.setVisibility(View.GONE);
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
	private  TextWatcher mobileTextWatcher = new TextWatcher(){
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	boolean hasContent = (boolean)(s.length() > 0);
	        	mMobileEditClear.setVisibility(hasContent ? View.VISIBLE : View.GONE);
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
				.getString(R.string.forgot_password_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	/**
	 * 忘记密码
	 */
	private void forgotPassword() {
		mMobile = mMobileEdit.getText().toString();
		mNewPwd = mNewPwdEdit.getText().toString();
		mConfirmPwd = mConfirmPwdEdit.getText().toString();
		mValidCode = mValidCodeEdit.getText().toString();
		
		// 验证用户输入
		if(StringUtils.isEmpty(mMobile) || !StringUtils.isMobileNumber(mMobile)){
			ToastUtils.show(mContext, mResources.getString(R.string.register_mobile_wrong_tips));
			mMobileEdit.setFocusable(true);
			mMobileEdit.setFocusableInTouchMode(true);
			mMobileEdit.requestFocus();
			return;
		} 
		else if (StringUtils.isEmpty(mValidCode)) {
			ToastUtils.show(mContext, mResources
					.getString(R.string.register_validcode_empty_wrong_tips));
			mValidCodeEdit.setFocusable(true);
			mValidCodeEdit.setFocusableInTouchMode(true);
			mValidCodeEdit.requestFocus();
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
		
		new ForgotPasswordTask().execute();
	}

	/**
	 * 调用修改密码接口
	 * @author 魏工
	 * @Date 2015-05-13
	 */
	private class ForgotPasswordTask extends AsyncTask<String, Integer, Boolean> {

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
				mDatas = DataServer.getInstance().forgetPassword(mMobile, mValidCode, mNewPwd);
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
				ToastUtils.show(mContext, mResources.getString(R.string.reset_pwd_success_tips));
				LocationUtil.writeInit(mContext, Consts.PASSWORD, "");
				mContext.finish();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}

	/**
	 * 获取验证码
	 */
	private void getValidCode() {
		mMobile = mMobileEdit.getText().toString();
		// 验证用户输入
		if(StringUtils.isEmpty(mMobile) || !StringUtils.isMobileNumber(mMobile)){
			ToastUtils.show(mContext, mResources.getString(R.string.register_mobile_wrong_tips));
			mMobileEdit.setFocusable(true);
			mMobileEdit.setFocusableInTouchMode(true);
			mMobileEdit.requestFocus();
			return;
		} 
		
		new GetCodeTask().execute();
	}
	
	/**
	 * 调用获取短信验证码接口
	 * @author AA
	 * @Date 2015-02-04
	 */
	private class GetCodeTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext,
					mResources.getString(R.string.get_validcod_loading_text), false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(500);
				mDatas = DataServer.getInstance().getCode(mMobile);
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
				ToastUtils.show(mContext, mResources.getString(R.string.get_validcod_success_tips));
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_mobile_clear:
			mMobileEdit.setText("");
			mMobileEditClear.setVisibility(View.INVISIBLE);
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
			UIHelper.hideSoftInput(this, mMobileEdit);
			UIHelper.hideSoftInput(this, mNewPwdEdit);
			UIHelper.hideSoftInput(this, mConfirmPwdEdit);
			UIHelper.hideSoftInput(this, mValidCodeEdit);

			forgotPassword();
			break;
		case R.id.id_ib_get_validcode: // 获取短信验证码
			getValidCode();
			break;
		}
	}
}
