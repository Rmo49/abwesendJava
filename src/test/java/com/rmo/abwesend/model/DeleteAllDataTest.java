package com.rmo.abwesend.model;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.rmo.abwesend.TestData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class DeleteAllDataTest extends TestCase{
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DeleteAllDataTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DeleteAllDataTest.class );
    }


    /**
     * Alle Spieler löschen, dann Spieler lesen
     */
    public void test1DeleteMatches() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		MatchData.instance().deleteAllRow();
    		}
	    	catch (Exception ex) {
	    		// nix tun
	    	}
	    		
	    	// versuchen zu lesen
	    	try {
	    		MatchData.instance().readAll(TestData.testSpieler.get(0).getId());
	    	}
	    	catch (Exception ex) {
	    		assertEquals(true, ex.getMessage().contains("nicht"));
	    	}
    	}
    }
 
   
    
    /**
     * Alle Spieler löschen, dann Spieler lesen
     */
    public void test3DeleteSpieler() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	for (int i = 0; i < TestData.testSpieler.size(); i++) {
		    	try {
		    		SpielerData.instance().delete(TestData.testSpieler.get(i).getId());
	    		}
		    	catch (Exception ex) {
		    		// nix tun
		    	}
	    	}
	    		
	    	// versuchen zu lesen
	    	try {
	    		SpielerData.instance().read(TestData.testSpieler.get(0).getId());
	    	}
	    	catch (Exception ex) {
	    		assertEquals(true, ex.getMessage().contains("nicht"));
	    	}
    	}
    }
 
    
    /**
     * Alle Tableau löschen, dann Tableau lesen
     */
    public void test4DeleteTableau() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	for (int i = 0; i < TestData.testTableau.size(); i++) {
		    	try {
		    		TableauData.instance().delete(TestData.testTableau.get(i).getId());
	    		}
		    	catch (Exception ex) {
		    		// nix tun
		    	}
	    	}
	    		
	    	// versuchen zu lesen
	    	try {
	    		TableauData.instance().read(TestData.testTableau.get(0).getId());
	    	}
	    	catch (Exception ex) {
	    		assertEquals(true, ex.getMessage().contains("nicht"));
	    	}
    	}
    }
 

}

