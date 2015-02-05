package com.goddess.service.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.goddess.service.model.Order;
import com.goddess.service.model.User;
import com.goddess.service.network.LoginRequest;
import com.goddess.service.network.LoginResponse;
import com.goddess.service.network.OrderRequest;
import com.goddess.service.network.OrderResponse;
import com.goddess.service.network.OrdersRequest;
import com.goddess.service.network.OrdersResponse;
import com.goddess.service.network.RegisterRequest;
import com.goddess.service.network.RegisterResponse;
import com.goddess.service.service.GoddessService;
import com.google.gson.Gson;
import com.iwaki.web.util.Util;


@Controller
@RequestMapping("/goddess")
public class GoddessController {

	private static final Logger logger = LoggerFactory.getLogger(GoddessController.class);
	
	@Value("${goddess.service.key}")
	private String magicKey;
	
	
	private Gson gson = new Gson();
	
	@Autowired
	private GoddessService service;
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public RegisterResponse register(String data) {
		RegisterResponse response = new RegisterResponse();
		try {
			if (data == null || data.length() == 0) {
				response.setResult(false);
				response.setDesc("数据错误");
				return response;
			}
			data = URLDecoder.decode(data, "UTF-8");
			RegisterRequest request = gson.fromJson(data, RegisterRequest.class);
			String md5 = Util.getMD5Str(request.getCellphone() + magicKey);
			
			if (!request.getS().equals(md5)) {
				response.setResult(false);
				response.setDesc("验证失败");
				return response;
			}
			
			String cellphone = request.getCellphone();
			String password = request.getPassword();
			
			// 手机号验证
			if (cellphone == null || cellphone.isEmpty() || cellphone.length() != 11) {
				response.setResult(false);
				response.setDesc("手机号格式错误");
				return response;
			}
			// 密码验证 
			if (password == null || password.isEmpty() || password.length() < 6 || password.length() > 12) {
				response.setResult(false);
				response.setDesc("密码格式错误");
				return response;
			}
			User u = service.findUser(cellphone);
			if (u != null) {
				response.setResult(false);
				response.setDesc("该手机已注册");
				return response;
			}
			
			// 注册成功
			u = new User();
			u.setUsername(cellphone);
			u.setPassword(password);
			u.setCellphone(cellphone);
			u.setCreateTime(System.currentTimeMillis());
			u.setSid(UUID.randomUUID().toString());
			service.saveUser(u);
			response.setResult(true);
			response.setDesc("注册成功");
			logger.info("用户" + cellphone + " 注册成功");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			response.setResult(false);
			response.setDesc("未知错误");
			return response;
		}
		return response;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public LoginResponse login(String data) {
		LoginResponse response = new LoginResponse();
		try {
			if (data == null || data.length() == 0) {
				response.setResult(false);
				response.setDesc("数据错误");
				return response;
			}
			data = URLDecoder.decode(data, "UTF-8");
			LoginRequest request = gson.fromJson(data, LoginRequest.class);
			String md5 = Util.getMD5Str(request.getUsername() + magicKey);
			
			if (!request.getS().equals(md5)) {
				response.setResult(false);
				response.setDesc("验证失败");
				return response;
			}
			
			String username = request.getUsername();
			String password = request.getPassword();

			if (username == null || username.isEmpty()) {
				response.setResult(false);
				response.setDesc("用户名为空");
				return response;
			}
			
			User u = service.findUser(username);
			if (u == null) {
				response.setResult(false);
				response.setDesc("用户未注册");
				return response;
			}
			// 密码验证 
			if (password == null || password.isEmpty() || !password.equals(u.getPassword())) {
				response.setResult(false);
				response.setDesc("密码错误");
				return response;
			}
			
			// 登陆成功
			response.setResult(true);
			response.setDesc("登陆成功");
			logger.info("用户" + username + " 登陆成功");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			response.setResult(false);
			response.setDesc("未知错误");
			return response;
		}
		return response;
	}

	
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	@ResponseBody
	public OrderResponse order(String data) {
		OrderResponse response = new OrderResponse();
		try {
			if (data == null || data.length() == 0) {
				response.setResult(false);
				response.setDesc("数据错误");
				return response;
			}
			data = new String(data.getBytes("ISO-8859-1"),"UTF-8");
			data = URLDecoder.decode(data, "UTF-8");
			
			OrderRequest request = gson.fromJson(data, OrderRequest.class);
			String md5 = Util.getMD5Str(request.getUsername() + magicKey);
			
			if (!request.getS().equals(md5)) {
				response.setResult(false);
				response.setDesc("验证失败");
				return response;
			}
			
			Order order = gson.fromJson(request.getOrderJson(), Order.class);
			String username = request.getUsername();

			User u = service.findUser(username);
			if (u == null) {
				response.setResult(false);
				response.setDesc("用户不存在");
				return response;
			}
			order.setUser(u.getCellphone());
			order.setSid(UUID.randomUUID().toString());
			order.setTime(new Date());
			service.saveOrder(order);
			// 登陆成功
			response.setResult(true);
			response.setDesc("登陆成功");
			response.setOrderSid(order.getSid());
			logger.info("用户" + username + " 下单成功");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			response.setResult(false);
			response.setDesc("未知错误");
			return response;
		}
		return response;
	}
	
	@RequestMapping(value = "/orders", method = RequestMethod.POST)
	@ResponseBody
	public OrdersResponse orders(String data) {
		OrdersResponse response = new OrdersResponse();
		try {
			if (data == null || data.length() == 0) {
				response.setResult(false);
				response.setDesc("数据错误");
				return response;
			}
			data = URLDecoder.decode(data, "UTF-8");
			
			OrdersRequest request = gson.fromJson(data, OrdersRequest.class);
			String md5 = Util.getMD5Str(request.getUsername() + magicKey);
			
			if (!request.getS().equals(md5)) {
				response.setResult(false);
				response.setDesc("验证失败");
				return response;
			}
			
			String username = request.getUsername();

			User u = service.findUser(username);
			if (u == null) {
				response.setResult(false);
				response.setDesc("用户不存在");
				return response;
			}
			ArrayList<Order> orders = service.listOrders(u.getUsername());
			if (orders == null || orders.isEmpty()) {
				response.setResult(false);
				response.setDesc("您没有任何订单");
			} else {
				response.setResult(true);
				response.setDesc("成功");
				response.setOrders(orders);
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			response.setResult(false);
			response.setDesc("未知错误");
			return response;
		}
		return response;
	}
	
}
