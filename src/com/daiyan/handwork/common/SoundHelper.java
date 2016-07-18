package com.daiyan.handwork.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.SDCardFileUtils;

import android.R.integer;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Base64;

public class SoundHelper {

	// 语音播放结束监听器
	public static interface OnPlayCompletionListener {
		  public abstract void onCompletion();
	}
	
	private static OnPlayCompletionListener mOnPlayCompletionListener = null;
	private static MediaRecorder mRecorder;
	private static MediaPlayer mPlayer;
	private final static String SOUND_FOLDER = "sounds";
	public final static String SOUND_FILENAME = "spoke.amr";
	public final static String SOUND_PATH = Environment.getExternalStorageDirectory()
			+ File.separator + SOUND_FOLDER + File.separator;
	
	/**
	 * 开始录制
	 * @param filename
	 */
	public static void start(String filename) {
		if(!SDCardFileUtils.sdCardIsExit()) {
			return ;
		}
		
		File file = new File(SOUND_PATH);
		if (!file.exists())
		{
			file.mkdir();
		}
		
		if(mRecorder == null) {
			mRecorder = new MediaRecorder();
			//设置音频源为麦克风
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//设置录制的音频格式【采用AMR格式：魏工-2015-06-18】
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			//设置录制音频编码为AMR
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			//设置音频文件保存路径
			mRecorder.setOutputFile(SOUND_PATH + filename);
			try {
				//开始录制
				mRecorder.prepare();
				mRecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 停止录制
	 */
	public static void stop() {
		if(mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
	
	/**
	 * 暂停录制
	 */
	public static void pause() {
		if(mRecorder != null) {
			mRecorder.stop();
		}
	}
	
	/**
	 * 恢复录制
	 */
	public static void resume() {
		if(mRecorder != null) {
			mRecorder.start();
		}
	}
	
	/**
	 * 播放录制文件
	 * @param filename
	 */
	public static void play(String path, OnPlayCompletionListener listener) {
		
		mOnPlayCompletionListener = listener;
		
		if(mPlayer == null) {
			mPlayer = new MediaPlayer();
		}
		
		try {
			mPlayer.setDataSource(path);
			mPlayer.prepare();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		mPlayer.start();
		
		mPlayer.setOnCompletionListener(new OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
            	mPlayer.stop();
            	mPlayer.release();
            	mPlayer = null;
            	
            	// 播放结束回调应用层
            	if (mOnPlayCompletionListener != null)
            	{
            		mOnPlayCompletionListener.onCompletion();
            		mOnPlayCompletionListener = null;
            	}
            }
         });
	}
	
	/**
	 * 获得音频时长
	 * @param path
	 */
	public static int getDuration(String path) {
		int duration = 0;
		if(mPlayer == null) {
			mPlayer = new MediaPlayer();
		}
		
		try {
			mPlayer.reset();
			mPlayer.setDataSource(path);
			mPlayer.prepare();
			duration = mPlayer.getDuration() / 1000; // 转换为秒
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return duration;
	}
	
	/**
	 * 获得声音振幅
	 * @return
	 */
	public static double getAmplitude() {
		if(mRecorder != null) 
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else 
			return 0;
	}
	
	/**
	 * 语音文件转成Base64编码字符串
	 * @param path
	 * @return
	 */
	public static String soundsToBase64(String path) {
		String result = null;
		File file = new File(path);
		FileInputStream fis = null;
		byte[] buffer = null;
		ByteArrayOutputStream arrayOutputStream = null;
		
		try {
			fis = new FileInputStream(file);
			arrayOutputStream = new ByteArrayOutputStream();
			buffer = new byte[1024];
			int ch = -1;
			while((ch = fis.read(buffer)) != -1) {
				arrayOutputStream.write(buffer, 0, ch);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		byte[] data = arrayOutputStream.toByteArray();
		result = Base64.encodeToString(data, Base64.DEFAULT);
		return result;
	}
}
