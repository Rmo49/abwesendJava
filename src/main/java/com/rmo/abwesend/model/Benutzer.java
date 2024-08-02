package com.rmo.abwesend.model;

/**
 * Der aktuelle Benutzer.
 * 
 * @author ruedi
 *
 */
public class Benutzer {

	private String name;
	private String passwort;

	public Benutzer(String name, String passwort) {
		this.name = name;
		this.passwort = passwort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswort() {
		return passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
}
