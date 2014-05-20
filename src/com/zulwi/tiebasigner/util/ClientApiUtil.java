package com.zulwi.tiebasigner.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.exception.ClientApiException;
import com.zulwi.tiebasigner.exception.StatusCodeException;

public class ClientApiUtil {
	private HttpGet get;
	private HttpPost post;
	private HttpClient client;
	@SuppressWarnings("unused")
	private Context context;
	private String result = "无返回";
	private List<Cookie> cookies;
	private String cookie;
	private String siteUrl;
	public final static int ADD_SITE = 0;
	public final static int EDIT_SITE = 1;
	public final static String API_PATH = "/plugin.php?id=zw_client_api&a=";
	public final static int ERROR = 0;
	public final static int SUCCESSED = 1;
	public final static int LOAD_IMG = 2;

	public ClientApiUtil(Context context, String siteUrl) {
		client = new DefaultHttpClient();
		this.context = context;
		this.siteUrl = siteUrl;
	}

	public ClientApiUtil(Context context, String siteUrl, String cookie) {
		this(context, siteUrl);
		this.cookie = cookie;
	}

	public JSONBean get(String action) throws ClientApiException {
		String url = getApiPath(action);
		get = new HttpGet(url);
		get.addHeader("Client-Version", "1.0.0");
		get.addHeader("Content-Type", "application/json");
		get.addHeader("User-Agent", "Android Client For Tieba Signer");
		if (cookie != null) get.addHeader("Cookie", cookie);
		try {
			HttpResponse response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
			if (statusCode != 200) {
				throw new ClientApiException(new StatusCodeException("HTTP状态码错误！", statusCode));
			} else {
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, "utf-8");
				JSONObject jsonObject = new JSONObject(result);
				int status = jsonObject.getInt("status");
				String message = jsonObject.getString("msg");
				JSONObject data = jsonObject.getJSONObject("data");
				if (status == -1) throw new ClientApiException(ClientApiException.AUTH_FAIL);
				return new JSONBean(status, message, data);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.NETWORK_FAIL);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.NETWORK_FAIL);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.PARSE_ERROR);
		}
	}

	public JSONBean post(String action, List<NameValuePair> postParams) throws ClientApiException {
		String url = getApiPath(action);
		post = new HttpPost(url);
		post.addHeader("Client-Version", "1.0.0");
		post.addHeader("User-Agent", "Android Client For Tieba Signer");
		try {
			post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.NETWORK_FAIL);
		}
		if (cookie != null) post.addHeader("Cookie", cookie);
		try {
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
			if (statusCode != 200) {
				throw new ClientApiException(new StatusCodeException("HTTP状态码错误！", statusCode));
			} else {
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, "utf-8");
				JSONObject jsonObject = new JSONObject(result);
				int status = jsonObject.getInt("status");
				String message = jsonObject.getString("msg");
				JSONObject data = jsonObject.getJSONObject("data");
				if (status == -1) throw new ClientApiException(ClientApiException.AUTH_FAIL);
				return new JSONBean(status, message, data);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.NETWORK_FAIL);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.NETWORK_FAIL);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ClientApiException(ClientApiException.PARSE_ERROR);
		}
	}

	public String getApiPath(String action) {
		return siteUrl + API_PATH + action;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public String getCookieString() {
		List<Cookie> cookie = getCookies();
		StringBuilder cookieBuilder = new StringBuilder();
		for (int i = 0; i < cookie.size(); i++) {
			Cookie cookieObject = cookie.get(i);
			cookieBuilder.append(cookieObject.getName() + "=" + cookieObject.getValue() + "; ");
		}
		return cookieBuilder.toString();
	}

	public String getResult() {
		return result;
	}
	
	public void close(){
		client.getConnectionManager().shutdown();
	}
	
	@Override
	protected void finalize() throws Throwable {
	    super.finalize();
	    close();
	}
	
}