package com.rmo.abwesend.model;


public class Match {

	private int spielerId = -1; // die unique ID
	private String datum;
	private String spielTyp;  // E oder D

	public int getSpielerId() {
		return spielerId;
	}
	public void setSpielerId(int spielerId) {
		this.spielerId = spielerId;
	}
	public String getDatum() {
		return datum;
	}
	public String getZeit() {
		String[] dates = datum.split(" ");
		return dates[1];
	}
	public void setDatum(String datum) {
		this.datum = datum;
	}
	public String getSpielTyp() {
		return spielTyp;
	}
	public void setSpielTyp(String spielTyp) {
		this.spielTyp = spielTyp;
	}

}
