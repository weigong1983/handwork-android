package com.daiyan.handwork.app.activity;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseSelectAreaActivity;
import com.daiyan.handwork.app.widget.wheel.OnWheelChangedListener;
import com.daiyan.handwork.app.widget.wheel.WheelView;
import com.daiyan.handwork.app.widget.wheel.adapters.ArrayWheelAdapter;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.StringUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectAreaActivity extends BaseSelectAreaActivity implements OnClickListener, OnWheelChangedListener {
	
	private Activity mContext;
	
	private String province;
	private String city;
	private String distinct;
	
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private Button mBtnConfirm;
	private final int VISIABLE_ITEMS = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = SelectAreaActivity.this;
		
		province = getIntent().getStringExtra(Consts.EXTRA_CONTENT_PROVINCE);
		city = getIntent().getStringExtra(Consts.EXTRA_CONTENT_CITY);
		distinct = getIntent().getStringExtra(Consts.EXTRA_CONTENT_DISTINCT);

		setContentView(R.layout.activity_select_area);
		initViews();
		initData();
		
		/**
		 * 根据当前省市区初始化控件
		 */
		initCurrentArea();
		
		//修改登录按钮内容和背景颜色
//		String loading = this.getResources().getString(R.string.loading_data_tips);
//		//显示正在登录对话框
//		UIHelper.showDialogForLoading(mContext, loading, false);
//		new LoadProvinceDataTask().execute();
	}
	
	private void initViews() {
		initTitleBar();
		
		mViewProvince = (WheelView) findViewById(R.id.id_province);
		mViewCity = (WheelView) findViewById(R.id.id_city);
		mViewDistrict = (WheelView) findViewById(R.id.id_district);
		mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
		
    	// 添加change事件
    	mViewProvince.addChangingListener(this);
    	// 添加change事件
    	mViewCity.addChangingListener(this);
    	// 添加change事件
    	mViewDistrict.addChangingListener(this);
    	// 添加onclick事件
    	mBtnConfirm.setOnClickListener(this);
	}
	
	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.personal_info_area));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}
	
	private void initData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(SelectAreaActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(VISIABLE_ITEMS);
		mViewCity.setVisibleItems(VISIABLE_ITEMS);
		mViewDistrict.setVisibleItems(VISIABLE_ITEMS);
		updateCities();
		updateAreas();
	}
	
	/**
	 * 后台加载xml数据
	 * @author weigong
	 *
	 */
//	private class LoadProvinceDataTask extends AsyncTask<Void, Void, Boolean> {
//
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			
//			//initProvinceDatas();
////			try {
////				Thread.sleep(500);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			return true;
//		}
//
//		@Override
//		protected void onPostExecute(Boolean isSuccess) {
//			UIHelper.hideDialogForLoading();
//			initProvinceDatas();
//			initData();
//			initCurrentArea();
//		}
//	}
	
	/**
	 * 根据当前省市区初始化控件
	 */
	private void initCurrentArea()
	{
		// 省
		mCurrentProviceName = province;
		mViewProvince.setCurrentItem(findPosition(mCurrentProviceName, mProvinceDatas));
				
		mCurrentCityName = city;
		mViewCity.setCurrentItem(findPosition(mCurrentCityName, mCitisDatasMap.get(mCurrentProviceName)));
		
		mCurrentDistrictName = distinct;
		mViewDistrict.setCurrentItem(findPosition(mCurrentDistrictName, mDistrictDatasMap.get(mCurrentCityName)));
	}
	
	private int findPosition(String targetString, final String [] sourceArray)
	{
		if (StringUtils.isEmpty(targetString) 
			|| sourceArray == null || sourceArray.length == 0)
			return 0;
		
		int len = sourceArray.length;
		for (int pos = 0; pos < len; pos++)
		{
			if (targetString.equalsIgnoreCase(sourceArray[pos]))
				return pos;
		}
		return 0;
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
		
		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			showSelectedResult();
			break;
		case R.id.btn_confirm:
			showSelectedResult();
			break;
		default:
			break;
		}
	}

	private void showSelectedResult() {
//		Toast.makeText(SelectAreaActivity.this, "当前选中:"+mCurrentProviceName+","+mCurrentCityName+","
//				+mCurrentDistrictName+","+mCurrentZipCode, Toast.LENGTH_SHORT).show();
		
		Intent intent=new Intent();
        intent.putExtra(Consts.EXTRA_CONTENT_PROVINCE, mCurrentProviceName);
        intent.putExtra(Consts.EXTRA_CONTENT_CITY, mCurrentCityName);
        intent.putExtra(Consts.EXTRA_CONTENT_DISTINCT, mCurrentDistrictName);
        setResult(Consts.REQUEST_CODE_EDIT_AREA, intent);
        finish();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK 
			&& event.getAction() == KeyEvent.ACTION_UP) 
		{
			showSelectedResult();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
