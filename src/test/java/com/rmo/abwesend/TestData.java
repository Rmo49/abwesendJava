package com.rmo.abwesend;

import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerDataTest;
import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauDataTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Config-Daten werden am Ende des Test alle wieder gelöscht
 * @author Ruedi
 *
 */
public class TestData extends TestCase {
	
	public static List<Spieler> testSpieler;
	public static List<Tableau> testTableau;
	 
	
	public TestData(String name) {
		super(name);
		// Auto-generated constructor stub
	}
	
	/**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestData.class );
    }

    /**
     * Init all Data, for use with the testcases
     */
    @BeforeClass
    public static void testInitAllData() {
    	System.out.println("initall()");
    	testSpieler = new ArrayList<Spieler>();
    	SpielerDataTest.getTestSpieler(testSpieler);
    	testTableau = new ArrayList<Tableau>();
    	TableauDataTest.getTestTableau(testTableau);
    }
    
 

}
