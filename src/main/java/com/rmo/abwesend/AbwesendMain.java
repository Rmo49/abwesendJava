package com.rmo.abwesend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.rmo.abwesend.model.DbConnection;
import com.rmo.abwesend.model.TraceDbData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.FileHandler;
import com.rmo.abwesend.util.Trace;
import com.rmo.abwesend.view.MainFrame;
import com.rmo.abwesend.view.util.CmUtil;


/**
 * Die Hauptview der Applikaiton.
 * Initialisiert das Menu und stellt die basis-pane (BoderPane) zur Verfügung.
 * @author Ruedi
 *
 */
public class AbwesendMain {
	
	private final static String version = "TC Allschwil Abwesenheiten, Version 6.0";
//	private static JOptionPane startFrame;

	public AbwesendMain() {
		
		JFrame mainFrame = null;
		
		if (! readProperties()) {
			JOptionPane.showMessageDialog(null, "Config lesen", 
					"kann Config nicht lesen, ist die Verbindung mit dem Internet ok?", ImageObserver.ERROR);
			return;
		}

		readConfiguration();
		mainFrame  = new MainFrame(version);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** To handle the close event we just need to implement the windowClosing() method.
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
		});		
		mainFrame.setVisible(true);
	}
	
/*
	private static void showStart() {
		startFrame = new JOptionPane(version);
		startFrame.setLocation(100, 100);
		startFrame.setSize(500, 200);
		startFrame.setVisible(true);
	}
*/
	
	/**
	 * Alle Configuration daten lesen
	 * @return
	 */
	private static boolean readProperties() {
		// read used Java Version
		Trace.println(1, "System.java.version: " + System.getProperty("java.version"));
		Trace.println(1, "Runtime.classe.java.version: " + Runtime.class.getPackage().getImplementationVersion());
		Config.setJavaVersion(System.getProperty("java.version"));
		
		try {
			Config.readProperties();
			return true;
		}
		catch (Exception ex) {
			CmUtil.alertError("Kann Configurations-Datei nicht lesen.", ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
		
	private static boolean readConfiguration() {
		// DB-File voerhanden
		if (! checkDbPasswordFile()) {
			CmUtil.alertWarning("Passwort Datenbank",
					"Passwort Datei für die Datenbank ist nicht vorhanden,\n"
					+ "Muss zuerst generiert werden, oder von Administrator verlangen");
			return false;
		}
		try {
			// test Connection zuerst
			DbConnection.getConnection();
		} 
		catch (SQLException ex) {
			if (ex.getMessage().contains("Access denied")) {
				CmUtil.alertError("Verbindung zur Datenbank kann nicht hergestellt werden,"
						+ " wahrscheinlich falsches Passwort", ex);
				return false;
			}
			CmUtil.alertError("Verbindung zur Datenbank kann nicht hergestellt werden,\n"
					+ "Ist eine Internetverbindung vorhanden?", ex);
			return false;
		}
		try {
			Config.readConfigData();
		} 
		catch (Exception ex) {
			CmUtil.alertError("Probleme beim lesen der Config von der Datenbank", ex);
			return true;
		}
		if (Config.turnierMaxTage > Config.maxTageErlaubt) {
			CmUtil.alertError("Bitte Setup > Config überprüfen", "Turnier Begin- oder End-Datum falsch\n"
					+ "Setup > Config data anpassen");
			return true;
		}
		return true;
	}

	/**
	 * prüfen, ob Passwort-File voerhanden ist.
	 * @return
	 */
	private static boolean checkDbPasswordFile() {
		FileHandler fh = new FileHandler(Config.sDbPwFileName);
		return fh.exists();
	}
	
	/**
	 * Trace schreiben, wer sich eingelogged hat.
	 */
	private void writeTraceInfo() {
		// In Trace schreiben, wer sich eingelogged hat
		StringBuffer env = new StringBuffer(80);
		env.append("COMPUTERNAME: ");
		env.append(System.getenv("COMPUTERNAME"));
		env.append("  USERNAME: ");
		env.append(System.getenv("USERNAME"));
		try {
			TraceDbData.instance().add(env.toString());
		}
		catch (Exception ex) {
			Trace.println(3, "Kann nicht in TraceDb schreiben");
		}
	}

	/**
	 * Configruations in properties und DB schreiben
	 */
	private static void writeConfiguration() {
		
		try {
			Config.saveConfigData();
		}
		catch (Exception ex) {
			Trace.println(1, ex.getMessage());
		}
		try {
			Config.saveProperties();
		}
		catch (Exception ex) {
			Trace.println(1, ex.getMessage());
		}
	}
	
	/**
	 * Hier wird das program gestartet.
	 * @param args
	 */
	public static void main(String[] args) {
		Trace.println(0, "AbwesendMain.main() start: " + Config.sdfDb.format(new Date()) );
		try {
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//			UIManager.put("nimbusBase", new Color(140,140,140));
//			UIManager.put("nimbusBlueGrey", new Color(47,92,180));
//			UIManager.put("control", new Color(176,179,50));
			UIDefaults defaults = UIManager.getLookAndFeelDefaults();
			defaults.put("Table.alternateRowColor", new Color(220,220,220));
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
		}
		catch (Exception ex) {
			CmUtil.alertError("Probleme beim Start.", ex);
		}		
	}
}
