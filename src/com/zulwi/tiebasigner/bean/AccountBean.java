package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

@SuppressWarnings("serial")
public class AccountBean implements Serializable {
	public int id;
	public int uid;
	public String username;
	public String email;
	public String cookieString;
	public String formhash;
	public int sid;
	public String siteUrl;
	public String siteName;
	public int current;
	public Bitmap avatar;

	public AccountBean(int uid, String username, String email, String siteUrl, String cookieString, String formhash) {
		this.uid = uid;
		this.username = username;
		this.email = email;
		this.siteUrl = siteUrl;
		this.cookieString = cookieString;
		this.formhash = formhash;
	}

	public AccountBean(int id, int uid, int sid, String username, String email, String cookieString, String formhash, int current, String siteName, String siteUrl) {
		this(uid, username, email, siteUrl, cookieString, formhash);
		this.id = id;
		this.sid = sid;
		this.current = current;
		this.siteName = siteName;
	}
}