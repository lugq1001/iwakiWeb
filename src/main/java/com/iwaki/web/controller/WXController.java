package com.iwaki.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.iwaki.web.model.InputMessage;
import com.iwaki.web.model.MessageType;
import com.iwaki.web.model.OutputMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Controller
public class WXController {

	private static final Logger logger = LoggerFactory.getLogger(WXController.class);

	@Value("${game.url}")
	private String gameURL;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String checkUrl(String signature, String timestamp, String nonce,
			String echostr) {
		logger.info("signature:" + signature);
		logger.info("timestamp:" + timestamp);
		logger.info("nonce:" + nonce);
		logger.info("echostr:" + echostr);
		return echostr;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public void wx(HttpServletRequest request,HttpServletResponse resp) throws IOException {
		logger.info("body:" + request.getParameterMap());
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Content-type", "text/html;charset=UTF-8");  
		OutputStream ps = resp.getOutputStream();  
		// 处理接收消息
		ServletInputStream in = request.getInputStream();
		// 将POST流转换为XStream对象
		XStream xs = new XStream(new DomDriver());
		// 将指定节点下的xml节点数据映射为对象
		xs.alias("xml", InputMessage.class);
		// 将流转换为字符串
		StringBuilder xmlMsg = new StringBuilder();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			xmlMsg.append(new String(b, 0, n, "UTF-8"));
		}
		// 将xml内容转换为InputMessage对象
		InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());
		// 取得消息类型
		String msgType = inputMsg.getMsgType();
		logger.info("msgType：" + msgType);
		// 根据消息类型获取对应的消息内容
		if (msgType.equals(MessageType.Text.toString())) {
			// 文本消息
			logger.info("开发者微信号：" + inputMsg.getToUserName());
			logger.info("发送方帐号：" + inputMsg.getFromUserName());
			logger.info("消息创建时间：" + inputMsg.getCreateTime());
			logger.info("消息内容：" + inputMsg.getContent());
			logger.info("消息Id：" + inputMsg.getMsgId());
		} else if (msgType.equals(MessageType.Event.toString())) {
			logger.info("开发者微信号：" + inputMsg.getToUserName());
			logger.info("发送方帐号：" + inputMsg.getFromUserName());
			logger.info("消息创建时间：" + inputMsg.getCreateTime());
			logger.info("事件：" + inputMsg.getEvent());
			logger.info("事件Key：" + inputMsg.getEventKey());
			
			String eventKey = inputMsg.getEventKey();
			if (eventKey.equals("game")) {// 游戏响应
				String xml = OutputMessage.createImgResp(inputMsg.getFromUserName(), inputMsg.getToUserName(), 
						"iwaki国王的宝藏", "点击开始游戏", 
						"http://112.65.246.168:81/images/wx_game.jpg", 
						gameURL + "?openid=" + inputMsg.getFromUserName() + "&");
				logger.info("响应：" + xml);
				ps.write(xml.getBytes("UTF-8"));  
			} else if (eventKey.equals("sub_1")) {// 领奖方式
				String xml = OutputMessage.createGameResp(inputMsg.getFromUserName(), inputMsg.getToUserName(),
						OutputMessage.createAwardContent());
				logger.info("响应：" + xml);
				ps.write(xml.getBytes("UTF-8"));  
			} else if (eventKey.equals("sub_2")) {// 中奖贴士
				String xml = OutputMessage.createGameResp(inputMsg.getFromUserName(), inputMsg.getToUserName(),
						OutputMessage.createTipsContent());
				logger.info("响应：" + xml);
				ps.write(xml.getBytes("UTF-8"));  
			} else if (eventKey.equals("award")) {// 我要领奖
				String xml = OutputMessage.createImgResp(inputMsg.getFromUserName(), inputMsg.getToUserName(), 
						"我要领奖", "点击进入领奖'30天天天送大礼活动:2014年11月7日至12月6日'", 
						"http://112.65.246.168:81/images/wx_award_img.jpg", 
						"http://112.65.246.168:81/game/contact.html?openid=" + inputMsg.getFromUserName() + "&");
				logger.info("响应：" + xml);
				ps.write(xml.getBytes("UTF-8"));  
			}
		}
		ps.write("".getBytes("UTF-8"));  
	}
}
