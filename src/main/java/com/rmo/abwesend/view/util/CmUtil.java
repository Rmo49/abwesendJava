package com.rmo.abwesend.view.util;

import javax.swing.JOptionPane;

import com.rmo.abwesend.util.Config;

/**
 * Anzeige von speziellen Alerts (Popups).
 * 
 * @author Ruedi
 *
 */
public class CmUtil {

	/**
	 * Fehlermeldung mit header und Exception.
	 * 
	 * @param header
	 * @param ex
	 */
	public static void alertError(String header, Exception ex) {
		alertError(header, ex.getMessage());
	}

	/**
	 * Fehlermeldung
	 * 
	 * @param header: Titel
	 * @param fehler: Fehlermeldung
	 */
	public static void alertError(String header, String fehler) {
		JOptionPane.showMessageDialog(null, "Fehlermeldung: " + fehler, header, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Eine Warnung ausgeben
	 * 
	 * @param warnung: Die Warnung (Titel)
	 * @param fehler:  Was eingegeben werden muss
	 */
	public static void alertWarning(String titel, String fehler) {
		JOptionPane.showMessageDialog(null, fehler, titel, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Eine Warnung ausgeben
	 * 
	 * @param titel: Die Info (Titel)
	 * @param info:  Die Information
	 */
	public static void info(String titel, String info) {
		JOptionPane.showMessageDialog(null, info, titel, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Prüft, ob das eingegebene Passwort richtig ist.
	 * 
	 * @return
	 */
	public static boolean passwordOk() {
		String name = JOptionPane.showInputDialog(null, "Passwort");
		if (name == null) {
			return false;
		}
		if (name.compareTo(Config.configDbPasswort) == 0) {
			return true;
		}
		return false;
	}

}
