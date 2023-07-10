package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.Trace;



/**
 * Trace Daten in der DB, schreibt Datum und wer eingelogged hat.
 */
public class TraceDbData {

	/**
	 * Das Create Statement für diese Tabelle
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS trace (");
		sb.append(" datum datetime DEFAULT CURRENT_TIMESTAMP, wert VARCHAR(180) DEFAULT NULL");
		sb.append(");");
		return sb.toString();
	}

	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS trace;");
	}


	private static TraceDbData instance = null;

	/**
	 * Enthält Connection zur DB. Wird in setupResultset gesetzt, bleibt während
	 * ganzer Sitzung erhalten.
	 */
	private Statement mReadStmt;

	/**
	 * Der Set mit allen Match-Daten von dem gelesen wird. Ist ein scrollable
	 * Set der von allen Methoden verwendet wird. id: IntegerInteger <br>
	 * name: String <br>
	 */
	private ResultSet mReadSet;

	/**
	 * MatchData versteckt, wird immer über Singelton aufgerufen
	 */
	private TraceDbData() {
	}

	/**
	 * Singleton
	 * @return
	 */
	public static TraceDbData instance() {
		if (instance == null) {
			instance = new TraceDbData();
		}
		return instance;
	}

	/**
	 * Die Connection zu der DB.
	 */
	protected Connection getConnection() throws SQLException {
		return DbConnection.getConnection();
	}


	/**
	 * Das Match wird gespeichert. Falls schon vorhanden, wird nix gemacht
	 */
	public void add(String traceData) throws SQLException {
		try {
			addRow(traceData);
		}
		catch (SQLException ex) {
			Trace.println(5, "TraceDB schreiben: " + ex.getMessage());
		}
	}

	/**
	 * Alle Einträge ab einem bestimmten Datum zurückgeben.
	 */
	public List<TraceDb> readAll(Date fromDate) throws Exception {
		return findAll(fromDate);
	}

	/**
	 * Die Trace-Einträge bis zu einen Datum löschen
	 * @param date
	 * @throws SQLException
	 */
	public void deleteBis(Date date) throws SQLException {
		deleteBisDate(date);
	}

	/**
	 * Alle Traces löschen
	 */
	public void deleteAll() throws SQLException {
		setupReadSet();
		mReadStmt.execute("TRUNCATE Trace");
	}



	// ------ interne Methoden -----------------------------------------

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen.
	 */
	private void addRow(String traceData) throws SQLException {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO trace VALUES (");
		lQuery.append("CURTIME(), '(GMT) ");
		lQuery.append(traceData);
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}


	/**
	 * Trace ab einem bestimmten Datum anzeigen
	 */
	private List<TraceDb> findAll(Date fromDate) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		String statmt = "SELECT * FROM trace WHERE datum >= '" + Config.sdfDb.format(fromDate) + "';";
		mReadSet = mReadStmt.executeQuery(statmt);
		List<TraceDb> traceList = new ArrayList<>();
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			TraceDb trace = new TraceDb(mReadSet.getTimestamp(1), mReadSet.getString(2));
//			trace.setDatum(mReadSet.getTimestamp(1));
			trace.setWert(mReadSet.getString(2));
			traceList.add(trace);
		}
		return traceList;
	}

	/**
	 * Trace bis zu einem bestimmten Datum löschen
	 */
	private void deleteBisDate(Date bisDate) throws SQLException {
		Statement stmt = getConnection().createStatement();
		String lQuery = "DELETE FROM trace WHERE datum <= '" + Config.sdfDb.format(bisDate) + "';";

		stmt.executeUpdate(lQuery);
		stmt.close();
	}


	/**
	 * Setzt das Statement (Connection zur DB) und den Scroll-Set, der für
	 * Insert oder update verwendet werden kann.
	 */
	private synchronized void setupReadSet() throws SQLException {
		if (mReadStmt == null) {
			mReadStmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}
	}

}
