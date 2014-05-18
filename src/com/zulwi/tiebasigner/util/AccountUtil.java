package com.zulwi.tiebasigner.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.exception.ClientApiException;

@SuppressWarnings("serial")
public class AccountUtil implements Serializable {
	public int uid;
	private String siteUrl;
	private String username;
	private String password;
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

	public AccountBean doLogin() throws ClientApiException {
		ClientApiUtil site = new ClientApiUtil(null, siteUrl);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		NameValuePair pair1 = new BasicNameValuePair("username", username);
		NameValuePair pair2 = new BasicNameValuePair("password", password);
		postParams.add(pair1);
		postParams.add(pair2);
		JSONObject result;
		result = site.post("do_login", postParams);
		cookieString = site.getCookieString();
		int status;
		try {
			status = result.getInt("status");
			String msg = result.getString("msg");
			JSONObject data = result.getJSONObject("data");
			if (status != 0) throw new ClientApiException(msg, ClientApiUtil.AUTH_FAIL);
			return new AccountBean(data.getString("username"), data.getString("email"), siteUrl, cookieString);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiUtil.PARSE_ERROR);
		}
	}

	public String getCookieString() {
		return cookieString;
	}
}