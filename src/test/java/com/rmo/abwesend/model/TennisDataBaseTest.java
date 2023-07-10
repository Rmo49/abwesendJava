package com.rmo.abwesend.model;

import org.junit.Test;

import junit.framework.TestCase;

public class TennisDataBaseTest extends TestCase {
	
	static final boolean dbLoeschen = false;
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TennisDataBaseTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
//    public static Test suite()
//    {
//        return new TestSuite( TennisDataBaseTest.class );
//    }

    /**
     * Test Tabellen definieren
     */
    @Test
    public void testDropAllTables() {
    	int returnCode = DbConnectionTest.connectDb();
    	if (returnCode == -2) {
    		fail("Probleme mit Config");
    	}
    	if (returnCode == -1) {
    		fail("Kann DB nicht öffnen");
    	}
    	if (returnCode >= 0) {
    		if (dbLoeschen) {
		    	try {
		    		TennisDataBase.deleteAllTables();
		    		DbConnectionTest.resetConnection();
		    	}
		    	catch (Exception ex) {
		    		fail(ex.getMessage());
		    	}   	
	    	}
    	}
    }

    /**
     * Test Tabellen definieren
     */
    @Test
    public void testNewTables() {
    	int returnCode = DbConnectionTest.connectDb();
    	if (returnCode == -2) {
    		fail("Probleme mit Config");
    	}
    	if (returnCode == -1) {
    		fail("Kann DB nicht öffnen");
    	}
    	if (returnCode >= 0) {
        	if (returnCode >= 0) {
	    		try {
		    		TennisDataBase.generateNewTables();
		    		DbConnectionTest.resetConnection();
		    	}
		    	catch (Exception ex) {
		    		fail(ex.getMessage());
		    	}
        	}
    	}
    }
    
}

