package com.rmo.abwesend.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

import com.rmo.abwesend.AbwesendMain;
import com.rmo.abwesend.model.ConfigDbData;

/**
 * Configuration Abwesend. Kann seine Daten vom Config-File einlesen, und
 * schreiben. Allgemeine Daten werden in der DB gespeichert. Diese werden nach
 * dem Start in die keyValueMap gelesen. Von dieser Map werden die statischen
 * (typisierten) Variablen gesetzt, und diese Werte sogleich wieder in die Map
 * zurückgeschrieben, somit werden Werte mit den Default-Werten gesetzt falls
 * noch nicht in der DB. Wenn ein Wert geändert wird, wird auch die statische
 * Variable gesetzt. Am Ende des Programms werde alle Werte in die DB
 * geschrieben.
 */
public class Config {
	public static final boolean newVersion = true;
	private static boolean configDataGelesen = false;

	// fonts, background, Eingabefelder
	public static final String fontStyle = "sans-serif";
	public static final Font fontTitel = new Font(fontStyle, Font.BOLD, 16);
	public static final Color colorBackground = Color.LIGHT_GRAY;
	public static final Color colorWeekend = new Color(180, 180, 180);
	public static final Color colorSpieler = new Color(180, 220, 220);
	public static final Color colorTable = new Color(150, 200, 200);
	public static final Color colorTCA = new Color(240, 140, 110);

	public static final int textFieldHeigth = 25;
	public static final Dimension datumFeldSize = new Dimension(90, textFieldHeigth);

	// Datums formate
	public static final SimpleDateFormat sdfDb = new SimpleDateFormat("yyy-MM-dd HH:mm");
	public static final SimpleDateFormat sdfDatum = new SimpleDateFormat("yyy-MM-dd");
	public static final SimpleDateFormat sdfZeit = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat sdfSwiss = new SimpleDateFormat("dd.MM.yyyy");
	public static final SimpleDateFormat sdfTagName = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm", Locale.GERMANY);
	public static final long einTagLong = 24 * 60 * 60 * 1000; // für Datum in long

	/** Config path filename und file */
	public static String sPath = "";
	private static final String sConfigFileName = "AbwesendConfig.txt";
	private static File sConfigFile;

	/** Weitere Files */
	public static final String sTraceFileName = "Trace.txt";
	public static final String sMailToSend = "MailToSend.txt";
	public static String sMailToSendPath = "";
	public static final String sMailControl = "MailControl";
	public static String sMailControlPath = "";
	public static final String sMailTest = "MailTest.txt";
	public static String sMailTestPath = "";

	public static final String sDbPwFileName = "DbPassword.txt";

	/** Die Properties, gespeichert in der Config-Datei */
	private static Properties mProperties;
	private static Vector<Object> mPropertyList;

	// ----- Config-Werte in der DB
	// alle Werte werden hier gehalten und in die DB gespeichert
	private static Map<String, String> keyValueMap = new TreeMap<>();
	// das Passwort um die Config-Werte zu ändern
	public static String configDbPasswort = "T4123";

	// --- Trace
	public static int traceLevel = 5;
	private static final String traceLevelKey = "trace.level";
	public static boolean traceTimestamp;
//	private static final String traceTimestampKey = "trace.timestamp";

	// --- Version von Java
	public static int javaVersion = 10;

	// --- Database, muss in Config, oder Parameter gespeichert werden.
	public static final String dbNameKey = "db.name";
	public static String dbName = "tennis";
	public static String dbUrlPrefix = "jdbc:mysql:";
	public static final String dbUrlKey = "db.url";
	public static String dbUrl = "//server41.hostfactory.ch";
	public static final String dbPortKey = "db.port";
	public static String dbPort = "3307";
	public static String dbUrlSetting = "?useLegacyDatetimeCode=false&serverTimezone=UTC";
	public static final String dbUserKey = "db.username";
	public static String dbUser = "javacon";
//	public static final String dbPasswordKey = "db.password";
//	public static String dbPassword = "Tennis4123";

	// --- Download von der Tennis-Homepage (Properties)
	public static final String webDriverKey = "browser.webdriverTyp";
	public static String webDriver = "webdriver.chrome.driver";
	public static final String webDriverFileKey = "browser.driverLocation";
	public static String webDriverFile = "C:/Program Files (x86)/Google/Chrome/chromedriver.exe";

	// Swiss Tennis in DB speichern, da für alle gleich
	public static final String swissTennisUrlKey = "swisstennis.url";
	public static String swissTennisUrl = "https://comp.swisstennis.ch/advantage/servlet/ProtectedDisplayTournament?Lang=D&tournament=Id";
	public static String swissTennisUrlTournament;
	public static final String swisstennisTournamentKey = "swisstennis.tournament";
	public static String swisstennisTournament = "112263";
	public static final String swisstennisIdKey = "swisstennis.id";
	public static String swisstennisId = "2007";
	public static final String swisstennisPwdKey = "swisstennis.pwd";
	public static String swisstennisPwd = "28560T";

	// --- Anzeige der Abwesenheiten Datum und Zeit
	// Turnier Datum, in DB
	public static GregorianCalendar beginDatumTmp = new GregorianCalendar(2022, 8, 26); // 0=Jan
	public static final String datumBeginKey = "turnier.beginDatum";
	public static Date turnierBeginDatum = beginDatumTmp.getTime();
	public static final String datumEndKey = "turnier.endDatum";
	public static Date turnierEndDatum = beginDatumTmp.getTime();
	// Anzahl maximale Tage, die geplant und angezeigt werden
	public static int turnierMaxTage = 3;
	// maximal Anzahl erlaubte Tage
	public static int maxTageErlaubt = 30;

	// Combobox der Tabelle, Anzahl anzuzeigende Werte
	public static final String showTableauBoxKey = "anzeige.tableauBox";
	public static int showTableauBox = 10;

	// die aktuellen Datumspanne, wo die Abwesenheiten gezeigt werden, sind
	// Properties
	public static final String showBeginDatumKey = "anzeige.beginDatum";
	public static Date showBeginDatum = turnierBeginDatum;
	public static final String showEndDatumKey = "anzeige.endDatum";
	public static Date showEndDatum = turnierBeginDatum;
	// die aktuell anzuzeigenden Tage, wird bei Veränderung berechnet
	public static int showBeginNumber = 0;
	public static int showEndNumber = turnierMaxTage;

	public static final String weekendBeginKey = "weekend.beginZeit";
	public static double weekendBegin = 10.0;
	public static final String weekendEndKey = "weekend.endZeit";
	public static double weekendEnd = 17.0;
	public static double weekendDauer = 7.0;

	public static final String weekBeginKey = "week.beginZeit";
	public static double weekBegin = 17.0;
	public static final String weekEndKey = "week.endZeit";
	public static double weekEnd = 22.0;
	public static double weekDauer = 5.0;

	// die Zeiten pro Tag, erstmals einen kleinen Standardwert
	public static final String zeitStartKey = "zeit.start";
	public static String zeitStartStr = "9;9;17;17;";
	public static int[] zeitStart;
	public static final String zeitEndeKey = "zeit.ende";
	public static String zeitEndeStr = "17;17;22;22";
	public static int[] zeitEnde;
	private static String zeitTrennChar = ";";

	// --- Für einlesen von Spieler und Spielplan, in Config
	public static final String spielerExportDirKey = "spieler.export.dir";
	public static String spielerExportDir = "D:/Downloads";
	public static final String spielerExportFileKey = "spieler.export.file";
	public static String spielerExportFile = "SpielerExport.txt";

	// Spieler import
	public static final String spielerImportDirKey = "spieler.import.dir";
	public static String spielerImportDir = "D:/Downloads";
	public static final String spielerImportFileKey = "spieler.import.file";
	public static String spielerImportFile = "PlayerList.csv";
	public static final String spielerImportSplitCharKey = "spieler.import.splitChar";
	public static String spielerImportSplitChar = ",";
	// falls direkt von XLS file gelesen wird
	public static final String spielerXlsImportFileKey = "spieler.excel.import.file";
	public static String spielerXlsImportFile = "PlayerList.xls";

	public static String spielerRowIndex = "Konkurrenz";
	public static final String spielerColKonkurrenzKey = "spieler.import.konkurrenz";
	public static String spielerColKonkurrenz = "Konkurrenzx";
	public static final String spielerColName1Key = "spieler.import.name1";
	public static String spielerColName1 = "Name";
	public static final String spielerColVorname1Key = "spieler.import.vorname1";
	public static String spielerColVorname1 = "Vorname";
	public static final String spielerColName2Key = "spieler.import.name2";
	public static String spielerColName2 = "Name Doppelpartner";
	public static final String spielerColVorname2Key = "spieler.import.vorname2";
	public static String spielerColVorname2 = "Vorname Doppelpartner";

	public static final String emailImportFileKey = "email.import.file";
	public static String emailImportFile = "emails.txt";

	public static final String planDirKey = "spielplan.dir";
	public static String planDir = "D:/Downloads";
	public static final String planFileKey = "spielplan.file";
	public static String planFile = "Calendar.xls";
	public static final String planTrennCharKey = "spielplan.trennChar.Doppel";
	public static String planTrennChar = "/";
	public static final String planRowStartKey = "spielplan.row.start";
	public static int planRowStart = 2;
	public static final String planColDatumZeitKey = "spielplan.col.datumZeit";
	public static int planColDatumZeit = 1;
	public static final String planColDatumKey = "spielplan.col.datum";
	public static int planColDatum = 1;
	public static final String planColZeitKey = "spielplan.col.zeit";
	public static int planColZeit = 2;
	public static final String planColName1Key = "spielplan.col.name1";
	public static int planColName1 = 4;
	public static final String planColName2Key = "spielplan.col.name2";
	public static int planColName2 = 6;

	// --- mail senden
	public static final String emailUser = "email.user";
	public static final String emailPassword = "email.passwort";
	public static final String emailHostImap = "email.hostImap";
	public static final String emailHostSmtp = "email.hostSmtp";
	public static final String emailSmtpPort = "email.smtpPort";

	// --- Windows
	public static final String windowWidthKey = "window.width";
	public static double windowWidth = 800;
	public static final String windowHeigthKey = "window.heigth";
	public static double windowHeight = 500;
	public static final String windowXKey = "window.x";
	public static double windowX = 50;
	public static final String windowYKey = "window.y";
	public static double windowY = 10;

	/**
	 * Config nur statische Methoden, darum Konstruktor verstcken.
	 */
	private Config() {
	}

	/**
	 * Gibt den gespeicherten Wert zurück aus Property oder Config
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		if (mProperties.getProperty(key) != null) {
			return mProperties.getProperty(key);
		}
		return keyValueMap.get(key);
	}

	/**
	 * Alle key value pairs einlesen, das sind felder mit einer 'final String
	 * wertEndung'. Die Werte werden in der keyValueMap gespeichert.
	 */
	public static void readConfigData() throws Exception {
		Trace.println(1, "Config.readConfigData()");
		// zuerst due Default Werte, falls nichts gesetzt ist.
		writeDefaultInMap();
		// alle Config-Werte von der DB lesen
		keyValueMap = ConfigDbData.instance().readAll(keyValueMap);
		setAllValues();
		showDatumSetzen();
		setZeitenProTag();
		configDataGelesen = true;
	}

	/**
	 * Variable setzen, damit diese einfacher verwendet werden können. Wird
	 * aufgerufen, nachdem alle Werte eingelesen wurden
	 */
	public static void setAllValues() throws Exception {
		Trace.println(2, "Config.setAllValues() from DB");
		swissTennisUrl = getWert(swissTennisUrlKey, swissTennisUrl);
		swisstennisTournament = getWert(swisstennisTournamentKey, swisstennisTournament);
		swisstennisId = getWert(swisstennisIdKey, swisstennisId);
		swisstennisPwd = getWert(swisstennisPwdKey, swisstennisPwd);
		turnierBeginDatum = getWert(datumBeginKey, turnierBeginDatum);
		turnierEndDatum = getWert(datumEndKey, turnierEndDatum);
		// max Tage berechnen
		turnierMaxTage = Math.toIntExact((turnierEndDatum.getTime() - turnierBeginDatum.getTime()) / einTagLong) + 1;
		if (turnierMaxTage < 1 || turnierMaxTage > maxTageErlaubt) {
			turnierMaxTage = maxTageErlaubt + 1;
		}
		// die Url richtig zusammenstellen
		swissTennisUrlTournament = swissTennisUrl + swisstennisTournament;

		weekendBegin = getWert(weekendBeginKey, weekendBegin);
		weekendEnd = getWert(weekendEndKey, weekendEnd);
		weekendDauer = weekendEnd - weekendBegin;
		weekBegin = getWert(weekBeginKey, weekBegin);
		weekEnd = getWert(weekEndKey, weekEnd);
		weekDauer = weekEnd - weekBegin;
		zeitStartStr = getWert(zeitStartKey, zeitStartStr);
		zeitEndeStr = getWert(zeitEndeKey, zeitEndeStr);

		// das file für mails versenden
		sMailToSendPath = sPath + "/" + sMailToSend;
		sMailTestPath = sPath + "/" + sMailTest;
	}

	/**
	 * Alle Werte in den Map schreiben zur Zwischenspeicherung
	 * 
	 * @throws Exception
	 */
	public static void writeDefaultInMap() throws Exception {
		Trace.println(2, "Config.writeDefaultInMap()");
		// String Values
		keyValueMap.put(swissTennisUrlKey, swissTennisUrl);
		keyValueMap.put(swisstennisTournamentKey, swisstennisTournament);
		keyValueMap.put(swisstennisIdKey, swisstennisId);
		keyValueMap.put(swisstennisPwdKey, swisstennisPwd);

		// Datum in die Map speichern
		putWert(datumBeginKey, turnierBeginDatum);
		putWert(datumEndKey, turnierEndDatum);
		putWert(weekendEndKey, weekendEnd);
		putWert(weekendBeginKey, weekendBegin);
		putWert(weekBeginKey, weekBegin);
		putWert(weekEndKey, weekEnd);

		keyValueMap.put(zeitStartKey, zeitStartStr);
		keyValueMap.put(zeitEndeKey, zeitEndeStr);
	}

	/**
	 * Alle Werte in die DB schreiben. Die keyValueMap enthält alle tupel
	 * 
	 * @throws Exception
	 */
	public static void saveConfigData() throws Exception {
		// nur sichern, wenn auch vorher gelesen
		if (configDataGelesen) {
			Trace.println(1, "Config.saveConfigData()");
			// zuerst alles löschen
			ConfigDbData.instance().deleteAll();
			// alle Werte in die DB schreiben
			ConfigDbData.instance().addAll(keyValueMap);
		}
	}

	/**
	 * Den gesetzen Wert einer Variablen lesen und zurückgeben
	 * 
	 * @param key Name der Variable
	 * @return gefunderner Wert, oder null wenn nicht gefunden
	 */
	private static String getWert(String key, String wert) {
		String tmp = keyValueMap.get(key);
		if (tmp == null || tmp.length() < 1) {
			return wert;
		}
		return tmp;
	}

	/** Einen int-Werte einlesen */
//	private static int getWert(String key, int wert) throws Exception {
//		int tmp = wert;
//		try {
//			tmp = Integer.parseInt(getWert(key, ""));
//		}
//		catch (NumberFormatException ex) {
//			throw new Exception("Config-Wert: '" + key + "' falsch \n"
//					+ "Fehler: " + ex.getMessage());
//		}
//		return tmp;
//	}

	/** Einen double-Werte einlesen */
	private static double getWert(String key, double wert) throws Exception {
		try {
			return Double.parseDouble(getWert(key, Double.toString(wert)));
		} catch (NumberFormatException ex) {
			throw new Exception("Config-Wert: '" + key + "' falsch \n" + "Fehler: " + ex.getMessage());
		}
	}

	/** Einen Date-Werte einlesen */
	private static Date getWert(String key, Date wert) throws Exception {
		String tmp = sdfDatum.format(wert);
		try {
			return sdfDatum.parse(getWert(key, tmp));
		} catch (ParseException ex) {
			throw new Exception("Config-Wert: '" + key + "' falsch \n" + "Fehler: " + ex.getMessage());
		}
	}

	/** Einen int-Werte in den Map speichen */
//	private static void putWert(String key, int wert) {
//		keyValueMap.put(key, Integer.toString(wert));
//	}

	/** Einen double-Werte in den Map speichen */
	private static void putWert(String key, double wert) {
		keyValueMap.put(key, Double.toString(wert));
	}

	/** Einen datum in den Map speichen */
	private static void putWert(String key, Date wert) {
		keyValueMap.put(key, sdfDatum.format(wert));
	}

	/** Prüft, ob das File vorhanden ist */
	private static void checkConfigFile(String configFileName) throws Exception {
		String fileName = null;
		if (sPath.length() > 0) {
			fileName = sPath + "/" + configFileName;
		} else {
			fileName = configFileName;
		}
		sConfigFile = new File(fileName);
		if (!sConfigFile.exists()) {
			if (!sConfigFile.createNewFile()) {
				throw new Exception("Kann ConfigFile nicht anlegen: " + fileName);
			}
		}
	}

	// ------- Property file handling -----------------------------

	/**
	 * Alle Properties einlesen, wenn in Properties-file nicht gefunden, wird ein
	 * default-Wert gesetzt dieser wird dann mit dem Property im flie gespeichert.
	 * So werden neue Properties im File angezeigt.
	 */
	public static void readProperties() throws Exception {
		Trace.println(1, "Config.readProperties()");

		// den richtigen Pfad setzen
		String path = AbwesendMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		if (path.endsWith(".jar")) {
			sPath = path.substring(0, path.lastIndexOf("/"));
		} else {
			sPath = path.substring(0, path.lastIndexOf("/"));
		}

		if (sConfigFile == null) {
			checkConfigFile(sConfigFileName);
		}
		Trace.println(2, "lese von File: '" + sConfigFile + "'");
		FileInputStream inputStream = new FileInputStream(sConfigFile);
		mProperties = new Properties();
		mProperties.load(inputStream);
		inputStream.close();

		// --- temporärer Tracelevel setzen
		int temp = traceLevel;
		temp = getIntProperty(traceLevelKey);
		if (temp > 0) {
			traceLevel = temp;
		}
		mProperties.setProperty(traceLevelKey, Integer.toString(traceLevel));
		Trace.println(1, "trace.level = " + traceLevel);

		// --- überprüfen ob bereits gesetzt, sonst neu in die Property schreiben
		if (mProperties.getProperty(dbNameKey) == null) {
			mProperties.setProperty(dbNameKey, dbName);
		} else {
			dbName = mProperties.getProperty(dbNameKey);
		}
		if (mProperties.getProperty(dbUrlKey) == null) {
			mProperties.setProperty(dbUrlKey, dbUrl);
		} else {
			dbUrl = mProperties.getProperty(dbUrlKey);
		}
		if (mProperties.getProperty(dbPortKey) == null) {
			mProperties.setProperty(dbPortKey, dbPort);
		} else {
			dbPort = mProperties.getProperty(dbPortKey);
		}

		if (mProperties.getProperty(dbUserKey) == null) {
			mProperties.setProperty(dbUserKey, dbUser);
		} else {
			dbUser = mProperties.getProperty(dbUserKey);
		}
		if (mProperties.getProperty(showTableauBoxKey) == null) {
			mProperties.setProperty(showTableauBoxKey, Integer.toString(showTableauBox));
		} else {
			try {
				showTableauBox = Integer.parseInt(mProperties.getProperty(showTableauBoxKey));
			} catch (NumberFormatException ex) {
				// nichts tun default wie gesetzt
			}
		}

		if (mProperties.getProperty(spielerExportDirKey) != null) {
			spielerExportDir = mProperties.getProperty(spielerExportDirKey);
		}
		if (mProperties.getProperty(spielerExportFileKey) != null) {
			spielerExportFile = mProperties.getProperty(spielerExportFileKey);
		}

		if (mProperties.getProperty(spielerImportDirKey) != null) {
			spielerImportDir = mProperties.getProperty(spielerImportDirKey);
		}
		if (mProperties.getProperty(spielerImportFileKey) != null) {
			spielerImportFile = mProperties.getProperty(spielerImportFileKey);
		}
		if (mProperties.getProperty(spielerImportSplitCharKey) != null) {
			spielerImportSplitChar = mProperties.getProperty(spielerImportSplitCharKey);
		}

		if (mProperties.getProperty(spielerColKonkurrenzKey) != null) {
			spielerColKonkurrenz = mProperties.getProperty(spielerColKonkurrenzKey);
		}
		if (mProperties.getProperty(spielerColName1Key) != null) {
			spielerColName1 = mProperties.getProperty(spielerColName1Key);
		}
		if (mProperties.getProperty(spielerColVorname1Key) != null) {
			spielerColVorname1 = mProperties.getProperty(spielerColVorname1Key);
		}
		if (mProperties.getProperty(spielerColName2Key) != null) {
			spielerColName2 = mProperties.getProperty(spielerColName2Key);
		}
		if (mProperties.getProperty(spielerColVorname2Key) != null) {
			spielerColVorname2 = mProperties.getProperty(spielerColVorname2Key);
		}

		if (mProperties.getProperty(emailImportFileKey) != null) {
			emailImportFile = mProperties.getProperty(emailImportFileKey);
		}

		if (mProperties.getProperty(planDirKey) != null) {
			planDir = mProperties.getProperty(planDirKey);
		}
		if (mProperties.getProperty(planFileKey) != null) {
			planFile = mProperties.getProperty(planFileKey);
		}
		if (mProperties.getProperty(planTrennCharKey) != null) {
			planTrennChar = mProperties.getProperty(planTrennCharKey);
		}
		// die Integer-Werte setzem
		planRowStart = Integer.parseInt(mProperties.getProperty(planRowStartKey, Integer.toString(planRowStart)));
		planColDatumZeit = Integer
				.parseInt(mProperties.getProperty(planColDatumZeitKey, Integer.toString(planColDatumZeit)));
		planColDatum = Integer.parseInt(mProperties.getProperty(planColDatumKey, Integer.toString(planColDatum)));
		planColZeit = Integer.parseInt(mProperties.getProperty(planColZeitKey, Integer.toString(planColZeit)));
		planColName1 = Integer.parseInt(mProperties.getProperty(planColName1Key, Integer.toString(planColName1)));
		planColName2 = Integer.parseInt(mProperties.getProperty(planColName2Key, Integer.toString(planColName2)));

		webDriver = mProperties.getProperty(webDriverKey, webDriver);
		webDriverFile = mProperties.getProperty(webDriverFileKey, webDriverFile);

		if (mProperties.getProperty(emailUser) == null) {
			mProperties.setProperty(emailUser, "cm@tcallschwil.ch");
		}
		if (mProperties.getProperty(emailPassword) == null) {
			mProperties.setProperty(emailPassword, "xxx");
		}
		if (mProperties.getProperty(emailHostImap) == null) {
			mProperties.setProperty(emailHostImap, "imap.mail.hostpoint.ch");
		}
		if (mProperties.getProperty(emailHostSmtp) == null) {
			mProperties.setProperty(emailHostSmtp, "asmtp.mailstation.ch");
		}
		if (mProperties.getProperty(emailSmtpPort) == null) {
			mProperties.setProperty(emailSmtpPort, "25");
		}

		// die double Variablen setzen
		windowWidth = Double.parseDouble(mProperties.getProperty(windowWidthKey, Double.toString(windowWidth)));
		windowHeight = Double.parseDouble(mProperties.getProperty(windowHeigthKey, Double.toString(windowHeight)));
		windowX = Double.parseDouble(mProperties.getProperty(windowXKey, Double.toString(windowX)));
		windowY = Double.parseDouble(mProperties.getProperty(windowYKey, Double.toString(windowY)));
	}

	/**
	 * Die Werte für das Anzeigedatum setzen. Kann erst ausgeführt werden, wenn
	 * Config auch eingelesen
	 * 
	 * @throws Exception
	 */
	private static void showDatumSetzen() throws Exception {
		// spezielle formate, statdatum,
		showBeginDatum = sdfDatum.parse(mProperties.getProperty(showBeginDatumKey, sdfDatum.format(turnierBeginDatum)));
		showEndDatum = sdfDatum.parse(mProperties.getProperty(showEndDatumKey, sdfDatum.format(turnierBeginDatum)));
		if (showBeginDatum.getTime() < turnierBeginDatum.getTime()
				|| showBeginDatum.getTime() > turnierEndDatum.getTime()) {
			showBeginDatum = turnierBeginDatum;
		}
		if (showEndDatum.getTime() < turnierBeginDatum.getTime()
				|| showEndDatum.getTime() > turnierEndDatum.getTime()) {
			showEndDatum = turnierEndDatum;
		}
		showNumberBerechnen();
	}

	/**
	 * Die Positionen im Array für die Anzeite berechnen, abhängig von Start- und
	 * EndDatum.
	 */
	public static void showNumberBerechnen() {
		long diffDate = showBeginDatum.getTime() - turnierBeginDatum.getTime();
		long diffTage = diffDate / einTagLong;
		showBeginNumber = 0;
		if (diffTage > 0) {
			showBeginNumber = Math.toIntExact(diffTage);
		}
		diffDate = showEndDatum.getTime() - showBeginDatum.getTime();
		diffTage = diffDate / einTagLong;
		if (diffTage >= 0) {
			showEndNumber = Math.toIntExact(showBeginNumber + diffTage) + 1;
		}
		if (showEndNumber > turnierMaxTage) {
			showEndNumber = turnierMaxTage;
		}
	}

	/**
	 * Die Werte aus dem String in den Zeiten-Array schreiben
	 */
	private static void setZeitenProTag() {
		// zuerst initialisieren mit Standard-Werten
		zeitStart = new int[turnierMaxTage];
		for (int i = 0; i < zeitStart.length; i++) {
			zeitStart[i] = (int) weekBegin;
		}

		// übertragen der Werte aus DB
		String[] split = zeitStartStr.split(zeitTrennChar);
		int i = 0;
		for (String zeit : split) {
			zeitStart[i] = Integer.valueOf(zeit);
			i++;
		}

		// zuerst initialisieren mit Standard-Werten
		zeitEnde = new int[turnierMaxTage];
		for (i = 0; i < zeitEnde.length; i++) {
			zeitEnde[i] = (int) weekEnd;
		}

		split = zeitEndeStr.split(zeitTrennChar);
		i = 0;
		for (String zeit : split) {
			zeitEnde[i] = Integer.valueOf(zeit);
			i++;
		}
	}

	/** Alle Properites in das File schreiben */
	public static void saveProperties() throws Exception {
		Trace.println(1, "Config.saveProperties()");
		writeProperties();
		sortPorperties();
		try {
			PrintWriter outputStream = new PrintWriter(sConfigFile);
			outputStream.println("# TCA Config-Einstellungen");
			outputStream.println("# " + Config.sdfDb.format(new Date()));
			Iterator<Object> iterProp = mPropertyList.iterator();
			String key = new String();
			while (iterProp.hasNext()) {
				key = (String) iterProp.next();
				outputStream.print(key);
				outputStream.print("=");
				outputStream.println(mProperties.getProperty(key));
			}
			outputStream.flush();
			outputStream.close();
		} catch (IOException ex) {
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * Die Wert, die vom Programm geändert wurden, wieder in die Properties
	 * schreiben, dann weden diese gespeichert.
	 */
	private static void writeProperties() {
		mProperties.setProperty(dbUrlKey, dbUrl);
		mProperties.setProperty(dbPortKey, dbPort);
		mProperties.setProperty(dbUserKey, dbUser);
		mProperties.setProperty(showTableauBoxKey, Integer.toString(showTableauBox));
		mProperties.setProperty(showBeginDatumKey, sdfDatum.format(showBeginDatum));
		mProperties.setProperty(showEndDatumKey, sdfDatum.format(showEndDatum));
		mProperties.setProperty(windowHeigthKey, Double.toString(windowHeight));
		mProperties.setProperty(windowWidthKey, Double.toString(windowWidth));
		mProperties.setProperty(windowXKey, Double.toString(windowX));
		mProperties.setProperty(windowYKey, Double.toString(windowY));
		mProperties.setProperty(webDriverKey, webDriver);
		mProperties.setProperty(webDriverFileKey, webDriverFile);
		mProperties.setProperty(spielerImportDirKey, spielerImportDir);
		mProperties.setProperty(spielerImportFileKey, spielerImportFile);
		mProperties.setProperty(spielerColKonkurrenzKey, spielerColKonkurrenz);
		mProperties.setProperty(spielerColName1Key, spielerColName1);
		mProperties.setProperty(spielerColVorname1Key, spielerColVorname1);
		mProperties.setProperty(spielerColName2Key, spielerColName2);
		mProperties.setProperty(spielerColVorname2Key, spielerColVorname2);
		mProperties.setProperty(spielerExportDirKey, spielerExportDir);
		mProperties.setProperty(spielerExportFileKey, spielerExportFile);
		mProperties.setProperty(emailImportFileKey, emailImportFile);
		mProperties.setProperty(planRowStartKey, Integer.toString(planRowStart));
		mProperties.setProperty(planColDatumZeitKey, Integer.toString(planColDatumZeit));
		mProperties.setProperty(planColDatumKey, Integer.toString(planColDatum));
		mProperties.setProperty(planColZeitKey, Integer.toString(planColZeit));
		mProperties.setProperty(planColName1Key, Integer.toString(planColName1));
		mProperties.setProperty(planColName2Key, Integer.toString(planColName2));
		mProperties.setProperty(planTrennCharKey, planTrennChar);
		mProperties.setProperty(planDirKey, planDir);
		mProperties.setProperty(planFileKey, planFile);
		mProperties.setProperty(planTrennCharKey, planTrennChar);

	}

	/** Einen int-Werte von den Properties lesen */
	private static int getIntProperty(String property) throws Exception {
		try {
			String value = mProperties.getProperty(property);
			if (value == null) {
				return -1;
			}
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new Exception("Property: '" + property + "' falsch \n" + "Fehler: " + ex.getMessage());
		}
	}

	/**
	 * Alle keys der Properites spielerieren
	 */
	private static void sortPorperties() {
		Enumeration<Object> properityKeys = mProperties.keys();
		mPropertyList = new Vector<>();
		while (properityKeys.hasMoreElements()) {
			mPropertyList.add(properityKeys.nextElement());
		}

		Collections.sort(mPropertyList, new Comparator<>() {

			@Override
			public int compare(Object o1, Object o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

	}

	// ------- getter und setter -----------------------
//	public static String getDbName() {
//		return dbName;
//	}

	public static Map<String, String> getKeyValueMap() {
		return keyValueMap;
	}

	public static void setJavaVersion(String version) {
		if (version.length() > 3) {
			String v1 = version.substring(0, 3);
			String v2 = v1.substring(v1.indexOf(".") + 1, v1.length());
			try {
				javaVersion = Integer.parseInt(v2);
			} catch (Exception ex) {
			}
		}
	}

}
