package com.rmo.abwesend.model;

import org.junit.runners.MethodSorters;

import com.rmo.abwesend.TestData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;


@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class SpielerTableauDataTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SpielerTableauDataTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( SpielerTableauDataTest.class );
    }

    /**
     * Test ein Tableau dazufügen, Daten normal
     */
    public void testAdd1() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		SpielerTableau st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(0).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(0).getId());
	    		tableaux.add(tableau1);
	    		st.setTableauList(tableaux);
	    		
	    		SpielerTableauData.instance().add(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }

    /**
     * Test mehrere Tableau dazufügen, Daten normal
     */
    public void testAdd2() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		SpielerTableau st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(1).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(1).getId());
	    		tableaux.add(tableau1);
	    		Integer tableau2 = Integer.valueOf(TestData.testTableau.get(2).getId());
	    		tableaux.add(tableau2);
	    		Integer tableau3 = Integer.valueOf(TestData.testTableau.get(3).getId());
	    		tableaux.add(tableau3);
	    		st.setTableauList(tableaux);
	    		
	    		SpielerTableauData.instance().add(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }

    /**
     * Test mehrere Tableau dazufügen vorher 1 Tableau, gleicher Spieler
     */
    public void testAdd3() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		SpielerTableau st = null;
	    	try {
	    		st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(2).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(1).getId());
	    		tableaux.add(tableau1);
	    		
	    		st.setTableauList(tableaux);	    		
	    		SpielerTableauData.instance().add(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
	    	try {
	    		st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(2).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(1).getId());
	    		tableaux.add(tableau1);
	    		Integer tableau2 = Integer.valueOf(TestData.testTableau.get(2).getId());
	    		tableaux.add(tableau2);
	    		Integer tableau3 = Integer.valueOf(TestData.testTableau.get(3).getId());
	    		tableaux.add(tableau3);

	    		st.setTableauList(tableaux);	    		
	    		SpielerTableauData.instance().update(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }
 
    /**
     * Test mehrere Tableau vorher mehrere 
     */
    public void testAdd4() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		SpielerTableau st = null;
	    	try {
	    		st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(3).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(3).getId());
	    		tableaux.add(tableau1);
	    		Integer tableau2 = Integer.valueOf(TestData.testTableau.get(0).getId());
	    		tableaux.add(tableau2);
	    		Integer tableau3 = Integer.valueOf(TestData.testTableau.get(4).getId());
	    		tableaux.add(tableau3);

	    		st.setTableauList(tableaux);  		
	    		SpielerTableauData.instance().add(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	

	    	try {
	    		st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(3).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(3).getId());
	    		tableaux.add(tableau1);
	    		Integer tableau2 = Integer.valueOf(TestData.testTableau.get(1).getId());
	    		tableaux.add(tableau2);
	    		Integer tableau3 = Integer.valueOf(TestData.testTableau.get(2).getId());
	    		tableaux.add(tableau3);
	    		Integer tableau4 = Integer.valueOf(TestData.testTableau.get(4).getId());
	    		tableaux.add(tableau4);

	    		st.setTableauList(tableaux);  		
	    		SpielerTableauData.instance().update(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	
    	}
    }

 
    /**
     * Test ein Tableau, vorher mehrere
     */
    public void testAdd5() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		SpielerTableau st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(4).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(5).getId());
	    		tableaux.add(tableau1);
	    		Integer tableau2 = Integer.valueOf(TestData.testTableau.get(1).getId());
	    		tableaux.add(tableau2);

	    		st.setTableauList(tableaux);  		
	    		SpielerTableauData.instance().add(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	

	    	try {
	    		SpielerTableau st = new SpielerTableau();
	    		st.setSpielerId(TestData.testSpieler.get(4).getId());
	    		ArrayList<Integer> tableaux = new ArrayList<Integer>();
	    		Integer tableau1 = Integer.valueOf(TestData.testTableau.get(4).getId());
	    		tableaux.add(tableau1);

	    		st.setTableauList(tableaux);  		
	    		SpielerTableauData.instance().update(st);
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
}
    }

     /**
     * Test lesen von Spieler 0, sollte 1 Tableau haben
     */
	public void testRead1() {
		if (DbConnectionTest.connectDb() >= 1) {
			try {
				List<Integer> tableauList = SpielerTableauData.instance().
						readAllTableau(TestData.testSpieler.get(0).getId());
				assertEquals(1, tableauList.size());
				assertEquals(TestData.testTableau.get(0).getId(), tableauList.get(0).intValue());
			} catch (Exception ex) {
				fail(ex.getMessage());
			}
		}
	}

	
	
	/**
	 * Test lesen von Spieler 1, sollte 3 Tableau haben
	 */
	public void testRead2() {
		if (DbConnectionTest.connectDb() >= 1) {
			try {
				List<Integer> tableauList = SpielerTableauData.instance().
						readAllTableau(TestData.testSpieler.get(1).getId());
				assertEquals(3, tableauList.size());
			} catch (Exception ex) {
				fail(ex.getMessage());
			}
		}
	}

	/**
	 * Test lesen von Spieler 4, sollte 1 Tableau haben
	 */
	public void testRead3() {
		if (DbConnectionTest.connectDb() >= 1) {
			try {
				List<Integer> tableauList = SpielerTableauData.instance().
						readAllTableau(TestData.testSpieler.get(4).getId());
				assertEquals(1, tableauList.size());
			} catch (Exception ex) {
				fail(ex.getMessage());
			}
		}
	}

}

