package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SiteBean implements Serializable {
	public int id;
	public int position;
	public String name;
	public String url;

	public SiteBean(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public SiteBean(int id, String name, String url) {
		this(name, url);
		this.id = id;
	}

	public SiteBean(String name, String url, int position) {
		this(name, url);
		this.position = position;
	}
}