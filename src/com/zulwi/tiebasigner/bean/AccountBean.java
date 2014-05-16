package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AccountBean implements Serializable {
	public int id;
	public String username;
	public String email;
	public String cookieString;
	public int sid;
	public String siteUrl;
	public String siteName;
	public int current;

	public AccountBean(int id, int sid, String username, String email) {
		this.id = id;
		this.sid = sid;
		this.username = username;
		this.email = email;
	}

	public AccountBean(String username, String email, String siteUrl, String cookieString) {
		this.username = username;
		this.email = email;
		this.siteUrl = siteUrl;
		this.cookieString = cookieString;
	}

	public AccountBean(String username, String email, int sid, String siteName, String siteUrl) {
		this.username = username;
		this.email = email;
		this.sid = sid;
		this.siteName = siteName;
		this.siteUrl = siteUrl;
	}

	public AccountBean(int id, int sid, String username, String email, String cookieString, int current, String siteName, String siteUrl) {
		this.id = id;
		this.sid = sid;
		this.username = username;
		this.cookieString = cookieString;
		this.current = current;
		this.siteName = siteName;
		this.siteUrl = siteUrl;
	}
}