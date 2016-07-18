package com.daiyan.handwork.constant;

import android.app.Activity;

import com.daiyan.handwork.R;
import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.utils.LocationUtil;

public class Consts {

	// 服务器请求KEY，服务器端写死
	public static final String APP_KEY = "14r30n97b86e69a";
	public static final String APP_SECRET = "0484khbf9802romv0668bpgr1541iebm";

	// 蒲公英内测平台SDK APPKEY
	public static final String PGY_APP_KEY = "15ff2a1d1d9ea66f3ae2b0f62af448e6";

	
	/**
	 * 是否调试环境
	 */
	public static boolean IS_DEBUG = false;

	public static final String DOMAIN_NAME = "shouzuopin.com";
	
	/** 服务器API请求正式地址前缀 */
	private static final String URL_API_BASE = "http://api." + DOMAIN_NAME + "/ArtApi/index.php/";
	/** 服务器API请求测试地址前缀 */
	private static final String URL_API_TEST = "http://apitest.daiyan123.com/ArtApi/index.php/";
	
	/** 个人简介网址 */
	//public static final String URL_INTRODUCE_BASE = "http://api.daiyan123.com/ArtApi/index.php/ShowPage/getIntroduce/token/";
	/** 工艺美术交易服务标准网址 */
	public static final String URL_STANDARD_BASE = "http://api." + DOMAIN_NAME + "/ArtApi/index.php/ShowPage/getStandard/token/";
	/** 活动详情页基准网址 */
	public static final String URL_ACTIVITY_DETAIL_BASE = "http://api." + DOMAIN_NAME + "/ArtApi/index.php/ShowPage/detailActivity";
	/** 服务条款网址 */
	public static final String URL_SERVICE_TERM_BASE = "http://api." + DOMAIN_NAME + "/ArtApi/index.php/ShowPage/getTermofservice/token/";
	
	/** APP下载网址 */
	public static final String URL_APP_DOWNLOAD = "http://app.shouzuopin.com/";
	
	
	/** 拍照图片保存路径*/
	public static final String PHOTO_DIR_PATH = "/Handwork/images/";
	/** 拍照后图片临时名称*/
	public static final String PHOTO_TEMP_NAME = "image.jpg";
	/** 用户头像尺寸 */
	public static final int AVATAR_SIZE = 240;
	/** APP允许最大图片尺寸（避免内存爆了） */
	public static final int MAX_IMG_SIZE = 640;
	
	/** 正常加载 */
	public static final int LOAD_NORMAL = 0;
	/** 下拉刷新 */
	public static final int LOAD_REFRESH = 1;
	/** 加载更多 */
	public static final int LOAD_MORE = 2;
	
	// 进入登录页面的类型
	public static final int LOGIN_TYPE_LAUNCH = 1; // 启动登录
	public static final int LOGIN_TYPE_NORMAL = 2; // 常规登录
	public static final int LOGIN_TYPE_MODIFY_PASSWORD = 3; // 修改密码
	public static final int LOGIN_TYPE_LOGOUT = 4; // 退出登录
	public static final int LOGIN_TYPE_REMOTE_LOGIN = 5; // 异地登录
	public static final int LOGIN_TYPE_BIND_PHONE_LOGIN = 6; // 已绑定手机号
	public static final String EXTRA_LOGIN_TYPE = "login_type";
	
	// 网页浏览器页面加载类型
	public static final int WEBVIEW_PAGE_ACTIVITY_DETAIL = 1; // 活动详情
	public static final int WEBVIEW_PAGE_PRODUCTION_PROCESS = 2; // 工艺制作流程
	public static final int WEBVIEW_PAGE_ART_STANDARD = 3; // 工艺美术标准
	public static final int WEBVIEW_PAGE_SERVICE_TERM = 4; // 服务条款
	public static final int WEBVIEW_PAGE_SHARE_APP = 5; // 分享APP
	public static final int WEBVIEW_PAGE_BUS_GUIDE = 6; // 乘车指引
	
	// 主页3个Fragment
	public final static int FRAGMENT_HOME = 0; // 首页
	public final static int FRAGMENT_FIND = 1; // 发现
	public final static int FRAGMENT_MINE = 2; // 我的
	
	// 作品交流互动2个Fragment
	public final static int FRAGMENT_LIKE = 0; // 赞
	public final static int FRAGMENT_COMMENT = 1; // 评论
	
	// 作品浏览类型
	public static final String BROWSE_WORKS_TYPE = "browseWorksType";
	public final static int BROWSE_WORKS_SHOP = 0; // 作品坊
	public final static int BROWSE_WORKS_COLLECTING = 1; // 收藏中
	
	// 个人资料编辑内容类型
	public final static int CONTENT_TYPE_NAME = 1; // 姓名
	public final static int CONTENT_TYPE_SIGNATURE = 2; // 个性签名
	/** 编辑的内容类型 */
	public static final String EXTRA_CONTENT_TYPE = "contentType";
	/** 编辑前的内容 */
	public static final String EXTRA_OLD_CONTENT = "oldContent";
	/** 编辑后的内容 */
	public static final String EXTRA_EDIT_CONENT = "editContent";
	
	// 个人资料编辑地区
	public static final String EXTRA_ACCOUNT_TYPE = "accountType";
	public static final String EXTRA_CONTENT_PROVINCE = "province";
	public static final String EXTRA_CONTENT_CITY = "city";
	public static final String EXTRA_CONTENT_DISTINCT = "distinct";
	
	
	// 个人主页4个Fragment
	public final static int FRAGMENT_HOMEPAGE = 0; // 主页
	public final static int FRAGMENT_INTRODUCTION = 1; // 简介
	public final static int FRAGMENT_MY_WORKS = 2; // 作品
	public final static int FRAGMENT_MY_COLLECTING = 3; // 收藏中
	
	
	
	public static final String STATUS = "status";
	public static final String DATA = "data";
	public static final String INFO = "info";
	public static final String DATA_LIST = "infos"; // data字段包含的是列表数据
	public static final String HAS_MORE = "hasMore"; // 还有更多数据（列表分页加载时使用）
	
	// 登录后获得的用户信息
	public static final String TOKEN = "token";
	public static final String SECRET = "secret";
	
	public static final String POSITION = "position";
	public static final String UID = "uid";
	public static final String PHONE = "phone";
	public static final String REALNAME = "realname";
	public static final String UNAME = "uname";
	public static final String NICKNAME = "nickname";
	public static final String PHOTO = "s_photo"; // 头像
	public static final String B_PHOTO = "photo"; // 大头像
	public static final String SIGNATURE = "signature"; //个性签名
	public static final String ADDRESS = "address"; // 地址
	public static final String PROVINCE = "province"; // 省
	public static final String CITY = "city"; // 市
	public static final String DISTINCT = "district"; // 区

	public static final String JOB = "job"; // 职称
	public static final String CALLNAME = "callname"; // 称号
	public static final String CATEGORY = "madeclassid"; // 从事的工艺品类
	public static final String IS_AUTH = "isauth"; // // 0: 普通用户; 1: 认证工艺师； 2： 认证中 3： 游客
	public static final String INTANGIBLEHERITAGE = "Intangibleheritage"; // 非遗
	public static final String WORK_AGE = "worktime"; // 从业年限
	public static final String AID = "aid"; // 协会ID
	public static final String ASSOCIATION = "association"; // 协会名称
	
	public static final String INTRODUCE = "introduce"; // 简介(个人主页显示)
	public static final String PASSWORD = "password";
	public static final String GEOX = "geox";
	public static final String GEOY = "geoy";
	public static final String OPENID = "openid";
	
	// 手作品代言录音文件路径
	public static final String VOICE_PATH = "voicepath";
	
	// 我的首页资料
	public static final String USERINFO = "userinfo";
	public static final String LIKE_COUNT = "upclickcount";
	public static final String ACTIVITY_COUNT = "joinactcount";
	public static final String NOTICE_COUNT = "totalmsg";
	
	// 资料类型
	public static final int INFO_TYPE_AVATAR = 1;
	public static final int INFO_TYPE_SIGNATURE = 2;
	public static final int INFO_TYPE_NAME = 3;
	public static final int INFO_TYPE_TITLE = 4;
	public static final int INFO_TYPE_DESIGNATION = 5;
	public static final int INFO_TYPE_INTANGIBLEHERITAGE = 6;
	public static final int INFO_TYPE_WORKAGE = 7;
	public static final int INFO_TYPE_CATEGORY = 8;
	public static final int INFO_TYPE_AREA = 9;
	public static final int INFO_TYPE_ASSOCIATION = 10;
	

	// 通用字段
	public static final String CLASSES = "classes";
	public static final String CLASSID = "classid";
	public static final String CLASSNAME = "classname";
	
	public static final String MESSAGES = "messages";
	public static final String ITEMID = "mgid";
	public static final String ZANCOUNT = "upclick";
	public static final String CONTENT = "content";
	public static final String SELECTITEMS = "selectitems";
	public static final String SELECTMORE = "selectmore";
	public static final String SINGLE_SELECT = "0";
	public static final String MULTI_SELECT = "1";
	public static final String VOTE_COUNT = "countthis";
	public static final String VOTE_PERCENT = "voteresult";
	public static final String RULE_CONTENT = "rulecontent";
	public static final String JOIN_COUNT = "joincount";
	public static final String VIEW_COUNT = "viewcount";
	public static final String TITLE = "title";
	public static final String IMAGES = "images";
	public static final String IMAGE = "image";

	// 活动接口相关字段
	public static final String ACT_ID = "id"; // 活动ID
	public static final String ACT_TITLE = "title"; // 活动标题
	public static final String ACT_THUMB_ICON = "s_image"; // 活动缩略图
	public static final String ACT_M_IMAGE = "m_image"; // 活动中图
	public static final String ACT_IMAGE = "image"; // 活动大图
	public static final String ACT_START_TIME = "startdatetime"; // 开始时间
	public static final String ACT_ADDRESS = "address"; // 举办地址

	// 作品列表接口基础信息
	public static final String WORKS_AUTHOR = "author"; // 作者
	public static final String WORKS_AUTHOR_ID = "uid"; // 作者ID
	public static final String WORKS_ID = "mgid"; // 作品编号
	public static final String WORKS_NAME = "gdname"; // 作品名称
	public static final String WORKS_DESCRIPTION = "description"; // 作品描述
	public static final String WORKS_IMAGE = "image"; // 作品大图
	public static final String WORKS_IMAGE_M = "m_image"; // 作品首页列表中等图
	public static final String WORKS_IMAGE_S = "s_image"; // 作品九宫格缩略图
	
	public static final String WORKS_LIKE_COUNT = "upclickcount"; // 作品点赞数目
	public static final String WORKS_COMMENT_COUNT = "remsgcount"; // 作品总评论数目
	public static final String WORKS_IS_LIKE = "isupclick"; // 我是否点过赞
	
	public static final String WORKS_COLOR = "color"; // 作品图片主色调
	public static final String WORRKS_DETAIL_URL = "detailUrl"; // 作品详情API返回字段： 网页版的作品详情页url地址，用于微博、微信分享链接使用
	
	
	// 作品详情页
	public static final String WORKS_DETAIL = "workdetail"; // 作品详情
	public static final String WORKS_IS_SALE = "issale"; // 是否出售（状态）
	public static final String WORKS_PRICE = "saleprice"; // 作品价格
	public static final String WORKS_MARK_TYPE = "marktype"; // 标价方式：1： 标价出售； 2： 议价出售
	public static final String WORKS_IMGS = "workimgs"; // 作品大图数组
	public static final String WORKS_MADE_FLOW = "madeflow"; // 作品制作流程网页地址
	public static final String WORKS_CARD = "workcard"; // 作品卡
	
	// 作品评论
	public static final String COMMENT_ID = "mgid"; // 评论ID
	public static final String COMMENT_TIME = "createtime"; // 评论时间
	public static final String COMMENT_CONTENT = "remessage"; // 评论内容
	
	// 消息
	public static final String MESSAGE_ID = "mid"; // 消息ID
	public static final String MESSAGE_WORKS_ID = "mgid"; // 消息关联的作品ID
	public static final String MESSAGE_FROM_UID = "seeuid"; // 消息发送者UID
	public static final String MESSAGE_TO_UID = "uid"; // 消息接收者UID
	public static final String MESSAGE_CONTENT = "content"; // 消息内容
	public static final String MESSAGE_TIME = "createtime"; // 发送时间
	public static final String MESSAGE_IS_SYSMSG = "issysmsg"; // 是否为系统消息 1 系统消息 0普通消息
	
	// 工艺家列表作品摘要列表
	public static final String ARTISANS_WORKS_LIST = "worksinfo";
	public static final String ARTISANS_WORKS_COUNT = "workscount"; // 工艺家作品数目
	
	// 第一次启动进入欢迎页标志
	public static final String EXTRA_IS_LAUNCH = "is_launch";
	
	// 用户ID
	public static final String EXTRA_UID = "uid";
	
	// 活动详情页
	public static final String EXTRA_ACTIVITY_ID = "id";
	//public static final String EXTRA_ACTIVITY_DETAIL_URL = "detailUrl";
	
	// 作品制作流程
	public static final String EXTRA_WORKS_ID = "id";
	//public static final String EXTRA_WORKS_PRODUCTION_PROCESS_URL = "productionProcessUrl";
	public static final String EXTRA_WORKS_CARD = "workscard";
	
	// 作品评论、点赞页面标示
	public static final String EXTRA_FRGMENT = "frgment";
	
	//数据传输
	public static final String VALUE_NULL = "null"; // 服务器空值返回定义
	public static final String STATUS_OK = "1";
	public static final String INVALID_AUTH = "10010";
	public static final String STATUS_DISABLE = "10080";
	public static final String STATUS_BIND_PHONE = "60040"; // 该设备已经绑定手机号，需要输入账号密码登陆
	
	public static String NET_WORK_ERROR = "";
	public static String NET_WORK_FAIL = "数据获取失败！";
	public static String NET_WORK_TIMEOUT = "请求超时，请稍后重试！";
	public static final String STATUS_INVALID_TIPS = "此帐号已在其他地方登录";
	public static final String STATUS_DISABLE_TIPS = "此帐号已被禁用";
	

	/**
	 * APP首次使用标志 用作SharedPreferences的Key
	 */
	public static final String KEY_APP_FIRST_LOAD = "app_first_load_flag";

	/**
	 * ImageFolder与ImageFolderItem间传值的Key
	 */
	public static final String KEY_EXTRA_IMAGE_LIST = "extra_image_list";
	
	/**
	 * 是否自动登录
	 */
	public static final String KEY_AUTO_LOGIN = "auto_login";

	// 首页显示数据的类型
	public static final int TYPE_MAIN_SHUOSHUO = 1;
	public static final int TYPE_MAIN_TOPIC = 2;
	public static final int TYPE_MAIN_ACTIVITY = 3;
	public static final int TYPE_MAIN_VOTE = 4;
	public static final int TYPE_MAIN_SPOKE = 5;
	public static final int TYPE_MAIN_GOODS = 6;

	// 拍照
	public static final int RESULT_TAKE_PICTURE = 1001;
	public static final String KEY_FOR_INTENT_TAKE_PICTURE = "take_picture_key_for_intent";

	// 头像原图缩放大小， 尽量大一些，避免缩小失真
	public static final int AVATAR_WIDTH = 120;
	public static final int AVATAR_HEIGHT = 120;
	
	
	/** 进入忘记密码页面请求码 */
	public static final int REQUEST_CODE_FORGOT_PASSWORD = 99;
	
	/** 进入单行内容编辑页面请求码 */
	public static final int REQUEST_CODE_EDIT_CONTENT = 100;
	public static final int RESULT_CODE_EDIT_NAME = 101;
	public static final int RESULT_CODE_EDIT_SIGNATURE = 102;
	public static final int RESULT_CODE_EDIT_ASSOCIATION = 103;
	/** 进入地区（省市区）编辑页面请求码 */
	public static final int REQUEST_CODE_EDIT_AREA = 110;
	
	/** 调用相册请求码 */
	public static final int REQUEST_CODE_OPEN_ALBUM = 660;

	/** 作品详情页返回作品列表页是否需要刷新 */
	public static final int REQUEST_REFRESH_WORKS_LIST = 120;
	public static final String EXTRA_REFRESH_WORKS_LIST = "refresh_works_list";
	/** 作品详情页进入评论页面返回是否需要刷新评论数目 */
	public static final int REQUEST_REFRESH_COMMENT_COUNT = 130;
	public static final String EXTRA_REFRESH_COMMENT_COUNT = "refresh_comment_count";
	
	/** 发表内容请求码 */
	public static final int REQUEST_CODE_PUB = 210;
	/** 发表内容返回码 */
	public static final int RESULT_CODE_FRAGMENT_HOME_OK = 211;
	

	/** 发评论请求码 */
	public static final int REQUEST_CODE_COMMENT_PUB = 300;
	/** 发评论返回码 */
	public static final int RESULT_CODE_COMMENT_PUB_OK = 301;
	
	/** 图片索引 */
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	/** 图片地址 */
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	
	
	// 通用浏览器网址参数传递
	public static final String EXTRA_WEB_URL = "webUrl";
	public static final String EXTRA_WEB_PAGE = "webPage";
	
	/**
	 * 获得API地址头部
	 * 
	 * @return
	 */
	public static String getApiUrl() {
		return IS_DEBUG ? URL_API_TEST : URL_API_BASE;
	}

	
	public static final String GUEST_UNAME = "guest";
	public static final String GUEST_PASSWORD = "123456";
	
	/**
	 * 判断是否访客身份
	 * @param userName
	 * @return
	 */
	public static boolean IS_GUEST(Activity context) {
		String isAuth = LocationUtil.readInit(context, Consts.IS_AUTH, "0");
		return isAuth.equals("3");
	}
}
