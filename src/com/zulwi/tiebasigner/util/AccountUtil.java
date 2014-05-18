package com.zulwi.tiebasigner.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.exception.StatusCodeException;
import com.zulwi.tiebasigner.util.HttpUtil;

@SuppressWarnings("serial")
public class AccountUtil implements Serializable {
	public int uid;
	private String siteUrl;
	private String username;
	private String password;
	private List<Cookie> cookie;
	private String cookieString;

	public AccountUtil(String username, String password, String siteUrl) {
		this.username = username;
		this.password = password;
		this.siteUrl = siteUrl;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public String getUsername() {
		return username;
	}

	public AccountBean doLogin() throws JSONException, StatusCodeException, ClientProtocolException, IOException, Exception {
		HttpUtil site = new HttpUtil(null, siteUrl + "/plugin.php?id=zw_client_api&a=do_login");
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		NameValuePair pair1 = new BasicNameValuePair("username", username);
		NameValuePair pair2 = new BasicNameValuePair("password", password);
		postParams.add(pair1);
		postParams.add(pair2);
		String result = site.post(postParams);
		cookie = site.getCookies();
		StringBuilder cookieBuilder = new StringBuilder();
		for (int i = 0; i < cookie.size(); i++) {
			Cookie cookieObject = cookie.get(i);
			cookieBuilder.append(cookieObject.getName() + "=" + cookieObject.getValue() + "; ");
		}
		cookieString = cookieBuilder.toString();
		JSONObject jsonObject = new JSONObject(result);
		int status = jsonObject.getInt("status");
		String msg = jsonObject.getString("msg");
		JSONObject data = jsonObject.getJSONObject("data");
		System.out.println(data);
		if (status != 0) throw new Exception(msg);
		return new AccountBean(data.getString("username"), data.getString("email"), siteUrl, cookieString);
	}

	public String getCookieString() {
		return cookieString;
	}
}