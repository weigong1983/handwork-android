package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseFragmentActivity;
import com.daiyan.handwork.app.fragment.CommentListFragment;
import com.daiyan.handwork.app.fragment.FindFragment;
import com.daiyan.handwork.app.fragment.HomeFragment;
import com.daiyan.handwork.app.fragment.HomepageFragment;
import com.daiyan.handwork.app.fragment.HomepageFragment.OnFragmentDataLoadListening;
import com.daiyan.handwork.app.fragment.IntroductionFragment;
import com.daiyan.handwork.app.fragment.LikeFragment;
import com.daiyan.handwork.app.fragment.MineFragment;
import com.daiyan.handwork.app.fragment.MyWorksFragment;
import com.daiyan.handwork.app.fragment.TestFragment;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.bean.UserInfo;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SystemBarTintManager;
import com.daiyan.handwork.utils.ToastUtils;
import com.viewpagerindicator.TabPageIndicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

/**
 * 主页
 * 
 * @author 魏工
 * @Date 2015年05月09日
 */
public class Homepage extends BaseFragmentActivity implements
		OnClickListener {

	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	
	// 标题栏控件
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;

	/** 资源对象 */
	private Resources mResources;
	/** 上下文 */
	private Activity mContext;

	private TabPageIndicator mIndicator;
	private ViewPager mMainPager;
	private HomepageFragmentPagerAdapter mAdapter;
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private String [] mPageTitleArray = {"主页", "简介", "作品", "热卖"};
	
	private int mCurrentFragment = Consts.FRAGMENT_HOMEPAGE;

	public static UserInfo mCurrentUserInfo = null;
	public static boolean isMe = false;
	public static String mUserId = "";
	
	
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
				
		super.onCreate(savedInstanceState,
				R.layout.activity_homepage);
		mContext = Homepage.this;
		
		// 读取参数传递
		mUserId = getIntent().getStringExtra(Consts.EXTRA_UID);
		String myUID = LocationUtil.readInit(mContext, Consts.UID, "");
		if (myUID.equalsIgnoreCase(mUserId))
		{
			isMe = true; // 我的主页
		}
		else
		{
			isMe = false; // 别人的主页
		}

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		DataServer.getInstance().initialize(mContext);
		initTitleBar();
		initPager();
	}
	
	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	/**
	 * 初始化ViewPager
	 */
	private void initPager() {
		mMainPager = (ViewPager) findViewById(R.id.pager);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);

		// 关闭预加载
		mMainPager.setOffscreenPageLimit(0);
		
		// 加入Fragment
		HomepageFragment homepageFragment = new HomepageFragment();
		homepageFragment.setOnPagerDataLoadListening(new OnFragmentDataLoadListening() {
			@Override
			public void onUserDataLoad(UserInfo userInfo) {
				mCurrentUserInfo = userInfo;
				// 刷新标题
				mTitleTextView.setText(mCurrentUserInfo.nickName);
			}
		});
		
		mFragmentList.add(homepageFragment);
		mFragmentList.add(new IntroductionFragment());
		mFragmentList.add(new MyWorksFragment(0));
		mFragmentList.add(new MyWorksFragment(1));

		mAdapter = new HomepageFragmentPagerAdapter(getSupportFragmentManager());
		mMainPager.setAdapter(mAdapter);
		mMainPager.setOnPageChangeListener(onPageChangeListener);
		mMainPager.setCurrentItem(mCurrentFragment);

		mIndicator.setViewPager(mMainPager);
		mIndicator.setOnPageChangeListener(onPageChangeListener);
		mIndicator.setCurrentItem(mCurrentFragment);
	}

	class HomepageFragmentPagerAdapter extends FragmentPagerAdapter {
		public HomepageFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return (mFragmentList == null) ? null
					: mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList == null ? 0 : mFragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mPageTitleArray[position];
		}
	}

	/**
	 * ViewPager切换监听器
	 */
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			mCurrentFragment = position;
			switch (position) {
			case Consts.FRAGMENT_HOMEPAGE: // 切换到【主页】
				// 操作代码 TODO..
				break;
			case Consts.FRAGMENT_INTRODUCTION: // 切换到【简介】
				// 操作代码 TODO..
				break;
			case Consts.FRAGMENT_MY_WORKS: // 切换到【作品】
				// 操作代码 TODO..
				break;
			case Consts.FRAGMENT_MY_COLLECTING: // 切换到【收藏中】
				// 操作代码 TODO..
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
		case R.id.id_ib_title_left:
			finish();
			break;
		default:
			break;
		}
	}
}
