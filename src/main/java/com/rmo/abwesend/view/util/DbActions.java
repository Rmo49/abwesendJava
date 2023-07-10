package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.TennisDataBase;
import com.rmo.abwesend.model.TraceDbData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.DbPasswordFile;

/**
 * Spieler von einem File einlesen
 * @author Ruedi
 *
 */
public class DbActions {

	private JTextField dbPassword;
	private JFormattedTextField bisDatum;


	public JComponent getPanel() {
		JPanel pane = new JPanel(new GridBagLayout());
		pane.setSize(400, 200);
		pane.setBorder(BorderFactory.createLineBorder(Color.black));

	    int zeileNr = 0;

	    JLabel labelDb = new JLabel("Datenbank: " + Config.dbUrl);
		pane.add(labelDb, getConstraintNext(1, zeileNr++));


		JLabel labelTitel1 = new JLabel("Neue DB anlegen");
		labelTitel1.setFont(Config.fontTitel);
		pane.add(labelTitel1, getConstraintNext(1, zeileNr++));

		JButton btnNewDb = new JButton("neu DB generieren");
		btnNewDb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startNewDb();
 			}
		});
		pane.add(btnNewDb, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel(" "), getConstraintNext(1, zeileNr++));

		JLabel labelTitel2 = new JLabel("Trace löschen");
		labelTitel2.setFont(Config.fontTitel);
		pane.add(labelTitel2, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("bis Datum: "), getConstraintNext(1, zeileNr++));

		Date heute = new Date();
		long vorHeute = heute.getTime() - (5 * Config.einTagLong);
		heute.setTime(vorHeute);

		bisDatum = new JFormattedTextField(Config.sdfDatum);
		bisDatum.setPreferredSize(Config.datumFeldSize);
		bisDatum.setValue(heute);
		pane.add(bisDatum, getConstraintNext(1, zeileNr++));

		JButton btnDelTrace = new JButton("Trace löschen");
		btnDelTrace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startDelTrace();
 			}
		});
		pane.add(btnDelTrace, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel(" "), getConstraintNext(1, zeileNr++));
		JLabel labelTitel4 = new JLabel("Spieler => Tableau");
		labelTitel4.setFont(Config.fontTitel);
		pane.add(labelTitel4, getConstraintNext(1, zeileNr++));
		
		JButton btnVerbindung = new JButton("Alle Beziehungen löschen");
		btnVerbindung.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableauSpielerLoeschen();
 			}
		});
		pane.add(btnVerbindung, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel(" "), getConstraintNext(1, zeileNr++));
		JLabel labelTitel3 = new JLabel("Alle Spieler löschen");
		labelTitel3.setFont(Config.fontTitel);
		pane.add(labelTitel3, getConstraintNext(1, zeileNr++));

		JTextArea labelText = new JTextArea(1,0);
		labelText.append("Alle Spieler werden gelöschen, inkl. deren Beziehung zu Tableau");
		labelText.setEditable(false);
		pane.add(labelText, getConstraintNext(1, zeileNr++));

		JButton btnloeschen = new JButton("Löschen");
		btnloeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startSpielerLoeschen();
 			}
		});
		pane.add(btnloeschen, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel(" "), getConstraintNext(1, zeileNr++));
		JLabel labelTitel5 = new JLabel("Datei mit DB-Passwort generieren, DbPassword.txt wird generiert");
		labelTitel5.setFont(Config.fontTitel);
		pane.add(labelTitel5, getConstraintNext(1, zeileNr++));

		JTextArea labelText2 = new JTextArea(1,0);
		labelText2.append("Unten das neue Passwort eingeben");
		labelText2.setEditable(false);
		pane.add(labelText2, getConstraintNext(1, zeileNr++));

		dbPassword = new JTextField();
		dbPassword.setPreferredSize(new Dimension(150, Config.textFieldHeigth));
		dbPassword.setEditable(true);
		pane.add(dbPassword, getConstraintNext(1, zeileNr++));

		JButton btnPwGenerate = new JButton("Passwort generieren");
		btnPwGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startPwGenerate();
 			}
		});
		pane.add(btnPwGenerate, getConstraintNext(1, zeileNr++));

		return pane;
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
	 * Eine neue DB anlegen
	 */
	private void startNewDb() {
		try {
			TennisDataBase.generateNewTables();
			CmUtil.info("DB generieren", "Datenbank generiert, >>> Programm neu starten");
		}
		catch (SQLException ex) {
			CmUtil.alertError("DB generieren", "Fehler: " + ex.getMessage());
		}
	}

	/**
	 * Trace bis zum agegebenen Tag löschen
	 */
	private void startDelTrace() {
		if (CmUtil.passwordOk()) {
			try {
				Date dateBis = Config.sdfDatum.parse(bisDatum.getText());
				TraceDbData.instance().deleteBis(dateBis);
			}
			catch (Exception ex) {
				CmUtil.alertError("Trace löschen", ex.getMessage());
				return;
			}
		}
		else {
			CmUtil.alertWarning("Trace löschen", "falsches Passwort nichts gelöscht");
		}
		CmUtil.alertWarning("Trace löschen", "bis Datum gelöscht");

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
	 * Löschen von File
	 */
	private void startSpielerLoeschen() {
		if (CmUtil.passwordOk()) {
			try {
				SpielerTableauData.instance().deleteAllRow();
				MatchData.instance().deleteAllRow();
				SpielerData.instance().deleteAllRow();
			}
			catch (Exception ex) {
				CmUtil.alertError("Spieler löschen", ex.getMessage());
			}
		}
		else {
			CmUtil.alertWarning("löschen", "falsches Passwort nichts gelöscht");
		}
		CmUtil.alertWarning("löschen", "Alles gelöscht");
	}

	/**
	 * Aus dem Db-Passwort ein verschlüsseltes Passwort generieren
	 */
	private void startPwGenerate() {
		if (dbPassword.getText().length() < 3) {
			CmUtil.alertWarning("Passwort generieren", "Passwort zu kurz");
			return;
		}
		String pwHash = DbPasswordFile.generateDbPassword(dbPassword.getText());
		DbPasswordFile file = new DbPasswordFile(Config.sDbPwFileName);
		file.savePw(pwHash);

		CmUtil.alertWarning("Passwort gespeichert", "siehe: " + Config.sDbPwFileName);
	}

}
