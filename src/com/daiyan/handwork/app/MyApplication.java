package com.daiyan.handwork.app;

import com.daiyan.handwork.R;
import com.daiyan.handwork.constant.Consts;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pgyersdk.crash.PgyCrashManager;

import android.app.Application;

public class MyApplication extends Application {

	private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(100)// 缓存一百张图片
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
		
		// 注册蒲公英闪退
		PgyCrashManager.register(this, Consts.PGY_APP_KEY);

	}
}
