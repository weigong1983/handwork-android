package com.daiyan.handwork.bean;

import java.util.ArrayList;
import java.util.List;

public class WorksInfo {
	public String id = ""; // 作品ID
	public String worksPicUrl = ""; // 作品封面图
	public String worksName = ""; // 作品名称
	public boolean like = false; // 是否点赞过？
	
	public String uid = ""; // 作者ID
	public String avatarUrl = ""; // 作者头像
	public String authorName = ""; // 作者昵称
	public String callname = ""; // 称号

	public int likeCount = 0; // 作品点赞数
	public int commentCount = 0; // 作品评论数
	
	// 出售字段
	public int issale = 1; // 出售状态： 1：非卖品； 2：出售中； 3：已售
	public String price = ""; // 出售价格
	public int marktype = 1; // 标价方式：1： 标价出售； 2： 议价出售
	
	// 更详细了解
	public String description = ""; // 作品描述
	public String madeflow = ""; // 作品制作流程网址
	
	// 作品图片展示
	public List<String> workimgs = new ArrayList<String>(); // 作品图片
	public List<String> m_workimgs = new ArrayList<String>(); // 中等作品图片
	
	
	
	public String bgColor = "#FFFFFF"; // 作品封面图片主色调值，格式"#FFFFFF"
	public String detailUrl = ""; // 作品详情API返回字段： 网页版的作品详情页url地址，用于微博、微信分享链接使用
}
