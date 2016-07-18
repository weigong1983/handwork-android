package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.app.widget.pull.PullToRefreshGridView;
import com.daiyan.handwork.app.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.daiyan.handwork.bean.WorksInfo;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ViewHolder;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 作品坊（收藏中）
 * 
 * @author 魏工
 * @Date 2015年05月09日
 */
public class WorksBrowse extends BaseActivity implements OnClickListener {

	private Activity mContext;
	private Resources mResources;
	
	
	private int mBrowseType = 0; // 0:作品坊； 1：热卖中
	private int numOfColumns = 2;

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	private List<HashMap<String, Object>> mListDatas;
	
	/** 带下拉刷新和加载更多的Layout */
	private PullToRefreshGridView mRefreshLayout;

	/** 作品列表 */
	private GridView mGridView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_works_browse);
		mContext = WorksBrowse.this;
		mResources = getResources();
		isPrepared = true;
		
		mBrowseType = getIntent().getIntExtra(Consts.BROWSE_WORKS_TYPE, Consts.BROWSE_WORKS_SHOP);

		initView();
		
		// 首次加载数据
		Connection(Consts.LOAD_NORMAL);
	}

	/**
	 * 初始化列表
	 */
	private void initView() {

		initTitleBar();
		initRefreshLayout();

		mGridView.setOnItemClickListener(onItemClickListener);
		if (Consts.BROWSE_WORKS_SHOP == mBrowseType)
			numOfColumns = 3;
		mGridView.setNumColumns(numOfColumns);
		
		// 初始化适配器
		mAdapter = new CommonAdapter<WorksInfo>(mContext, mWorksListViewDatas, 
				R.layout.item_for_works_gridview) {
			@Override
			public void convert(ViewHolder holder, final WorksInfo item) {
				// 动态计算ITEM大小
				LinearLayout worksLinearLayout = (LinearLayout)holder.getView(R.id.worksLinearLayout);
				int screenWidth = SystemUtils.getScreenWidth(mContext);
				int itemSize = screenWidth / numOfColumns;
				LayoutParams params = (LayoutParams) worksLinearLayout.getLayoutParams();
				params.width = itemSize;
				params.height = itemSize;
				worksLinearLayout.setLayoutParams(params);
				
				ImageView worksImageView = (ImageView)holder.getView(R.id.worksImageView);
				// 设置背景色值
				 worksImageView.setBackgroundColor(Color.parseColor(item.bgColor));
				 //worksImageView.setImageResource(R.drawable.item_works_pic);
				 if (!item.worksPicUrl.isEmpty())
						mImageLoader.loadImage(item.worksPicUrl, worksImageView, true);
				 worksImageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							UIHelper.showWorksDetail(mContext, item.id);
						}
					 });

				final TextView priceTextView = (TextView) holder
						.getView(R.id.priceTextView);
				if (item.issale == 2 && mBrowseType == 1) 
				{ // 1：非卖品； 2：出售中； 3：已售
					priceTextView.setVisibility(View.VISIBLE);
					
					if (item.marktype == 1)
						priceTextView.setText(item.price);
					else
						priceTextView.setText(getResources().getString(R.string.bargain_price));
				} 
				else 
				{
					priceTextView.setVisibility(View.GONE);
				}
			}
		};
		mGridView.setAdapter(mAdapter);
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
		
		// 设置标题
		if (Consts.BROWSE_WORKS_SHOP == mBrowseType)
		{
			mTitleTextView.setText(getResources().getString(
					R.string.title_workshop));
		}
		else if (Consts.BROWSE_WORKS_COLLECTING == mBrowseType)
		{
			mTitleTextView.setText(getResources().getString(
					R.string.title_collecting));
		}
	}
	
	/**
	 * 初始化RefreshLayout
	 */
	private void initRefreshLayout()
	{
		mRefreshLayout = (PullToRefreshGridView) this.findViewById(R.id.myRefreshLayout);
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
		
		mGridView = mRefreshLayout.getRefreshableView();
		// 动态修改底部加载更多刷新条提示文字
		mRefreshLayout.setLabelFooter(mResources.getText(R.string.footer_pull_to_refresh_pull_label).toString(), 
				mResources.getText(R.string.footer_pull_to_refresh_release_label).toString(),
				mResources.getText(R.string.footer_pull_to_refresh_refreshing_label).toString());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		}

	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			WorksInfo item = mWorksListViewDatas.get(position);
			UIHelper.showWorksDetail(mContext, item.id);
		}
	};
	
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
			worksInfoItem.worksPicUrl = iteMap.get(Consts.WORKS_IMAGE_S).toString();
			worksInfoItem.commentCount = Integer.parseInt(iteMap.get(Consts.WORKS_COMMENT_COUNT).toString());
			worksInfoItem.likeCount = Integer.parseInt(iteMap.get(Consts.WORKS_LIKE_COUNT).toString());
//			worksInfoItem.uid = iteMap.get(Consts.UID).toString();
//			worksInfoItem.avatarUrl = iteMap.get(Consts.PHOTO).toString();
//			worksInfoItem.authorName = iteMap.get(Consts.NICKNAME).toString();
//			worksInfoItem.like = (Integer.parseInt(iteMap.get(Consts.WORKS_IS_LIKE).toString()) == 1 ? true : false);
			worksInfoItem.bgColor = iteMap.get(Consts.WORKS_COLOR).toString();
			worksInfoItem.issale = Integer.parseInt(iteMap.get(Consts.WORKS_IS_SALE).toString());
			worksInfoItem.price = "￥" + iteMap.get(Consts.WORKS_PRICE).toString();
			worksInfoItem.marktype = Integer.parseInt(iteMap.get(Consts.WORKS_MARK_TYPE).toString());
			mWorksListViewDatas.add(worksInfoItem);
		}
	}
	
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
			UIHelper.showDialogForLoading(mContext, loading, true);
			new GetMyWorks().execute(flag);
			break;
		case Consts.LOAD_REFRESH: // 下拉刷新
			mPageIndex = 0;
			new GetMyWorks().execute(flag);
			break;
		case Consts.LOAD_MORE: // 加载更多作品
			mPageIndex = mPageIndex + 1;
			mIsPullUpToLoadMore = true;
			new GetMyWorks().execute(flag);
			break;
		}
	}
	
	/**
	 * 获取我的作品列表数据
	 */
	private class GetMyWorks extends AsyncTask<Integer, Void, Boolean> {

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
					mDatas = DataServer.getInstance().getAllWorks(mPageIndex, mBrowseType);
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
				
				mHasLoadedOnce = true;
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
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
}