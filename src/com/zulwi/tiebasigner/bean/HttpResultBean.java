package com.zulwi.tiebasigner.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.http.cookie.Cookie;

@SuppressWarnings("serial")
public class HttpResultBean implements Serializable {
	public int status;
	public String result;
	public List<Cookie> cookie;
	public String cookieString;

	public HttpResultBean(int status, String result, List<Cookie> cookie) {
		this.status = status;
		this.result = result;
		this.cookie = cookie;
		StringBuilder cookieBuilder = new StringBuilder();
		for (int i = 0; i < cookie.size(); i++) {
			Cookie cookieObject = cookie.get(i);
			cookieBuilder.append(cookieObject.getName() + "=" + cookieObject.getValue() + "; ");
		}
		this.cookieString = cookieBuilder.toString();
	}

}