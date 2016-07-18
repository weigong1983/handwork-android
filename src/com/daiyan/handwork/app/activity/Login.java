package com.daiyan.handwork.app.activity;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.bean.LoginUsers;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.DBManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.SystemBarTintManager;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

/**
 * 登录界面
 * @author AA
 * @Date 2014-11-23
 */
public class Login extends BaseActivity implements OnClickListener{

	private int mLoginType;
	
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private Activity mContext;
	private Resources mResources;
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	/** 用户名 */
	private EditText mUsernameEdit;
	private String mUsername = "";
	private ImageButton mUsernameClearBtn;

	/** 密码 */
	private EditText mPwdEdit;
	private String mPwd = "";
	private ImageButton mPwdClearBtn;
	/** 登录按钮 */
	private Button mLoginBtn;
	
	/** 注册按钮 */
	private Button mRegisterBtn;
	
	/** 忘记密码按钮 */
	private Button mForgetPasswordBtn;
	
	
	/** 数据库管理类 */
	private DBManager mDBManager;
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_login);
		
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
		mLoginType =  getIntent().getIntExtra(Consts.EXTRA_LOGIN_TYPE, Consts.LOGIN_TYPE_NORMAL);
		
		mContext = Login.this;
		mDBManager = new DBManager(mContext);
		initView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDBManager.closeDB();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		DataServer.getInstance().initialize(mContext);
		initTitleBar();
		initContentView();
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setOnClickListener(this);
		if (mLoginType == Consts.LOGIN_TYPE_NORMAL)
			mTitleLeftBtn.setVisibility(View.VISIBLE);
		else
			mTitleLeftBtn.setVisibility(View.GONE);
		
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.login_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setText("");
		mTitleRightTextView.setOnClickListener(this);
	}
	
	/**
	 * 内容页
	 */
	private void initContentView() {
		//读取本地储存的登录账号和密码并显示
		boolean isAutoLogin = LocationUtil.readInit(mContext, Consts.KEY_AUTO_LOGIN, false);
		if (isAutoLogin)
		{
			mUsername = LocationUtil.readInit(mContext, Consts.UNAME, "");
			mPwd = LocationUtil.readInit(mContext, Consts.PASSWORD, "");
		}
		
		mUsernameEdit = (EditText)findViewById(R.id.id_et_login_mobile);
		mUsernameClearBtn = (ImageButton)findViewById(R.id.id_ib_login_user_clear);
		mUsernameClearBtn.setOnClickListener(this);

		//显示内容
		mPwdEdit = (EditText)findViewById(R.id.id_et_login_pwd);
		mPwdClearBtn = (ImageButton)findViewById(R.id.id_ib_login_pwd_clear);
		mPwdClearBtn.setOnClickListener(this);
		mUsernameEdit.setText(mUsername);
		mUsernameEdit.setSelection(mUsername.length());
		mPwdEdit.setText(mPwd);
		mPwdEdit.setSelection(mPwd.length());
		//登录按钮
		mLoginBtn = (Button)findViewById(R.id.id_btn_login);
		mLoginBtn.setOnClickListener(this);
		
		/** 注册按钮 */
		mRegisterBtn = (Button)findViewById(R.id.id_btn_register);
		mRegisterBtn.setOnClickListener(this);
		/** 忘记密码按钮 */
		mForgetPasswordBtn = (Button)findViewById(R.id.id_btn_forgot_pwd);
		mForgetPasswordBtn.setOnClickListener(this);
		
		//监听编辑框焦点变化
		mUsernameEdit.setOnFocusChangeListener(onFocusChangeListener);
		mPwdEdit.setOnFocusChangeListener(onFocusChangeListener);
		mUsernameEdit.addTextChangedListener(usernameTextWatcher);
		mPwdEdit.addTextChangedListener(passwordTextWatcher);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// 重置密码后清空密码框
		if (Consts.REQUEST_CODE_FORGOT_PASSWORD == requestCode)
		{
			mPwd = LocationUtil.readInit(mContext, Consts.PASSWORD, "");
			mPwdEdit.setText(mPwd);
			mPwdEdit.setSelection(mPwd.length());
		}
	}

	/**
	 * 登录任务线程
	 * @author AA
	 *
	 */
	private class LoginTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(2000);
				mDatas = DataServer.getInstance().login(mUsername, mPwd);
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
				
				//将用户名和密码保存到本地
				LocationUtil.writeInit(mContext, Consts.UNAME, mUsername);
				LocationUtil.writeInit(mContext, Consts.PASSWORD, mPwd);
				LocationUtil.writeInit(mContext, Consts.KEY_AUTO_LOGIN, true);
				
				ToastUtils.show(mContext, mResources.getString(R.string.login_success_text));
				
				// 进入APP首页
				UIHelper.showMain(mContext);
				
				// 读取所有作品分类
				CategoryManager.getInstance().loadCategoryData();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
		
	}
	
	/**
	 * 检查用户输入的有效性
	 * @param username
	 * @param pwd
	 * @return
	 */
	private boolean checkInputValid(String username, String pwd) {
		if(StringUtils.isEmpty(username) 
			|| (!username.equalsIgnoreCase(Consts.GUEST_UNAME) && !StringUtils.isMobileNumber(username))) 
		{
			ToastUtils.show(mContext, R.string.login_input_invalid_phoneno);
			return false;
		}
		
		if(StringUtils.isEmpty(pwd) ) {
			ToastUtils.show(mContext, R.string.login_input_invalid_password);
			return false;
		}

		return true;
	}
	
	/**
	 * 编辑框焦点监听器
	 */
	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			EditText focusEditText = (EditText)v;
			switch (v.getId()) {
			case R.id.id_et_login_mobile:
				if (hasFocus && focusEditText.length() > 0)
					mUsernameClearBtn.setVisibility(View.VISIBLE);
				else
					mUsernameClearBtn.setVisibility(View.GONE);
				break;
			case R.id.id_et_login_pwd:
				if (hasFocus && focusEditText.length() > 0)
					mPwdClearBtn.setVisibility(View.VISIBLE);
				else
					mPwdClearBtn.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 编辑框内容变化监听器
	 */
	private  TextWatcher usernameTextWatcher = new TextWatcher(){
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	boolean hasContent = (boolean)(s.length() > 0);
	        	mUsernameClearBtn.setVisibility(hasContent ? View.VISIBLE : View.GONE);
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
	        	mPwdClearBtn.setVisibility(hasContent ? View.VISIBLE : View.GONE);
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	        }
	    };
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		//清除当前用户名
		case R.id.id_ib_login_user_clear:
			mUsernameEdit.setText("");
			mUsernameClearBtn.setVisibility(View.INVISIBLE);
			break;
		//清除当前密码
		case R.id.id_ib_login_pwd_clear:
			mPwdEdit.setText("");
			mPwdClearBtn.setVisibility(View.INVISIBLE);
			break;
			
		//登录按钮
		case R.id.id_btn_login:
			// 隐藏键盘
			UIHelper.hideSoftInput(this, mUsernameEdit);
			UIHelper.hideSoftInput(this, mPwdEdit);
			
			mUsername = mUsernameEdit.getText().toString();
			mPwd = mPwdEdit.getText().toString();
			if(checkInputValid(mUsername, mPwd)) 
			{
				//修改登录按钮内容和背景颜色
				String loading = mResources.getString(R.string.login_logining_text);
				//显示正在登录对话框
				UIHelper.showDialogForLoading(mContext, loading, false);
				new LoginTask().execute();
			}
			break;
		case R.id.id_btn_register: // 注册
			UIHelper.showRegister(mContext);
			break;
		case R.id.id_btn_forgot_pwd: // 忘记密码
			UIHelper.showForgotPassword(mContext);
			break;
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(event.getAction() == KeyEvent.ACTION_UP) {
				if (mLoginType == Consts.LOGIN_TYPE_NORMAL)
					finish();
				else
					AppManager.getInstance().exitApp();
				
				return true;				
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
}
