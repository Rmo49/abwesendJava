package com.rmo.abwesend.view.util;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.view.MainFrame;


public class VonBisDatum implements PropertyChangeListener {

//	private final int DATE_PICKER_WIDTH = 120;
//	private DatePicker vonDatePicker;
//	private DatePicker bisDatePicker;
	private JFormattedTextField vonDatum;
	private JFormattedTextField bisDatum;


	/**
	 * Anzeige und Einlesen der Datumspanne, die angezeigt werden soll.
	 */
	public VonBisDatum() {
	}


	/**
	 * Eingabefeld und Liste der Spieler werden zurückgegeben.
	 *
	 * @return
	 */
	public JComponent getPanel(MainFrame mainFrame) {
		FlowLayout flow = new FlowLayout();
		JPanel panel = new JPanel(flow);

		panel.add(new JLabel("Anzeige von: "));

		vonDatum = new JFormattedTextField(Config.sdfDatum);
		vonDatum.setPreferredSize(Config.datumFeldSize);
		vonDatum.setValue(Config.showBeginDatum);

		vonDatum.addPropertyChangeListener("value", this);
		panel.add(vonDatum);

//		panel.add(setupVonDataPicker());
//		hbox.getChildren().add(vonDatePicker);

		panel.add(new JLabel("bis: "));
		bisDatum = new JFormattedTextField(Config.sdfDatum);
		bisDatum.setPreferredSize(Config.datumFeldSize);
		bisDatum.setValue(Config.showEndDatum);

		bisDatum.addPropertyChangeListener("value", this);
		panel.add(bisDatum);

		return panel;
	}

	 /** Called when a field's "value" property changes. */
    @Override
	public void propertyChange(PropertyChangeEvent e) {
        Object source = e.getSource();
        if (source == vonDatum) {
        	Date datum = (Date) vonDatum.getValue();
        	if ((datum.compareTo(Config.turnierBeginDatum) < 0)
        			|| (datum.compareTo(Config.turnierEndDatum) > 0)) {
        		// wenn Datum ausserhalb Bereich
        		Config.showBeginDatum = Config.turnierBeginDatum;
        		vonDatum.setValue(Config.showBeginDatum);
        	}
        	else {
        		Config.showBeginDatum = datum;
        	}
    		Config.showNumberBerechnen();
    }
    else if (source == bisDatum) {
        	Date datum = (Date) bisDatum.getValue();
        	if ((datum.compareTo(Config.turnierBeginDatum) <= 0)
        		|| (datum.compareTo(Config.turnierEndDatum) > 0)) {
        			// wenn ausserhalb Bereich
            		Config.showEndDatum = Config.turnierEndDatum;
        			bisDatum.setValue(Config.showEndDatum);
        	}
        	else {
        		Config.showEndDatum = datum;
        	}
    		Config.showNumberBerechnen();
        }
    }

}
