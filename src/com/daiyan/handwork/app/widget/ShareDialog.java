package com.daiyan.handwork.app.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daiyan.handwork.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ShareDialog{		
	
	public static final String QQ = "QQ";
	public static final String QZONE = "QQ空间";
	public static final String SINA_WEIBO = "新浪微博";
	public static final String WECHAT = "微信好友";
	public static final String WECHAT_MOMENTS = "微信朋友圈";
	
	private AlertDialog dialog;
	private GridView gridView;
	private Button cancelButton;
	private SimpleAdapter saImageItems;
	private int[] image={R.drawable.logo_qq,R.drawable.logo_qzone,R.drawable.logo_sinaweibo,R.drawable.logo_wechat,R.drawable.logo_wechatmoments};
	private String[] name={QQ, QZONE, SINA_WEIBO, WECHAT, WECHAT_MOMENTS};
	

	public ShareDialog(Context context){
		
		dialog=new android.app.AlertDialog.Builder(context).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.share_dialog);
		gridView=(GridView) window.findViewById(R.id.share_gridView);
		cancelButton=(Button) window.findViewById(R.id.id_btn_share_cancel);
		List<HashMap<String, Object>> shareList=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<image.length;i++){
			HashMap<String, Object> map = new HashMap<String, Object>();  
	        map.put("ItemImage", image[i]);//添加图像资源的ID  
	        map.put("ItemText", name[i]);//按序号做ItemText  
	        shareList.add(map);
		}
		  
		saImageItems =new SimpleAdapter(context, shareList, R.layout.share_item, new String[] {"ItemImage","ItemText"}, new int[] {R.id.imageView1,R.id.textView1});
		gridView.setAdapter(saImageItems);
	}
	
	public void setCancelButtonOnClickListener(OnClickListener Listener){
		cancelButton.setOnClickListener(Listener);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		gridView.setOnItemClickListener(listener);
	}
			
	
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		dialog.dismiss();
	}
}