package com.rmo.abwesend.model;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.rmo.abwesend.TestData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class SpielerDataDeleteTest extends TestCase{
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SpielerDataDeleteTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SpielerDataDeleteTest.class );
    }


    /**
     * Spieler 4 löschen, hat auch Matches, sollte nicht gehen
     */
    public void test2Delete() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		SpielerData.instance().delete(TestData.testSpieler.get(4).getId());
	    		fail ("Löschen von Spieler mit Matches keine Exception geworfen.");
	    	}
	    	catch (Exception ex) {
	    		// nothing
	    	}   	
	    	try {
	    		Spieler sp = SpielerData.instance().read(TestData.testSpieler.get(4).getId());
	    		assertNotNull(sp.getName());	    		
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}
    	}
    }

    /**
     * Alle Matches löschen, dann Spieler 4 löschen
     */
    public void test3Delete() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		Spieler spieler = TestData.testSpieler.get(4);
    		try {
    			
    			MatchData.instance().updateAll(spieler.getId(), null);
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}
	    	try {
	    		SpielerData.instance().delete(spieler.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}
	    	try {
	    		SpielerData.instance().read(spieler.getId());
	    	}
	    	catch (Exception ex) {
	    		assertEquals(true, ex.getMessage().contains("nicht"));
	    	}
    	}
    }

}

