package com.rmo.abwesend.util;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Das DB-Passwort in ein File schreiben Wenn dieses File vorhanden ist, kann
 * eine Verbindung zur DB hergestellt werden.
 */
public class PasswordFile {
	private static PasswordFile instance;
	/** Name des Files */
	private String mFileName = null;
	/** Das File mit dem Password */
	private FileReader fileReader = null;
	/** Tag für das DB Passwort, muss im Passwort-File vorhanden sein */
	private final String dbPwTag = "DB=";
	/** Das Passwort für Datenbank, einmal lesen immer verwenden */
	private static String dbPassword = null;
	/** Tag für das mail-Passwort */
	private final String mailPwTag = "Mail=";
	/** Das Passwort für Mails versenden, einmal lesen immer verwenden */
	private static String mailPassword = null;

	/**
	 * MailControl constructor comment.
	 */
	private PasswordFile() {
		mFileName = Config.sPath + "/" + Config.sPwFileName;
	}

	public static PasswordFile getInstance() {
	    if(instance == null) {
	    	instance = new PasswordFile();
        }
        return instance;
    }


	/**
	 * Gibt das gespeicherte Passwort zurück. Wenn kein File vorhanden, dann NULL;
	 * 
	 * @return
	 */
	public String getDbPassword() {
		Trace.println(2, "DbPasswordFile.getDbPassword() file: " + mFileName);
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
	public String getMailPassword() {
		Trace.println(2, "DbPasswordFile.getMailPassword() file: " + mFileName);
		if (mailPassword == null) {
			mailPassword = readPassword(mailPwTag);
		}
		return mailPassword;
	}



	/**
	 * Das Passwort vom File lesen, das dem pwType entspricht
	 * @param pwTag die Bezeichnung des Passworts das gesucht wird
	 * @return
	 */
	private String readPassword(String pwTag) {
		String password = null;
		try {
			if (fileReader == null) {
				fileReader = new FileReader(mFileName);
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
			return null;
		}
		return password;
	}


}
