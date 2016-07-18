package com.daiyan.handwork.app.widget;

import com.daiyan.handwork.R;
import com.daiyan.handwork.utils.DisplayUtils;
import com.daiyan.handwork.utils.StringUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义标题栏
 * @author AA
 * @Date 2015-01-19
 */
public class CustomTitleBar extends RelativeLayout {

	/** 文字 */
	private String mLeftText, mTitleText, mRightText;
	/** 文字大小 */
	private float mAmboTextSize, mTitleTextSize;
	/** 文字颜色 */
	private int mTextColor;
	/** 图片 */
	private int mLeftImage, mRightImage;
	/** 是否可见 */
	private boolean mLeftVisible, mRightVisible;
	
	/** 左边、标题、右边 */
	private TextView mLeftTextView, mTitleTextView, mRightTextView;
	private ImageView mLeftImageButton, mRightImageButton;
	/** 标题栏视图参数 */
	private LayoutParams mLeftParams, mTitleParams, mRightParams;
	/** 自定义点击监听器 */
	private TitleBarClickListener mTitleBarClickListener;
	

	public CustomTitleBar(Context context) {
		this(context, null);
	}

	public CustomTitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CustomTitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//获得自定义属性值
		getTypedArray(context, attrs);
		
		//创建LayoutParams，并且设置对齐方式
		mLeftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
		mLeftParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
		
		mTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
		
		mRightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		mRightParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
		
		//左边文字
		if(StringUtils.isNotEmpty(mLeftText) && mLeftVisible) {
			mLeftTextView = new TextView(context);
			mLeftTextView.setText(mLeftText);
			mLeftTextView.setTextSize(mAmboTextSize);
			mLeftTextView.setTextColor(mTextColor);
			addView(mLeftTextView, mLeftParams);
			mLeftTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mTitleBarClickListener.onLeftClickListener();
				}
			});
		}
		//右边文字
		if(StringUtils.isNotEmpty(mRightText) && mRightVisible){
			mRightTextView = new TextView(context);
			mRightTextView.setText(mRightText);
			mRightTextView.setTextSize(mAmboTextSize);
			mRightTextView.setTextColor(mTextColor);
			addView(mRightTextView, mRightParams);
			mRightTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mTitleBarClickListener.onRightClickListener();
				}
			});
		}
		//标题文字
		if(StringUtils.isNotEmpty(mTitleText)) {
			mTitleTextView = new TextView(context);
			mTitleTextView.setText(mTitleText);
			mTitleTextView.setTextSize(mTitleTextSize);
			mTitleTextView.setGravity(Gravity.CENTER);
			mTitleTextView.setTextColor(mTextColor);
			addView(mTitleTextView, mTitleParams);
		}
		//左边图片按钮
		if(mLeftImage > 0 && mLeftVisible) {
			mLeftImageButton = new ImageView(context);
			mLeftImageButton.setImageResource(mLeftImage);
			addView(mLeftImageButton, mLeftParams);
			mLeftImageButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mTitleBarClickListener.onLeftClickListener();
				}
			});
		}
		//右边图片按钮
		if(mRightImage > 0 && mRightVisible) {
			mRightImageButton = new ImageView(context);
			mRightImageButton.setImageResource(mRightImage);
			addView(mRightImageButton, mRightParams);
			mRightImageButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mTitleBarClickListener.onRightClickListener();
				}
			});
		}
	}
	
	/**
	 * 获得自定义属性值
	 * @param context
	 * @param attrs
	 */
	private void getTypedArray(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleBar);
		int count = ta.getIndexCount();
		for(int i=0; i<count; i++) {
			int attr = ta.getIndex(i);
			switch (attr) {
			case R.styleable.CustomTitleBar_leftText:
				mLeftText = ta.getString(attr);
				break;

			case R.styleable.CustomTitleBar_titleText:
				mTitleText = ta.getString(attr);
				break;
				
			case R.styleable.CustomTitleBar_rightText:
				mRightText = ta.getString(attr);
				break;
				
			case R.styleable.CustomTitleBar_amboTextSize:
				mAmboTextSize = DisplayUtils.px2sp(context, ta.getDimensionPixelSize(attr, 12));
				break;
				
			case R.styleable.CustomTitleBar_titleTextSize:
				mTitleTextSize = DisplayUtils.px2sp(context, ta.getDimensionPixelSize(attr, 16));
				break;
	
			case R.styleable.CustomTitleBar_textColor:
				mTextColor = ta.getColor(attr, Color.BLACK);
				break;
				
			case R.styleable.CustomTitleBar_leftVisible:
				mLeftVisible = ta.getBoolean(attr, false);
				break;
				
			case R.styleable.CustomTitleBar_rightVisible:
				mRightVisible = ta.getBoolean(attr, false);
				break;
				
			case R.styleable.CustomTitleBar_leftImage:
				mLeftImage = ta.getResourceId(attr, 0);
				break;
				
			case R.styleable.CustomTitleBar_rightImage:
				mRightImage = ta.getResourceId(attr, 0);
				break;
			}
		}
		ta.recycle();
	}

	/**
	 * 设置点击监听事件
	 * @param listener
	 */
	public void setOnTitleBarClickListener(TitleBarClickListener listener) {
		this.mTitleBarClickListener = listener;
	}
	
	
	public interface TitleBarClickListener {
		public void onLeftClickListener();
		public void onRightClickListener();
	}
}
