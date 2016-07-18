package com.daiyan.handwork.bean;

public class LoginUsers {

	private String uid;
	private String headImage;
	private String username;
	private String password;
	
	public LoginUsers(String uid, String headImage, String username, String password) {
		this.uid = uid;
		this.headImage = headImage;
		this.username = username;
		this.password = password;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
