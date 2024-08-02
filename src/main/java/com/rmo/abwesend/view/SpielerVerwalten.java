package com.rmo.abwesend.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableau;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.view.util.SpielerSelektieren;

/**
 * Spieler und Tableau anzeigen, vom Spieler kann der Name geändert werden. Die
 * Tableaux in denen er spielt können selektiert werden.
 * 
 * @author Ruedi
 *
 */
public class SpielerVerwalten extends BasePane implements PropertyChangeListener {

	private Spieler mSpieler;
	private SpielerSelektieren selectSpieler; // View zum selektieren

	private JTextField nameFiled;
	private JTextField vorNameField;
	private JTextField emailField;
	private JLabel idShow;
	private JLabel quittung;
	private JButton btnNeu;
	private JButton btnLoeschen;
	private JButton btnSpeichern;

	// Alle Tableaux in einer Liste
	private List<JCheckBox> mTableauList;
	private List<Tableau> mTableauListData;

	/**
	 * Neue Spieler eintragen.
	 *
	 * @param ps
	 */
	public SpielerVerwalten(SpielerSelektieren pSelect) {
		selectSpieler = pSelect;
	}

	public JComponent getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.add(addSpielerAnzeigen());
		panel.add(addTableauList());

		int spielerID = selectSpieler.getSelectedSpielerId();
		if (spielerID >= 0) {
			readSpieler(spielerID);
			setCheckedData(spielerID);
		} else {
			setElementsEnable(true);
		}

		// dies ist ein Listener
		selectSpieler.addChangeListener(this);
		return panel;
	}

	/**
	 * Die Eingaben für einen Spieler
	 *
	 * @return
	 */
	private JComponent addSpielerAnzeigen() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		addEingabeFelder(panel);
		addButtons(panel);
		return panel;
	}

	private void addEingabeFelder(JPanel panel) {
		panel.add(new JLabel("Name:"), getConstraintEingabe(0, 0));
		panel.add(new JLabel("Vorname:"), getConstraintEingabe(1, 0));

		nameFiled = new JTextField();
		nameFiled.setPreferredSize(new Dimension(50, Config.textFieldHeigth));
		panel.add(nameFiled, getConstraintEingabe(0, 1));

		vorNameField = new JTextField();
		vorNameField.setPreferredSize(new Dimension(50, Config.textFieldHeigth));
		panel.add(vorNameField = new JTextField(), getConstraintEingabe(1, 1));

		panel.add(new JLabel("e-mail:"), getConstraintEingabe(0, 2));
		panel.add(new JLabel("SpielerID"), getConstraintEingabe(1, 2));

		emailField = new JTextField();
		emailField.setPreferredSize(new Dimension(50, Config.textFieldHeigth));
		panel.add(emailField = new JTextField(), getConstraintEingabe(0, 3));

		idShow = new JLabel();
		idShow.setPreferredSize(new Dimension(10, Config.textFieldHeigth));
		panel.add(idShow = new JLabel(), getConstraintEingabe(1, 3));
	}

	/**
	 * Die Buttons unterhalb der Eingabefelder
	 * 
	 * @param grid
	 */
	private void addButtons(JPanel panel) {
		panel.add(btnSpeichern = new JButton("Daten speichern"), getConstraintEingabe(0, 4));
		panel.add(btnLoeschen = new JButton("Spieler löschen"), getConstraintEingabe(0, 5));
		panel.add(btnNeu = new JButton("Neue Spieler"), getConstraintEingabe(1, 5));

		panel.add(quittung = new JLabel(), getConstraintEingabe(0, 4));

		btnLoeschen.setBackground(Config.colorSpieler);
		btnLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnLoeschenCalled();
			}
		});

		btnSpeichern.setBackground(Config.colorSpieler);
		btnSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});

		btnNeu.setBackground(Config.colorSpieler);
		btnNeu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnNeuCalled();
			}
		});
	}

	private JComponent addTableauList() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		JLabel titel = new JLabel("spielt in:");
		panel.add(titel, getConstraintTableau(0, 0));

		readAllTableauData();

		mTableauList = new ArrayList<>();
		int col = 0;
		int row = 1;
		for (int i = 0; i < mTableauListData.size(); i++) {
			mTableauList.add(new JCheckBox(mTableauListData.get(i).getBezeichnung()));
			mTableauList.get(i).setSelected(true);
			panel.add(mTableauList.get(i), getConstraintTableau(col, row));
			if ((col == 0) && (i >= (mTableauListData.size() / 2))) {
				col = 1;
				row = 0;
			}
			row++;
		}
		return panel;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * 
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintEingabe(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 2, 2);
		c.weightx = 1;
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * 
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintTableau(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Alle daten (nochmals) einlesen
	 */
	private void readAllTableauData() {
		try {
			if (mTableauListData != null) {
				mTableauListData = null;
			}
			mTableauListData = new ArrayList<>(); // statt remove
			mTableauListData.addAll(TableauData.instance().readAllTableau());
		} catch (Exception ex) {
			alertError("ReadAll Tableau", ex);
		}
	}

	/**
	 * Spieler loeschen
	 */
	private void btnLoeschenCalled() {
		if (mSpieler != null) {

			int answer = JOptionPane.showConfirmDialog(null, "Soll " + mSpieler.getName() + " gelöscht werden?",
					"Bestätigen", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) {
				if (deleteSpieler(mSpieler.getId())) {
					nameFiled.setText("");
					vorNameField.setText("");
					emailField.setText("");
					setElementsEnable(false);
					selectSpieler.refreshAfterChange();
				}
			} else {
				// nichts machen
			}

		}
	}

	/**
	 * Spieler speichern
	 */
	@Override
	protected void saveData() {
		if (spielerSpeichern()) {
			spielerTableauSpeichern();
			setElementsEnable(false);
		}
	}

	/**
	 * Neuen Spieler aufnehmen
	 */
	private void btnNeuCalled() {
		mSpieler = null;
		nameFiled.setText("");
		vorNameField.setText("");
		emailField.setText("");
		setUncheckTableau();
		nameFiled.setEnabled(true);
		vorNameField.setEnabled(true);
		emailField.setEnabled(true);
		btnLoeschen.setEnabled(false);
		btnSpeichern.setEnabled(true);
	}

	/**
	 * Wird aufgerufen, wenn Doppelklick oder löschen in der Spieler-Liste
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		int spielerID = Integer.parseInt((String) event.getNewValue());
		if (spielerID >= 0) {
			readSpieler(spielerID);
			setCheckedData(spielerID);
			return;
		}
		spielerID = Integer.parseInt((String) event.getOldValue());
		if (spielerID >= 0) {
			deleteSpieler(spielerID);
		}
	}

	/**
	 * Spieler einlesen und anzeigen
	 *
	 * @param spielerId
	 */
	private void readSpieler(int spielerID) {
		try {
			mSpieler = SpielerData.instance().read(spielerID);
			nameFiled.setText(mSpieler.getName());
			vorNameField.setText(mSpieler.getVorName());
			emailField.setText(mSpieler.getEmail());
			idShow.setText(String.valueOf(mSpieler.getId()));
		} catch (Exception ex) {
			alertError("Fehler beim Lesen in SpielerData, Spieler: " + spielerID, ex);
		}
	}

	/**
	 * Spieler löschen
	 * 
	 * @param spielerID
	 * @return true wenn gelöscht, false wenn löschen nicht möglich
	 */
	private boolean deleteSpieler(int spielerID) {
		try {
			SpielerData.instance().delete(spielerID);
		} catch (Exception ex) {
			alertError("Fehler beim Löschen in SpielerData, Spieler: " + spielerID, ex);
			return false;
		}
		return true;
	}

	/**
	 * Alle Tableau Selektion auf unchecked setzen.
	 */
	private void setUncheckTableau() {
		for (int i = 0; i < mTableauListData.size(); i++) {
			mTableauList.get(i).setSelected(false);
		}
	}

	/**
	 * In der Tableau Liste die bisher selektierten Zeilen anzeigen Kann erst
	 * aufgerufen werden, wenn ein Spieler selektiert ist.
	 *
	 * @param mTableauModel
	 */
	private void setCheckedData(int spielerID) {
		// alle Tableaux wo der Spieler spielt
		List<Integer> tableauId = null;
		try {
			tableauId = SpielerTableauData.instance().readAllTableau(spielerID);
		} catch (Exception ex) {
			alertError("Fehler beim Lesen in SpielerTableauData, Spieler" + spielerID, ex);
		}
		setUncheckTableau();
		if (tableauId != null && tableauId.size() > 0) {
			// Position in der tableau Liste suchen, dann in View selektieren
			for (Integer id : tableauId) {
				for (int i = 0; i < mTableauListData.size(); i++) {
					if (mTableauListData.get(i).getId() == id.intValue()) {
						mTableauList.get(i).setSelected(true);
					}
				}
			}
		}
		setElementsEnable(true);
	}

	/**
	 * Spieler mit Abwesenheitsliste speichern
	 */
	private boolean spielerSpeichern() {
		boolean isNew = false;
		if (mSpieler == null) {
			// nicht von der DB gelesen
			mSpieler = new Spieler();
			mSpieler.setId(0);
			isNew = true;
		}
		mSpieler.setName(nameFiled.getText());
		mSpieler.setVorName(vorNameField.getText());
		mSpieler.setEmail(emailField.getText());
		try {
			SpielerData.instance().add(mSpieler);
			quittung.setText("gespeichert");
			selectSpieler.refreshAfterChange();
		} catch (Exception ex) {
			alertError("Fehler bei spielerSpeichern in SpielerData", ex);
			return false;
		}
		if (isNew) {
			// nochmals lesen, damit Spieler-Tableau gesetzt werden kann
			try {
				mSpieler = SpielerData.instance().read(mSpieler.getName(), mSpieler.getVorName());
			} catch (Exception ex) {
				alertError("Fehler bei spielerSpeichern in SpielerData", ex);
				return false;
			}
		}
		return true;
	}

	/**
	 * Die selektierten Tableau wieder speichern
	 */
	private void spielerTableauSpeichern() {
		SpielerTableau st = new SpielerTableau();
		List<Integer> tableauListNew = new ArrayList<>();
		for (int i = 0; i < mTableauList.size(); i++) {
			if (mTableauList.get(i).isSelected()) {
				tableauListNew.add(Integer.valueOf(mTableauListData.get(i).getId()));
			}
		}
		st.setTableauList(tableauListNew);
		st.setSpielerId(mSpieler.getId());
		try {
			SpielerTableauData.instance().update(st);
		} catch (Exception ex) {
			alertError("Fehler beim Speichern in SpielerTableauData", ex);
		}
	}

	/**
	 * Alle Buttons und Eingabefelder aktiv / inaktiv
	 * 
	 * @param disable
	 */
	private void setElementsEnable(boolean enable) {
		btnLoeschen.setEnabled(enable);
		btnSpeichern.setEnabled(enable);
//		listBox.setEnabled(enable);
//		nameFiled.setEnabled(enable);
//		vorNameField.setEnabled(enable);
//		emailField.setEnabled(enable);
//		btnNeu.setEnabled(enable);
	}

}
