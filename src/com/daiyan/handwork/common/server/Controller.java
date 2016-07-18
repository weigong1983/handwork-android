package com.daiyan.handwork.common.server;

import java.util.List;

import org.apache.http.NameValuePair;

import com.daiyan.handwork.common.server.base.HttpUtil;
import com.daiyan.handwork.common.server.base.SHA1;
import com.daiyan.handwork.constant.Consts;
import com.daiyan.handwork.utils.LocationUtil;

import android.content.Context;


public class Controller {

	protected String Post(String url, List<NameValuePair> params, boolean needAuth, Context context) throws Exception {

		String Url = Consts.getApiUrl() + url + "?";
		String sig = "";
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				NameValuePair v = params.get(i);
				sig = sig + v.getName() + "=" + v.getValue();
			}
		}
		sig = sig + "appkey=" + Consts.APP_KEY;
		String auth = "";
		SHA1 sha1 = new SHA1();

		if (needAuth) {
			sig = sig + LocationUtil.readInit(context, Consts.SECRET, "");
			sig = sha1.getDigestOfString(sig.toString().getBytes());
			auth = "token=" + LocationUtil.readInit(context, Consts.TOKEN, "") + ",appkey=" + Consts.APP_KEY + ",sig=" + sig;
		} else {
			sig = sig + Consts.APP_SECRET;
			sig = sha1.getDigestOfString(sig.toString().getBytes());
			auth = "appkey=" + Consts.APP_KEY + ",sig=" + sig;
		}
		Url = Url.substring(0, Url.length() - 1);
		String result = HttpUtil.post(Url, params, auth);
		return result;
	}

}
