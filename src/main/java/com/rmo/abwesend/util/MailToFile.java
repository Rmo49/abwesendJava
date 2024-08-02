package com.rmo.abwesend.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * File in das die Mails zuerst geschrieben werden.
 */
public class MailToFile {
	/** Stringbuffer für Message */
	private StringBuffer sBuffer = new StringBuffer(255);
	/** Das File in das geschrieben wird */
	private String filePath;
	private File file = null;
	private FileWriter fileWriter = null;
	private BufferedWriter writer = null;

	/**
	 * MailControl constructor comment.
	 */
	public MailToFile(String filePath) {
		super();
		this.filePath = filePath;
	}

	/**
	 * Ausgabe, falls dieser Level gedruckt werden soll.
	 * 
	 * @param level   int level of Trace
	 * @param message java.lang.String
	 */
	public void println(String message) {
		if (fileWriter == null) {
			makeWriter();
		}
		try {
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
			System.out.println("Trace.makeFile, Fehler: " + ex.getMessage());
		}
	}

	/** Initialisiert alle Writers */
	private void makeWriter() {
		try {
			// zuerst löschen
			file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}

			fileWriter = new FileWriter(filePath);
			writer = new BufferedWriter(fileWriter);
			Trace.println(1, "MailToSend: " + filePath);
		} catch (IOException ex) {
			System.out.println("MailControl.makeFile: " + ex.getMessage());
		}
	}

	/**
	 * Alles Schliessen
	 */
	public void close() {
		try {
			if (fileWriter != null) {
				fileWriter.close();
				fileWriter = null;
			}
		} catch (IOException ex) {
			// nichts
		}
	}

	/**
	 * Wird aufgerufen wenn die letzte Zeile verarbeitet ist.
	 */
	public String readEnd() {
		close();
		return ("\nAlles geschrieben. \n siehe: " + filePath);
	}

}
