package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.ViewPagerAdapter;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 引导页
 * 
 * @author AA
 * @Date 2014-11-22
 */
public class WelcomePage extends BaseActivity implements OnClickListener {

	private Activity mContext;
	
	private HashMap<String, Object> mDatas;
	
	private ViewPager mViewPager;
	private ViewPagerAdapter mAdapter;
	private LayoutInflater mInflater;
	private ArrayList<View> mViewList;
	private View mPageOne;
	private View mPageTwo;
	private View mPageThree;

	private ImageView mPageOneImage;
	private ImageView mPageTwoImage;
	private ImageView mPageThreeImage;

	private ImageView[] mDotsView;
	private int[] bgs = { R.drawable.welcome_1, R.drawable.welcome_2, R.drawable.welcome_3};
	
	private boolean isLaunch = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_page);
		//入栈
		AppManager.getInstance().pushActivity(this);
		
		//不显示系统的标题栏          
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                              WindowManager.LayoutParams.FLAG_FULLSCREEN );
		
		mContext = WelcomePage.this;
		isLaunch =  getIntent().getBooleanExtra(Consts.EXTRA_IS_LAUNCH, true);
		initView();
	}

	private void initView() {
		mInflater = getLayoutInflater();
		mPageOne = mInflater.inflate(R.layout.guide_page_nor, null);
		mPageTwo = mInflater.inflate(R.layout.guide_page_nor, null);
		mPageThree = mInflater.inflate(R.layout.guide_page_nor, null);

		mViewList = new ArrayList<View>();
		mViewList.add(mPageOne);
		mViewList.add(mPageTwo);
		mViewList.add(mPageThree);

		initViewPager();
		initDots();
	}

	/**
	 * 初始化每一个页面
	 */
	private void initViewPager() {
		mPageOneImage = (ImageView) mPageOne.findViewById(R.id.id_iv_guide_page_bg);
		mPageOneImage.setBackgroundResource(R.drawable.welcome_1);
		
		mPageTwoImage = (ImageView) mPageTwo.findViewById(R.id.id_iv_guide_page_bg);
		mPageTwoImage.setBackgroundResource(R.drawable.welcome_2);
		
		mPageThreeImage = (ImageView) mPageThree.findViewById(R.id.id_iv_guide_page_bg);
		mPageThreeImage.setBackgroundResource(R.drawable.welcome_3);
		mPageThreeImage.setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.start_view_pager);
		mAdapter = new ViewPagerAdapter(mViewList);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				View view = mViewList.get(position);
				ImageView imageView = (ImageView) view
						.findViewById(R.id.id_iv_guide_page_bg);
				imageView.setBackgroundResource(bgs[position]);
				updateDotImages(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	/**
	 * 初始化下面的原点
	 */
	private void initDots() {
		ViewGroup group = (ViewGroup) findViewById(R.id.start_dot_group);
		mDotsView = new ImageView[mViewList.size()];

		for (int i = 0; i < mViewList.size(); i++) {
			ImageView imageView = new ImageView(WelcomePage.this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 0, 5, 0);
			imageView.setLayoutParams(params);
			mDotsView[i] = imageView;

			if (i == 0) {
				mDotsView[i].setBackgroundResource(R.drawable.landing_page_dot_on);
			} else {
				mDotsView[i].setBackgroundResource(R.drawable.landing_page_dot_off);
			}

			if (mDotsView.length != group.getChildCount()) {
				group.addView(mDotsView[i]);
			}
		}
	}

	/**
	 * 根据用户切换页面去更新原点
	 * 
	 * @param currentPageNo
	 */
	private void updateDotImages(int currentPageNo) {
		for (int index = 0; index < mDotsView.length; index++) {
			if (currentPageNo == index) {
				mDotsView[index].setBackgroundResource(R.drawable.landing_page_dot_on);
			} else {
				mDotsView[index].setBackgroundResource(R.drawable.landing_page_dot_off);
			}
		}
	}

	@Override
	public void onClick(View v) 
	{
		// 最后一页点击事件处理
		if (v.getId() == R.id.id_iv_guide_page_bg) 
		{
			if (isLaunch) // 首次启动，自动登录游客账号
				new GuestLoginTask().execute();
			else // 返回【关于我们】页面
				finish();
		}
	}
	
	private class GuestLoginTask extends AsyncTask<Void, Void, Boolean> {
    	@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				//mDatas = DataServer.getInstance().login(Consts.GUEST_UNAME, Consts.GUEST_PASSWORD);
				String deviceId = SystemUtils.getDeviceId(mContext);
				mDatas = DataServer.getInstance().loginGuest(deviceId);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			// 获取数据成功并且当前为自动登录
			if (isSuccess) {
				// 将接口返回数据保存至本地
				LocationUtil.writeInit(
						mContext,
						new String[] { Consts.SECRET, Consts.TOKEN, Consts.UID,
								Consts.PHOTO, Consts.PHONE, Consts.NICKNAME,
								Consts.REALNAME, Consts.SIGNATURE,
								Consts.PROVINCE, Consts.CITY, Consts.DISTINCT,
								Consts.JOB, Consts.CALLNAME, Consts.CATEGORY,
								Consts.IS_AUTH, Consts.INTANGIBLEHERITAGE,
								Consts.WORK_AGE, Consts.AID,
								Consts.ASSOCIATION, Consts.INTRODUCE,
								Consts.VOICE_PATH },

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
								mDatas.get(Consts.INTANGIBLEHERITAGE)
										.toString(),
								mDatas.get(Consts.WORK_AGE).toString(),
								mDatas.get(Consts.AID).toString(),
								mDatas.get(Consts.ASSOCIATION).toString(),
								mDatas.get(Consts.INTRODUCE).toString(),
								mDatas.get(Consts.VOICE_PATH).toString() });

				//将用户名和密码保存到本地
				LocationUtil.writeInit(mContext, Consts.UNAME, "");
				LocationUtil.writeInit(mContext, Consts.PASSWORD, "");
				LocationUtil.writeInit(mContext, Consts.KEY_AUTO_LOGIN, false); // 游客不自动登录
				
				//进入首页
				UIHelper.showMain(mContext);
				
				// 读取所有作品分类
				CategoryManager.getInstance().loadCategoryData();
			}
		}
    }
}
