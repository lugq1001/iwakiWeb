package com.iwaki.web.wx;

import java.util.ArrayList;

public class Button {

	private String name;
	
	private String type;
	
	private String key;
	
	private String url;
	
	private ArrayList<Button> sub_button;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ArrayList<Button> getSub_button() {
		return sub_button;
	}

	public void setSub_button(ArrayList<Button> sub_button) {
		this.sub_button = sub_button;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	 
}
