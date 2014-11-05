package com.iwaki.web.model.prize;

import com.iwaki.web.model.Contact;

/**
 * 奖品
 * @author Administrator
 *
 */
public class Prize {

	// 兑换码
	private String exchangeCode;
	
	// 优惠券
	private String realCode;
	
	// 是否领取
	private boolean exchange;
	
	// 奖品类型
	private PrizeType prizeType;
	
	// 联系方式
	private Contact contact;
	
	public static Prize makeLevel6Prize(String exchangeCode, String realCode) {
		Prize p = new Prize();
		p.setExchangeCode(exchangeCode);
		p.setPrizeType(PrizeType.LEVEL_6);
		p.setRealCode(realCode);
		return p;
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

	public boolean isExchange() {
		return exchange;
	}

	public void setExchange(boolean exchange) {
		this.exchange = exchange;
	}

	public PrizeType getPrizeType() {
		return prizeType;
	}

	public void setPrizeType(PrizeType prizeType) {
		this.prizeType = prizeType;
	}

	public String getRealCode() {
		return realCode;
	}

	public void setRealCode(String realCode) {
		this.realCode = realCode;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	
}











