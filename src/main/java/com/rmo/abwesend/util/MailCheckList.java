package com.rmo.abwesend.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Die mails generieren und in ein File schreiben, Einstieg mit mailGenerate.
 * Wenn gestartet werden die Daten zuerst eingelesen.
 *
 * @author ruedi
 */
public class MailCheckList {

	private List<Integer> mailCheckListIn = null;
	private String fileName;
	private FileReader fileReader = null;
	private BufferedReader reader = null;
	private boolean inputStreamOk = false;


	private List<Integer> mailCheckListOut = null;
	private FileWriter fileWriter = null;
	private BufferedWriter writer = null;

	/**
	 * Kontrolle, ob bereits ein mail in dieser Kategorie (fileName)
	 * gesendet wurde
	 * @param fileName
	 */
	public MailCheckList(String fileName) {
		this.fileName = fileName;
		openInputFile(fileName);
		if (inputStreamOk) {
			readList();
		}
		mailCheckListOut = new ArrayList<>();
		if (inputStreamOk) {
			mailCheckListOut.addAll(mailCheckListIn);
		}
	}

	public int openInputFile(String fileName) {
		try {
			fileReader = new FileReader(fileName);
			reader = new BufferedReader(fileReader);
		} catch (Exception ex) {
			Trace.println(3, "File nicht gefunden: " + fileName);
			inputStreamOk = false;
			return -1;
		}
		inputStreamOk = true;
		return 0;
	}

	/**
	 * Einlesen der Liste vom file
	 */
	public void readList() {
		if (inputStreamOk) {
			mailCheckListIn = new ArrayList<>();
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					mailCheckListIn.add(Integer.valueOf(line));
				}
			}
			catch (Exception ex) {
				Trace.println(3, "MailCheckList.readList() : " + ex.getMessage());
			}
		}
	}

	/**
	 *
	 * @param spielerId
	 * @param datum
	 * @return true wenn der Spieler noch nicht in der Liste
	 */
	public boolean canSend(int spielerId) {
		if (inputStreamOk) {
			for (Integer element : mailCheckListIn) {
				if (element == spielerId) {
					return false;
				}
			}
		}
		// wenn nicht gefunden in der Liste
		return true;
	}

	/**
	 * Wenn ein mail gesendet, das Datum sichern.
	 * @param spielerId
	 * @param datum
	 */
	public void addMailSent(int spielerId) {
		mailCheckListOut.add(spielerId);
	}


	/**
	 * Die Infos wieder in das File schreiben
	 * @return true wenn alles ok, false wenn Probleme
	 */
	public boolean save() {
		if (makeWriter()) {
			try {
				for (Integer element : mailCheckListOut) {
					fileWriter.write(element.toString() + System.lineSeparator());
				}
				fileWriter.flush();
			} catch (IOException ex) {
				Trace.println(1, "MailCheckList.writeFile(): " + ex.getMessage());
				closeFile();
				return false;
			}
			closeFile();
			return true;
		}
		return false;
	}


	/**
	 * Initialisiert alle Writers um in das File zu schreiben
	 */
	private boolean makeWriter() {
		try {
			// zuerst löschen
			if (inputStreamOk) {
				File file = new File(fileName);
				if (file.exists()) {
					file.delete();
				}
			}
			fileWriter = new FileWriter(fileName);
			writer = new BufferedWriter(fileWriter);
			Trace.println(3, "MailCheckList, schreibe in: " + fileName);
		} catch (IOException ex) {
			Trace.println(1, "MailCheckList.makeFile: " + ex.getMessage());
			return false;
		}
		return true;
	}


	public void closeFile() {
		try {
			reader.close();
		}
		catch (IOException ex) {
			// nix
		}
		try {
			writer.close();
		}
		catch (IOException ex) {
			// nix
		}
	}



}
