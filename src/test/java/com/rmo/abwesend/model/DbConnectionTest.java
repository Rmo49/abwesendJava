package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.ConfigTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class DbConnectionTest extends TestCase {
	
	private static Connection conn = null;
	private static int code = -2;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DbConnectionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DbConnectionTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testConnection() {
    	if (connectDb() >= 0) {
    		try {
	    		DatabaseMetaData meta = DbConnection.getConnection().getMetaData();
	    		assertNotNull("Test", meta.getDatabaseMajorVersion());
	    		assertTrue("DB closed", ! DbConnection.getConnection().isClosed());
//    			assertEquals("Schema nicht gefunden", "tennis", DbConnection.getConnection().getSchema());
        	}
        	catch (Exception ex) {
        		fail(ex.getMessage());
        	}  
	    	try {
	    		DbConnection.close();
	    	}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}
    	}
    	else {
    		fail("Cannot connect to DB tennis");
    	}
    }
    
    
    /** 
     * Allgemeine Methode für die Connection 
     */
    
    /**
     * Properties einlesen, DB connection
     * @return
     * 2 wenn bereits Spieler vorhanden
     * 1 wenn nur Tabelle vorhanden,
     * 0 wenn keine Tabelle aber connection ok
     * -1 wenn keine connection
     * -2 wenn Probleme mit Properties
     */
    public static int connectDb()  {
    	Statement mReadStmt;
    	ResultSet mReadSet;
    	
    	// wenn schon einmal verbunden
    	if (conn != null) {
    		return code;
    	}
    	
    	try {
    		ConfigTest.setupProperties();
    	}
    	catch (Exception ex) {
    		code = -2;
    		return code;
    	}   	
    	
    	try {
    		conn = DbConnection.getConnection();
    	}
    	catch (Exception ex) {
    		code = -1;
    		return code;
    	}
   	
    	try { 	
    		mReadStmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    		mReadSet = mReadStmt.executeQuery("SELECT * FROM Spieler");
    		if (mReadSet.next()) {
    			Config.readConfigData();
        		code = 2;
        		return code;
    		}
			Config.readConfigData();
    		code = 1;
    		return code;
    	}
    	catch (Exception ex) {
    		code = 0;
    		return code;
    	}   	
    }
    
    public static void resetConnection() {
    	conn = null;
    	code = -2;
    }
}

