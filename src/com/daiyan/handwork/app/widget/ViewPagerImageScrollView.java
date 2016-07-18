package com.daiyan.handwork.app.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;

public class ViewPagerImageScrollView extends ScrollView {
	private ImageView image;
	private Context mContext;
	private int displayWidth;
	private ViewPager bannerPager ;

	private final int IMG_SIZE = 640;
	private float imgHeight, imgWidth;

	// ScrollView内嵌ViewPager导致ViewPager滑动困难问题
	private GestureDetector mGestureDetector;  
	
	public ViewPagerImageScrollView(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	public ViewPagerImageScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	public ViewPagerImageScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager mWm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(dm);
		displayWidth = dm.widthPixels;
		
		// ScrollView内嵌ViewPager导致ViewPager滑动困难问题
		mGestureDetector = new GestureDetector(getContext(),  
                new YScrollDetector());  
        setFadingEdgeLength(0);  
	}
	
	@Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        return super.onInterceptTouchEvent(ev)  
                && mGestureDetector.onTouchEvent(ev);  
    }

	private class YScrollDetector extends SimpleOnGestureListener {  
        @Override  
        public boolean onScroll(MotionEvent e1, MotionEvent e2,  
                float distanceX, float distanceY) {  
        	/** 
             * 如果我们滚动更接近水平方向,返回false,让子视图来处理它 
             */  
            return (Math.abs(distanceY) > Math.abs(distanceX));
        }  
    }  
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		/*if (getChildCount() > 0) {
			view = getChildAt(0);
			image = (ImageView) view.findViewById(R.id.image);
		}*/
	}

	public void setImageBitMap(Bitmap bitmap,ImageView imageview, ViewPager bannerPager) {
		//this.bmp = bitmap;
		this.image = imageview ;
		this.bannerPager = bannerPager;
		initHead();
	}

	private void initHead() {
		// TODO Auto-generated method stub
		float scale = (float) displayWidth / IMG_SIZE;
		imgWidth = scale * IMG_SIZE;
		imgHeight = scale * IMG_SIZE;
		image.setScaleType(ScaleType.MATRIX);
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale, 0, 0);
		image.setImageMatrix(matrix);

		android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
				(int) imgWidth, (int) imgHeight);
		bannerPager.setLayoutParams(lp);
		
		// 开启图片缩放动画
		setScaleBigAnimation(matrix);
	}
	
	// 缩放动画
	private class MyScaleAnimatorListener implements AnimatorUpdateListener {
		private Matrix mPrimaryMatrix;
		public MyScaleAnimatorListener(Matrix matrix) {
			mPrimaryMatrix = matrix;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			float scale = (Float) animation.getAnimatedValue();
			Matrix matrix = new Matrix(mPrimaryMatrix);
			matrix.postScale(scale, scale, imgWidth / 2, imgHeight / 2);
			image.setImageMatrix(matrix);
		}
	}

	public void setScaleBigAnimation(Matrix matrix) {
		ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 1.12f);
		animator.addUpdateListener(new MyScaleAnimatorListener(matrix));
		animator.setDuration(5000);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.setStartDelay(1000);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.start();
	}
}
