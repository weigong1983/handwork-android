package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseFragmentActivity;
import com.daiyan.handwork.app.fragment.CommentListFragment;
import com.daiyan.handwork.app.fragment.LikeFragment;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.SystemBarTintManager;
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
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

/**
 * 作品交流主页（包括：赞、评论列表）
 * 
 * @author 魏工
 * @Date 2015年05月09日
 */
public class WorksCommunication extends BaseFragmentActivity implements
		OnClickListener {

	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);

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
	private WorksCommunicationFragmentPagerAdapter mAdapter;
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private List<String> mPageTitleList = new ArrayList<String>();
	private int mCurrentFragment = Consts.FRAGMENT_COMMENT;

	// Fragment 引用
	private CommentListFragment mCommentListFragment;
	
	private String mWorksId;
	
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
				R.layout.activity_works_communication);
		mContext = WorksCommunication.this;
		mWorksId = getIntent().getStringExtra(Consts.EXTRA_WORKS_ID);
		mCurrentFragment = getIntent().getIntExtra(Consts.EXTRA_FRGMENT, Consts.FRAGMENT_COMMENT);
		
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initTitleBar();
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
		setTitleBar();
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
		
		// 创建Fragment
		mCommentListFragment = new CommentListFragment(mWorksId);
		
		// 加入Fragment
		mFragmentList.add(new LikeFragment(mWorksId));
		mFragmentList.add(mCommentListFragment);
		// 加入标题
		mPageTitleList.add("赞");
		mPageTitleList.add("评论");

		mAdapter = new WorksCommunicationFragmentPagerAdapter(
				getSupportFragmentManager(), mFragmentList, mPageTitleList);
		mMainPager.setAdapter(mAdapter);
		mMainPager.setOnPageChangeListener(onPageChangeListener);
		mMainPager.setCurrentItem(mCurrentFragment);

		mIndicator.setViewPager(mMainPager);
		mIndicator.setOnPageChangeListener(onPageChangeListener);
		mIndicator.setCurrentItem(mCurrentFragment);
	}

	class WorksCommunicationFragmentPagerAdapter extends FragmentPagerAdapter {

		private static final int FRAGMENT_COUNT = 2; // 赞、评论 2个页面
		private List<Fragment> mFragmentList;
		private List<String> mPageTitleList;

		public WorksCommunicationFragmentPagerAdapter(FragmentManager fm,
				List<Fragment> fragments, List<String> titles) {
			super(fm);
			this.mFragmentList = fragments;
			this.mPageTitleList = titles;
		}

		@Override
		public Fragment getItem(int position) {
			return (mFragmentList == null || mFragmentList.size() < FRAGMENT_COUNT) ? null
					: mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList == null ? 0 : mFragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mPageTitleList.get(position);
		}
	}

	/**
	 * 根据当前页面索引设置标题
	 * 
	 * @param current
	 */
	private void setTitleBar() {
		switch (mCurrentFragment) {
		case Consts.FRAGMENT_LIKE:
			mTitleTextView.setText(mResources
					.getString(R.string.title_works_like));
			break;
		case Consts.FRAGMENT_COMMENT:
			mTitleTextView.setText(mResources
					.getString(R.string.title_works_comment));
			break;
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
			case Consts.FRAGMENT_LIKE: // 切换到点赞列表
				// 操作代码 TODO..
				setTitleBar();
				break;
			case Consts.FRAGMENT_COMMENT: // 切换到评论列表
				// 操作代码 TODO..
				setTitleBar();
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
			onBackToWorksDetailPage();
			break;
		default:
			break;
		}
	}
	
	private void onBackToWorksDetailPage()
	{
		Intent intent=new Intent();
        intent.putExtra(Consts.EXTRA_REFRESH_COMMENT_COUNT, mCommentListFragment.mCommentCount);
        setResult(Consts.REQUEST_REFRESH_COMMENT_COUNT, intent);
		finish();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK 
			&& event.getAction() == KeyEvent.ACTION_UP) 
		{
			onBackToWorksDetailPage();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
