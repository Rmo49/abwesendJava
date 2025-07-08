package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Tableau-Model, Verbindung zu Tableau-Tabelle in der DB. Schnittstelle zur DB.
 * Tableau werden mit der Klasse Tableau sichtbar gemacht.
 */
public class TableauData {

	/**
	 * Das Create Statement für diese Tabelle
	 * 
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS tableau (");
		sb.append(
				" tableauID INT unsigned NOT NULL AUTO_INCREMENT, bezeichnung VARCHAR(50) DEFAULT NULL, position VARCHAR(2) DEFAULT NULL,");
		sb.append(" konkurrenz VARCHAR(50) DEFAULT NULL, PRIMARY KEY (tableauID)");
		sb.append(");");
		return sb.toString();
	}

	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS tableau;");
	}

	private static TableauData instance = null;

	/**
	 * Enthält Connection zur DB. Wird in setupResultset gesetzt, bleibt während
	 * ganzer Sitzung erhalten.
	 */
	private Statement mReadStmt;

	/**
	 * Der Set mit allen Tableau-Daten von dem gelesen wird. Ist ein scrollable Set
	 * der von allen Methoden verwendet wird. id: IntegerInteger <br>
	 * tableauID: String <br>
	 */
	private ResultSet mReadSet;

	/**
	 * TableauData versteckt, wird immer über Singelton aufgerufen
	 */
	private TableauData() {
	}

	/**
	 * Singleton
	 * 
	 * @return
	 */
	public static TableauData instance() {
		if (instance == null) {
			instance = new TableauData();
		}
		return instance;
	}

	/**
	 * Die Connection zu der DB.
	 */
	protected Connection getConnection() throws Exception {
		return DbConnection.getConnection();
	}

	/**
	 * Das Tableau wird gespeichert. Falls die TableauNr nicht vorhanden ist, wird
	 * ein neuer Tableau angelegt
	 */
	public void add(Tableau pTableau) throws Exception {
		if (pTableau.getBezeichnung().length() < 2) {
			throw new SQLException("Tableau Bezeichung eingeben");
		}
		if (pTableau.getId() <= 0) {
			// nicht gelesen von DB, vielleicht aber schon vorhanden.
			if (findBezeichnung(pTableau.getBezeichnung())) {
				throw new SQLException("Tableau mit diesem Namen schon vorhanden: " + pTableau.getBezeichnung());
			}
			// wenn nicht gefunden, neues anlegen
			addRow(pTableau);
		} else {
			if (findRow(pTableau.getId())) {
				updateRow(pTableau);
			} else {
				// wenn nicht gefunden, neues anlegen
				addRow(pTableau);
			}
		}
	}

	/**
	 * Das Tableau mit der id wird zurückgegeben. Wenn nicht gefunden wird
	 * TableauNotFoundException geworfen.
	 */
	public Tableau read(int pTableauId) throws Exception {
		if (findRow(pTableauId)) {
			mReadSet.refreshRow();
			Tableau lTableau = copyToTableau();
			return lTableau;
		} else {
			throw new SQLException("Tableau nicht gefunden, ID: " + pTableauId);
		}
	}

	/**
	 * Das Tableau mit dem namen wird zurückgegeben. Wenn nicht gefunden wird
	 * SQLException geworfen.
	 */
	public Tableau readBezeichnung(String pTableauName) throws Exception {
		if (findBezeichnung(pTableauName)) {
			mReadSet.refreshRow();
			Tableau lTableau = copyToTableau();
			return lTableau;
		} else {
			throw new SQLException("Tableau nicht gefunden: " + pTableauName);
		}
	}

	/**
	 * Das Tableau mit der Swiss-Tennis bezeichung wird zurückgegeben. Wenn nicht
	 * gefunden wird eine SQLException geworfen.
	 */
	public Tableau readKonkurrenz(String pKonkurrenz) throws Exception {
		if (findKonkurrenz(pKonkurrenz)) {
			mReadSet.refreshRow();
			Tableau lTableau = copyToTableau();
			return lTableau;
		} else {
			throw new SQLException("Tableau nicht gefunden: " + pKonkurrenz);
		}
	}

	/**
	 * Alle Tableaux geordnet nach position zurückgeben
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Tableau> readAllTableau() throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		ArrayList<Tableau> tableauList = new ArrayList<>();
		mReadSet = mReadStmt.executeQuery("SELECT * FROM tableau ORDER by position;");
		while (mReadSet.next()) {
			Tableau lTableau = new Tableau();
			lTableau.setId(mReadSet.getInt(1));
			lTableau.setBezeichnung(mReadSet.getString(2));
			lTableau.setPosition(mReadSet.getString(3));
			lTableau.setKonkurrenz(mReadSet.getString(4));

			tableauList.add(lTableau);
		}
		return tableauList;
	}

	/**
	 * Das Tableau mit der Nummer pTableauNr wird gelöscht. Falls die TableauNr
	 * nicht vorhanden ist, wird die Exception TableauNotFoundException geworfen.
	 */
	public void delete(int pTableauNr) throws Exception {
		if (findRow(pTableauNr)) {
			mReadSet.deleteRow();
		} else {
			throw new SQLException("Tableau " + pTableauNr + " nicht gelöscht!");
		}
	}

	// ------ interne Methoden -----------------------------------------

	/**
	 * Sucht die Row mit der Tableaunummer. Wenn true, steht mReadSet auf dieser
	 * Zeile.
	 */
	private boolean findRow(int pTableauNr) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM tableau ORDER BY tableauID");
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			if (mReadSet.getInt(1) == pTableauNr) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sucht den Tableau über Bezeichnung. Wenn true, steht mReadSet auf dieser
	 * Zeile.
	 */
	private boolean findBezeichnung(String bezeichnung) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		StringBuffer lQuery = new StringBuffer(100);
		lQuery.append("SELECT * FROM tableau WHERE bezeichnung LIKE '%");
		lQuery.append(bezeichnung);
		lQuery.append("%';");
		mReadSet = mReadStmt.executeQuery(lQuery.toString());
		if (mReadSet.next()) {
			return true;
		}
		return false;
	}

	/**
	 * Sucht den Tableau über Konkurrenz, die Bezeichnung von Swisstennis. Wenn
	 * true, steht mReadSet auf dieser Zeile.
	 */
	private boolean findKonkurrenz(String konkurrenz) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		StringBuffer lQuery = new StringBuffer(100);
		lQuery.append("SELECT * FROM tableau WHERE konkurrenz LIKE '");
		lQuery.append(konkurrenz);
		lQuery.append("%';");
		mReadSet = mReadStmt.executeQuery(lQuery.toString());
		if (mReadSet.next()) {
			return true;
		}
		return false;
	}

	/**
	 * Kopiert die Attribute vom ResultSet in das Objekt Tableau
	 */
	private Tableau copyToTableau() throws SQLException {
		Tableau pTableau = new Tableau();
		pTableau.setId(mReadSet.getInt(1));
		pTableau.setBezeichnung(mReadSet.getString(2));
		pTableau.setPosition(mReadSet.getString(3));
		pTableau.setKonkurrenz(mReadSet.getString(4));
		return pTableau;
	}

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen. Kopiert die Attribute vom
	 * ResultSet in das Objekt Tableau. Der SQL-String wird zusammengestellt.
	 */
	private void addRow(Tableau pTableau) throws Exception {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO tableau VALUES (");
		lQuery.append("null, '");
		lQuery.append(pTableau.getBezeichnung());
		lQuery.append("', '");
		lQuery.append(pTableau.getPosition());
		lQuery.append("', '");
		lQuery.append(pTableau.getKonkurrenz());
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}

	/**
	 * Aendert die Attribute der gewählten Zeile.
	 */
	private void updateRow(Tableau pTableau) throws Exception {
		String lQuery = "UPDATE tableau SET bezeichnung = ?, position = ?, konkurrenz = ?  WHERE TableauID = ";
		lQuery += pTableau.getId();
		PreparedStatement updateTableau = getConnection().prepareStatement(lQuery);
		updateTableau.setString(1, pTableau.getBezeichnung());
		updateTableau.setString(2, pTableau.getPosition());
		updateTableau.setString(3, pTableau.getKonkurrenz());
		updateTableau.executeUpdate();
	}

	/**
	 * Setzt das Statement (Connection zur DB) und den Scroll-Set, der für Insert
	 * oder update verwendet werden kann.
	 */
	private synchronized void setupReadSet() throws Exception {
		if (mReadStmt == null) {
			mReadStmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}
	}

}
