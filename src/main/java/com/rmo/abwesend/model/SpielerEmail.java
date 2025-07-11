package com.rmo.abwesend.model;

/**
 * Für das einlesen von e-mails. Wird in der Liste gebraucht.
 * 
 * @author Ruedi
 *
 */
public class SpielerEmail {
	public String name;
	public String vorname;
	public String email;
	
	/**
	 * Neues Objekt anlegen mit Name, Vorname, e-mail
	 * @param data
	 */
	public SpielerEmail(String[] data) {
		if (data.length >= 3) {
			name = data[0];
			vorname = data[1];
			email = data[2];
		}
		else {
			name = "";
			vorname = "";
			email = "";
		}
	}
	
	
	/**
	 * @return the vorname
	 */
	public String getVorname() {
		return vorname;
	}


	/**
	 * @param vorname the vorname to set
	 */
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


}
