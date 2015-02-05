package com.goddess.service.service;

import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goddess.service.model.Order;
import com.goddess.service.model.User;
import com.google.gson.Gson;
import com.iwaki.web.cache.RedisManager;
import com.iwaki.web.model.ScoreRank;

@Service
public class GoddessServiceImpl implements GoddessService {

	private static final Logger logger = LoggerFactory.getLogger(GoddessServiceImpl.class);
	
	@Autowired
	private RedisManager redisManager;
	private Gson gson = new Gson();

	@Override
	public void saveUser(User user) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = userKey(user.getCellphone());
			jedis.set(key, gson.toJson(user));
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
	}

	@Override
	public User findUser(String cellphone) {
		Jedis jedis = null;
		User u = null;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = userKey(cellphone);
			String json = jedis.get(key);
			u = gson.fromJson(json, User.class);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return u;
	}

	@Override
	public void saveOrder(Order order) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = orderKey(order.getUser());
			long oldScore = order.getTime().getTime();
			jedis.zadd(key, oldScore, gson.toJson(order));
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
	}
	
	@Override
	public ArrayList<Order> listOrders(String username) {
		Jedis jedis = null;
		ArrayList<Order> orders = new ArrayList<Order>();
		try { 
			jedis = redisManager.getRedisInstance();
			String key = orderKey(username);
			Set<String> orderJsons = jedis.zrevrange(key, 0, -1);
			for (String json : orderJsons) {
				Order o = gson.fromJson(json, Order.class);
				orders.add(o);
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return orders;
	}

	
	private String userKey(String cellphone) {
		return "goddess:user:" + cellphone;
	}

	private String orderKey(String cellphone) {
		return "goddess:order:" + cellphone;
	}

	

}
