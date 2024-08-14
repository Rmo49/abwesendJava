package com.rmo.abwesend.view;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.ExcelSpieler;
import com.rmo.abwesend.util.Trace;
import com.rmo.abwesend.view.util.CmUtil;

/**
 * Spielerdaten in Excel-Format von Swisstennis herunterladen dann im Excel-File
 * Zeile für Zeile die Spieler einlesen. Was vorher gesetzt werden muss: die
 * erste Zeile ab der gelesen werden soll Spalte wo steht Tableau, Name und
 * Vorname
 * 
 * @author Ruedi
 *
 */
public class SpielerDatenImport extends BasePane implements ActionListener, PropertyChangeListener {

//	private final String BTN_EXCEL = "MS-Excel"; // Was im JButton für herunterlanden steht

	private JFrame mainFrame;
	private WebDriver driver;
	private JButton btnEinlesen;
	private JCheckBox tableauLoeschen;

	// Progress dialog
	private JDialog dialog;
	private JProgressBar progressBar;
	private JTextArea message;
	private JButton btnStart;
	private JButton btnLoeschen;
	private JButton btnSchliessen;

	private MyTask task;
	private ExcelSpieler excelSpieler;

	public SpielerDatenImport(JFrame parent) {
		this.mainFrame = parent;
	}

	public JComponent getPanel() {
		JPanel pane = new JPanel(new GridBagLayout());

		int row = 0;
		JLabel labelTitel = new JLabel("Spieler von Swiss Tennis herunterladen");
		labelTitel.setFont(Config.fontTitel);
		GridBagConstraints gbc = getConstraintNext(0, row++);
		gbc.gridwidth = 2;
//		gbc.anchor = GridBagConstraints.PAGE_START;
//		gbc.ipady = 5;
		pane.add(labelTitel, gbc);

		pane.add(new JLabel("Daten anpassen"), getConstraintFirst(0, row));
		pane.add(new JLabel(">>>>>> In der Datei 'AbwesendConfig.txt'"), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.swissTennisUrlKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.swissTennisUrl), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.swisstennisTournamentKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.swisstennisTournament), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.swisstennisIdKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.swisstennisId), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.swisstennisPwdKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.swisstennisPwd), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.webDriverKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.webDriver), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.webDriverFileKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.webDriverFile), getConstraintNext(1, row++));

		JButton btnLaden = new JButton("Spieler Daten herunterladen");
		btnLaden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnLaden.setEnabled(false);
				startDownload();
			}
		});
		pane.add(btnLaden, getConstraintFirst(0, row++));

		pane.add(new JLabel(" "), getConstraintFirst(0, row++));

		JLabel labelTitel2 = new JLabel("Spieler Daten einlesen");
		labelTitel2.setFont(Config.fontTitel);
		gbc = getConstraintNext(0, row++);
		gbc.gridwidth = 2;
		pane.add(labelTitel2, gbc);

		pane.add(new JLabel(Config.spielerImportDirKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.spielerImportDir), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.spielerImportFileKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.spielerImportFile), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.spielerColKonkurrenzKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.spielerColKonkurrenz), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.spielerColName1Key), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.spielerColName1), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.spielerColVorname1Key), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.spielerColVorname1), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.spielerColName2Key), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.spielerColName2), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.spielerColVorname2Key), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.spielerColVorname2), getConstraintNext(1, row++));
		
		tableauLoeschen = new JCheckBox("Beziehung Spieler->Tableau zuerst löschen");
		tableauLoeschen.setSelected(false);
		pane.add(tableauLoeschen, getConstraintNext(1, row++));

		btnEinlesen = new JButton("Spieler einlesen");
		btnEinlesen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnEinlesen.setEnabled(false);
				showPopup();
			}
		});
		pane.add(btnEinlesen, getConstraintFirst(0, row++));

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
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.weightx = 1;
//		gbc.gridx = colNr;
//		gbc.gridy = rowNr;
//		gbc.ipady = 3;
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
		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.weightx = 4;
//		gbc.gridx = colNr;
//		gbc.gridy = rowNr;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 8, 2, 2);
		gbc.gridx = colNr;
		gbc.gridy = rowNr;

		return gbc;
	}

	/**
	 * Download der Excel-Datei
	 */
	private boolean startDownload() {
		try {
			if (!startSeiteLesen() || !downloadSpieler()) {
				return false;
			}
		} catch (Exception ex) {
			CmUtil.alertError("SwissTennis Seite lesen", ex);
			return false;
		}
		return true;
	}

	/**
	 * Die erste Seite von Swisstennis lesen
	 *
	 * @return true wenn gefunden
	 * @throws NoSuchElementException
	 */
	public boolean startSeiteLesen() throws NoSuchElementException {
		Trace.println(3, "SwissTennis startSeiteLesen()");
		// declaration and instantiation of objects/variables
//	    	System.setProperty("webdriver.firefox.marionette","C:\\geckodriver.exe");
//			WebDriver driver = new FirefoxDriver();

		Trace.println(4, "SwissTennis create WebDriver");
		try {
			if (Config.webDriver.contains("chrome")) {
				// im System muss der driver gesetzt werden.
				System.setProperty(Config.webDriver, Config.webDriverFile);
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless");
//				driver = new ChromeDriver(options);
				driver = new ChromeDriver();

			} else {
				throw new Exception("WebDriver nicht gefunden: " + Config.webDriverFile);
			}
		} catch (Exception ex) {
			CmUtil.alertError("SwissTennis Seite öffnen ", ex);
			return false;
		}

		String expectedTitle = "Login-Zone";
		String actualTitle = "";

		// launch Browser and direct it to the Base URL
		Trace.println(4, "SwissTennis get baseUrl: " + Config.swissTennisUrlTournament);
		try {
			driver.get(Config.swissTennisUrlTournament);
		} catch (Exception ex) {
			CmUtil.alertError("Kann Seite von Swiss Tennis nicht öffnen. '" + Config.swissTennisUrlTournament + "'",
					ex.getMessage());
			return false;
		}

		// get the actual value of the title
		actualTitle = driver.getTitle();
		Trace.println(4, "SwissTennis.getTitle(): " + actualTitle);
		if (actualTitle.contentEquals(expectedTitle)) {
			WebElement element = null;
			element = driver.findElement(By.name("id"));
			element.sendKeys(Config.swisstennisId);

			element = driver.findElement(By.name("pwd"));
			element.sendKeys(Config.swisstennisPwd);

			element = driver.findElement(By.name("Tournament"));
			element.click();
		} else {
			CmUtil.alertError("Swiss Tennis", "Seite nicht gefunden: " + Config.swissTennisUrlTournament);
			return false;
		}
		return true;
	}

	/**
	 * Wenn hier, sollte auf der Übersichtsseite von SwissTennis stehen.
	 * 
	 * @return
	 */
	private boolean downloadSpieler() {
		Trace.println(3, "SwissTennis downloadSpielplan()");
		// Crome > Entwicklertools Lasche Sources
		// für das Menü Spieler-Liste steht:
		// servlet/PlayerList?tournament=Id144642&lang=D

		StringBuffer xpath = new StringBuffer(100);
		xpath.append("//a[@href='../servlet/PlayerList?tournament=Id");
		xpath.append(Config.swisstennisTournament);
		xpath.append("&lang=D']");
		WebElement element = driver.findElement(By.xpath(xpath.toString()));
		Trace.println(5, "element Tag: " + element.getTagName() + " " + element.getText());
		element.click();

		// servlet/PlayerList.xls?tournament=Id144642&lang=D
		xpath.setLength(0);
		xpath.append("//a[@href='../servlet/PlayerList.xls?tournament=Id");
		xpath.append(Config.swisstennisTournament);
		xpath.append("&lang=D']");
		element = driver.findElement(By.xpath(xpath.toString()));
		Trace.println(5, "element Tag: " + element.getTagName() + " " + element.getText());
		element.click();

		return true;
	}

	/**
	 * Den Spielplan vom Excel-File einlesen mit einem Popup-Window. Dieses wird
	 * aufgerufen von Spieldaten einlesen.
	 */
	private void showPopup() {
		// Ein Popup-Window erstellen
		dialog = new JDialog(mainFrame, "Spieler importieren");
		dialog.setLocationRelativeTo(btnEinlesen);
		dialog.setSize(200, 250);
		dialog.setModal(true);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		addSpace(panel);

		btnStart = new JButton("Start");
		btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnStart.setEnabled(true);
		btnStart.addActionListener(this);
		// siehe: actionPerformed(ActionEvent evt)
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

		btnLoeschen = new JButton("Datei löschen");
		btnLoeschen.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLoeschen.setEnabled(false);
		panel.add(btnLoeschen);

		btnLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				excelSpieler.deleteFile(message);
			}
		});

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
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
	}

	/**
	 * Invoked when the user presses the start button. Startet MyTask, in der
	 * Methode doInBackground wird alles ausgeführt.
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (tableauLoeschen.isSelected()) {
			spielerTabLoeschen();
		}
		btnStart.setEnabled(false);
		dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		task = new MyTask();
		task.addPropertyChangeListener(this);
		task.execute();
		// => siehe MyTask.doInBackground()
	}

	
	/**
	 * Alle Beziehungen Spieler->Tableau löschen
	 */
	private void spielerTabLoeschen() {
		try {
			SpielerTableauData.instance().deleteAllRow();
		} catch (Exception ex) {
			Trace.println(3, "Fehler bei Spieler->Tableau löschen: " + ex.toString());
		}
		Trace.println(3, "Alle Beziehungen Spieler->Tableau gelöscht");
	}
	
	/**
	 * Sichern der geänderten Werte.
	 */
	@Override
	protected void saveData() {
		// nichts vorläufig
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
//			message.setText("Progress: " + task.getProgress());
		}
	}

//-------- MyTask für Einlesen der Match-Daten vom File.

	class MyTask extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread. Hier wird das einlesen ausgeführt.
		 */
		@Override
		public Void doInBackground() {
			Trace.println(3, "SpielerDatenImport.doInBackground()");
			excelSpieler = new ExcelSpieler();
			try {
				if (excelSpieler.openFile(Config.spielerImportFile, message) < 0) {
					// wenn Fehler
					btnLoeschen.setEnabled(true);
					btnSchliessen.setEnabled(true);
					return null;
				}
			} catch (Exception ex) {
				// nix tun
//    			String xx = ex.getMessage();
			}

			double i = 0.0;
			int progress = 0;
			setProgress(0);
			double last = excelSpieler.getLastRowNr();
			Iterator<Row> iterator = excelSpieler.getIterator();
			while (iterator.hasNext()) {
				i++;
				Row lRow = iterator.next();
				excelSpieler.readLine(lRow); // <=== hier wird verarbeitet
				double k = i / last * 100;
				progress = (int) k;
				setProgress(progress);
			}
//    		message.setText(excelSpieler.readEnd());
			return null;
		}

		/*
		 * Executed in event dispatching thread. Wenn erledigt wird diese Methode
		 * aufgerufen.
		 */
		@Override
		public void done() {
//	            Toolkit.getDefaultToolkit().beep();
			btnLoeschen.setEnabled(true);
			btnSchliessen.setEnabled(true);
//	            setCursor(null); //turn off the wait cursor
			message.append(excelSpieler.readEnd());
			dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
