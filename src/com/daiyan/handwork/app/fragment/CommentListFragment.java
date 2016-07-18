package com.daiyan.handwork.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.app.widget.AdImageAdapter;
import com.daiyan.handwork.app.widget.PointPageView;
import com.daiyan.handwork.app.widget.RefreshLayout;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.app.widget.RefreshLayout.OnLoadListener;
import com.daiyan.handwork.bean.Ad;
import com.daiyan.handwork.bean.Comment;
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
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 评论列表
 * 
 * @author 魏工
 * @Date 2015年05月09日
 */
public class CommentListFragment extends BaseFragment {

	private View mHomeView;
	private Activity mContext;
	private Resources mResources;
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);

	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	private List<HashMap<String, Object>> mListDatas;

	/** 带下拉刷新和加载更多的Layout */
	private RefreshLayout mRefreshLayout;
	
	/** 评论列表 */
	private ListView mListView;
	private CommonAdapter<Comment> mAdapter;
	private List<Comment> mListViewDatas = new ArrayList<Comment>();

	/** 是否还有数据可加载 */
	private boolean mCanLoadMore = true;
	/** 是否加载更多 */
	private boolean mIsPullUpToLoadMore;

	/** 列表数据总数 */
	private int mTotal = 0;
	/** 页码 */
	private int mPageIndex = 0;


	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;

	/** 是否已被加载过一次 */
	private boolean mHasLoadedOnce;

	private String mWorksId;
	
	private Button mCommentButton;
	private EditText mContentEditText;
	private String mContent;
	
	public int mCommentCount = 0;
	
	
	
	public CommentListFragment() {
	}

	public CommentListFragment(String worksId) {
		mWorksId = worksId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mHomeView == null) {
			mHomeView = inflater.inflate(R.layout.fragment_comment_list, container, false);
			mContext = getActivity();
			mResources = getResources();
			isPrepared = true;
			
			// 初始化界面元素 
			initView();
			initRefreshLayout();
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

		Connection(Consts.LOAD_NORMAL);
	}

	
	/**
	 * 初始化列表
	 */
	private void initView() {
		// 推荐作品列表
		mListView = (ListView) mHomeView.findViewById(R.id.id_lv_only);
		mListView.setOnItemClickListener(onItemClickListener);
		
		mCommentButton = (Button) mHomeView.findViewById(R.id.commentButton);
		mCommentButton.setOnClickListener(mOnClickListener);
		mContentEditText = (EditText) mHomeView.findViewById(R.id.contentEditText);
		
		// 初始化适配器
		mAdapter = new CommonAdapter<Comment>(mContext, mListViewDatas,
				R.layout.item_for_comment_listview) {
			@Override
			public void convert(ViewHolder holder, final Comment item) {
				 RoundImageView avatarImageView = (RoundImageView)holder.getView(R.id.avatarImageView);
				 avatarImageView.setImageResource(R.drawable.default_avatar);
				 if (!item.avatarUrl.isEmpty())
						mImageLoader.loadImage(item.avatarUrl, avatarImageView, true);
				 holder.setText(R.id.nameTextView, item.nickName);
				 holder.setText(R.id.timeTextView, item.time);
				 holder.setText(R.id.contentTextView, item.content);
			}
		};
		mListView.setAdapter(mAdapter);
	}
	
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
			case R.id.commentButton: // 发表评论
				publish();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 初始化RefreshLayout
	 */
	private void initRefreshLayout() 
	{
		// 设置下拉刷新和加载更多
		mRefreshLayout = (RefreshLayout) mHomeView.findViewById(R.id.myRefreshLayout);
		mRefreshLayout.setListView(mListView);
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
	 * 解析评论列表数据
	 */
	private void setCommentsData() 
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
			mListViewDatas.clear();
		}

		for (int i = 0; i < mListDatas.size(); i++) {
			Comment worksInfoItem = new Comment();
			HashMap<String, Object> iteMap = mListDatas.get(i);
			worksInfoItem.id = iteMap.get(Consts.WORKS_ID).toString();
			worksInfoItem.uid = iteMap.get(Consts.UID).toString();
			worksInfoItem.avatarUrl = iteMap.get(Consts.PHOTO).toString();
			worksInfoItem.nickName = iteMap.get(Consts.NICKNAME).toString();
			// 匿名显示
			if (StringUtils.isEmpty(worksInfoItem.nickName))
				worksInfoItem.nickName = "网友";
			
			worksInfoItem.time = iteMap.get(Consts.COMMENT_TIME).toString();
			worksInfoItem.content = iteMap.get(Consts.COMMENT_CONTENT).toString();
			mListViewDatas.add(worksInfoItem);
		}
		
		// 记录当前评论数目，用于作品详情页刷新显示
		mCommentCount = mListViewDatas.size();
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
			new GetWorksCommentsTask().execute(flag);
			break;
		case Consts.LOAD_REFRESH: // 下拉刷新
			mPageIndex = 0;
			new GetWorksCommentsTask().execute(flag);
			break;
		case Consts.LOAD_MORE: // 加载更多作品
			mPageIndex = mPageIndex + 1;
			mIsPullUpToLoadMore = true;
			new GetWorksCommentsTask().execute(flag);
			break;
		}
	}
	
	/**
	 * 获取评论列表
	 */
	private class GetWorksCommentsTask extends AsyncTask<Integer, Void, Boolean> {

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
					mDatas = DataServer.getInstance().getWorksComments(mWorksId, mPageIndex);	
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
				setCommentsData();
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
				mRefreshLayout.setRefreshing(false);
				break;
			case Consts.LOAD_MORE:
				mRefreshLayout.setLoading(false);
				mIsPullUpToLoadMore = false;
				break;
			}
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//Comment commentItem = mListViewDatas.get(position);
			//UIHelper.showWorksDetail(mContext);
		}
	};
	
	/**
	 * 发表评论
	 */
	private void publish() {
		//获取并验证用户输入
		mContent = mContentEditText.getText().toString();
		if(StringUtils.isEmpty(mContent)) {
			ToastUtils.show(mContext, mResources.getString(R.string.comment_pub_empty_tips));
			mContentEditText.requestFocus();
			mContentEditText.setFocusable(true);
			mContentEditText.setFocusableInTouchMode(true);
			return;
		}
		
		new CommentTask().execute();
	}
	
	private class CommentTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.comment_pub_loading_text), false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(500);
				mDatas = DataServer.getInstance().comment(mWorksId, mContent);
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
				// 清空输入框内容
				mContentEditText.setText("");
				ToastUtils.show(mContext, mResources.getString(R.string.comment_success_hint_tips));
				// 刷新评论列表
				Connection(Consts.LOAD_REFRESH);
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);				
			}
		}		
	}
}