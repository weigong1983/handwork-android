package com.daiyan.handwork.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 我的
 * @author 魏工
 * @Date 2015-05-06
 */
public class MineFragment extends BaseFragment {

	private Activity mContext;
	private Resources mResources;
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	/** 首页布局视图 */
	private View mMineView;
	
	private LinearLayout userinfoLinearLayout; // 【用户资料】栏目
	private LinearLayout authLinearLayout; // 【认证/我的主页】栏目
	private LinearLayout likeLinearLayout; // 【赞】栏目
	private LinearLayout activityLinearLayout; // 【活动】栏目
	private LinearLayout noticeLinearLayout; // 【通知】栏目
	private LinearLayout settingLinearLayout; // 【设置】栏目
	
	private RoundImageView avatarImageView; // 头像
	private TextView nameTextView; // 名字
	private TextView likeCountTextView; // 赞数目
	private TextView activityCountTextView; // 活动数目
	private TextView noticeCountTextView; // 通知数目
	
	private int isAuth = 0; // 认证状态： 0 普通用户   1 认证工艺家  2 认证中
	private ImageView authImageView;
	private TextView authTitleTextView;
	private TextView authHintTextView;
	
	private String uid;
	private String avatarUrl;
	private String oldAvatarUrl;
	private String nickname;
	private String likeCount = "0";
	private String activityCount = "0";
	private String noticeCount = "0";
	
	private boolean mHasInitFlag= false;
    
	public MineFragment() {
		// TODO Auto-generated constructor stub
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mMineView == null) {
			mMineView = inflater.inflate(R.layout.fragment_mine, container, false);
			mContext = getActivity();
			mResources = getResources();
			DataServer.getInstance().initialize(mContext);
			
			initViews();
			
			isAuth = Integer.parseInt(LocationUtil.readInit(mContext, Consts.IS_AUTH, "0"));
			uid = LocationUtil.readInit(mContext, Consts.UID, "");

			//显示正在登录对话框
			//String loading = mResources.getString(R.string.loading_data_tips);
			//UIHelper.showDialogForLoading(mContext, loading, false);
			
		}
		
		ViewGroup parent = (ViewGroup) mMineView.getParent();
		if (parent != null) {
			parent.removeView(mMineView);
		}
		return mMineView;
	}

	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 加载个人基础数据
		new LoadPersonCardTask().execute();
		
//		// 判断头像和昵称是否发生修改
//		String curName = LocationUtil.readInit(mContext, Consts.NICKNAME, "");
//		if (!curName.equalsIgnoreCase(nickname))
//		{
//			nameTextView.setText(curName);
//			nickname = curName;
//		}
//		
//		String curAvatarUrl = LocationUtil.readInit(mContext, Consts.PHOTO, "");
//		if (!curAvatarUrl.equalsIgnoreCase(avatarUrl))
//		{
//			mImageLoader.loadImage(curAvatarUrl, avatarImageView, true);
//			avatarUrl = curAvatarUrl;
//		}
	}

	private void initViews()
	{
		userinfoLinearLayout = (LinearLayout) mMineView.findViewById(R.id.userinfoLinearLayout);
		authLinearLayout = (LinearLayout) mMineView.findViewById(R.id.authLinearLayout);
		likeLinearLayout = (LinearLayout) mMineView.findViewById(R.id.likeLinearLayout);
		activityLinearLayout = (LinearLayout) mMineView.findViewById(R.id.activityLinearLayout);
		noticeLinearLayout = (LinearLayout) mMineView.findViewById(R.id.noticeLinearLayout);
		settingLinearLayout = (LinearLayout) mMineView.findViewById(R.id.settingLinearLayout);

		avatarImageView = (RoundImageView) mMineView.findViewById(R.id.avatarImageView);
		nameTextView = (TextView) mMineView.findViewById(R.id.nameTextView);
		likeCountTextView = (TextView) mMineView.findViewById(R.id.likeCountTextView);
		activityCountTextView = (TextView) mMineView.findViewById(R.id.activityCountTextView);
		noticeCountTextView = (TextView) mMineView.findViewById(R.id.noticeCountTextView);
		
		authImageView = (ImageView) mMineView.findViewById(R.id.authImageView);
		authTitleTextView = (TextView) mMineView.findViewById(R.id.authTitleTextView);
		authHintTextView = (TextView) mMineView.findViewById(R.id.authHintTextView);
		
		// 设置监听事件
		userinfoLinearLayout.setOnClickListener(mOnClickListener);
		authLinearLayout.setOnClickListener(mOnClickListener);
		likeLinearLayout.setOnClickListener(mOnClickListener);
		activityLinearLayout.setOnClickListener(mOnClickListener);
		noticeLinearLayout.setOnClickListener(mOnClickListener);
		settingLinearLayout.setOnClickListener(mOnClickListener);
		
		if (Consts.IS_GUEST(mContext))
			authLinearLayout.setVisibility(View.GONE);
		else
			authLinearLayout.setVisibility(View.VISIBLE);
	}

	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
			case R.id.userinfoLinearLayout: // 个人资料
				// 游客跳转到注册页面
				if (Consts.IS_GUEST(mContext))
				{
					UIHelper.showRegister(mContext);
					return ;
				}
					
				if (isAuth == 1)
				{
					UIHelper.showPersonalInfo(mContext, 1);
				}
				else 
				{
					UIHelper.showPersonalInfo(mContext, 0);
				}
				break;
			case R.id.authLinearLayout: // 认证
				// 游客禁止认证
				if (Consts.IS_GUEST(mContext))
					return ;
				
				if (isAuth == 0) // 未认证
				{
					UIHelper.showAuthentication(mContext);
				}
				else if (isAuth == 1) // 认证工艺家
				{
					UIHelper.showHomepage(mContext, uid);
				}
				break;
			case R.id.likeLinearLayout: // 赞过的作品
				UIHelper.showLikeWorks(mContext);
				break;
			case R.id.activityLinearLayout: // 参与过的活动
				if (Integer.parseInt(activityCount) > 0)
					UIHelper.showActivitysList(mContext);
				else
					ToastUtils.show(mContext, mResources.getString(R.string.mine_no_activity_tips));
				break;
			case R.id.noticeLinearLayout: // 通知列表
				if (Integer.parseInt(noticeCount) > 0)
					UIHelper.showNoticeList(mContext);
				else
					ToastUtils.show(mContext, mResources.getString(R.string.mine_no_notice_tips));
				break;
			case R.id.settingLinearLayout: // 设置
				UIHelper.showSetting(mContext);
				break;
			default:
				break;
			}
		}
	};
	
	
	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 获取我的页面基本资料信息
	 * @author weigong
	 *
	 */
	private class LoadPersonCardTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(2000);
				mDatas = DataServer.getInstance().personCard("");
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
	private void setUserInfoFromNet() {
		//个人信息
		if (!StringUtils.isEmpty(mDatas.get(Consts.LIKE_COUNT).toString()))
			likeCount = mDatas.get(Consts.LIKE_COUNT).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.ACTIVITY_COUNT).toString()))
			activityCount = mDatas.get(Consts.ACTIVITY_COUNT).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.NOTICE_COUNT).toString()))
			noticeCount = mDatas.get(Consts.NOTICE_COUNT).toString();

		HashMap<String, Object> userinfoMap = new HashMap<String, Object>();
		if (mDatas.get(Consts.USERINFO) != null)
		{
			try {
				userinfoMap = JsonUtils.getJsonValues(mDatas.get(Consts.USERINFO).toString());
				
				if (userinfoMap != null && userinfoMap.containsKey(Consts.IS_AUTH))
					isAuth = Integer.parseInt(userinfoMap.get(Consts.IS_AUTH).toString());
				if (userinfoMap != null && userinfoMap.containsKey(Consts.PHOTO))
					avatarUrl = userinfoMap.get(Consts.PHOTO).toString();
				if (userinfoMap != null && userinfoMap.containsKey(Consts.NICKNAME))
					nickname = userinfoMap.get(Consts.NICKNAME).toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (isAuth == 1) // 认证工艺家
		{
			authImageView.setBackgroundResource(R.drawable.icon_mine_homepage);
			authTitleTextView.setText(mResources.getString(R.string.mine_has_auth));
			authHintTextView.setText(mResources.getString(R.string.mine_has_auth_hint));
		}
		else if (isAuth == 2) // 认证中
		{
			authImageView.setBackgroundResource(R.drawable.icon_mine_auth);
			authTitleTextView.setText(mResources.getString(R.string.mine_auth_review));
			authHintTextView.setText(mResources.getString(R.string.mine_auth_review_hint));
		}
		else // 普通用户（未认证）
		{
			authImageView.setBackgroundResource(R.drawable.icon_mine_auth);
			authTitleTextView.setText(mResources.getString(R.string.mine_not_auth));
			authHintTextView.setText(mResources.getString(R.string.mine_not_auth_hint));
		}
		
		//设置个人头像
		if (!avatarUrl.isEmpty())
		{
			if (!mHasInitFlag) // 首次加载
			{
				mImageLoader.loadImage(avatarUrl, avatarImageView, true);
			}
			else
			{
				if (!oldAvatarUrl.equalsIgnoreCase(avatarUrl)) // 如果头像没变化，不做加载
					mImageLoader.loadImage(avatarUrl, avatarImageView, true);
			}
		}

		// 如果当前是游客账号，头像昵称栏引导用户注册会员
		if (Consts.IS_GUEST(mContext))
		{
			nameTextView.setText(mResources.getString(R.string.mine_register_hint));
		}
		else
		{
			// 没昵称显示登录用户名
			if (!StringUtils.isEmpty(nickname))
				nameTextView.setText(nickname);
			else
				nameTextView.setText(LocationUtil.readInit(mContext, Consts.UNAME, ""));
		}
		
		likeCountTextView.setText("(" + likeCount + ")");
		activityCountTextView.setText("(" + activityCount + ")");
		noticeCountTextView.setText("(" + noticeCount + ")");
		
		mHasInitFlag = true;
		oldAvatarUrl = avatarUrl;
	}
}
