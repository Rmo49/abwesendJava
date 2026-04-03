package com.rmo.abwesend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rmo.abwesend.util.Config;


@TestMethodOrder(MethodOrderer.MethodName.class)
public class MatchDataTest {

	static final long stunde = 60 * 60 * 1000;
	static final long tag = 24 * 60 * 60 * 1000;

	static private List<SpielerKurz> spielerListe;

	/**
	 * Alle Matches löschen.
	 */
	@BeforeAll
	static void test1_DeleteAll() {
		DbConnectionTest.setToLocal();
		
		try {
			MatchData.instance().deleteAllRow();
		} catch (SQLException ex) {
			fail(ex.getMessage());
		}
		try {
			if (SpielerDataTest.saveAllSpieler().size() > 0) {
				spielerListe = SpielerData.instance().readAllKurz();
				if (spielerListe.size() < 1) {
					fail("Keine Spielerdaten");
				}
			}
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	public void test2_Add1() {
		try {
			Match match = new Match();
			match.setSpielerId(spielerListe.get(0).getId());
			Date date = Config.turnierBeginDatum;
			date.setTime(date.getTime() + 9 * stunde);
			match.setDatum(Config.sdfDb.format(date));
			match.setSpielTyp("E");
			MatchData.instance().add(match);
		} catch (SQLException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	public void test2_Add2() {
		try {
			Match match = new Match();
			match.setSpielerId(spielerListe.get(1).getId());
			Date date = Config.turnierBeginDatum;
			date.setTime(date.getTime() + 9 * stunde + (30 * 60 * 1000));
			match.setDatum(Config.sdfDb.format(date));
			match.setSpielTyp("E");
			MatchData.instance().add(match);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	public void test2_Add3() {
		try {
			Match match = new Match();
			match.setSpielerId(spielerListe.get(0).getId());
			Date date = Config.turnierBeginDatum;
			date.setTime(date.getTime() + 2 * tag + 12 * stunde);
			match.setDatum(Config.sdfDb.format(date));
			match.setSpielTyp("D");
			MatchData.instance().add(match);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	public void test2_Add4() {
		try {
			Match match = new Match();
			match.setSpielerId(spielerListe.get(0).getId());
			Date date = Config.turnierBeginDatum;
			date.setTime(date.getTime() + 2 * tag + 12 * stunde);
			match.setDatum(Config.sdfDb.format(date));
			match.setSpielTyp("D");
			MatchData.instance().add(match);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler 4 einen Match dazufügen
	 */
	@Test
	public void test2_Add5() {
		try {
			Match match = new Match();
			match.setSpielerId(spielerListe.get(4).getId());
			Date date = Config.turnierBeginDatum;
			date.setTime(date.getTime() + 2 * tag + 12 * stunde);
			match.setDatum(Config.sdfDb.format(date));
			match.setSpielTyp("E");
			MatchData.instance().add(match);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	public void test4_read1() {
		try {
			List<Match> matches = MatchData.instance().readAll(spielerListe.get(0).getId());
			Match match = matches.get(0);
			assertEquals(spielerListe.get(0).getId(), match.getSpielerId());
			assertNotNull(match.getDatumZeit());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	//----- Hilfsmethoden

	/**
	 * Ein Match einem Spieler zuordnen
	 * @param spielerId
	 */
	public static void addMatchOfSpieler(int spielerId) {
		try {
			Match match = new Match();
			match.setSpielerId(spielerId);
			Date date = Config.turnierBeginDatum;
			date.setTime(date.getTime() + 9 * stunde + (30 * 60 * 1000));
			match.setDatum(Config.sdfDb.format(date));
			match.setSpielTyp("E");
			MatchData.instance().add(match);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

}
