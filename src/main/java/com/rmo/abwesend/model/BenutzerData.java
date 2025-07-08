package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Verbindung zur Tabelle User in der DB.
 */
public class BenutzerData {

	public static final String tableName = "benutzer";
	/**
	 * Das Create Statement für diese Tabelle
	 * 
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(tableName);
		sb.append(" name VARCHAR(20) NOT NULL, passwort VARCHAR(20) DEFAULT NULL");
		sb.append(");");
		return sb.toString();
	}

	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS benutzer;");
	}

	private static BenutzerData instance = null;

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
	private BenutzerData() {
	}

	/**
	 * Singleton
	 * 
	 * @return
	 */
	public static BenutzerData instance() {
		if (instance == null) {
			instance = new BenutzerData();
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
	 * Einen Benuter speichern, oder updaten
	 */
	public void add(Benutzer pBenutzer) throws Exception {
		if (find(pBenutzer.getName()) != null) {
			updateRow(pBenutzer);
		} else {
			addRow(pBenutzer);
		}
	}

	/**
	 * Einen Benuter löschen
	 */
	public void delete(String name) throws Exception {
		if (find(name) != null) {
			deleteRow(name);
		}
	}

	/**
	 * Einen Benutzer lesen
	 * 
	 * @param
	 * @return
	 */
	public String read(String name) throws SQLException {
		return find(name);
	}

	/**
	 * Das Speichern alle Einträge. Falls schon vorhanden wird update
	 */
	public void addAll(List<Benutzer> benutzerList) throws Exception {
		for (Benutzer lBenutzer : benutzerList) {
			if (find(lBenutzer.getName()) != null) {
				updateRow(lBenutzer);
			} else {
				addRow(lBenutzer);
			}
		}
	}

	/**
	 * Das Lesen aller Benutzer
	 */
	public List<Benutzer> readAll(List<Benutzer> benutzerList) throws SQLException {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM benutzer");
		mReadSet.beforeFirst();
		Benutzer lBenutzer;
		while (mReadSet.next()) {
			lBenutzer = new Benutzer(mReadSet.getString(1), mReadSet.getString(2));
			benutzerList.add(lBenutzer);
		}
		return benutzerList;
	}

	/**
	 * Alle benutzer-Daten löschen
	 */
	public void deleteAll() throws SQLException {
		setupReadSet();
		mReadStmt.execute("TRUNCATE benutzer");
	}

	// ------ interne Methoden -----------------------------------------

	/**
	 * Sucht einen benutzer
	 * 
	 * @param name
	 * @return das Passwort, oder null wenn nicht vorhanden
	 * @throws Exception
	 */
	private String find(String name) throws SQLException {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM benutzer WHERE name = '" + name + "'");
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
	private void addRow(Benutzer pBenutzer) throws SQLException {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO benutzer VALUES ('");
		lQuery.append(pBenutzer.getName());
		lQuery.append("', '");
		lQuery.append(pBenutzer.getPasswort());
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}

	/**
	 * Aendert die Attribute der gewählten Zeile.
	 */
	private void updateRow(Benutzer pBenutzer) throws SQLException {
		String lQuery = "UPDATE benutzer SET passwort = ? WHERE name = '";
		lQuery += (pBenutzer.getName() + "'");
		PreparedStatement updateBenutzer = getConnection().prepareStatement(lQuery);
		updateBenutzer.setString(1, pBenutzer.getPasswort());
		updateBenutzer.executeUpdate();
	}

	/**
	 * Löschen eines Benutzers
	 */
	private void deleteRow(String name) throws SQLException {
		StringBuffer lQuery = new StringBuffer(80);
		lQuery.append("DELETE FROM benutzer WHERE name = '");
		lQuery.append(name);
		lQuery.append("';");
		PreparedStatement deleteTupel = getConnection().prepareStatement(lQuery.toString());
		deleteTupel.executeUpdate();
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
