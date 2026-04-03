package com.rmo.abwesend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rmo.abwesend.util.Config;

/**
 * Config-Daten werden am Ende des Test alle wieder gelöscht
 *
 * @author Ruedi
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfigDataTest {

	@BeforeAll
	static void setToLocal() {
		DbConnectionTest.setToLocal();
	}

	/**
	 * Alle Config-Daten löschen
	 */
	@Test
	@Order(1)
	void deleteAll() {
		try {
			DbConnection.getConnection(Config.dbName);
			ConfigDbData.instance().deleteAll();
		} catch (SQLException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Ein Wert einlesen
	 */
	@Test
	@Order(2)
	void add1() {
		try {
			DbConnection.getConnection(Config.dbName);

			Map<String, String> map = new HashMap<>();
			map.put("test1.key", "Test1 Value");
			ConfigDbData.instance().addAll(map);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Mehrere Wert einlesen
	 */
	@Test
	@Order(3)
	void add2() {
		try {
			DbConnection.getConnection(Config.dbName);
			Map<String, String> map = new HashMap<>();
			map.put("test2.key", "Test2 Value");
			map.put("test3.key", "Test3 Value");
			ConfigDbData.instance().addAll(map);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Geleichen Wert update
	 */
	@Test
	@Order(5)
	void add3() {
		try {
			DbConnection.getConnection(Config.dbName);

			Map<String, String> map = new HashMap<>();
			map.put("test2.key", "Test2 Value New");
			map.put("test3.key", "Test3 Value");
			ConfigDbData.instance().addAll(map);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	@Order(7)
	void read() {
		Map<String, String> keyValue = new TreeMap<>();
		try {
			DbConnection.getConnection(Config.dbName);
			keyValue = ConfigDbData.instance().readAll(keyValue);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		assertEquals("Test1 Value", keyValue.get("test1.key"), "falscher Wert");
		assertEquals("Test2 Value New", keyValue.get("test2.key"), "falscher Wert");
	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	@Order(9)
	void loeschen() {
		try {
			DbConnection.getConnection(Config.dbName);
			ConfigDbData.instance().deleteAll();
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

}
