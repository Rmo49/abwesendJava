package com.rmo.abwesend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import com.rmo.abwesend.view.util.CmUtil;

/**
 * Das DB-Passwort in ein File schreiben Wenn dieses File vorhanden ist, kann
 * eine Verbindung zur DB hergestellt werden.
 */
public class DbPasswordFile {
	/** Name des Files */
	private String mFileName = null;
	/** Das File mit dem Password */
	private FileHandler fileHandler = null;
	/** Das Passwort, einmal lesen immer verwenden */
	private static String password = null;

	/**
	 * MailControl constructor comment.
	 */
	public DbPasswordFile(String fileName) {
		mFileName = Config.sMailControlPath = Config.sPath + "/" + fileName;
	}

	/**
	 * Gibt das gespeicherte Passwort zurück. Wenn kein File vorhanden, dann NULL;
	 * 
	 * @return
	 */
	public String getDbPassword() {
		Trace.println(2, "DbPasswordFile.getDbPassword() file: " + mFileName);
		if (password == null) {
			if (fileHandler == null) {
				fileHandler = new FileHandler(mFileName);
			}
			password = fileHandler.readLine();
			fileHandler.close();
		}
		return password;
	}

	/**
	 * Aus dem Db-Passwort ein verschlüsseltes Passwort generieren
	 */
	public static String generateDbPassword(String password1) {
		MessageDigest md;

		// Das Passwort bzw der Schluesseltext
		byte[] keyStr = password1.getBytes(StandardCharsets.UTF_8);

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			CmUtil.alertWarning("Passwort generieren: ", ex.getMessage());
			return "";
		}
		md.update(keyStr);
		byte[] digest = md.digest();
		String hex = DatatypeConverter.printHexBinary(digest);
		StringBuffer sb = new StringBuffer(50);
		sb.append(hex.substring(0, 15).toUpperCase());
		sb.append(hex.substring(15, hex.length()).toLowerCase());
		return sb.toString();
	}

	/**
	 * Das Passwort im fileHandler speichern
	 */
	public void savePw(String text) {
		if (fileHandler == null) {
			fileHandler = new FileHandler(mFileName);
		}
		fileHandler.println(text);
		fileHandler.close();
	}

}
