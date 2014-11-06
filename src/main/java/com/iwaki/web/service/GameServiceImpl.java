package com.iwaki.web.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwaki.web.cache.RedisManager;
import com.iwaki.web.model.Award;
import com.iwaki.web.model.Contact;
import com.iwaki.web.model.ScoreRank;
import com.iwaki.web.model.prize.Prize;
import com.iwaki.web.model.prize.PrizeType;

@Service
public class GameServiceImpl implements GameService {

	private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
	
	@Autowired
	private RedisManager redisManager;
	
	@Value("${game.rank.max}")
	private int rankMax;

	@Override
	public void incrSharedCount(String openid, String gamerIp) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			jedis.sadd(sharedKey(openid), gamerIp);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
	}
	
	public long incrRank(ScoreRank scoreRank) {
		Jedis jedis = null;
		Long rank = -1L;
		try { 
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(scoreRank);
			jedis = redisManager.getRedisInstance();
			String rankKey = rankKey();
			String openid = scoreRank.getOpenid();
			Double oldScore = jedis.zscore(rankKey, openid);
			if (oldScore == null || scoreRank.getScore() > oldScore) {
				jedis.zadd(rankKey, scoreRank.getScore(), openid);
				rank = jedis.zrevrank(rankKey, openid);
				if (openid != null && openid.length() > 0)
					jedis.set(prizeConditionKey(openid), true + "");
			} else { // 临时排名
				jedis.zadd(rankKey, scoreRank.getScore(), "temp");
				rank = jedis.zrevrank(rankKey, "temp");
				jedis.zrem(rankKey, "temp");
				if (openid != null && openid.length() > 0)
					jedis.set(prizeConditionKey(openid), false + "");
			}
			jedis.set(playerKey(openid), json);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return rank + 1;
	}
	
	public ArrayList<ScoreRank> topRanks() {
		Jedis jedis = null;
		ArrayList<ScoreRank> ranks = new ArrayList<ScoreRank>();
		try { 
			jedis = redisManager.getRedisInstance();
			String key = rankKey();
			Set<String> playerKeys = jedis.zrevrange(key, 0, rankMax);
			ObjectMapper mapper = new ObjectMapper();
			for (String pKey : playerKeys) {
				String json = jedis.get(playerKey(pKey));
				ScoreRank rank = mapper.readValue(json, ScoreRank.class);
				rank.setScore(jedis.zscore(key, pKey).longValue());
				ranks.add(rank);
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return ranks;
	}

	@Override
	public void acceptArticle(String openid) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = articleKey();
			jedis.sadd(key, openid);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}		
	}

	@Override
	public boolean hasAcceptArticle(String openid) {
		Jedis jedis = null;
		boolean has = false;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = articleKey();
			has = jedis.sismember(key, openid);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return has;
	}
	
	@Override
	public boolean helpAward(String ip, String openid, String code) {
		Jedis jedis = null;
		try { 
			jedis = redisManager.getRedisInstance();
			String key = helpKey(openid,code);
			jedis.sadd(key, ip);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			redisManager.returnResource(jedis);
		}
		return true;
	}

	// 领取兑奖码
	@Override
	public Award getGuestAward(String ip) throws Exception {
		Jedis jedis = null;
		Award a = null;
		jedis = redisManager.getRedisInstance();
		Random r = new Random();
		String code = "";
		Prize p;
		while (true) {
			code = 10000000 + r.nextInt(90000000) + "";
			String key = prizeCodeKey(code);
			String val = jedis.get(key);
			if (val == null || val.length() == 0) {
				String realCodeKey = couponskey(PrizeType.LEVEL_6.getPrice());// 10元优惠券
				String realCode = jedis.lpop(realCodeKey);
				if (realCode == null || realCode.length() == 0) {
					redisManager.returnResource(jedis);
					throw new Exception("亲爱的对不起，奖品已发放完毕。");
				}
				p = Prize.makeLevel6Prize(code,realCode);
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(p);
				jedis.set(key, json);
				break;
			}
		}
		a = new Award();
		int level = p.getPrizeType().getLevel();
		a.setLevel(level + "");
		a.setDesc("游客您好！恭喜您获得" + level + "等奖哦！");
		a.setCode(code); 
		jedis.sadd(dailyPrizeKey(), code);
		redisManager.returnResource(jedis);
		return a;
	}

	@Override
	public Award getFansAward(String openid) throws Exception {
		Jedis jedis = null;
		Award a = null;
		jedis = redisManager.getRedisInstance();
		Random r = new Random();
		String code = "";
		Prize p;
		while (true) {
			code = 10000000 + r.nextInt(90000000) + "";
			String key = prizeCodeKey(code);
			String val = jedis.get(key);
			if (val == null || val.length() == 0) {
				PrizeType type = lotty(openid);
				String realCode = "";
				if (type == PrizeType.LEVEL_4) {
					String realCodeKey = couponskey(PrizeType.LEVEL_4.getPrice());// 50元优惠券
					realCode = jedis.lpop(realCodeKey);
					if (realCode == null || realCode.length() == 0) {
						redisManager.returnResource(jedis);
						throw new Exception("亲爱的对不起，奖品已发放完毕。");
					}
				}
				if (type == PrizeType.LEVEL_5) {
					String realCodeKey = couponskey(PrizeType.LEVEL_5.getPrice());// 20元优惠券
					realCode = jedis.lpop(realCodeKey);
					if (realCode == null || realCode.length() == 0) {
						redisManager.returnResource(jedis);
						throw new Exception("亲爱的对不起，奖品已发放完毕。");
					}
				}
				p = new Prize();
				p.setExchange(false);
				p.setPrizeType(type);
				p.setRealCode(realCode);
				p.setExchangeCode(code);
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(p);
				jedis.set(key, json);
				jedis.sadd(prize123RecordKey(p.getPrizeType().toString()), fansPrizeRecordKey(openid));
				break;
			}
		}
		a = new Award();
		int level = p.getPrizeType().getLevel();
		a.setLevel(level + "");
		a.setDesc("亲爱的粉丝您好！恭喜您获得" + level + "等奖," + p.getPrizeType().getPrizeName());
		a.setCode(code); 
		jedis.sadd(dailyPrizeKey(), code);
		redisManager.returnResource(jedis);
		return a;
	}
	
	@Override
	public Prize recvAward(String openid, String code) throws Exception {
		Jedis jedis = redisManager.getRedisInstance();
		if(openid == null || openid.length() == 0) {
			redisManager.returnResource(jedis);
			throw new Exception("无效请求");
		}
		String key = prizeCodeKey(code);
		String val = jedis.get(key);
		if(val == null || val.length() == 0) {
			redisManager.returnResource(jedis);
			logger.error("用户" + openid  + " 获奖码不存在");
			throw new Exception("获奖码无效");	
		}
		ObjectMapper mapper = new ObjectMapper();
		Prize prize = mapper.readValue(val, Prize.class);
		if (prize.isExchange()) {
			redisManager.returnResource(jedis);
			logger.error("用户" + openid  + " 获奖码已领取");
			throw new Exception("获奖码已领取");
		}
		if (prize.getPrizeType() != PrizeType.LEVEL_6) {
			String helpkey = helpKey(openid,code);
			Set<String> helper = jedis.smembers(helpkey);
			if (helper == null) {
				redisManager.returnResource(jedis);
				logger.error("用户" + openid  + " 点击分享链接的人数不够，还缺2位好友帮您领奖");
				throw new Exception("点击分享链接的人数不够，还缺2位好友帮您领奖 ");
			}
			if (helper.size() < 2) {
				redisManager.returnResource(jedis);
				logger.error("用户" + openid  + "点击分享链接的人数不够，还缺" + (2 - helper.size())  + "位好友帮您领奖 ");
				throw new Exception("点击分享链接的人数不够，还缺" + (2 - helper.size())  + "位好友帮您领奖 ");
			}
		}
		if (prize.getPrizeType().getType() == 0) {
			prize.setExchange(true);
			jedis.set(key, mapper.writeValueAsString(prize));
		}
		redisManager.returnResource(jedis);
		
		return prize;
	}
	
	public void addContact(Contact contact, String code) throws Exception {
		Jedis jedis = redisManager.getRedisInstance();
		String key = prizeCodeKey(code);
		String json = jedis.get(key);
		ObjectMapper mapper = new ObjectMapper();
		Prize prize = mapper.readValue(json, Prize.class);
		prize.setContact(contact);
		prize.setExchange(true);
		jedis.set(key, mapper.writeValueAsString(prize));
		redisManager.returnResource(jedis);
	}
	
	private PrizeType lotty(String openid) {
		Jedis jedis = null;
		try {
			jedis = redisManager.getRedisInstance();;
			int r = new Random().nextInt(100);
			if (r >= 99) {// 1等奖
				String countKey = dailyPrizeCountKey(PrizeType.LEVEL_1);
				String countStr = jedis.get(countKey);
				int count = 0;
				if (countStr != null && countStr.length() > 0) {
					count = Integer.parseInt(countStr);
					if (count > 0) { // 每天抽取一名
						return PrizeType.LEVEL_5;
					}
				}
				
				if(hasPrize123(openid, jedis)) {// 1、2、3奖项每个用户只能中一次
					return PrizeType.LEVEL_5;
				}
				jedis.sadd(fansPrizeRecordKey(openid), PrizeType.LEVEL_1.toString());// 记录用户中奖
				jedis.set(countKey, count + 1 + "");
				return PrizeType.LEVEL_1;
			} else if (r >= 94) {// 2等奖
				String countKey = dailyPrizeCountKey(PrizeType.LEVEL_2);
				String countStr = jedis.get(countKey);
				int count = 0;
				if (countStr != null && countStr.length() > 0) {
					count = Integer.parseInt(jedis.get(countKey));
					if (count > 2) { // 每天抽取2名
						return PrizeType.LEVEL_5;
					}
				}
				if(hasPrize123(openid, jedis)) {// 1、2、3奖项每个用户只能中一次
					return PrizeType.LEVEL_5;
				}
				jedis.sadd(fansPrizeRecordKey(openid), PrizeType.LEVEL_2.toString());// 记录用户中奖
				jedis.set(countKey, count + 1 + "");
				return PrizeType.LEVEL_2;
			} else if (r >= 89) {// 3等奖
				String countKey = dailyPrizeCountKey(PrizeType.LEVEL_3);
				String countStr = jedis.get(countKey);
				int count = 0;
				if (countStr != null && countStr.length() > 0) {
					count = Integer.parseInt(jedis.get(countKey));
					if (count > 30) { // 每天抽取30名
						return PrizeType.LEVEL_5;
					}
				}
				if(hasPrize123(openid, jedis)) {// 1、2、3奖项每个用户只能中一次
					return PrizeType.LEVEL_5;
				}
				jedis.sadd(fansPrizeRecordKey(openid), PrizeType.LEVEL_3.toString());// 记录用户中奖
				jedis.set(countKey, count + 1 + "");
				return PrizeType.LEVEL_3;
			} else if (r >= 70) {// 4等奖
				if(hasPrize1234(openid, jedis)) {//1、2、3奖项每个用户只能中一次，即只要中过，1~4等奖不能再得  拿过一次4等奖，还能拿1~3等奖但不能再拿4等奖
					return PrizeType.LEVEL_5;
				}
				jedis.sadd(fansPrizeRecordKey(openid), PrizeType.LEVEL_4.toString());// 记录用户中奖
				return PrizeType.LEVEL_4;
			} 
			
		} catch (Exception e) {
			return PrizeType.LEVEL_5;
		} finally {
			redisManager.returnResource(jedis);
		}
		return PrizeType.LEVEL_5;
	}
	
	private boolean hasPrize123(String openid, Jedis jedis) {
		Set<String> record = jedis.smembers(fansPrizeRecordKey(openid));
		if (record.contains(PrizeType.LEVEL_1.toString()))
			return true;
		if (record.contains(PrizeType.LEVEL_2.toString()))
			return true;
		if (record.contains(PrizeType.LEVEL_3.toString()))
			return true;
		return false;
	}
	
	private boolean hasPrize1234(String openid, Jedis jedis) {
		Set<String> record = jedis.smembers(fansPrizeRecordKey(openid));
		if (record.contains(PrizeType.LEVEL_1.toString()))
			return true;
		if (record.contains(PrizeType.LEVEL_2.toString()))
			return true;
		if (record.contains(PrizeType.LEVEL_3.toString()))
			return true;
		if (record.contains(PrizeType.LEVEL_4.toString()))
			return true;
		return false;
	}
	
	//============================================
	private String sharedKey(String openid) {
		return "shared:" + openid;
	}
		
	private String rankKey() {
		return "rank:";
	}
		
	private String playerKey(String openid) {
		return "player:" + openid;
	}
	
	private String articleKey() {
		return "article:";
	}
	
	private String helpKey(String openid, String code) {
		return "award_help:" + openid + ":" + code + "";
	}

	// 抽奖
	private String prizeCodeKey(String code) {
		return "prize_code:" + code;
	}

	// 奖券key
	private String couponskey(int price) {
		return "coupons:" + price + ":";
	}

	// 粉丝抽奖资格
	private String prizeConditionKey(String openid) {
		return "prize_condition:" + openid;
	}
	
	// 每日抽奖数量
	private String dailyPrizeCountKey(PrizeType type) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String date = f.format(new Date()); 
		return "prize_daily_count:" + date + ":" + type.toString() + "";
	}
	
	// 粉丝获奖记录
	private String fansPrizeRecordKey(String openid) {
		return "prize_record:" + openid + ":";
	}
	
	// 大奖获取记录
	private String prize123RecordKey(String level) {
		return "prize_level:" + level + ":";
	}
	
	private String dailyPrizeKey() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String date = f.format(new Date());
		return "prize_index:" + date + ":";
	}
}
