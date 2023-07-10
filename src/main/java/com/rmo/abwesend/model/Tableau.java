package com.rmo.abwesend.model;

/**
 * Ein Tableau des Turniers.
 * @author Ruedi
 *
 */
public class Tableau {

	private int id;
	private String bezeichnung;
	private String position;
	private String konkurrenz;	// die offizielle Bezeichnung von Sisstennis

	public Tableau() {
		// default
	}

	public Tableau(int id, String bezeichnung, String positon) {
		this.id = id;
		this.bezeichnung = bezeichnung;
		this.position = positon;
	}

	public Tableau(int id, String bezeichnung, String positon, String konkurrenz) {
		this.id = id;
		this.bezeichnung = bezeichnung;
		this.position = positon;
		this.konkurrenz = konkurrenz;
	}

	//--- getter and setter
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = Integer.getInteger(id);
	}
	public String getBezeichnung() {
		return bezeichnung;
	}
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getKonkurrenz() {
		return konkurrenz;
	}
	public void setKonkurrenz(String konkurrenz) {
		this.konkurrenz = konkurrenz;
	}


}
