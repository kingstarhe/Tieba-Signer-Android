package com.zulwi.tiebasigner.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.JSONBean;
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
		JSONBean result = site.post("do_login", postParams);
		cookieString = site.getCookieString();
		System.out.println(result.status);
		if (result.status != 0) throw new ClientApiException(result.message, ClientApiException.AUTH_FAIL);
		try {
			String username = result.data.getString("username");
			String email = result.data.getString("email");
			return new AccountBean(username, email, siteUrl, cookieString);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.PARSE_ERROR);
		}
	}

	public String getCookieString() {
		return cookieString;
	}
}