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
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.ExcelSpielplan;
import com.rmo.abwesend.util.Trace;
import com.rmo.abwesend.view.util.CmUtil;

/**
 * Spiele von einem excel-File einlesen Zuerst wird das File mit den Spieldaten
 * heruntergeladen von Swisstennis und dann werden die Daten eingelesen.
 * 
 * @author Ruedi
 *
 */
public class MatchesImport extends BasePane implements ActionListener, PropertyChangeListener {

	private final String BTN_EXCEL = "MS-Excel"; // Was im JButton für herunterlanden steht

	private JFrame mainFrame;
	private WebDriver driver;
	private JButton btnEinlesen;
	private JCheckBox spieleLoeschen;

	// Progress dialog
	private JDialog dialog;
	private JProgressBar progressBar;
	private JTextArea message;
	private JFormattedTextField abDatum;
	private JButton btnStart;
	private JButton btnLoeschen;
	private JButton btnSchliessen;

	private MyTask task;
	private ExcelSpielplan excelPlan;

	public MatchesImport(JFrame parent) {
		this.mainFrame = parent;
	}

	public JComponent getPanel() {
		JPanel pane = new JPanel(new GridBagLayout());

		int row = 0;
		JLabel labelTitel = new JLabel("Spielplan von Swiss Tennis herunterladen");
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

		pane.add(new JLabel("ab Datum"), getConstraintFirst(0, row));
		abDatum = new JFormattedTextField(Config.sdfSwiss);
		abDatum.setPreferredSize(Config.datumFeldSize);
		abDatum.setMaximumSize(Config.datumFeldSize);
		abDatum.setMinimumSize(Config.datumFeldSize);
		abDatum.setValue(new Date());
		pane.add(abDatum, getConstraintNext(1, row++));

		JButton btnLaden = new JButton("Spieldaten herunterladen");
		btnLaden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnLaden.setEnabled(false);
				startDownload();
			}
		});
		pane.add(btnLaden, getConstraintFirst(0, row++));

		pane.add(new JLabel(" "), getConstraintFirst(0, row++));

		JLabel labelTitel2 = new JLabel("Spieldaten einlesen");
		labelTitel2.setFont(Config.fontTitel);
		gbc = getConstraintNext(0, row++);
		gbc.gridwidth = 2;
		pane.add(labelTitel2, gbc);

		pane.add(new JLabel(Config.planDirKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.planDir), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planFileKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.planFile), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planRowStartKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Integer.toString(Config.planRowStart)), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planColDatumZeitKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Integer.toString(Config.planColDatumZeit)), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planColDatumKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Integer.toString(Config.planColDatum)), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planColZeitKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Integer.toString(Config.planColZeit)), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planColName1Key), getConstraintFirst(0, row));
		pane.add(new JLabel(Integer.toString(Config.planColName1)), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planColName2Key), getConstraintFirst(0, row));
		pane.add(new JLabel(Integer.toString(Config.planColName2)), getConstraintNext(1, row++));

		pane.add(new JLabel(Config.planTrennCharKey), getConstraintFirst(0, row));
		pane.add(new JLabel(Config.planTrennChar), getConstraintNext(1, row++));

		spieleLoeschen = new JCheckBox("Bestehende Spiele löschen");
		spieleLoeschen.setSelected(true);
		pane.add(spieleLoeschen, getConstraintFirst(0, row++));

		btnEinlesen = new JButton("Spieldaten einlesen");
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
			if (!startSeiteLesen() || !downloadSpielplan()) {
				return false;
			}
		} catch (Exception ex) {
			CmUtil.alertError("SwissTennis Seite lesen", ex);
			return false;
		}
//		driver.close();
//		driver.quit();
		return true;
	}

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
			CmUtil.alertError(
					"Kann Turnierseite von Swiss Tennis nicht öffnen. '" + Config.swissTennisUrlTournament + "'",
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
	private boolean downloadSpielplan() {
		Trace.println(3, "SwissTennis downloadSpielplan()");

		StringBuffer xpath = new StringBuffer(100);
		xpath.append("//a[@href='../servlet/CalendarAdm?tournament=Id");
		xpath.append(Config.swisstennisTournament);
		xpath.append("&lang=D']");
		WebElement element = driver.findElement(By.xpath(xpath.toString()));
		Trace.println(5, "element Tag: " + element.getTagName() + " " + element.getText());
		element.click();

//		if (driver instanceof JavascriptExecutor) {
//			((JavascriptExecutor) driver)
//				.executeScript("../servlet/Calendar?tournament=ID105694&lang=D");
//		}
		// das Datum setzen beim Spielplan
		// <input id="inp_DateRangeFilter.fromDate" class="text" type="text"
		// value="31.08.2019"
		String datumVon = abDatum.getText();
		WebElement dateFrom = driver.findElement(By.xpath("//input[@id='Inp_DateRangeFilter.fromDate']"));
		dateFrom.clear();
		dateFrom.sendKeys(datumVon);
//    	driver.findElement(By.xpath("//input[@id='invoice_supplier_id'])).setAttribute("value", "your value");
		// kann einzelne JButton nicht aufrufen, jetzt Iteration
		List<WebElement> allButtons = driver.findElements(By.xpath("//button"));
		for (WebElement element1 : allButtons) {
			if (element1.getText().contains(BTN_EXCEL)) {
				element1.click();
			}
		}
		return true;
	}

	/**
	 * Den Spielplan vom Excel-File einlesen mit einem Popup-Window. Dieses wird
	 * aufgerufen von Spieldaten einlesen.
	 */
	private void showPopup() {
		// Ein Popup-Window erstellen
		dialog = new JDialog(mainFrame, "Spiele einlesen");
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
				excelPlan.deleteFile(message);
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
		btnStart.setEnabled(false);
		dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		task = new MyTask();
		task.addPropertyChangeListener(this);
		task.execute();
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
			if (spieleLoeschen.isSelected()) {
				try {
					MatchData.instance().deleteAllRow();
				} catch (SQLException ex) {
					// nichts machen
				}
			}

			excelPlan = new ExcelSpielplan();
			try {
				if (excelPlan.openFile(Config.planFile, message) < 0) {
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
			double last = excelPlan.getLastRowNr();
			Iterator<Row> iterator = excelPlan.getIterator();
			while (iterator.hasNext()) {
				i++;
				Row lRow = iterator.next();
				excelPlan.readLine(lRow); // <=== hier wird verarbeitet
				double k = i / last * 100;
				progress = (int) k;
				setProgress(progress);
			}
			message.setText(excelPlan.readEnd());
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
			message.append(excelPlan.readEnd());
			dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
