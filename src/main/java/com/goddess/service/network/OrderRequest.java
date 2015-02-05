package com.goddess.service.network;

public class OrderRequest extends BaseRequest {

	private String username;
	
	private String orderJson;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrderJson() {
		return orderJson;
	}

	public void setOrderJson(String orderJson) {
		this.orderJson = orderJson;
	}

	
	
}
