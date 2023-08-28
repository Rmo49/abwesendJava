package com.rmo.abwesend.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Ein File anlegen und für schreiben / lesen bereitstellen
 */
public class FileHandler {

	private String mFileName;
	/** Das File in das geschrieben wird */
	private FileWriter fileW = null;
	private BufferedWriter writer = null;
	private FileReader fileR = null;
	private BufferedReader reader = null;

	/**
	 * FileHandler mit diesem File initialisieren
	 */
	public FileHandler(String fileName) {
		super();
		mFileName = fileName;
	}

	public boolean exists() {
		File file = new File(mFileName);
		return file.exists();

	}

	/**
	 * Ausgabe, falls dieser Level gedruckt werden soll.
	 * @param message java.lang.String
	 */
	public void println(String message) {
		if (fileW == null) {
			makeWriters();
		}
		try {
			writer.write(message);
			writer.newLine();
			writer.flush();
		}
		catch (IOException ex) {
			System.out.println("Trace.makeFile: " + ex.getMessage());
		}
	}

	public void close() {
		try {
			if (fileW != null) {
				fileW.close();
			}
			if (fileR != null) {
				fileR.close();
			}
		}
		catch (IOException ex) {
			// nix tun
		}
	}

	/**
	 * Ausgabe, falls dieser Level gedruckt werden soll.
	 * @param message java.lang.String
	 */
	public void print(String message) {
		if (fileW == null) {
			makeWriters();
		}
		try {
			writer.write(message);
			writer.flush();
		}
		catch (IOException ex) {
			System.out.println("Trace.makeFile: " + ex.getMessage());
		}
	}

	/**
	 * Oeffnet das File und liest die nächste Zeile.
	 * Wenn keine File oder keine Zeile, dann wird NULL zurückgegeben.
	 * @return
	 */
	public String readLine() {
		if (fileW == null) {
			if (! makeReader()) {
				return null;
			}
		}
		try {
			return reader.readLine();
		}
		catch (IOException ex) {
			System.out.println("Trace.makeFile: " + ex.getMessage());
			return null;
		}

	}

	/**
	 * Ein Reader aufsetzen, wenn nicht gefunden wird false
	 * @return
	 */
	public boolean makeReader() {
		try {
			reader = new BufferedReader(new FileReader(mFileName));
		}
		catch (IOException ex) {
			Trace.println(2, "FileHandler.makeReader: " + ex.getMessage());
			return false;
		}
		return true;
	}


	/** Initialisiert Writer */
	private void makeWriters() {
		try {
			StringBuffer fileName = new StringBuffer(mFileName);

			fileW = new FileWriter(fileName.toString());
			writer = new BufferedWriter(fileW);
		}
		catch (IOException ex) {
			System.out.println("FileHandler.makeFile: " + ex.getMessage());
		}
	}

}
