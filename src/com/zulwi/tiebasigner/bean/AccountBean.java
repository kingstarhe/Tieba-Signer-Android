package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AccountBean implements Serializable {
	public String siteUrl;
	public String username;
	public String cookieString;

	public AccountBean(String username, String siteUrl, String cookieString) {
		this.username = username;
		this.cookieString = cookieString;
		this.siteUrl = siteUrl;
	}
}