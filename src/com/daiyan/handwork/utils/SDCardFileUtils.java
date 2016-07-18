package com.daiyan.handwork.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

/**
 * SD卡操作类
 * 
 * @author AA
 * @Date 2014-12-29
 */
public class SDCardFileUtils {

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean sdCardIsExit() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return /sdcard
	 */
	public static String getSDCardPath() {
		if (sdCardIsExit()) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	/**
	 * 创建文件
	 * 如果是/sdcard/download/123.doc则只需传入filePath=download/123.doc
	 * 
	 * @param filePath 文件路径
	 * @return 创建文件的路径
	 * @throws IOException
	 */
	public static String creatFile2SDCard(String filePath) throws IOException {
		// 无论传入什么值 都是从根目录开始 即/sdcard/+filePath
		// 创建文件路径包含的文件夹
		String filedir = creatDir2SDCard(getFileDir(filePath));
		String fileFinalPath = filedir + getFileName(filePath);
		File file = new File(fileFinalPath);
		if (!file.exists()) {
			file.createNewFile();
		}
		return fileFinalPath;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param dirPath
	 */
	public static String creatDir2SDCard(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dirPath;
	}

	/**
	 * 获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	private static String getFileName(String filePath) {
		int index = 0;
		String tempName = "";
		if ((index = filePath.lastIndexOf("/")) != -1) {
			// 如果有后缀名才
			tempName = filePath.substring(index + 1);
		}
		return tempName.contains(".") ? tempName : "";
	}

	/**
	 * 获取文件目录路径
	 * 
	 * @param filePath
	 * @return
	 */
	private static String getFileDir(String filePath) {
		if (filePath.startsWith(getSDCardPath())) {
			return filePath.replace(getFileName(filePath), "");
		}
		return getSDCardPath() + filePath.replace(getFileName(filePath), "");
	}

	/**
	 * 
	 * @param inputStream
	 * @param filePath
	 */
	public static void creatFileByInputStream(InputStream inputStream,
			String filePath) {
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			creatFile2SDCard(filePath);
			bufferedInputStream = new BufferedInputStream(inputStream);
			bufferedOutputStream = new BufferedOutputStream(
					new FileOutputStream(new File(filePath)));
			byte array[] = new byte[2048];
			int num = 0;
			while ((num = bufferedInputStream.read(array)) != -1) {
				bufferedOutputStream.write(array, 0, num);
			}
			bufferedOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedOutputStream.close();
				bufferedInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Deletes a directory recursively.
	 * 
	 * @param directory
	 *            directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		if (!isSymlink(directory)) {
			cleanDirectory(directory);
		}

		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		}
	}

	/**
	 * Determines whether the specified file is a Symbolic Link rather than an
	 * actual file.
	 * <p>
	 * Will not return true if there is a Symbolic Link anywhere in the path,
	 * only if the specific file is.
	 * 
	 * @param file
	 *            the file to check
	 * @return true if the file is a Symbolic Link
	 * @throws IOException
	 *             if an IO error occurs while checking the file
	 * @since Commons IO 2.0
	 */
	public static boolean isSymlink(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("File must not be null");
		}
		File fileInCanonicalDir = null;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}

		if (fileInCanonicalDir.getCanonicalFile().equals(
				fileInCanonicalDir.getAbsoluteFile())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Cleans a directory without deleting it.
	 * 
	 * @param directory
	 *            directory to clean
	 * @throws IOException
	 *             in case cleaning is unsuccessful
	 */
	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (File file : files) {
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	/**
	 * Deletes a file. If file is a directory, delete it and all
	 * sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>You get exceptions when a file or directory cannot be deleted.
	 * (java.io.File methods returns a boolean)</li>
	 * </ul>
	 * 
	 * @param file
	 *            file or directory to delete, must not be <code>null</code>
	 * @throws NullPointerException
	 *             if the directory is <code>null</code>
	 * @throws FileNotFoundException
	 *             if the file was not found
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent) {
					throw new FileNotFoundException("File does not exist: "
							+ file);
				}
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}

}
