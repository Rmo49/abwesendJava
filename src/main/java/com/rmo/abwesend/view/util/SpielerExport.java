package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.SpielerExportFile;


/**
 * Spieler mit Tableau in ein File schreiben.
 * @author Ruedi
 */
public class SpielerExport {

	public JComponent getPanel() {
		JPanel pane = new JPanel(new GridBagLayout());
	    int zeileNr = 0;
		pane.setBorder(BorderFactory.createLineBorder(Color.black));

	    JLabel labelTitel = new JLabel("Spieler exportieren");
		labelTitel.setFont(Config.fontTitel);
		pane.add(labelTitel, getConstraintNext(1, zeileNr++));

		JTextArea labelText = new JTextArea(2,0);
		labelText.append("Alle Spieler werden in eine Datei exportiert \n");
		labelText.append("In der Form: Name, Vorname, Tableau1, Tableau2, Tableau3");
		pane.add(labelText, getConstraintNext(1, zeileNr++));


		pane.add(new JLabel("Einstellungen in AbwesendConfig.txt"), getConstraintFirst(1, zeileNr++));

		pane.add(new JLabel(Config.spielerExportDirKey), getConstraintFirst(0, zeileNr));
		pane.add(new JLabel(Config.spielerExportDir), getConstraintFirst(1, zeileNr++));

		pane.add(new JLabel(Config.spielerExportFileKey), getConstraintFirst(0, zeileNr));
		pane.add(new JLabel(Config.spielerExportFile), getConstraintFirst(1, zeileNr++));

		JButton btnExport = new JButton("Start");
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startExport();
 			}
		});
		pane.add(btnExport, getConstraintNext(0, zeileNr++));

		return pane;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintFirst(int colNr, int rowNr) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = colNr;
		gbc.gridy = rowNr;
		gbc.ipady = 5;
		return gbc;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintNext(int colNr, int rowNr) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 4;
		gbc.gridx = colNr;
		gbc.gridy = rowNr;
		return gbc;
	}

	/**
	 * Einlensen von File
	 */
	private void startExport() {
		SpielerExportFile spielerInFile = new SpielerExportFile();
		boolean fehler = true;
		try {
			fehler = spielerInFile.startExport(Config.spielerExportDir, Config.spielerExportFile);
		}
		catch (Exception ex) {
			CmUtil.alertError("Spieler exportieren", ex);
			return;
		}
		if (fehler) {
			CmUtil.alertWarning("Spieler exportieren", "Fehler beim Schreiben, siehe Trace" );
		}
	}
}
