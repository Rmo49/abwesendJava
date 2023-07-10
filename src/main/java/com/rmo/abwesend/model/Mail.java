package com.rmo.abwesend.model;

/**
 * Für speichern des mail-Inhaltes (Text).
 */
public class Mail {

	private int mailId;
	private String betreff;
	private String text;


	public int getMailId() {
		return mailId;
	}
	public void setMailId(int mailId) {
		this.mailId = mailId;
	}
	public String getBetreff() {
		return betreff;
	}
	public void setBetreff(String betreff) {
		this.betreff = betreff;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
