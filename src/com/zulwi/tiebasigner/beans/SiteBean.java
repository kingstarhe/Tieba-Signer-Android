package com.zulwi.tiebasigner.beans;

public class SiteBean {
	public long id;
	public String name;
	public String url;
	
	public SiteBean(long id,String name,String url){
		this.id = id;
		this.name = name;
		this.url = url;
	}
	
	public SiteBean(String name,String url){
		this.name = name;
		this.url = url;
	}
}