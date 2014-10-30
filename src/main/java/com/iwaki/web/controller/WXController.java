package com.iwaki.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WXController {

	private static final Logger logger = LoggerFactory.getLogger(WXController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String checkUrl(String signature,String timestamp, String nonce, String echostr) {
		logger.info("signature:" + signature);
		logger.info("timestamp:" + timestamp);
		logger.info("nonce:" + nonce);
		logger.info("echostr:" + echostr);
		return echostr;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public String wxtest(HttpServletRequest request) {
		logger.info("body:" + request.getParameterMap());
		return "";
	}
	
}










































