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
 * 个人资料
 * @author 魏工
 * @Date 2015年05月10日
 */
public class PersonalInfo extends BaseActivity implements OnClickListener{

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
	
	// 个人签名
	private LinearLayout signatureLinearLayout;
	private TextView signatureTextView;
	private String curSignature = "";
	
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
	
	// 绑定手机
	private LinearLayout bindPhoneLinearLayout;
	private TextView bindPhoneTextView;
	private String curPhone = "";
	
	// 账号类型
	private LinearLayout accountTypeLinearLayout;
	private TextView accountTypeTextView;
	
	private int accountType = 0; // 0：普通用户；  1: 工艺家

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_personal_info);
		mContext = PersonalInfo.this;
		mResources = getResources();
		DataServer.getInstance().initialize(mContext);
		
		accountType = getIntent().getIntExtra(Consts.EXTRA_ACCOUNT_TYPE, 0);

		initView();
		//updateViews();
		
		setupAccountType();
		
		//显示正在登录对话框
		String loading = mResources.getString(R.string.loading_data_tips);
		UIHelper.showDialogForLoading(mContext, loading, false);
		
		// 读取所有作品分类
		CategoryManager.getInstance().loadCategoryData();
		
		new GetUserInfoTask().execute();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		
		avatarImageView = (RoundImageView) findViewById(R.id.avatarImageView);
		avatarLinearLayout = (LinearLayout) findViewById(R.id.avatarLinearLayout);
		avatarLinearLayout.setOnClickListener(this);
		
		signatureTextView = (TextView) findViewById(R.id.signatureTextView);
		signatureLinearLayout = (LinearLayout) findViewById(R.id.signatureLinearLayout);
		signatureLinearLayout.setOnClickListener(this);

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
		
		bindPhoneTextView = (TextView) findViewById(R.id.bindPhoneTextView);
		bindPhoneLinearLayout = (LinearLayout) findViewById(R.id.bindPhoneLinearLayout);
		bindPhoneLinearLayout.setOnClickListener(this);
		
		accountTypeTextView = (TextView) findViewById(R.id.accountTypeTextView);
		accountTypeLinearLayout = (LinearLayout) findViewById(R.id.accountTypeLinearLayout);
		accountTypeLinearLayout.setOnClickListener(this);
	}
	
	
	/**
	 * 根据账号类型刷新界面
	 */
	private void setupAccountType()
	{
		// 普通用户
		if (accountType == 0)
		{
			titleLinearLayout.setVisibility(View.GONE);
			designationLinearLayout.setVisibility(View.GONE);
			intangibleheritageLinearLayout.setVisibility(View.GONE);
			workAgeLinearLayout.setVisibility(View.GONE);
			categoryLinearLayout.setVisibility(View.GONE);
			areaLinearLayout.setVisibility(View.GONE);
			associationLinearLayout.setVisibility(View.GONE);
			
			accountTypeTextView.setText(mResources.getString(R.string.personal_info_normal_acount));
		}
		else // 认证工艺家
		{
			titleLinearLayout.setVisibility(View.VISIBLE);
			designationLinearLayout.setVisibility(View.VISIBLE);
			intangibleheritageLinearLayout.setVisibility(View.VISIBLE);
			workAgeLinearLayout.setVisibility(View.VISIBLE);
			categoryLinearLayout.setVisibility(View.VISIBLE);
			areaLinearLayout.setVisibility(View.VISIBLE);
			associationLinearLayout.setVisibility(View.VISIBLE);
			accountTypeTextView.setText(mResources.getString(R.string.personal_info_artisan_account));
		}
		
		// 游客身份禁止编辑修改功能
		if (Consts.IS_GUEST(mContext))
		{
			avatarLinearLayout.setClickable(false);
			signatureLinearLayout.setClickable(false);
			nameLinearLayout.setClickable(false);
			titleLinearLayout.setClickable(false);
			designationLinearLayout.setClickable(false);
			intangibleheritageLinearLayout.setClickable(false);
			workAgeLinearLayout.setClickable(false);
			categoryLinearLayout.setClickable(false);
			areaLinearLayout.setClickable(false);
			associationLinearLayout.setClickable(false);
		}
	}
	

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.setting_personal_info));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

//	/**
//	 * 更新显示
//	 */
//	private void updateViews()
//	{
//		signatureTextView.setText(curSignature);
//		nameTextView.setText(curName);
//		titleTextView.setText(curTitle);
//		designationTextView.setText(curDesignation);
//		intangibleheritageTextView.setText(curIntangibleheritage);
//		workAgeTextView.setText(curWorkAge + "年");
//		categoryTextView.setText(curCategory);
//		areaextView.setText(curProvince + " " + curCity + " " + curDistinct);
//		associationTextView.setText(curAssociation);
//		bindPhoneTextView.setText(curPhone);
//	}
	
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
		case R.id.signatureLinearLayout: // 个人签名
			UIHelper.showEditSinglelineContent(mContext, Consts.INFO_TYPE_SIGNATURE, curSignature);
			break;
		case R.id.nameLinearLayout: // 姓名
			UIHelper.showEditSinglelineContent(mContext, Consts.INFO_TYPE_NAME, curName);
			break;
		case R.id.titleLinearLayout: // 职称
			showChoiceDialog(Consts.INFO_TYPE_TITLE, titleTextView);
			break;
		case R.id.designationLinearLayout: // 称号
			showChoiceDialog(Consts.INFO_TYPE_DESIGNATION, designationTextView);
			break;
		case R.id.intangibleheritageLinearLayout: // 非遗
			showChoiceDialog(Consts.INFO_TYPE_INTANGIBLEHERITAGE, intangibleheritageTextView);
			break;
		case R.id.workAgeLinearLayout: // 从业年限
			selectWorkage();
			break;
		case R.id.categoryLinearLayout: // 工艺品类
			showChoiceDialog(Consts.INFO_TYPE_CATEGORY, categoryTextView);
			break;
		case R.id.areaLinearLayout: // 地区
			UIHelper.showSelectAreaActivity(mContext, curProvince, curCity, curDistinct);
			break;
		case R.id.associationLinearLayout: // 所属协会
			UIHelper.showEditSinglelineContent(mContext, Consts.INFO_TYPE_ASSOCIATION, curAssociation);
			break;
//		case R.id.bindPhoneLinearLayout: // 绑定手机
//			break;
//		case R.id.accountTypeLinearLayout: // 账号类型
//			break;
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
			if (Consts.RESULT_CODE_EDIT_NAME == resultCode) 
			{
				// 没修改
				String newContent = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				if (newContent.equals(curName))
					return ;
				
				curName = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				nameTextView.setText(curName);
				
				// 提交服务器修改
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
				new SetUserInfoTask().execute(Consts.INFO_TYPE_NAME);
			} 
			else if (Consts.RESULT_CODE_EDIT_SIGNATURE == resultCode) 
			{
				// 没修改
				String newContent = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				if (newContent.equals(curSignature))
					return ;
				
				curSignature = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				signatureTextView.setText(curSignature);
				
				// 提交服务器修改
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
				new SetUserInfoTask().execute(Consts.INFO_TYPE_SIGNATURE);
			} 
			else if (Consts.RESULT_CODE_EDIT_ASSOCIATION == resultCode)
			{
				// 没修改
				String newContent = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				if (newContent.equals(curAssociation))
					return ;
				
				curAssociation = data.getStringExtra(Consts.EXTRA_EDIT_CONENT);
				associationTextView.setText(curAssociation);
				
				// 提交服务器修改
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
				new SetUserInfoTask().execute(Consts.INFO_TYPE_ASSOCIATION);
			}
		}
		else if (Consts.REQUEST_CODE_EDIT_AREA == requestCode) // 编辑地区
		{
			String newCurProvince = data.getStringExtra(Consts.EXTRA_CONTENT_PROVINCE);
			String newCurCity = data.getStringExtra(Consts.EXTRA_CONTENT_CITY);
			String newCurDistinct = data.getStringExtra(Consts.EXTRA_CONTENT_DISTINCT);
			
			// 没修改
			if (newCurProvince.equals(curProvince) && newCurCity.equals(curCity) && newCurDistinct.equals(curDistinct))
				return ;
				
			curProvince = newCurProvince;
			curCity = newCurCity;
			curDistinct = newCurDistinct;
				
			// 更新地区显示
			areaextView.setText(curProvince + " " + curCity + " " + curDistinct);
			// 提交服务器修改
			UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
			new SetUserInfoTask().execute(Consts.INFO_TYPE_AREA);
		}
		else if(requestCode == Consts.RESULT_TAKE_PICTURE) // 启动相机
		{ 
			String bitmapDir = SDCardFileUtils.getSDCardPath() + Consts.PHOTO_DIR_PATH;
			Bitmap headBitmap = BitmapFactory.decodeFile(bitmapDir + Consts.PHOTO_TEMP_NAME);
			int scaleHeight = Consts.AVATAR_SIZE * headBitmap.getHeight() / headBitmap.getWidth();
			byte[] imgByte = Graphic.ConvertBitmapToByteArray(headBitmap, 
					Consts.AVATAR_SIZE, scaleHeight);
			String imgData = Base64.encodeToString(imgByte, Base64.DEFAULT);
			new UpdateAvatarTask().execute(imgData);
		} 
		else if(requestCode == Consts.REQUEST_CODE_OPEN_ALBUM) 
		{
			ContentResolver resolver = getContentResolver();
			try {
				//获得图片的Uri
				Uri originalUri = data.getData();
				InputStream picStream = resolver.openInputStream(Uri.parse(originalUri.toString()));
				Bitmap headBitmap = BitmapFactory.decodeStream(picStream);
				if(headBitmap != null) {
					int scaleHeight = Consts.AVATAR_SIZE * headBitmap.getHeight() / headBitmap.getWidth();
					byte[] imgByte = Graphic.ConvertBitmapToByteArray(headBitmap, 
							Consts.AVATAR_SIZE, scaleHeight);
					String imgData = Base64.encodeToString(imgByte, Base64.DEFAULT);
					//修改头像
					new UpdateAvatarTask().execute(imgData);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showChoiceDialog(final int infoType, final TextView textView)
	{
		String [] items = null;
		int titleId = 0;
		int checkedItem = 0;
		switch (infoType)
		{
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
			//items = getResources().getStringArray(R.array.category);
			items = CategoryManager.getInstance().getClassNameArray();
			titleId = R.string.personal_info_category;
			checkedItem = findPosition(CategoryManager.getInstance().getClassNameById(curCategory), items);
			break;
		}
		
		Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setIcon(R.drawable.logo_small);
		dialog.setTitle("请选择" + this.getResources().getString(titleId));
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
        	  
           }
        });
        final String [] tempItems = items;
		dialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
        	 String selectContent = tempItems[which];
        	switch (infoType)
      		{
      		case Consts.INFO_TYPE_TITLE:
      			// 没修改
				if (selectContent.equals(curTitle))
				{
					dialog.dismiss();
					return ;
				}
      			curTitle = selectContent;
      			// 提交服务器修改
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
				new SetUserInfoTask().execute(Consts.INFO_TYPE_TITLE);
      			break;
      		case Consts.INFO_TYPE_DESIGNATION:
      			// 没修改
				if (selectContent.equals(curDesignation))
				{
					dialog.dismiss();
					return ;
				}
      			curDesignation = selectContent;
      			// 提交服务器修改
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
				new SetUserInfoTask().execute(Consts.INFO_TYPE_DESIGNATION);
      			break;
      		case Consts.INFO_TYPE_INTANGIBLEHERITAGE:
      			// 没修改
				if (selectContent.equals(curIntangibleheritage))
				{
					dialog.dismiss();
					return ;
				}
      			curIntangibleheritage = selectContent;
      			// 提交服务器修改
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
				new SetUserInfoTask().execute(Consts.INFO_TYPE_INTANGIBLEHERITAGE);
      			break;
      		case Consts.INFO_TYPE_CATEGORY:
      			// 没修改
      			String newCategory = CategoryManager.getInstance().getClassIdByName(selectContent);
				if (newCategory.equals(curCategory))
				{
					dialog.dismiss();
					return ;
				}
      			curCategory = CategoryManager.getInstance().getClassIdByName(selectContent);
      			// 提交服务器修改
				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
				new SetUserInfoTask().execute(Consts.INFO_TYPE_CATEGORY);
      			break;
      		}
        	  textView.setText(selectContent);
        	  dialog.dismiss();
           }
        }).create();
      dialog.show();
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
	
	private DatePickerDialog datePickerDialog = null;
	private void selectWorkage()
	{
		final Calendar c = Calendar.getInstance();
		int curSelectYear = c.get(Calendar.YEAR) - curWorkAge + 1;
		datePickerDialog  = new CustomerDatePickerDialog(
            this,
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                	int newWorkAge = c.get(Calendar.YEAR) - year;
                	if (newWorkAge == curWorkAge)
                		return ;
                	curWorkAge = newWorkAge;
                	workAgeTextView.setText(curWorkAge + "年");
                	
         			// 提交服务器修改
    				UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), false);
    				new SetUserInfoTask().execute(Consts.INFO_TYPE_WORKAGE);
                }
            }, 
            curSelectYear, // 传入年份
            0, // 传入月份
            0 // 传入天数
        );
		datePickerDialog.setIcon(R.drawable.logo_small);
		datePickerDialog.setTitle("选择从业年份");
		datePickerDialog.show();
		DatePicker dp = findDatePicker((ViewGroup) datePickerDialog.getWindow().getDecorView());  
		if (dp != null) {  
		    ((ViewGroup)((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE); 
		    ((ViewGroup)((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE); 
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
	 * 获取我的用户资料
	 * @author weigong
	 *
	 */
	private class GetUserInfoTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(1000);
				mDatas = DataServer.getInstance().getUserInfo();
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
				//ToastUtils.show(mContext, "加载成功");
				setUserInfoFromNet();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
	
	/**
	 * 解析并显示调用接口返回数据
	 */
	private void setUserInfoFromNet() 
	{
		// 读取数据
		if (!StringUtils.isEmpty(mDatas.get(Consts.PHOTO).toString()))
			curAvatarUrl = mDatas.get(Consts.PHOTO).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.NICKNAME).toString()))
			curName = mDatas.get(Consts.NICKNAME).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.SIGNATURE).toString()))
			curSignature = mDatas.get(Consts.SIGNATURE).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.JOB).toString()))
			curTitle = mDatas.get(Consts.JOB).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.CALLNAME).toString()))
			curDesignation = mDatas.get(Consts.CALLNAME).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.INTANGIBLEHERITAGE).toString()))
			curIntangibleheritage = mDatas.get(Consts.INTANGIBLEHERITAGE).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.WORK_AGE).toString()))
			curWorkAge = Integer.parseInt(mDatas.get(Consts.WORK_AGE).toString());
		if (!StringUtils.isEmpty(mDatas.get(Consts.CATEGORY).toString()))
			curCategory = mDatas.get(Consts.CATEGORY).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.PROVINCE).toString()))
			curProvince = mDatas.get(Consts.PROVINCE).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.CITY).toString()))
			curCity = mDatas.get(Consts.CITY).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.DISTINCT).toString()))
			curDistinct = mDatas.get(Consts.DISTINCT).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.ASSOCIATION).toString()))
			curAssociation = mDatas.get(Consts.ASSOCIATION).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.PHONE).toString()))
			curPhone = mDatas.get(Consts.PHONE).toString();

		// 更新UI控件
		if (!curAvatarUrl.isEmpty())
			mImageLoader.loadImage(curAvatarUrl, avatarImageView, true);
		nameTextView.setText(curName);
		signatureTextView.setText(curSignature);
		titleTextView.setText(curTitle);
		designationTextView.setText(curDesignation);
		intangibleheritageTextView.setText(curIntangibleheritage);
		workAgeTextView.setText(curWorkAge + "年");
		categoryTextView.setText(CategoryManager.getInstance().getClassNameById(curCategory));
		areaextView.setText(curProvince + " " + curCity + " " + curDistinct);
		associationTextView.setText(curAssociation);
		bindPhoneTextView.setText(curPhone);
		
		// 将接口返回数据保存至本地
		LocationUtil.writeInit(
				mContext,
				new String[] { Consts.TOKEN, Consts.UID, Consts.PHOTO, 
						Consts.PHONE, Consts.NICKNAME, Consts.REALNAME, Consts.SIGNATURE,
						Consts.PROVINCE, Consts.CITY, Consts.DISTINCT, 
						Consts.JOB, Consts.CALLNAME, Consts.CATEGORY, Consts.IS_AUTH, 
						Consts.INTANGIBLEHERITAGE, Consts.WORK_AGE, Consts.AID, Consts.ASSOCIATION,
						Consts.INTRODUCE, Consts.VOICE_PATH},
						
				new String[] {
						mDatas.get(Consts.TOKEN).toString(),
						mDatas.get(Consts.UID).toString(),
						mDatas.get(Consts.PHOTO).toString(),
						mDatas.get(Consts.PHONE).toString(),
						mDatas.get(Consts.NICKNAME).toString(),
						mDatas.get(Consts.REALNAME).toString(),
						mDatas.get(Consts.SIGNATURE).toString(),
						mDatas.get(Consts.PROVINCE).toString(),
						mDatas.get(Consts.CITY).toString(),
						mDatas.get(Consts.DISTINCT).toString(),
						mDatas.get(Consts.JOB).toString(),
						mDatas.get(Consts.CALLNAME).toString(),
						mDatas.get(Consts.CATEGORY).toString(),
						mDatas.get(Consts.IS_AUTH).toString(),
						mDatas.get(Consts.INTANGIBLEHERITAGE).toString(),
						mDatas.get(Consts.WORK_AGE).toString(),
						mDatas.get(Consts.AID).toString(),
						mDatas.get(Consts.ASSOCIATION).toString(),
						mDatas.get(Consts.INTRODUCE).toString(),
						mDatas.get(Consts.VOICE_PATH).toString()
						});
	}
	
	
	/**
	 * 修改用户资料接口
	 */
	private class SetUserInfoTask extends AsyncTask<Integer, Void, Boolean> {

		int currentInfoType = -1;
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				//Thread.sleep(1000);
				
				currentInfoType = params[0];
				switch (currentInfoType)
				{
				case Consts.INFO_TYPE_AVATAR: // 头像
					break;
				case Consts.INFO_TYPE_SIGNATURE: // 个人签名
					mDatas = DataServer.getInstance().setUserInfo(2, curSignature, 
							null, null, null, null, -1, null, null, null, null, null);
					break;
				case Consts.INFO_TYPE_NAME: // 姓名
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							curName, null, null, null, -1, null, null, null, null, null);
					break;
				case Consts.INFO_TYPE_TITLE: // 职称
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							null, curTitle, null, null, -1, null, null, null, null, null);
					break;
				case Consts.INFO_TYPE_DESIGNATION: // 称号
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							null, null, curDesignation, null, -1, null, null, null, null, null);
					break;
				case Consts.INFO_TYPE_INTANGIBLEHERITAGE: // 非遗
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							null, null, null, curIntangibleheritage, -1, null, null, null, null, null);
					break;
				case Consts.INFO_TYPE_WORKAGE: // 从业年限
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							null, null, null, null, curWorkAge, null, null, null, null, null);
					break;
				case Consts.INFO_TYPE_CATEGORY: // 工艺品类
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							null, null, null, null, -1, curCategory, null, null, null, null);
					break;
				case Consts.INFO_TYPE_AREA: // 地区
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							null, null, null, null, -1, null, curProvince, curCity, curDistinct, null);
					break;
				case Consts.INFO_TYPE_ASSOCIATION: // 所属协会
					mDatas = DataServer.getInstance().setUserInfo(2, null, 
							null, null, null, null, -1, null, null, null, null, curAssociation);
					break;
				}

				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			
			switch (currentInfoType)
			{
			case Consts.INFO_TYPE_AVATAR: // 头像
				LocationUtil.writeInit(mContext, Consts.PHOTO, curAvatarUrl);
				break;
			case Consts.INFO_TYPE_SIGNATURE: // 个人签名
				LocationUtil.writeInit(mContext, Consts.SIGNATURE, curSignature);
				break;
			case Consts.INFO_TYPE_NAME: // 姓名
				LocationUtil.writeInit(mContext, Consts.NICKNAME, curName);
				break;
			case Consts.INFO_TYPE_TITLE: // 职称
				LocationUtil.writeInit(mContext, Consts.JOB, curTitle);
				break;
			case Consts.INFO_TYPE_DESIGNATION: // 称号
				LocationUtil.writeInit(mContext, Consts.CALLNAME, curDesignation);
				break;
			case Consts.INFO_TYPE_INTANGIBLEHERITAGE: // 非遗
				LocationUtil.writeInit(mContext, Consts.INTANGIBLEHERITAGE, curIntangibleheritage);
				break;
			case Consts.INFO_TYPE_WORKAGE: // 从业年限
				LocationUtil.writeInit(mContext, Consts.WORK_AGE, curWorkAge + "");
				break;
			case Consts.INFO_TYPE_CATEGORY: // 工艺品类
				LocationUtil.writeInit(mContext, Consts.CATEGORY, curCategory);
				break;
			case Consts.INFO_TYPE_AREA: // 地区
				LocationUtil.writeInit(mContext, Consts.PROVINCE, curProvince);
				LocationUtil.writeInit(mContext, Consts.CITY, curCity);
				LocationUtil.writeInit(mContext, Consts.DISTINCT, curDistinct);
				break;
			case Consts.INFO_TYPE_ASSOCIATION: // 所属协会
				LocationUtil.writeInit(mContext, Consts.ASSOCIATION, curAssociation);
				break;
			}
			
			if(isSuccess) {
				ToastUtils.show(mContext, mResources.getString(R.string.modify_success_tips));
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
	
	/**
	 * 修改头像
	 * @author AA
	 * @Date 2014-12-26
	 */
	private class UpdateAvatarTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UIHelper.showDialogForLoading(mContext, mResources.getString(R.string.modify_loading_text), true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//Thread.sleep(500);
				mUpdateAvatarDatas = DataServer.getInstance().updateAvatar(params[0]);
				return mUpdateAvatarDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			UIHelper.hideDialogForLoading();
			if(isSuccess) {
				ToastUtils.show(mContext, mResources.getString(R.string.modify_success_tips));
				
				//加载最新头像
				curAvatarUrl = mUpdateAvatarDatas.get(Consts.PHOTO).toString();
				mImageLoader.loadImage(curAvatarUrl, avatarImageView, true);
				//将头像链接写到本地
				LocationUtil.writeInit(mContext, Consts.PHOTO, curAvatarUrl);
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
		UIHelper.showDialogForAddPic(mContext, StringUtils.ArrayToList(UIHelper.POPUPWINDOW_KEY, new String[] { from_camera, from_album, cancel }), this);
	}
	
}