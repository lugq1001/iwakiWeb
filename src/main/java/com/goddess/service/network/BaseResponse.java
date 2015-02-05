package com.goddess.service.network;

public class BaseResponse {

	private boolean result = false;
	
	private String desc = "";

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
