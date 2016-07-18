package com.daiyan.handwork.common;

import java.util.ArrayList;
import java.util.List;

import com.daiyan.handwork.bean.LoginUsers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBManager {
	private DBHelper mHelper;
	private SQLiteDatabase mDatabase;
	
	
	public DBManager(Context context) {
		mHelper = new DBHelper(context);
		//因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		mDatabase = mHelper.getWritableDatabase();
	}
	
	/**
	 * 往Login表中添加数据
	 * @param loginUsers
	 */
	public void addAll(List<LoginUsers> loginUsers) {
		//开始事务
		mDatabase.beginTransaction();
		try {
			for(LoginUsers loginUser : loginUsers) {
				mDatabase.execSQL("INSERT INTO login VALUES(?, ?, ?, ?)", 
						new Object[] {loginUser.getUid(), loginUser.getHeadImage(), loginUser.getUsername(), loginUser.getPassword()});
				//设置事务成功完成
				mDatabase.setTransactionSuccessful();
			}
		} finally {
			//结束事务
			mDatabase.endTransaction();
		}
	}
	
	/**
	 * 添加数据
	 * @param loginUsers
	 */
	public void add(LoginUsers loginUsers) {
		mDatabase.beginTransaction();
		mDatabase.execSQL("INSERT INTO login VALUES(?, ?, ?, ?)", 
				new Object[] {loginUsers.getUid(), loginUsers.getHeadImage(), loginUsers.getUsername(), loginUsers.getPassword()});
		mDatabase.setTransactionSuccessful();
		mDatabase.endTransaction();
	}
	
	/**
	 * 删除指定数据
	 * @param loginUsers
	 */
	public void delete(LoginUsers loginUsers) {
		mDatabase.delete("login", "uid = ? AND username = ?", new String[] {loginUsers.getUid(), loginUsers.getUsername()});
	}
	
	/**
	 * 更新数据
	 * @param loginUsers
	 */
	public void update(LoginUsers loginUsers) {
		mDatabase.execSQL("UPDATE login set headurl=?, username=?, password=? WHERE uid=?", 
				new Object[] {loginUsers.getHeadImage(), loginUsers.getUsername(), loginUsers.getPassword(), loginUsers.getUid()});
	}
	
	/**
	 * 查找指定UID的数据
	 * @param uid
	 * @return
	 */
	public LoginUsers find(String uid) {
		Cursor cursor = mDatabase.rawQuery("SELECT * FROM login WHERE uid=?", 
				new String[] {uid});
		if(cursor.moveToNext()) {
			String headurl = cursor.getString(cursor.getColumnIndex("headurl"));
			String username = cursor.getString(cursor.getColumnIndex("username"));
			String password = cursor.getString(cursor.getColumnIndex("password"));
			return new LoginUsers(uid, headurl, username, password);
		}
		return null;
	}
	
	/**
	 * 查询Login表中的第一条数据
	 * @return
	 */
	public LoginUsers queryFirst() {
		Cursor cursor = mDatabase.rawQuery("SELECT * FROM login LIMIT 0,1", null);
		if(cursor.moveToNext()) {
			String uid = cursor.getString(cursor.getColumnIndex("uid"));
			String headurl = cursor.getString(cursor.getColumnIndex("headurl"));
			String username = cursor.getString(cursor.getColumnIndex("username"));
			String password = cursor.getString(cursor.getColumnIndex("password"));
			return new LoginUsers(uid, headurl, username, password);
		}
		return null;
	}
	
	/**
	 * 查询Login表所有数据
	 * @return
	 */
	public List<LoginUsers> queryAll() {
		List<LoginUsers> loginUsers = new ArrayList<LoginUsers>();
		Cursor cursor = queryTheCursor();
		while(cursor.moveToNext()) {
			String uid = cursor.getString(cursor.getColumnIndex("uid"));
			String headurl = cursor.getString(cursor.getColumnIndex("headurl"));
			String username = cursor.getString(cursor.getColumnIndex("username"));
			String password = cursor.getString(cursor.getColumnIndex("password"));
			LoginUsers loginUser = new LoginUsers(uid, headurl,username, password);
			loginUsers.add(loginUser);
		}
		cursor.close();
		return loginUsers;
	}
	
	/**
	 * 查询所有的Login表，返回光标
	 * @return
	 */
	public Cursor queryTheCursor() {
		return mDatabase.rawQuery("SELECT * FROM login", null);
	}
	
	/**
	 * 获得数据总条数
	 * @return
	 */
	public int getCount() {
		Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM login", null);
		if(cursor.moveToNext()) {
			return cursor.getInt(0);
		}
		return 0;
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		mDatabase.close();
	}
}
