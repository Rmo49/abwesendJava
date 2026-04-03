package com.rmo.abwesend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rmo.abwesend.util.Config;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TennisDataBaseTest {

	static final boolean dbLoeschen = false;

	@BeforeAll
	static void setup() {
		DbConnectionTest.setToLocal();
	}

	@Test
	@Order(1)
	void testDbDelete() {
		boolean hatDb = TennisDataBase.dbExists();
		if (!hatDb) {
			try {
				TennisDataBase.createDb(Config.dbName);
				hatDb = true;
			} catch (SQLException ex) {
				assertNull(ex, "Fehler beim DB anlegen");
			}
		}
		if (hatDb) {
			TennisDataBase.deleteDb(Config.dbName);
		}
		hatDb = TennisDataBase.dbExists();
		assertTrue(!hatDb, "DB löschen nicht möglich");
	}

	/**
	 * Die Datenbank anlegen
	 */
	@Test
	@Order(2)
	void dbCreate() {
		try {
			TennisDataBase.createDb(Config.dbName);
		} catch (SQLException ex) {
			assertNull(ex, "Fehler beim DB anlegen");
		}
		assertTrue(TennisDataBase.dbExists(), "DB existiert");
	}

	/**
	 * Alle Tabellen generieren und wieder löschen
	 */
	@Test
	@Order(3)
	void tabellenHandling() {
		TennisDataBase.generateAllTables();
		int anz = TennisDataBase.countTables(Config.dbName);
		assertEquals(TennisDataBase.numberOfTabels, anz);
		try {
			TennisDataBase.deleteAllTables();
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		anz = TennisDataBase.countTables(Config.dbName);
		assertEquals(0, anz);
	}

	/**
	 * Test Tabellen anlegen
	 */
	@Test
	@Order(7)
	public void testNewTables() {
		TennisDataBase.generateAllTables();
		int anz = TennisDataBase.countTables(Config.dbName);
		assertEquals(TennisDataBase.numberOfTabels, anz);
	}
}
