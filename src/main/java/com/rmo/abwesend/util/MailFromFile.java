package com.rmo.abwesend.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Mails vom File lesen
 */
public class MailFromFile {
	/** Stringbuffer für Message */
	/** Das File von dem gelesen wird */
	private FileInputStream fstream = null;
	private BufferedReader br = null;
	/**
	 * MailControl constructor comment.
	 */
	public MailFromFile() {
		super();
		// falls noch aktiv dann löschen
		if (fstream != null) {
			fstream = null;
		}
	}

	/**
	 * Ausgabe, falls dieser Level gedruckt werden soll.
	 *
	 * @param level   int level of Trace
	 * @param message java.lang.String
	 */
	public String readLine() {
		if (fstream == null) {
			makeReader();
		}
		try {
			return br.readLine();
		} catch (IOException ex) {
			return "";
		}
	}

	/**
	 * Die Anzahl mails im File
	 * @return
	 */
	public int getNubmerOfMails() {
		if (fstream == null) {
			makeReader();
		}
		int total = 0;
		int mailZeilen = 0;
		boolean first = true;
		String line = "";
		while (line != null) {
			try {
				line = br.readLine();
			} catch (IOException ex) {
				break;
			}
			total++;
			if (first) {
				if (line.startsWith("---")) {
					mailZeilen = total;
					first = false;
				}
			}
		}
		double anzahl = total / mailZeilen;
		close();
		return (int) anzahl;
	}

	/** Initialisiert alle Writers
	 * */
	private void makeReader() {
		try {
			fstream = new FileInputStream(Config.sMailToSendPath);
			br = new BufferedReader(new InputStreamReader(fstream));
		} catch (IOException ex) {
			Trace.println(0, "MailControl.makeReader: " + ex.getMessage());
		}
	}

	/**
	 * Alles Schliessen
	 */
	public void close() {
		try {
			if (fstream != null) {
				fstream.close();
				fstream = null;
			}
		}
		catch (IOException ex) {
			// nichts
		}
	}
}
