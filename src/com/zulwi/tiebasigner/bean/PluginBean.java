package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PluginBean implements Serializable {

	public String id;
	public String name;
	public String version;

	public PluginBean(String id, String name, String version) {
		this.id = id;
		this.name = name;
		this.version = version;
	}
}