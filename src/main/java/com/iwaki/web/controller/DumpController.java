package com.iwaki.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwaki.web.cache.RedisManager;
import com.iwaki.web.model.ScoreRank;
import com.iwaki.web.model.prize.Prize;


@Controller
@RequestMapping("/dump")
public class DumpController {

	private static final Logger logger = LoggerFactory.getLogger(DumpController.class);
	
	@Autowired
	private RedisManager redisManager;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public void dump(HttpServletResponse resp,String date) {
		resp.setHeader("Content-type", "text/html;charset=UTF-8");  
		resp.setCharacterEncoding("UTF-8");
		ObjectMapper mapper = new ObjectMapper();
		List<Prize> prizes = new ArrayList<Prize>();
		String result = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date d;
			if (date == null || date.length() == 0) {
				d = new Date(System.currentTimeMillis() - 28382132L);//服务器时差
			} else {
				d = sdf.parse(date);
			}
			String indexKey = dailyPrizeKey(d);
			Jedis jedis = redisManager.getRedisInstance();
			Set<String> codes = jedis.zrevrange(indexKey, 0, -1);
			for (String code : codes) {
				String json = jedis.get(prizeCodeKey(code));
				if (json == null || json.length() == 0) 
					continue;
				Prize p = mapper.readValue(json, Prize.class);
				prizes.add(p);
			}
			result = mapper.writeValueAsString(prizes);
		} catch(Exception e) {
			
		} 
		try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	private String prizeCodeKey(String code) {
		return "prize_code:" + code;
	}
	
	private String dailyPrizeKey(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String s = f.format(date);
		return "daily_prize_index:" + s + ":";
	}
	
	@RequestMapping(value = "/date", method = RequestMethod.GET)
	@ResponseBody
	public String date() {
		long sec = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		return sec + " " + now;
	} 
}
