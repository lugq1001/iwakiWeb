package com.goddess.service.network;

import java.util.ArrayList;

import com.goddess.service.model.Order;

public class OrdersResponse extends BaseResponse {

	private ArrayList<Order> orders = new ArrayList<Order>();

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}

}
