package com.iwaki.web.model;

public class OutputMessage {

	public static String createGameResp(String toUser, String fromUser,
			String content) {
		StringBuffer replyInfo = new StringBuffer();
		long CreateTime = System.currentTimeMillis() / 1000;
		replyInfo.append("<xml>");
		replyInfo.append("<ToUserName>");
		replyInfo.append("<![CDATA[").append(toUser).append("]]>");
		replyInfo.append("</ToUserName>");
		replyInfo.append("<FromUserName>");
		replyInfo.append("<![CDATA[").append(fromUser).append("]]>");
		replyInfo.append("</FromUserName>");
		replyInfo.append("<CreateTime>");
		replyInfo.append(CreateTime);
		replyInfo.append("</CreateTime>");
		replyInfo.append("<MsgType>");
		replyInfo.append("<![CDATA[text]]>");
		replyInfo.append("</MsgType>");
		replyInfo.append("<Content>");
		replyInfo.append("<![CDATA[" + content + "]]>");
		replyInfo.append("</Content>");
		replyInfo.append("</xml>");
		return replyInfo.toString();
	}
	
	public static String createImgResp(String toUser, String fromUser,
			String title,String desc,String img,String url) {
		StringBuffer replyInfo = new StringBuffer();
		long CreateTime = System.currentTimeMillis() / 1000;
		replyInfo.append("<xml>");
		replyInfo.append("<ToUserName>");
		replyInfo.append("<![CDATA[").append(toUser).append("]]>");
		replyInfo.append("</ToUserName>");
		replyInfo.append("<FromUserName>");
		replyInfo.append("<![CDATA[").append(fromUser).append("]]>");
		replyInfo.append("</FromUserName>");
		replyInfo.append("<CreateTime>");
		replyInfo.append(CreateTime);
		replyInfo.append("</CreateTime>");
		replyInfo.append("<MsgType>");
		replyInfo.append("<![CDATA[news]]>");
		replyInfo.append("</MsgType>");
		replyInfo.append("<ArticleCount>1</ArticleCount>");
		replyInfo.append("<Articles>");
		replyInfo.append("<item>");
		replyInfo.append("<Title>");
		replyInfo.append("<![CDATA[").append(title).append("]]>");
		replyInfo.append("</Title>");
		replyInfo.append("<Description>");
		replyInfo.append("<![CDATA[").append(desc).append("]]>");
		replyInfo.append("</Description>");
		replyInfo.append("<PicUrl>");
		replyInfo.append("<![CDATA[").append(img).append("]]>");
		replyInfo.append("</PicUrl>");
		replyInfo.append("<Url>");
		replyInfo.append("<![CDATA[").append(url).append("]]>");
		replyInfo.append("</Url>");
		replyInfo.append("</item>");
		replyInfo.append("</Articles>");
		replyInfo.append("</xml>");
		return replyInfo.toString();
	}
	
	public static String createAwardContent() {
		return "1.请点击【我要领奖】。\n"
				+ "2.出现兑奖界面后，请根据提示准确输入您的【兑奖码】。\n"
				+ "3.输入正确的兑奖码后，系统会自动回复相关信息，表示领奖成功。\n"
				+ "4.当奖品为1-3等奖时，系统还会提示输入您的联系方式和邮寄地址，请务必输入正确，避免错过奖品。"
				+ "注：一等奖是原装进口的限量款，预计在2014年11月20日左右为您寄出，敬请谅解。\n"
				+ "5.当奖品为4-6等奖时，系统会告知奖品使用的小贴士，请您留意。\n"
				+ "国王祝您天天有好运^_^";

	}
	
	public static String createTipsContent() {
		return "1.国王对心爱的粉丝们的慷慨你想象不到。30天天天送大礼，100%有奖。\n"
				+ "2.邀请亲朋好友们一起来添加怡万家的公众号！一起获得国王一波又一波的活动与奖品。\n"
				+ "3.国王宝藏无限多，所以不怕你挖空。挖的越多中奖机会越多！快去再玩一次吧！\n"
				+ "4. 知道得大奖的秘诀么。。。恩。。国王不会告诉你这与你的分数有关。嘘。。。。。。快破纪录去吧！";
	}
}
