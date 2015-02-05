package com.goddess.service.model;

import java.util.Date;

public class Order {

	private String user;
	
	private String sid = "";
	private Date time = new Date();
	private int price = 0;

	private String pdSid = "";
	private String pdName = "";
	private String pdDesc = "";

	private String mastarSid = "";
	private String mastarName = "";
	private Date mastarDay = new Date();
	private int masterHour = 0;
	private String masterAvatar = "";

	private String remark = "";

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getPdSid() {
		return pdSid;
	}

	public void setPdSid(String pdSid) {
		this.pdSid = pdSid;
	}

	public String getPdName() {
		return pdName;
	}

	public void setPdName(String pdName) {
		this.pdName = pdName;
	}

	public String getPdDesc() {
		return pdDesc;
	}

	public void setPdDesc(String pdDesc) {
		this.pdDesc = pdDesc;
	}

	public String getMastarSid() {
		return mastarSid;
	}

	public void setMastarSid(String mastarSid) {
		this.mastarSid = mastarSid;
	}

	public String getMastarName() {
		return mastarName;
	}

	public void setMastarName(String mastarName) {
		this.mastarName = mastarName;
	}

	public Date getMastarDay() {
		return mastarDay;
	}

	public void setMastarDay(Date mastarDay) {
		this.mastarDay = mastarDay;
	}

	public int getMasterHour() {
		return masterHour;
	}

	public void setMasterHour(int masterHour) {
		this.masterHour = masterHour;
	}

	public String getMasterAvatar() {
		return masterAvatar;
	}

	public void setMasterAvatar(String masterAvatar) {
		this.masterAvatar = masterAvatar;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
