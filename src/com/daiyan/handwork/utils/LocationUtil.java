package com.daiyan.handwork.utils;

import com.daiyan.handwork.constant.Consts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 读写本地数据
 * 
 * @author AA
 * @Date 2014-12-18
 */
public class LocationUtil {

	/**
	 * 把map类型的数据写入配置文件
	 * 
	 * @param context
	 * @param keys
	 * @param values
	 */
	public static void writeInit(Context context, String[] keys, String[] values) {

		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();

		for (int i = 0; i < keys.length; i++) {
			if (values[i] == null || values[i].equals(Consts.VALUE_NULL)) {
				values[i] = "";
			}
			edit.putString(keys[i], values[i]);
		}

		edit.commit();
	}

	/**
	 * 把String类型的数据写入配置文件
	 * 
	 * @param context
	 * @param keys
	 * @param values
	 */
	public static void writeInit(Context context, String key, String value) {

		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();

		edit.putString(key, value);
		edit.commit();
	}
	
	/**
	 * 把Boolean类型的数据写入配置文件
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void writeInit(Context context, String key, boolean value) {

		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();

		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * 读取配置文件
	 * 
	 * @param context
	 * @param key
	 * @param def
	 * @return
	 */
	public static String readInit(Context context, String key, String def) {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(context);
		String value = spf.getString(key, def);

		return value;
	}

	/**
	 * 读取配置文件
	 * 
	 * @param context
	 * @param key
	 * @param def
	 * @return
	 */
	public static boolean readInit(Context context, String key, boolean def) {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean value = spf.getBoolean(key, def);
		return value;
	}
}
