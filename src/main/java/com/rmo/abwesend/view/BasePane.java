package com.rmo.abwesend.view;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.view.util.CmUtil;

/**
 * Die Basis aller Panes, die im Center der View angezeigt werden.
 * 
 * @author Ruedi
 *
 */
public abstract class BasePane {
	private boolean notSaved = false;
	protected BasePane actualPane = null;

	/**
	 * Prüfen ob etwas zu sichern ist.
	 */
	protected void checkSave() {
		if (notSaved && actualPane != null) {
			actualPane.saveData();
		}
	}

	/**
	 * Die Daten sichern, muss von jeder Subklasse impl. werden
	 */
	protected abstract void saveData();

	/**
	 * Untersucht, ob Wochenende
	 * 
	 * @param pos die position in der Tage Liste
	 * @return true wenn an der position ein Wochenende steht
	 */
	protected boolean isWeekend(int pos) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(Config.turnierBeginDatum);
		calendar.add(Calendar.DAY_OF_MONTH, pos);
		if ((calendar.get(Calendar.DAY_OF_WEEK) == 7) || (calendar.get(Calendar.DAY_OF_WEEK) == 1)) {
			return true;
		}
		return false;
	}

	/**
	 * Fehlermeldung
	 * 
	 * @param header
	 * @param ex
	 */
	protected void alertError(String header, Exception ex) {
		JOptionPane.showMessageDialog(null, "Fehlermeldung: " + ex.getMessage(), header, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Check der Abwesenheits Eingabe.
	 * 
	 * @param inputString
	 * @return
	 */
	public static boolean isInputOk(String inputString) {
		if (!inputString.matches("0|-[0-9]+[.:]?[0-9]*|[0-9]+[.:]?[0-9]*-")) {
			CmUtil.alertWarning(inputString, "Mögliche Eingaben: 0 / -15 / -18.30 / 16- / 19:30-");
			return false;
		}
		return true;
	}

}
