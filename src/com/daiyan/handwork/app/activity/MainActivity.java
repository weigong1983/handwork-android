package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.HomeFragmentPagerAdapter;
import com.daiyan.handwork.app.BaseFragmentActivity;
import com.daiyan.handwork.app.fragment.FindFragment;
import com.daiyan.handwork.app.fragment.HomeFragment;
import com.daiyan.handwork.app.fragment.MineFragment;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SystemBarTintManager;
import com.daiyan.handwork.utils.ToastUtils;
import com.pgyersdk.update.PgyUpdateManager;
import com.umeng.analytics.AnalyticsConfig;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

/**
 * 主界面
 * 
 * @author AA
 * @Date 2014-11-20
 */
public class MainActivity extends BaseFragmentActivity implements OnClickListener, AnimatorListener{

	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用退出登录接口返回数据 */
	private HashMap<String, Object> mLogoutDatas;

	/** 底部按钮 */
	private RadioGroup mRadioGroup;
	/** 标题栏标题 */
	private TextView mTitleTextView;
	
	/** 资源对象 */
	private Resources mResources;
	/** 上下文 */
	private Activity mContext;

	private ViewPager mMainPager;
	private HomeFragmentPagerAdapter mAdapter;
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private HomeFragment mHomeFragment; // 主页Fragment引用
	
	/** ViewPager当前页面索引 */
	private int mCurPosition = 0;
	
	/** 退出程序的提示时间 */
	private long mExitTime = 0;
	
	// 首页：列表向上拖动隐藏标题栏，向下拖动显示标题栏
	private LinearLayout mTitleBar; 
	private boolean mIsTitleHide = false;
    private boolean mIsAnim = false;
    private float lastX = 0;
    private float lastY = 0;
	private boolean isDown = false;
    private boolean isUp = false;
    
    // Tab页面请求重置标题栏监听器
    public interface ResumeTitleListener {  
        void onRest();  
    }
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		
		super.onCreate(savedInstanceState, R.layout.activity_main);		
		mContext = MainActivity.this;
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initTitleBar();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (Consts.REQUEST_REFRESH_WORKS_LIST == requestCode && data != null)
		{
			// 刷新首页作品列表
			boolean refreshList  = data.getBooleanExtra(Consts.EXTRA_REFRESH_WORKS_LIST, false);
			if (refreshList)
			{
				mHomeFragment.Connection(Consts.LOAD_REFRESH);
			}
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		DataServer.getInstance().initialize(mContext);
		initTitleBar();
		initPager();
		initRadioGroup();

		// 蒲公英版本更新
		String channel = AnalyticsConfig.getChannel(this);
		if (channel.equalsIgnoreCase("pgyer"))
		{
			PgyUpdateManager.register(this, Consts.PGY_APP_KEY);
		}
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleBar = (LinearLayout) findViewById(R.id.id_ll_main_title);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title);
		setTitleBar(mCurPosition);
		
		mTitleBar.post(new Runnable() {
            @Override
            public void run() {
            	// 解决刚开始广告栏缩进到标题栏的问题(注：必须在UI线程中完成界面加载之后才能获取view高度)
            	setMarginTop(mTitleBar.getHeight());
            }
        });
	}

	/**
	 * 初始化ViewPager
	 */
	private void initPager() {
		mMainPager = (ViewPager) findViewById(R.id.id_vp_main);
		//关闭预加载
		mMainPager.setOffscreenPageLimit(0);
		
		// 首页Fragment引用
		mHomeFragment = new HomeFragment(mMainPager, new ResumeTitleListener()
		{
			@Override
			public void onRest() {
				resumeTitlebar();
			}
		});
		
		//加入Fragment
		mFragmentList.add(mHomeFragment);
		mFragmentList.add(new FindFragment());
		mFragmentList.add(new MineFragment());
		mAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
		mMainPager.setAdapter(mAdapter);
		mMainPager.setOnPageChangeListener(onPageChangeListener);
		mMainPager.setCurrentItem(Consts.FRAGMENT_HOME);
	}

	/**
	 * 初始化底部按钮
	 */
	private void initRadioGroup() {
		mRadioGroup = (RadioGroup)findViewById(R.id.id_rg_main_tab_group);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.id_rb_FRAGMENT_HOME:
					mMainPager.setCurrentItem(Consts.FRAGMENT_HOME);
					break;
				case R.id.id_rb_FRAGMENT_FIND:
					mMainPager.setCurrentItem(Consts.FRAGMENT_FIND);
					break;
				case R.id.id_rb_FRAGMENT_MINE:
					mMainPager.setCurrentItem(Consts.FRAGMENT_MINE);
					break;
				}
			}
		});
	}


	/**
	 * 根据当前页面索引设置标题
	 * @param current
	 */
	private void setTitleBar(int current) {
		switch (current) {
		case Consts.FRAGMENT_HOME:
			mTitleTextView.setText(mResources.getString(R.string.title_home_text));
			break;

		case Consts.FRAGMENT_FIND:
			mTitleTextView.setText(mResources.getString(R.string.title_find_text));
			
			break;
		case Consts.FRAGMENT_MINE:
			mTitleTextView.setText(mResources.getString(R.string.title_mine_text));
			break;
		}
	}

	/**
	 * ViewPager切换监听器
	 */
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			// 恢复标题栏
			resumeTitlebar();
			mCurPosition = position;
			switch (position) {
			case Consts.FRAGMENT_HOME:
				setTitleBar(Consts.FRAGMENT_HOME);
				mRadioGroup.check(R.id.id_rb_FRAGMENT_HOME);
				break;
			case Consts.FRAGMENT_FIND:
				setTitleBar(Consts.FRAGMENT_FIND);
				mRadioGroup.check(R.id.id_rb_FRAGMENT_FIND);
				break;
			case Consts.FRAGMENT_MINE:
				setTitleBar(Consts.FRAGMENT_MINE);
				mRadioGroup.check(R.id.id_rb_FRAGMENT_MINE);
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	/**
	 * 调用注销登录接口
	 * @author AA
	 * @Date 2014-12-29
	 */
	private class LogoutTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				mLogoutDatas = DataServer.getInstance().logout();
				return mLogoutDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			if(isSuccess) {
				//清楚自动登录标志，跳转至登录界面
				LocationUtil.writeInit(mContext, Consts.KEY_AUTO_LOGIN, false);
				UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_LOGOUT);
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
	
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			
				// 如果两次按键时间间隔大于2000毫秒，则不退出
				if (System.currentTimeMillis() - mExitTime > 2000) {
					String tips = mResources.getString(R.string.main_exit_app_tips);
					ToastUtils.show(mContext, tips);
					mExitTime = System.currentTimeMillis();
				} else {
					AppManager.getInstance().exitApp();
				}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
    // 自动显示隐藏标题栏处理代码-开始
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) 
    {
    	// 首页支持隐藏标题栏
    	if (mCurPosition == Consts.FRAGMENT_HOME)
    		super.dispatchTouchEvent(event);
    	else
    		return super.dispatchTouchEvent(event);
    	
        if (mIsAnim) {  
            return false;  
        }  
        final int action = event.getAction();  
        
        float x = event.getX();  
        float y = event.getY();  
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                lastX = x;
                return false;
            case MotionEvent.ACTION_MOVE:
                float dY = Math.abs(y - lastY);
                float dX = Math.abs(x - lastX);
                boolean down = y > lastY ? true : false;
                lastY = y;
                lastX = x;
                isUp =   dX < 6 && dY > 8 && !mIsTitleHide && !down ;
                isDown = dX < 6 && dY > 8 && mIsTitleHide && down;
                
                if (isUp) 
                {
                	hideTitlebar(true);
                }
                else if (isDown) 
                {
                	hideTitlebar(false);
                } else {
                    return false;
                }
                mIsTitleHide = !mIsTitleHide;
                mIsAnim = true;
                break;
            default:
                return false;
            }
        return false;
    }
    
    private void hideTitlebar(boolean hide)
    {
    	float[] f = new float[2];
    	if (hide)
    	{
    		f[0] = 0.0F;
            f[1] = -mTitleBar.getHeight();
    	}
    	else
    	{
    		f[0] = -mTitleBar.getHeight();
            f[1] = 0F;
    	}
    	
    	ObjectAnimator animator1 = ObjectAnimator.ofFloat(this.mTitleBar, "translationY", f);
        animator1.setDuration(300);
        animator1.setInterpolator(new AccelerateDecelerateInterpolator());
        animator1.start();
        animator1.addListener(this);
        
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this.mMainPager, "translationY", f);
        animator2.setDuration(200);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
        animator2.addListener(this);
    }
    
    public void resumeTitlebar()
    {
    	mIsTitleHide = false;
        setMarginTop(mTitleBar.getHeight());
        this.mTitleBar.setTranslationY(0);
        mMainPager.setTranslationY(0);
    }
    
    public void setMarginTop(int page){
    	RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, 
    			RelativeLayout.LayoutParams.MATCH_PARENT); 
        layoutParam.setMargins(0, page, 0, 0);
        mMainPager.setLayoutParams(layoutParam);
    }
    
    
    
    @Override
    public void onAnimationCancel(Animator arg0) {
    }
    @Override
    public void onAnimationEnd(Animator arg0) {
    	if (isDown)
    	{
    		setMarginTop(mTitleBar.getHeight());
    	}
    	else if (isUp)
    	{
    		setMarginTop(0);
    	}
    	
    	mMainPager.refreshDrawableState();
    	
        mIsAnim = false;
    }
    @Override
    public void onAnimationRepeat(Animator arg0) {
        
    }
    @Override
    public void onAnimationStart(Animator arg0) {
        
    }
    // 自动显示隐藏标题栏处理代码-结束
}
