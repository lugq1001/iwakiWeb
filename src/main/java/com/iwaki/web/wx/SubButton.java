package com.iwaki.web.wx;

import java.util.ArrayList;

public class SubButton {

	private String type;
	
	private String name;
	
	private String key;
	
	private ArrayList<SubButton> sub_button;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ArrayList<SubButton> getSub_button() {
		return sub_button;
	}

	public void setSub_button(ArrayList<SubButton> sub_button) {
		this.sub_button = sub_button;
	}
}
