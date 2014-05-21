package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

@SuppressWarnings("serial")
public class BaiduAccountBean implements Serializable {
	public String userId;
	public Bitmap avatar;

	public BaiduAccountBean(String userId, Bitmap avatar) {
		this.userId = userId;
		this.avatar = avatar;
	}

}