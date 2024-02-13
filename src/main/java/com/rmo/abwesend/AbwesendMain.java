package com.rmo.abwesend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.rmo.abwesend.model.DbConnection;
import com.rmo.abwesend.model.TennisDataBase;
import com.rmo.abwesend.model.TraceDbData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.DbPasswordFile;
import com.rmo.abwesend.util.Trace;
import com.rmo.abwesend.view.MainFrame;
import com.rmo.abwesend.view.util.CmUtil;

/**
 * Die Hauptview der Applikaiton. Initialisiert das Menu und stellt die
 * basis-pane (BoderPane) zur Verfügung.
 *
 * @author Ruedi
 *
 */
public class AbwesendMain {

	private final static String version = "TCA CM abwesend, (V8.6)";
//	private static JOptionPane startFrame;

	public AbwesendMain() {

		JFrame mainFrame = null;

		// die Einstellungen vom File lesen (enthält Adresse von DB)
		if (!readProperties()) {
			JOptionPane.showMessageDialog(null, "kann Config nicht lesen, ist die Verbindung mit dem Internet ok?",
					"Config lesen", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (! checkDbPasswordFile()) {
			CmUtil.alertWarning("Passwort Datenbank", "Passwort Datei für die Datenbank ist nicht vorhanden,\n"
					+ "Muss zuerst angelegt werden, siehe auch Trace.txt");
			return;
		}

		// prüfen, ob DB vorhanden, dann normal weiter
		if (TennisDataBase.dbExists()) {
			readConfiguration();
		} else {
			CmUtil.alertError("Database check", TennisDataBase.getDbError());
		}

		mainFrame = new MainFrame(version);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		/**
		 * To handle the close event we just need to implement the windowClosing()
		 * method.
		 */
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Trace.println(0, "mainFrame closing event, ende <<<");
				writeConfiguration();
				writeTraceInfo();
			}
		});

		mainFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				Dimension dim = componentEvent.getComponent().getSize();
				Config.windowHeight = dim.getHeight();
				Config.windowWidth = dim.getWidth();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				Point p = e.getComponent().getLocation();
				Config.windowX = p.getX();
				Config.windowY = p.getY();
			}
		});
		mainFrame.setVisible(true);
	}


	/**
	 * Alle Configuration daten lesen
	 *
	 * @return
	 */
	private static boolean readProperties() {
		// read used Java Version
		Trace.println(1, "System.java.version: " + System.getProperty("java.version"));
		Trace.println(1, "Runtime.classe.java.version: " + Runtime.class.getPackage().getImplementationVersion());
		Config.setJavaVersion(System.getProperty("java.version"));
		try {
			Config.readProperties();
		} catch (Exception ex) {
			CmUtil.alertError("Kann Configurations-Datei nicht lesen.", ex.getMessage());
			ex.printStackTrace();
			return false;
		}
		Trace.println(1, "Pfad zu Files: " + Config.sPath);
		return true;
	}

	/**
	 * Configuration von der DB lesen
	 *
	 * @return
	 */
	private static boolean readConfiguration() {
		// DB-File vorhanden

		try {
			// Config von DB einlesen
			Config.readConfigData();
		} catch (Exception ex) {
			CmUtil.alertError("Probleme beim lesen der Config von der Datenbank", ex);
			return false;
		}
		if (Config.turnierMaxTage > Config.maxTageErlaubt) {
			CmUtil.alertError("Bitte Setup > Config überprüfen",
					"Turnier Begin- oder End-Datum falsch\n" + "Setup > Config data anpassen");
			return true;
		}
		return true;

	}

	/**
	 * prüfen, ob Passwort-File vorhanden ist, wenn ein  Passwort zurückgegeben wird.
	 *
	 * @return
	 */
	private static boolean checkDbPasswordFile() {
		DbPasswordFile dbPwFile = new DbPasswordFile(Config.sDbPwFileName);
		if (dbPwFile.getDbPassword() == null) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Testen, ob Zugriff auf DB
	 *
	 * @return
	 */
//	private static boolean checkDbConnection() {
//		try {
//			// test Connection zuerst
//			DbConnection.getConnection();
//		} catch (SQLException ex) {
//			if (ex.getMessage().contains("Access denied")) {
//				CmUtil.alertError(
//						"Verbindung zur Datenbank kann nicht hergestellt werden," + " wahrscheinlich falsches Passwort",
//						ex);
//				return false;
//			}
//			CmUtil.alertError("Verbindung zur Datenbank kann nicht hergestellt werden,\n"
//					+ "Ist eine Internetverbindung vorhanden?", ex);
//			return false;
//		}
//		return true;
//	}

	/**
	 * Trace schreiben, wer sich eingelogged hat.
	 */
	private void writeTraceInfo() {
		// nur wenn verbunden, sonst nicht schreiben, da keine DB offen.
		if (DbConnection.isConnected()) {
			StringBuffer env = new StringBuffer(80);
			env.append("COMPUTERNAME: ");
			env.append(System.getenv("COMPUTERNAME"));
			env.append("  USERNAME: ");
			env.append(System.getenv("USERNAME"));
			try {
				TraceDbData.instance().add(env.toString());
			} catch (Exception ex) {
				Trace.println(3, "Kann nicht in TraceDb schreiben");
			}
		}
	}

	/**
	 * Configruations in properties und DB schreiben
	 */
	private static void writeConfiguration() {
		try {
			Config.saveConfigData();
		} catch (Exception ex) {
			Trace.println(1, ex.getMessage());
		}
		try {
			Config.saveProperties();
		} catch (Exception ex) {
			Trace.println(1, ex.getMessage());
		}
	}

	/**
	 * Hier wird das program gestartet.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Trace.println(0, "AbwesendMain.main() start: " + Config.sdfDb.format(new Date()));
		try {
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//			UIManager.put("nimbusBase", new Color(140,140,140));
//			UIManager.put("nimbusBlueGrey", new Color(47,92,180));
//			UIManager.put("control", new Color(176,179,50));
			UIDefaults defaults = UIManager.getLookAndFeelDefaults();
			defaults.put("Table.alternateRowColor", new Color(220, 220, 220));
			defaults.put("Table.selectionBackground", Color.YELLOW);
			defaults.put("ComboBox.selectionBackground", Color.YELLOW);
//			defaults.put("Panel.background", Color.YELLOW);
			// Applikation starten
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new AbwesendMain();
				}
			});
		} catch (Exception ex) {
			CmUtil.alertError("Probleme beim Start.", ex);
		}
	}
}
