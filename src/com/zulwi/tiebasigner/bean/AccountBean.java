package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AccountBean implements Serializable {
	public String siteUrl;
	public String username;
	public String email;
	public String cookieString;

	public AccountBean(String username, String email, String siteUrl, String cookieString) {
		this.username = username;
		this.email = email;
		this.siteUrl = siteUrl;
		this.cookieString = cookieString;
	}
}