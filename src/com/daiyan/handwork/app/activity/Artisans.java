package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseFragmentActivity;
import com.daiyan.handwork.app.fragment.ArtisansFragment;
import com.daiyan.handwork.app.fragment.CommentListFragment;
import com.daiyan.handwork.app.fragment.FindFragment;
import com.daiyan.handwork.app.fragment.HomeFragment;
import com.daiyan.handwork.app.fragment.LikeFragment;
import com.daiyan.handwork.app.fragment.MineFragment;
import com.daiyan.handwork.app.fragment.TestFragment;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
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
 * 工艺家
 * @author 魏工
 * @Date 2015年05月09日
 */
public class Artisans extends BaseFragmentActivity implements
		OnClickListener {

	private HashMap<String, Object> mDatas;
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
	private ArtisansFragmentPagerAdapter mAdapter;
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private String [] mClassNameArray;
	private String [] mClassIdArray;
	
	private int mCurrentFragment = 0;


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
				R.layout.activity_artisans);
		mContext = Artisans.this;
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
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
		
		mTitleTextView.setText(getResources().getText(R.string.title_artisans));
	}

	/**
	 * 初始化ViewPager
	 */
	private void initPager() {
		mMainPager = (ViewPager) findViewById(R.id.pager);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);

		// 关闭预加载
		mMainPager.setOffscreenPageLimit(0);
		
		// 读取工艺品类数据
		mClassNameArray = CategoryManager.getInstance().getClassNameArray();
		mClassIdArray = CategoryManager.getInstance().getClassIdArray();
		
		if (mClassNameArray == null || mClassIdArray == null)
		{
			String loading = mResources.getString(R.string.loading_data_tips);
			UIHelper.showDialogForLoading(mContext, loading, true);
			// 读取所有作品分类
			CategoryManager.getInstance().loadCategoryData();
			return ;
		}

		setPagerData();
	}
	
	private void setPagerData()
	{
		// 加入Fragment
		int fragmentCount = mClassNameArray.length;
		for (int i = 0; i < fragmentCount; i++) {
			mFragmentList.add(new ArtisansFragment(mClassIdArray[i]));
		}

		mAdapter = new ArtisansFragmentPagerAdapter(getSupportFragmentManager());
		mMainPager.setAdapter(mAdapter);
		mMainPager.setOnPageChangeListener(onPageChangeListener);
		mMainPager.setCurrentItem(mCurrentFragment);

		mIndicator.setViewPager(mMainPager);
		mIndicator.setOnPageChangeListener(onPageChangeListener);
		mIndicator.setCurrentItem(mCurrentFragment);
	}

	class ArtisansFragmentPagerAdapter extends FragmentPagerAdapter {
		public ArtisansFragmentPagerAdapter(FragmentManager fm) {
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
			return "  " + mClassNameArray[position] + "  ";
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
			case 0: // 雕刻
				
				break;
			case 1: // 刺绣
	
				break;
			case 2: // 陶瓷

				break;
			case 3: // 漆器

				break;
			case 4: // 金属

				break;
			case 5: // 综合

				break;
			default:
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
