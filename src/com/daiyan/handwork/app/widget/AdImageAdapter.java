package com.daiyan.handwork.app.widget;

import java.util.ArrayList;
import java.util.List;

import com.asked.asked.asynctask.ImageDownloader;
import com.daiyan.handwork.R;
import com.daiyan.handwork.bean.Ad;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.ImageLoader.Type;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class AdImageAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater listContainer;
	private List<Ad> data = new ArrayList<Ad>();
	//private ImageDownloader downloader;
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);

	public AdImageAdapter(Context context)
	{
		mContext = context;
		listContainer = LayoutInflater.from(mContext);
	}

	public void setData(List<Ad> data)
	{
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		if (data != null)
		{
			return data.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		// return data.get(position);
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// 实例化viewholader
		final ViewHolder listItemView;

		if (convertView == null)
		{
			listItemView = new ViewHolder();
			convertView = listContainer.inflate(R.layout.aditem, null);
			listItemView.img_icon = (ImageView) convertView
					.findViewById(R.id.gallery_image);
//			LayoutParams params = (LayoutParams) listItemView.img_icon.getLayoutParams();  
//			WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//		    int screenWidth = wm.getDefaultDisplay().getWidth();
//			
//		    params.width = screenWidth;
//		    params.height = LayoutParams.MATCH_PARENT;
//		    listItemView.img_icon.setLayoutParams(params);
//		    listItemView.img_icon.setScaleType(ScaleType.FIT_XY);
			
			convertView.setTag(listItemView);
		}
		else
		{
			listItemView = (ViewHolder) convertView.getTag();
		}
		try
		{
			listItemView.img_icon.setScaleType(ScaleType.FIT_XY);
			mImageLoader.loadImage(data.get(position).image, listItemView.img_icon, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return convertView;
	}

	/**
	 * 缩放
	 * 
	 * @param size
	 *            原图宽
	 * @param bitmap
	 *            原图
	 * @return
	 */
	public Bitmap scaling(float size, Bitmap bitmap)
	{
		// 图片宽度跟屏幕比例（即图片占屏幕的宽度比例）
		float img_Proportion = size / 480f;
		// 获取屏幕分辨率
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		// 获取分辨率宽度
		int screenW = dm.widthPixels;
		// 在当前手机显示的图片应有长度
		float show_img_width = (img_Proportion * (float) screenW);
		// 网络获取的图片宽度
		int w = bitmap.getWidth();
		// 网络获取的图片高度
		int h = bitmap.getHeight();
		// 计算网络获取的图片的缩放比例
		float m = show_img_width / (float) w;
		// 进行缩放
		Matrix matrix = new Matrix();
		matrix.postScale(m, m);
		Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		// 返回
		return b;

	}

	/**
	 * 声明list_item的控件
	 * 
	 * @author 琴弦欲奏
	 */
	final class ViewHolder
	{
		/** 头像 */
		ImageView img_icon;
		/** 标题 */
		TextView txt_title;
		/** 内容 */
		TextView txt_content;
		/** 时间 */
		TextView txt_datetime;
	}
}
