package com.iwaki.web.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwaki.web.model.Token;

@Service
public class WXServiceImpl implements WXService {
	
	private static final Logger logger = LoggerFactory.getLogger(WXServiceImpl.class);
	
	@Value("${app.wx.appid}")
	private String AppID;
	
	@Value("${app.wx.appsecret}")
	private String AppSecret;
	
	private static String accessToken = "";
	
	private static long expiresTime = 0L;
	
	
	@Override
	public String refreshAccessToken() {
		long time = System.currentTimeMillis();
		if(time - expiresTime < 0) {
			return accessToken;
		}  
		logger.info("access_token过期");
		Token token = getAccess_token();
		accessToken = token.getAccess_token();
		expiresTime = time + token.getExpires_in() * 1000;
		return accessToken;
	}

	
	private Token getAccess_token() {
		Token token = null;
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
				+ AppID + "&secret=" + AppSecret;
		String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            logger.info("获取access_token:" + result);
            ObjectMapper mapper = new ObjectMapper();
            token = mapper.readValue(result, Token.class);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) { in.close(); }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return token;
	}
}
