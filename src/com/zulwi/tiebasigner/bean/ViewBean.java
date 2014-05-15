package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

import android.view.View;

@SuppressWarnings("serial")
public class ViewBean implements Serializable {
	public int id;
	public View view;

	public ViewBean(int id, View view) {
		this.id = id;
		this.view = view;
	}
	
}