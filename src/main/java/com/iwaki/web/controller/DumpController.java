package com.iwaki.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwaki.web.cache.RedisManager;
import com.iwaki.web.model.Contact;
import com.iwaki.web.model.prize.Prize;
import com.iwaki.web.model.prize.PrizeType;

@Controller
@RequestMapping("/dump")
public class DumpController {

	private static final Logger logger = LoggerFactory.getLogger(DumpController.class);

	@Autowired
	private RedisManager redisManager;

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public void dump(HttpServletRequest request, HttpServletResponse resp,
			String date) throws Exception {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			if (date == null || date.length() == 0) {
				d = new Date(System.currentTimeMillis() - 28382132L);// 服务器时差
			} else {
				d = sdf.parse(date);
			}
		} catch (Exception e) {
			resp.getWriter().write("日期格式错误");
		}
		String indexKey = dailyPrizeKey(d);
		Jedis jedis = redisManager.getRedisInstance();
		Set<String> codes = jedis.zrevrange(indexKey, 0, -1);
		ObjectMapper mapper = new ObjectMapper();
		List<Prize> allPrizes = new ArrayList<Prize>();
		List<Prize> materialPrizes = new ArrayList<Prize>();
		long exchangeCount = 0;
		for (String code : codes) {
			String json = jedis.get(prizeCodeKey(code));
			if (json == null || json.length() == 0)
				continue;
			Prize p = mapper.readValue(json, Prize.class);
			// 每天实物奖品的
			allPrizes.add(p);
			if(p.getPrizeType().getType() == 1) { //实物
				materialPrizes.add(p);
			}
			if (p.isExchange())
				exchangeCount ++;
		}
		
		
		String fileName = sdf.format(d) + ".xls";
		File f = new File(fileName);
		// 打开文件
		WritableWorkbook book = Workbook.createWorkbook(new File(fileName));
		// 生成名为“第一页”的工作表，参数0表示这是第一页
		WritableSheet sheet = book.createSheet("实物奖", 0);
		int size = materialPrizes.size();
		//SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日HH点MM分");
		
		String titles[] = {"兑奖码","是否领取","奖品名称","奖品等级","姓名","手机","地址"};
		for (int i = 0; i < titles.length; i ++) {
			Label l = new Label(i, 0, titles[i]);
			sheet.addCell(l);
		}
		
		for (int i = 1; i <= size; i ++) {
			Prize p = materialPrizes.get(i - 1);
			Label l1 = new Label(0, i, p.getExchangeCode());
			Label l2 = new Label(1, i, p.isExchange() ? "领取" : "未领取");
			PrizeType t = p.getPrizeType();
			Label l3 = new Label(2, i, t.getPrizeName());
			Label l4 = new Label(3, i, t.getLevel() + "等奖");
			//Label l5 = new Label(4, i, sdf2.format(new Date(p.getExchangeCodeTimpstamp() - 28382132L)));
			//Label l6 = new Label(5, i, sdf2.format(new Date(p.getRealCodeTimpstamp() - 28382132L)));
			Contact c = p.getContact();
			if (c != null) {
				Label l5 = new Label(4, i, c.getName());
				Label l6 = new Label(5, i, c.getCellphone());
				Label l7 = new Label(6, i, c.getAddr());
				sheet.addCell(l5);
				sheet.addCell(l6);
				sheet.addCell(l7);
			}
			sheet.addCell(l1);
			sheet.addCell(l2);
			sheet.addCell(l3);
			sheet.addCell(l4);
			//sheet.addCell(l5);
			//sheet.addCell(l6);
		}
		
		WritableSheet sheet2 = book.createSheet("统计", 1);
		String titles2[] = {"领奖总人数","已兑换人数","当日游戏人数"};
		for (int i = 0; i < titles2.length; i ++) {
			Label l = new Label(i, 0, titles2[i]);
			sheet2.addCell(l);
		}
		
		Label l1 = new Label(0, 1, allPrizes.size() + "");
		Label l2 = new Label(1, 1, exchangeCount + "");
		Label l3 = new Label(2, 1, jedis.get(dailyPlayerCountKey(d)));
		sheet2.addCell(l1);
		sheet2.addCell(l2);
		sheet2.addCell(l3);
		
		book.write();
		book.close();

		long fileLength = f.length();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		resp.setHeader("Content-type", "text/html;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Content-disposition", "attachment; filename="
				+ new String(fileName.getBytes("utf-8"), "ISO8859-1"));
		resp.setHeader("Content-Length", String.valueOf(fileLength));
		bis = new BufferedInputStream(new FileInputStream(fileName));
		bos = new BufferedOutputStream(resp.getOutputStream());
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		bis.close();
		bos.close();
		f.delete();
		redisManager.returnResource(jedis);
	}

	private String prizeCodeKey(String code) {
		return "prize_code:" + code;
	}

	private String dailyPrizeKey(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String s = f.format(date);
		return "daily_prize_index:" + s + ":";
	}
	
	private String dailyPlayerCountKey(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String s = f.format(date);
		return "daily_player_count:" + s + ":";
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
