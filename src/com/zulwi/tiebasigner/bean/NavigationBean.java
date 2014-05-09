package com.zulwi.tiebasigner.bean;

import android.support.v4.app.Fragment;

public class NavigationBean{
	public int icon;
	public String title;
	public Fragment fragment;
	
	public NavigationBean(int icon, String title) {
		this.icon = icon;
		this.title = title;
	}

	public NavigationBean(int icon, String title, Fragment fragment) {
		this(icon, title);
		this.fragment = fragment;
	}
}