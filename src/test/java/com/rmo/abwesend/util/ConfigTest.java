package com.rmo.abwesend.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConfigTest {

	/**
	 * Wird vor einem Testlauf aufgerufen
	 *
	 * @throws Exception
	 */
	@BeforeAll
	public static void setupProperties() throws Exception {
		Config.turnierBeginDatum.setTime(Config.turnierBeginDatum.getTime());
		Config.turnierEndDatum.setTime(Config.turnierBeginDatum.getTime() + (Config.einTagLong * 14));
		Config.readProperties();
		Config.saveProperties();
		Config.setConnectionToLocal();
	}

	/**
	 * Datum lesen
	 */
	@Test
	void test1_inhalt() {
		try {
			Config.readProperties();
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		String url = Config.get(Config.dbUrl);
		String listDir = Config.spielerExportDirKey;
		Date datum = Config.showBeginDatum;
		String expotDir = Config.spielerExportDir;
		assertNotNull(url);
		assertNotNull(listDir);
		assertNotNull(datum);
		assertNotNull(expotDir);
	}

	@Test
	void test5_save() {
		try {
			Config.saveProperties();
		}
		catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

}
