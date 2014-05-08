package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
public class NavigationBean implements Serializable {
	public int icon;
	public String title;

	public NavigationBean(int icon, String title) {
		this.icon = icon;
		this.title = title;
	}
}