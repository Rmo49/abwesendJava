package com.rmo.abwesend.model;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.rmo.abwesend.util.Config;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MatchDataTest extends TestCase {
	
	static final long stunde = 60*60*1000;
	static final long tag = 24*60*60*1000;
	
	static private List<SpielerKurz> spielerListe;
	
	public MatchDataTest(String name) {
		super(name);
		// Auto-generated constructor stub
	}
	
	/**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MatchDataTest.class );
    }

    /**
     * Alle Matches löschen.
     */
    @BeforeClass
	public void test1_DeleteAll() {
    	if (DbConnectionTest.connectDb() >= 1) {
			try {
				MatchData.instance().deleteAllRow();
			}
	    	catch (SQLException ex) {
	    		fail(ex.getMessage());
	    	}
			try {
				spielerListe = SpielerData.instance().readAllKurz();
			}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}

				
    	}
	}

    
    /**
     * Spieler und Datum im Range
     */
	public void test2_Add1() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			Match match = new Match();
    			match.setSpielerId(spielerListe.get(0).getId());
    			Date date = Config.turnierBeginDatum;
    			date.setTime(date.getTime() + 9*stunde);
     			match.setDatum(Config.sdfDb.format(date));
    			match.setSpielTyp("E");
    			MatchData.instance().add(match);
    		}
	    	catch (SQLException ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}

	/**
     * Spieler und Datum im Range
     */
	public void test2_Add2() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			Match match = new Match();
    			match.setSpielerId(spielerListe.get(1).getId());
    			Date date = Config.turnierBeginDatum;
    			date.setTime(date.getTime() + 9*stunde + (30*60*1000));
    			match.setDatum(Config.sdfDb.format(date));
    			match.setSpielTyp("E");
    			MatchData.instance().add(match);
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}

	/**
     * Spieler und Datum im Range
     */
	public void test2_Add3() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			Match match = new Match();
    			match.setSpielerId(spielerListe.get(0).getId());
    			Date date = Config.turnierBeginDatum;
    			date.setTime(date.getTime() + 2*tag + 12*stunde);
    			match.setDatum(Config.sdfDb.format(date));
    			match.setSpielTyp("D");
    			MatchData.instance().add(match);
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}

	/**
     * Spieler und Datum im Range
     */
	public void test2_Add4() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			Match match = new Match();
    			match.setSpielerId(spielerListe.get(0).getId());
    			Date date = Config.turnierBeginDatum;
    			date.setTime(date.getTime() + 2*tag + 12*stunde);
    			match.setDatum(Config.sdfDb.format(date));
    			match.setSpielTyp("D");
    			MatchData.instance().add(match);
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}

	/**
     * Spieler 4 einen Match dazufügen
     */
	public void test2_Add5() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			Match match = new Match();
    			match.setSpielerId(spielerListe.get(4).getId());
    			Date date = Config.turnierBeginDatum;
    			date.setTime(date.getTime() + 2*tag + 12*stunde);
    			match.setDatum(Config.sdfDb.format(date));
    			match.setSpielTyp("E");
    			MatchData.instance().add(match);
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}

	/**
     * Spieler und Datum im Range
     */
	public void test4_read1() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			List<Match> matches = MatchData.instance().readAll(spielerListe.get(0).getId());
    			Match match = matches.get(0);
    			assertEquals(spielerListe.get(0).getId(), match.getSpielerId());
    			assertNotNull(match.getDatumZeit());		
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}

	
	
}
