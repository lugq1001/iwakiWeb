package com.iwaki.web.service;

import java.util.ArrayList;

import com.iwaki.web.model.Award;
import com.iwaki.web.model.Contact;
import com.iwaki.web.model.ScoreRank;
import com.iwaki.web.model.prize.Prize;

public interface GameService {

	public void incrSharedCount(String openid, String gamerIp);

	// 排行榜
	public long incrRank(ScoreRank scoreRank);
	
	// 获取排行榜跟个人排名
	public ArrayList<ScoreRank> topRanks();
	
	// 接受条款
	public void acceptArticle(String openid);
	
	// 是否接受条款
	public boolean hasAcceptArticle(String openid);
	
	// 帮忙搬礼品
	public boolean helpAward(String ip,String openid,String code);
	
	// 游客抽奖
	public Award getGuestAward(String ip) throws Exception;
	
	// 玩家抽奖
	public Award getFansAward(String openid) throws Exception;
	
	
	// 领奖
	public Prize recvAward(String openid, String code) throws Exception;
	
	public void addContact(Contact contact, String code) throws Exception;
}












