package com.zulwi.tiebasigner.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TiebaBean implements Serializable {
	public long tid;
	public int level;
	public String name;
	public int status;

	public TiebaBean(long tid, int level, String name) {
		this.tid = tid;
		this.level = level;
		this.name = name;
	}
	
	public TiebaBean(long tid, String name, int status) {
		this.tid = tid;
		this.name = name;
		this.status = status;
	}
}