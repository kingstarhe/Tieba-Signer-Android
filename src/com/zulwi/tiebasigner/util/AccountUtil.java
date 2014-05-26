package com.zulwi.tiebasigner.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.HttpResultBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.exception.HttpResultException;

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

	public AccountBean doLogin() throws HttpResultException {
		List<NameValuePair> header = new ArrayList<NameValuePair>();
		header.add(new BasicNameValuePair("Client-Version", "1.0.0"));
		header.add(new BasicNameValuePair("User-Agent", "Android Client For Tieba Signer"));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		HttpResultBean resultBean = HttpUtil.post(siteUrl + "/plugin.php?id=zw_client_api&a=do_login", params, header);
		cookieString = resultBean.cookieString;
		System.out.println(resultBean.status);
		try {
			JSONObject json = new JSONObject(resultBean.result);
			JSONBean jsonBean = new JSONBean(json);
			if (jsonBean.status != 0) throw new HttpResultException(jsonBean.message, HttpResultException.AUTH_FAIL);
			int uid = jsonBean.data.getInt("uid");
			String username = jsonBean.data.getString("username");
			String email = jsonBean.data.getString("email");
			String formhash = jsonBean.data.getString("formhash");
			return new AccountBean(uid, username, email, siteUrl, cookieString, formhash);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new HttpResultException(HttpResultException.PARSE_ERROR);
		}
	}

	public String getCookieString() {
		return cookieString;
	}
}