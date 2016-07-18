package com.daiyan.handwork.utils;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class SDCardHelper {

	
	/**
	 * 是否插入SD卡
	 * @return
	 */
	public static boolean isSDCardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 获得SD卡根路径
	 * @return
	 */
	public static String getSDCardPath() {
		if(isSDCardExist()) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}
	
	/**
	 * 创建文件夹
	 * @param filepath
	 * @return
	 */
	public static boolean createDir(String filepath) {
		if(isSDCardExist()) {
			File dirFile = new File(filepath);
			if(!dirFile.exists()) {
				dirFile.mkdirs();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 创建文件
	 * @param dirpath
	 * @param filename
	 * @return
	 */
	public static boolean createFile(String dirpath, String filename) {
		if(isSDCardExist()) {
			File dir = new File(dirpath);
			if(!(dir.exists() && dir.isDirectory())) {
				createDir(dirpath);				
			}
			String filepath = dir.getAbsolutePath() + File.separator + filename;
			File file = new File(filepath);
			try {
				file.createNewFile();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	
	
	
	
	
}
