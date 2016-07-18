package com.daiyan.handwork.app.widget;

import com.daiyan.handwork.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PointPageView extends View {
	
	private int mPageSize;
	private int mPageIndex;
	private int mPointIconWidht = 19;
	private int mPointPadding = 18;
	private int mStep = 1;
	private int mDisplaySize;
	private int mDisplayIndex;
	private Paint paint;
	private Context mContext;
	
	public void init(Context context) {
		mContext=context; 
		paint = new Paint(Paint.DITHER_FLAG);
	}
	
	public PointPageView(Context context) {		
		super(context, null);
	}
	/**
	 * Used to inflate the Workspace from XML.
	 */
	public PointPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setPageSize(int pageSize) {
		mPageSize = Math.max(pageSize, 0);
		mDisplaySize = (int)Math.ceil((double)mPageSize / mStep);
		invalidate();
	}
	
	public int getPageSize() {
		return mPageSize;
	}
	
	public void setPageIndex(int pageIndex) {
		mPageIndex = Math.min(Math.max(pageIndex, 0), mPageSize - 1);
		mDisplayIndex = (int)Math.floor((double)mPageIndex / mStep);
		invalidate();
	}
	
	public int getPageIndex() {
		return mPageIndex;
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		drawAllPoint(canvas);
	}
	
	private void drawAllPoint(Canvas canvas) {
		canvas.save();		
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		int contentWidth = (mPointIconWidht + mPointPadding) * mPageSize;
		int beginX = (width - contentWidth) / 2;
		int beginY = height - 36;
		
		Bitmap dotNormalBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dot_normal);
		Bitmap dotFocusBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dot_focus);

		for (int i = 0; i < mDisplaySize; i++) {
			if (i == mDisplayIndex) {
				canvas.drawBitmap(dotFocusBitmap, beginX, beginY, paint);
			} else {
				canvas.drawBitmap(dotNormalBitmap, beginX, beginY, paint);
			}

			beginX = beginX + 20 + mPointPadding;
		}		
		canvas.restore();
	}
}
