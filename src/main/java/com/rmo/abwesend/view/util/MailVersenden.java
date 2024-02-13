package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import com.rmo.abwesend.model.Mail;
import com.rmo.abwesend.model.MailData;
import com.rmo.abwesend.model.Match;
import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.MailFromFile;
import com.rmo.abwesend.util.MailGenerator;
import com.rmo.abwesend.util.MailSenden;
import com.rmo.abwesend.util.MailToFile;
import com.rmo.abwesend.util.Trace;

/**
 * Versenden von mails. Der mail-Text ist in der DB gespeichert. Alle Matches
 * lesen, sortieren nach SpielerId und Match-Datum.
 *
 * @author ruedi
 *
 */
public class MailVersenden implements ActionListener, PropertyChangeListener {

	private JFrame mainFrame;

	private boolean doGenerate = true; // mails werden generiert, wenn false dann gesendet.
	private JTextArea textBetreff;
	private JTextArea textMail;
	private JFormattedTextField vonDatum;
	private JFormattedTextField bisDatum;
	private JButton btnMailSenden;
	private JButton btnMailGenerate;
	private JTextField testTo;

	private List<Match> listMatches; // alle Matches der Zeitspanne, geordnet nach Spieler und Datum
	private List<Spieler> listTableauSpieler; // alle Spieler eines Tableau
	private JComboBox<String> tableauCombo;
	private String tableauSelected;
	private JCheckBox anAlle;
	private JCheckBox keineAbwesenheit;

	private MailGenerator mailGenerator;
	private MailSenden mailSenden;
//	private MailToFile mailToFile = null;

	// Progress dialog
	private JDialog dialog;
	private JProgressBar progressBar;
	private JTextArea message;
	private JButton btnStart;
	private JButton btnSchliessen;

	private TaskMail task;

	public MailVersenden(JFrame parent) {
		this.mainFrame = parent;
	}

	public JComponent getPanel() {
		JPanel compPanel = new JPanel(new GridBagLayout());
//		compPanel.setSize(400, 200);
		compPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		int zeileNr = 0;

		JLabel labelTitel = new JLabel("Mail versenden");
		labelTitel.setFont(Config.fontTitel);
		compPanel.add(labelTitel, getConstraintNext(1, zeileNr++));

		compPanel.add(new JLabel("Betreff"), getConstraintFirst(0, zeileNr));
		textBetreff = new JTextArea(1, 40);
		textBetreff.setEditable(true);
		compPanel.add(textBetreff, getConstraintNext(1, zeileNr++));

		compPanel.add(new JLabel("Mail-Text"), getConstraintFirst(0, zeileNr));

		textMail = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textMail, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textMail.setEditable(true);
		Dimension dimText = new Dimension(600, 160);
		scrollPane.setMinimumSize(dimText);
		scrollPane.setPreferredSize(dimText);
		compPanel.add(scrollPane, getConstraintNext(1, zeileNr++));

		compPanel.add(new JLabel("Variable:"), getConstraintFirst(0, zeileNr));
		compPanel.add(new JLabel("<Vorname>, <Spiele> müssen im Text vorhanden sein"), getConstraintNext(1, zeileNr++));

		JButton btnSpeichern = new JButton("Speichern");
		btnSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mailTextSpeichern();
			}
		});
		compPanel.add(btnSpeichern, getConstraintNext(1, zeileNr++));
		compPanel.add(new JLabel("======================================="), getConstraintNext(1, zeileNr++));

		//--- Selektieren
		compPanel.add(new JLabel("Selektieren:"), getConstraintFirst(0, zeileNr));
		compPanel.add(new JLabel("Auswählen an wen die mails gesendet werden"), getConstraintNext(1, zeileNr++));

		JPanel paneSel = new JPanel(new FlowLayout());
		anAlle = new JCheckBox("an Alle");
		anAlle.setSelected(false);
		anAlle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				anAlleAction();
 			}
		});
		paneSel.add(anAlle);

		paneSel.add(new JLabel(" /  oder noch "));
		keineAbwesenheit = new JCheckBox("keine Abwensenheit");
		keineAbwesenheit.setSelected(false);
		keineAbwesenheit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keineAbwesenheitAction();
 			}
		});
		paneSel.add(keineAbwesenheit);

		paneSel.add(new JLabel(" /  oder von Tableau: "));
		ArrayList<String> tableauString = new ArrayList<>();
		// Leerstring wenn nichts gewählt
		tableauString.add("");
		List<Tableau> tabList = null;
		try {
			tabList = TableauData.instance().readAllTableau();
		}
		catch (Exception e) {
		}
		for (Tableau element : tabList) {
			tableauString.add(element.getBezeichnung());
		}
		String[] tableauStr = tableauString.toArray(new String[0]);
		tableauCombo = new JComboBox<>(tableauStr);
		tableauCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableauComboAction();
 			}
		});

		paneSel.add(tableauCombo);
		compPanel.add(paneSel, getConstraintNext(1, zeileNr++));

		//--- Match von ... bis
		JPanel paneMatch = new JPanel(new FlowLayout());
		paneMatch.add(new JLabel("/ oder Datum der Matches ab: "));
		// DatumSpanne
		vonDatum = new JFormattedTextField(Config.sdfDatum);
		vonDatum.setPreferredSize(Config.datumFeldSize);
		paneMatch.add(vonDatum);
		paneMatch.add(new JLabel("  bis: "));
		bisDatum = new JFormattedTextField(Config.sdfDatum);
		bisDatum.setPreferredSize(Config.datumFeldSize);
		paneMatch.add(bisDatum);
		compPanel.add(paneMatch, getConstraintNext(1, zeileNr++));

		compPanel.add(new JLabel("TO:"), getConstraintFirst(0, zeileNr));

		JPanel paneTo = new JPanel(new FlowLayout());
		testTo = new JTextField();
		testTo.setPreferredSize(new Dimension(150, Config.textFieldHeigth));
		testTo.setEditable(true);
		paneTo.add(testTo);
		paneTo.add(new JLabel("für Test: wenn ausgefüllt werden alle mails an diese Adresse gesendet"));
		compPanel.add(paneTo, getConstraintNext(1, zeileNr++));

		// --- Button generieren
		JPanel paneGen = new JPanel(new FlowLayout());
		btnMailGenerate = new JButton("Mails generieren");
		btnMailGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateMails();
			}
		});
		paneGen.add(btnMailGenerate);
		paneGen.add(new JLabel(">>>> wird in: '" + Config.sMailToSend + "' generiert"));
		compPanel.add(paneGen, getConstraintNext(1, zeileNr++));


		compPanel.add(new JLabel("siehe in: " + Config.sMailToSendPath), getConstraintNext(1, zeileNr++));
//		compPanel.add(new JLabel("======================================="), getConstraintNext(1, zeileNr++));

		//--- Mails versenden
		JPanel paneSend = new JPanel(new FlowLayout());

		btnMailSenden = new JButton("Mails versenden");
		btnMailSenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doGenerate = false;
				showPopup();
			}
		});
		paneSend.add(btnMailSenden);
		paneSend.add(new JLabel("<<< wird von '" + Config.sMailToSend + "' gelesen"));

		compPanel.add(paneSend, getConstraintNext(1, zeileNr++));

		readMailTextFromDb();
		setDatumSpanne();

		return compPanel;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 *
	 * @param row
	 * @return
	 */
	private GridBagConstraints getConstraintFirst(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(2, 2, 2, 2);
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
	private GridBagConstraints getConstraintNext(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Wenn anAlle selektiert
	 */
	private void anAlleAction() {
		boolean enable = true;
		if (anAlle.isSelected()) {
			enable = false;
		}
		keineAbwesenheit.setEnabled(enable);
		tableauCombo.setEnabled(enable);
		vonDatum.setEnabled(enable);
		bisDatum.setEditable(enable);
	}

	/**
	 * Wenn keine Abwesenheit eingetragen selektiert
	 */
	private void keineAbwesenheitAction() {
		boolean enable = true;
		if (keineAbwesenheit.isSelected()) {
			enable = false;
		}
		anAlle.setEnabled(enable);
		tableauCombo.setEnabled(enable);
		vonDatum.setEnabled(enable);
		bisDatum.setEditable(enable);
	}

	/**
	 * Wenn tableauCombo etwas selektiert
	 */
	private void tableauComboAction() {
		boolean enable = true;
		if (tableauCombo.getSelectedIndex() > 0) {
			enable = false;
		}
		anAlle.setEnabled(enable);
		keineAbwesenheit.setEnabled(enable);
		vonDatum.setEnabled(enable);
		bisDatum.setEditable(enable);
	}



	/**
	 * Die Daten von DB.
	 */
	private void readMailTextFromDb() {
		try {
			Mail mail = MailData.instance().readAll();
			textBetreff.setText(mail.getBetreff());
			textMail.setText(mail.getText());
		} catch (SQLException ex) {
			CmUtil.alertWarning("Mail speichern", "Problem: " + ex.getMessage());
			return;
		}
	}

	/**
	 * Die Datumspanne für die nächsten Tage
	 */
	private void setDatumSpanne() {
		Date now = new Date();
		if (now.getTime() < Config.turnierBeginDatum.getTime()) {
			now = Config.turnierBeginDatum;
		} else {
			// ein Tag voraus
			now.setTime(now.getTime() + Config.einTagLong);
		}
		vonDatum.setValue(now);
//		now.setTime(now.getTime() + Config.einTagLong);
		bisDatum.setValue(now);
	}

	/**
	 * Speichern der mails in der DB
	 */
	private void mailTextSpeichern() {
		Mail mail = new Mail();
		mail.setMailId(1);
		mail.setBetreff(textBetreff.getText());
		mail.setText(textMail.getText());
		try {
			MailData.instance().add(mail);
		} catch (SQLException ex) {
			CmUtil.alertWarning("Mail speichern", "Problem: " + ex.getMessage());
			return;
		}

	}

	/**
	 * Die Liste der Matches erstellen und dann den mail-Generator aufrufen.
	 */
	private void generateMails() {
		doGenerate = true;
		if (listMatches != null) {
			listMatches.clear();
		}
		// zuerst alle Matches lesen, diese müssen vor dem Generate vorhanden sein.
		matchesLesen();

		// prüfen, ob ein Tableau selektiert wurde.
		int selected = tableauCombo.getSelectedIndex();
		tableauSelected = tableauCombo.getItemAt(selected);
		if (tableauSelected.length() > 0) {
			tableauSpielerLesen();
		}

		// setup des Generators mit den nötigen Infos
		mailGenerator = new MailGenerator(textBetreff.getText(), textMail.getText(), testTo.getText(),
				anAlle.isSelected(), keineAbwesenheit.isSelected());
		// Popup anzeigen, das dann den Prozess startet
		showPopup();

	}

	/**
	 * Alle Matches in der Zeitspanne einlesen, die Class-Var listMatches wird gefüllt.
	 */
	private void matchesLesen() {
		Date dateVon;
		Date dateBis;
		try {
			dateVon = Config.sdfDatum.parse(vonDatum.getText());
			dateBis = Config.sdfDatum.parse(bisDatum.getText());
			List<Match> listMatchesRead;

			while (dateVon.getTime() <= dateBis.getTime()) {
				try {
					listMatchesRead = MatchData.instance().readAll(dateVon);
					if (listMatches == null) {
						listMatches = listMatchesRead;
					} else {
						listMatches.addAll(listMatchesRead);
					}
				} catch (SQLException ex) {
					CmUtil.alertWarning("Mail versenden, Fehler", "Lesen der Matches: " + ex.getMessage());
					return;
				}
				dateVon.setTime(dateVon.getTime() + Config.einTagLong);
			}
			// hier auch nach Zeit selektieren
			if (listMatches != null && listMatches.size() > 0) {

			}
			// Liste sortieren nach Spieler ID
			Collections.sort(listMatches, new Comparator<Match>() {
				@Override
				public int compare(Match s1, Match s2) {
					int id = s1.getSpielerId() - s2.getSpielerId();
					if (id != 0) {
						return id;
					}
					return s1.getDatum().compareTo(s2.getDatum());
				}
			});
		} catch (java.text.ParseException ex) {
			CmUtil.alertWarning("Mail generieren", "Falsches Datum: " + ex.getMessage());
			return;
		}
	}

	/**
	 * Alle Spieler eines bestimmten Tableau lesen
	 */
	private void tableauSpielerLesen() {
		Tableau tableau = null;
		try {
			tableau = TableauData.instance().readBezeichnung(tableauSelected);
		}
		catch (Exception e) {
			CmUtil.alertWarning("Email versenden", "Fehler: " + e.getMessage());
			return;
		}
		listTableauSpieler = SpielerTableauData.instance().readSpielerOfTableau(tableau.getId());
	}


	/**
	 * Die mails versenden mit einem Popup-Window. Dieses wird aufgerufen von Mail
	 * Versenden einlesen.
	 */
	private void showPopup() {
		// Ein Popup-Window erstellen
		if (doGenerate) {
			dialog = new JDialog(mainFrame, "Mails generieren");
			dialog.setLocationRelativeTo(btnMailGenerate);
		} else {
			dialog = new JDialog(mainFrame, "Mails senden");
			dialog.setLocationRelativeTo(btnMailSenden);
		}
		dialog.setSize(200, 250);
		dialog.setModal(true);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		addSpace(panel);

		btnStart = new JButton("Start");
		btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnStart.setEnabled(true);
		btnStart.addActionListener(this);
		panel.add(btnStart);
		addSpace(panel);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(progressBar);
		addSpace(panel);

		message = new JTextArea(5, 1);
		message.setMargin(new Insets(5, 5, 5, 5));
		message.setEditable(false);
		panel.add(message);
		addSpace(panel);

		btnSchliessen = new JButton("Schliessen");
		btnSchliessen.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSchliessen.setEnabled(false);
		panel.add(btnSchliessen);

		btnSchliessen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});

		dialog.add(panel);
		dialog.setVisible(true);
	}

	/**
	 * Abstand zwischen Elementen.
	 *
	 * @param panel
	 */
	private void addSpace(JPanel panel) {
		panel.add(Box.createRigidArea(new Dimension(0, 6)));
	}

	/**
	 * Invoked when the user presses the start button. Startet MyTask, in der
	 * Methode doInBackground wird alles ausgeführt.
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		btnStart.setEnabled(false);
		dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed, siehe Klasse unten.
		task = new TaskMail();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
//			message.setText("Progress: " + task.getProgress());
		}
	}



//-------- MyTask für versenden der mails ------------------------------------
	class TaskMail extends SwingWorker<Void, Void> {

		List<Match> listMatchSpieler = null;
		// für Progress
		double last = 200;
		int progress = 0;

		/*
		 * Main task. Executed in background thread. Hier wird das einlesen ausgeführt.
		 */
		@Override
		public Void doInBackground() {
			btnSchliessen.setEnabled(true);

			// Progressbar setzen
			setProgress(0);

			if (doGenerate) {
				if (anAlle.isSelected()) {
					generateAlleSpieler();
				} else if (keineAbwesenheit.isSelected()) {
					generateKeineAbwesenheit();
				} else if (tableauSelected.length() > 0) {
					generateForTableau();
				}
				else {
					generateMatches();
				}
			} else {
				// alle gespeicherten mails versenden
				iterateOverMails();
			}
			setProgress(100);
			return null;
		}

		/**
		 * Itertation über alle Spieler
		 *
		 * @return
		 */
		private void generateAlleSpieler() {
			List<Spieler> listSpieler = null;
			try {
				listSpieler = SpielerData.instance().readAllSpieler();
			} catch (Exception e) {
				CmUtil.alertWarning("Email versenden", "Fehler: " + e.getMessage());
			}
			Iterator<Spieler> spielerIter = listSpieler.iterator();
			int pos = 0; // für die Berechnung des Fortschrittes
			last = listSpieler.size();

			while (spielerIter.hasNext()) {
				double k = pos / last * 100;
				progress = (int) k;
				setProgress(progress);

				mailGenerator.generateMail(spielerIter.next());
			}
			mailGenerator.closeFile();
		}
		/**
		 * Itertation über alle Spieler, die keine Abwensenheit eingetragen
		 *
		 * @return
		 */
		private void generateKeineAbwesenheit() {
			List<Spieler> listSpieler = null;
			try {
				listSpieler = SpielerData.instance().readAllSpieler();
			} catch (Exception e) {
				CmUtil.alertWarning("Email versenden", "Fehler: " + e.getMessage());
			}
			int pos = 0; // für die Berechnung des Fortschrittes
			last = listSpieler.size();

			for (Spieler spieler : listSpieler) {
				double k = pos / last * 100;
				progress = (int) k;
				setProgress(progress);

				// wenn keine Abwesenheit eingetragen
				if (! spieler.hasAbwesenheit()) {
					mailGenerator.generateMail(spieler);
				}
			}
			mailGenerator.closeFile();
		}


		/**
		 * Iteration über die alle Spieler eines bestimmten Tableaus.
		 */
		private void generateForTableau() {
			int pos = 0; // für die Berechnung des Fortschrittes
			last = listTableauSpieler.size();

			Spieler spieler = null;
			Iterator<Spieler> tsIter = listTableauSpieler.iterator();
			while (tsIter.hasNext()) {
				double k = pos / last * 100;
				progress = (int) k;
				setProgress(progress);

				spieler = tsIter.next();
				mailGenerator.generateMail(spieler);
				pos++;
			}
			mailGenerator.closeFile();
		}


		/**
		 * Iteration über die ganze Liste der selektierten Matches.
		 */
		private void generateMatches() {
			int spielerIdLast = -1;
			int pos = 0; // für die Berechnung des Fortschrittes
			Match match = null;
			last = listMatches.size();
			List<Match> listMatchSpieler = new ArrayList<>();

			Iterator<Match> matchIter = listMatches.iterator();
			// vorlesen erster Eintag
			if (matchIter.hasNext()) {
				match = matchIter.next();
				spielerIdLast = match.getSpielerId();
				listMatchSpieler.add(match);
				pos++;
			}

			while (matchIter.hasNext()) {
				double k = pos / last * 100;
				progress = (int) k;
				setProgress(progress);

				match = matchIter.next();
				// Alle Matches eines Spieler lesen
				if (spielerIdLast == match.getSpielerId()) {
					listMatchSpieler.add(match);
				} else {
					// der nächste Spieler bereits im Iterator, zuerst mail senden
					mailGenerator.generateMail(listMatchSpieler);

					listMatchSpieler.clear();
					listMatchSpieler.add(match);
					spielerIdLast = match.getSpielerId();
				}
				pos++;
			}
			// noch den letzten match senden
			mailGenerator.generateMail(listMatchSpieler);
			mailGenerator.closeFile();
		}


		/**
		 * Itertation über alle Mails, diese senden
		 *
		 * @return
		 */
		private void iterateOverMails() {
			int pos = 0; // für die Berechnung des Fortschrittes

			message.append("lese: " + Config.sMailToSendPath + "\n");
			mailSenden = new MailSenden();
			MailFromFile mailFrom = new MailFromFile();
			last = mailFrom.getNubmerOfMails();

			String line = mailFrom.readLine();
			while (line != null && line.length() > 0) {
				String toAdresse = mailFrom.readLine();
				Trace.println(4, "Mail an: " + toAdresse);
				String betreff = mailFrom.readLine();
				String mailText = readMailText(mailFrom);

				// TODO definitiv entfernen, wenn nicht mehr gebraucht
//				if (mailTest.isSelected()) {
//					writeMailToFile(toAdresse, betreff, mailText);
//				} else {
					mailSenden.sendMail(toAdresse, betreff, mailText);
//				}
				double k = pos++ / last * 100;
				progress = (int) k;
				setProgress(progress);
//				message.append("send to: " + toAdresse + "\n");
				// hier wird der Name und Vorname gelesen, wird nicht verwendet
				line = mailFrom.readLine();
			}
//			if (mailTest.isSelected()) {
//				mailToFile.close();
//			}
			mailFrom.close();
			mailSenden.closeFile();
		}

		// Die message eines mails lesen
		private String readMailText(MailFromFile file) {
			StringBuffer sb = new StringBuffer(255);
			String line = file.readLine();
			while (!line.startsWith("---")) {
				sb.append(line);
				sb.append("\n");
				line = file.readLine();
			}
			return sb.toString();
		}

		/*
		 * die mail in ein File schreiben, zu Testzwechen
		 */
//		private void writeMailToFile(String toAdresse, String betreff, String message) {
//			if (mailToFile == null) {
//				mailToFile = new MailToFile(Config.sMailTestPath);
//			}
//			mailToFile.println(toAdresse);
//			mailToFile.println(betreff);
//			mailToFile.println(message);
//		}

		/*
		 * Executed in event dispatching thread. Wenn erledigt wird diese Methode
		 * aufgerufen.
		 */
		@Override
		public void done() {
//  	            Toolkit.getDefaultToolkit().beep();
			btnSchliessen.setEnabled(true);
			setProgress(100);
			if (doGenerate) {
				message.append(mailGenerator.readEnd());
			} else {
//				if (mailTest.isSelected()) {
//					message.append(mailToFile.readEnd());
//				} else {
					message.append(mailSenden.readEnd());
//				}
			}
			dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
