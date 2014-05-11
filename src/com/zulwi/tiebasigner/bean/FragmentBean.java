package com.zulwi.tiebasigner.bean;

import android.support.v4.app.Fragment;

public class FragmentBean{
	public String title;
	public Fragment fragment;
	
	public FragmentBean(String title) {
		this.title = title;
	}

	public FragmentBean(String title, Fragment fragment) {
		this.fragment = fragment;
	}
}