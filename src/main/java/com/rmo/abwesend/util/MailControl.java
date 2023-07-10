package com.rmo.abwesend.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Zur Kontrolle der mails.
 */
public class MailControl {
	/** Stringbuffer für Message */
	private static StringBuffer sBuffer = new StringBuffer(255);
	/** Das File in das geschrieben wird */
	private static FileWriter file = null;
	private static BufferedWriter writer = null;

	/**
	 * MailControl constructor comment.
	 */
	public MailControl() {
		super();
	}

	/**
	 * Ausgabe, falls dieser Level gedruckt werden soll.
	 *
	 * @param level   int level of Trace
	 * @param message java.lang.String
	 */
	public static void println(int level, String message) {
		if (file == null) {
			makeWriters();
		}
		try {
			for (int i = 0; i < level; i++) {
				writer.write("  ");
			}
			if (sBuffer.length() > 0) {
				writer.write(sBuffer.toString());
				sBuffer.delete(0, sBuffer.length());
			}
			if (message == null) {
				message = "NULL";
			}
			writer.write(message);
			writer.newLine();
			writer.flush();
		} catch (IOException ex) {
			Trace.println(3, "Trace.makeFile: " + ex.getMessage());
		}
	}

	/** Initialisiert alle Writers
	 * */
	private static void makeWriters() {
		try {
			StringBuffer fileName = new StringBuffer(Config.sMailControl);
			Date date = new Date();
			fileName.append(Config.sdfDatum.format(date));
			fileName.append(".txt");
			Config.sMailControlPath = Config.sPath + "/" + fileName;

			file = new FileWriter(Config.sMailControlPath);
			writer = new BufferedWriter(file);
		} catch (IOException ex) {
			Trace.println(0, "MailControl.makeFile, Fehler: " + ex.getMessage());
		}
	}

	public static void closeFile() {
		if (file != null) {
			try {
				file.close();
			} catch (IOException ex) {
				Trace.println(0, "MailControl.closeFile(), Fehler: " + ex.getMessage());
			}

		}
	}
}
