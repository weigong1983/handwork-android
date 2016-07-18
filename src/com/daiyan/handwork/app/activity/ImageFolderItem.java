package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.ImageFolderItemAdapter;
import com.daiyan.handwork.adapter.ImageFolderItemAdapter.TextCallback;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.common.AlbumHelper;
import com.daiyan.handwork.common.ImageItem;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.BitmapUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 一个相册中的图片目录
 * @author AA
 * @Date 2014-12-10
 */
public class ImageFolderItem extends BaseActivity implements OnClickListener {

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;

	private Activity mContext;
	private Resources mResources;
	
	private List<ImageItem> mDataList;
	private GridView mGridView;
	private ImageFolderItemAdapter mAdapter;
	private AlbumHelper mAlbumHelper;
	private Button mFinishBtn;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ToastUtils.show(mContext, mResources.getString(R.string.image_folder_limit_exceeded_text));
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_image_folder_item);

		mContext = ImageFolderItem.this;
		mAlbumHelper = AlbumHelper.getHelper();
		mAlbumHelper.init(getApplicationContext());

		mDataList = (List<ImageItem>) getIntent().getSerializableExtra(Consts.KEY_EXTRA_IMAGE_LIST);

		initView();
		mFinishBtn = (Button) findViewById(R.id.id_btn_image_folder_item_finish);
		mFinishBtn.setOnClickListener(this);
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mResources = getResources();
		initTitleBar();
		mGridView = (GridView) findViewById(R.id.id_gv_image_folder_item);
		mAdapter = new ImageFolderItemAdapter(mContext, mDataList, mHandler);
		mGridView.setAdapter(mAdapter);
		mAdapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				String finishLeft = mResources.getString(R.string.image_folder_finish_left_text);
				String finishRight = mResources.getString(R.string.image_folder_finish_right_text);
				mFinishBtn.setText(finishLeft + count + finishRight);
			}
		});
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
			mAdapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			UIHelper.showImageFolderSelector(mContext, true);
			break;
			
		case R.id.id_btn_image_folder_item_finish:
			ArrayList<String> list = new ArrayList<String>();
			Collection<String> c = mAdapter.map.values();
			Iterator<String> it = c.iterator();
			for (; it.hasNext();) {
				list.add(it.next());
			}

			if (BitmapUtils.act_bool) {
				BitmapUtils.act_bool = false;
			}
			for (int i = 0; i < list.size(); i++) {
				if (BitmapUtils.drr.size() < 9) {
					BitmapUtils.drr.add(list.get(i));
				}
			}
			finish();
			break;
		}
	}
	
	
}
