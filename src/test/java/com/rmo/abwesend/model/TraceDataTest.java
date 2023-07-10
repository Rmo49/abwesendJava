package com.rmo.abwesend.model;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.rmo.abwesend.util.Config;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TraceDataTest extends TestCase {
	
	static final long stunde = 60*60*1000;
	static final long tag = 24*60*60*1000;
	

	public TraceDataTest(String name) {
		super(name);
		// Auto-generated constructor stub
	}
	
	/**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TraceDataTest.class );
    }

    /**
     * Trace 
     */
//    @BeforeClass
//	public void test1_DeleteAll() {
//    	if (DbConnectionTest.connectDb() >= 0) {
//			try {
//				TraceDbData.instance().deleteAll();
//			}
//	    	catch (SQLException ex) {
//	    		fail(ex.getMessage());
//	    	}
//    	}
//	}

    
    /**
     * Trace eintragen
     */
	public void test2_Add1() {
    	if (DbConnectionTest.connectDb() >= 0) {
			try {
				TraceDbData.instance().add("Trace 1");
			}
	    	catch (SQLException ex) {
	    		fail(ex.getMessage());
	    	}
    	}
	}
	
	  /**
     * Trace eintragen
     */
	public void test2_Add2() {
    	if (DbConnectionTest.connectDb() >= 0) {
    		try {
				TraceDbData.instance().add("Trace 2");
    		}
	    	catch (SQLException ex) {
	    		fail(ex.getMessage());
	    	}   	
    	}
	}



	/**
     * Spieler und Datum im Range
     */
	public void test3_read1() {
    	if (DbConnectionTest.connectDb() >= 0) {
    		Date datum = new Date();
    		datum.setTime(datum.getTime() - Config.einTagLong);
    		List<TraceDb> traceList = null;
    		try {
    			traceList = TraceDbData.instance().readAll(datum);
     		}
	    	catch (Exception ex) {
	    		fail(ex.getMessage());
	    	}
    		TraceDb trace = traceList.get(0);
    		assertNotNull(trace.getDatum());
    		assertNotNull(trace.getWert());
    	}
	}

	public void test4_DeleteAll() {
	  	if (DbConnectionTest.connectDb() >= 0) {
			try {
				TraceDbData.instance().deleteAll();
			}
	    	catch (SQLException ex) {
	    		fail(ex.getMessage());
	    	}
	  	}
	}
	
	
}
