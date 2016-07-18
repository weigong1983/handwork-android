package com.daiyan.handwork.app.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.app.activity.MainActivity.ResumeTitleListener;
import com.daiyan.handwork.app.widget.AdImageAdapter;
import com.daiyan.handwork.app.widget.GuideGallery;
import com.daiyan.handwork.app.widget.PointPageView;
import com.daiyan.handwork.app.widget.RefreshLayout;
import com.daiyan.handwork.app.widget.RefreshLayout.OnLoadListener;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.app.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.daiyan.handwork.app.widget.pull.PullToRefreshListView;
import com.daiyan.handwork.bean.Ad;
import com.daiyan.handwork.bean.WorksInfo;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ViewHolder;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.BitmapUtils;
import com.daiyan.handwork.utils.Graphic;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SDCardFileUtils;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 首页： 展示活动和平台推荐的作品列表
 * 
 * @author AA
 * @Date 2015-1-01-17
 */
public class HomeFragment extends BaseFragment {

	private View mHomeView;
	private Activity mContext;
	private Resources mResources;
	
	/** 带下拉刷新和加载更多的Layout */
	private PullToRefreshListView mRefreshLayout;
	
	
	// 顶部广告栏（活动广告条）
	private LinearLayout  mHearderViewLayout;
	private GuideGallery images_ga;
	private AdImageAdapter adsGalleryAdapter;
	List<Ad> adsListData = new ArrayList<Ad>();
	// 广告小圆点
	private PointPageView adsFocusPageView;

	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	private List<HashMap<String, Object>> mListDatas;

	/** 作品列表 */
	private ListView mWorksListView;
	private CommonAdapter<WorksInfo> mAdapter;
	private List<WorksInfo> mWorksListViewDatas = new ArrayList<WorksInfo>();

	/** 是否还有数据可加载 */
	private boolean mCanLoadMore = true;
	/** 是否加载更多 */
	private boolean mIsPullUpToLoadMore;

	/** 页码 */
	private int mPageIndex = 0;

	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;

	/** 是否已被加载过一次 */
	private boolean mHasLoadedOnce;

	private ViewPager mMainPager;
	
	private ResumeTitleListener mResumeTitleListener;
	
	public HomeFragment() {
	}

	public HomeFragment(ViewPager pager, ResumeTitleListener listener) {
		mMainPager = pager;
		mResumeTitleListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mHomeView == null) {
			mHomeView = inflater.inflate(R.layout.fragment_home, container, false);
			mContext = getActivity();
			mResources = getResources();
			
			// 初始化界面元素 
			initRefreshLayout();
			initView();
			
			isPrepared = true;
			
			lazyLoad();
		}

		ViewGroup parent = (ViewGroup) mHomeView.getParent();
		if (parent != null) {
			parent.removeView(mHomeView);
		}
		return mHomeView;
	}

	@Override
	protected void lazyLoad() {
		//ViewPager + Fragment实现懒加载
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}

		// 首次加载数据
		Connection(Consts.LOAD_NORMAL);
	}

	/**
	 * 初始化列表
	 */
	private void initView() {
		
		// 推荐作品列表
		//mWorksListView = (ListView) mHomeView.findViewById(R.id.id_lv_only);
		mWorksListView.setOnItemClickListener(onItemClickListener);
		
		mHearderViewLayout = (LinearLayout)mContext.getLayoutInflater().
				inflate(R.layout.fragment_home_ads_item, null);

		images_ga = (GuideGallery) mHearderViewLayout.findViewById(R.id.adsGallery);
		images_ga.setmPager(mMainPager);
		
		adsFocusPageView = (PointPageView) mHearderViewLayout.findViewById(R.id.adsFocusPageView);
		adsFocusPageView.init(mContext);
		
		AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) images_ga.getLayoutParams();
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		layoutParams.width = screenWidth;
		layoutParams.height = (int)(layoutParams.width * 0.45); // 图片宽高比例
		images_ga.setLayoutParams(layoutParams);
		adsFocusPageView.setLayoutParams(layoutParams);
		
		images_ga.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int postion, long arg3)
			{
				// 进入活动详细页
				Ad adItem = adsListData.get(postion);
				
				String token = LocationUtil.readInit(mContext, Consts.TOKEN, "");
				String webUrl = Consts.URL_ACTIVITY_DETAIL_BASE + "?token=" + token + "&atid=" + adItem.id;
				UIHelper.showWebView(mContext, webUrl, Consts.WEBVIEW_PAGE_ACTIVITY_DETAIL);
			}
		});
		
		images_ga.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id)
			{
				adsFocusPageView.setPageIndex(position);
			}
	
			@Override public void onNothingSelected(AdapterView<?> arg0)
			{
			}
		});

		// 添加头部广告条
		//mWorksListView.addHeaderView(mHearderViewLayout);
		mWorksListView.addHeaderView(mHearderViewLayout, null, true);
		
		// 设置适配器
		mAdapter = new CommonAdapter<WorksInfo>(mContext, mWorksListViewDatas, 
				R.layout.item_for_main_listview) {
			@Override
			public void convert(ViewHolder holder, final WorksInfo item) {
				 ImageView worksImageView = (ImageView)holder.getView(R.id.worksImageView);
				 //worksImageView.setImageResource(R.drawable.default_icon);
				 
				 // 设置背景色值
				 //worksImageView.setBackgroundColor(Color.parseColor(item.bgColor));
				 Bitmap defaultBitmap = BitmapUtils.createBitmapByColor(Color.parseColor(item.bgColor), 
						 SystemUtils.getScreenWidth(mContext), 640);
				 worksImageView.setImageBitmap(defaultBitmap);
				 
				 if (!item.worksPicUrl.isEmpty())
						mImageLoader.loadImage(item.worksPicUrl, worksImageView, true);
				 worksImageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							UIHelper.showWorksDetail(mContext, item.id);
						}
					 });
				 
				 final TextView likeCountTextView = (TextView)holder.getView(R.id.likeCountTextView);
				 
				 final ImageView likeImageView = (ImageView)holder.getView(R.id.likeImageView);
				 if (item.like)
					 likeImageView.setImageResource(R.drawable.btn_like_focus);
				 else
					 likeImageView.setImageResource(R.drawable.btn_like_normal);
				 likeImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
						// 游客检测
//						if (Consts.IS_GUEST(mContext))
//						{
//							UIHelper.showDialogForRegister(mContext);
//							return ;
//						}
						
						item.like = !item.like;
						
						// 调用赞接口
						if (item.like)
						{
							 likeImageView.setImageResource(R.drawable.btn_like_focus);
							 item.likeCount++;
							 likeCountTextView.setText(item.likeCount + "");
							 
							 new LikeTask().execute(item.id, "true");
						}
						 else
						 {
							 item.likeCount--;
							 likeCountTextView.setText(item.likeCount + "");
							 likeImageView.setImageResource(R.drawable.btn_like_normal);
							 new LikeTask().execute(item.id, "false");
						 }
					}
				 });
				 
				 // 头像
				 RoundImageView avatarImageView = (RoundImageView)holder.getView(R.id.avatarImageView);
				 //avatarImageView.setImageResource(R.drawable.default_avatar);
				 if (!item.avatarUrl.isEmpty())
						mImageLoader.loadImage(item.avatarUrl, avatarImageView, true);
				 
				 ImageView authImageView = (ImageView)holder.getView(R.id.authImageView);
				 authImageView.setVisibility(View.VISIBLE);
				 
				 holder.setText(R.id.nameTextView, item.authorName);
				 holder.setText(R.id.worksNameTextView, item.worksName);
				 holder.setText(R.id.likeCountTextView, item.likeCount + "");
				 holder.setText(R.id.commentCountTextView, item.commentCount + "");
				 
				 // 点击用户信息栏进入作者的个人主页
				 LinearLayout authorLinearLayout = (LinearLayout)holder.getView(R.id.authorLinearLayout);
				 authorLinearLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							UIHelper.showHomepage(mContext, item.uid);
						}
					 });
				 
				 // 点击赞数目跳转到赞列表页面
				 LinearLayout likeCountLinearLayout = (LinearLayout)holder.getView(R.id.likeCountLinearLayout);
				 likeCountLinearLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							// 游客检测
//							if (Consts.IS_GUEST(mContext))
//							{
//								UIHelper.showDialogForRegister(mContext);
//								return ;
//							}
							UIHelper.showWorksCommunication(mContext, item.id, Consts.FRAGMENT_LIKE);
						}
					 });
				 
				 // 点击评论数目跳转到评论列表页面
				 LinearLayout commentCountLinearLayout = (LinearLayout)holder.getView(R.id.commentCountLinearLayout);
				 commentCountLinearLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							// 游客检测
//							if (Consts.IS_GUEST(mContext))
//							{
//								UIHelper.showDialogForRegister(mContext);
//								return ;
//							}
							UIHelper.showWorksCommunication(mContext, item.id, Consts.FRAGMENT_COMMENT);
						}
					 });
			}
		};
		mWorksListView.setAdapter(mAdapter);
	}

	/**
	 * 初始化RefreshLayout
	 */
	private void initRefreshLayout()
	{
		mRefreshLayout = (PullToRefreshListView) mHomeView.findViewById(R.id.myRefreshLayout);
		mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mRefreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						//下拉刷新
						Connection(Consts.LOAD_REFRESH);
					}
				}, 1000);
			}

			@Override
			public void onLoading() {
				mRefreshLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						//加载更多
						Connection(Consts.LOAD_MORE);
					}
				}, 1500);
			}
		});
		
		mWorksListView = mRefreshLayout.getRefreshableView();
		// 动态修改底部加载更多刷新条提示文字
		mRefreshLayout.setLabelFooter(mResources.getText(R.string.footer_pull_to_refresh_pull_label).toString(), 
				mResources.getText(R.string.footer_pull_to_refresh_release_label).toString(),
				mResources.getText(R.string.footer_pull_to_refresh_refreshing_label).toString());
	}

	/**
	 * 解析作品列表数据
	 */
	private void setWorksData() 
	{
		try {
			if (mDatas.get(Consts.DATA_LIST) != null 
					&& !mDatas.get(Consts.DATA_LIST).toString().equals(Consts.VALUE_NULL)) {
				mListDatas = JsonUtils.getJsonValuesInArray(mDatas.get(Consts.DATA_LIST).toString());
			} else {
				mListDatas = new ArrayList<HashMap<String, Object>>();
			}
			// 是否还有数据可被加载
			mCanLoadMore = StringUtils.checkValid(mDatas.get(Consts.HAS_MORE).toString()).equals("1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!mIsPullUpToLoadMore) {
			// 不是加载更多，先清空列表数据，再添加数据
			mWorksListViewDatas.clear();
		}

		for (int i = 0; i < mListDatas.size(); i++) {
			WorksInfo worksInfoItem = new WorksInfo();
			HashMap<String, Object> iteMap = mListDatas.get(i);
			worksInfoItem.id = iteMap.get(Consts.WORKS_ID).toString();
			worksInfoItem.worksName = iteMap.get(Consts.WORKS_NAME).toString();
			worksInfoItem.worksPicUrl = iteMap.get(Consts.WORKS_IMAGE_M).toString();
			worksInfoItem.commentCount = Integer.parseInt(iteMap.get(Consts.WORKS_COMMENT_COUNT).toString());
			worksInfoItem.likeCount = Integer.parseInt(iteMap.get(Consts.WORKS_LIKE_COUNT).toString());
			worksInfoItem.uid = iteMap.get(Consts.UID).toString();
			worksInfoItem.avatarUrl = iteMap.get(Consts.PHOTO).toString();
			worksInfoItem.authorName = iteMap.get(Consts.NICKNAME).toString();
			worksInfoItem.like = (Integer.parseInt(iteMap.get(Consts.WORKS_IS_LIKE).toString()) == 1 ? true : false);
			worksInfoItem.bgColor = iteMap.get(Consts.WORKS_COLOR).toString();
			mWorksListViewDatas.add(worksInfoItem);
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			// 减去下拉刷新头部和广告栏
			position = position - 2;
			
			WorksInfo worksInfoItem = mWorksListViewDatas.get(position);
			UIHelper.showWorksDetail(mContext, worksInfoItem.id);
		}
	};

	
	/**
	 * 获取推荐活动列表数据
	 * @author weigong
	 *
	 */
	private class GetRecommendActivityTask extends AsyncTask<Integer, Void, Boolean> {
		
		private int mFlag = -1;
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			mFlag = params[0];
			try {
				//Thread.sleep(1000);
				mDatas = DataServer.getInstance().getRecommendActivity();
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			//UIHelper.hideDialogForLoading();
			if(isSuccess) 
			{
				setActivitysData();
			} 
			else
			{
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
			
			// 首次加载推荐作品列表
			//UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.loading_data_tips), true);
			new GetRecommedWorks().execute(mFlag);
		}
	}
	
	/**
	 * 解析并显示调用接口返回数据
	 */
	private void setActivitysData() 
	{
		if (mDatas.get(Consts.DATA_LIST) != null && !mDatas.get(Consts.DATA_LIST).toString().isEmpty())
		{
			List<HashMap<String, Object>> activityListData = 
					JsonUtils.getJsonValuesInArray(mDatas.get(Consts.DATA_LIST).toString());
			if (activityListData != null && activityListData.size() > 0)
			{
				// 清空老数据
				adsListData.clear();
				
				int activityCount = activityListData.size();
				for (int index=0; index<activityCount; index++)
				{
					HashMap<String, Object> activityItemMap = activityListData.get(index);
					// 读取活动数据
					Ad item = new Ad();
					item.id = activityItemMap.get(Consts.ACT_ID).toString();
					item.thumbIcon = activityItemMap.get(Consts.ACT_THUMB_ICON).toString();
					item.image = activityItemMap.get(Consts.ACT_M_IMAGE).toString();
					item.title = activityItemMap.get(Consts.ACT_TITLE).toString();
					item.date = activityItemMap.get(Consts.ACT_START_TIME).toString();
					item.place = activityItemMap.get(Consts.ACT_ADDRESS).toString();
					item.detailUrl = "http://statictest.daiyan123.com/demo/activityinfo.html";
					
					adsListData.add(item);
				}
				
				// 刷新活动广告控件
				if (adsGalleryAdapter == null) // 首次加载
				{
					adsGalleryAdapter = new AdImageAdapter(mContext);
					adsGalleryAdapter.setData(adsListData);
					images_ga.setAdapter(adsGalleryAdapter);
					new Thread(new CountTime()).start();
				}
				
				adsFocusPageView.setPageSize(adsListData.size());
				adsGalleryAdapter.notifyDataSetChanged();
			}
		}
	}

	class CountTime implements Runnable
	{
		int gallerypisition = 0;

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			while (true)
			{
				try
				{
					Thread.sleep(3 * 1000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Message message = new Message();
				Bundle date = new Bundle();
				date.putInt("pos", gallerypisition);
				message.setData(date);
				// Message message = new Message();
				message.what = 1;
				autoGalleryHandler.sendMessage(message);// 發送消息
				
				// 广告轮播
				gallerypisition = gallerypisition +1;
				gallerypisition %= images_ga.getCount();
			}
		}
	}
	

	final Handler autoGalleryHandler = new Handler()
	{
		public void handleMessage(Message message)
		{
			super.handleMessage(message);
			switch (message.what)
			{
				case 1:
				{
					// 更新当前显示广告
					int position = message.getData().getInt("pos");
					images_ga.setSelection(position);
					adsFocusPageView.setPageIndex(position);
					break;
				}
			}
		}
	};

	
	
	/**
	 * 调用接口加载数据
	 * 
	 * @param flag 0:普通加载, 1:下拉刷新, 2:加载更多
	 */
	public void Connection(int flag) {
		switch (flag) {
		case Consts.LOAD_NORMAL: // 第一次加载数据，先加载广告栏，再加载推荐作品列表
			mPageIndex = 0;
			String loading = mResources.getString(R.string.loading_data_tips);
			UIHelper.showDialogForLoading(mContext, loading, true);
			new GetRecommendActivityTask().execute(flag);
			break;
		case Consts.LOAD_REFRESH: // 下拉刷新
			mPageIndex = 0;
			new GetRecommendActivityTask().execute(flag);
			break;
		case Consts.LOAD_MORE: // 加载更多作品
			mPageIndex = mPageIndex + 1;
			mIsPullUpToLoadMore = true;
			new GetRecommedWorks().execute(flag);
			break;
		}
	}

	/**
	 * 获取首页推荐作品列表数据
	 */
	private class GetRecommedWorks extends AsyncTask<Integer, Void, Boolean> {

		private int mFlag = -1;

		@Override
		protected Boolean doInBackground(Integer... params) {
			mFlag = params[0];
			try {
				if(mIsPullUpToLoadMore && !mCanLoadMore) // 【加载更多】到最后一页了
				{
					mDatas = null;
					Consts.NET_WORK_ERROR = mResources.getString(R.string.loading_no_more_data_tips);
				} 
				else 
				{
					mDatas = DataServer.getInstance().getRecommendWorks(mPageIndex);	
				}
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			
			// 【加载更多】到最后一页了
			if(mIsPullUpToLoadMore && !mCanLoadMore) 
			{
				ToastUtils.show(getActivity(), "回到第一个作品");
				mWorksListView.setAdapter(mAdapter);
				mWorksListView.setSelection(0);
				if (mResumeTitleListener != null)
					mResumeTitleListener.onRest();
				mRefreshLayout.onRefreshComplete();
				mIsPullUpToLoadMore = false;
				return ;
			}
			
			
			// 加载数据显示处理
			if (isSuccess) {
				// 添加列表数据
				setWorksData();
				// 更新列表显示
				mAdapter.notifyDataSetChanged();
				mHasLoadedOnce = true;
			} else {
				ToastUtils.show(getActivity(), Consts.NET_WORK_ERROR);
			}
			
			// 后处理
			switch (mFlag) {
			case Consts.LOAD_NORMAL:
				UIHelper.hideDialogForLoading();
				break;
			case Consts.LOAD_REFRESH:
				mRefreshLayout.onRefreshComplete();
				break;
			case Consts.LOAD_MORE:
				mRefreshLayout.onRefreshComplete();
				mIsPullUpToLoadMore = false;
				break;
			}
		}
	}
	
	/**
	 * 点赞
	 */
	private class LikeTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				String worksId = params[0];
				boolean like = Boolean.parseBoolean(params[1].toString());
				if (like)
				{
					mDatas = DataServer.getInstance().like(worksId);
				}
				else
				{
					mDatas = DataServer.getInstance().cancelLike(worksId);
				}
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			// 点赞成功后处理
			//mAdapter.notifyDataSetChanged();
		}
	}


}