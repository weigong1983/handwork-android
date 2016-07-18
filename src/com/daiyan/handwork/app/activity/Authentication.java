package com.daiyan.handwork.app.activity;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.Graphic;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SDCardFileUtils;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.ToastUtils;

/**
 * 认证成为工艺家
 * 
 * @author 魏工
 * @Date 2015年05月10日
 */
public class Authentication extends BaseActivity implements OnClickListener {

	private Activity mContext;
	private Resources mResources;

	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);

	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;

	/** 调用修改头像接口返回数据 */
	private HashMap<String, Object> mUpdateAvatarDatas;

	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;

	// 头像
	private LinearLayout avatarLinearLayout;
	private RoundImageView avatarImageView;
	private String curAvatarUrl = "";

	// 姓名
	private LinearLayout nameLinearLayout;
	private TextView nameTextView;
	private String curName = "";

	// 职称
	private LinearLayout titleLinearLayout;
	private TextView titleTextView;
	private String curTitle = "";

	// 称号
	private LinearLayout designationLinearLayout;
	private TextView designationTextView;
	private String curDesignation = "";

	// 非遗
	private LinearLayout intangibleheritageLinearLayout;
	private TextView intangibleheritageTextView;
	private String curIntangibleheritage = "";

	// 从业年限
	private LinearLayout workAgeLinearLayout;
	private TextView workAgeTextView;
	private int curWorkAge = 1;

	// 工艺品类
	private LinearLayout categoryLinearLayout;
	private TextView categoryTextView;
	private String curCategory = "1";

	// 地区
	private LinearLayout areaLinearLayout;
	private TextView areaextView;
	private String curProvince = "广东省";
	private String curCity = "广州市";
	private String curDistinct = "天河区";

	// 所属协会
	private LinearLayout associationLinearLayout;
	private TextView associationTextView;
	private String curAssociation = "";

	// 提交认证
	private LinearLayout authenticationLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_authentication);
		mContext = Authentication.this;
		mResources = getResources();
		initView();
		
		// 读取所有作品分类
		CategoryManager.getInstance().loadCategoryData();
		
		// 设置头像和昵称
		String nickName = LocationUtil.readInit(mContext, Consts.NICKNAME, "");
		if (!StringUtils.isEmpty(nickName))
		{
			curName = nickName;
			nameTextView.setText(curName);
		}
		
		String avatarUrl = LocationUtil.readInit(mContext, Consts.PHOTO, "");
		if (!StringUtils.isEmpty(avatarUrl))
		{
			curAvatarUrl = avatarUrl;
			mImageLoader.loadImage(curAvatarUrl, avatarImageView, true);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		
		DataServer.getInstance().initialize(mContext);

		avatarImageView = (RoundImageView) findViewById(R.id.avatarImageView);
		avatarLinearLayout = (LinearLayout) findViewById(R.id.avatarLinearLayout);
		avatarLinearLayout.setOnClickListener(this);

		nameTextView = (TextView) findViewById(R.id.nameTextView);
		nameLinearLayout = (LinearLayout) findViewById(R.id.nameLinearLayout);
		nameLinearLayout.setOnClickListener(this);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleLinearLayout = (LinearLayout) findViewById(R.id.titleLinearLayout);
		titleLinearLayout.setOnClickListener(this);

		designationTextView = (TextView) findViewById(R.id.designationTextView);
		designationLinearLayout = (LinearLayout) findViewById(R.id.designationLinearLayout);
		designationLinearLayout.setOnClickListener(this);

		intangibleheritageTextView = (TextView) findViewById(R.id.intangibleheritageTextView);
		intangibleheritageLinearLayout = (LinearLayout) findViewById(R.id.intangibleheritageLinearLayout);
		intangibleheritageLinearLayout.setOnClickListener(this);

		workAgeTextView = (TextView) findViewById(R.id.workAgeTextView);
		workAgeLinearLayout = (LinearLayout) findViewById(R.id.workAgeLinearLayout);
		workAgeLinearLayout.setOnClickListener(this);

		categoryTextView = (TextView) findViewById(R.id.categoryTextView);
		categoryLinearLayout = (LinearLayout) findViewById(R.id.categoryLinearLayout);
		categoryLinearLayout.setOnClickListener(this);

		areaextView = (TextView) findViewById(R.id.areaextView);
		areaLinearLayout = (LinearLayout) findViewById(R.id.areaLinearLayout);
		areaLinearLayout.setOnClickListener(this);

		associationTextView = (TextView) findViewById(R.id.associationTextView);
		associationLinearLayout = (LinearLayout) findViewById(R.id.associationLinearLayout);
		associationLinearLayout.setOnClickListener(this);

		authenticationLinearLayout = (LinearLayout) findViewById(R.id.authenticationLinearLayout);
		authenticationLinearLayout.setOnClickListener(this);
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
				R.string.setting_personal_info));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			finish();
			break;
		case R.id.avatarLinearLayout: // 头像
			showAddPictureDialog();
			break;
		case R.id.id_tv_pw_common_item_one: // 拍照
			UIHelper.takePictureForHeadImg(mContext);
			UIHelper.hideDialogForAddPic();
			break;
		case R.id.id_tv_pw_common_item_two: // 从相册中选择
			UIHelper.showSystemAlbum(mContext);
			UIHelper.hideDialogForAddPic();
			break;
		case R.id.nameLinearLayout: // 姓名
			UIHelper.showEditSinglelineContent(mContext, Consts.INFO_TYPE_NAME,
					curName);
			break;
		case R.id.titleLinearLayout: // 职称
			showChoiceDialog(Consts.INFO_TYPE_TITLE, titleTextView);
			break;
		case R.id.designationLinearLayout: // 称号
			showChoiceDialog(Consts.INFO_TYPE_DESIGNATION, designationTextView);
			break;
		case R.id.intangibleheritageLinearLayout: // 非遗
			showChoiceDialog(Consts.INFO_TYPE_INTANGIBLEHERITAGE,
					intangibleheritageTextView);
			break;
		case R.id.workAgeLinearLayout: // 从业年限
			selectWorkage();
			break;
		case R.id.categoryLinearLayout: // 工艺品类
			showChoiceDialog(Consts.INFO_TYPE_CATEGORY, categoryTextView);
			break;
		case R.id.areaLinearLayout: // 地区
			UIHelper.showSelectAreaActivity(mContext, curProvince, curCity,
					curDistinct);
			break;
		case R.id.associationLinearLayout: // 所属协会
			UIHelper.showEditSinglelineContent(mContext,
					Consts.INFO_TYPE_ASSOCIATION, curAssociation);
			break;
		case R.id.authenticationLinearLayout: // 提交认证
			
			if (StringUtils.isEmpty(curAvatarUrl) || StringUtils.isEmpty(curName) || 
					StringUtils.isEmpty(curTitle) || StringUtils.isEmpty(curDesignation) || 
					StringUtils.isEmpty(curIntangibleheritage) || StringUtils.isEmpty(curCategory) || 
					StringUtils.isEmpty(curProvince) || StringUtils.isEmpty(curCity) || 
					StringUtils.isEmpty(curDistinct) || StringUtils.isEmpty(curAssociation))
			{
				ToastUtils.show(mContext, mResources.getString(R.string.authentication_not_complete_tips));
				return ;
			}
			
			// 提交服务器修改
			UIHelper.showDialogForLoading(mContext, mResources
					.getString(R.string.authentication_submiting_tips),
					false);
			new AuthTask().execute();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// 空值判断(拍照数据存在SD卡)
		if (data == null && requestCode != Consts.RESULT_TAKE_PICTURE)
			return ;

		if (Consts.REQUEST_CODE_EDIT_CONTENT == requestCode) // 编辑单行文本内容
		{
			// 重置密码后清空密码框
			if (Consts.RESULT_CODE_EDIT_NAME == resultCode) {
				curName = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				nameTextView.setText(curName);
			} else if (Consts.RESULT_CODE_EDIT_ASSOCIATION == resultCode) {
				curAssociation = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				associationTextView.setText(curAssociation);
			}
		} else if (Consts.REQUEST_CODE_EDIT_AREA == requestCode) // 编辑地区
		{
			curProvince = data.getStringExtra(Consts.EXTRA_CONTENT_PROVINCE);
			curCity = data.getStringExtra(Consts.EXTRA_CONTENT_CITY);
			curDistinct = data.getStringExtra(Consts.EXTRA_CONTENT_DISTINCT);
			// 更新地区显示
			areaextView.setText(curProvince + " " + curCity + " " + curDistinct);
		} else if (requestCode == Consts.RESULT_TAKE_PICTURE) // 启动相机
		{
			String bitmapDir = SDCardFileUtils.getSDCardPath()
					+ Consts.PHOTO_DIR_PATH;
			Bitmap headBitmap = BitmapFactory.decodeFile(bitmapDir
					+ Consts.PHOTO_TEMP_NAME);
			int scaleHeight = Consts.AVATAR_SIZE * headBitmap.getHeight() / headBitmap.getWidth();
			byte[] imgByte = Graphic.ConvertBitmapToByteArray(headBitmap, 
					Consts.AVATAR_SIZE, scaleHeight);
			String imgData = Base64.encodeToString(imgByte, Base64.DEFAULT);
			new UpdateAvatarTask().execute(imgData);
		} else if (requestCode == Consts.REQUEST_CODE_OPEN_ALBUM) {
			ContentResolver resolver = getContentResolver();
			try {
				// 获得图片的Uri
				Uri originalUri = data.getData();
				InputStream picStream = resolver.openInputStream(Uri
						.parse(originalUri.toString()));
				Bitmap headBitmap = BitmapFactory.decodeStream(picStream);
				if (headBitmap != null) {
					int scaleHeight = Consts.AVATAR_SIZE * headBitmap.getHeight() / headBitmap.getWidth();
					byte[] imgByte = Graphic.ConvertBitmapToByteArray(headBitmap, 
							Consts.AVATAR_SIZE, scaleHeight);
					String imgData = Base64.encodeToString(imgByte,
							Base64.DEFAULT);
					// 修改头像
					new UpdateAvatarTask().execute(imgData);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showChoiceDialog(final int infoType, final TextView textView) {
		String[] items = null;
		int titleId = 0;
		int checkedItem = 0;
		switch (infoType) {
		case Consts.INFO_TYPE_TITLE:
			items = getResources().getStringArray(R.array.title);
			titleId = R.string.personal_info_title;
			checkedItem = findPosition(curTitle, items);
			break;
		case Consts.INFO_TYPE_DESIGNATION:
			items = getResources().getStringArray(R.array.callname);
			titleId = R.string.personal_info_designation;
			checkedItem = findPosition(curDesignation, items);
			break;
		case Consts.INFO_TYPE_INTANGIBLEHERITAGE:
			items = getResources().getStringArray(R.array.intangibleheritage);
			titleId = R.string.personal_info_intangibleheritage;
			checkedItem = findPosition(curIntangibleheritage, items);
			break;
		case Consts.INFO_TYPE_CATEGORY:
			// items = getResources().getStringArray(R.array.category);
			items = CategoryManager.getInstance().getClassNameArray();
			titleId = R.string.personal_info_category;
			checkedItem = findPosition(CategoryManager.getInstance()
					.getClassNameById(curCategory), items);
			break;
		}

		Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setIcon(R.drawable.logo_small);
		dialog.setTitle("请选择" + this.getResources().getString(titleId));
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		final String[] tempItems = items;
		dialog.setSingleChoiceItems(items, checkedItem,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String selectContent = tempItems[which];
						switch (infoType) {
						case Consts.INFO_TYPE_TITLE:
							curTitle = selectContent;
							break;
						case Consts.INFO_TYPE_DESIGNATION:
							curDesignation = selectContent;
							break;
						case Consts.INFO_TYPE_INTANGIBLEHERITAGE:
							curIntangibleheritage = selectContent;
							break;
						case Consts.INFO_TYPE_CATEGORY:
							curCategory = CategoryManager.getInstance()
									.getClassIdByName(selectContent);
							break;
						}
						textView.setText(selectContent);
						dialog.dismiss();
					}
				}).create();
		dialog.show();
	}

	private int findPosition(String targetString, final String[] sourceArray) 
	{
		if (StringUtils.isEmpty(targetString) 
			|| sourceArray == null || sourceArray.length == 0)
			return 0;
		
		int len = sourceArray.length;
		for (int pos = 0; pos < len; pos++) {
			if (targetString.equalsIgnoreCase(sourceArray[pos]))
				return pos;
		}
		return 0;
	}

	private DatePickerDialog datePickerDialog = null;

	private void selectWorkage() {
		final Calendar c = Calendar.getInstance();
		int curSelectYear = c.get(Calendar.YEAR) - curWorkAge + 1;
		datePickerDialog = new CustomerDatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker dp, int year, int month,
							int dayOfMonth) {
						curWorkAge = c.get(Calendar.YEAR) - year;
						workAgeTextView.setText(curWorkAge + "年");
					}
				}, curSelectYear, // 传入年份
				0, // 传入月份
				0 // 传入天数
		);
		datePickerDialog.setIcon(R.drawable.logo_small);
		datePickerDialog.setTitle("选择从业年份");
		datePickerDialog.show();
		DatePicker dp = findDatePicker((ViewGroup) datePickerDialog.getWindow()
				.getDecorView());
		if (dp != null) {
			((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
					.getChildAt(1).setVisibility(View.GONE);
			((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
					.getChildAt(2).setVisibility(View.GONE);
			dp.setMaxDate(Calendar.getInstance().getTimeInMillis());
		}
	}

	class CustomerDatePickerDialog extends DatePickerDialog {
		public CustomerDatePickerDialog(Context context,
				OnDateSetListener callBack, int year, int monthOfYear,
				int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);
			datePickerDialog.setTitle("选择从业年份");
		}
	}

	private DatePicker findDatePicker(ViewGroup group) {
		if (group != null) {
			for (int i = 0, j = group.getChildCount(); i < j; i++) {
				View child = group.getChildAt(i);
				if (child instanceof DatePicker) {
					return (DatePicker) child;
				} else if (child instanceof ViewGroup) {
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}

	/**
	 * 提交资料认证接口
	 */
	private class AuthTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(1000);
				mDatas = DataServer.getInstance().setUserInfo(1, null,
							curName, curTitle, curDesignation, curIntangibleheritage, 
							curWorkAge, curCategory, curProvince, curCity, curDistinct,
							curAssociation);
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			if (isSuccess) {
				ToastUtils.show(mContext,
						mResources.getString(R.string.authentication_submit_success));
				finish();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}

	/**
	 * 修改头像
	 * 
	 * @author AA
	 * @Date 2014-12-26
	 */
	private class UpdateAvatarTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext,
					mResources.getString(R.string.modify_loading_text), true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//Thread.sleep(500);
				mUpdateAvatarDatas = DataServer.getInstance().updateAvatar(
						params[0]);
				return mUpdateAvatarDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			if (isSuccess) {
				ToastUtils.show(mContext,
						mResources.getString(R.string.modify_success_tips));

				// 加载最新头像
				curAvatarUrl = mUpdateAvatarDatas.get(Consts.PHOTO)
						.toString();
				mImageLoader.loadImage(curAvatarUrl, avatarImageView, true);
				// 将头像链接写到本地
				// LocationUtil.writeInit(mContext, Consts.PHOTO, headImgUrl);
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}

	/**
	 * 显示添加图片对话框
	 */
	private void showAddPictureDialog() {
		String from_camera = mResources.getString(R.string.pw_from_camera);
		String from_album = mResources.getString(R.string.pw_from_album);
		String cancel = mResources.getString(R.string.pw_cancel);
		UIHelper.showDialogForAddPic(
				mContext,
				StringUtils.ArrayToList(UIHelper.POPUPWINDOW_KEY, new String[] {
						from_camera, from_album, cancel }), this);
	}

}