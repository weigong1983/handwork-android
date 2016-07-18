package com.daiyan.handwork.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.app.widget.BannerAdapter;
import com.daiyan.handwork.app.widget.ViewPagerImageScrollView;
import com.daiyan.handwork.bean.WorksCard;
import com.daiyan.handwork.bean.WorksInfo;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.common.ImageLoader;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.common.ImageLoader.Type;
import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.LocationUtil;
import com.daiyan.handwork.utils.StringUtils;
import com.daiyan.handwork.utils.ToastUtils;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 作品详情页
 * @author AA
 * @Date 2014-11-26
 */
public class WorksDetail extends BaseActivity implements OnClickListener{

	private Activity mContext;
	private Resources mResources;
	
	private ImageView mTitleLeftBtn;
	private TextView mTitleTextView;
	private TextView mTitleRightTextView;
	
	private final int EVT_AUTO_SCROLL_CHANGE_PIC = 100;
	private ViewPager bannerPager;
	private CirclePageIndicator mIndicator;
	private int currentItem = 0;
	private ArrayList<View> bannerViewlist;
	private BannerAdapter adapter;
	private ScheduledExecutorService scheduledExecutorService;
	private ViewPagerImageScrollView moveScrollView;

	// 操作按钮
	private Button productionProcessBtn; // 查看作品制作流程按钮
	private Button registerCardBtn; // 查看作品登记卡按钮
	private Button leaveMessageBtn; // 给研究院留言按钮
	private Button gotoSeeBtn; // 去研究院看看按钮
	
	private LinearLayout userinfoLinearLayout; // 作者信息栏
	private LinearLayout shareLinearLayout; // 分享
	private LinearLayout commentLinearLayout; // 评论
	private LinearLayout likeLinearLayout; // 赞
	
	private TextView worksNameTextView;
	private ImageView avatarImageView;
	private TextView nameTextView;
	private TextView titleTextView;
	private TextView descriptionTextView;
	private TextView commentCountTextView;
	private TextView likeCountTextView;
	private ImageView likeImageView;
	private TextView priceTextView;
	
	
	/** 图片加载类 */
	private ImageLoader mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	
	/** 调用接口返回数据 */
	private HashMap<String, Object> mDatas;
	
	private String mWorksId = "";// 作品ID
	
	private WorksInfo mWorksDetailInfo = new WorksInfo();
	
	private WorksCard mWorksCard = null;
	
	private boolean refreshFlag = false; // 点赞、评论之后返回作品列表需要刷新的标记
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_works_detail);
		mContext = WorksDetail.this;
		mResources = getResources();
		
		mWorksId = getIntent().getStringExtra(Consts.EXTRA_WORKS_ID);
		
		initView();
		
		String loading = mResources.getString(R.string.loading_data_tips);
		UIHelper.showDialogForLoading(mContext, loading, true);
		new GetWorksDetailTask().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (Consts.REQUEST_REFRESH_COMMENT_COUNT == requestCode)
		{
			// 刷新评论数目
			mWorksDetailInfo.commentCount = data.getIntExtra(Consts.EXTRA_REFRESH_COMMENT_COUNT, 0);
			commentCountTextView.setText(mWorksDetailInfo.commentCount + "");
		}
	}
	
	/**
	 * 初始化界面
	 */
	private void initView() {
		initTitleBar();
		initBannerViews();

		productionProcessBtn = (Button) findViewById(R.id.id_btn_production_process);
		productionProcessBtn.setOnClickListener(this);
		registerCardBtn = (Button) findViewById(R.id.id_btn_registration_card);
		registerCardBtn.setOnClickListener(this);
		leaveMessageBtn = (Button) findViewById(R.id.id_btn_leave_message);
		leaveMessageBtn.setOnClickListener(this);
		gotoSeeBtn = (Button) findViewById(R.id.id_btn_goto_see);
		gotoSeeBtn.setOnClickListener(this);
		
		userinfoLinearLayout= (LinearLayout) findViewById(R.id.userinfoLinearLayout);
		userinfoLinearLayout.setOnClickListener(this);
		shareLinearLayout = (LinearLayout) findViewById(R.id.shareLinearLayout);
		shareLinearLayout.setOnClickListener(this);
		commentLinearLayout = (LinearLayout) findViewById(R.id.commentLinearLayout);
		commentLinearLayout.setOnClickListener(this);
		likeLinearLayout = (LinearLayout) findViewById(R.id.likeLinearLayout);
		likeLinearLayout.setOnClickListener(this);
		
		worksNameTextView = (TextView) findViewById(R.id.worksNameTextView);
		avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		commentCountTextView = (TextView) findViewById(R.id.commentCountTextView);
		likeCountTextView = (TextView) findViewById(R.id.likeCountTextView);
		likeImageView = (ImageView) findViewById(R.id.likeImageView); 
		priceTextView = (TextView) findViewById(R.id.priceTextView);
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mTitleLeftBtn = (ImageView) findViewById(R.id.id_ib_title_left);
		mTitleLeftBtn.setImageResource(R.drawable.icon_back);
		mTitleLeftBtn.setOnClickListener(this);
		mTitleTextView = (TextView) findViewById(R.id.id_tv_title_center);
		mTitleTextView.setText(getResources().getString(R.string.works_detail_title));
		mTitleRightTextView = (TextView) findViewById(R.id.id_tv_title_right);
		mTitleRightTextView.setVisibility(View.GONE);
	}

	/**
	 * 初始化顶部作品大图广告栏视图控件
	 */
	private void initBannerViews() {
		moveScrollView = (ViewPagerImageScrollView) findViewById(R.id.topScrollView);
		moveScrollView.setHorizontalFadingEdgeEnabled(false);
		moveScrollView.setEnabled(false);
		
		bannerPager = (ViewPager) findViewById(R.id.invest_banner);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
	}

	/**
	 * 初始化顶部作品大图广告栏数据
	 */
	private void initBannerData() {
		
		if (mWorksDetailInfo.m_workimgs == null || mWorksDetailInfo.m_workimgs.size() == 0)
			return ;
		
		bannerViewlist = new ArrayList<View>();
		final int length = mWorksDetailInfo.m_workimgs.size();
		for (int i = 0; i < length; i++) {
			View view = getLayoutInflater().inflate(R.layout.activity_works_detail_top_pic_item, null);
			ImageView worksPicImageView = (ImageView) view.findViewById(R.id.worksPicImageView);
			//worksPicImageView.setImageResource(worksPisList[i]);
			final String imageUrl = mWorksDetailInfo.m_workimgs.get(i);
			mImageLoader.loadImage(imageUrl, worksPicImageView, true);
			
			// 设置图片点击事件
			worksPicImageView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view) {
					// 全屏浏览大图
					String[] imageList = new String[length];
					for (int index=0; index<length; index++)
						imageList[index] = mWorksDetailInfo.workimgs.get(index);
					UIHelper.showImagePager(mContext, imageList, currentItem);
				}
				
			});
			
			bannerViewlist.add(view);
		}
		
		if (bannerViewlist.size() == 0)
		{
			return ;
		}
		
		moveScrollView.setEnabled(true);
		adapter = new BannerAdapter(bannerViewlist);
		bannerPager.setAdapter(adapter);
		mIndicator.setViewPager(bannerPager);
		mIndicator.setOnPageChangeListener(new MyPageChangeListener());
		
		// 设置初始图
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.works_detail_icon);
		int item = bannerPager.getCurrentItem();
		View view = bannerViewlist.get(item);
		ImageView img = (ImageView) view.findViewById(R.id.worksPicImageView);
		moveScrollView.setImageBitMap(bmp, img, bannerPager);
	}

	private class MyPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			currentItem = position;
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK 
			&& event.getAction() == KeyEvent.ACTION_UP) 
		{
			onBack();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	private void onBack()
	{
		Intent intent=new Intent();
        intent.putExtra(Consts.EXTRA_REFRESH_WORKS_LIST, refreshFlag);
        setResult(Consts.REQUEST_REFRESH_WORKS_LIST, intent);
		finish();
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ib_title_left:
			onBack();
			break;
		case R.id.id_btn_production_process: // 查看工艺制作流程
			UIHelper.showWebView(mContext, mWorksDetailInfo.madeflow, Consts.WEBVIEW_PAGE_PRODUCTION_PROCESS);
			break;
		case R.id.id_btn_registration_card: // 查看作品登记卡
			UIHelper.showWorksRegistrationCard(mContext, mWorksCard);
			break;
		case R.id.id_btn_leave_message: // 给研究院留言
			// 游客检测
//			if (Consts.IS_GUEST(mContext))
//			{
//				UIHelper.showDialogForRegister(mContext);
//				return ;
//			}
						
			UIHelper.showSendMessage(mContext, mWorksId);
			break;
			
		case R.id.id_btn_goto_see: // 去研究院看看
			UIHelper.showInstitute(mContext);
			break;
		case R.id.userinfoLinearLayout: // 进入作者主页
			UIHelper.showHomepage(mContext, mWorksDetailInfo.uid);
			break;
		case R.id.shareLinearLayout: // 分享
		{
			// 游客检测
//			if (Consts.IS_GUEST(mContext))
//			{
//				UIHelper.showDialogForRegister(mContext);
//				return ;
//			}
			
			String shareTitle = mWorksDetailInfo.worksName + " - " + mWorksDetailInfo.authorName;
			String shareContent = shareTitle;
			
			String []imageArray = null;
			if (mWorksDetailInfo.workimgs != null 
				&& mWorksDetailInfo.workimgs.size() > 0)
			{
				int imgCount = mWorksDetailInfo.workimgs.size();
				imageArray = new String[imgCount];
				for (int index=0; index<imgCount; index++)
				{
					imageArray[index] = mWorksDetailInfo.workimgs.get(index);
				}
			}
			String shareUrl = mWorksDetailInfo.detailUrl;
			UIHelper.showShare(mContext, shareTitle, shareUrl, shareContent, imageArray, 
					23.103969f, 113.375496f);
			break;
		}
		case R.id.commentLinearLayout: // 评论
			// 游客检测
//			if (Consts.IS_GUEST(mContext))
//			{
//				UIHelper.showDialogForRegister(mContext);
//				return ;
//			}
			UIHelper.showWorksCommunication(mContext, mWorksId, Consts.FRAGMENT_COMMENT);
 			break;
		case R.id.likeLinearLayout: // 赞
			// 游客检测
//			if (Consts.IS_GUEST(mContext))
//			{
//				UIHelper.showDialogForRegister(mContext);
//				return ;
//			}
			
			new LikeTask().execute();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取作品详情数据
	 * @author weigong
	 *
	 */
	private class GetWorksDetailTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//Thread.sleep(2000);
				mDatas = DataServer.getInstance().getWorksDetail(mWorksId);
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
				setWorksDataFromNet();
			} else {
				ToastUtils.show(mContext, Consts.NET_WORK_ERROR);
			}
		}
	}
	
	/**
	 * 解析并显示调用接口返回数据
	 */
	private void setWorksDataFromNet() 
	{
		try {
			if (mDatas.containsKey(Consts.WORKS_DETAIL)
					&& mDatas.get(Consts.WORKS_DETAIL) != null) 
			{
				HashMap<String, Object> detailMap = JsonUtils.getJsonValues(mDatas.get(Consts.WORKS_DETAIL).toString());
				mWorksDetailInfo.id = detailMap.get(Consts.WORKS_ID).toString();
				mWorksDetailInfo.worksName = detailMap.get(Consts.WORKS_NAME).toString();
				mWorksDetailInfo.worksPicUrl = detailMap.get(Consts.WORKS_IMAGE).toString();
				mWorksDetailInfo.commentCount = Integer.parseInt(detailMap.get(Consts.WORKS_COMMENT_COUNT).toString());
				mWorksDetailInfo.likeCount = Integer.parseInt(detailMap.get(Consts.WORKS_LIKE_COUNT).toString());
				mWorksDetailInfo.description = detailMap.get(Consts.WORKS_DESCRIPTION).toString();
				
				mWorksDetailInfo.issale = Integer.parseInt(detailMap.get(Consts.WORKS_IS_SALE).toString());
				mWorksDetailInfo.price = "￥" + detailMap.get(Consts.WORKS_PRICE).toString();
				mWorksDetailInfo.marktype = Integer.parseInt(detailMap.get(Consts.WORKS_MARK_TYPE).toString());
			}
			
			// 作品详情页URL，用于分享
			if (mDatas.containsKey(Consts.WORRKS_DETAIL_URL))
				mWorksDetailInfo.detailUrl = mDatas.get(Consts.WORRKS_DETAIL_URL).toString();
			
			// 我是否赞过
			mWorksDetailInfo.like = (Integer.parseInt(mDatas.get(Consts.WORKS_IS_LIKE).toString()) == 1 ? true : false);
			
			// 作品制作流程网址
			mWorksDetailInfo.madeflow = mDatas.get(Consts.WORKS_MADE_FLOW).toString();
			
			// 作者资料
			if (mDatas.containsKey(Consts.WORKS_AUTHOR)
				&& !mDatas.get(Consts.WORKS_AUTHOR).toString().equals(Consts.VALUE_NULL)) 
			{
				HashMap<String, Object> authorMap = JsonUtils.getJsonValues(mDatas.get(Consts.WORKS_AUTHOR).toString());
				if (authorMap != null) 
				{
					mWorksDetailInfo.uid = authorMap.get(Consts.UID).toString();
					mWorksDetailInfo.avatarUrl = authorMap.get(Consts.PHOTO).toString();
					if (!StringUtils.isEmpty(authorMap.get(Consts.NICKNAME).toString()))
						mWorksDetailInfo.authorName = authorMap.get(Consts.NICKNAME).toString();
					if (!StringUtils.isEmpty(authorMap.get(Consts.CALLNAME).toString()))
						mWorksDetailInfo.callname = authorMap.get(Consts.CALLNAME).toString();
				}
			}

			// public List<String> workimgs; // 作品图片
			if (mDatas.containsKey(Consts.WORKS_IMGS)
				&& !mDatas.get(Consts.WORKS_IMGS).toString().equals(Consts.VALUE_NULL)) 
			{
				List<HashMap<String, Object>> workimgsMapList = JsonUtils.getJsonValuesInArray(mDatas.get(Consts.WORKS_IMGS).toString());
				if (workimgsMapList != null && workimgsMapList.size() > 0) {
					mWorksDetailInfo.workimgs.clear();
					mWorksDetailInfo.m_workimgs.clear();
					
					for (int i = 0; i < workimgsMapList.size(); i++) 
					{
						String mImageUrl;
						HashMap<String, Object> itemMap = workimgsMapList.get(i);
						mImageUrl = itemMap.get(Consts.WORKS_IMAGE_M).toString();
						if (mImageUrl != null && !mImageUrl.isEmpty())
							mWorksDetailInfo.m_workimgs.add(mImageUrl);
						
						String imageUrl;
						imageUrl = itemMap.get(Consts.WORKS_IMAGE).toString();
						if (imageUrl != null && !imageUrl.isEmpty())
							mWorksDetailInfo.workimgs.add(imageUrl);
						
					}
				}
			}
			
			if (mDatas.containsKey(Consts.WORKS_CARD)
				&& !mDatas.get(Consts.WORKS_CARD).toString().equals(Consts.VALUE_NULL)) 
			{
				mWorksCard = new WorksCard();
				// 作品卡
				HashMap<String, Object> workcardrMap = JsonUtils
						.getJsonValues(mDatas.get("workcard").toString());
				mWorksCard.workname = workcardrMap.get("workname").toString();
				mWorksCard.mademan = workcardrMap.get("mademan").toString();
				mWorksCard.size = workcardrMap.get("size").toString();
				mWorksCard.madeplace = workcardrMap.get("madeplace").toString();
				mWorksCard.material = workcardrMap.get("material").toString();
				mWorksCard.jobtitle = workcardrMap.get("jobtitle").toString();
			
				// 是否主创
				if (!StringUtils.isEmpty(workcardrMap.get("createman").toString()))
					mWorksCard.createman = Integer.parseInt(workcardrMap.get("createman").toString());
				else
					mWorksCard.createman = 0;
				
				// 是否监制
				if (!StringUtils.isEmpty(workcardrMap.get("producer").toString()))
					mWorksCard.producer = Integer.parseInt(workcardrMap.get("producer").toString());
				else
					mWorksCard.producer = 0;
				
				mWorksCard.manufacture = workcardrMap.get("manufacture").toString();
				mWorksCard.productiontime = workcardrMap.get("productiontime").toString();
				
				
				//mWorksCard.referenceprice = workcardrMap.get("referenceprice").toString();
				// 根据出售状态显示参考价格：1：非卖品； 2：出售中； 3：已售
				if (mWorksDetailInfo.issale == 1)
				{
					mWorksCard.referenceprice = "非卖品";
				}
				else if (mWorksDetailInfo.issale == 2) 
				{
					if (mWorksDetailInfo.marktype == 1)
					{
						mWorksCard.referenceprice = mWorksDetailInfo.price;
					}
					else
					{
						mWorksCard.referenceprice = getResources().getString(R.string.bargain_price);
					}
				}
				else if (mWorksDetailInfo.issale == 3)
				{
					mWorksCard.referenceprice = "已售";
				}

				mWorksCard.customtime = workcardrMap.get("customtime").toString();
				mWorksCard.opusnumber = workcardrMap.get("opusnumber").toString();
				
				// 是否量产
				if (!StringUtils.isEmpty(workcardrMap.get("limited").toString()))
					mWorksCard.limited = Integer.parseInt(workcardrMap.get("limited").toString());
				else
					mWorksCard.limited = 1;
				
				mWorksCard.packing = workcardrMap.get("packing").toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}

		// 刷新顶部图片
		initBannerData();

		worksNameTextView.setText(mWorksDetailInfo.worksName);
		// 设置个人头像
		if (!mWorksDetailInfo.avatarUrl.isEmpty())
			mImageLoader.loadImage(mWorksDetailInfo.avatarUrl, avatarImageView,true);
		nameTextView.setText(mWorksDetailInfo.authorName);
		titleTextView.setText(mWorksDetailInfo.callname);
		descriptionTextView.setText(mWorksDetailInfo.description);
		commentCountTextView.setText(mWorksDetailInfo.commentCount + "");
		likeCountTextView.setText(mWorksDetailInfo.likeCount + "");
		
		if (mWorksDetailInfo.like)
		{
			likeImageView.setImageResource(R.drawable.icon_detail_like_focus);
		}
		else
		{
			likeImageView.setImageResource(R.drawable.icon_detail_like);
		}
		
		// 出售状态：1：非卖品； 2：出售中； 3：已售
		priceTextView.setVisibility(View.VISIBLE);
		if (mWorksDetailInfo.issale == 1)
		{
			priceTextView.setText("非卖品");
		}
		else if (mWorksDetailInfo.issale == 2) 
		{
			if (mWorksDetailInfo.marktype == 1)
			{
				priceTextView.setText(mWorksDetailInfo.price);
			}
			else
			{
				priceTextView.setText(getResources().getString(R.string.bargain_price));
			}
		}
		else if (mWorksDetailInfo.issale == 3)
		{
			priceTextView.setText("已售");
		}
		
	}
	
	/**
	 * 点赞
	 */
	private class LikeTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if (!mWorksDetailInfo.like)
				{
					mDatas = DataServer.getInstance().like(mWorksDetailInfo.id);
				}
				else
				{
					mDatas = DataServer.getInstance().cancelLike(mWorksDetailInfo.id);
				}
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			
			refreshFlag = true;
			
			mWorksDetailInfo.like = !mWorksDetailInfo.like;
			if (isSuccess)
			{
				if (mWorksDetailInfo.like)
				{
					mWorksDetailInfo.likeCount++;
					likeImageView.setImageResource(R.drawable.icon_detail_like_focus);
					likeCountTextView.setText(mWorksDetailInfo.likeCount + "");
				}
				else
				{
					mWorksDetailInfo.likeCount--;
					likeImageView.setImageResource(R.drawable.icon_detail_like);
					likeCountTextView.setText(mWorksDetailInfo.likeCount + "");
				}
			}
		}
	}
}