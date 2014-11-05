package com.iwaki.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwaki.web.model.Token;
import com.iwaki.web.wx.Button;
import com.iwaki.web.wx.Menu;

public class WechatTest {

	private static String AppID = "wxcfe285f2fd512b64";
	private static String AppSecret = "093ce0056968449553fd0f38fb3491e3";

	public static void main(String args[]) throws Exception {
		//getAccess_token();
		createButton();
	}
	
	public static void createButton() {
		String token = "G9AKSs8RvsdC9ylNdMbM9uHa8FGrIQR9VGr-DrajFOJcsVE-UQr7Fd89dAoHyxaOVrsBeQiPSQ0nxYv_HLA6U23GhSRw4LAi3ecg5kyXOEY";
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + token;
		PrintWriter out = null;
		String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.write(getButtonsJson());
            out.flush();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
	}

	public static Token getAccess_token() {
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
            System.out.println(result);
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

	private static String getButtonsJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		ArrayList<Button> buttons = new ArrayList<Button>();

		Button b1 = new Button();
		b1.setName("我要领奖");
		b1.setType("click");
		b1.setKey("award");

		Button b2 = new Button();
		b2.setName("宝藏攻略");

		ArrayList<Button> subs = new ArrayList<Button>();
		Button sub1 = new Button();
		sub1.setType("view");
		sub1.setName("领奖方式");
		sub1.setUrl("http://112.65.246.168:81/game/award_way.html");
		Button sub2 = new Button();
		sub2.setType("view");
		sub2.setName("中奖贴士");
		sub2.setUrl("http://112.65.246.168:81/game/award_tips.html");
		Button sub3 = new Button();
		sub3.setType("view");
		sub3.setName("奖品一览");
		sub3.setUrl("http://112.65.246.168:81/game/award.html");
		Button sub4 = new Button();
		sub4.setType("view");
		sub4.setName("游戏规则");
		sub4.setUrl("http://112.65.246.168:81/game/rule.html");
		subs.add(sub1);
		subs.add(sub2);
		subs.add(sub3);
		subs.add(sub4);
		b2.setSub_button(subs);

		Button b3 = new Button();
		b3.setName("国王宝藏");
		b3.setType("click");
		b3.setKey("game");

		buttons.add(b1);
		buttons.add(b2);
		buttons.add(b3);

		Menu menu = new Menu();
		menu.setButton(buttons);

		String json = mapper.writeValueAsString(menu);
		System.out.println(json);
		return json;
	}
}
