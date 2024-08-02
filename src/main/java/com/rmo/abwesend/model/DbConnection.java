package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.DbPasswordFile;
import com.rmo.abwesend.util.Trace;

/**
 * Connection zur MySql-DB.
 */
public class DbConnection {
	private static Connection sConnection = null; // Verbindung zu DB
	// --- connection to Access-DB
	// public static final String sJdbcDriver = "sun.jdbc.odbc.JdbcOdbcDriver";
	// --- connection to MySql
	public static final String sJdbcDriver = "com.mysql.cj.jdbc.Driver";
	// public static final String url = "jdbc:mysql://localhost:3306/";
//	jdbc:mysql://localhost/db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
	private static DbPasswordFile dbPwFile = null;

	public DbConnection() {
	}

	/**
	 * Prüfen, ob die Verbindung zur DB bereits besteht.
	 * 
	 * @return
	 */
	public static boolean isConnected() {
		try {
			if (sConnection == null || sConnection.isClosed()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException ex) {
			return false;
		}
	}

	/**
	 * Opens the Connection to MySql which is set in Config.
	 *
	 * @param dbName the name of database (schema), if null no database is opend
	 * @return Connection or null if not opened.
	 */
	private static Connection open(String dbName) throws SQLException {
		// Connection aufbauen
		Trace.println(0, "DbConnection.open(dbName:" + dbName + ")");
		dbPwFile = new DbPasswordFile(Config.sDbPwFileName);
		String dbUrl1 = "";
		try {
			if (!isConnected()) {
				Class.forName(sJdbcDriver, true, Thread.currentThread().getContextClassLoader());
				if (dbName == null) {
					dbName = Config.dbName;
				}
				StringBuffer dbUrlTemp = new StringBuffer(80);
				dbUrlTemp.append(Config.dbUrlPrefix);
				dbUrlTemp.append(Config.dbUrl);
				if (!Config.dbUrl.endsWith("/")) {
					dbUrlTemp.append("/");
				}
				dbUrlTemp.append(Config.dbName);
				dbUrlTemp.append(Config.dbUrlSetting);
				dbUrl1 = dbUrlTemp.toString();
				String dbPw = dbPwFile.getDbPassword();
				sConnection = DriverManager.getConnection(dbUrl1, Config.get(Config.dbUserKey), dbPw);
			}
			sConnection.setSchema(dbName); // hat keine Auswirkungen
			Statement statement = sConnection.createStatement();
			statement.execute("USE " + dbName);
			sConnection.setAutoCommit(true);

		} catch (Exception ex) {
			Trace.println(0, "Connection.open(), Url: " + dbUrl1 + " Fehler: " + ex.getMessage());
			StringBuffer message = new StringBuffer(500);
			message.append(" von Url: ");
			message.append(dbUrl1);
			message.append("\n");
			message.append(ex.getMessage());
			throw new SQLException(message.toString());
		}
		Trace.println(0, "Verbunden mit: " + Config.dbUrl + " Database: " + Config.dbName);
		return sConnection;
	}

	/**
	 * Opens the Connection to MySql which is set in Config.
	 *
	 * @param dbName the name of database (schema), if null no database is opend
	 * @return Connection or null if not opened.
	 */
	private static Connection openLocal(String dbName) throws SQLException {
		// Connection aufbauen
		Trace.println(0, "DbConnection.openLocal(dbName:" + dbName + ")");
		dbPwFile = new DbPasswordFile(Config.sDbPwFileName);
		String dbUrl1 = "";
		try {
			if (!isConnected()) {
				Class.forName(sJdbcDriver, true, Thread.currentThread().getContextClassLoader());
				if (dbName == null) {
					dbName = Config.dbName;
				}
				StringBuffer dbUrlTemp = new StringBuffer(80);
				dbUrlTemp.append(Config.dbUrlPrefix);
				dbUrlTemp.append(Config.dbUrl);
				if (!Config.dbUrl.endsWith(":")) {
					dbUrlTemp.append(":");
				}
				dbUrlTemp.append(Config.dbPort);
				dbUrl1 = dbUrlTemp.toString();
				String dbPw = dbPwFile.getDbPassword();
				sConnection = DriverManager.getConnection(dbUrl1, Config.get(Config.dbUserKey), dbPw);
			}
			sConnection.setSchema(dbName); // hat keine Auswirkungen
			Statement statement = sConnection.createStatement();
			statement.execute("USE " + dbName);
			sConnection.setAutoCommit(true);

		} catch (Exception ex) {
			Trace.println(0, "Connection.openLocal(), Url: " + dbUrl1 + " Fehler: " + ex.getMessage());
			StringBuffer message = new StringBuffer(500);
			message.append(" von Url: ");
			message.append(dbUrl1);
			message.append("\n");
			message.append(ex.getMessage());
			throw new SQLException(message.toString());
		}
		Trace.println(0, "Verbunden mit: " + Config.dbUrl + " Database: " + Config.dbName);
		return sConnection;
	}

	/**
	 * Close the Connection.
	 */
	public static void close() throws Exception {
		try {
			// Connection schliessen
			if (sConnection != null) {
				// sConnection.commit();
				sConnection.close();
				sConnection = null;
			}
		} catch (Exception e) {
			throw new Exception("Schliessen der DB '" + Config.dbName + "' Fehlermeldung: \n" + e.getMessage());
		}
	}

	/**
	 * Returns the Connection to database. If not open, it will setup a connection
	 * to mySQL, or a specific schema.
	 * 
	 * @return Connection to mySql or a schema
	 */
	public static Connection getConnection() throws SQLException {
		if (!isConnected()) {
			if (Config.dbUrl.startsWith("//local")) {
				openLocal(Config.dbName);
			} else {
				open(Config.dbName);
			}
		}
		return sConnection;
	}

}