package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;
import com.rmo.abwesend.model.TennisDataBase;
import com.rmo.abwesend.model.TraceDbData;
import com.rmo.abwesend.util.Config;

/**
 * Spieler von einem File einlesen
 * @author Ruedi
 *
 */
public class DbActions {

	private JFormattedTextField bisDatum;
	// Die Anzeige der Tableau
	private DefaultComboBoxModel<Tableau> tableauListModel;
	private JComboBox<Tableau> tableauListView;
	private int selectedTableauIndex = 0;


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

		//------ Beziehung Spiele => Tableau
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


		// Die Combobox für seletion des Tableau
		pane.add(new JLabel(" "), getConstraintNext(1, zeileNr++));
		JPanel flowPane = new JPanel(new FlowLayout());

		JLabel labelTableau = new JLabel("Tableau: ");
		labelTableau.setBackground(Config.colorTable);
		labelTableau.setOpaque(true);
		flowPane.add(labelTableau);

		// Tableau Combobox
		tableauListModel = new DefaultComboBoxModel<>();
		tableauListView = new JComboBox<>(tableauListModel);
		tableauListView.setRenderer(new ComboBoxRenderer());
		// die Anzahl der angezeigten Tableau je nach Window-Höhe
		tableauListView.setMaximumRowCount(Config.showTableauBox);

		try {
			Collection<Tableau> allTableau = TableauData.instance().readAllTableau();
			tableauListModel.addElement(new Tableau(-1, " ", "1", "SwissTennis"));
			Iterator<Tableau> iter = allTableau.iterator();
			while (iter.hasNext()) {
				tableauListModel.addElement(iter.next());
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Datenbank lesen", JOptionPane.ERROR_MESSAGE);
		}

		tableauListView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedTableauIndex = tableauListView.getSelectedIndex();
			}
		});

		// Wenn der Focus verloren wird die letzte selektion wieder setzen
		tableauListView.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
//				System.out.println("tableau: focus lost");
//				tableauListView.setSelectedIndex(selectedTableauIndex);
			}

			@Override
			public void focusGained(FocusEvent e) {
//				System.out.println("tableau: focus gained");
			}
		});
		flowPane.add(tableauListView);
		pane.add(flowPane, getConstraintNext(1, zeileNr++));

		JButton btnTableauSpieler = new JButton("Spieler von Tableau löschen");
		btnTableauSpieler.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allSpielerFromTableauLoeschen(selectedTableauIndex);
 			}
		});
		pane.add(btnTableauSpieler, getConstraintNext(1, zeileNr++));


		//------ alle Spieler löschen
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




/*
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
*/
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
	 * Für die Anzeige der Tableau
	 * @author ruedi
	 *
	 */
	private class ComboBoxRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 8295447589224463493L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list,
                    value, index, isSelected, cellHasFocus);
            Tableau tableau = (Tableau) value;
            label.setText(tableau.getBezeichnung());
            return label;
        }
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
	 * Alle Spieler von einem Tableau löschen
	 */
	private void allSpielerFromTableauLoeschen(int TableauId) {

        int answer = JOptionPane.showConfirmDialog (null, "Alle Spieler von Tableau löschen?", "Bestätigen",
        		JOptionPane.YES_NO_OPTION);
        if(answer == JOptionPane.YES_OPTION) {
			try {
				// da erstes leer, ist die Liste um eine Position versetzt.
				Tableau tableau = tableauListModel.getElementAt(selectedTableauIndex);
				SpielerTableauData.instance().deleteAllSpielerFromTableau(tableau.getId());
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
/*
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
*/
}
