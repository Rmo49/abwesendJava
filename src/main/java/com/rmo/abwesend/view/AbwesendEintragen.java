package com.rmo.abwesend.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.view.util.CmUtil;
import com.rmo.abwesend.view.util.KalenderAbwesend;
import com.rmo.abwesend.view.util.SpielerSelektieren;

/**
 * Die Abwesenheiten eines Spielers eintragen. Spieler selektieren, wenn
 * gewählt, dann Abwesenheitsliste anzeigen.
 *
 * @author Ruedi
 *
 */
public class AbwesendEintragen extends BasePane implements PropertyChangeListener {

	// referenz, damit setPaneCenter aufgerufen werden kann.
	private MainFrame mainFrame;
	private SpielerSelektieren mSpielerSelect; // View zum selektieren
	private Spieler mSpieler;
	// der zentrale Panel der zurückgegeben wird.
	private KalenderAbwesend abwesendKalender;
	// hat etwas in den Input-Fields geändert?
	private PropertyChangeListener inputFieldListener;
//	private JFrame mainFrame;
	// wenn der Wochenwert überschrieben werden soll
	private JCheckBox checkOverwrite;
	// Werte von der Abwesend-Liste speichern
	private JButton saveBtn;

	public AbwesendEintragen(SpielerSelektieren pSelect) {
		mSpielerSelect = pSelect;
	}

	public AbwesendEintragen(SpielerSelektieren pSelect, MainFrame main) {
		mSpielerSelect = pSelect;
		mainFrame = main;
	}

	public JComponent getPanel() {
		// dies ist ein Listener, für SpielerListe
		mSpielerSelect.addChangeListener(this);
		// Layout für Anzeige von Spieler und Buttons
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(abwesendKalender.getBasePane());
		panel.add(addButtons());

		saveBtn.setEnabled(false);
		inputFieldListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				saveBtn.setEnabled(true);
			}
		};
		abwesendKalender.addPropertyChangeListener(inputFieldListener);

		return panel;
	}

	/**
	 * Wird aufgerufen, wenn Eingabe-Daten geändert wurden
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		int spielerID = Integer.parseInt((String) event.getNewValue());
		if (readSpieler(spielerID)) {
			addShowAbwesenheiten();
		}
	}

	/**
	 * Die Buttons am Ende der View
	 */
	private JComponent addButtons() {
		JTextField wochenWert = new JTextField();
		wochenWert.setPreferredSize(new Dimension(60, Config.textFieldHeigth));
		wochenWert.setMaximumSize(new Dimension(60, Config.textFieldHeigth));

		JButton wochenwertBtn = new JButton("Diesen Wert bei Wochentage einfügen");
		wochenwertBtn.setPreferredSize(new Dimension(60, 20));
		wochenwertBtn.setBackground(Config.colorSpieler);

		wochenwertBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enterWochenwert(wochenWert.getText());
			}
		});

		saveBtn = new JButton("Speichern");
		saveBtn.setPreferredSize(new Dimension(40, 20));
		saveBtn.setBackground(Config.colorSpieler);
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});

		checkOverwrite = new JCheckBox("bestehende Werte überschreiben");

		// Panel anlegen und compnents zuordnem
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(wochenWert)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(wochenwertBtn)
						.addComponent(checkOverwrite).addComponent(saveBtn)));

		layout.setVerticalGroup(layout
				.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(wochenWert).addComponent(wochenwertBtn))
				.addComponent(checkOverwrite).addComponent(saveBtn));

		return panel;
	}

	/**
	 * Den Spieler in Abwesenheiten anzeigen.
	 */
	public boolean addShowSpieler(int spielerId) {
		if (readSpieler(spielerId)) {
			addShowAbwesenheiten();
			return true;
		}
		return false;
	}

	/**
	 * Spieler einlesen und anzeigen
	 *
	 * @param spielerId
	 */
	private boolean readSpieler(int spielerID) {
		if (spielerID <= 0) {
			CmUtil.alertWarning("Spieler lesen", "zuerst Spieler wählen");
			return false;
		}
		try {
			mSpieler = SpielerData.instance().read(spielerID);
		} catch (Exception ex) {
			alertError("Fehler beim Lesen in SpielerData, Spieler: " + spielerID, ex);
			return false;
		}
		SpielerData.instance().addChangeListener(this);
		return true;
	}

	/**
	 * Die Abwesenheiten anzeigen
	 */
	private void addShowAbwesenheiten() {
		if (abwesendKalender == null) {
			abwesendKalender = new KalenderAbwesend(true);
			abwesendKalender.setupKalendarPanel(true);
			abwesendKalender.setupScrollPane();
		}
		if (abwesendKalender.addSpieler(mSpieler, false) >= 0) {
			abwesendKalender.showSpieler(mSpieler, false, true);
			abwesendKalender.addInputFields(mSpieler);
		}
	}

	/**
	 * Alle Wert in Wochen eintragen, zuerst letzten Wochenwert lesen, dann ändern
	 */
	private void enterWochenwert(String text) {
		for (int i = 0; i < Config.turnierMaxTage; i++) {
			if (!isWeekend(i)) {
				if (checkOverwrite.isSelected()) {
					abwesendKalender.getInputList().get(i).setText(text);
				} else {
					// nur einfügen, wenn noch keine Eingabe
					if (abwesendKalender.getInputList().get(i).getText().length() == 0) {
						abwesendKalender.getInputList().get(i).setText(text);
					}
				}
			}
		}
	}

	/**
	 * Sichern der geänderten Werte.
	 */
	@Override
	protected void saveData() {
		for (int i = 0; i < Config.turnierMaxTage; i++) {
			mSpieler.setAbwesendAt(i, abwesendKalender.getInputList().get(i).getText());
		}
		try {
			SpielerData.instance().add(mSpieler);
		} catch (Exception ex) {
			alertError("AbwesendEintragen: Fehler beim Speichern", ex);
		}
		saveBtn.setEnabled(false);

		abwesendKalender = null;
		addShowAbwesenheiten();
		mainFrame.setPaneCenter(getPanel());
	}

}
