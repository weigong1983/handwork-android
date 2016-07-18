package com.daiyan.handwork.app.fragment;

import java.util.ArrayList;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.bean.FindItemInfo;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ViewHolder;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.BitmapUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 发现
 * @author AA
 * @Date 2015-01-17
 */
public class FindFragment extends BaseFragment {

	private Activity mContext;
	private Resources mResources;
	
	/** 首页布局视图 */
	private View mFindView;
	/** GridView */
	private GridView mGridView;
	/** 适配器 */
	private CommonAdapter<FindItemInfo> mAdapter;
	/** List */
	private List<FindItemInfo> mGridViewDatas;

	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次 */
	private boolean mHasLoadedOnce;
	
	
	
	public FindFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mFindView == null) {
			mFindView = inflater.inflate(R.layout.fragment_find, container, false);
			mContext = getActivity();
			mResources = getResources();
			isPrepared = true;
			
			mGridViewDatas = getGridViewDatas();
		}
		
		ViewGroup parent = (ViewGroup) mFindView.getParent();
		if (parent != null) {
			parent.removeView(mFindView);
		}
		return mFindView;
	}
	
	/**
	 * 获得发起界面GridView数据
	 * @return
	 */
	private List<FindItemInfo> getGridViewDatas() {
		TypedArray images = mResources.obtainTypedArray(R.array.find_item_imgs);
		String[] names = mResources.getStringArray(R.array.find_item_names);
				
		List<FindItemInfo> list = new ArrayList<FindItemInfo>();
		for(int i=0; i<names.length; i++) {
			int imageId = images.getResourceId(i, 0);
			// 加快读取速度
			//Bitmap bitmap= BitmapFactory.decodeResource(mResources, imageId);
			Bitmap bitmap = BitmapUtils.readBitMap(mContext, imageId);
			FindItemInfo bean = new FindItemInfo();
			bean.image = bitmap;
			list.add(bean);
		}
		images.recycle();
		return list;
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position)
			{
			case 0: // 作品坊
				UIHelper.showWorksBrowse(mContext, Consts.BROWSE_WORKS_SHOP);
				break;
			case 1: // 工艺家
				UIHelper.showArtisans(mContext);
				break;
			case 2: // 热卖中
				UIHelper.showWorksBrowse(mContext, Consts.BROWSE_WORKS_COLLECTING);
				break;
			case 3: // 研究院
				UIHelper.showInstitute(mContext);
				break;
			default:
				break;
			}
			
		}
	};

	@Override
	protected void lazyLoad() {
		if(!isPrepared || !isVisible || mHasLoadedOnce) {
			return ;
		}
		
		mGridView = (GridView)mFindView.findViewById(R.id.id_gv_find);
		mAdapter = new CommonAdapter<FindItemInfo>(mContext, mGridViewDatas, R.layout.item_for_find_gridview) {

			@Override
			public void convert(ViewHolder holder, FindItemInfo item) {
				holder.setImageBitmap(R.id.id_iv_item_for_find, item.image);
			}
		};
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(onItemClickListener);
	}
}
