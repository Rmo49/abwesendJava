package com.rmo.abwesend.util;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Das DB-Passwort in ein File schreiben Wenn dieses File vorhanden ist, kann
 * eine Verbindung zur DB hergestellt werden.
 */
public class PasswordFile {
	/** Das File mit dem Password */
	private static FileReader fileReader = null;
	/** Tag für das DB Passwort, muss im Passwort-File vorhanden sein */
	private static final String dbPwTag = "DB=";
	/** Das Passwort für Datenbank, einmal lesen immer verwenden */
	private static String dbPassword = null;
	/** Tag für das DB Passwort, muss im Passwort-File vorhanden sein */
	private static final String dbLocalPwTag = "DBlocal=";
	/** Das Passwort für Datenbank, einmal lesen immer verwenden */
	private static String dbLocalPassword = null;
	/** Tag für das mail-Passwort */
	private static final String mailPwTag = "Mail=";
	/** Das Passwort für Mails versenden, einmal lesen immer verwenden */
	private static String mailPassword = null;

		
	/**
	 * Gibt das gespeicherte Passwort zurück. Wenn kein File vorhanden, dann NULL;
	 * 
	 * @return
	 */
	public static String getDbPassword() {
		Trace.println(5, "DbPasswordFile.getDbPassword()");
		if (dbPassword == null) {
			dbPassword = readPassword(dbPwTag);
		}
		return dbPassword;
	}

	/**
	 * Gibt das gespeicherte Passwort zurück. Wenn kein File vorhanden, dann NULL;
	 * 
	 * @return
	 */
	public static String getDbLocalPassword() {
		Trace.println(5, "DbPasswordFile.getDbLocalPassword()");
		if (dbLocalPassword == null) {
			dbLocalPassword = readPassword(dbLocalPwTag);
		}
		return dbLocalPassword;
	}



	/**
	 * Gibt das gespeicherte Passwort zurück. Wenn kein File vorhanden, dann NULL;
	 *
	 * @return
	 */
	public static String getMailPassword() {
		Trace.println(5, "DbPasswordFile.getMailPassword()");
		if (mailPassword == null) {
			mailPassword = readPassword(mailPwTag);
		}
		return mailPassword;
	}

	/**
	 * Gibt den Namen des File zurück, prüft zuerst ob auch Config gelesen wurde
	 * @return
	 */
	private static String getFileName() {
		if (Config.sPath.length() < 1) {
			try {
				Config.readProperties();
			
			} catch (Exception ex) {
				Trace.println(1, "Password File, kann Properies nicht lesen");
				return "";
			}
		}
		return Config.sPath + "/" + Config.sPwFileName;
	}
	
	
	/**
	 * Das Passwort vom File lesen, das dem pwType entspricht
	 * @param pwTag die Bezeichnung des Passworts das gesucht wird
	 * @return
	 */
	private static String readPassword(String pwTag) {
		String password = null;
		try {
			if (fileReader == null) {
				fileReader = new FileReader(getFileName());
			}
			BufferedReader buffReader = new BufferedReader(fileReader);

			String line = buffReader.readLine();
			while (line != null) {
				if (line.startsWith(pwTag)) {
					int len = pwTag.length();
					password = line.substring(len);
					break;
				}
				line = buffReader.readLine();
			}
			buffReader.close();
			fileReader.close();
			fileReader = null;
		}
		catch (Exception ex) {
			Trace.println(1, "Fehler: " + ex.getMessage());
			return null;
		}
		return password;
	}


}
