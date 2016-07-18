package com.daiyan.handwork.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

import com.daiyan.handwork.common.server.DataServer;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;
import com.daiyan.handwork.utils.ToastUtils;


/**
 * 管理工艺品类
 * 
 * @author AA
 * @date 2014-10-23
 *
 */
public class CategoryManager {

	private HashMap<String, Object> mDatas;
	
	private static HashMap<String, String> categoryMapId2Name = new HashMap<String, String>();
	private static HashMap<String, String> categoryMapName2Id = new HashMap<String, String>();
	private static String[] categoryNameArray;
	private static String[] categoryIdArray;
	
	private static CategoryManager instance;

	private CategoryManager() {
	}

	/**
	 * 获得单一实例
	 * 
	 * @return
	 */
	public static CategoryManager getInstance() {
		if (instance == null) {
			instance = new CategoryManager();
		}
		return instance;
	}

	public void setData(List<HashMap<String, Object>> categoryListData) {
		
		if (categoryListData == null || categoryListData.size() == 0)
			return ;
		
		categoryMapId2Name.clear();
		categoryMapName2Id.clear();
		
		int categoryCount = categoryListData.size();
		
		categoryNameArray = new String[categoryCount];
		categoryIdArray = new String[categoryCount];

		int len = categoryListData.size();
		for (int i = 0; i < len; i++) {
			HashMap<String, Object> mapItem = categoryListData.get(i);
			String classid = mapItem.get(Consts.CLASSID).toString();
			String classname = mapItem.get(Consts.CLASSNAME).toString();
			categoryMapId2Name.put(classid, classname);
			categoryMapName2Id.put(classname, classid);
			
			categoryIdArray[i] = classid;
			categoryNameArray[i] = classname;
		}
	}

	public String getClassNameById(String classid) {
		return categoryMapId2Name.get(classid);
	}

	public String getClassIdByName(String classname) {
		return categoryMapName2Id.get(classname);
	}

	/**
	 * 返回工艺品类名称数组
	 * @return
	 */
	public String[] getClassNameArray() {
		return categoryNameArray;
	}
	
	/**
	 * 返回工艺品类ID数组
	 * @return
	 */
	public String[] getClassIdArray() {
		return categoryIdArray;
	}
	
	/*
	 * 如果工艺品类数据未加载，后台从服务器读取
	 */
	public void loadCategoryData()
	{
		if (categoryMapId2Name.size() == 0
			|| categoryMapName2Id.size() == 0
			|| categoryNameArray == null || categoryNameArray.length == 0
			|| categoryIdArray == null || categoryIdArray.length == 0)
		{
			new GetCategoryTask().execute();
		}
	}
	
	/**
	 * 读取所有作品分类
	 * @author AA
	 *
	 */
	private class GetCategoryTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				mDatas = DataServer.getInstance().getAllClasses();
				return mDatas != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			if(isSuccess) 
			{
				if (mDatas.get(Consts.CLASSES) != null && !mDatas.get(Consts.CLASSES).toString().isEmpty())
				{
					List<HashMap<String, Object>> categoryListData = JsonUtils.getJsonValuesInArray(mDatas.get(Consts.CLASSES).toString());
					setData(categoryListData);
				}
			}
		}
	}
}
