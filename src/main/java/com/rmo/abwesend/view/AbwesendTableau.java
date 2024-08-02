package com.rmo.abwesend.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.view.util.CmUtil;
import com.rmo.abwesend.view.util.KalenderAbwesend;
import com.rmo.abwesend.view.util.SpielerSelektieren;

/**
 * Wenn ein Tableau gewählt, dann alle Spieler anzeigen.
 *
 * @author Ruedi
 *
 */
public class AbwesendTableau extends BasePane implements PropertyChangeListener {

	private SpielerSelektieren mSpielerSelektieren;
	private KalenderAbwesend abwesendKalender;

	/**
	 * @param scene
	 */
	public AbwesendTableau(SpielerSelektieren pSelect) {
		mSpielerSelektieren = pSelect;
	}

	/**
	 * Wird aufgerufen, wenn angezeigt werden soll
	 * 
	 * @return
	 */
	public JComponent getPanel() {
		if (mSpielerSelektieren.getSelectedTableauIndex() <= 0) {
			CmUtil.alertWarning("Kein Tableau selektiert", "Zuerst Tableau selektieren.");
			// wenn leere Feld
			return new JPanel();
		}
		return abwesendKalender.getBasePane();
	}

	/**
	 * Die Abwesenheiten anzeigen
	 */
	/**
	 * Die Abwesenheiten eines Tableau anzeigen
	 * 
	 * @param index wie er in der angezeigten Liste steht
	 */
	public void showTableauAbwesend(int index) {
		if (index > 0) {
			if (abwesendKalender == null) {
				abwesendKalender = new KalenderAbwesend(false);
//				abwesendKalender.setupScrollPane();
			}
			abwesendKalender.clearSpielerList();
			// Alle Spieler eines Tableau
			List<Spieler> spielerList = mSpielerSelektieren.getSpielerOfTableau(index + 1);
			for (ListIterator<Spieler> iter = spielerList.listIterator(); iter.hasNext();) {
				Spieler lSpieler = iter.next();
				abwesendKalender.addSpieler(lSpieler, true);
			}
			abwesendKalender.showAllSpielerShort();
		}
	}

	/**
	 * Wird aufgerufen, wenn Doppelklick in der Spieler-Liste
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// nichts machen
	}

	@Override
	protected void saveData() {
		// nichts machen
	}

}
