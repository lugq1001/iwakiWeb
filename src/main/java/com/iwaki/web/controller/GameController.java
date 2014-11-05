package com.iwaki.web.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwaki.web.model.Award;
import com.iwaki.web.model.Contact;
import com.iwaki.web.model.ScoreRank;
import com.iwaki.web.model.prize.Prize;
import com.iwaki.web.model.prize.PrizeType;
import com.iwaki.web.resp.AwardResp;
import com.iwaki.web.resp.GetAwardResp;
import com.iwaki.web.resp.Resp;
import com.iwaki.web.resp.ScoreResp;
import com.iwaki.web.service.GameService;
import com.iwaki.web.util.Util;

@Controller
@RequestMapping("/game")
public class GameController {

	private static final Logger logger = LoggerFactory.getLogger(GameController.class);
	
	@Autowired
	private GameService gameService;
	
	@Value("${game.url}")
	private String gameURL;
	
	@Value("${web.url}")
	private String serverURL;
	
	@Value("${app.magic.key}")
	private String magicKey;
	
	/**
	 * 游戏链接
	 * @param request
	 * @param openid 分享者openid
	 * @return
	 */
/*	@RequestMapping(value = "/share/{openid}", method = RequestMethod.GET)
	public String share(HttpServletRequest request, @PathVariable("openid") String openid) {
		if (openid != null && openid.length() > 0) {
			String ip = request.getLocalAddr();
			logger.info("用户ip:" + ip  + " 从分享游戏链接进入,分享者:" + openid);
			
			// 增加分享次数
			gameService.incrSharedCount(openid,ip);	
		}
		return "redirect:" + gameURL;
	}
	*/
	
	@RequestMapping(value = "/score", method = RequestMethod.POST)
	@ResponseBody
	public ScoreResp score(HttpServletRequest request,HttpServletResponse resp, Long score, String openid, String nickname,
			String avatar, String sign) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		ScoreResp scoreResp = new ScoreResp();
		String md5 = Util.getMD5Str(openid + avatar + magicKey);
		if (!md5.equals(sign)) {
			scoreResp.setResult(false);
			scoreResp.setDesc("认证失败");
			return scoreResp;
		}
		
		try {
			nickname = new String(nickname.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
		
		if (score == null ) {
			score = 0L;
		}
		if (openid == null || openid.length() == 0) {
			String ip = request.getRemoteAddr();
			openid = ip;
			//UUID uuid= UUID.randomUUID();
			//openid = uuid.toString();
			nickname = "游客";
			avatar = "";
		}
		
		ScoreRank scoreRank = new ScoreRank();
		scoreRank.setAvatar(avatar);
		scoreRank.setScore(score);
		scoreRank.setOpenid(openid);
		scoreRank.setNickname(nickname);
		long rank = gameService.incrRank(scoreRank);
		scoreResp.setResult(true);
		scoreResp.setDesc("");
		scoreResp.setMyRank(rank);
		scoreResp.setRanks(gameService.topRanks());
		boolean hasAcceptArticle = gameService.hasAcceptArticle(openid);
		scoreResp.setAtl(hasAcceptArticle);
		return scoreResp;
	}
	
	@RequestMapping(value = "/accept", method = RequestMethod.POST)
	@ResponseBody
	public boolean accept(HttpServletRequest request,HttpServletResponse resp, String openid) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		if (openid == null || openid.length() == 0) {
			return false;
		}
		gameService.acceptArticle(openid);
		return true;
	} 
	
	@RequestMapping(value = "/award", method = RequestMethod.POST)
	@ResponseBody
	public AwardResp award(HttpServletRequest request,HttpServletResponse resp, String openid) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		AwardResp awardResp = new AwardResp();
		awardResp.setResult(true);
		awardResp.setDesc("");
		Award a = null;
		try {
			if (openid != null && openid.length() > 0) {
				a = gameService.getFansAward(openid);
				String url = serverURL + "game/help?help=" + openid + "&code=" + a.getCode();
				awardResp.setHelpUrl(url);
			} else {
				a = gameService.getGuestAward(request.getRemoteAddr());
			}
		} catch (Exception e) {
			awardResp.setResult(false);
			awardResp.setDesc(e.getMessage());
			logger.error(e.getMessage());
			//e.printStackTrace();
			return awardResp;
		}
		awardResp.setAward(a);
		return awardResp;
	} 

	@RequestMapping(value = "/help", method = RequestMethod.GET)
	public String help(HttpServletRequest request,HttpServletResponse resp, String help,String code) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		gameService.helpAward(request.getRemoteAddr(), help,code);
		return "redirect:" + gameURL;
	} 
	
	/**
	 * 兑换奖品
	 * @param request
	 * @param resp
	 * @param openid
	 * @param code 兑奖码
	 * @return
	 */
	@RequestMapping(value = "/getAward", method = RequestMethod.POST)
	@ResponseBody
	public GetAwardResp getAward(HttpServletRequest request,HttpServletResponse resp,String openid,String code) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		GetAwardResp getAwardResp = new GetAwardResp();
		try {
			Prize p = gameService.recvAward(openid, code);
			getAwardResp.setResult(true);
			getAwardResp.setDesc("");
			getAwardResp.setAward_name(p.getPrizeType().getPrizeName());
			getAwardResp.setCode(p.getRealCode());
			getAwardResp.setPrice(p.getPrizeType().getPrice() + "");
			if(p.getPrizeType() == PrizeType.LEVEL_1)
				getAwardResp.setTips("*此奖品为原装进口，预计在2014年11月20日左右为你寄出，故请耐心等待它漂洋过海来到你身边。谢谢理解。");
			else
				getAwardResp.setTips("");
			getAwardResp.setType(p.getPrizeType().getType() + "");
		} catch (Exception e) {
			getAwardResp.setResult(false);
			getAwardResp.setDesc(e.getMessage());
			logger.error(e.getMessage());
			//e.printStackTrace();
		}
		return getAwardResp;
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.POST)
	@ResponseBody
	public Resp contact(HttpServletRequest request,HttpServletResponse resp, String code, String name,String cellphone, String addr) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		Resp r = new Resp();
		if (code == null || code.length() == 0) {
			r.setResult(false);
			r.setDesc("输入信息不完整，请重新输入");
			return r;
		}
		if (name == null || name.length() == 0) {
			r.setResult(false);
			r.setDesc("输入信息不完整，请重新输入");
			return r;
		}
		if (cellphone == null || cellphone.length() == 0) {
			r.setResult(false);
			r.setDesc("输入信息不完整，请重新输入");
			return r;
		}
		if (addr == null || addr.length() == 0) {
			r.setResult(false);
			r.setDesc("输入信息不完整，请重新输入");
			return r;
		}
		Contact c= new Contact();
		c.setAddr(addr);
		c.setCellphone(cellphone);
		c.setName(name);
		try {
			gameService.addContact(c, code);
			r.setResult(true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			r.setResult(false);
			r.setDesc(e.getMessage());
		}
		return r;
	}
}










































