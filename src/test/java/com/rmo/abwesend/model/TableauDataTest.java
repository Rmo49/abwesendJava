package com.rmo.abwesend.model;

import org.junit.runners.MethodSorters;

import com.rmo.abwesend.TestData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

import org.junit.FixMethodOrder;


@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class TableauDataTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TableauDataTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( TableauDataTest.class );
    }

    public static void getTestTableau(List<Tableau> testTableau) {
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
    }

 
    /**
     * Test ein Tableau 0 dazufügen, Daten normal
     */
    public void testAdd1() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Tableau tableu = TestData.testTableau.get(0);
	    		TableauData.instance().add(tableu);
	    		tableu = TableauData.instance().readBezeichnung(tableu.getBezeichnung());
	    		TestData.testTableau.get(0).setId(tableu.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }
 
    /**
     * Test Tableau 1 dazufügen, Daten normal
     */
    public void testAdd2() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Tableau tableu = TestData.testTableau.get(1);
	    		TableauData.instance().add(tableu);
	    		tableu = TableauData.instance().readBezeichnung(tableu.getBezeichnung());
	    		TestData.testTableau.get(1).setId(tableu.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }
 
    /**
     * Test Tableau 2 dazufügen, schon vorhanden
     */
    public void testAdd3() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Tableau tableau = TestData.testTableau.get(2);
	    		TableauData.instance().add(tableau);
	    		tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
	    		TestData.testTableau.get(2).setId(tableau.getId());
	    	}
	    	catch (Exception ex) {
	    		assertNotNull(ex);
	    		assertEquals("Tableau mit diesem Namen schon vorhanden", ex.getMessage());
	    	}   	
    	}
    }

    /**
     * Test ein leeres Tableau dazufügen
     */
    public void testAddLeer() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Tableau tableau = new Tableau();
	    		tableau.setId(-1);
	    		tableau.setBezeichnung(" ");
	    		TableauData.instance().add(tableau);
	    	}
	    	catch (Exception ex) {
	    		assertNotNull(ex);
	    		assertEquals("Tableau Bezeichung eingeben", ex.getMessage());
	    	}   	
    	}
    }

    /**
     * Test ein Tableau dazufügen, schon vorhanden
     */
    public void testUpdate() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Tableau tableau = new Tableau();
	    		tableau.setBezeichnung(TestData.testTableau.get(0).getBezeichnung());
	    		tableau.setId(-1);
	    		TableauData.instance().add(tableau);
	    	}
	    	catch (Exception ex) {
	    		assertNotNull(ex);
	    		assertTrue(ex.getMessage().contains("schon vorhanden") );
	    	}   	
    	}
    }

    /**
     * Test ein Tableau dazufügen, Daten normal
     */
    public void testAddMany() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Tableau tableau = TestData.testTableau.get(3);
	    		TableauData.instance().add(tableau);
	    		tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
	    		TestData.testTableau.get(3).setId(tableau.getId());
	    		
	    		tableau = TestData.testTableau.get(4);
	    		TableauData.instance().add(tableau);
	    		tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
	    		TestData.testTableau.get(4).setId(tableau.getId());

	    		tableau = TestData.testTableau.get(5);
	    		TableauData.instance().add(tableau);
	    		tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
	    		TestData.testTableau.get(5).setId(tableau.getId());

	    		tableau = TestData.testTableau.get(6);
	    		TableauData.instance().add(tableau);
	    		tableau = TableauData.instance().readBezeichnung(tableau.getBezeichnung());
	    		TestData.testTableau.get(6).setId(tableau.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }
   
}

