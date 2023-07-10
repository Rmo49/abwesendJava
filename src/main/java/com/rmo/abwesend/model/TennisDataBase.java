package com.rmo.abwesend.model;

import java.sql.Connection;
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

	private static String dbError;

	/**
	 * Prüft, ob eine DB vorhanden, liest die Tabelle config
	 *
	 * @return
	 * @throws Exception
	 */
	public static boolean dbExists() {
		if (dbError != null && ! dbError.isEmpty()) {
			// wenn eine Fehlermeldung vorhanden, dann bereits ein Versuch zu lesen
			return false;
		}
		else {
			dbError = "";
		}
		try {
			Connection conn = DbConnection.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();

			ResultSet tables = dbmd.getTables(null, null, ConfigDbData.tableName, null);
			if (tables.next()) {
				// Table exists
				return true;
			} else {
				dbError = "keine Tabellen in der DB gefunden";
				return false;
			}
		} catch (SQLException ex) {
			dbError = "TennisDataBase.dbExists(), Fehler: " + ex.getMessage();
			Trace.println(1, "TennisDataBase.dbExists(), Fehler: " + ex.getMessage());
			return false;
		}
	}

	/**
	 * Den Fehler bei DB öffnen zurückgeben
	 * @return Fehlermeldung
	 */
	public static String getDbError() {
		return dbError;
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
	public static void generateNewTables() throws SQLException {
		generateDb();

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
	}

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

		if (! gefunden) {
			try {
				Statement statement = DbConnection.getConnection().createStatement();
				String sql = "CREATE DATABASE " + Config.dbName;
				statement.executeUpdate(sql);
				statement.close();
			}
			catch (SQLException ex) {
				Trace.println(1, "TennisDataBase.generateDb(), Fehler: " + ex.getMessage());
			}
		}

	}
}
