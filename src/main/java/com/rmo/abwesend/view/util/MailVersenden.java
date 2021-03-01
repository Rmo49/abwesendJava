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
import javax.swing.SwingWorker;

import com.rmo.abwesend.model.Mail;
import com.rmo.abwesend.model.MailData;
import com.rmo.abwesend.model.Match;
import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.MailSenden;

/**
 * Versenden von mails. Der mail-Text ist in der DB gespeichert.
 * Alle Matches lesen, sortieren nach SpielerId und Match-Datum.
 * @author ruedi
 *
 */
public class MailVersenden implements ActionListener,  PropertyChangeListener {

	private JFrame mainFrame;

	private JTextArea textBetreff;
	private JTextArea textMail;
	private JFormattedTextField vonDatum;
	private JFormattedTextField bisDatum;
	private JButton btnMailSenden;
	private JTextField testTo;
	private JTextField fromEmail;
	private boolean doCheckAdress;
	
	private List<Match> listMatches;	// alle Matches der Zeitspanne, geordnet nach Spieler und Datu
	private JCheckBox testInFile;
	private MailSenden sendMail;

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
		JPanel pane = new JPanel(new GridBagLayout());
//		pane.setSize(400, 200);
		pane.setBorder(BorderFactory.createLineBorder(Color.black));

	    int zeileNr = 0;
	    
		JLabel labelTitel = new JLabel("Mail versenden");
		labelTitel.setFont(Config.fontTitel);
		pane.add(labelTitel, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("Betreff"), getConstraintFirst(0, zeileNr));
		textBetreff = new JTextArea(1,40);
		textBetreff.setEditable(true);
		pane.add(textBetreff, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("Mail-Text"), getConstraintFirst(0, zeileNr));
		textMail = new JTextArea(8,40);
		JScrollPane scrollPane = new JScrollPane(textMail); 
		textMail.setEditable(true);
		pane.add(scrollPane, getConstraintNext(1, zeileNr++));
		
		pane.add(new JLabel("Variable:"), getConstraintFirst(0, zeileNr));
		pane.add(new JLabel("<Vorname>, <Spiele> müssen im Text vorhanden sein"), getConstraintNext(1, zeileNr++));
		
		
		JButton btnSpeichern = new JButton("Speichern");
		btnSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mailTextSpeichern();
 			}
		});
		pane.add(btnSpeichern, getConstraintNext(1, zeileNr++));

		// DatumSpanne
		pane.add(new JLabel("ab Datum"), getConstraintFirst(0, zeileNr));
		vonDatum = new JFormattedTextField(Config.sdfDatum);
		vonDatum.setPreferredSize(Config.datumFeldSize);
		pane.add(vonDatum, getConstraintNext(1, zeileNr++));
		
		pane.add(new JLabel("bis Datum"), getConstraintFirst(0, zeileNr));
		bisDatum = new JFormattedTextField(Config.sdfDatum);
		bisDatum.setPreferredSize(Config.datumFeldSize);
		pane.add(bisDatum, getConstraintNext(1, zeileNr++));
		
		testInFile = new JCheckBox("Test");
		testInFile.setSelected(true);
		pane.add(testInFile, getConstraintNext(0, zeileNr));

		JPanel paneFlow = new JPanel(new FlowLayout());
		paneFlow.add(new JLabel("TO: "));
		testTo = new JTextField();
		testTo.setPreferredSize(new Dimension(150, Config.textFieldHeigth));
		testTo.setEditable(true);
		paneFlow.add(testTo);
		paneFlow.add(new JLabel("wenn ausgefüllt werden alle mails an diese Adresse gesendet"));
		pane.add(paneFlow, getConstraintNext(1, zeileNr++));

		pane.add(new JLabel("ab e-mail"), getConstraintFirst(0, zeileNr));
		JPanel paneFlow2 = new JPanel(new FlowLayout());
		fromEmail = new JTextField();
		fromEmail.setPreferredSize(new Dimension(150, Config.textFieldHeigth));
		fromEmail.setEditable(true);
		paneFlow2.add(fromEmail);
		paneFlow2.add(new JLabel("erst ab dieser Adresse senden (wenn schon ein Teil gesendet)"));
		pane.add(paneFlow2, getConstraintNext(1, zeileNr++));
		
		btnMailSenden = new JButton("Mail versenden");
		btnMailSenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listMatches != null) {
					listMatches.clear();
				}
				matchesLesen();
				sendMail = new MailSenden(textBetreff.getText(), textMail.getText(), 
						testInFile.isSelected(), testTo.getText());
				if (fromEmail.getText().length() > 5) {
					doCheckAdress = false;
				}
				else {
					doCheckAdress = true;
				}
				showPopup();
 			}
		});
		pane.add(btnMailSenden, getConstraintNext(1, zeileNr++));
		
		readMailTextFromDb();
		setDatumSpanne();
		
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
	 * Die Daten von DB.
	 */
	private void readMailTextFromDb() {
		try {
			Mail mail = MailData.instance().readAll();
			textBetreff.setText(mail.getBetreff());
			textMail.setText(mail.getText());
		}
		catch (SQLException ex) {
			CmUtil.alertWarning("Mail speichern", "Problem: " +ex.getMessage());
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
		}
		else {
			// ein Tag voraus
			now.setTime(now.getTime() + (2 * Config.einTag));
		}
		vonDatum.setValue(now);
		now.setTime(now.getTime() + Config.einTag);
		bisDatum.setValue(now);
	}
	
	
	/**
	 * Speichern in der DB
	 */
	private void mailTextSpeichern() {
		Mail mail = new Mail();
		mail.setMailId(1);
		mail.setBetreff(textBetreff.getText());
		mail.setText(textMail.getText());
		try {
			MailData.instance().add(mail);
		}
		catch (SQLException ex) {
			CmUtil.alertWarning("Mail speichern", "Problem: " +ex.getMessage());
			return;
		}
		
	}
	
	/**
	 * Alle Matches in der Zeitspanne einlesen
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
					}
					else {
						listMatches.addAll(listMatchesRead);
					}
				}
				catch (SQLException ex) {
					CmUtil.alertWarning("Mail versenden, Fehler", "Lesen der Matches: " + ex.getMessage());
					return;
				}
				dateVon.setTime(dateVon.getTime() + Config.einTag);
//				Calendar c = Calendar.getInstance();
//				c.setTime(dateVon);
//				c.add(Calendar.DATE, 1);  // number of days to add
//				dateVon = c.getTime();
			}
			// Liste sortieren nach Spieler ID
			Collections.sort(listMatches,new Comparator<Match>(){
                @Override
				public int compare(Match s1, Match s2){
                      int id =  s1.getSpielerId() - s2.getSpielerId();
                      if (id != 0) {
                    	  return id;
                      }
                      return s1.getDatum().compareTo(s2.getDatum());
                }});
		}
		catch (java.text.ParseException ex) {
			CmUtil.alertWarning("Mail versenden", "Falsches Datum: " +ex.getMessage());
			return;
		}
	}

	
	/**
	 * Die mails versenden mit einem Popup-Window.
	 * Dieses wird aufgerufen von Mail Versenden einlesen.
	 */
	private void showPopup() {
		// Ein Popup-Window erstellen
		dialog = new JDialog(mainFrame, "Mails versenden");
		dialog.setLocationRelativeTo(btnMailSenden);
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
		message.setMargin(new Insets(5,5,5,5));
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
	 * @param panel
	 */
	private void addSpace(JPanel panel) {
		panel.add(Box.createRigidArea(new Dimension(0,6)));
	}

	 /**
     * Invoked when the user presses the start button.
     * Startet MyTask, in der Methode doInBackground wird alles ausgeführt.
     */
    @Override
	public void actionPerformed(ActionEvent evt) {
        btnStart.setEnabled(false);
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed, siehe Klasse unten.
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
		
		  /*
		   * Main task. Executed in background thread.
		   * Hier wird das einlesen ausgeführt.
		   */
		  @Override
		  public Void doInBackground() {
				btnSchliessen.setEnabled(true);
		    	
		  		int progress = 0;
		  		setProgress(0);
				double last = listMatches.size();
				
				List<Match> listMatchSpieler = new ArrayList<Match>();
				int spielerIdLast = -1;
				int pos = 0;	// für die Berechnung des Fortschrittes
				Match match = null;
			
				Iterator<Match> matchIter = listMatches.iterator();
				// vorlesen erster Eintag
				if (matchIter.hasNext()) {
					match = matchIter.next();
					pos++;
					spielerIdLast = match.getSpielerId();
					listMatchSpieler.add(match);
				}
				
				while (matchIter.hasNext()) {  				
					double k = pos / last * 100;
					progress = (int) k;    			
					setProgress(progress);
					
					match = matchIter.next();
					// Alle Matches eines Spieler lesen
					if (spielerIdLast == match.getSpielerId()) {
						listMatchSpieler.add(match);
					}
					else {
						// der nächste Spieler bereits im Iterator, zuerst mail senden
						sendMail(listMatchSpieler);

						listMatchSpieler.clear();
						listMatchSpieler.add(match);
						spielerIdLast = match.getSpielerId();
					}
					pos++;
				}
				// noch den letzten match senden
				sendMail(listMatchSpieler);
				
				setProgress(100);
		  		return null;   		
		  }
     
		  /**
		   * Die mails eines Spielers versenden
		   * @param listMatchSpieler, die Liste der Matches
		   */
		  private void sendMail(List<Match> listMatchSpieler) {
				if (! doCheckAdress) {
					if (sendMail.isSameToAdress(
							listMatchSpieler.get(0).getSpielerId(), fromEmail.getText())) {
						doCheckAdress = true;
					}
				}
				if (doCheckAdress) {
					sendMail.mailSenden(listMatchSpieler);
				}

		  }
		  
          /*
           * Executed in event dispatching thread.
           * Wenn erledigt wird diese Methode aufgerufen.
           */
          @Override
          public void done() {
//  	            Toolkit.getDefaultToolkit().beep();
              btnSchliessen.setEnabled(true);
//  	            setCursor(null); //turn off the wait cursor
      		message.append(sendMail.readEnd());
              dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
	}
	
	
}

