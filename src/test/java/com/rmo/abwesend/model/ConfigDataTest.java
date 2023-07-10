package com.rmo.abwesend.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Config-Daten werden am Ende des Test alle wieder gelöscht
 * @author Ruedi
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigDataTest extends TestCase {	 
	
	public ConfigDataTest(String name) {
		super(name);
		// Auto-generated constructor stub
	}
	
	/**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ConfigDataTest.class );
    }

    /**
     * Alle Config-Daten löschen
     */
	public void test1_DeleteAll() {
    	System.out.println("test1_DeleteAll()");
    	if (DbConnectionTest.connectDb() >= 1) {
			try {
				ConfigDbData.instance().deleteAll();
			}
	    	catch (SQLException ex) {
	    		fail(ex.getMessage());
	    	}
    	}
	}

    
    /**
     * Ein Wert einlesen
     */
	public void test2_Add1() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			Map<String, String> map = new HashMap<String, String>();
    			map.put("test1.key", "Test1 Value");
     			ConfigDbData.instance().addAll(map);
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}

	   /**
     * Mehrere Wert einlesen
     */
	public void test2_Add2() {
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			Map<String, String> map = new HashMap<String, String>();
    			map.put("test2.key", "Test2 Value");
    			map.put("test3.key", "Test3 Value");
     			ConfigDbData.instance().addAll(map);
    		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}
	
	   /**
  * Geleichen Wert update
  */
	public void test2_Add3() {
 	if (DbConnectionTest.connectDb() >= 1) {
 		try {
 			Map<String, String> map = new HashMap<String, String>();
 			map.put("test2.key", "Test2 Value New");
 			map.put("test3.key", "Test3 Value");
  			ConfigDbData.instance().addAll(map);
 		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}   	
 		}
	}
	

	/**
     * Spieler und Datum im Range
     */
	public void test4_read1() {
		Map<String, String> keyValue = new TreeMap<String, String>();
    	if (DbConnectionTest.connectDb() >= 1) {
    		try {
    			keyValue = ConfigDbData.instance().readAll(keyValue);
     		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}
    		assertEquals("falscher Wert", "Test1 Value", keyValue.get("test1.key"));
    		assertEquals("falscher Wert", "Test2 Value New", keyValue.get("test2.key"));
    	}
	}

	/**
     * Spieler und Datum im Range
     */
	public void test9_loeschen() {
    	if (DbConnectionTest.connectDb() >= 0) {
    		try {
    			ConfigDbData.instance().deleteAll();
     		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}
    	}
	}


}
