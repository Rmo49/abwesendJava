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
	//public static final String url = "jdbc:mysql://localhost:3306/";
//	jdbc:mysql://localhost/db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
	private static DbPasswordFile dbPwFile = null;

	public DbConnection() {
	}

	/**
	 * Prüfen, ob die Verbindung zur DB bereits besteht.
	 * @return
	 */
	public static boolean isConnected() {
		try {
			if (sConnection == null || sConnection.isClosed()) {
				return false;
			}
			else {
				return true;
			}
		}
		catch (SQLException ex) {
			return false;
		}
	}
	
	/**
	 * Opens the Connection to MySql which is set in Config.
	 * 
	 * @param dbName
	 *            the name of database (schema), if null no database is opend
	 * @return Connection or null if not opened.
	 */
	public static Connection open(String dbName) throws SQLException {
		// Connection aufbauen
		Trace.println(0, "Connection.open(dbName:" + dbName + ")");
		dbPwFile = new DbPasswordFile(Config.sDbPwFileName);
		try {
			if (sConnection == null || sConnection.isClosed()) {
				Class.forName(sJdbcDriver, true, Thread.currentThread().getContextClassLoader());
				if (dbName == null) {
					dbName = Config.dbNname;
				}
				StringBuffer dbUrlTemp = new StringBuffer(80);
				dbUrlTemp.append(Config.dbUrlPrefix);
				dbUrlTemp.append(Config.dbUrl);
				if (! Config.dbUrl.endsWith("/")) {
					dbUrlTemp.append("/");
				}
				dbUrlTemp.append(Config.dbNname);
//				dbUrlTemp.append(Config.dbUrlSetting);
				String dbUrl1 = dbUrlTemp.toString();
				String dbPw = dbPwFile.getDbPassword();
				sConnection = DriverManager.getConnection(dbUrl1, Config.get(Config.dbUserKey), dbPw);
//				sConnection = DriverManager.getConnection(dbUrl1, Config.get(Config.dbUserKey), "");
			}
			sConnection.setSchema(dbName);	// hat keine Auswirkungen
			Statement statement = sConnection.createStatement();
			statement.execute("USE " + dbName);
			sConnection.setAutoCommit(true);

		} catch (Exception ex) {
			throw new SQLException(ex + ex.getMessage() );
		}
		Trace.println(0, "Verbunden mit: " + Config.dbUrl + " Database: " + Config.dbNname);
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
			throw new Exception("Schliessen der DB '" + Config.dbNname
					+ "' Fehlermeldung: \n" + e.getMessage());
		}
	}

	/**
	 * Returns the Connection to database. If not open, it will setup
	 * a connection to mySQL, or a specific schema. 
	 * @return Connection to mySql or a schema
	 */
	public static Connection getConnection() throws SQLException {
		if (sConnection == null || sConnection.isClosed()) {
			open(Config.dbNname);
		}
		return sConnection;
	}

}