package com.daiyan.handwork.app.activity;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.ImageFolderAdapter;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.AlbumHelper;
import com.daiyan.handwork.common.ImageBucket;
import com.daiyan.handwork.common.UIHelper;

/**
 * 显示手机相册目录
 * @author AA
 * @Date 2014-12-10
 */
public class ImageFolder extends BaseActivity implements OnClickListener {

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	/** 用来装载所有图片目录 */
	private List<ImageBucket> mDataList;
	private GridView mGridView;
	private ImageFolderAdapter mAdapter;
	private AlbumHelper mAlbumHelper;
	public static Bitmap bitmap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_image_folder);
		
		mAlbumHelper = AlbumHelper.getHelper();
		mAlbumHelper.init(getApplicationContext());
		
		initData();
		initView();		
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		mDataList = mAlbumHelper.getImagesBucketList(false);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_add_pic_n);
	}
	
	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		mGridView = (GridView)findViewById(R.id.id_gv_image_folder);
		mAdapter = new ImageFolderAdapter(ImageFolder.this, mDataList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(onItemClickListener);
	}
	
	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.image_folder_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			UIHelper.showImageItemSelector(ImageFolder.this, mDataList, position);
		}
	};


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		}
	}
}
