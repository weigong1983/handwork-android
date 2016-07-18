package com.daiyan.handwork.common;

import com.daiyan.handwork.common.ImageLoader.Type;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewHolder {

	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	public ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
	}

	/**
	 * 获得一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder) convertView.getTag();
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 通过控件ID去获取控件对象，如果没有则加入View
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}
	
	/**
	 * 通过控件ID去设置点击事件监听
	 * @param viewId
	 * @param onClickListener
	 * @return
	 */
	public ViewHolder setOnClickListener(int viewId, OnClickListener onClickListener) {
		View view = getView(viewId);
		view.setOnClickListener(onClickListener);
		return this;
	}
	
	/**
	 * 为TextView设置字符串，并附带下划线
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setTextWithUnderline(int viewId, String text) {
		TextView view = getView(viewId);
		view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		view.setText(text);
		return this;
	}
	
	/**
	 * 为ImageView设置图片
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);
		return this;
	}
	
	/**
	 * 为ImageView设置图片
	 * @param viewId
	 * @param bitmap
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}
	
	/**
	 * 为ImageView设置图片
	 * @param viewId
	 * @param drawable
	 * @return
	 */
	public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
		ImageView view = getView(viewId);
		view.setImageDrawable(drawable);
		return this;
	}
	
	/**
	 * 为ImageView设置图片
	 * @param ViewId
	 * @param url
	 * @return
	 */
	public ViewHolder setImageByUrl(int viewId, String url) {
		ImageLoader.getInstance(3, Type.LIFO).loadImage(url, (ImageView)getView(viewId), true);
		return this;
	}
	
	/**
	 * 为ImageView设置背景
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setBackgroundResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setBackgroundResource(drawableId);
		return this;
	}
	
	/**
	 * 为ImageView设置背景
	 * @param viewId
	 * @param bitmap
	 * @return
	 */
	public ViewHolder setBackgroundBitmap(int viewId, Bitmap bitmap) {
		ImageView view = getView(viewId);
		view.setBackground(new BitmapDrawable(mConvertView.getResources(), bitmap));
		return this;
	}
	
	public int getPosition() {
		return mPosition;
	}
	
}
