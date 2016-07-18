package com.daiyan.handwork.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ispoke.db";
	private static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	//数据库第一次被创建时调用
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS login" + 
				"(uid VARCHAR PRIMARY KEY NOT NULL, headurl TEXT, username VARCHAR, password VARCHAR)");
	}

	@Override
	//如果DATABASE_VERSION值被改为2，系统发现现有数据库版本不同，就会调用
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE login ADD COLUMN other STRING");
	}

}
