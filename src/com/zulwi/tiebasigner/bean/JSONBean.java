package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class JSONBean implements Serializable {
	public int status = -1;
	public String message = "";
	public JSONObject data;
	public String jsonString = "";

	public JSONBean(int status, String message, JSONObject data, String jsonString) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.jsonString = jsonString;
	}

	public JSONBean(JSONObject json) {
		try {
			status = json.getInt("status");
			message = json.getString("msg");
			data = json.getJSONObject("data");
			jsonString = json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}