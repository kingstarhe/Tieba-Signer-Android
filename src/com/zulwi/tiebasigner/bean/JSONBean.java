package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

import org.json.JSONObject;

@SuppressWarnings("serial")
public class JSONBean implements Serializable {
	public int status;
	public String message;
	public JSONObject data;

	public JSONBean(int status, String message, JSONObject data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
}