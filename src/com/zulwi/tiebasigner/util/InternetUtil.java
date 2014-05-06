package com.zulwi.tiebasigner.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.zulwi.tiebasigner.exception.StatusCodeException;

public class InternetUtil {
	private String url;
	private HttpGet get;
	private HttpPost post;
	private HttpClient client;
	@SuppressWarnings("unused")
	private Context context;
	private String result = "ÎÞ·µ»Ø";;
	public final static int NETWORK_FAIL = 0;
	public final static int STATUS_ERROR = 1;
	public final static int PARSE_ERROR = 2;
	public final static int OTHER_ERROR = 3;
	public final static int AUTH_FAIL = 4;
	public final static int SUCCESSED = 5;
	public final static int ADD_SITE = 0;
	public final static int EDIT_SITE = 1;

	public InternetUtil(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	public String get() throws StatusCodeException, IOException, ClientProtocolException, Exception {
		client = new DefaultHttpClient();
		get = new HttpGet(url);
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		get.addHeader("Client-Version", "1.0.0");
		get.addHeader("Content-Type", "application/json");
		get.addHeader("User-Agent", "Android Client For Tieba Signer");
		HttpResponse response = client.execute(get, localContext);
		int statusCode = response.getStatusLine().getStatusCode();
		List<Cookie> cookies = cookieStore.getCookies();
		for (int i = 0; i < cookies.size(); i++) {
			Log.i("Local cookie: ", cookies.get(i).toString());
		}
		if (statusCode != 200) {
			throw new StatusCodeException("HTTP×´Ì¬Âë´íÎó£¡", statusCode);
		} else {
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "utf-8");
			return result;
		}
	}

	public String post(List<NameValuePair> postParams) throws StatusCodeException, ClientProtocolException, IOException, Exception {
		post = new HttpPost(url);
		client = new DefaultHttpClient();
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		post.addHeader("Client-Version", "1.0.0");
		post.addHeader("User-Agent", "Android Client For Tieba Signer");
		HttpEntity entity = new UrlEncodedFormEntity(postParams, "UTF-8");
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		int statusCode = response.getStatusLine().getStatusCode();
		List<Cookie> cookies = cookieStore.getCookies();
		for (int i = 0; i < cookies.size(); i++) {
			Log.i("Local cookie: ", cookies.get(i).toString());
		}
		if (statusCode != 200) {
			throw new StatusCodeException("HTTP×´Ì¬Âë´íÎó£¡", statusCode);
		} else {
			HttpEntity responseEntity = response.getEntity();
			result = EntityUtils.toString(responseEntity, "utf-8");
			return result;
		}
	}

	public String getResult() {
		return result;
	}
}
