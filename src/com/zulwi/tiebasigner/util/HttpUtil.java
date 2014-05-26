package com.zulwi.tiebasigner.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.zulwi.tiebasigner.bean.HttpResultBean;
import com.zulwi.tiebasigner.exception.HttpResultException;
import com.zulwi.tiebasigner.exception.StatusCodeException;

public class HttpUtil {
	public static HttpResultBean post(String url, List<NameValuePair> params, String paramsEncoding, List<NameValuePair> header) throws HttpResultException {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
		HttpPost post = new HttpPost(url);
		if (header != null && header.size() != 0) {
			for (int i = 0; i < header.size(); i++) {
				post.addHeader(header.get(i).getName(), header.get(i).getValue());
			}
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(params, paramsEncoding));
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			List<Cookie> cookie = ((AbstractHttpClient) client).getCookieStore().getCookies();
			if (statusCode != HttpStatus.SC_OK) throw new HttpResultException(new StatusCodeException("HTTP状态码错误！", statusCode));
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			return new HttpResultBean(statusCode, result, cookie);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new HttpResultException(HttpResultException.UNSUPPORTED_ENCODING);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpResultException(HttpResultException.NETWORK_FAIL);
		} catch (IOException e) {
			throw new HttpResultException(HttpResultException.NETWORK_FAIL);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public static HttpResultBean post(String url, List<NameValuePair> params, List<NameValuePair> header) throws HttpResultException {
		return post(url, params, "UTF-8", header);
	}

	public static HttpResultBean get(String url, List<NameValuePair> header) throws HttpResultException {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
		HttpGet get = new HttpGet(url);
		if (header != null && header.size() != 0) {
			for (int i = 0; i < header.size(); i++) {
				get.addHeader(header.get(i).getName(), header.get(i).getValue());
			}
		}
		try {
			HttpResponse response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			List<Cookie> cookie = ((AbstractHttpClient) client).getCookieStore().getCookies();
			if (statusCode != HttpStatus.SC_OK) throw new HttpResultException(new StatusCodeException("HTTP状态码错误！", statusCode));
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			return new HttpResultBean(statusCode, result, cookie);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new HttpResultException(HttpResultException.UNSUPPORTED_ENCODING);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpResultException(HttpResultException.NETWORK_FAIL);
		} catch (IOException e) {
			throw new HttpResultException(HttpResultException.NETWORK_FAIL);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

}