package com.v2tech.vo;

import android.graphics.Bitmap;

import com.v2tech.util.DateUtil;

public class ContactConversation extends Conversation {

	private User u;

	public void setDateLong(String dateLong) {
		this.dateLong = dateLong;
	}

	public ContactConversation(User u) {
		super();
		this.u = u;
		if (u != null) {
			this.mExtId = u.getmUserId();
			this.mType = TYPE_CONTACT;
		}
	}

	@Override
	public String getName() {
		if (u != null) {
			return u.getName();
		}
		return super.getName();
	}

	@Override
	public CharSequence getMsg() {
		if (msg != null) {
			return msg;
		}
		return super.getMsg();
	}

	@Override
	public String getDate() {
		if (dateLong != null) {
			return DateUtil.getStringDate(Long.valueOf(dateLong));
		}
		return super.getDate();
	}

	@Override
	public String getDateLong() {
		if (dateLong != null) {
			return dateLong;
		}
		return super.getDateLong();
	}

	public void setMsg(CharSequence msg) {
		this.msg = msg;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Bitmap getAvatar() {
		if (u != null) {
			return u.getAvatarBitmap();
		} else {
			return null;
		}
	}

	public void updateUser(User u) {
		this.u = u;
	}

	public long getUserID() {
		if (u != null)
			return u.getmUserId();
		else
			return -1;
	}

	public User getU() {
		return u;
	}

}
