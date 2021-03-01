package com.rmo.abwesend.model;

import java.sql.SQLException;
import java.sql.Statement;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.Trace;

/**
 * Verwaltet die Datenbank weiss wie die Tabellen anlegen.
 * Kennt den namen der geöffneten DB.
 * 
 * @author Ruedi
 */
public class TennisDataBase {
	
	/** Verbindung zu Data bean */
	private static String useName = "USE tennis";

	// --- Control-Vars
	/** Eine DB öffnen, die connection setzen */
	public static void openDb (String dbName) throws Exception {
		Trace.println(1, "DataBase.open(name: " + dbName + ")");
		// Connection öffnen
		DbConnection.open(Config.dbNname);
	}

	/**
	 * Alle Tabellen löschen.
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
	 * @throws SQLException
	 */
	public static void newTables() throws SQLException {
		Statement statement = DbConnection.getConnection().createStatement();
		statement.execute(useName);
//		statement.execute(SpielerData.createTable());
//		statement.execute(TableauData.createTable());
//		statement.execute(SpielerTableauData.createTable2());
//		statement.execute(MatchData.createTable());
//		statement.execute(ConfigDbData.createTable());
//		statement.execute(TraceDbData.createTable());
		statement.execute(MailData.createTable());
		
		statement.close();
	}

}
