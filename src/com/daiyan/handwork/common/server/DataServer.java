package com.daiyan.handwork.common.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.daiyan.handwork.common.UIHelper;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.JsonUtils;

import android.app.Activity;

/**
 * API接口处理类
 * @author AA
 * @Date 2014-12-01
 */
public class DataServer extends Controller {
	private static Activity mContext;

	private static DataServer mInstance;

	/**
	 * 获得单实例对象
	 * 
	 * @return
	 */
	public static DataServer getInstance() {
		if (mInstance == null) {
			mInstance = new DataServer();
		}
		return mInstance;
	}

	public void initialize(Activity context) {
		mContext = context;
	}
	
	/**
	 * 接口通用异常处理
	 * @param errorCode
	 * @return true: 表示帐号出现异常，直接系统处理，接口调用不需要返回到Activity界面处理了。
	 */
	private boolean handleException(String errorCode)
	{
		if (errorCode.equals(Consts.INVALID_AUTH)) // 无效的用户授权信息
		{
			Consts.NET_WORK_ERROR = "此帐号在异地登录，请重新登录！";
			UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_REMOTE_LOGIN);
			return true;
		}
		else if (errorCode.equals(Consts.STATUS_DISABLE)) // 帐号禁用
		{
			Consts.NET_WORK_ERROR = "此帐号被禁用！";
			UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_REMOTE_LOGIN);
			return true;
		}
		else if (errorCode.equals(Consts.STATUS_BIND_PHONE)) // 本设备ID已绑定手机号
		{
			Consts.NET_WORK_ERROR = "该设备ID已经绑定了手机号码，请用绑定的手机号码登录！";
			UIHelper.showLogin(mContext, Consts.LOGIN_TYPE_BIND_PHONE_LOGIN);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 做些什么呢，你决定吧
	 * @param url 地址
	 * @param params 参数
	 * @param needAuth 是否需要授权
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, Object> doSomething(String url,
			List<NameValuePair> params, boolean needAuth) throws Exception {
		String result = this.Post(url, params, needAuth, mContext);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map = JsonUtils.getJsonValues(result);
		Consts.NET_WORK_ERROR = "";
		
		// 异常处理
		if (handleException(map.get(Consts.STATUS).toString()))
			return null;

		// 正确时保存下发的数据
		if (map.get(Consts.STATUS).toString().equals(Consts.STATUS_OK)) {
			String dataString = map.get(Consts.DATA).toString();
			if (JsonUtils.IS_JSONOBJECT == JsonUtils.isJsonArray(dataString))
				map = JsonUtils.getJsonValues(dataString);
		} else {
			// 保存错误信息
			Consts.NET_WORK_ERROR = map.get(Consts.INFO).toString();
			map = null;
		}

		return map;
	}
	
	
	/**
	 * 获取验证码（不需要授权）
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getCode(String mobile) throws Exception{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phone", mobile));
		return doSomething("Account/getCode", params, false);
	}
	
	/**
	 * 注册（不需要授权）
	 * @param mobile
	 * @param code
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> register(String mobile, String code, String password, String deviceId) throws Exception{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phone", mobile));
		//params.add(new BasicNameValuePair("nickname", nickname));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("appinfo", deviceId));
		return doSomething("Account/register", params, false);
	}

	/**
	 * 登录（不需要授权）
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> login(String username, String password)
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));

		return doSomething("Account/login", params, false);
	}
	
	/**
	 * 游客登录（不需要授权）
	 * @param deviceId
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> loginGuest(String deviceId) 
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appinfo", deviceId));
		return doSomething("Account/login", params, false);
	}

	/**
	 * 退出登录
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> logout() throws Exception{
		return doSomething("Account/logout",
				new ArrayList<NameValuePair>(), true);
	}
	
	/**
	 * 修改密码
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> updatePassword(String oldPassword, String newPassword)
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("oldpwd", oldPassword));
		params.add(new BasicNameValuePair("pwd", newPassword));

		return doSomething("Account/updatePwd", params, true);
	}
	
	/**
	 * 忘记密码
	 * 处理步骤：
		1.	验证用户名和手机号码的一致性
		2.	如未传验证码，触发验证码发送操作
		3.	如验证码匹配，重置密码。
	 * @param phone
	 * @param code
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> forgetPassword(String phone, String code, String password)
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phone", phone));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("password", password));

		return doSomething("Account/forgetPassword", params, false);
	}
	
	/**
	 * 获取用户信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getUserInfo() throws Exception {
		return doSomething("Account/getUserInfo",
				new ArrayList<NameValuePair>(), true);
	}
	
	/**
	 * 获取其他用户信息
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getOtherUserInfo(String uid) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		return doSomething("Account/getOtherUserInfo", params, true);		
	}
	
	/**
	 * 修改用户资料和进行工艺家认证
	 * 备注：
	 * 1. isauth： 是否为认证 1 认证，2普通修改
	 * 2. 修改资料时，不更新的传入null即可
	 * @param isauth
	 * @param signature
	 * @param nickname
	 * @param job
	 * @param callname
	 * @param intangibleheritage
	 * @param worktime
	 * @param madeclassid
	 * @param address
	 * @param association
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> setUserInfo(int isauth, String signature, String nickname,
			String job, String callname, String intangibleheritage, int worktime, 
			String madeclassid, String province, String city, String distinct, String association) 
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(Consts.IS_AUTH, String.valueOf(isauth)));
		if (signature != null)
			params.add(new BasicNameValuePair(Consts.SIGNATURE, signature));
		if (nickname != null)
			params.add(new BasicNameValuePair(Consts.NICKNAME, nickname));
		if (job != null)
			params.add(new BasicNameValuePair(Consts.JOB, job));
		if (callname != null)
			params.add(new BasicNameValuePair(Consts.CALLNAME, callname));
		if (intangibleheritage != null)
			params.add(new BasicNameValuePair(Consts.INTANGIBLEHERITAGE, intangibleheritage));
		if (worktime > 0)
			params.add(new BasicNameValuePair(Consts.WORK_AGE, String.valueOf(worktime)));
		if (madeclassid != null)
			params.add(new BasicNameValuePair(Consts.CATEGORY, madeclassid));
		if (province != null)
			params.add(new BasicNameValuePair(Consts.PROVINCE, province));
		if (city != null)
			params.add(new BasicNameValuePair(Consts.CITY, city));
		if (distinct != null)
			params.add(new BasicNameValuePair(Consts.DISTINCT, distinct));
		if (association != null)
			params.add(new BasicNameValuePair(Consts.ASSOCIATION, association));
		return doSomething("Account/setUserInfo", params, true);
	}
	
	/**
	 * 修改头像
	 * @param headBytes
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> updateAvatar(String headBytes) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("avatar", headBytes));
		return doSomething("Account/pushAvatar", params, true);
	}
	
	/**
	 * 设置个人主页的录音
	 * @param voiceBytes  【amr格式的语音二进制数据】
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> setVoice(String voiceBytes) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voice", voiceBytes));
		params.add(new BasicNameValuePair("voicetype", "android"));
		return doSomething("Account/setVoice", params, true);
	}
	
	/**
	 * 获取我的页面基本数据
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> personCard(String uid) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (!uid.isEmpty())
			params.add(new BasicNameValuePair("uid", uid));
		return doSomething("Account/peopleCard", params, true);
	}
	
	/** ----------------------- 活动模块 ------------------**/
	
	/**
	 * 获取首页推荐的活动
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getRecommendActivity() throws Exception {
		return doSomething("Activity/getTopActivity",
				new ArrayList<NameValuePair>(), true);
	}
	
	/**
	 * 获取所有活动，按时间排序
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getAllActivitys(int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("Activity/activityIndex", params, true);
	}
	
	/**
	 * 获取活动详情
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getActivityDetail(int atid) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("atid", String.valueOf(atid)));
		return doSomething("Activity/detailActivity", params, true);
	}
	
	/**
	 * 参加活动
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> joinActivity(int atid) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("atid", String.valueOf(atid)));
		return doSomething("Activity/joinActivity", params, true);
	}
	
	/**
	 * 获取所有我参加过的活动
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getMyActivity(int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("Activity/getJoinActs", params, true);
	}

	/** ----------------------- 发现模块 ------------------**/
	/**
	 * 获取所有工艺品类（雕刻、刺绣、陶瓷、漆器...）
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getAllClasses() throws Exception {
		return doSomething("Find/getAllClasses",
				new ArrayList<NameValuePair>(), true);
	}
	
	/**
	 * 获取指定工艺品类下的工艺家
	 * @param classid
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getUserByClassid(String classid, int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("classid",classid));
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("Find/getUserByClassid", params, true);
	}

	/**
	 * 获取研究院信息
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getInstitute() throws Exception {
		return doSomething("Find/getInstitute",
				new ArrayList<NameValuePair>(), true);
	}
	
	/** ----------------------- 消息模块 ------------------**/
	/**
	 * 发送消息留言
	 * @param uid
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> sendMessage(String uid, String content) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("content", content));
		return doSomething("Messages/AddMsg", params, true);
	}
	
	
	/**
	 * 获取所有消息
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getAllMessages(int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("Messages/getAllMessages", params, true);
	}
	
	/** ----------------------- 作品模块 ------------------**/

	/**
	 * 获取首页推荐作品
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getRecommendWorks(int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("HomePage/getRecommendWorks", params, true);
	}
	
	/**
	 * 获取所有作品
	 * @param page
	 * @param isCollect: 0: 全部作品（【作品坊】版块显示）； 1： 全部出售中的作品（【收藏中】版块显示）
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getAllWorks(int page, int isCollect) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		params.add(new BasicNameValuePair("isCollect", String.valueOf(isCollect)));
		return doSomething("Works/getAllWorks", params, true);
	}
	
	/**
	 * 获取指定用户的作品（uid不传则获取我自己的作品）
	 * @param page
	 * @param uid
	 * @param isCollect: 0: 我的全部作品； 1： 我的出售中作品
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getMyWorks(int page, String uid, int isCollect) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("isCollect", String.valueOf(isCollect)));
		return doSomething("Works/getMyworks", params, true);
	}
	
	
	/**
	 * 获取所有赞过的作品
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getLikeWorks(int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("Works/getUpclicks", params, true);
	}
	
	/**
	 * 获取所有收藏的作品【暂未使用】
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getCollects() throws Exception {
		return doSomething("Works/getCollects",
				new ArrayList<NameValuePair>(), true);
	}
	
	/**
	 * 获取作品详情
	 * @param id 作品编号
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getWorksDetail(String id) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		return doSomething("Works/detailWorks", params, true);
	}
	
	/**
	 * 给研究院留言（后台可以看到所有留言）
	 * @param id 作品编号
	 * @param content 留言内容
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> leaveMessage(String id, String content) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("content", content));
		return doSomething("Works/leaveMsg", params, true);
	}
	
	/**
	 * 作品点赞
	 * @param id 作品编号
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> like(String id) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		return doSomething("Works/upClick", params, true);
	}
	
	/**
	 * 作品取消点赞
	 * @param id 作品编号
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> cancelLike(String id) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		return doSomething("Works/cancelUpclick", params, true);
	}
	
	/**
	 * 收藏作品
	 * @param id 作品编号
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> collect(String id) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		return doSomething("Works/collectClick", params, true);
	}
	
	/**
	 * 评论作品
	 * @param id 作品编号
	 * @param content 
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> comment(String id, String content) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("content", content));
		return doSomething("Works/reMessage", params, true);
	}
	
	/**
	 * 获取所有作品坊作品
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getWorksIndex(int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("Works/getWorksIndex", params, true);
	}
	
	/**
	 * 获取作品所有的点赞
	 * @param id 作品编号
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getWorksUpclicks(String id) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		return doSomething("Works/getWorksUpclicks", params, true);
	}
	
	/**
	 * 获取作品所有的评论
	 * @param id 作品编号
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getWorksComments(String id, int page) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		return doSomething("Works/getWorksRemsgs", params, true);
	}
	


	
	/**
	 * 意见反馈
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> feedback(String content) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("content", content));
		return doSomething("Account/feedback", params, true);
	}
}
