package com.zulwi.tiebasigner.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.zulwi.tiebasigner.exception.StatusCodeException;

public class InternetUtil {
	private String url;
	private HttpGet get;
	private HttpPost post;
	private HttpClient client;
	@SuppressWarnings("unused")
	private Context context;
	private String result = "ÎÞ·µ»Ø";;

	public InternetUtil(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	public String get() throws StatusCodeException, IOException, ClientProtocolException, Exception {
		client = new DefaultHttpClient();
		get = new HttpGet(url);
		get.addHeader("Client-Version", "1.0.0");
		get.addHeader("Content-Type", "application/json");
		get.addHeader("User-Agent", "Android Client For Tieba Signer");
		HttpResponse response = client.execute(get);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new StatusCodeException("HTTP×´Ì¬Âë´íÎó£¡", statusCode);
		} else {
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "utf-8");
			return result;
		}
	}

	public String post(List<? extends BasicNameValuePair> params) throws StatusCodeException, ClientProtocolException, IOException, Exception {
		post = new HttpPost(url);
		client = new DefaultHttpClient();
		post.addHeader("Client-Version", "1.0.0");
		post.addHeader("Content-Type", "application/json");
		post.addHeader("User-Agent", "Android Client For Tieba Signer");
		HttpEntity entity = new UrlEncodedFormEntity(params);
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		int statusCode = response.getStatusLine().getStatusCode();
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
