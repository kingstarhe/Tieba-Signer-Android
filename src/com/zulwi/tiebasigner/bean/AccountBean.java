package com.zulwi.tiebasigner.bean;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@SuppressWarnings("serial")
public class AccountBean implements Serializable {
	public int id;
	public int uid;
	public String username;
	public String email;
	public String cookieString;
	public String formhash;
	public int sid;
	public String siteName;
	public String siteUrl;
	public int current;
	public byte[] avatar;

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

	public void setSite(SiteBean site) {
		this.sid = site.id;
		this.siteName = site.name;
		this.siteUrl = site.url;
	}

	public void setAvatar(Bitmap avatar) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		avatar.compress(Bitmap.CompressFormat.PNG, 100, baos);
		this.avatar = baos.toByteArray();
	}

	public Bitmap getAvatar() {
		if (avatar == null) return null;
		return BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
	}
}