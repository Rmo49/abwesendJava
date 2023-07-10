package com.rmo.abwesend.model;

import java.util.Date;

import com.rmo.abwesend.util.Config;

//import javafx.beans.property.SimpleStringProperty;

/**
 * Ein Trace Eintrag in der DB.
 * @author Ruedi
 *
 */
public class TraceDb {

//    private final SimpleStringProperty datum;
    private String datum;
//    private final SimpleDateFormat datum;
    private String wert;

//	private Date datum;
//	private String wert;

    public TraceDb(Date date, String wert) {
    	this.datum = new String(Config.sdfDb.format(date));
    	this.wert = new String(wert);
    }

	public String getDatum() {
		return datum;
	}
	public void setDatum(String datum) {
		this.datum = datum;
	}
	public String getWert() {
		return wert;
	}
	public void setWert(String wert) {
		this.wert = wert;
	}

}
