package com.rmo.abwesend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SpielerTableauDataTest {

	static List<Tableau> testTableau;
	static List<Spieler> testSpieler;

	@BeforeAll
	static void setup() {
		DbConnectionTest.setToLocal();
		try {
			SpielerTableauData.instance().deleteAllRow();

			TableauData.instance().deleteAllRow();
			assertEquals(0, TableauData.instance().readAllTableau().size());
			testTableau = TableauDataTest.saveAllTableau();

			SpielerData.instance().deleteAllRow();
			assertEquals(0, SpielerData.instance().readAllSpieler().size());
			testSpieler = SpielerDataTest.saveAllSpieler();

		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}


	/**
	 * Test ein Tableau dazufügen, Daten normal
	 */
	@Test
	void add1() {
		try {
			SpielerTableau st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(0).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(0).getId());
			tableaux.add(tableau1);
			st.setTableauList(tableaux);

			SpielerTableauData.instance().add(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test mehrere Tableau dazufügen, Daten normal
	 */
	@Test
	void add2() {
		try {
			SpielerTableau st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(1).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(1).getId());
			tableaux.add(tableau1);
			Integer tableau2 = Integer.valueOf(testTableau.get(2).getId());
			tableaux.add(tableau2);
			Integer tableau3 = Integer.valueOf(testTableau.get(3).getId());
			tableaux.add(tableau3);
			st.setTableauList(tableaux);

			SpielerTableauData.instance().add(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test mehrere Tableau dazufügen vorher 1 Tableau, gleicher Spieler
	 */
	@Test
	void add3() {
		SpielerTableau st = null;
		try {
			st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(2).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(1).getId());
			tableaux.add(tableau1);

			st.setTableauList(tableaux);
			SpielerTableauData.instance().add(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		try {
			st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(2).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(1).getId());
			tableaux.add(tableau1);
			Integer tableau2 = Integer.valueOf(testTableau.get(2).getId());
			tableaux.add(tableau2);
			Integer tableau3 = Integer.valueOf(testTableau.get(3).getId());
			tableaux.add(tableau3);

			st.setTableauList(tableaux);
			SpielerTableauData.instance().update(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test mehrere Tableau vorher mehrere
	 */
	@Test
	void add4() {
		SpielerTableau st = null;
		try {
			st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(3).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(3).getId());
			tableaux.add(tableau1);
			Integer tableau2 = Integer.valueOf(testTableau.get(0).getId());
			tableaux.add(tableau2);
			Integer tableau3 = Integer.valueOf(testTableau.get(4).getId());
			tableaux.add(tableau3);

			st.setTableauList(tableaux);
			SpielerTableauData.instance().add(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

		try {
			st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(3).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(3).getId());
			tableaux.add(tableau1);
			Integer tableau2 = Integer.valueOf(testTableau.get(1).getId());
			tableaux.add(tableau2);
			Integer tableau3 = Integer.valueOf(testTableau.get(2).getId());
			tableaux.add(tableau3);
			Integer tableau4 = Integer.valueOf(testTableau.get(4).getId());
			tableaux.add(tableau4);

			st.setTableauList(tableaux);
			SpielerTableauData.instance().update(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

	/**
	 * Test ein Tableau, vorher mehrere
	 */
	@Test
	void add5() {
		try {
			SpielerTableau st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(4).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(5).getId());
			tableaux.add(tableau1);
			Integer tableau2 = Integer.valueOf(testTableau.get(1).getId());
			tableaux.add(tableau2);

			st.setTableauList(tableaux);
			SpielerTableauData.instance().add(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

		try {
			SpielerTableau st = new SpielerTableau();
			st.setSpielerId(testSpieler.get(4).getId());
			ArrayList<Integer> tableaux = new ArrayList<>();
			Integer tableau1 = Integer.valueOf(testTableau.get(4).getId());
			tableaux.add(tableau1);

			st.setTableauList(tableaux);
			SpielerTableauData.instance().update(st);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test lesen von Spieler 0, sollte 1 Tableau haben
	 */
	@Test
	void read1() {
		try {
			List<Integer> tableauList = SpielerTableauData.instance()
					.readAllTableau(testSpieler.get(0).getId());
			assertEquals(1, tableauList.size());
			assertEquals(testTableau.get(0).getId(), tableauList.get(0).intValue());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test lesen von Spieler 1, sollte 3 Tableau haben
	 */
	@Test
	void read2() {
		try {
			List<Integer> tableauList = SpielerTableauData.instance()
					.readAllTableau(testSpieler.get(1).getId());
			assertEquals(3, tableauList.size());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * Test lesen von Spieler 4, sollte 1 Tableau haben
	 */
	@Test
	void testRead3() {
		try {
			List<Integer> tableauList = SpielerTableauData.instance()
					.readAllTableau(testSpieler.get(4).getId());
			assertEquals(1, tableauList.size());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

}
