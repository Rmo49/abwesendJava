package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.SpielerInFile;


/**
 * Spieler mit Tableau in ein File schreiben.
 * @author Ruedi
 */
public class SpielerExport {

	private JTextField dirName;
	private JTextField fileName;


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

		pane.add(new JLabel("Directory"),  getConstraintFirst(0, zeileNr));
		dirName = new JTextField();
		dirName.setText(Config.get(Config.spielerListDirKey));
		dirName.setPreferredSize(new Dimension(300, 20));
		pane.add(dirName, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("File Name"),  getConstraintFirst(0, zeileNr));
		fileName = new JTextField();
		fileName.setText("SpielerList.txt");
		fileName.setPreferredSize(new Dimension(150, 20));
		pane.add(fileName, getConstraintNext(1, zeileNr++));

		JButton btnExport = new JButton("Start");
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startExport();
 			}
		});
		pane.add(btnExport, getConstraintNext(1, zeileNr++));
		
		return pane;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintFirst(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(2,2,2,2);
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintNext(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Einlensen von File
	 */
	private void startExport() {
		SpielerInFile spielerInFile = new SpielerInFile();
		boolean fehler = true;
		try {
			fehler = spielerInFile.startExport(dirName.getText(), fileName.getText());
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
