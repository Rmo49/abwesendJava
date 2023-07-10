package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Mail-Daten, der Inhalt der mail, meist nur die aktuelle Version (Text) in der DB.
 *
 */
public class MailData {

	/**
	 * Das Create Statement für diese Tabelle
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS mail (");
		sb.append(" mailID int, betreff VARCHAR(50), text VARCHAR(500) DEFAULT NULL");
		sb.append(");");
		return sb.toString();
	}


	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS mail;");
	}

	private static MailData instance = null;

	/**
	 * Enthält Connection zur DB. Wird in setupResultset gesetzt, bleibt während
	 * ganzer Sitzung erhalten.
	 */
	private Statement mReadStmt;

	/**
	 * Der Set mit allen Mail-Daten von dem gelesen wird. Ist ein scrollable
	 * Set der von allen Methoden verwendet wird. id: IntegerInteger <br>
	 * name: String <br>
	 */
	private ResultSet mReadSet;

	/**
	 * MailData versteckt, wird immer über Singelton aufgerufen
	 */
	private MailData() {
	}

	/**
	 * Singleton
	 * @return
	 */
	public static MailData instance() {
		if (instance == null) {
			instance = new MailData();
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
	 * Das Mail wird gespeichert. Falls schon vorhanden, wird nix gemacht
	 */
	public void add(Mail pMail) throws SQLException {
		deleteAllRow();
		try {
			addRow(pMail);
		}
		catch (SQLException ex) {
			if (ex.getErrorCode() == 1062) {
				// wenn Duplicate error, dann nix machen.
			}
			else {
				throw ex;
			}
		}
	}

	/**
	 * Die Mailes eines Tages zurückgeben, leere Liste wenn nichts gefunden
	 */
	public Mail readAll() throws SQLException {
		return findAll();
	}


	/**
	 * Alle Mailes löschen
	 */
	public void deleteAllRow() throws SQLException {
		setupReadSet();
		mReadStmt.execute("TRUNCATE mail");
	}



	// ------ interne Methoden -----------------------------------------

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen. Der SQL-String wird zusammengestellt.
	 */
	private void addRow(Mail pMail) throws SQLException {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO mail VALUES (");
		lQuery.append(pMail.getMailId());
		lQuery.append(", '");
		lQuery.append(pMail.getBetreff());
		lQuery.append("', '");
		lQuery.append(pMail.getText());
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}


	/**
	 * Sucht die Row mit der Spielernummer. Wenn true, steht mReadSet auf dieser
	 * Zeile.
	 */
	private Mail findAll() throws SQLException {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM mail");
		Mail mail = new Mail();
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			mail = copyToMail(mReadSet);
		}
		return mail;
	}

	/**
	 * Kopiert die Attribute vom ResultSet in das Objekt Spieler
	 */
	private Mail copyToMail(ResultSet readSet) throws SQLException {
		Mail lMail = new Mail();
		lMail.setMailId(readSet.getInt(1));
		lMail.setBetreff(readSet.getString(2));
		lMail.setText(readSet.getString(3));
		return lMail;
	}

	/**
	 * Setzt das Statement (Connection zur DB) und den Scroll-Set, der für
	 * Insert oder update verwendet werden kann.
	 */
	private synchronized void setupReadSet() throws SQLException {
		if (mReadStmt == null) {
			mReadStmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}
	}

}
