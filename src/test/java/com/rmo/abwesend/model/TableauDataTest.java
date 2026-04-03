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
public class TableauDataTest {

	static List<Tableau> testTableau;

	@BeforeAll
	static void setup() {
		DbConnectionTest.setToLocal();
		try {
			TableauData.instance().deleteAllRow();
			assertEquals(0, TableauData.instance().readAllTableau().size());
			testTableau = generateTableau();
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test ein Tableau 0 dazufügen, Daten normal
	 */
	@Test
	void add1() {
		try {
			Tableau tableau = testTableau.get(0);
			TableauData.instance().add(tableau);
			tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
			assertNotNull(tableau);
			testTableau.get(0).setId(tableau.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test Tableau 1 dazufügen, Daten normal
	 */
	@Test
	void add2() {
		try {
			Tableau tableau = testTableau.get(1);
			TableauData.instance().add(tableau);
			tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
			assertNotNull(tableau);
			testTableau.get(1).setId(tableau.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test Tableau 2 dazufügen, schon vorhanden
	 */
	@Test
	void add3() {
		try {
			Tableau tableau = testTableau.get(2);
			TableauData.instance().add(tableau);
			tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
			assertNotNull(tableau);
			testTableau.get(2).setId(tableau.getId());
		} catch (Exception ex) {
			assertNotNull(ex);
			assertEquals("Tableau mit diesem Namen schon vorhanden", ex.getMessage());
		}
	}

	/**
	 * Test ein leeres Tableau dazufügen
	 */
	@Test
	void addLeer() {
		try {
			Tableau tableau = new Tableau();
			tableau.setId(-1);
			tableau.setBezeichnung(" ");
			TableauData.instance().add(tableau);
		} catch (Exception ex) {
			assertNotNull(ex);
			assertEquals("Tableau Bezeichung eingeben", ex.getMessage());
		}
	}

	/**
	 * Test ein Tableau dazufügen, schon vorhanden TODO noch genau anschauen ob mit
	 * gleicher Bezeichnung auch erlaubt
	 */
	@Test
	void update() {
		try {
			Tableau tableau = new Tableau();
			tableau.setBezeichnung(testTableau.get(0).getBezeichnung());
			tableau.setPosition("11");
			tableau.setId(-1);
			TableauData.instance().add(tableau);
		} catch (Exception ex) {
			assertNotNull(ex);
			assertTrue(ex.getMessage().contains("schon vorhanden"));
		}
	}

	/**
	 * Test ein Tableau dazufügen, Daten normal
	 */
	@Test
	void addMany() {
		try {
			Tableau tableau = testTableau.get(3);
			TableauData.instance().add(tableau);
			tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
			testTableau.get(3).setId(tableau.getId());

			tableau = testTableau.get(4);
			TableauData.instance().add(tableau);
			tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
			testTableau.get(4).setId(tableau.getId());

			tableau = testTableau.get(5);
			TableauData.instance().add(tableau);
			tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
			testTableau.get(5).setId(tableau.getId());

			tableau = testTableau.get(6);
			TableauData.instance().add(tableau);
			tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
			testTableau.get(6).setId(tableau.getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Tableau 0 löschen, hat Beziehungen TODO noch genau testen
	 */
	@Test
	void testDelete1() {
		try {
			TableauData.instance().delete(testTableau.get(0).getId());
		} catch (Exception ex) {
			assertEquals(true, ex.getMessage().contains("Cannot delete"));
		}

	}

	/**
	 * Tableau 4 löschen, hat keine Beziehungen
	 */
	@Test
	void testDelete2() {
		try {
			TableauData.instance().delete(testTableau.get(5).getId());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

	// ---- Daten für alle Programme -----------------------

	/**
	 * Einige Tableau generieren.
	 *
	 * @return
	 */
	public static List<Tableau> generateTableau() {
		ArrayList<Tableau> testTableau = new ArrayList<>();

		Tableau tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung("MS Test1");
		tableau.setPosition("1");
		testTableau.add(tableau);

		tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung("MS Test2");
		tableau.setPosition("2");
		testTableau.add(tableau);

		tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung("WS Test1");
		tableau.setPosition("3");
		testTableau.add(tableau);

		tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung("WS Test2");
		tableau.setPosition("3");
		testTableau.add(tableau);

		tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung("MD Test1");
		tableau.setPosition("5");
		testTableau.add(tableau);

		tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung("MD Test2");
		tableau.setPosition("9");
		testTableau.add(tableau);

		tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung("WD Test1");
		tableau.setPosition("12");
		testTableau.add(tableau);

		return testTableau;
	}

	/**
	 * Alle Tableau in die DB schreiben
	 *
	 * @return
	 */
	public static List<Tableau> saveAllTableau() {
		List<Tableau> testTableau = generateTableau();
		Tableau tableau;
		try {
			for (int i = 0; i < testTableau.size(); i++) {
				tableau = testTableau.get(i);
				int id = TableauData.instance().add(tableau);
				tableau.setId(id);
				testTableau.set(i, tableau);
			}
		} catch (Exception ex) {
			return null;
		}
		return testTableau;
	}

}
