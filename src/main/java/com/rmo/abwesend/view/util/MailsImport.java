package com.rmo.abwesend.view.util;

import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.SpielerImportCsv;
import com.rmo.abwesend.util.Trace;

/**
 * Mail von einem File einlesen. Wird normalerweise beim setup
 * der Daten ausgeführt.
 *
 * @author Ruedi
 *
 */
public class MailsImport {

	public JComponent getPanel() {
		JPanel pane = new JPanel(new GridBagLayout());
		pane.setSize(400, 200);
		pane.setBorder(BorderFactory.createLineBorder(Color.black));

		int zeileNr = 0;

		JLabel labelTitel2 = new JLabel("E-Mail einlesen");
		labelTitel2.setFont(Config.fontTitel);
		pane.add(labelTitel2, getConstraintNext(1, zeileNr++));

		JTextArea labelText2 = new JTextArea(2, 0);
		labelText2.append("Die e-mails müssen in der Form 'Name, Vorname, e-mail' in der Datei vorhanden sein.\n");
		labelText2.append("verwendetert Zeichensatz: UTF-8");
		labelText2.setEditable(false);
		pane.add(labelText2, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel(Config.spielerImportDirKey), getConstraintFirst(0, zeileNr));
		pane.add(new JLabel(Config.spielerImportDir), getConstraintNext(1, zeileNr++));

		pane.add(new JLabel(Config.emailImportFileKey), getConstraintFirst(0, zeileNr));
		pane.add(new JLabel(Config.emailImportFile), getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("e-mail von Datei lesen"), getConstraintFirst(0, zeileNr));
		JButton btnEmailLesen = new JButton("e-mail einlesen");
		btnEmailLesen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				emailEinlesen();
			}
		});
		pane.add(btnEmailLesen, getConstraintNext(1, zeileNr++));

		JButton btnEmailCheck = new JButton("e-mail überprüfen");
		btnEmailCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				emailCheck();
			}
		});
		pane.add(new JLabel("alle e-mail vorhanden?"), getConstraintFirst(0, zeileNr));
		pane.add(btnEmailCheck, getConstraintNext(1, zeileNr++));

		return pane;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 *
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintFirst(int colNr, int rowNr) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(2, 2, 2, 8);
		gbc.gridx = colNr;
		gbc.gridy = rowNr;
		return gbc;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 *
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintNext(int colNr, int rowNr) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 8, 2, 2);
		gbc.gridx = colNr;
		gbc.gridy = rowNr;
		return gbc;
	}


	/**
	 * Einlensen von File
	 */
	private void emailEinlesen() {
		SpielerImportCsv spielerVonFile = new SpielerImportCsv();
		boolean fehler = true;
		try {
			fehler = spielerVonFile.startEmailEinlesen(Config.spielerImportDir, Config.emailImportFile);
		} catch (Exception ex) {
			CmUtil.alertError("Email einlesen", ex);
			return;
		}
		if (fehler) {
			CmUtil.alertWarning("Email einlesen", "Fehler beim Lesen, siehe Trace");
		} else {
			CmUtil.alertWarning("Email eingelesen", "siehe Trace");
		}
	}

	/**
	 * Prüfen, ob ein Eintrag im Feld e-mail vorhanden ist. Liste mit Namen, die
	 * keinen Eintrag haben.
	 */
	private void emailCheck() {
		Trace.println(0, "----> emailCheck");
		try {
			List<Spieler> spielerList = SpielerData.instance().readAllSpieler();
			for (Spieler lSpieler : spielerList) {
				if (lSpieler.getEmail().length() < 5) {
					Trace.println(0,
							lSpieler.getName() + " " + lSpieler.getVorName() + " email: " + lSpieler.getEmail());
				}
			}
		} catch (Exception e) {
			CmUtil.alertWarning("Email prüfen", "Fehler: " + e.getMessage());
		}
		CmUtil.alertWarning("Email prüfen", "siehe Trace");
	}

}
