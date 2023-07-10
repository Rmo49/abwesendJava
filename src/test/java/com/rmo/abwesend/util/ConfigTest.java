package com.rmo.abwesend.util;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConfigTest extends TestCase {

	public ConfigTest(String name) {
		super(name);
		// Auto-generated constructor stub
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ConfigTest.class);
	}

	/**
	 * Wird vor einem Testlauf aufgerufen
	 * @throws Exception
	 */
	public static void setupProperties() throws Exception {
		Config.turnierBeginDatum.setTime(Config.turnierBeginDatum.getTime());
		Config.turnierEndDatum.setTime(Config.turnierBeginDatum.getTime() + (Config.einTagLong * 14));
		Config.readProperties();
	}

	/**
	 * Datum lesen
	 */
	public void test_date1() {
		try {
			Config.readProperties();
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		String url = Config.get(Config.dbUrlKey);
		String listDir = Config.spielerExportDirKey;
		Date datum = Config.showBeginDatum;
		assertNotNull(url);
		assertNotNull(listDir);
		assertNotNull(datum);
	}


}
