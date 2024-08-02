package com.rmo.abwesend.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Spieler-Model, Verbindung zu Spieler in der DB. Schnittstelle zur DB. Spieler
 * werden mit der Klasse Spieler sichtbar gemacht.
 */
public class SpielerData {

	/**
	 * Das Create Statement für diese Tabelle
	 * 
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS spieler (");
		sb.append(" spielerID INT unsigned NOT NULL AUTO_INCREMENT, name VARCHAR(30) DEFAULT NULL,");
		sb.append(" vorname VARCHAR(30) DEFAULT NULL, abwesendArray VARCHAR(180) DEFAULT NULL,");
		sb.append(" email VARCHAR(50) DEFAULT NULL,");
		sb.append(" PRIMARY KEY (spielerID)");
		sb.append(");");
		return sb.toString();
	}

	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS spieler;");
	}

	private static SpielerData instance = null;
	private PropertyChangeListener myListener = null;

	/**
	 * Enthält Connection zur DB. Wird in setupResultset gesetzt, bleibt während
	 * ganzer Sitzung erhalten.
	 */
	private Statement mReadStmt;

	/**
	 * Der Set mit allen Spieler-Daten von dem gelesen wird. Ist ein scrollable Set
	 * der von allen Methoden verwendet wird. id: IntegerInteger <br>
	 * name: String <br>
	 */
	private ResultSet mReadSet;

	/**
	 * SpielerData versteckt, wird immer über Singelton aufgerufen
	 */
	private SpielerData() {
	}

	/**
	 * Singleton
	 * 
	 * @return
	 */
	public static SpielerData instance() {
		if (instance == null) {
			instance = new SpielerData();
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
	 * Das Spieler wird gesucht und die SpielerID zurückgegeben. Falls der Spieler
	 * nicht vorhanden ist, wird ein neuer Spieler angelegt.
	 * 
	 * @param pSpieler
	 * @return die ID des Spielers
	 * @throws Exception wenn der Name zu kurz
	 */
	public int add(Spieler pSpieler) throws Exception {
		int spielerID = -1;
		if (pSpieler.getId() < 0) {
			if ((pSpieler.getName().length() < 3) || (pSpieler.getVorName().length() < 3)) {
				throw new SQLException("Name und Vorname eingeben");
			}
			// nicht gelesen von DB, vielleicht aber schon vorhanden.
			spielerID = findRow(pSpieler.getName(), pSpieler.getVorName());
			if (spielerID >= 0) {
				return spielerID;
			} else {
				// wenn nicht gefunden, neue Spieler anlegen
				addRow(pSpieler);
				// nochmals lesen, für die neue Nummer
				spielerID = findRow(pSpieler.getName(), pSpieler.getVorName());
			}
		} else {
			if (findRow(pSpieler.getId())) {
				updateRow(pSpieler);
				spielerID = pSpieler.getId();
			} else {
				// wenn nicht gefunden, neuen Spieler anlegen
				addRow(pSpieler);
				spielerID = findRow(pSpieler.getName(), pSpieler.getVorName());
			}
		}
		// an dieser Stelle sollte der Spieler in der DB sein

		notifyListeners(this, "spielerID", "-1", Integer.toString(spielerID));
		return spielerID;
	}

	/**
	 * Das Spieler mit der id wird zurückgegeben. Wenn nicht gefunden wird
	 * SpielerNotFoundException geworfen.
	 */
	public Spieler read(int pSpielerId) throws Exception {
		if (findRow(pSpielerId)) {
			mReadSet.refreshRow();
			Spieler lSpieler = copyToSpieler(mReadSet);
			return lSpieler;
		} else {
			throw new SQLException("Spieler nicht gefunden, ID: " + pSpielerId);
		}
	}

	/**
	 * Das Spieler mit der pSpielerId wird zurückgegeben. Wenn nicht gefunden wird
	 * SpielerNotFoundException geworfen.
	 */
	public Spieler read(String pSpielerId) throws Exception {
		int lSpielerNr = Integer.valueOf(pSpielerId).intValue();
		if (lSpielerNr > 0) {
			return read(lSpielerNr);
		} else {
			throw new SQLException("Spieler nicht gefunden, ID: " + pSpielerId);
		}
	}

	/**
	 * Das Spieler mit diesem Namen wird zurückgegeben. Wenn nicht gefunden wird
	 * SpielerNotFoundException geworfen.
	 */
	public Spieler read(String name, String vorname) throws Exception {
		if (findRow(name, vorname) >= 0) {
			mReadSet.refreshRow();
			Spieler lSpieler = copyToSpieler(mReadSet);
			return lSpieler;
		} else {
			throw new SQLException("Spieler nicht gefunden: " + name + " " + vorname);
		}
	}

	/**
	 * Die SpielerId wird zurückgegeben. Wenn nicht gefunden wird -1 rurückgegeben
	 */
	public int readId(String name, String vorname) throws Exception {
		return findRow(name, vorname);
	}

	/**
	 * Das Spieler mit der id wird zurückgegeben. Wenn nicht gefunden wird
	 * SpielerNotFoundException geworfen.
	 */
	public SpielerKurz readKurz(int pSpielerId) throws Exception {
		if (findRow(pSpielerId)) {
			mReadSet.refreshRow();
			SpielerKurz lSpieler = copyToSpielerKurz(mReadSet);
			return lSpieler;
		} else {
			throw new SQLException("Spieler nicht gefunden, ID: " + pSpielerId);
		}
	}

	/**
	 * Alle Spieler mit Name und Vorname zurückgeben.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SpielerKurz> readAllKurz() throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		ArrayList<SpielerKurz> nameList = new ArrayList<>();
		mReadSet = mReadStmt.executeQuery("SELECT spielerID, name, vorname FROM spieler ORDER by name, vorname;");
		while (mReadSet.next()) {
			SpielerKurz spielerKurz = new SpielerKurz();
			spielerKurz.setId(mReadSet.getInt(1));
			spielerKurz.setName(mReadSet.getString(2) + ", " + mReadSet.getString(3));
			nameList.add(spielerKurz);
		}
		return nameList;
	}

	/**
	 * Alle Spieler mit Name und Vorname zurückgeben.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Spieler> readAllSpieler() throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		ArrayList<Spieler> spielerList = new ArrayList<>();
		mReadSet = mReadStmt.executeQuery("SELECT * FROM spieler ORDER by name, vorname;");
		while (mReadSet.next()) {
			Spieler spieler = copyToSpieler(mReadSet);
			spielerList.add(spieler);
		}
		return spielerList;
	}

	/**
	 * Das Spieler mit der Nummer pSpielerNr wird gelöscht. Falls die SpielerNr
	 * nicht vorhanden ist, wird die Exception SpielerNotFoundException geworfen.
	 */
	public void delete(int pSpielerID) throws Exception {
		if (findRow(pSpielerID)) {
			mReadSet.deleteRow();
		} else {
			throw new SQLException("Spieler " + pSpielerID + " nicht gelöscht!");
		}
	}

	/**
	 * Alle Daten löschen, zuerst muss SpielerTableau gelöscht werden
	 */
	public void deleteAllRow() throws Exception {
		String lQuery = "DELETE FROM spieler;";
		Statement statement = getConnection().createStatement();
		statement.executeUpdate(lQuery);
		statement.close();
	}

	/**
	 * Wenn an Aenderungen interessiert. Listener von der gleichen Klasse nur einmal
	 * dazufügen.
	 *
	 * @param newListener
	 */
	public void addChangeListener(PropertyChangeListener newListener) {
		myListener = newListener;
	}

	// ------ interne Methoden -----------------------------------------

	/**
	 * Ruft immer den letzten registrierten Listeners, sonst wird der gleiche
	 * Listener mehrmals registriert.
	 */
	private void notifyListeners(Object object, String property, String oldValue, String newValue) {
		if (myListener != null) {
			myListener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
//		for (PropertyChangeListener name : myListeners) {
//			name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
//		}
	}

	/**
	 * Sucht die Row mit der Spielernummer. Wenn true, steht mReadSet auf dieser
	 * Zeile.
	 */
	private boolean findRow(int pSpielerID) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM spieler ORDER BY spielerID");
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			if (mReadSet.getInt(1) == pSpielerID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sucht den Spieler über Namen. Wenn true, steht mReadSet auf dieser Zeile.
	 * 
	 * @param name
	 * @param vorname
	 * @return die ID des Spielers
	 * @throws Exception
	 */
	private int findRow(String name, String vorname) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		StringBuffer lQuery = new StringBuffer(100);
		lQuery.append("SELECT * FROM spieler WHERE name LIKE '%");
		lQuery.append(name.trim());
		lQuery.append("%' AND vorname LIKE '%");
		lQuery.append(vorname.trim());
		lQuery.append("%';");
		mReadSet = mReadStmt.executeQuery(lQuery.toString());
		if (mReadSet.next()) {
			return mReadSet.getInt(1);
		}
		return -1;
	}

	/**
	 * Kopiert die Attribute vom ResultSet in das Objekt Spieler
	 */
	private Spieler copyToSpieler(ResultSet readSet) throws SQLException {
		Spieler pSpieler = new Spieler();
		pSpieler.setId(readSet.getInt(1));
		pSpieler.setName(readSet.getString(2));
		pSpieler.setVorName(readSet.getString(3));
		pSpieler.setAbwesendList(readSet.getString(4));
		pSpieler.setEmail(readSet.getString(5));
		pSpieler.setSpieleList(" ");
		return pSpieler;
	}

	/**
	 * Kopiert die Attribute vom ResultSet in das Objekt Spieler
	 */
	private SpielerKurz copyToSpielerKurz(ResultSet readSet) throws SQLException {
		SpielerKurz pSpieler = new SpielerKurz();
		pSpieler.setId(readSet.getInt(1));
		pSpieler.setName(readSet.getString(2) + ", " + readSet.getString(3));
		return pSpieler;
	}

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen. Kopiert die Attribute vom
	 * ResultSet in das Objekt Spieler. Der SQL-String wird zusammengestellt.
	 */
	private void addRow(Spieler pSpieler) throws Exception {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO spieler VALUES (");
//		lQuery.append(pSpieler.getiD());
		lQuery.append("null, '");
		lQuery.append(pSpieler.getName());
		lQuery.append("', '");
		lQuery.append(pSpieler.getVorName());
		lQuery.append("', '");
		lQuery.append(pSpieler.getAbwesendAsString());
		lQuery.append("', '");
		lQuery.append(pSpieler.getEmail());
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}

	/**
	 * Aendert die Attribute der gewählten Zeile.
	 */
	private void updateRow(Spieler pSpieler) throws Exception {
		String lQuery = "UPDATE spieler SET name = ? , vorname = ? , abwesendArray = ? , email = ? WHERE SpielerID = ";
		lQuery += pSpieler.getId();
		PreparedStatement updateSpieler = getConnection().prepareStatement(lQuery);
		updateSpieler.setString(1, pSpieler.getName());
		updateSpieler.setString(2, pSpieler.getVorName());
		updateSpieler.setString(3, pSpieler.getAbwesendAsString());
		updateSpieler.setString(4, pSpieler.getEmail());
		updateSpieler.executeUpdate();
	}

	/**
	 * Setzt das Statement (Connection zur DB) und den Scroll-Set, der für Insert
	 * oder update verwendet werden kann.
	 */
	private synchronized void setupReadSet() throws Exception {
		if (mReadStmt == null) {
			mReadStmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}
//		if (mReadSet == null) {
//			mReadSet = mReadStmt.executeQuery("SELECT * FROM spieler ORDER BY Name");
//		}
	}

}
