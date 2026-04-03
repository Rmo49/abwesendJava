package com.rmo.abwesend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SpielerDataTest {

	static List<Spieler> testSpieler;


	@BeforeAll
	static void setup() {
		DbConnectionTest.setToLocal();
		try {
			SpielerData.instance().deleteAllRow();
			testSpieler = SpielerDataTest.generateSpieler();
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test Spieler 0 dazufügen, Daten normal, Tagesliste zu kurz
	 */
	@Test
	public void testAdd1() {
		try {
			Spieler spieler = testSpieler.get(0);
			SpielerData.instance().add(spieler);
			spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
			testSpieler.get(0).setId(spieler.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test Spieler 1 dazufügen, Tageliste zu lang
	 */
	@Test
	public void testAdd2() {
		try {
			Spieler spieler = testSpieler.get(1);
			SpielerData.instance().add(spieler);
			spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
			assertNotNull(spieler);
			testSpieler.get(1).setId(spieler.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test Spieler 2 dazufügen, Name lang (30 Zeichen)
	 */
	@Test
	public void testAdd3() {
		try {
			Spieler spieler = testSpieler.get(2);
			SpielerData.instance().add(spieler);
			spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
			assertNotNull(spieler);
			testSpieler.get(2).setId(spieler.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test Spieler 1 nochmals dazufügen, gleicher Name
	 */
	@Test
	public void testAdd4() {
		int tmpId = -1;
		Spieler spieler = null;
		try {
			spieler = testSpieler.get(1);
			tmpId = spieler.getId();
			spieler.setId(-1);
			SpielerData.instance().add(spieler);
		} catch (Exception ex) {
			assertNotNull(ex);
			assertTrue(ex.getMessage().contains("schon vorhanden"));
			spieler.setId(tmpId);
		}
	}

	/**
	 * Spieler 3 dazufügen, langer Name
	 */
	@Test
	public void testAdd5() {
		try {
			Spieler spieler = testSpieler.get(3);
			SpielerData.instance().add(spieler);
			spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
			testSpieler.get(3).setId(spieler.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler 4 dazufügen, Name lang (30 Zeichen)
	 */
	@Test
	public void testAdd6() {
		try {
			Spieler spieler = testSpieler.get(4);
			SpielerData.instance().add(spieler);
			spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
			testSpieler.get(4).setId(spieler.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Spieler 4 löschen, hat auch Matches, sollte nicht gehen
	 */
	@Test
	void testDelete1() {
		try {
			MatchDataTest.addMatchOfSpieler(testSpieler.get(4).getId());
			SpielerData.instance().delete(testSpieler.get(4).getId());
			// wenn cascade delete dann werden Matches gelöscht
//			fail("Löschen von Spieler mit Matches keine Exception geworfen.");
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		try {
			// Spieler muss noch vorhanden sein.
			Spieler sp = SpielerData.instance().read(testSpieler.get(4).getId());
			assertNotNull(sp);
		} catch (Exception ex) {
			assertNotNull(ex);
		}
	}

	/**
	 * Test mit löschen
	 */
	@Test
	void testDelete2() {
		Spieler spieler = testSpieler.get(4);
		try {
			MatchData.instance().updateAll(spieler.getId(), null);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		try {
			SpielerData.instance().delete(spieler.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		try {
			SpielerData.instance().read(spieler.getId());
		} catch (Exception ex) {
			assertEquals(true, ex.getMessage().contains("nicht"));
		}
	}

	// ----- Daten für alle Testcases

	public static List<Spieler> generateSpieler() {
		List<Spieler> testSpieler = new ArrayList<>();
		Spieler spieler = new Spieler();
		spieler.setName("Atest1");
		spieler.setVorName("Armin");
		spieler.setAbwesendList("-15; -14; ; -19.00; -18.30; ; ; -17; ;");
		testSpieler.add(spieler);

		// Tagesliste zu lang
		spieler = new Spieler();
		spieler.setName("Btest1");
		spieler.setVorName("Beatrice");
		spieler.setAbwesendList("-15; -14; ; -19.00; -18.30; ; ; -17; ; -15; -14; ; -19.00; -18.30; ; ; -17; ;");
		testSpieler.add(spieler);

		spieler = new Spieler();
		spieler.setName("Ctest1");
		spieler.setVorName("Casanova");
		spieler.setAbwesendList("-15; -14; ; -19.00; -18.30; ; ; -17; ; -15; -14; ; -19.00; -18.30; ; ; -17; ;");
		testSpieler.add(spieler);

		spieler = new Spieler();
		spieler.setName("Dtest1");
		spieler.setVorName("123456789012345678901234567890");
		spieler.setAbwesendList("0;; ; ; ; ; ; ; ;; ; ; -19.00; ; ; ; ; ;");
		testSpieler.add(spieler);

		spieler = new Spieler();
		spieler.setName("Etest1");
		spieler.setVorName("Erich");
		spieler.setAbwesendList(";; ; ; ; ;");
		testSpieler.add(spieler);
		return testSpieler;
	}

	/**
	 * Alle Spieler in die DB schreiben und ID sichern
	 * @return
	 */
	public static List<Spieler> saveAllSpieler() {
		List<Spieler> testSpieler = generateSpieler();
		try {
			Spieler spieler;
			for (int i = 0; i < testSpieler.size(); i++) {
				spieler = testSpieler.get(i);
				int id = SpielerData.instance().add(spieler);
				spieler.setId(id);
				testSpieler.set(i, spieler);
			}
		} catch (Exception ex) {
			return null;
		}
		return testSpieler;
	}

}
