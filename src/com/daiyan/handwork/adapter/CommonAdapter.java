package com.daiyan.handwork.adapter;

import java.util.List;

import com.daiyan.handwork.common.ViewHolder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 万能适配器
 * @author AA
 * @Date 2014-11-25
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

	protected LayoutInflater mInflater;
	protected Activity mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;
	
	
	public CommonAdapter(Activity context, List<T> datas, int itemLayoutId) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mDatas = datas;
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
		final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();
	}

	
	public abstract void convert(ViewHolder holder, T item);
	
	
	private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
	}
}
