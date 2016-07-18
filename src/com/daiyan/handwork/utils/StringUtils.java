package com.daiyan.handwork.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.daiyan.handwork.constant.Consts;

/**
 * 字符串处理类
 * @author AA
 * @date 2014-10-20
 *
 */
public class StringUtils {

	/**
	 * 如果为null，返回空字符串
	 * @param src
	 * @return
	 */
	public static String checkNull(String src) {
		return src == null ? "" : src;
	}
	
	/**
	 * 检查字符串的有效性
	 * @param src
	 * @return
	 */
	public static String checkValid(String src) {
		boolean valid = src == null || src.equals("") || src.equals(Consts.VALUE_NULL);
		return valid ? "" : src;
	}
	
	/**
	 * 如果为null或为空，返回字符串："0"
	 * @param src
	 * @return
	 */
	public static String emptyConvertZero(String src) {
		return isEmpty(src) ? "0" : src;
	}
	
	
	/**
	 * 获得当前时间 格式：MM--dd HH:mm
	 * @return
	 */
	public static String getCurTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
		return sdf.format(new Date());
	}
	
	
	/**
	 * 是否不为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return s != null && !"".equals(s.trim());
	}

	
	/**
	 * 是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim()) || "null".equals(s.trim());
	}
	
	/**
	 * 比较两个字符串是否相同
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isEqual(String str1, String str2) {
		return str1.trim().equals(str2.trim());
	}

	
	/**
	 * 通过{n},格式化.
	 * 
	 * @param src
	 * @param objects
	 * @return
	 */
	public static String format(String src, Object... objects) {
		int k = 0;
		for (Object obj : objects) {
			src = src.replace("{" + k + "}", obj.toString());
			k++;
		}
		return src;
	}
	
	/**
	 * 字符串数组转成List<HashMap<String, String>>
	 * @param key
	 * @param array
	 * @return
	 */
	public static  List<HashMap<String, String>> ArrayToList(String key, String[] array) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		for(int i=0; i<array.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(key, array[i]);
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 将长时间格式转换为指定格式的时间字符串（yyyy:MM:dd HH:mm）
	 * @param date
	 * @param simpleDateFormat
	 * @return
	 */
	public static String getStringDateByMillis(long date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return simpleDateFormat.format(date);
	}
		
	/**
	 * 转义字符串
	 * @param src
	 * @param charset
	 * @return
	 */
	public static String URLDecoder(String src, String charset) {
		String result = "";
		try {
			result = URLDecoder.decode(src, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 将<br />字符转成换行符
	 * @param src
	 * @return
	 */
	public static String transBrToLineBreak(String src) {
		return src.replaceAll("<br />", "\r\n");
	}
	
	/**
	 * 将&nbsp;字符转成空格
	 * @param src
	 * @return
	 */
	public static String transNbspToSpace(String src) {
		return src.replaceAll("&nbsp;", " ");
	}
	
	
	/**
	 * 是否手机号(11位数字即可)
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNumber(String mobileString)
	{
//		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
//		Matcher m = p.matcher(mobileString);  
//		return m.matches();  
		// 11位数字
		Pattern pattern = Pattern.compile("[0-9]*");   
		Matcher isNum = pattern.matcher(mobileString); 
		if (!isEmpty(mobileString) && mobileString.length() == 11 && isNum.matches())
			return true;
		return false;
	}
	
	/**
	 * 转换金额字符串
	 * @param price
	 * @return
	 */
	public static String convertPriceString(String price)
	{
		String convertString = "";
		double priceValue = Double.parseDouble(price);
		if (priceValue < 10000f)
		{
			convertString = "￥" + price;
		}
		else
		{
			convertString = priceValue/10000f + "万";
		}
		
		return convertString;
	}
}
