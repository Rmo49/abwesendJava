package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.rmo.abwesend.util.Config;

/**
 * Verbindung zur Tabelle Config in der DB. Die Tabelle besteht aus token und
 * wert (key / value) pairs.
 */
public class ConfigDbData {

	public static String tableName = "config";

	/**
	 * Das Create Statement für diese Tabelle
	 * 
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(tableName);
		sb.append(" ( token VARCHAR(40) DEFAULT NULL, wert VARCHAR(180) DEFAULT NULL");
		sb.append(");");
		return sb.toString();
	}

	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS config;");
	}

	private static ConfigDbData instance = null;

	/**
	 * Enthält Connection zur DB. Wird in setupResultset gesetzt, bleibt während
	 * ganzer Sitzung erhalten.
	 */
	private Statement mReadStmt;

	/**
	 * Der Set mit allen Match-Daten von dem gelesen wird. Ist ein scrollable Set
	 * der von allen Methoden verwendet wird. id: IntegerInteger <br>
	 * name: String <br>
	 */
	private ResultSet mReadSet;

	/**
	 * MatchData versteckt, wird immer über Singelton aufgerufen
	 */
	private ConfigDbData() {
	}

	/**
	 * Singleton
	 * 
	 * @return
	 */
	public static ConfigDbData instance() {
		if (instance == null) {
			instance = new ConfigDbData();
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
	 * Ein key-value pair speichern, oder updaten, Datatype: String
	 */
	public void add(String key, String value) throws Exception {
		if (find(key) != null) {
			updateRow(key, value);
		} else {
			addRow(key, value);
		}
	}

	/**
	 * Ein key-value pair mit int Wert, speichern oder updaten
	 */
	public void add(String key, int value) throws Exception {
		String wert = Integer.toString(value);
		if (find(key) != null) {
			updateRow(key, wert);
		} else {
			addRow(key, wert);
		}
	}

	/**
	 * Ein key-value pair mit double Wert, speichern oder updaten
	 */
	public void add(String key, double value) throws Exception {
		String wert = Double.toString(value);
		if (find(key) != null) {
			updateRow(key, wert);
		} else {
			addRow(key, wert);
		}
	}

	/**
	 * Ein key-value pair mit Datum Wert, speichern oder updaten
	 */
	public void add(String key, Date value) throws Exception {
		String wert = Config.sdfDatum.format(value);
		if (find(key) != null) {
			updateRow(key, wert);
		} else {
			addRow(key, wert);
		}
	}

	/**
	 * Einen Wert lesen
	 * 
	 * @param key
	 * @return
	 */
	public String read(String key) throws SQLException {
		return find(key);
	}

	/**
	 * Das Speichern alle Einträge. Falls schon vorhanden wird update
	 */
	public void addAll(Map<String, String> map) throws Exception {
		for (Entry<String, String> pair : map.entrySet()) {
			if (find(pair.getKey()) != null) {
				updateRow(pair.getKey(), pair.getValue());
			} else {
				addRow(pair.getKey(), pair.getValue());
			}
		}
	}

	/**
	 * Das Lesen alle Einträge von der DB, col 1 = token, col 2 = value
	 */
	public Map<String, String> readAll(Map<String, String> keyValue) throws SQLException {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM config");
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			keyValue.put(mReadSet.getString(1), mReadSet.getString(2));
		}
		return keyValue;
	}

	/**
	 * Alle Config-Daten löschen
	 */
	public void deleteAll() throws SQLException {
		setupReadSet();
		mReadStmt.execute("TRUNCATE config");
	}

	// ------ interne Methoden -----------------------------------------

	/**
	 *
	 * return wert, oder
	 */

	/**
	 * Sucht einen Wert des Keys.
	 * 
	 * @param key
	 * @return den gefundenen Wert, oder null wenn nicht vorhandne
	 * @throws Exception
	 */
	private String find(String key) throws SQLException {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM config WHERE token = '" + key + "'");
		mReadSet.beforeFirst();
		if (mReadSet.next()) {
			return mReadSet.getString(2);
		}
		return null;
	}

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen. Der SQL-String wird
	 * zusammengestellt.
	 */
	private void addRow(String key, String value) throws SQLException {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO config VALUES ('");
		lQuery.append(key);
		lQuery.append("', '");
		lQuery.append(value);
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}

	/**
	 * Aendert die Attribute der gewählten Zeile.
	 */
	private void updateRow(String key, String value) throws SQLException {
		String lQuery = "UPDATE config SET wert = ? WHERE token = '";
		lQuery += (key + "'");
		PreparedStatement updateConfig = getConnection().prepareStatement(lQuery);
		updateConfig.setString(1, value);
		updateConfig.executeUpdate();
	}

	/**
	 * Setzt das Statement (Connection zur DB) und den Scroll-Set, der für Insert
	 * oder update verwendet werden kann.
	 */
	private synchronized void setupReadSet() throws SQLException {
		if (mReadStmt == null) {
			mReadStmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}
	}

}
