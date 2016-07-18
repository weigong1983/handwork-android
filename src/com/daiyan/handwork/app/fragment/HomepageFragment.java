package com.daiyan.handwork.app.fragment;

import java.util.HashMap;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseFragment;
import com.daiyan.handwork.app.activity.Homepage;
import com.daiyan.handwork.app.widget.RoundImageView;
import com.daiyan.handwork.bean.UserInfo;
import com.daiyan.handwork.common.CategoryManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.SoundHelper;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.SoundHelper.OnPlayCompletionListener;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.BitmapUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.ToastUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 我的主页
 * @author 魏工
 * @Date 2015-05-06
 */
public class HomepageFragment extends BaseFragment{

	private Activity mContext;
	private Resources mResources;
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	/** 首页布局视图 */
	private View mParentView;
	
	private RoundImageView avatarImageView; // 头像
	private TextView nameTextView; // 名字
	private ImageView authImageView; // 认证图标
	private TextView titleTextView; // 称号
	
	private TextView areaTextView; // 地区
	private ImageView locationImageView; // 位置小图标
	private ImageView vocieButton; // 语音按钮
	private TextView secondsTextView; // 语音秒数
	private TextView recordButton;
	private TextView signatureTextView; // 个人签名
	
	private UserInfo mCurrentUserInfo;
	private OnFragmentDataLoadListening OnFragmentDataLoadListening = null;
	
	
	
	
	/** 语音对话框的显示状态 */
	private final int RECORD_DISMISS = -1;
	private final int RECORD_START = 0;
	private final int RECORD_RUNNING = 1;
	private final int RECORD_STOP = 2;
	/** 语音对话框 */
	private PopupWindow mRecordDialog;
	private View mRecordLayout;
	private ImageButton mRecordCloseBtn;
	private TextView mRecordTextView;
	private ImageView mRecordImageView;
	private Button mRecordCancelBtn;
	private Button mRecordSendBtn;
	private int mRecordStatus = RECORD_DISMISS;
	
	/** 计时器 */
	private Chronometer mRecordChronometer;
	
	private Bitmap[] mVociePlayingBitmap = new Bitmap[3];
	
	// 数据加载监听器
	public interface OnFragmentDataLoadListening{
	    void onUserDataLoad(UserInfo userInfo);
	}
	
	public void setOnPagerDataLoadListening(OnFragmentDataLoadListening listener){
		OnFragmentDataLoadListening = listener;
	}
	
	

	public HomepageFragment() {
		// TODO Auto-generated constructor stub
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mParentView == null) {
			mParentView = inflater.inflate(R.layout.fragment_homepage, container, false);
			mContext = getActivity();
			mResources = getResources();
			mCurrentUserInfo = new UserInfo();

			initViews();
			
			// 语音动画图标
			mVociePlayingBitmap[0] = BitmapUtils.readBitMap(mContext, R.drawable.btn_voice_0);
			mVociePlayingBitmap[1] = BitmapUtils.readBitMap(mContext, R.drawable.btn_voice_1);
			mVociePlayingBitmap[2] = BitmapUtils.readBitMap(mContext, R.drawable.btn_voice_2);
			
			// 读取个人主页数据
			String loading = mResources.getString(R.string.loading_data_tips);
			UIHelper.showDialogForLoading(mContext, loading, true);
			new GetUserInfoTask().execute();
		}
		
		ViewGroup parent = (ViewGroup) mParentView.getParent();
		if (parent != null) {
			parent.removeView(mParentView);
		}
		return mParentView;
	}

	private void initViews()
	{
		avatarImageView = (RoundImageView) mParentView.findViewById(R.id.avatarImageView);
		nameTextView = (TextView) mParentView.findViewById(R.id.nameTextView);
		authImageView = (ImageView) mParentView.findViewById(R.id.authImageView);
		titleTextView = (TextView) mParentView.findViewById(R.id.titleTextView);
		areaTextView = (TextView) mParentView.findViewById(R.id.areaTextView);
		locationImageView = (ImageView) mParentView.findViewById(R.id.locationImageView);
		vocieButton = (ImageView) mParentView.findViewById(R.id.vocieButton);
		secondsTextView = (TextView) mParentView.findViewById(R.id.secondsTextView);
		recordButton = (TextView) mParentView.findViewById(R.id.recordButton);
		signatureTextView = (TextView) mParentView.findViewById(R.id.signatureTextView);
		
		// 设置监听事件
		vocieButton.setOnClickListener(mOnClickListener);
		recordButton.setOnClickListener(mOnClickListener);
		recordButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
		recordButton.getPaint().setAntiAlias(true);//抗锯齿
		
		// 我的主页可以录制【非游客身份】
		if (Homepage.isMe && !Consts.IS_GUEST(mContext))
		{
			recordButton.setVisibility(View.VISIBLE);
		}
	}
	
	private int mIndex = 0;
	private final int PLAYING_DELAY = 300;
	Handler mVoicePlayinghandler = new Handler();
    Runnable mVoicePlayingRunnable = new Runnable() {

        @Override
        public void run() {
            try {
            	vocieButton.setImageBitmap(mVociePlayingBitmap[mIndex]);
            	mIndex++;
            	mIndex %= 3;
            	mVoicePlayinghandler.postDelayed(this, PLAYING_DELAY);
            } catch (Exception e) {
            }
        }
    };
	

	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
			case R.id.vocieButton: // 语音图标
				SoundHelper.play(mCurrentUserInfo.voicepath, new OnPlayCompletionListener() {
					@Override
					public void onCompletion() {
						mVoicePlayinghandler.removeCallbacks(mVoicePlayingRunnable);
						mIndex = 2;
						vocieButton.setImageBitmap(mVociePlayingBitmap[mIndex]);
					}
				});
				// 动画切换播放按钮图标
				mIndex = 0;
				mVoicePlayinghandler.postDelayed(mVoicePlayingRunnable, PLAYING_DELAY);
				
				break;
			case R.id.recordButton: // 录制按钮
				showDialogForRecord();
				break;
				
				/**
				 * 语音对话框右上角关闭按钮
				 * 语音对话框取消按钮
				 */
				case R.id.id_ib_pub_record_close:
				case R.id.id_btn_pub_record_cancel:
					if(mRecordDialog != null && mRecordDialog.isShowing()) {
						mRecordStatus = RECORD_DISMISS;
						mRecordDialog.dismiss();
					}
					break;
					
				//语音对话框中间按钮
				case R.id.id_iv_pub_record:
					switch (mRecordStatus) {
					case RECORD_START:
						mRecordStatus = RECORD_RUNNING;
						//改变图片，隐藏“点击录音”，显示计时器,开始计时
						mRecordImageView.setImageResource(R.drawable.icon_record_running);
						mRecordTextView.setVisibility(View.GONE);
						mRecordChronometer.setVisibility(View.VISIBLE);
						mRecordChronometer.setBase(SystemClock.elapsedRealtime());
						mRecordChronometer.start();
						//开始录制语音
						SoundHelper.start(SoundHelper.SOUND_FILENAME);
						break;
						
					case RECORD_RUNNING:
						mRecordStatus = RECORD_STOP;
						//更换图片，显示取消和发送按钮,停止计时
						mRecordImageView.setImageResource(R.drawable.icon_record_stop);
						View line = mRecordLayout.findViewById(R.id.id_line_pub_record_bottom);
						line.setVisibility(View.VISIBLE);
						LinearLayout bottomLayout = (LinearLayout)mRecordLayout.findViewById(R.id.id_ll_pub_record_bottom);
						bottomLayout.setVisibility(View.VISIBLE);
						mRecordChronometer.stop();
						//停止录制语音
						SoundHelper.stop();
						break;
						
					case RECORD_STOP:
						//播放录制的语音
						SoundHelper.play(SoundHelper.SOUND_PATH + SoundHelper.SOUND_FILENAME, null);
						break;
					}
					
					break;
					
				//语音对话框发送按钮
				case R.id.id_btn_pub_record_send:
					String voiceBytes = SoundHelper.soundsToBase64(SoundHelper.SOUND_PATH + SoundHelper.SOUND_FILENAME);
					String loading = mResources.getString(R.string.sending_voice_tips);
					UIHelper.showDialogForLoading(mContext, loading, true);
					new SendVoiceTask().execute(voiceBytes);
					
					if(mRecordDialog != null && mRecordDialog.isShowing()) {
						mRecordStatus = RECORD_DISMISS;
						mRecordDialog.dismiss();
					}
					
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
	 * 获取我的用户资料
	 * @author weigong
	 *
	 */
	private class GetUserInfoTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(1000);
				if (Homepage.isMe)
					mDatas = DataServer.getInstance().getUserInfo();
				else
					mDatas = DataServer.getInstance().getOtherUserInfo(Homepage.mUserId);
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
		mCurrentUserInfo.avatarUrl = mDatas.get(Consts.B_PHOTO).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.NICKNAME).toString()))
			mCurrentUserInfo.nickName = mDatas.get(Consts.NICKNAME).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.SIGNATURE).toString()))
			mCurrentUserInfo.signature = mDatas.get(Consts.SIGNATURE).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.JOB).toString()))
			mCurrentUserInfo.title = mDatas.get(Consts.JOB).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.CALLNAME).toString()))
			mCurrentUserInfo.designation = mDatas.get(Consts.CALLNAME).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.INTANGIBLEHERITAGE).toString()))
			mCurrentUserInfo.intangibleheritage = mDatas.get(Consts.INTANGIBLEHERITAGE).toString();
		if (!StringUtils.isEmpty(mDatas.get(Consts.WORK_AGE).toString()))
			mCurrentUserInfo.workAge = Integer.parseInt(mDatas.get(Consts.WORK_AGE).toString());
		mCurrentUserInfo.category = mDatas.get(Consts.CATEGORY).toString();
		mCurrentUserInfo.province = mDatas.get(Consts.PROVINCE).toString();
		mCurrentUserInfo.city = mDatas.get(Consts.CITY).toString();
		mCurrentUserInfo.distinct = mDatas.get(Consts.DISTINCT).toString();
		mCurrentUserInfo.association = mDatas.get(Consts.ASSOCIATION).toString();
		mCurrentUserInfo.phone = mDatas.get(Consts.PHONE).toString();
		mCurrentUserInfo.introduce = mDatas.get(Consts.INTRODUCE).toString();
		mCurrentUserInfo.voicepath = mDatas.get(Consts.VOICE_PATH).toString();
		
		// 更新UI控件
		if (!StringUtils.isEmpty(mCurrentUserInfo.avatarUrl))
			mImageLoader.loadImage(mCurrentUserInfo.avatarUrl, avatarImageView, true);
		// 没昵称显示手机号
		if (!StringUtils.isEmpty(mCurrentUserInfo.nickName))
			nameTextView.setText(mCurrentUserInfo.nickName);
		else
			nameTextView.setText(mCurrentUserInfo.phone);
		
		titleTextView.setText(mCurrentUserInfo.designation);
		authImageView.setVisibility(View.VISIBLE);

		// 地区显示
		if (StringUtils.isEmpty(mCurrentUserInfo.province)
			|| StringUtils.isEmpty(mCurrentUserInfo.city)
			|| StringUtils.isEmpty(mCurrentUserInfo.distinct))
		{
			areaTextView.setVisibility(View.GONE);
			locationImageView.setVisibility(View.GONE);
		}
		else
		{
			areaTextView.setVisibility(View.VISIBLE);
			locationImageView.setVisibility(View.VISIBLE);
			
			areaTextView.setText(mCurrentUserInfo.province + " " 
					+ mCurrentUserInfo.city + " " 
					+ mCurrentUserInfo.distinct);
		}
		
		signatureTextView.setText(mCurrentUserInfo.signature);
		
		if (OnFragmentDataLoadListening != null)
			OnFragmentDataLoadListening.onUserDataLoad(mCurrentUserInfo);
		
		// 存在录音文件
		if (!StringUtils.isEmpty(mCurrentUserInfo.voicepath))
		{
			vocieButton.setVisibility(View.VISIBLE);
			secondsTextView.setVisibility(View.VISIBLE);
			secondsTextView.setText(SoundHelper.getDuration(mCurrentUserInfo.voicepath) + "″");
			recordButton.setText(mResources.getString(R.string.voice_reset_record));
		}
		else
		{
			recordButton.setText(mResources.getString(R.string.voice_record));
		}
	}
	
	/**
	 * 显示语音对话框
	 */
	private void showDialogForRecord() {
		mRecordLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_pub_record, null);
		//初始化对话框中的控件，并添加监听事件
		mRecordCloseBtn = (ImageButton) mRecordLayout.findViewById(R.id.id_ib_pub_record_close);
		mRecordTextView = (TextView) mRecordLayout.findViewById(R.id.id_tv_pub_record);
		mRecordImageView = (ImageView) mRecordLayout.findViewById(R.id.id_iv_pub_record);
		mRecordCancelBtn = (Button) mRecordLayout.findViewById(R.id.id_btn_pub_record_cancel);
		mRecordSendBtn = (Button) mRecordLayout.findViewById(R.id.id_btn_pub_record_send);
		mRecordChronometer = (Chronometer)mRecordLayout.findViewById(R.id.id_cm_pub_record_timer);
		
		mRecordCloseBtn.setOnClickListener(mOnClickListener);
		mRecordImageView.setOnClickListener(mOnClickListener);
		mRecordCancelBtn.setOnClickListener(mOnClickListener);
		mRecordSendBtn.setOnClickListener(mOnClickListener);
		//创建对话框
		mRecordDialog = new PopupWindow(mRecordLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		//设置背景
		mRecordDialog.setBackgroundDrawable(mResources.getDrawable(R.color.translucent));
		//进场和退场动画
		mRecordDialog.setAnimationStyle(R.style.popupwindow_anim_style);
		//显示语音对话框
		mRecordStatus = RECORD_START;
		mRecordDialog.showAtLocation(mContext.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}
	
	/**
	 * 发送语音接口
	 * @author weigong
	 *
	 */
	private class SendVoiceTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				String voiceBytes = params[0];
				mDatas = DataServer.getInstance().setVoice(voiceBytes);
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
				// 保存语音路径
				if (mDatas.containsKey(Consts.VOICE_PATH)
					&& !StringUtils.isEmpty(mDatas.get(Consts.VOICE_PATH).toString()))
				{
					mCurrentUserInfo.voicepath = mDatas.get(Consts.VOICE_PATH).toString();
					LocationUtil.writeInit(mContext, Consts.VOICE_PATH, mCurrentUserInfo.voicepath);
					// 显示录音播放按钮
					vocieButton.setVisibility(View.VISIBLE);
					secondsTextView.setVisibility(View.VISIBLE);
					secondsTextView.setText(SoundHelper.getDuration(mCurrentUserInfo.voicepath) + "″");
					recordButton.setText(mResources.getString(R.string.voice_reset_record));
				}
				ToastUtils.show(mContext, mResources.getString(R.string.sending_voice_success_tips));
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
}
