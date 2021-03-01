package com.rmo.abwesend.model;

import org.junit.runners.MethodSorters;

import com.rmo.abwesend.TestData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.FixMethodOrder;


@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class TableauDataDeleteTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TableauDataDeleteTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( TableauDataDeleteTest.class );
    }

    /**
     * Tableau 0 löschen, hat Beziehungen
     */
    public void testDelete1() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		TableauData.instance().delete(TestData.testTableau.get(0).getId());
	    	}
	    	catch (Exception ex) {
	    		assertEquals(true, ex.getMessage().contains("Cannot delete"));
	    	}   	
    	}
    }

    /**
     * Tableau 4 löschen, hat keine Beziehungen
     */
    public void testDelete2() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		TableauData.instance().delete(TestData.testTableau.get(5).getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }

}

