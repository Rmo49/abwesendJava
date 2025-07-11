package com.rmo.abwesend.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Trace-Output for debugging. Level 1: Struktur-Methoden Level 2: Wichtige
 * Methoden von Model, View (Ablauf) Level 3: allg. Methoden, Variable
 * (detailierter Ablauf) Level 4: Kontroll-Output (mit Einstellungen) Level 5:
 * viel aufgerufenen Methoden der View-Schicht Level 7: viel aufgerufene
 * Methoden der Datenschicht mit Vars.
 */
public class Trace {
	/** Stringbuffer für Message */
	private static StringBuffer sBuffer = new StringBuffer(255);
	/** Das File in das geschrieben wird */
	private static  File file = null;
	private static FileWriter fileWriter = null;
	private static BufferedWriter writer = null;
	private static PrintWriter printWriter = null;

	/**
	 * Trace constructor comment.
	 */
	public Trace() {
		super();
	}

	public static String getTracePath() {
		if (fileWriter == null) {
			return "noch nicht gesetzt";
		}
		else {
			return file.getPath();
		}
	} 
	
	/**
	 * Ausgabe, falls dieser Level gedruckt werden soll.
	 * 
	 * @param level   int
	 * @param message java.lang.String
	 */
	public static void print(int level, String message) {
		if (level <= Config.traceLevel) {
			sBuffer.append(message);
			sBuffer.append(' ');
		}
	}

	/**
	 * Ausgabe, falls dieser Level gedruckt werden soll.
	 * 
	 * @param level   int level of Trace
	 * @param message java.lang.String
	 */
	public static void println(int level, String message) {
		if (level <= Config.traceLevel) {
			if (fileWriter == null) {
				makeWriters();
			}
			try {
				if (Config.traceTimestamp) {
					// writer.write(new Date().getTime() + " ");
					writer.write(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()) + " ");
				}
				for (int i = 1; i < level; i++) {
					writer.write("  ");
				}
				if (sBuffer.length() > 0) {
					writer.write(sBuffer.toString());
					sBuffer.delete(0, sBuffer.length());
				}
				writer.write(message);
				writer.newLine();
				writer.flush();
			} catch (IOException ex) {
				System.out.println("Trace.makeFile: " + ex.getMessage());
			}
		}
	}

	public static PrintWriter getPrintWriter() {
		if (fileWriter == null) {
			makeWriters();
		}
		return printWriter;
	}

	public static void flush() {
		try {
			writer.flush();
			fileWriter.flush();
		} catch (IOException ex) {
			System.out.println("Trace.flush: " + ex.getMessage());
		}

	}

	/** Initialisiert alle Writers */
	private static void makeWriters() {
		try {
			file = new File(Config.sTraceFileName);
			fileWriter = new FileWriter(file);
			writer = new BufferedWriter(fileWriter);
			printWriter = new PrintWriter(writer);
		} catch (IOException ex) {
			System.out.println("Trace.makeFile: " + ex.getMessage());
		}
	}

}
