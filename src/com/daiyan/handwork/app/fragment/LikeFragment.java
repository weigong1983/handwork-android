package com.daiyan.handwork.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.bean.Comment;
import com.daiyan.handwork.bean.FindItemInfo;
import com.daiyan.handwork.bean.LikeItemInfo;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 赞过的人
 * @author AA
 * @Date 2015-01-17
 */
public class LikeFragment extends BaseFragment {

	private Activity mContext;
	private Resources mResources;
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);

	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	private List<HashMap<String, Object>> mListDatas;
	
	/** 首页布局视图 */
	private View mHomeView;
	/** GridView */
	private GridView mGridView;
	/** 适配器 */
	private CommonAdapter<LikeItemInfo> mAdapter;
	/** 赞过的用户列表数据 */
	private List<LikeItemInfo> mGridViewDatas = new ArrayList<LikeItemInfo>();

	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次 */
	private boolean mHasLoadedOnce;
	
	private String mWorksId;
	

	public LikeFragment() {
		// TODO Auto-generated constructor stub
	}

	public LikeFragment(String worksId)
	{
		mWorksId = worksId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mHomeView == null) {
			mHomeView = inflater.inflate(R.layout.fragment_like, container, false);
			mContext = getActivity();
			mResources = getResources();
			isPrepared = true;
			
			initViews();
			
			lazyLoad();
		}
		
		ViewGroup parent = (ViewGroup) mHomeView.getParent();
		if (parent != null) {
			parent.removeView(mHomeView);
		}
		return mHomeView;
	}
	
	private void initViews()
	{
		mGridView = (GridView)mHomeView.findViewById(R.id.id_gv_like);
		mGridView.setOnItemClickListener(onItemClickListener);
		
		// 初始化适配器
		mAdapter = new CommonAdapter<LikeItemInfo>(mContext, mGridViewDatas, R.layout.item_for_like_gridview) {
			@Override
			public void convert(ViewHolder holder, LikeItemInfo item) {
				RoundImageView avatarImageView = (RoundImageView)holder.getView(R.id.avatarImageView);
				 if (!item.avatarUrl.isEmpty())
					mImageLoader.loadImage(item.avatarUrl, avatarImageView, true);
			}
		};
		mGridView.setAdapter(mAdapter);
	}
	
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//UIHelper.showSpokePub(mContext, templetIDs[position]);
		}
	};

	@Override
	protected void lazyLoad() {
		if(!isPrepared || !isVisible || mHasLoadedOnce) {
			return ;
		}
		
		// 加载点赞数据
		String loading = mResources.getString(R.string.loading_data_tips);
		UIHelper.showDialogForLoading(mContext, loading, true);
		new GetWorksLikeTask().execute();
	}
	
	/**
	 * 解析点赞列表数据
	 */
	private void setLikesData() 
	{
		try {
			if (mDatas.get(Consts.DATA_LIST) != null 
					&& !mDatas.get(Consts.DATA_LIST).toString().equals(Consts.VALUE_NULL)) {
				mListDatas = JsonUtils.getJsonValuesInArray(mDatas.get(Consts.DATA_LIST).toString());
			} else {
				mListDatas = new ArrayList<HashMap<String, Object>>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 清空数据
		mGridViewDatas.clear();

		for (int i = 0; i < mListDatas.size(); i++) {
			LikeItemInfo item = new LikeItemInfo();
			HashMap<String, Object> iteMap = mListDatas.get(i);

			item.uid = iteMap.get(Consts.UID).toString();
			item.avatarUrl = iteMap.get(Consts.PHOTO).toString();
			item.nickName = iteMap.get(Consts.NICKNAME).toString();
			mGridViewDatas.add(item);
		}
	}
	
	/**
	 * 获取点赞头像列表
	 */
	private class GetWorksLikeTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try 
			{
				mDatas = DataServer.getInstance().getWorksUpclicks(mWorksId);	
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			
			// 加载数据显示处理
			if (isSuccess) {
				// 添加列表数据
				setLikesData();
				// 更新列表显示
				mAdapter.notifyDataSetChanged();
				
				mHasLoadedOnce = true;
			} 
			else 
			{
				ToastUtils.show(getActivity(), Consts.NET_WORK_ERROR);
			}
		}
	}
}
