package com.zulwi.tiebasigner.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.HttpResultBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.exception.HttpResultException;

public class ClientApiUtil {
	private AccountBean accountBean;
	public final static String API_PATH = "/plugin.php?id=zw_client_api&a=";
	public final static int ADD_SITE = 0;
	public final static int EDIT_SITE = 1;
	public final static int ERROR = 0;
	public final static int SUCCESSED = 1;
	public final static int LOAD_IMG = 2;
	private HttpResultBean resultBean;

	public ClientApiUtil(AccountBean accountBean) {
		this.accountBean = accountBean;
	}

	public JSONBean get(String action, String apiParam) throws HttpResultException {
		String url = getApiPath(action, apiParam);
		List<NameValuePair> header = new ArrayList<NameValuePair>();
		header.add(new BasicNameValuePair("Client-Version", "1.0.0"));
		header.add(new BasicNameValuePair("Content-Type", "application/json"));
		header.add(new BasicNameValuePair("User-Agent", "Android Client For Tieba Signer"));
		if (accountBean.cookieString != null) header.add(new BasicNameValuePair("Cookie", accountBean.cookieString));
		resultBean = HttpUtil.get(url, header);
		try {
	        JSONObject jsonObject = new JSONObject(resultBean.result);
			int status = jsonObject.getInt("status");
			String message = jsonObject.getString("msg");
			JSONObject data = jsonObject.getJSONObject("data");
			if (status == -1) throw new HttpResultException(HttpResultException.AUTH_FAIL);
			return new JSONBean(status, message, data, resultBean.result);
        } catch (JSONException e) {
	        e.printStackTrace();
	        throw new HttpResultException(HttpResultException.PARSE_ERROR);
        }
	}

	public JSONBean get(String action) throws HttpResultException {
		return get(action, null);
	}

	public JSONBean post(String action, List<NameValuePair> postParams, String apiParam) throws HttpResultException {
		String url = getApiPath(action, apiParam);
		List<NameValuePair> header = new ArrayList<NameValuePair>();
		header.add(new BasicNameValuePair("Client-Version", "1.0.0"));
		header.add(new BasicNameValuePair("Content-Type", "application/json"));
		header.add(new BasicNameValuePair("User-Agent", "Android Client For Tieba Signer"));
		if (accountBean.cookieString != null) header.add(new BasicNameValuePair("Cookie", accountBean.cookieString));
		resultBean = HttpUtil.post(url, postParams, header);
		try {
	        JSONObject jsonObject = new JSONObject(resultBean.result);
			int status = jsonObject.getInt("status");
			String message = jsonObject.getString("msg");
			JSONObject data = jsonObject.getJSONObject("data");
			if (status == -1) throw new HttpResultException(HttpResultException.AUTH_FAIL);
			return new JSONBean(status, message, data, resultBean.result);
        } catch (JSONException e) {
	        e.printStackTrace();
	        throw new HttpResultException(HttpResultException.PARSE_ERROR);
        }
	}

	public JSONBean post(String action, List<NameValuePair> postParams) throws HttpResultException {
		return post(action, postParams, null);
	}

	public String getApiPath(String action, String apiParam) {
		return accountBean.siteUrl + API_PATH + action + (apiParam != null && !apiParam.equals("") ? "&" + apiParam : "") + "&formhash=" + accountBean.formhash;
	}

	public List<Cookie> getCookies() {
		return resultBean.cookie;
	}

	public String getCookieString() {
		return resultBean.cookieString;
	}

	public String getResult() {
		return resultBean.result;
	}
}