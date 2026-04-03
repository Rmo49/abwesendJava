package com.rmo.abwesend.model;

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
public class TraceDataTest {

	static final long stunde = 60 * 60 * 1000;
	static final long tag = 24 * 60 * 60 * 1000;

	/**
	 * Trace
	 */
	@BeforeAll
	static void setup() {
		DbConnectionTest.setToLocal();
		try {
			TraceDbData.instance().deleteAll();
		} catch (SQLException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Trace eintragen
	 */
	@Test
	public void test2_Add1() {
		try {
			TraceDbData.instance().add("Trace 1");
		} catch (SQLException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Trace eintragen
	 */
	@Test
	public void test2_Add2() {
		try {
			TraceDbData.instance().add("Trace 2");
		} catch (SQLException ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler und Datum im Range
	 */
	@Test
	public void test3_read1() {
		Date datum = new Date();
		datum.setTime(datum.getTime() - Config.einTagLong);
		List<TraceDb> traceList = null;
		try {
			traceList = TraceDbData.instance().readAll(datum);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		TraceDb trace = traceList.get(0);
		assertNotNull(trace.getDatum());
		assertNotNull(trace.getWert());
	}

	@Test
	public void test4_DeleteAll() {
		try {
			TraceDbData.instance().deleteAll();
		} catch (SQLException ex) {
			fail(ex.getMessage());
		}
	}

}
