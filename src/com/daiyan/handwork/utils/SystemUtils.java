package com.daiyan.handwork.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

public class SystemUtils {

	
	/**
	 * 获得屏幕宽度
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	
	/**
	 * 获得屏幕高度
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	
	/**
	 * 获取版本号(例如：1.0.0)
	 * @param context
	 * @return
	 */
	public static String getVersion(Context context)
	{
		try {
			PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return "V" + pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "未知版本";
		}
	}
	
	/**
	 * 获取版本号(内部识别号，例如：100)
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context)
	{
		try {
			PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 隐藏输入法键盘
	 * @param context
	 */
	public static void hideSoftInputFromWindow(Activity context) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
	}
	
	/**
	 * 验证电话号码或者手机号码
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {		
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|"
				+ "(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|"
				+ "(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	
	/**
	 * 验证邮箱地址格式是否正确
	 * @param email
	 * @return
	 */
	public static boolean isEmailValid(String email) {
		  Pattern pattern = Pattern.compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
		  Matcher matcher = pattern.matcher(email);
		  return matcher.matches();
	}
	
	/**
	 * 拨打电话
	 * @param context
	 * @param num
	 */
	public static void callPhone(Context context, String num) {
		if(isPhoneNumberValid(num)) {
			//点击数字后跳转到拨号盘
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + num));
			context.startActivity(intent);
		}
	}
	
	/**
	 * 发送短信
	 * 
	 * @param context
	 * @param num
	 */
	public static void sendSms(Context context, String content) {
		// 点击数字后跳转到拨号盘
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("sms_body", content);
		intent.setType("vnd.android-dir/mms-sms");
		context.startActivity(intent);
	}
	
	/**
	 * 发送邮件
	 * @param context
	 * @param mailAddress
	 */
	public static boolean sendEmail(Context context, String mailAddress) {
		if(isEmailValid(mailAddress)) {
			// 检查系统中是否安装了邮件程序
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mailAddress));
			PackageManager pm = context.getPackageManager();  
			ComponentName cn = intent.resolveActivity(pm);  
			if (cn != null) 
			{
				context.startActivity(intent);
				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 显示地图
	 * 
	 * @param context
	 * @param num
	 */
	public static boolean viewMap(Context context, double lalatitude, double longitute) 
	{
		// 检查系统中是否安装了邮件程序
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + lalatitude + ", " + longitute));
		PackageManager pm = context.getPackageManager();
		ComponentName cn = intent.resolveActivity(pm);
		if (cn != null) 
		{
			context.startActivity(intent);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 获取设备ID，作为游客唯一注册凭证
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		String DEVICE_ID = tm.getDeviceId();
		return DEVICE_ID;
	}
}


