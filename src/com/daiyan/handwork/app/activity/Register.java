package com.daiyan.handwork.app.activity;

import java.util.HashMap;
import java.util.List;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.bean.LoginUsers;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.DBManager;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.SystemBarTintManager;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

/**
 * 注册界面
 * 
 * @author AA
 * @Date 2014-11-23
 */
public class Register extends BaseActivity implements OnClickListener {        
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;

	private Activity mContext;
	private Resources mResources;

	private EditText mMobileEdit;
	private String mMobile;
	private EditText mPwdEdit;
	private String mPwd;
	private EditText mValidCodeEdit;
	private String mValidCode;
	
	private ImageButton mMobileEditClear;
	private ImageButton mPasswordEditClear;
	
	/** 获取验证码按钮 */
	private Button mGetValidCodeBtn;
	
	/** 注册按钮 */
	private Button mRegisterBtn;
	
	/** 同意服务条款 */
	private CheckBox agreeSSCheckBox;
	private TextView viewSSTextView;


	/** 从服务器接收的数据 */
	private HashMap<String, Object> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_register);
		
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
				
		mContext = Register.this;
		DataServer.getInstance().initialize(mContext);
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		initTitleBar();

		mMobileEdit = (EditText) findViewById(R.id.id_et_mobile);
		mPwdEdit = (EditText) findViewById(R.id.id_et_pwd);
		mValidCodeEdit = (EditText) findViewById(R.id.id_et_validcode);

		mMobileEditClear = (ImageButton) findViewById(R.id.id_ib_mobile_clear);
		mMobileEditClear.setOnClickListener(this);
		
		mPasswordEditClear = (ImageButton) findViewById(R.id.id_ib_pwd_clear);
		mPasswordEditClear.setOnClickListener(this);
		
		mRegisterBtn = (Button) findViewById(R.id.id_btn_register);
		mRegisterBtn.setOnClickListener(this);
		
		mGetValidCodeBtn = (Button) findViewById(R.id.id_ib_get_validcode);
		mGetValidCodeBtn.setOnClickListener(this);
		
		//监听编辑框焦点变化
		mMobileEdit.setOnFocusChangeListener(onFocusChangeListener);
		mPwdEdit.setOnFocusChangeListener(onFocusChangeListener);
		mMobileEdit.addTextChangedListener(mobileTextWatcher);
		mPwdEdit.addTextChangedListener(passwordTextWatcher);
		
		agreeSSCheckBox = (CheckBox) findViewById(R.id.agreeSSCheckBox);
		viewSSTextView = (TextView) findViewById(R.id.viewSSTextView);
		viewSSTextView.setOnClickListener(this);
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
			case R.id.id_et_pwd:
				if (hasFocus && focusEditText.length() > 0)
					mPasswordEditClear.setVisibility(View.VISIBLE);
				else
					mPasswordEditClear.setVisibility(View.GONE);
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
	    
	    private  TextWatcher passwordTextWatcher = new TextWatcher(){
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	boolean hasContent = (boolean)(s.length() > 0);
	        	mPasswordEditClear.setVisibility(hasContent ? View.VISIBLE : View.GONE);
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
				.getString(R.string.register_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	/**
	 * 注册
	 */
	private void register() {
		mMobile = mMobileEdit.getText().toString();
		mPwd = mPwdEdit.getText().toString();
		mValidCode = mValidCodeEdit.getText().toString();
		
		// 必须同意服务条款
		if (!agreeSSCheckBox.isChecked())
		{
			ToastUtils.show(mContext, mResources.getString(R.string.register_please_agree_service_term_hint));
			return ;
		}
		
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
		else if (StringUtils.isEmpty(mPwd)) {
			ToastUtils.show(mContext, mResources
					.getString(R.string.register_pwd_empty_wrong_tips));
			mPwdEdit.setFocusable(true);
			mPwdEdit.setFocusableInTouchMode(true);
			mPwdEdit.requestFocus();
			return;
		}

		new RegisterTask().execute(mMobile, mValidCode, mPwd);
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
	 * 调用注册接口
	 * @author AA
	 * @Date 2015-02-04
	 */
	private class RegisterTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext,
					mResources.getString(R.string.register_loading_text), false);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//Thread.sleep(500);
				String deviceId = SystemUtils.getDeviceId(mContext);
				mDatas = DataServer.getInstance().register(params[0],
						params[1], params[2], deviceId);
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
				ToastUtils.show(mContext, mResources.getString(R.string.register_success_tips));
				//UIHelper.hideDialogForSingleText();
				new LoginTask().execute();
//				//提示注册成功，点击确定进行登录
//				UIHelper.showDialogForSingleText(mContext,
//						mResources.getString(R.string.register_success_tips),
//						new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								UIHelper.hideDialogForSingleText();
//								new LoginTask().execute();
//							}
//						});
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}

	/**
	 * 调用登录接口，登录成功则进入首页
	 * @author AA
	 * @Date 2015-02-04
	 */
	private class LoginTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.login_logining_text), true);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				mDatas = DataServer.getInstance().login(mMobile, mPwd);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			if (isSuccess) {
				
				//将用户名和密码保存到本地
				LocationUtil.writeInit(mContext, Consts.UNAME, mMobile);
				LocationUtil.writeInit(mContext, Consts.PASSWORD, mPwd);
				LocationUtil.writeInit(mContext, Consts.KEY_AUTO_LOGIN, true);
				
				// 将接口返回数据保存至本地
				LocationUtil.writeInit(
						mContext,
						new String[] { Consts.SECRET, Consts.TOKEN, Consts.UID, Consts.PHOTO, 
								Consts.PHONE, Consts.NICKNAME, Consts.REALNAME, Consts.SIGNATURE,
								Consts.PROVINCE, Consts.CITY, Consts.DISTINCT, 
								Consts.JOB, Consts.CALLNAME, Consts.CATEGORY, Consts.IS_AUTH, 
								Consts.INTANGIBLEHERITAGE, Consts.WORK_AGE, Consts.AID, Consts.ASSOCIATION,
								Consts.INTRODUCE, Consts.VOICE_PATH},
								
						new String[] {
								mDatas.get(Consts.SECRET).toString(),
								mDatas.get(Consts.TOKEN).toString(),
								mDatas.get(Consts.UID).toString(),
								mDatas.get(Consts.PHOTO).toString(),
								mDatas.get(Consts.PHONE).toString(),
								mDatas.get(Consts.NICKNAME).toString(),
								mDatas.get(Consts.REALNAME).toString(),
								mDatas.get(Consts.SIGNATURE).toString(),
								mDatas.get(Consts.PROVINCE).toString(),
								mDatas.get(Consts.CITY).toString(),
								mDatas.get(Consts.DISTINCT).toString(),
								mDatas.get(Consts.JOB).toString(),
								mDatas.get(Consts.CALLNAME).toString(),
								mDatas.get(Consts.CATEGORY).toString(),
								mDatas.get(Consts.IS_AUTH).toString(),
								mDatas.get(Consts.INTANGIBLEHERITAGE).toString(),
								mDatas.get(Consts.WORK_AGE).toString(),
								mDatas.get(Consts.AID).toString(),
								mDatas.get(Consts.ASSOCIATION).toString(),
								mDatas.get(Consts.INTRODUCE).toString(),
								mDatas.get(Consts.VOICE_PATH).toString()
								});

				// 进入首页
				UIHelper.showMain(mContext);
				
				// 读取所有作品分类
				CategoryManager.getInstance().loadCategoryData();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
			//关闭加载对话框
			UIHelper.hideDialogForLoading();
		}
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
		case R.id.id_ib_pwd_clear:
			mPwdEdit.setText("");
			mPasswordEditClear.setVisibility(View.INVISIBLE);
			break;
		case R.id.id_ib_title_left:
			finish();
			break;
		// 注册
		case R.id.id_btn_register:
			// 隐藏键盘
			UIHelper.hideSoftInput(this, mMobileEdit);
			UIHelper.hideSoftInput(this, mPwdEdit);
			UIHelper.hideSoftInput(this, mValidCodeEdit);

			register();
			break;
		case R.id.id_ib_get_validcode: // 获取短信验证码
			getValidCode();
			break;
		case R.id.viewSSTextView: // 查看服务条款
			String token = LocationUtil.readInit(mContext, Consts.TOKEN, "");
			String webUrl = Consts.URL_SERVICE_TERM_BASE + token;
			UIHelper.showWebView(mContext, webUrl, Consts.WEBVIEW_PAGE_SERVICE_TERM);
			break;
		}
	}
}
