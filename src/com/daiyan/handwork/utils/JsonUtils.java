package com.daiyan.handwork.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.daiyan.handwork.constant.Consts;

/**
 * Json处理类
 * 
 * @author AA
 * @Date 2014-12-22
 */
public class JsonUtils {

	public static final int IS_JSONARRAY = 0;
	public static final int IS_JSONOBJECT = 1;

	/**
	 * 判断一个字符串能否转换为JSON数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static int isJsonArray(String jsonStr) {
		int result = -1;
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			if (jsonArray != null) {
				result = IS_JSONARRAY;
			}
		} catch (JSONException e) {
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);
				if (jsonObject != null) {
					result = IS_JSONOBJECT;
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 字符串转JSON
	 * 
	 * @param jsonString
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, Object> getJsonValues(String jsonString)
			throws Exception {
		JSONObject jsonObject = new JSONObject(jsonString);
		HashMap<String, Object> map = new HashMap<String, Object>();
		Iterator<?> it = jsonObject.keys();

		while (it.hasNext()) {
			String ikey = (String) it.next().toString();
			String value = (String) jsonObject.get(ikey).toString();
			map.put(ikey, value);
		}

		return map;
	}

	/**
	 * 从JSON数组中获得JSON
	 * 
	 * @param jsonString
	 * @return
	 * @throws Exception
	 */
	public static List<HashMap<String, Object>> getJsonValuesInArray(
			String jsonString) {

		List<HashMap<String, Object>> array = new ArrayList<HashMap<String, Object>>();

		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = ((JSONObject) jsonArray.opt(i));

				HashMap<String, Object> map = new HashMap<String, Object>();

				Iterator<?> it = jsonObject.keys();
				while (it.hasNext()) {
					String value = "";
					String ikey = (String) it.next().toString();
					if (jsonObject.get(ikey).toString().equals(Consts.VALUE_NULL)) {
						value = "";
					} else {
						value = (String) jsonObject.get(ikey).toString();
					}

					map.put(ikey, value);
				}

				array.add(map);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array;
	}

	/**
	 * 从JSON中获取图片地址
	 * 
	 * @param datas
	 * @param key
	 * @param index
	 * @return
	 */
	public static String getImageUrlFromJson(
			List<HashMap<String, Object>> datas, String key, int index) {
		String result = "";
		try {
			HashMap<String, Object> map = JsonUtils.getJsonValues(datas
					.get(index).get(Consts.IMAGES).toString());
			result = (String) map.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 从JSON数组中获取第一张图片地址
	 * 
	 * @param datas
	 * @param key
	 * @param index
	 * @return
	 */
	public static String getFirstImageUrlFromJsonArray(
			List<HashMap<String, Object>> datas, String key, int index) {
		String result = "";
		try {
			List<HashMap<String, Object>> map = JsonUtils
					.getJsonValuesInArray(datas.get(index).get(Consts.IMAGES)
							.toString());
			result = (String) map.get(0).get(Consts.IMAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 从JSON数组中获取所有图片地址
	 * 
	 * @param datas
	 * @param key
	 * @param index
	 * @return
	 */
	public static List<String> getAllImageUrlFromJsonArray(
			List<HashMap<String, Object>> datas, String key, int index) {
		List<String> list = new ArrayList<String>();
		try {
			List<HashMap<String, Object>> map = JsonUtils
					.getJsonValuesInArray(datas.get(index).get(Consts.IMAGES)
							.toString());
			for (int i = 0; i < map.size(); i++) {
				list.add((String) map.get(i).get(Consts.IMAGE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
