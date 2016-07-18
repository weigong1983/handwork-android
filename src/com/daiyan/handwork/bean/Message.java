package com.daiyan.handwork.bean;

public class Message 
{
	public String id; // 消息ID
	public String mgid; // 关联的作品ID
	public String seeuid; // 消息发送者用户ID
	public String uid; // 消息接收者用户ID
	public String avatarUrl; // 头像
	public String time; // 发送时间：createtime
	public String content; // 通知内容
	
	public int issysmsg; // 是否为系统消息： 1 系统消息 0普通消息
}
