package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.app.widget.ListViewForScrollView;
import com.daiyan.handwork.bean.Activitys;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ViewHolder;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.SystemUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

/**
 * 研究院
 * 
 * @author 魏工
 * @Date 2015年05月09日
 */
public class Institute extends BaseActivity implements OnClickListener {

	private Activity mContext;

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private LinearLayout phoneLinearLayout; // 电话
	private LinearLayout mailLinearLayout; // 地址
	private LinearLayout addressLinearLayout; // 地址
	private LinearLayout standardLinearLayout; // 标准
	private LinearLayout busLinearLayout; // 乘车指引
	
	
	private ImageView logoImageView;
	private TextView nameTextView;
	private TextView phoneTextView;
	private TextView mailTextView;
	private TextView addressTextView;
	
	private String imageUrl;
	private String name;
	private String telphone;
	private String mail;
	private String address;
	private String bus;
	
	
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	

	/** 活动列表 */
	private ListViewForScrollView mListView;
	private CommonAdapter<Activitys> mAdapter;
	private List<Activitys> mListViewDatas = new ArrayList<Activitys>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_institute);
		mContext = Institute.this;

		initView();
		
		// 获取研究院资料
		UIHelper.showDialogForLoading(mContext, this.getResources().getString(R.string.loading_data_tips), false);
		new GetInstituteTask().execute();
	}

	/**
	 * 初始化列表
	 */
	private void initView() {

		initTitleBar();

		phoneLinearLayout = (LinearLayout) this.findViewById(R.id.phoneLinearLayout);
		phoneLinearLayout.setOnClickListener(this);
		
		mailLinearLayout = (LinearLayout) this.findViewById(R.id.mailLinearLayout);
		mailLinearLayout.setOnClickListener(this);
		
		addressLinearLayout = (LinearLayout) this.findViewById(R.id.addressLinearLayout);
		addressLinearLayout.setOnClickListener(this);
		
		standardLinearLayout = (LinearLayout) this.findViewById(R.id.standardLinearLayout);
		standardLinearLayout.setOnClickListener(this);
		
		busLinearLayout = (LinearLayout) this.findViewById(R.id.busLinearLayout);
		busLinearLayout.setOnClickListener(this);
		
		logoImageView = (ImageView) this.findViewById(R.id.logoImageView);
		logoImageView.setScaleType(ScaleType.FIT_XY);
		
		nameTextView = (TextView) this.findViewById(R.id.nameTextView);
		phoneTextView = (TextView) this.findViewById(R.id.phoneTextView);
		mailTextView = (TextView) this.findViewById(R.id.mailTextView);
		addressTextView = (TextView) this.findViewById(R.id.addressTextView);
		
		mListView = (ListViewForScrollView) this.findViewById(R.id.id_lv_only);
		mListView.setOnItemClickListener(onItemClickListener);

		mAdapter = new CommonAdapter<Activitys>(mContext, mListViewDatas,
				R.layout.item_for_activitys_listview) {
			@Override
			public void convert(ViewHolder holder, final Activitys item) {
				ImageView avatarImageView = (ImageView) holder
						.getView(R.id.thumbIconImageView);
				mImageLoader.loadImage(item.thumbIcon, avatarImageView, true);
				
				holder.setText(R.id.nameTextView, item.title);
				holder.setText(R.id.placeTextView, item.place);
				holder.setText(R.id.dateTextView, item.date);
			}
		};
		mListView.setAdapter(mAdapter);
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(
				R.string.title_institute));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		case R.id.phoneLinearLayout: // 联系电话
			//String number = getResources().getString(R.string.institute_phone);
			if (!StringUtils.isEmpty(telphone))
				SystemUtils.callPhone(this, telphone);
			break;
		case R.id.mailLinearLayout: // 邮件
			//String email = getResources().getString(R.string.institute_email);
			if (!SystemUtils.sendEmail(this, mail))
				ToastUtils.show(mContext, getResources().getString(R.string.install_email_application_hint));
			break;
		case R.id.addressLinearLayout: // 打开地图
			if (!SystemUtils.viewMap(this, 23.103969, 113.375496))
				ToastUtils.show(mContext, getResources().getString(R.string.install_map_application_hint));
			break;
		case R.id.standardLinearLayout: // 工艺美术标准
			String token = LocationUtil.readInit(this, Consts.TOKEN, "");
			String webUrl = Consts.URL_STANDARD_BASE + token;
			UIHelper.showWebView(mContext, webUrl, Consts.WEBVIEW_PAGE_ART_STANDARD);
			break;
		case R.id.busLinearLayout: // 乘车指引
			if (!StringUtils.isEmpty(bus))
				UIHelper.showWebView(mContext, bus, Consts.WEBVIEW_PAGE_BUS_GUIDE);
			break;
			
		}

	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Activitys item = mListViewDatas.get(position);
			String token = LocationUtil.readInit(mContext, Consts.TOKEN, "");
			String webUrl = Consts.URL_ACTIVITY_DETAIL_BASE + "?token=" + token + "&atid=" + item.id;
			UIHelper.showWebView(mContext, webUrl, Consts.WEBVIEW_PAGE_ACTIVITY_DETAIL);
		}
	};

	
	
	/**
	 * 获取研究院资料
	 * @author weigong
	 *
	 */
	private class GetInstituteTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(1000);
				mDatas = DataServer.getInstance().getInstitute();
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			if(isSuccess) {
				setInstituteFromNet();
				new GetActivitysTask().execute();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
	
	/**
	 * 解析并显示调用接口返回数据
	 */
	private void setInstituteFromNet() 
	{
		// 读取数据
		imageUrl = mDatas.get("m_image").toString();
		name = mDatas.get("name").toString();
		telphone = mDatas.get("telphone").toString();
		mail = mDatas.get("mail").toString();
		address = mDatas.get("address").toString();
		bus = mDatas.get("bus").toString();
		
		// 更新UI控件
		if (!imageUrl.isEmpty())
			mImageLoader.loadImage(imageUrl, logoImageView, true);
		if (!StringUtils.isEmpty(name))
			nameTextView.setText(name);
		if (!StringUtils.isEmpty(telphone))
			phoneTextView.setText(telphone);
		if (!StringUtils.isEmpty(mail))
			mailTextView.setText(mail);
		if (!StringUtils.isEmpty(address))
			addressTextView.setText(address);
	}
	
	/**
	 * 获取近期活动列表数据
	 * @author weigong
	 *
	 */
	private class GetActivitysTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(1000);
				mDatas = DataServer.getInstance().getAllActivitys(0);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			//UIHelper.hideDialogForLoading();
			if(isSuccess) {
				setActivitysFromNet();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
	
	/**
	 * 解析并显示调用接口返回数据
	 */
	private void setActivitysFromNet() 
	{
		if (mDatas.get(Consts.DATA_LIST) != null && !mDatas.get(Consts.DATA_LIST).toString().isEmpty())
		{
			List<HashMap<String, Object>> activityListData = 
					JsonUtils.getJsonValuesInArray(mDatas.get(Consts.DATA_LIST).toString());
			if (activityListData != null && activityListData.size() > 0)
			{
				int activityCount = activityListData.size();
				for (int index=0; index<activityCount; index++)
				{
					HashMap<String, Object> activityItemMap = activityListData.get(index);
					// 读取活动数据
					Activitys item = new Activitys();
					item.id = activityItemMap.get(Consts.ACT_ID).toString();
					item.thumbIcon = activityItemMap.get(Consts.ACT_THUMB_ICON).toString();
					item.title = activityItemMap.get(Consts.ACT_TITLE).toString();
					item.date = activityItemMap.get(Consts.ACT_START_TIME).toString();
					item.place = activityItemMap.get(Consts.ACT_ADDRESS).toString();
					item.detailUrl = "http://statictest.daiyan123.com/demo/activityinfo.html";
					
					mListViewDatas.add(item);
				}
				
				// 刷新活动列表控件
				mAdapter.notifyDataSetChanged();
			}
		}
	}
}