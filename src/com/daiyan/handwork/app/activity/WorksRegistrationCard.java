package com.daiyan.handwork.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daiyan.handwork.R;
import com.daiyan.handwork.app.BaseActivity;
import com.daiyan.handwork.bean.WorksCard;
import com.daiyan.handwork.common.AppManager;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.StringUtils;

/**
 * 作品登记卡
 * @author 魏工
 * @Date 2015-05-08
 */
public class WorksRegistrationCard extends BaseActivity implements OnClickListener{

	private LinearLayout cardLinearLayout;
	private WorksCard mWorksCard;
	
	private TextView workNameTextView; // 作品名称
	private TextView materialTextView; // 材料
	private TextView sizeTextView; // 尺寸
	private TextView madeplaceTextView; // 出品地
	private TextView mademanTextView; // 出品人
	private TextView jobtitleTextView; // 职称
	
	private CheckBox createmanCheckBox; // 主创
	private CheckBox producerCheckBox; // 监制
	
	private TextView manufactureTextView; // 制作工艺
	private TextView productiontimeTextView; // 制作工时
	private TextView referencepriceTextView; // 参考价格
	private TextView customtimeTextView; // 订制时间
	private TextView opusnumberTextView; // 作品编号
	
	private RadioGroup worksQualityRadioGroup; // 作品质量单选组合框
	private CheckBox originalCheckBox; // 原作
	private CheckBox limitCheckBox; // 限量
	private CheckBox massCheckBox; // 量产
   
	private TextView packingTextView; // 包装
	
	private ScrollView cardScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_works_registration_card);
		//入栈
		AppManager.getInstance().pushActivity(this);

		mWorksCard = (WorksCard) getIntent().getSerializableExtra(Consts.EXTRA_WORKS_CARD);  
		
		initView();
		
		updateView();
		
		overridePendingTransition(R.anim.zoom_enter, R.anim.empty);
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		
		cardScrollView = (ScrollView) findViewById(R.id.cardScrollView);
		cardScrollView.setAlpha(0.95f);
		
		cardLinearLayout = (LinearLayout) findViewById(R.id.cardLinearLayout);
		cardLinearLayout.setOnClickListener(this);
		
		
		workNameTextView = (TextView) findViewById(R.id.workNameTextView);
		materialTextView = (TextView) findViewById(R.id.materialTextView);
		sizeTextView = (TextView) findViewById(R.id.sizeTextView);
		madeplaceTextView = (TextView) findViewById(R.id.madeplaceTextView);
		mademanTextView = (TextView) findViewById(R.id.mademanTextView);
		jobtitleTextView = (TextView) findViewById(R.id.jobtitleTextView);
		
		createmanCheckBox = (CheckBox) findViewById(R.id.createmanCheckBox);
		producerCheckBox = (CheckBox) findViewById(R.id.producerCheckBox);
		
		manufactureTextView = (TextView) findViewById(R.id.manufactureTextView);
		productiontimeTextView = (TextView) findViewById(R.id.productiontimeTextView);
		referencepriceTextView = (TextView) findViewById(R.id.referencepriceTextView);
		customtimeTextView = (TextView) findViewById(R.id.customtimeTextView);
		opusnumberTextView = (TextView) findViewById(R.id.opusnumberTextView);
		
		worksQualityRadioGroup = (RadioGroup) findViewById(R.id.worksQualityRadioGroup);
		originalCheckBox = (CheckBox) findViewById(R.id.originalCheckBox);
		limitCheckBox = (CheckBox) findViewById(R.id.limitCheckBox);
		massCheckBox = (CheckBox) findViewById(R.id.massCheckBox);
		
		packingTextView = (TextView) findViewById(R.id.packingTextView);
	}

	private void updateView()
	{
		if (mWorksCard == null)
			return ;
		
		// 作品名称
		if (!StringUtils.isEmpty(mWorksCard.workname))
			workNameTextView.setText(mWorksCard.workname);
		
		// 材料
		if (!StringUtils.isEmpty(mWorksCard.material))
			materialTextView.setText(mWorksCard.material);
		
		// 尺寸
		if (!StringUtils.isEmpty(mWorksCard.size))
			sizeTextView.setText(mWorksCard.size);
		
		// 出品人
		if (!StringUtils.isEmpty(mWorksCard.madeplace))
			madeplaceTextView.setText(mWorksCard.madeplace);
		
		// 出品人
		if (!StringUtils.isEmpty(mWorksCard.mademan))
			mademanTextView.setText(mWorksCard.mademan);
		
		// 职称
		if (!StringUtils.isEmpty(mWorksCard.jobtitle))
			jobtitleTextView.setText(mWorksCard.jobtitle);
		
		// 是否主创
		 createmanCheckBox.setChecked(mWorksCard.createman == 1);;
		 // 是否监制
		 producerCheckBox.setChecked(mWorksCard.producer == 1);

		// 制作工艺
		if (!StringUtils.isEmpty(mWorksCard.manufacture))
			manufactureTextView.setText(mWorksCard.manufacture);
		
		// 制作工时
		if (!StringUtils.isEmpty(mWorksCard.productiontime))
			productiontimeTextView.setText(mWorksCard.productiontime);
				
		// 参考价格
		if (!StringUtils.isEmpty(mWorksCard.referenceprice))
			referencepriceTextView.setText(mWorksCard.referenceprice);

		// 订制时间
		if (!StringUtils.isEmpty(mWorksCard.customtime))
			customtimeTextView.setText(mWorksCard.customtime);
		
		// 作品编号
		if (!StringUtils.isEmpty(mWorksCard.opusnumber))
			opusnumberTextView.setText(mWorksCard.opusnumber);
		
		// 作品质量
		if (mWorksCard.limited == 1)
			originalCheckBox.setChecked(true);
		else if (mWorksCard.limited == 2)
			limitCheckBox.setChecked(true);
		else if (mWorksCard.limited == 3)
			massCheckBox.setChecked(true);
		
		// 包装
		if (!StringUtils.isEmpty(mWorksCard.packing))
			packingTextView.setText(mWorksCard.packing);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cardLinearLayout:
			finish();
			overridePendingTransition(R.anim.empty, R.anim.zoom_exit);
			break;
		}
		
	}
	
}