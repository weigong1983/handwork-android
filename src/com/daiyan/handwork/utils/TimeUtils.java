package com.daiyan.handwork.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

	/**
	 * 判断指定时间戳是否为今天
	 * @param timeStr
	 * @return
	 */
	public static boolean isToday(String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = sdf.format(new Date());
		String timeDate = sdf.format(new Date(Long.parseLong(timeStr)*1000L));;
		return nowDate.equals(timeDate);		
	}
	
	/**
	 * 将时间戳转为“距离现在时间多久之前”的字符串（精简版）
	 * 例如：今天 15:20
	 * @param timeStr 时间戳
	 * @return
	 */
	public static String getIntervalDate(String timeStr) {
		String result;
		long lTime = Long.parseLong(timeStr);
		
		//小时:分钟
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String hourAndMinu = sdf.format(new Date(lTime*1000L));
		//月-日
		sdf = new SimpleDateFormat("MM月dd日");
		String monthAndDay = sdf.format(new Date(lTime*1000L));

		if (isToday(timeStr)) {
			result = "今天 " + hourAndMinu;
		} else {
			result = monthAndDay;
		}
		return result;
	}
	
	/**
	 * 将时间戳转为“距离现在时间多久之前”的字符串（详细版）
	 * @param timeStr 时间戳
	 * @return
	 */
	public static String getIntervalTime(String timeStr) {
		String result;
		long lTime = Long.parseLong(timeStr);
		
		//小时:分钟
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String hourAndMinu = sdf.format(new Date(lTime*1000L));
		//月-日
		sdf = new SimpleDateFormat("MM月dd日");
		String monthAndDay = sdf.format(new Date(lTime*1000L));

		if (isToday(timeStr)) {
			result = "今天 " + hourAndMinu;
		} else {
			result = monthAndDay+hourAndMinu;
		}
		return result;
	}
	
	/**
	 * 将时间戳转为年月日格式 2015-01-01
	 * @param timeStr
	 * @return
	 */
	public static String getDate(String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(Long.parseLong(timeStr)*1000L));
	}
	
	/**
	 * 将时间戳转为年月日时分格式 2015-01-01 00:00
	 * @param timeStr
	 * @return
	 */
	public static String getDateTime(String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(Long.parseLong(timeStr)*1000L));
	}
	
	/**
	 * 获得当前时间（格式：MM-dd HH:mm)
	 * @return
	 */
	public static String getCurTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(new Date());
	}
	
	/**
	 * 将指定毫秒转成（小时：分钟）格式
	 * @param mill
	 */
	public static String getTime(int msec) {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		return sdf.format(new Date(msec));
	}

}
