package com.rmo.abwesend.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.view.util.CmUtil;
import com.rmo.abwesend.view.util.KalenderAbwesend;


/**
 * Spieler auswählen aus Tableau oder Name eingeben. Abwesenheiten anzeigen.
 *
 * @author Ruedi
 *
 */
public class AbwesendSpieler extends BasePane implements PropertyChangeListener {

	private Spieler mSpieler;
	private KalenderAbwesend kalenderAbw;

	/**
	 * Neues Objekt mit neuem Kalender
	 */
	public AbwesendSpieler() {
		if (kalenderAbw == null) {
			kalenderAbw = new KalenderAbwesend(false);
		}
	}

	/**
	 * Der Panel der die Abwensenheiten eines Spieler anzeigt.
	 * @return
	 */
	public JComponent getPanel() {
		// wenn nichts selektiert, dann leeres panel
		return kalenderAbw.getBasePane();
	}

	/**
	 * Wird aufgerufen, wenn Doppelklick in der Spieler-Liste
	 */
	@Override
    public void propertyChange(PropertyChangeEvent event) {
    	int spielerID = Integer.parseInt((String)event.getNewValue());
    	readSpieler(spielerID);
//		addShowFirst();
   }

	/**
	 * Den ersten Spieler anzeigen
	 */
    public void addShowSpielerFirst(int spielerId, boolean alleTage) {
    	if (spielerId >= 0) {
     		// die DB
	    	readSpieler(spielerId);
			kalenderAbw.clearSpielerList();
			kalenderAbw.addSpieler(mSpieler, true);

	   		kalenderAbw.removeAllSpieler();
	   		kalenderAbw.setupKalendarPanel(alleTage);
	   		kalenderAbw.setupScrollPane();
	   		kalenderAbw.showSpieler(mSpieler, true, false);
    	}
    	else {
    		CmUtil.alertWarning("Spieler anzeigen", "zuerst Spieler selektieren");
    	}
   }

	/**
	 * Einen weiteren Spieler anzeigen
	 */
    public void addShowSpielerNext(int spielerId) {
    	if (spielerId >= 0) {
     		// die DB
	    	readSpieler(spielerId);
			if (kalenderAbw.addSpieler(mSpieler, true) > 0) {
		   		kalenderAbw.showSpieler(mSpieler, true, false);
			}
    	}
    	else {
    		CmUtil.alertWarning("Spieler anzeigen", "zuerst Spieler selektieren");
    	}
   }


	/**
	 * Spieler einlesen und anzeigen
	 * @param spielerId
	 */
	private void readSpieler(int spielerID) {
		try {
			mSpieler = SpielerData.instance().read(spielerID);
		}
		catch (Exception ex) {
			alertError("Fehler beim Lesen in SpielerData, Spieler: " + spielerID, ex);
		}
	}


	@Override
	protected void saveData() {
		// nichts machen
	}

}
