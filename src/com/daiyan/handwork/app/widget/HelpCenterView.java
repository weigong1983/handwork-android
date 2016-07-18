package com.daiyan.handwork.app.widget;

import java.util.ArrayList;
import java.util.List;
import com.daiyan.handwork.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 帮助中心（ExpandableListView）
 * @author AA
 * @Date 2014-11-27
 */
public class HelpCenterView extends ExpandableListView{

	private Context mContext;
	private List<String> mGroups;
	private List<String> mChilds;
	
	private HelpCenterAdapter mAdapter;
	
	
	public HelpCenterView(Context context) {
		super(context);
		
		mContext = context;
		mGroups = getGroupData();
		mChilds = getChildData();
		
		// 隐藏滚动条
		this.setVerticalScrollBarEnabled(false);
		// 隐藏左边默认箭头
		this.setGroupIndicator(null);
		
		setCacheColorHint(Color.TRANSPARENT);
		setDividerHeight(0);
		setChildrenDrawnWithCacheEnabled(false);
		setGroupIndicator(null);
		
		// 隐藏选择的黄色高亮
		ColorDrawable drawable_tranparent_ = new ColorDrawable(Color.TRANSPARENT);
		setSelector(drawable_tranparent_);
		
		mAdapter = new HelpCenterAdapter();
		setAdapter(mAdapter);
	}
	
	public HelpCenterView(Context context, AttributeSet attrs) {
		this(context);
	}
	
	
	/**
	 * 获得Group数据
	 * @return
	 */
	private List<String> getGroupData() {
		String[] groups = mContext.getResources().getStringArray(R.array.help_center_group);
		List<String> list = new ArrayList<String>();
		for(int i=0; i<groups.length; i++) {
			list.add(groups[i]);
		}
		return list;
	}
	
	/**
	 * 获得Child数据
	 * @return
	 */
	private List<String> getChildData() {
		String[] childs = mContext.getResources().getStringArray(R.array.help_center_child);
		List<String> list = new ArrayList<String>();
		for(int i=0; i<childs.length; i++) {
			list.add(childs[i]);
		}
		return list;
	}
	
	
	public class HelpCenterAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return mGroups.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			//Child下只显示一条
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroups.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChilds.get(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_help_center_group, null);
			}
			
			TextView groupText = (TextView)convertView.findViewById(R.id.id_tv_item_help_center_group);
			ImageView groupImg = (ImageView)convertView.findViewById(R.id.id_iv_item_help_center_group);
			
			//Group展开时
			if(isExpanded) {
				groupText.setTextColor(ColorStateList.valueOf(Color.parseColor("#ff5e4c")));
				groupImg.setImageResource(R.drawable.icon_more_down);
			//Group未展开时
			} else {
				groupText.setTextColor(ColorStateList.valueOf(Color.parseColor("#555555")));
				groupImg.setImageResource(R.drawable.icon_main_more);
			}
			//设置Group内容
			groupText.setText(mGroups.get(groupPosition));
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_help_center_child, null);
			}
			TextView childText = (TextView)convertView.findViewById(R.id.id_tv_item_help_center_child);
			childText.setText(mChilds.get(groupPosition));
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
