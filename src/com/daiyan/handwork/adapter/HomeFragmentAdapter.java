package com.daiyan.handwork.adapter;

import java.util.List;

import com.daiyan.handwork.common.HomeFragmentViewHolder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 首页适配器
 * @author AA
 * @Date 2014-11-25
 * @param <T>
 */
public abstract class HomeFragmentAdapter<T> extends BaseAdapter {

	protected LayoutInflater mInflater;
	protected Activity mContext;
	protected List<T> mDatas;
	protected final int mFirstItemLayoutId; // 第一项布局ID
	protected final int mItemLayoutId;
	
	
	public HomeFragmentAdapter(Activity context, List<T> datas, int firstItemLayoutId, int itemLayoutId) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mDatas = datas;
		mFirstItemLayoutId = firstItemLayoutId;
		mItemLayoutId = itemLayoutId;
	}
	
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final HomeFragmentViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();
	}

	// 重载此函数
	public abstract void convert(HomeFragmentViewHolder holder, T item, final int position);
	
	
	private HomeFragmentViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		if (position == 0)
		{
			return HomeFragmentViewHolder.get(mContext, convertView, parent, mFirstItemLayoutId, position);
		}
		else
		{
			return HomeFragmentViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
		}
	}
}
