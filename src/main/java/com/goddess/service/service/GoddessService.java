package com.goddess.service.service;

import java.util.ArrayList;

import com.goddess.service.model.Order;
import com.goddess.service.model.User;

public interface GoddessService {

	public void saveUser(User user);

	public User findUser(String cellphone);
	
	public void saveOrder(Order order);
	
	public ArrayList<Order> listOrders(String username);
}
