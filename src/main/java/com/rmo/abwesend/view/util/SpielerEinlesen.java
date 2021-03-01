package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.SpielerVonFile;
import com.rmo.abwesend.util.Trace;

/**
 * Spieler von einem File einlesen
 * @author Ruedi
 *
 */
public class SpielerEinlesen {

	private JTextField dirName;
	private JTextField splitChar;
	private JTextField tableauName;

	private JTextField fileNameSpieler;
	private JTextField fileNameEmail;


	public JComponent getPanel() {
		JPanel pane = new JPanel(new GridBagLayout());
		pane.setSize(400, 200);
		pane.setBorder(BorderFactory.createLineBorder(Color.black));

	    int zeileNr = 0;
	    
		JLabel labelTitel = new JLabel("Spieler einlesen");
		labelTitel.setFont(Config.fontTitel);
		pane.add(labelTitel, getConstraintNext(1, zeileNr++));

		JTextArea labelText = new JTextArea(2,0);
		labelText.append("Die Spieler müssen in der Form 'Konkurrenz, Name, Vorname, Name, Vorname' \n");
		labelText.append("in der Datei vorhanden sein. Verwendetert Zeichensatz: UTF-8");
		labelText.setEditable(false);
		pane.add(labelText, getConstraintNext(1, zeileNr++));
		
		JButton btnEinlesenInfo = new JButton("mehr Info");
		btnEinlesenInfo.setBorderPainted(false);
		btnEinlesenInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Das zweite Name-Paar ist der Doppelpartner, falls vorhanden. \n"
						+ "Wenn der Spieler noch nicht vorhanden ist, wird ein neuer angelegt.\n"
						+ "Der Spieler wird auch im Tableau eingetragen, falls eine Übereinstimmung der Bezeichnung \n"
						+ "mit Swisstennis gefunden wird. (siehe auch: Setup > Tableaux verwalten)", 
						"Spieler einlesen", JOptionPane.INFORMATION_MESSAGE);
 			}
		});
		pane.add(btnEinlesenInfo, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("Directory"), getConstraintFirst(0, zeileNr));
		dirName = new JTextField();
		dirName.setText(Config.get(Config.spielerListDirKey));
		dirName. setPreferredSize(new Dimension(300, 20));
		pane.add(dirName, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("File Name Spieler"), getConstraintFirst(0, zeileNr));
		fileNameSpieler = new JTextField();
		fileNameSpieler.setText(Config.get(Config.spielerListFileKey));
		fileNameSpieler.setPreferredSize(new Dimension(150, 20));
		pane.add(fileNameSpieler, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("Split Character"), getConstraintFirst(0, zeileNr));
		splitChar = new JTextField();
		splitChar.setText(Config.get(Config.spielerSplitCharKey));
		splitChar.setPreferredSize(new Dimension(40, 20));
		pane.add(splitChar, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("nur diese Tableau-Verbindung"), getConstraintFirst(0, zeileNr));
		tableauName = new JTextField();
		tableauName.setPreferredSize(new Dimension(100, 20));
		pane.add(tableauName, getConstraintNext(1, zeileNr++));

		JButton btnSpieler = new JButton("Spieler einlesen");
		btnSpieler.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spielerEinlesen();
 			}
		});
		pane.add(btnSpieler, getConstraintNext(1, zeileNr++));
		
		pane.add(new JLabel("Spieler => Tableau"), getConstraintFirst(0, zeileNr));
		JButton btnVerbindung = new JButton("Beziehungen löschen");
		btnVerbindung.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableauSpielerLoeschen();
 			}
		});
		pane.add(btnVerbindung, getConstraintNext(1, zeileNr++));

		
		JLabel labelTitel2 = new JLabel("E-Mail einlesen");
		labelTitel2.setFont(Config.fontTitel);
		pane.add(labelTitel2, getConstraintNext(1, zeileNr++));

		JTextArea labelText2 = new JTextArea(2,0);
		labelText2.append("Die e-mails müssen in der Form 'Name, Vorname, e-mail' in der Datei vorhanden sein.\n");
		labelText2.append("verwendetert Zeichensatz: UTF-8");
		labelText2.setEditable(false);
		pane.add(labelText2, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("File Name e-mail"), getConstraintFirst(0, zeileNr));
		fileNameEmail = new JTextField();
		fileNameEmail.setText(Config.get(Config.emailListFileKey));
		fileNameEmail.setPreferredSize(new Dimension(150, 20));
		pane.add(fileNameEmail, getConstraintNext(1, zeileNr++));

		JButton btnEmail = new JButton("e-mail einlesen");
		btnEmail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				emailEinlesen();
 			}
		});
		pane.add(btnEmail, getConstraintNext(1, zeileNr++));

		JButton btnEmailCheck = new JButton("e-mail überprüfen");
		btnEmailCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				emailCheck();
 			}
		});
		pane.add(btnEmailCheck, getConstraintNext(1, zeileNr++));
		
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
	 * Spieler einlensen von File
	 */
	private void spielerEinlesen() {
		SpielerVonFile spielerVonFile = new SpielerVonFile();
		boolean fehler = true;
		try {
			fehler = spielerVonFile.startSpielerEinlesen(dirName.getText(), fileNameSpieler.getText(), tableauName.getText());
		}
		catch (Exception ex) {
			CmUtil.alertError("Spieler einlesen", ex);
			return;
		}
		if (fehler) {
			CmUtil.alertWarning("Spieler einlesen", "Fehler beim Lesen, siehe Trace" );
			Trace.flush();
		}
	}
	
	/**
	 * Die Beziehung Spieler / Tableau löschen.
	 */
	private void tableauSpielerLoeschen() {
		
        int answer = JOptionPane.showConfirmDialog (null, "Alle Beziehungen Spieler => Tableau löschen?", "Bestätigen",
        		JOptionPane.YES_NO_OPTION);
        if(answer == JOptionPane.YES_OPTION) {
			try {
				SpielerTableauData.instance().deleteAllRow();
			}
			catch (Exception ex) {
				CmUtil.alertError("Beziehung Spieler => Tableau löschen", ex.getMessage());
			}
        } else {
        	// nichts machen
        }
	}

	/**
	 * Einlensen von File
	 */
	private void emailEinlesen() {
		SpielerVonFile spielerVonFile = new SpielerVonFile();
		boolean fehler = true;
		try {
			fehler = spielerVonFile.startEmailEinlesen(dirName.getText(), fileNameEmail.getText());
		}
		catch (Exception ex) {
			CmUtil.alertError("Email einlesen", ex);
			return;
		}
		if (fehler) {
			CmUtil.alertWarning("Email einlesen", "Fehler beim Lesen, siehe Trace" );
		}
		else {
			CmUtil.alertWarning("Email eingelesen", "siehe Trace" );			
		}
	}

	/**
	 * Prüfen, ob ein Eintrag im Feld e-mail vorhanden ist.
	 * Liste mit Namen, die keinen Eintrag haben.
	 */
	private void emailCheck() {
		Trace.println(0, "----> emailCheck");
		try {
			List<Spieler> spielerList = SpielerData.instance().readAllSpieler();
			for (Spieler lSpieler : spielerList) {
				if (lSpieler.getEmail().length() < 5) {
					Trace.println(0, lSpieler.getName() + " " + lSpieler.getVorName() + " email: " + lSpieler.getEmail());
				}
			}
		}
		catch (Exception e) {
			CmUtil.alertWarning("Email prüfen", "Fehler: " + e.getMessage());			
		}
		CmUtil.alertWarning("Email prüfen", "siehe Trace" );			
	}

	
}
