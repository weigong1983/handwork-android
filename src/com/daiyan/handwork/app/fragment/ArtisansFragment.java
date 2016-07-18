package com.daiyan.handwork.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.app.widget.pull.PullToRefreshListView;
import com.daiyan.handwork.app.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.daiyan.handwork.bean.ArtisansInfo;
import com.daiyan.handwork.bean.FindItemInfo;
import com.daiyan.handwork.bean.LikeItemInfo;
import com.daiyan.handwork.bean.WorksThumbInfo;
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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 工艺家
 * @author AA
 * @Date 2015-01-17
 */
public class ArtisansFragment extends BaseFragment {

	private Activity mContext;
	private Resources mResources;
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	private List<HashMap<String, Object>> mListDatas;
	
	
	/** 带下拉刷新和加载更多的Layout */
	private PullToRefreshListView mRefreshLayout;
	
	/** 首页布局视图 */
	private View mParentView;
	
	/** 工艺家列表 */
	private ListView artisansListView;
	/** 适配器 */
	private CommonAdapter<ArtisansInfo> mAdapter;
	/** 列表数据 */
	private List<ArtisansInfo> mArtisansViewDatas = new ArrayList<ArtisansInfo>();
	
	private CommonAdapter<WorksThumbInfo> mWorksGridViewAdapter;

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
	
	/** 工艺品类ID */
	private String mClassId;
	
	
	
	public ArtisansFragment() {
		// TODO Auto-generated constructor stub
	}

	public ArtisansFragment(String classId)
	{
		mClassId = classId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mParentView == null) {
			mParentView = inflater.inflate(R.layout.fragment_artisans, container, false);
			mContext = getActivity();
			mResources = getResources();
			isPrepared = true;
			
			initViews();
			
			lazyLoad();
		}
		
		ViewGroup parent = (ViewGroup) mParentView.getParent();
		if (parent != null) {
			parent.removeView(mParentView);
		}
		return mParentView;
	}
	
	private void initViews()
	{
		mRefreshLayout = (PullToRefreshListView) mParentView.findViewById(R.id.myRefreshLayout);
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
		
		// 动态修改底部加载更多刷新条提示文字
		mRefreshLayout.setLabelFooter(mResources.getText(R.string.footer_pull_to_refresh_pull_label).toString(), 
						mResources.getText(R.string.footer_pull_to_refresh_release_label).toString(),
						mResources.getText(R.string.footer_pull_to_refresh_refreshing_label).toString());
				
		// 初始化列表控件
		artisansListView = mRefreshLayout.getRefreshableView();
		//artisansListView.setOnItemClickListener(onListItemClickListener);
		
		// 初始化适配器
		mAdapter = new CommonAdapter<ArtisansInfo>(mContext, mArtisansViewDatas,
				R.layout.item_for_artisans_listview) {
			@Override
			public void convert(ViewHolder holder, final ArtisansInfo item) {
				ImageView avatarImageView = (ImageView)holder.getView(R.id.avatarImageView);
				if (!item.avatarUrl.isEmpty())
					mImageLoader.loadImage(item.avatarUrl, avatarImageView, true);
				
				holder.setText(R.id.nameTextView, item.nickname);
				holder.setText(R.id.worksCountTextView, item.worksCount + "件作品");
				
				// 点击用户信息栏进入作者的个人主页
				 LinearLayout authorLinearLayout = (LinearLayout)holder.getView(R.id.authorLinearLayout);
				 authorLinearLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							UIHelper.showHomepage(mContext, item.uid);
						}
					 });
				
				GridView worksGridView = (GridView)holder.getView(R.id.worksGridView);
				mWorksGridViewAdapter = new CommonAdapter<WorksThumbInfo>(mContext, item.representativeWorks,
						R.layout.item_for_artisans_works_gridview) {
					@Override
					public void convert(ViewHolder holder, final WorksThumbInfo item) {
						ImageView worksPicImageView = (ImageView)holder.getView(R.id.worksPicImageView);
						// 设置背景色值
						worksPicImageView.setBackgroundColor(Color.parseColor(item.bgColor));
						if (!item.worksPicUrl.isEmpty())
							mImageLoader.loadImage(item.worksPicUrl, worksPicImageView, true);
						
						worksPicImageView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								UIHelper.showWorksDetail(mContext, item.id);
							}
						 });
					}
				};
				
				// 动态设置横向滚动GridView
				setWorksGridView(worksGridView, item.representativeWorks.size());
				worksGridView.setAdapter(mWorksGridViewAdapter);
			}
		};
		artisansListView.setAdapter(mAdapter);
	}
	
//	private OnItemClickListener onListItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			//UIHelper.showSpokePub(mContext, templetIDs[position]);
//		}
//	};

	@Override
	protected void lazyLoad() {
		if(!isPrepared || !isVisible || mHasLoadedOnce) {
			return ;
		}
		
		Connection(Consts.LOAD_NORMAL);
	}

	/**
	 * 解析作品列表数据
	 */
	private void setWorksData() 
	{
		try {
			if (mDatas.get(Consts.DATA_LIST) != null 
					&& !mDatas.get(Consts.DATA_LIST).toString().equals(Consts.VALUE_NULL)) 
			{
				mListDatas = JsonUtils.getJsonValuesInArray(mDatas.get(Consts.DATA_LIST).toString());
			} else {
				mListDatas = new ArrayList<HashMap<String, Object>>();
			}
			// 是否还有数据可被加载
			mCanLoadMore = StringUtils.checkValid(mDatas.get(Consts.HAS_MORE).toString()).equals("1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 空数据
		if (mListDatas.size() == 0)
			return ;
		
		// 不是加载更多，先清空列表数据，再添加数据
		if (!mIsPullUpToLoadMore) {
			mArtisansViewDatas.clear();
		}

		int artisanCount = mListDatas.size();
		for (int i = 0; i < artisanCount; i++) {
			ArtisansInfo artisansItem = new ArtisansInfo();
			HashMap<String, Object> artisansMap = mListDatas.get(i);

			artisansItem.uid = artisansMap.get(Consts.UID).toString();
			artisansItem.nickname = artisansMap.get(Consts.NICKNAME).toString();
			artisansItem.avatarUrl = artisansMap.get(Consts.PHOTO).toString();
			artisansItem.worksCount = Integer.parseInt(artisansMap.get(Consts.ARTISANS_WORKS_COUNT).toString());
			
			// 读取该工艺家的前5个作品
			if (artisansMap.get(Consts.ARTISANS_WORKS_LIST) != null 
					&& !artisansMap.get(Consts.ARTISANS_WORKS_LIST).toString().equals(Consts.VALUE_NULL)) 
			{
				List<HashMap<String, Object>> worksListData = 
						JsonUtils.getJsonValuesInArray(artisansMap.get(Consts.ARTISANS_WORKS_LIST).toString());
				
				if (worksListData.size() > 0)
				{
					int worksCount = worksListData.size();
					for(int j = 0; j < worksCount; j++) 
					{
						HashMap<String, Object> worksMap = worksListData.get(j);
						WorksThumbInfo worksItem = new WorksThumbInfo();
						worksItem.id = worksMap.get(Consts.WORKS_ID).toString();
						worksItem.worksPicUrl = worksMap.get(Consts.WORKS_IMAGE_S).toString();
						worksItem.bgColor = worksMap.get(Consts.WORKS_COLOR).toString();
						
						artisansItem.representativeWorks.add(worksItem);
					}
				}
				else
				{
					continue; // 没作品的工艺家不显示
				}
			}
			
			mArtisansViewDatas.add(artisansItem);
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
			new GetArtisansTask().execute(flag);
			break;
		case Consts.LOAD_REFRESH: // 下拉刷新
			mPageIndex = 0;
			new GetArtisansTask().execute(flag);
			break;
		case Consts.LOAD_MORE: // 加载更多作品
			mPageIndex = mPageIndex + 1;
			mIsPullUpToLoadMore = true;
			new GetArtisansTask().execute(flag);
			break;
		}
	}

	/**
	 * 按工艺品类获取工艺家列表数据
	 */
	private class GetArtisansTask extends AsyncTask<Integer, Void, Boolean> {

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
					mDatas = DataServer.getInstance().getUserByClassid(mClassId, mPageIndex);	
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
	 * 动态设置GirdView参数
	 * @param gridView
	 * @param size
	 */
    private void setWorksGridView(GridView gridView, int size) {
        final int WORK_ITEM_SIZE = 135;
        final int H_SPACING = 10;
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (WORK_ITEM_SIZE + H_SPACING) * density);
        int itemWidth = (int) (WORK_ITEM_SIZE * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        gridView.setNumColumns(size); // 设置列数量=列表集合
    }
}
