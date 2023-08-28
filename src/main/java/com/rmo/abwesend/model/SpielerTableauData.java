package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



/**
 * Spieler-Tableau-Beziehung, Verbindung zur Tabelle SpielerTableau in der DB. Schnittstelle zur
 * DB. Tableau werden mit der Klasse Tableau sichtbar gemacht.
 */
public class SpielerTableauData {

	/**
	 * Das Create Statement für diese Tabelle
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append("CREATE TABLE IF NOT EXISTS spielertableau (");
		sb.append(" spielerID int unsigned NOT NULL, tableauID INT unsigned NOT NULL,");
		sb.append(" FOREIGN KEY (spielerID) REFERENCES Spieler(spielerID),");
		sb.append(" FOREIGN KEY (tableauID) REFERENCES Tableau(tableauID)");
		sb.append(");");
		return sb.toString();
	}

	public static String createTable2() {
		StringBuffer sb = new StringBuffer(300);
		sb.append ("CREATE TABLE IF NOT EXISTS spielerTableau (");
		sb.append (" spielerID int unsigned NOT NULL, tableauID INT unsigned NOT NULL,");
		sb.append (" PRIMARY KEY (spielerID, tableauID),");
		sb.append (" FOREIGN KEY (spielerID) REFERENCES Spieler(spielerID)");
		sb.append (" ON DELETE CASCADE,");
		sb.append (" FOREIGN KEY (tableauID) REFERENCES Tableau(tableauID)");
//		sb.append (" ON DELETE CASCADE");
		sb.append(");");
		return sb.toString();
	}

	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS spielertableau;");
	}


	private static SpielerTableauData instance = null;

	/**
	 * Enthält Connection zur DB. Wird in setupResultset gesetzt, bleibt während
	 * ganzer Sitzung erhalten.
	 */
	private Statement mReadStmt;

	/**
	 * Der Set mit allen Tableau-Daten von dem gelesen wird. Ist ein scrollable
	 * Set der von allen Methoden verwendet wird. id: IntegerInteger <br>
	 * tableauID: String <br>
	 */
	private ResultSet mReadSet;

	/**
	 * TableauData versteckt, wird immer über Singelton aufgerufen
	 */
	private SpielerTableauData() {
	}


	/**
	 * Singleton
	 * @return
	 */
	public static SpielerTableauData instance() {
		if (instance == null) {
			instance = new SpielerTableauData();
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
	 * Die Beziehungen Spieler / Tableau werden zusätzlich gespeichert.
	 * Falls eine Beziehung nicht vorhanden ist, wird diese aufgenommen.
	 */
	public void add(SpielerTableau pST) throws Exception {
		List<Integer> oldList = findAllTableaux(pST.getSpielerId());
		// iter über neue Liste, wenn nicht vorhanden dazufügen
		boolean found = false;
		for (Integer newId : pST.getTableauList()) {
			for (Integer oldId : oldList) {
				if (oldId.intValue() == newId.intValue()) {
					found = true;
					break;
				}
			}
			if (! found) {
				addRow(pST.getSpielerId(), newId.intValue());
			}
			found = false;
		}
	}


	/**
	 * Eine neue Beziehung speichern.
	 * Falls diese nicht vorhanden ist, wird diese aufgenommen.
	 */
	public void add(int spielerID, int tableauID) throws Exception {
		List<Integer> oldList = null;
		try {
			oldList = findAllTableaux(spielerID);
		}
		catch (Exception ex) {
			// nichts machen wenn nicht gefunden
			oldList = null;
		}

		// iter über alte Liste, wenn nicht vorhanden dazufügen
		boolean found = false;
		if (oldList != null) {
			for (Integer oldId : oldList) {
				if (oldId.intValue() == tableauID) {
					found = true;
					break;
				}
			}
		}
		if (! found) {
			addRow(spielerID, tableauID);
		}
	}


	/**
	 * Die Beziehungen Spieler / Tableau werden neu gespeichert.
	 * Falls eine Beziehung nicht vorhanden ist, wird diese aufgenommen.
	 * Beziehungen, die nicht mehr gelten, werden gelöscht.
	 */
	public void update(SpielerTableau pST) throws Exception {
		List<Integer> oldList = findAllTableaux(pST.getSpielerId());
		// iter über neue Liste, wenn nicht vorhanden dazufügen
		boolean found = false;
		for (Integer newId : pST.getTableauList()) {
			for (Integer oldId : oldList) {
				if (oldId.intValue() == newId.intValue()) {
					found = true;
					break;
				}
			}
			if (! found) {
				addRow(pST.getSpielerId(), newId.intValue());
			}
			found = false;
		}
		// iter über alte Liste, wenn in neu nicht gefunden, dann löschen
		found = false;
		for (Integer oldId : oldList) {
			for (Integer newId : pST.getTableauList()) {
				if (oldId.intValue() == newId.intValue()) {
					found = true;
					break;
				}
			}
			if (! found) {
				deleteRow(pST.getSpielerId(), oldId);
			}
			found = false;
		}
	}


	/**
	 * Alle TableauId wo der Spieler spielt.
	 * @param spielerId
	 * @return Liste mit allen tableauId
	 */
	public List<Tableau> readAllSpielerTableau(int spielerId) throws Exception {
		List<Integer> intList = findAllTableaux(spielerId);
		List<Tableau> tableauList = new ArrayList<>();
		for (Integer id : intList) {
			tableauList.add(TableauData.instance().read(id));
		}
		return tableauList;
	}


	/**
	 * Alle TableauId wo der Spieler spielt.
	 * @param spielerId
	 * @return Liste mit allen tableauId
	 */
	public List<Integer> readAllTableau(int spielerId) throws Exception {
		return findAllTableaux(spielerId);
	}

	/**
	 * Alle SpielerID eines Tableaux.
	 * @param tableauId
	 * @return Liste mit allen spielerId
	 */
	public List<Integer> readAllSpieler(int tableauId) throws Exception {
		return findAllSpieler(tableauId);
	}

	/**
	 * Die Spieler eines Tableau
	 * @param die Id des Tableau
	 * @return
	 */
	public List<Spieler> readSpielerOfTableau(int tableauId) {
		List<Spieler> spielerList = null;
		try {
			List<Integer> spielerIdList = readAllSpieler(tableauId);
			spielerList = new ArrayList<>();
			for (Integer spielerId : spielerIdList) {
				Spieler lSpieler = SpielerData.instance().read(spielerId.intValue());
				spielerList.add(lSpieler);
			}
//			spielerList.sort(spielerComparator);
		}
		catch (Exception ex) {
//			alertError("Probleme bei Tableau lesen.", ex);
		}
		return spielerList;
	}


	/**
	 * Alle Daten löschen.
	 */
	public void deleteAllRow() throws Exception {
		String lQuery = "TRUNCATE spielertableau;";
		Statement statement = getConnection().createStatement();
		statement.executeUpdate(lQuery);
		statement.close();
	}

	/**
	 * Alle Spieler von einem Tableau entfernen
	 */
	public void deleteAllSpielerFromTableau(int tableauId) throws Exception  {
		List<Integer> spielerIdList = readAllSpieler(tableauId);
		for (Integer spielerId : spielerIdList) {
			deleteRow(spielerId, tableauId);
		}
	}

	// ------ interne Methoden -----------------------------------------

	/**
	 * Sucht alle Tableaus eines Spielers, gibt die Liste der TableauIDs zurück.
	 * @param pSpielerId
	 * @return Liste mit TableauID wo Spieler spielt
	 * @throws Exception
	 */
	private List<Integer> findAllTableaux(int pSpielerId) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		StringBuffer lQuery = new StringBuffer(100);
		lQuery.append("SELECT tableauID FROM spielertableau WHERE spielerID = '");
		lQuery.append(pSpielerId);
		lQuery.append("';");
		mReadSet = mReadStmt.executeQuery(lQuery.toString());
		ArrayList<Integer> list = new ArrayList<>();
//		int[] tableaux = new int[8];
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			list.add(Integer.valueOf(mReadSet.getInt(1)));
		}
		return list;
	}

	/**
	 * Sucht alle Spiele eines Tableaus, gibt die Liste der TableauIDs zurück.
	 * @param pSpielerId
	 * @return Liste mit TableauID wo Spieler spielt
	 * @throws Exception
	 */
	private List<Integer> findAllSpieler(int pTableauId) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		StringBuffer lQuery = new StringBuffer(100);
		lQuery.append("SELECT spielerID FROM spielertableau WHERE tableauID = '");
		lQuery.append(pTableauId);
		lQuery.append("';");
		mReadSet = mReadStmt.executeQuery(lQuery.toString());
		List<Integer> list = new ArrayList<>();
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			list.add(Integer.valueOf(mReadSet.getInt(1)));
		}
		return list;
	}

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen. Kopiert die Attribute vom
	 * ResultSet in das Objekt Tableau. Der SQL-String wird zusammengestellt.
	 */
	private void addRow(int spielerId, int tableauId) throws Exception {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO spielertableau VALUES (");
		lQuery.append(spielerId);
		lQuery.append(", ");
		lQuery.append(tableauId);
		lQuery.append(");");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}

	/**
	 * Aendert die Attribute der gewählten Zeile.
	 */
	private void deleteRow(Integer spielerId, Integer tableauId) throws Exception {
		StringBuffer lQuery = new StringBuffer(80);
		lQuery.append("DELETE FROM spielertableau WHERE SpielerID = ");
		lQuery.append(spielerId);
		lQuery.append(" AND TableauId = ");
		lQuery.append(tableauId);
		lQuery.append(";");
		PreparedStatement deleteTupel = getConnection().prepareStatement(lQuery.toString());
		deleteTupel.executeUpdate();
	}


	/**
	 * Setzt das Statement (Connection zur DB) und den Scroll-Set, der für
	 * Insert oder update verwendet werden kann.
	 */
	private synchronized void setupReadSet() throws Exception {
		if (mReadStmt == null) {
			mReadStmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}
	}

}
