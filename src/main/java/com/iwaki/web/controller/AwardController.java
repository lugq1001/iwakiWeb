package com.iwaki.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.iwaki.web.cache.RedisManager;

@Controller
@RequestMapping("/coupons")
public class AwardController {

	private static final Logger logger = LoggerFactory.getLogger(AwardController.class);
	
	@Autowired
	private RedisManager redisManager;
	
	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	@ResponseBody
	public boolean generate() {
		generate50();
		generate20();
		generate10();
		return true;
	} 
	
	private void generate50() {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
		//jedis.ltrim(key(50), 1, 0);
			boolean exist = Boolean.parseBoolean(jedis.get(existkey(50)));
			//boolean exist = false;
			if (!exist) {
				String key = key(50);
				for (int i = 1; i <= 150 ; i++) {
					String num = String.format("%03d", i);
					String coupons = "IK5012F" + num + "Y";
					jedis.lpush(key, coupons);
					logger.info(coupons);
				}
				jedis.set(existkey(50), true + "");
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
	}
	
	private void generate20() {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			//jedis.ltrim(key(20), 1, 0);
			boolean exist = Boolean.parseBoolean(jedis.get(existkey(20)));
			//boolean exist = false;
			if (!exist) {
				String key = key(20);
				for (int i = 0; i < 1000 ; i++) {
					String num = String.format("%03d", i);
					String couponsB = "IK2015A" + num + "B";
					jedis.lpush(key, couponsB);
					String couponsC = "IK2015B" + num + "C";
					jedis.lpush(key, couponsC);
					String couponsD = "IK2015C" + num + "D";
					jedis.lpush(key, couponsD);
					logger.info(couponsB + " " + couponsC + " " + couponsB);
				}
				
				jedis.set(existkey(20), true + "");
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
	}
	
	private void generate10() {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			//.ltrim(key(10), 1, 0);
			boolean exist = Boolean.parseBoolean(jedis.get(existkey(10)));
			//boolean exist = false;
			if (!exist) {
				String key = key(10);
				for (int i = 0; i < 1000 ; i++) {
					String num = String.format("%03d", i);
					String couponsB = "IK1025A" + num + "C";
					jedis.lpush(key, couponsB);
					String couponsC = "IK1025B" + num + "D";
					jedis.lpush(key, couponsC);
					String couponsD = "IK1025C" + num + "E";
					jedis.lpush(key, couponsD);
					logger.info(couponsB + " " + couponsC + " " + couponsB);
				}
				
				jedis.set(existkey(10), true + "");
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
	}
	
	private String key(int price) {
		return "coupons:" + price + ":";
	}
	
	private String existkey(int price) {
		return "coupons_ext:" + price + ":";
	}
}
