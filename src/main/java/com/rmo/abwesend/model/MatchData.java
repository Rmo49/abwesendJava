package com.rmo.abwesend.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.rmo.abwesend.util.Config;

/**
 * Match-Model, Verbindung zur Tabelle Matches in der DB. Matches werden mit der
 * Klasse Match sichtbar gemacht.
 */
public class MatchData {

	/**
	 * Das Create Statement für diese Tabelle
	 * 
	 * @return
	 */
	public static String createTable() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS matches (");
		sb.append(" spielerID int unsigned NOT NULL, time VARCHAR(22), matchTyp VARCHAR(1) DEFAULT NULL,");
		sb.append(" PRIMARY KEY (spielerID, time),");
		sb.append(" FOREIGN KEY (spielerID) REFERENCES Spieler(spielerID)");
		sb.append(");");
		return sb.toString();
	}

	public static String createTable2() {
		StringBuffer sb = new StringBuffer(300);
		sb.append(" CREATE TABLE IF NOT EXISTS matches (");
		sb.append(" spielerID int unsigned NOT NULL, time VARCHAR(22), matchTyp VARCHAR(1) DEFAULT NULL,");
		sb.append(" PRIMARY KEY (spielerID, time),");
		sb.append(" FOREIGN KEY (spielerID) REFERENCES Spieler(spielerID) ON DELETE CASCADE");
		sb.append(");");
		return sb.toString();
	}

	public static String dorpTable() {
		return ("DROP TABLE IF EXISTS matches;");
	}

	private static MatchData instance = null;

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
	private MatchData() {
	}

	/**
	 * Singleton
	 * 
	 * @return
	 */
	public static MatchData instance() {
		if (instance == null) {
			instance = new MatchData();
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
	 * Das Match wird gespeichert. Falls schon vorhanden, wird nix gemacht
	 */
	public void add(Match pMatch) throws SQLException {
		try {
			addRow(pMatch);
		} catch (SQLException ex) {
			if (ex.getErrorCode() == 1062) {
				// wenn Duplicate error, dann nix machen.
			} else {
				throw ex;
			}
		}
	}

	/**
	 * Die Matches eines Tages zurückgeben, leere Liste wenn nichts gefunden
	 */
	public List<Match> readAll(Date pDate) throws SQLException {
		return findAll(pDate);
	}

	/**
	 * Die Matches eines Spieler zurückgeben, leere Liste wenn nichts gefunden
	 */
	public List<Match> readAll(int pSpielerId) throws Exception {
		return findAll(pSpielerId);
	}

	/**
	 * Alle Matches löschen
	 */
	public void deleteAllRow() throws SQLException {
		setupReadSet();
		mReadStmt.execute("TRUNCATE matches");
	}

	/**
	 * Update alle matches eines Spielers (löschen oder dazufügen)
	 * 
	 * @param pSpielerId
	 * @param newMatches
	 */
	public void updateAll(int pSpielerId, List<String> newMatches) throws Exception {
		setupReadSet();
		mReadStmt.execute("DELETE FROM matches WHERE spielerId = " + pSpielerId);

		Calendar spielCalendar = new GregorianCalendar();
		spielCalendar.setTime(Config.turnierBeginDatum);

		for (int i = 0; i < Config.turnierMaxTage; i++) {
			int startPos = 0;
			if (newMatches == null) {
				return;
			}
			while (newMatches.get(i).length() > startPos) {
				String[] spielEintrag = checkSpiel(newMatches.get(i), startPos);
				Date datum = spielCalendar.getTime();
				StringBuffer datumText = new StringBuffer(Config.sdfDatum.format(datum));
				datumText.append(" ");
				datumText.append(spielEintrag[1]);
				addRow(pSpielerId, datumText.toString(), spielEintrag[0]);
				startPos += 7;
			}
			spielCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	private String[] checkSpiel(String spielZeit, int startPos) throws ParseException {
		String[] spiel = new String[2];
		spiel[0] = spielZeit.substring(startPos, 1);
		if (!spiel[0].matches("[ED]")) {
			throw new ParseException("Nur E oder D erlaubt", startPos);
		}
		if (spielZeit.length() < 6) {
			throw new ParseException("Zeit muss 5 Zeichen lang sein", 1);
		}
		spiel[1] = spielZeit.substring(startPos + 1, 6);
		// wirft ParseException wenn nicht ok
		Config.sdfZeit.parse(spiel[1]);
		return spiel;
	}

	// ------ interne Methoden -----------------------------------------

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen. Kopiert die Attribute vom
	 * ResultSet in das Objekt Spieler. Der SQL-String wird zusammengestellt.
	 */
	private void addRow(Match pMatch) throws SQLException {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO matches VALUES (");
		lQuery.append(pMatch.getSpielerId());
		lQuery.append(", '");
		lQuery.append(pMatch.getDatum());
		lQuery.append("', '");
		lQuery.append(pMatch.getSpielTyp());
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}

	/**
	 * Eine neue Zeile (Row) in die Tabelle eintragen. Kopiert die Attribute vom
	 * ResultSet in das Objekt Spieler. Der SQL-String wird zusammengestellt.
	 */
	private void addRow(int spielerId, String time, String spielTyp) throws SQLException {
		Statement stmt = getConnection().createStatement();
		StringBuffer lQuery = new StringBuffer("INSERT INTO matches VALUES (");
		lQuery.append(spielerId);
		lQuery.append(", '");
		lQuery.append(time);
		lQuery.append("', '");
		lQuery.append(spielTyp);
		lQuery.append("');");
		stmt.executeUpdate(lQuery.toString());
		stmt.close();
	}

	/**
	 * Sucht die Row mit der Spielernummer. Wenn true, steht mReadSet auf dieser
	 * Zeile.
	 */
	private List<Match> findAll(int pSpielerId) throws Exception {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		mReadSet = mReadStmt.executeQuery("SELECT * FROM matches WHERE spielerId = " + pSpielerId);
		List<Match> matches = new ArrayList<>();
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			matches.add(copyToMatch(mReadSet));
		}
		return matches;
	}

	/**
	 * Sucht alle Matches von einem bestimmten Datum.
	 * 
	 * @param pDate das Datum wo alle Matches gelesen werden
	 * @return die Liste dier Matches
	 * @throws SQLException
	 */
	private List<Match> findAll(Date pDate) throws SQLException {
		setupReadSet();
		if (mReadSet != null) {
			mReadSet.close();
		}
		String datum = Config.sdfDatum.format(pDate);
		mReadSet = mReadStmt.executeQuery("SELECT * FROM matches WHERE time LIKE '" + datum + "%'");
		List<Match> matches = new ArrayList<>();
		mReadSet.beforeFirst();
		while (mReadSet.next()) {
			matches.add(copyToMatch(mReadSet));
		}
		return matches;
	}

	/**
	 * Kopiert die Attribute vom ResultSet in das Objekt Spieler
	 */
	private Match copyToMatch(ResultSet readSet) throws SQLException {
		Match lMatch = new Match();
		lMatch.setSpielerId(readSet.getInt(1));
		lMatch.setDatum(readSet.getString(2));
		lMatch.setSpielTyp(readSet.getString(3));
		return lMatch;
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
