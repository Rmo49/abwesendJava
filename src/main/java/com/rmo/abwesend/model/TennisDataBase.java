package com.rmo.abwesend.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.Trace;

/**
 * Verwaltet die Datenbank weiss wie die Tabellen anlegen. Kennt den namen der
 * geöffneten DB.
 *
 * @author Ruedi
 */
public class TennisDataBase {

	/** Verbindung zu Data bean */
	private static String useName = "USE " + Config.dbName;
	public static final int numberOfTabels = 7;
	
	private static String dbError;
	private static String tableMissing;

	/**
	 * Prüft, ob eine DB vorhanden, liest die Tabelle config
	 *
	 * @return
	 * @throws Exception
	 */
	public static boolean dbExists() {
		if (dbError != null && !dbError.isEmpty()) {
			// wenn eine Fehlermeldung vorhanden, dann bereits ein Versuch zu lesen
			return false;
		} else {
			dbError = "";
		}
		try {
			// Connection aufbauen
			DbConnection.getConnection();
		} catch (SQLException ex) {
			dbError = "TennisDataBase.dbExists(), Fehler: " + ex.getMessage();
			Trace.println(1, "TennisDataBase.dbExists(), Fehler: " + ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Den Fehler bei DB öffnen zurückgeben
	 * 
	 * @return Fehlermeldung
	 */
	public static String getDbError() {
		return dbError;
	}

	/**
	 * Prüfen, ob alle Tabellen vorhanden sind.
	 * 
	 * @return
	 */
	public static boolean allTablesExist() {
		StringBuffer tableFehlt = new StringBuffer();
		checkTable(ConfigDbData.tableName, tableFehlt);
		checkTable(BenutzerData.tableName, tableFehlt);

		// TODO weitere Tabllen dazufügen
		if (tableFehlt.length() < 1) {
			return true;
		}
		tableMissing = tableFehlt.toString();
		return false;
	}

	/**
	 * Tabelle prüfen, wenn nicht existiert, Name dem Buffer anhängen.
	 * 
	 * @param tableName, die Tabekke
	 * @param noTable,   die Liste der Tabllen
	 * @return
	 */
	private static StringBuffer checkTable(String tableName, StringBuffer noTable) {
		if (!tableExist(tableName)) {
			noTable.append(tableName);
			noTable.append(", ");
		}
		return noTable;
	}

	/**
	 * Prüfen, ob die Tabelle existiert
	 */
	public static boolean tableExist(String tableName) {
		try {
			DatabaseMetaData dbmd = DbConnection.getConnection().getMetaData();

			// prüfen, ob Tabelle exisitert
			ResultSet tables = dbmd.getTables(null, null, tableName, new String[] { "TABLE" });
			if (tables.next()) {
				// Table exists
//				String na = tables.getString("Table_NAME");
				// System.out.println(na);
				return true;
			} else {
				return false;
			}
		} catch (SQLException ex) {
			dbError = "TennisDataBase.tableExists(), Fehler: " + ex.getMessage();
			Trace.println(1, "TennisDataBase.tableExists(), Fehler: " + ex.getMessage());
			return false;
		}
	}

	/**
	 * Prüfen, ob alle Tabellen voerhanden sind.
	 */
	public static int countTables(String dbName) {
		int anzahl = 0;
		try {
			DatabaseMetaData dbmd = DbConnection.getConnection().getMetaData();
			// prüfen, ob Tabelle exisitert
			ResultSet tables = dbmd.getTables(dbName, null, "%", new String[] { "TABLE" });

			while (tables.next()) {
				anzahl++;
				// Table exists
				// String na = tables.getString("Table_NAME");
				// System.out.println(na);
			}
		} catch (SQLException ex) {
			dbError = "TennisDataBase.tableExists(), Fehler: " + ex.getMessage();
			Trace.println(1, "TennisDataBase.tableExists(), Fehler: " + ex.getMessage());
			return 0;
		}
		return anzahl;
	}

	public static String getTableMissing() {
		return tableMissing;
	}

	/**
	 * Alle Tabellen löschen.
	 *
	 * @throws SQLException
	 */
	public static void deleteAllTables() throws SQLException {
		Statement statement = DbConnection.getConnection().createStatement();
		statement.execute(useName);
//		statement.execute(SpielerTableauData.dorpTable());
//		statement.execute(MatchData.dorpTable());
//		statement.execute(SpielerData.dorpTable());
//		statement.execute(TableauData.dorpTable());
//		statement.execute(ConfigDbData.dorpTable());
//		statement.execute(TraceDbData.dorpTable());
		statement.execute(MailData.dorpTable());
		statement.close();
	}

	/**
	 * Alle Tabellen neu anlegen
	 *
	 * @throws SQLException
	 */
	public static boolean generateNewTables() {
		generateDb();
		try {
			Statement statement = DbConnection.getConnection().createStatement();

			statement.execute(useName);
			statement.execute(SpielerData.createTable());
			statement.execute(TableauData.createTable());
			statement.execute(SpielerTableauData.createTable2());
			statement.execute(MatchData.createTable2());
			statement.execute(ConfigDbData.createTable());
			statement.execute(TraceDbData.createTable());
			statement.execute(MailData.createTable());

			statement.close();
		} catch (SQLException ex) {
			Trace.println(1, "TennisDataBase.generateNewTables(), Fehler: " + ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Eine neue DB anlegen
	 */
	public static void generateDb() {
		boolean gefunden = false;
		try {
			ResultSet resultSet = DbConnection.getConnection().getMetaData().getCatalogs();

			while (resultSet.next()) {
				String databaseName = resultSet.getString(1);
				if (databaseName.equalsIgnoreCase(Config.dbName)) {
					gefunden = true;
				}
			}
			resultSet.close();
		} catch (Exception ex) {
			Trace.println(1, "TennisDataBase.generateDb(), Fehler: " + ex.getMessage());
		}

		if (!gefunden) {
			try {
				Statement statement = DbConnection.getConnection().createStatement();
				String sql = "CREATE DATABASE " + Config.dbName;
				statement.executeUpdate(sql);
				statement.close();
			} catch (SQLException ex) {
				Trace.println(1, "TennisDataBase.generateDb(), Fehler: " + ex.getMessage());
			}
		}
	}
}
