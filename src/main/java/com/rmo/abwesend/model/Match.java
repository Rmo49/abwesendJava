package com.rmo.abwesend.model;

public class Match {

	private int spielerId = -1; // die unique ID
	// Form des Datums: "2024-08-16 19:00"
	private String datum;
	private String spielTyp; // E oder D

	public int getSpielerId() {
		return spielerId;
	}

	public void setSpielerId(int spielerId) {
		this.spielerId = spielerId;
	}

	public String getDatumZeit() {
		return datum;
	}
	
	public String getDatum() {
		String[] dates = datum.split(" ");
		return dates[0];
	}


	public String getZeit() {
		String[] dates = datum.split(" ");
		return dates[1];
	}
	
	public int getZeitAsInt() {
		String zeit = getZeit();
		String[] zeit1 = zeit.split(":");
		try {
			return Integer.parseInt(zeit1[0]);
		}
		catch (NumberFormatException ex) {
			return 0;
		}
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
