package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.app.widget.RefreshLayout;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.app.widget.RefreshLayout.OnLoadListener;
import com.daiyan.handwork.bean.WorksInfo;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ViewHolder;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 赞过的作品列表
 * 
 * @author 魏工
 * @Date 2015年05月09日
 */
public class LikeWorks extends BaseActivity implements OnClickListener {

	private Activity mContext;
	private Resources mResources;
	
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;

	/** 带下拉刷新和加载更多的Layout */
	private RefreshLayout mRefreshLayout;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_like_works);
		mContext = LikeWorks.this;
		mResources = getResources();
		
		initView();
		initRefreshLayout();
		
		// 首次加载数据
		Connection(Consts.LOAD_NORMAL);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (Consts.REQUEST_REFRESH_WORKS_LIST == requestCode && data != null) 
		{
			// 刷新作品列表
			boolean refreshList  = data.getBooleanExtra(Consts.EXTRA_REFRESH_WORKS_LIST, false);
			if (refreshList)
			{
				Connection(Consts.LOAD_REFRESH);
			}
		}
	}
	
	/**
	 * 初始化列表
	 */
	private void initView() {

		initTitleBar();

		// 点赞作品列表
		mWorksListView = (ListView) this.findViewById(R.id.id_lv_only);
		mWorksListView.setOnItemClickListener(onItemClickListener);
		
		// 初始化适配器
		mAdapter = new CommonAdapter<WorksInfo>(mContext, mWorksListViewDatas, 
				R.layout.item_for_like_works_listview) {
			@Override
			public void convert(ViewHolder holder, final WorksInfo item) {
				 ImageView worksImageView = (ImageView)holder.getView(R.id.worksImageView);
				 //worksImageView.setImageResource(R.drawable.item_works_pic);
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
				 likeImageView.setImageResource(R.drawable.btn_like_focus);
				 likeImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
						// 游客检测
//						if (Consts.IS_GUEST(mContext))
//						{
//							UIHelper.showDialogForRegister(mContext);
//							return ;
//						}
						
						item.like = false;
						item.likeCount--;
						likeCountTextView.setText(item.likeCount + "");
						likeImageView.setImageResource(R.drawable.btn_like_normal);
					    new LikeTask().execute(item.id, "false");
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
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(
				R.string.mine_like));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		}

	}

	/**
	 * 初始化RefreshLayout
	 */
	private void initRefreshLayout() {
		// 设置下拉刷新和加载更多
		mRefreshLayout = (RefreshLayout) this.findViewById(R.id.myRefreshLayout);
		mRefreshLayout.setListView(mWorksListView);
		mRefreshLayout.setColorSchemeResources(R.color.green, R.color.orange,
				R.color.white, R.color.light_orange_pre);
		// 设置下拉刷新监听器
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
		});
		// 设置加载更多监听器
		mRefreshLayout.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
				mRefreshLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						//加载更多
						Connection(Consts.LOAD_MORE);
					}
				}, 1500);
			}
		});
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
			worksInfoItem.like = true;
			
			// 作者信息
			if (iteMap.containsKey(Consts.WORKS_AUTHOR))
			{
				HashMap<String, Object> authorMap;
				try {
					authorMap = JsonUtils.getJsonValues(iteMap.get(Consts.WORKS_AUTHOR).toString());
					if (authorMap != null)
					{
						worksInfoItem.uid = authorMap.get(Consts.UID).toString();
						worksInfoItem.avatarUrl = authorMap.get(Consts.PHOTO).toString();
						worksInfoItem.authorName = authorMap.get(Consts.NICKNAME).toString();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mWorksListViewDatas.add(worksInfoItem);
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WorksInfo worksInfoItem = mWorksListViewDatas.get(position);
			UIHelper.showWorksDetail(mContext, worksInfoItem.id);
		}
	};

	/**
	 * 调用接口加载数据
	 * 
	 * @param flag 0:普通加载, 1:下拉刷新, 2:加载更多
	 */
	private void Connection(int flag) {
		switch (flag) {
		case Consts.LOAD_NORMAL: // 第一次加载数据，先加载广告栏，再加载推荐作品列表
			mPageIndex = 0;
			String loading = mResources.getString(R.string.loading_data_tips);
			UIHelper.showDialogForLoading(mContext, loading, false);
			new GetLikeWorks().execute(flag);
			break;
		case Consts.LOAD_REFRESH: // 下拉刷新
			mPageIndex = 0;
			new GetLikeWorks().execute(flag);
			break;
		case Consts.LOAD_MORE: // 加载更多作品
			mPageIndex = mPageIndex + 1;
			mIsPullUpToLoadMore = true;
			new GetLikeWorks().execute(flag);
			break;
		}
	}

	/**
	 * 获取首页推荐作品列表数据
	 */
	private class GetLikeWorks extends AsyncTask<Integer, Void, Boolean> {

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
					mDatas = DataServer.getInstance().getLikeWorks(mPageIndex);	
				}
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			// 加载数据显示处理
			if (isSuccess) {
				// 添加列表数据
				setWorksData();
				// 更新列表显示
				mAdapter.notifyDataSetChanged();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
			
			// 后处理
			switch (mFlag) {
			case Consts.LOAD_NORMAL:
				UIHelper.hideDialogForLoading();
				break;
			case Consts.LOAD_REFRESH:
				UIHelper.hideDialogForLoading();
				mRefreshLayout.setRefreshing(false);
				break;
			case Consts.LOAD_MORE:
				mRefreshLayout.setLoading(false);
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
				mDatas = DataServer.getInstance().cancelLike(worksId);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			// 刷新点赞列表
			UIHelper.showDialogForLoading(mContext, 
					mResources.getString(R.string.refreshing_data_tips), false);
			Connection(Consts.LOAD_REFRESH);
		}
	}
}