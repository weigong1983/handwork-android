package com.daiyan.handwork.bean;

import java.io.Serializable;

public class WorksCard implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6767073683035488406L;
	
	
	public String workname; // 作品名称
	public String mademan; // 出品人
	public String size; // 尺寸
	public String madeplace; // 出品地
	public String material; // 材料
	public String jobtitle; // 职称
	public int createman; // 是否主创：0/1
	public int producer; // 是否监制：0/1
	public String manufacture; // 制作工艺【作品品类】
	public String productiontime; // 制作工时
	public String referenceprice; // 参考价格
	public String customtime; // 订制时间
	public String opusnumber; // 作品编号
	public int limited; // 1：原作  2： 限量  3： 量产
	public String packing; // 包装
}
