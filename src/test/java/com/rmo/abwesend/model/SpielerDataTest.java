package com.rmo.abwesend.model;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.rmo.abwesend.TestData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class SpielerDataTest extends TestCase{

   /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SpielerDataTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SpielerDataTest.class );
    }

    public static void getTestSpieler(List<Spieler> testSpieler) {
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
    }
    
    /**
     * Test Spieler 0 dazufügen, Daten normal, Tagesliste zu kurz
     */
    public void testAdd1() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Spieler spieler = TestData.testSpieler.get(0);
	    		SpielerData.instance().add(spieler);
	    		spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
	    		TestData.testSpieler.get(0).setId(spieler.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }
 
    /**
     * Test Spieler 1 dazufügen, Tageliste zu lang
     */
    public void testAdd2() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Spieler spieler = TestData.testSpieler.get(1);
	    		SpielerData.instance().add(spieler);
	    		spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
	    		TestData.testSpieler.get(1).setId(spieler.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }
    
    /**
     * Test Spieler 2 dazufügen, Name lang (30 Zeichen)
     */
    public void testAdd3() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Spieler spieler = TestData.testSpieler.get(2);
	    		SpielerData.instance().add(spieler);
	    		spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
	    		TestData.testSpieler.get(2).setId(spieler.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }

    /**
     * Test Spieler 1 nochmals dazufügen, gleicher Name
     */
    public void testAdd4() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		int tmpId = -1;
    		Spieler spieler = null;
	    	try {
	    		spieler = TestData.testSpieler.get(1);
	    		tmpId = spieler.getId();
	    		spieler.setId(-1);
	    		SpielerData.instance().add(spieler);
	    	}
	    	catch (Exception ex) {
	    		assertNotNull(ex);
	    		assertTrue(ex.getMessage().contains("schon vorhanden") );
	    		spieler.setId(tmpId);
	    	}   	
    	}
    }
   
    /**
     * Spieler 3 dazufügen, langer Name
     */
    public void testAdd5() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Spieler spieler = TestData.testSpieler.get(3);
	    		SpielerData.instance().add(spieler);
	    		spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
	    		TestData.testSpieler.get(3).setId(spieler.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }

    /**
     * Spieler 4 dazufügen, Name lang (30 Zeichen)
     */
    public void testAdd6() {
    	if (DbConnectionTest.connectDb() >= 1) {
	    	try {
	    		Spieler spieler = TestData.testSpieler.get(4);
	    		SpielerData.instance().add(spieler);
	    		spieler = SpielerData.instance().read(spieler.getName(), spieler.getVorName());
	    		TestData.testSpieler.get(4).setId(spieler.getId());
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
    }

}

