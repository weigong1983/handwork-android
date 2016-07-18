package com.daiyan.handwork.common;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import com.daiyan.handwork.R;
import com.daiyan.handwork.adapter.CommonAdapter;
import com.daiyan.handwork.app.activity.AboutUs;
import com.daiyan.handwork.app.activity.ActivitysList;
import com.daiyan.handwork.app.activity.Artisans;
import com.daiyan.handwork.app.activity.Authentication;
import com.daiyan.handwork.app.activity.CommentPub;
import com.daiyan.handwork.app.activity.EditSinglelineContent;
import com.daiyan.handwork.app.activity.Feedback;
import com.daiyan.handwork.app.activity.ForgotPassword;
import com.daiyan.handwork.app.activity.HelpCenter;
import com.daiyan.handwork.app.activity.Homepage;
import com.daiyan.handwork.app.activity.ImageFolder;
import com.daiyan.handwork.app.activity.ImageFolderItem;
import com.daiyan.handwork.app.activity.ImagePagerActivity;
import com.daiyan.handwork.app.activity.Institute;
import com.daiyan.handwork.app.activity.LikeWorks;
import com.daiyan.handwork.app.activity.ModifyPassword;
import com.daiyan.handwork.app.activity.NoticeList;
import com.daiyan.handwork.app.activity.PersonalInfo;
import com.daiyan.handwork.app.activity.PublishWorks;
import com.daiyan.handwork.app.activity.SampleTabsStyled;
import com.daiyan.handwork.app.activity.SelectAreaActivity;
import com.daiyan.handwork.app.activity.SendMessage;
import com.daiyan.handwork.app.activity.WebviewActivity;
import com.daiyan.handwork.app.activity.WelcomePage;
import com.daiyan.handwork.app.activity.Login;
import com.daiyan.handwork.app.activity.MainActivity;
import com.daiyan.handwork.app.activity.PhotoViewer;
import com.daiyan.handwork.app.activity.Register;
import com.daiyan.handwork.app.activity.Setting;
import com.daiyan.handwork.app.activity.WorksBrowse;
import com.daiyan.handwork.app.activity.WorksCommunication;
import com.daiyan.handwork.app.activity.WorksDetail;
import com.daiyan.handwork.app.activity.WorksRegistrationCard;
import com.daiyan.handwork.app.widget.ShareDialog;
import com.daiyan.handwork.bean.LoginUsers;
import com.daiyan.handwork.bean.WorksCard;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.SDCardFileUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * @author AA
 * @Date 2014-11-23
 */
public class UIHelper {

	public static final String POPUPWINDOW_KEY = "pop_key";
	public static final int POPUPWINDOW_TOP = 0;
	public static final int POPUPWINDOW_RIGHT = 1;
	public static final int POPUPWINDOW_BOTTOM = 2;
	public static final int POPUPWINDOW_LEFT = 3;
	
	public static String PHOTO_PATH = "";
	
	/** 添加图片对话框 */
	private static PopupWindow mPopupWindow;
	/** 提示对话框 */
	private static AlertDialog mSingleTextDialog;
	/** 加载数据对话框 */
	private static Dialog mLoadingDialog;
	/** 登录页面更多用户对话框 */
	private static PopupWindow mMoreUsersDialog;
	
	/**
	 * 点击添加图片后显示对话框
	 * @param context
	 * @param width
	 * @param list
	 * @param direction
	 * @param onClickListener
	 */
	public static void showDialogForAddPic(final Activity context, final List<HashMap<String, String>> list, OnClickListener onClickListener) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.pw_common_layout, null);
		TextView textOne = (TextView)layout.findViewById(R.id.id_tv_pw_common_item_one);
		textOne.setText(list.get(0).get(POPUPWINDOW_KEY));
		textOne.setOnClickListener(onClickListener);
		TextView textTwo = (TextView)layout.findViewById(R.id.id_tv_pw_common_item_two);
		textTwo.setText(list.get(1).get(POPUPWINDOW_KEY));
		textTwo.setOnClickListener(onClickListener);
		TextView textThree = (TextView)layout.findViewById(R.id.id_tv_pw_common_item_three);
		textThree.setText(context.getResources().getString(R.string.pw_cancel));
		textThree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mPopupWindow != null && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
			}
		});
		
		mPopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		//设置背景图片，不能再布局中设置，要通过代码来设置
		mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.color.white));
		//点屏幕其他地方对话框消失
		mPopupWindow.setOutsideTouchable(true);
		//设置对话框显示和消失动画
		mPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
		//显示对话框
		mPopupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}
	
	/**
	 * 关闭添加图片对话框
	 */
	public static void hideDialogForAddPic() {
		if(mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	
	/**
	 * 显示提示对话框
	 * @param context
	 * @param content
	 * @param onClickListener
	 */
	public static void showDialogForSingleText(final Activity context, String content, OnClickListener onClickListener) {
		mSingleTextDialog = new AlertDialog.Builder(context).create();
		mSingleTextDialog.show();
		//对话框视图
		View view = LayoutInflater.from(context).inflate(R.layout.layout_for_single_text_dialog, null);
		TextView contentTextView = (TextView)view.findViewById(R.id.id_tv_single_text_dialog_content);
		contentTextView.setText(content);
		Button cancelBtn = (Button)view.findViewById(R.id.id_btn_single_text_dialog_cancel);
		Button confirmBtn = (Button)view.findViewById(R.id.id_btn_single_text_dialog_confirm);
		//为取消按钮设定监听事件
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSingleTextDialog.dismiss();
			}
		});
		//为确定按钮设定监听事件
		confirmBtn.setOnClickListener(onClickListener);
		//修改对话框样式
		Window window = mSingleTextDialog.getWindow();
		window.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		//点击对话框外部不关闭对话框
		mSingleTextDialog.setCanceledOnTouchOutside(false);
	}
	
	/**
	 * 关闭提示对话框
	 */
	public static void hideDialogForSingleText() {
		if(mSingleTextDialog != null && mSingleTextDialog.isShowing()) {
			mSingleTextDialog.dismiss();
		}
	}
	
	/**
	 * 显示注册提示对话框
	 * @param context
	 * @param content
	 * @param onClickListener
	 */
	public static void showDialogForRegister(final Activity context) 
	{
		String content = context.getResources().getString(R.string.guest_register_hint);
	    final AlertDialog registerDialog = new AlertDialog.Builder(context).create();
	    registerDialog.show();
	    
		//对话框视图
		View view = LayoutInflater.from(context).inflate(R.layout.layout_for_single_text_dialog, null);
		TextView contentTextView = (TextView)view.findViewById(R.id.id_tv_single_text_dialog_content);
		contentTextView.setText(content);
		Button cancelBtn = (Button)view.findViewById(R.id.id_btn_single_text_dialog_cancel);
		Button confirmBtn = (Button)view.findViewById(R.id.id_btn_single_text_dialog_confirm);
		//为取消按钮设定监听事件
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				registerDialog.dismiss();
			}
		});
		//为确定按钮设定监听事件
		confirmBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				// 进入注册界面
				//showRegister(context);
				showLogin(context, Consts.LOGIN_TYPE_NORMAL);
				registerDialog.dismiss();
			}
		});
		
		//修改对话框样式
		Window window = registerDialog.getWindow();
		window.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT));
		//点击对话框外部不关闭对话框
		registerDialog.setCanceledOnTouchOutside(false);
	}
	
	
	
	/**
	 * 显示加载对话框
	 * @param context 上下文
	 * @param msg 对话框显示内容
	 * @param cancelable 对话框是否可以取消
	 */
	public static void showDialogForLoading(Activity context, String msg, boolean cancelable) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
		TextView loadingText = (TextView)view.findViewById(R.id.id_tv_loading_dialog_text);
		loadingText.setText(msg);
		
		mLoadingDialog = new Dialog(context, R.style.loading_dialog_style);
		mLoadingDialog.setCancelable(cancelable);
		mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		mLoadingDialog.show();		
	}
	
	/**
	 * 关闭加载对话框
	 */
	public static void hideDialogForLoading() {
		if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.cancel();
		}
	}
	
	/**
	 * 结束指定Activity
	 * @param context
	 * @param isFinish
	 */
	private static void finishActivity(Activity context, boolean isFinish) {
		if(isFinish) {
			context.finish();
		}
	}
	
	/**
	 * 显示欢迎引导页
	 * @param context
	 */
	public static void showWelcomePage(Activity context, boolean isLauch) {
		Intent intent = new Intent(context, WelcomePage.class);
		intent.putExtra(Consts.EXTRA_IS_LAUNCH, isLauch);
		context.startActivity(intent);
		//context.finish();
	}
	
	/**
	 * 显示登录界面
	 * @param context
	 */
	public static void showLogin(Activity context, final int loginType) {
		Intent intent = new Intent(context, Login.class);
		intent.putExtra(Consts.EXTRA_LOGIN_TYPE, loginType);
		context.startActivity(intent);
		//context.finish();
	}
	
	/**
	 * 显示注册界面
	 * @param context
	 */
	public static void showRegister(Activity context) {
		Intent intent = new Intent(context, Register.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示忘记密码界面
	 * @param context
	 */
	public static void showForgotPassword(Activity context) {
		Intent intent = new Intent(context, ForgotPassword.class);
		context.startActivityForResult(intent, Consts.REQUEST_CODE_FORGOT_PASSWORD);
	}
	
	/**
	 * 显示首页
	 * @param context
	 */
	public static void showMain(Activity context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
		context.finish();
	}
	
	/**
	 * 显示评论页面
	 * @param context
	 */
	public static void showCommentPub(Activity context, String itemID) {
		Intent intent = new Intent(context, CommentPub.class);
		intent.putExtra(Consts.ITEMID, itemID);
		context.startActivityForResult(intent, Consts.REQUEST_CODE_COMMENT_PUB);
	}
	
	/**
	 * 打开拍照功能-上传头像
	 * @param context
	 */
	public static void takePictureForHeadImg(Activity context) {
		String dirName = SDCardFileUtils.getSDCardPath() + Consts.PHOTO_DIR_PATH;
		File file = new File(dirName, Consts.PHOTO_TEMP_NAME);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		context.startActivityForResult(intent, Consts.RESULT_TAKE_PICTURE);
		
	}
	
	/**
	 * 打开拍照功能
	 * @param context
	 */
	public static void takePicture(Activity context) {
		String dirName = SDCardFileUtils.getSDCardPath() + Consts.PHOTO_DIR_PATH;
		File file = new File(dirName, Consts.PHOTO_TEMP_NAME);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		PHOTO_PATH = file.getPath();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		context.startActivityForResult(intent, Consts.RESULT_TAKE_PICTURE);
	}
	
	/**
	 * 调用系统相册
	 * @param context
	 */
	public static void showSystemAlbum(Activity context) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		context.startActivityForResult(intent, Consts.REQUEST_CODE_OPEN_ALBUM);		
	}
	
	/**
	 * 显示手机相册目录
	 * @param context
	 * @param isFinish 是否关闭当前Activity
	 */
	public static void showImageFolderSelector(Activity context, boolean isFinish) {
		Intent intent = new Intent(context, ImageFolder.class);
		context.startActivity(intent);
		finishActivity(context, isFinish);
	}
	
	/**
	 * 显示手机相册中的图片
	 * @param context
	 */
	public static void showImageItemSelector(Activity context, List<ImageBucket> dataList, int position) {
		Intent intent = new Intent(context, ImageFolderItem.class);
		intent.putExtra(Consts.KEY_EXTRA_IMAGE_LIST, (Serializable)dataList.get(position).imageList);
		context.startActivity(intent);
		context.finish();
	}
	
	/**
	 * 显示相片查看页面
	 * @param context
	 * @param position
	 */
	public static void showPhotoViewer(Activity context, int position) {
		Intent intent = new Intent(context, PhotoViewer.class);
		intent.putExtra("ID", position);
		context.startActivity(intent);
	}
	
	/**
	 * 显示发表页面
	 * @param fragment
	 * @param type
	 */
	public static void showPub(Fragment fragment, int type) {
		Intent intent = null;
		switch (type) {
		
		}
		fragment.startActivityForResult(intent, Consts.REQUEST_CODE_PUB);
	}

	/**
	 * 显示设置界面
	 * @param context
	 */
	public static void showSetting(Activity context) {
		Intent intent = new Intent(context, Setting.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示关于代言
	 * @param context
	 */
	public static void showAboutUs(Activity context) {
		Intent intent = new Intent(context, AboutUs.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示帮助中心
	 * @param context
	 */
	public static void showHelpCenter(Activity context) {
		Intent intent = new Intent(context, HelpCenter.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示意见反馈
	 * @param context
	 */
	public static void showFeedback(Activity context) {
		Intent intent = new Intent(context, Feedback.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示图片集
	 * @param context
	 * @param urls
	 * @param position
	 */
	public static void showImagePager(Activity context, String[] urls, int position) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		intent.putExtra(Consts.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(Consts.EXTRA_IMAGE_INDEX, position);
		context.startActivity(intent);		
	}

	/**
	 * 显示作品详情页
	 * @param context
	 */
	public static void showWorksDetail(Activity context, String id) {
		Intent intent = new Intent(context, WorksDetail.class);
		intent.putExtra(Consts.EXTRA_WORKS_ID, id);
		//context.startActivity(intent);
		context.startActivityForResult(intent, Consts.REQUEST_REFRESH_WORKS_LIST);
	}
	
	/**
	 * 显示作品登记卡页
	 * @param context
	 */
	public static void showWorksRegistrationCard(Activity context, WorksCard worksCard) {
		Intent intent = new Intent(context, WorksRegistrationCard.class);
		Bundle bundle = new Bundle();  
		bundle.putSerializable(Consts.EXTRA_WORKS_CARD, (WorksCard)worksCard);  
		intent.putExtras(bundle);  
		context.startActivity(intent);
	}
	
	
	/**
	 * 显示作品制作流程页
	 * @param context
	 */
	public static void showEditSinglelineContent(Activity context, int contentType, String oldContent) {
		Intent intent = new Intent(context, EditSinglelineContent.class);
		intent.putExtra(Consts.EXTRA_CONTENT_TYPE, contentType);
		intent.putExtra(Consts.EXTRA_OLD_CONTENT, oldContent);
		context.startActivityForResult(intent, Consts.REQUEST_CODE_EDIT_CONTENT);
	}
	
	/**
	 * 给研究院发送留言
	 * @param context
	 */
	public static void showSendMessage(Activity context, String id) {
		Intent intent = new Intent(context, SendMessage.class);
		intent.putExtra(Consts.EXTRA_WORKS_ID, id);
		context.startActivity(intent);
	}
	
	
	/**
	 * 显示浏览器界面
	 * @param context
	 */
	public static void showWebView(Activity context, String webUrl, int webPage) {
		Intent intent = new Intent(context, WebviewActivity.class);
		intent.putExtra(Consts.EXTRA_WEB_URL, webUrl);
		intent.putExtra(Consts.EXTRA_WEB_PAGE, webPage);
		context.startActivity(intent);
	}
	
	
	
	/**
	 * 显示范例Tab指示器页面
	 * @param context
	 */
	public static void showSampleTabsStyled(Activity context) {
		Intent intent = new Intent(context, SampleTabsStyled.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示作品交流标签页
	 * @param context
	 */
	public static void showWorksCommunication(Activity context, String id, int frgment) {
		Intent intent = new Intent(context, WorksCommunication.class);
		intent.putExtra(Consts.EXTRA_WORKS_ID, id);
		intent.putExtra(Consts.EXTRA_FRGMENT, frgment);
		
		//context.startActivity(intent);
		context.startActivityForResult(intent, Consts.REQUEST_REFRESH_COMMENT_COUNT);
	}
	
	/**
	 * 显示通知列表
	 * @param context
	 */
	public static void showNoticeList(Activity context) {
		Intent intent = new Intent(context, NoticeList.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示活动列表
	 * @param context
	 */
	public static void showActivitysList(Activity context) {
		Intent intent = new Intent(context, ActivitysList.class);
		context.startActivity(intent);
	}
	
	
	/**
	 * 显示赞过的作品列表
	 * @param context
	 */
	public static void showLikeWorks(Activity context) {
		Intent intent = new Intent(context, LikeWorks.class);
		context.startActivity(intent);
	}
	
	/**
	 * 研究院
	 * @param context
	 */
	public static void showInstitute(Activity context) {
		Intent intent = new Intent(context, Institute.class);
		context.startActivity(intent);
	}
	
	/**
	 * 九宫格浏览作品
	 * @param context
	 * @param position
	 */
	public static void showWorksBrowse(Activity context, int browseType) {
		Intent intent = new Intent(context, WorksBrowse.class);
		intent.putExtra(Consts.BROWSE_WORKS_TYPE, browseType);
		context.startActivity(intent);
	}
	
	/**
	 * 显示工艺家
	 * @param context
	 */
	public static void showArtisans(Activity context) {
		Intent intent = new Intent(context, Artisans.class);
		context.startActivity(intent);
	}
	
	
	/**
	 * 显示修改密码界面
	 * @param context
	 */
	public static void showModifyPassword(Activity context) {
		Intent intent = new Intent(context, ModifyPassword.class);
		context.startActivity(intent);
	}
	
	
	/**
	 * 显示个人资料页面
	 * @param context
	 */
	public static void showPersonalInfo(Activity context, int accountType) {
		Intent intent = new Intent(context, PersonalInfo.class);
		intent.putExtra(Consts.EXTRA_ACCOUNT_TYPE, accountType);
		context.startActivity(intent);
	}
	
	/**
	 * 显示认证页面
	 * @param context
	 */
	public static void showAuthentication(Activity context) {
		Intent intent = new Intent(context, Authentication.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示个人主页
	 * @param context
	 */
	public static void showHomepage(Activity context, String uid) {
		Intent intent = new Intent(context, Homepage.class);
		intent.putExtra(Consts.EXTRA_UID, uid);
		context.startActivity(intent);
	}
	
	/**
	 * 显示发布作品说明页面
	 * @param context
	 */
	public static void showPublishWorks(Activity context) {
		Intent intent = new Intent(context, PublishWorks.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示地区选择界面
	 * @param context
	 */
	public static void showSelectAreaActivity(Activity context, String province, 
			String city, String distinct) {
		Intent intent = new Intent(context, SelectAreaActivity.class);
		intent.putExtra(Consts.EXTRA_CONTENT_PROVINCE, province);
		intent.putExtra(Consts.EXTRA_CONTENT_CITY, city);
		intent.putExtra(Consts.EXTRA_CONTENT_DISTINCT, distinct);
		context.startActivityForResult(intent, Consts.REQUEST_CODE_EDIT_AREA);
	}

	
	public static void showShareDialog(Activity context, final String content, 
			final String imageUrl, final String shareUrl, final PlatformActionListener listener) 
	{
		ShareSDK.initSDK(context);
		final ShareDialog shareDialog = new ShareDialog(context);
		//对话框取消按钮
		shareDialog.setCancelButtonOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareDialog.dismiss();
			}
		});
		//设置分享监听器
		shareDialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
				//设置分享内容
				ShareParams sp = new ShareParams();
				sp.setTitle("分享");
				//sp.setTitleUrl("http://www.daiyan123.com");
				sp.setText(content);
				sp.setImageUrl(imageUrl);
				
				// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
				sp.setTitleUrl(shareUrl);
	     		// text是分享文本，所有平台都需要这个字段
				sp.setText(content + " " + shareUrl);
				// url仅在微信（包括好友和朋友圈）中使用
				sp.setUrl(shareUrl);
				
				
				//判断分享的平台
				if(item.get("ItemText").equals(ShareDialog.QQ)) {
					Platform qq = ShareSDK.getPlatform(QQ.NAME);
					qq.setPlatformActionListener(listener);
					qq.share(sp);
				} else if(item.get("ItemText").equals(ShareDialog.QZONE)) {
					Platform qzone = ShareSDK.getPlatform(QZone.NAME);
					qzone.setPlatformActionListener(listener);
					qzone.share(sp);
				} else if(item.get("ItemText").equals(ShareDialog.SINA_WEIBO)) {
					Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
					weibo.setPlatformActionListener(listener);
					weibo.share(sp);
				} else if(item.get("ItemText").equals(ShareDialog.WECHAT)) {
					Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
					wechat.setPlatformActionListener(listener);
					wechat.share(sp);
				} else if(item.get("ItemText").equals(ShareDialog.WECHAT_MOMENTS)) {
					Platform moments = ShareSDK.getPlatform(WechatMoments.NAME);
					moments.setPlatformActionListener(listener);
					moments.share(sp);
				}
				shareDialog.dismiss();
			}
		});
		
		
		
	}
	
	/**
	 * 分享到社交平台
	 * @param context
	 * @param shareTitle
	 * @param shareUrl
	 * @param content
	 * @param imageUrl
	 */
	public static void showShare(Activity context, String shareTitle,
			String shareUrl, String content, String[] imageArray,
			float latitude, float longitude) {
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setSilent(false);
		oks.setDialogMode();

		// 分享时Notification的图标和文字
//		oks.setNotification(R.drawable.logo_small,
//				context.getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(shareTitle);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(shareUrl);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(content);
		
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (imageArray != null && imageArray.length > 0)
		{
			oks.setImageUrl(imageArray[0]);//确保SDcard下面存在此张图片
			// 腾讯微博才支持多张图片
			oks.setImageArray(imageArray);
		}
		
		
		// 地理位置
		oks.setLatitude(latitude);
        oks.setLongitude(longitude);
		
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(shareUrl);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(context.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(context.getString(R.string.web_site));

		// 启动分享GUI
		oks.show(context);
	}
	
	/**
	 * 后台自动分享到各大社交平台
	 * @param context
	 * @param shareTitle
	 * @param shareUrl
	 * @param content
	 * @param imageUrl
	 */
	public static void showShareToAll(Activity context, String shareTitle,
			String shareUrl, String content, String[] imageArray,
			float latitude, float longitude) {
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setSilent(false);
		oks.setDialogMode();

		// 分享时Notification的图标和文字
//		oks.setNotification(R.drawable.logo_small,
//				context.getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(shareTitle);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(shareUrl);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(content + " " + shareUrl);
		
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImageUrl(imageArray[0]);//确保SDcard下面存在此张图片
		// 腾讯微博才支持多张图片
		oks.setImageArray(imageArray);
		
		
		// 地理位置
		oks.setLatitude(latitude);
        oks.setLongitude(longitude);
		
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(shareUrl);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(context.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(context.getString(R.string.web_site));

		// 启动分享GUI
		oks.show(context);
	}
	
	
	/**
	 * 隐藏输入键盘
	 * @param context
	 * @param etContent
	 */
	public static void hideSoftInput(Context context, EditText etContent) {
		try {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
