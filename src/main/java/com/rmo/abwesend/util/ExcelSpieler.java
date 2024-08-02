package com.rmo.abwesend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;

/**
 * Das Excel-File in dem die Daten der Spieler gespeichert sind. zuerst wird
 * openFile aufgerufen, wenn file gefunden über iterator readLine, hier werden
 * alle Daten verarbeitet. was vorher gesetzt werden muss: die Zeile mit der
 * Bezeichnung der Spalten (Tableau, Name und )
 * 
 * @author Ruedi
 *
 */
public class ExcelSpieler {

	private boolean fehler = false;
	private StringBuffer fehlerStr;

	private File inputFile;
	private FileInputStream inputStream;
	private String inputFileName;
	private Workbook workbook;
	private Sheet sheet0;

	private int colKonkurrenz = -1;
	private int colName1 = -1;
	private int colVorname1 = -1;
	private int colName2 = -1;
	private int colVorname2 = -1;
	private boolean colAllSet = true;
	private boolean indexRowFound = false;

	private String mKonkurrenz = Config.spielerColKonkurrenz;
	private String mName1 = Config.spielerColName1;
	private String mVorname1 = Config.spielerColVorname1;
	private String mName2 = Config.spielerColName2;
	private String mVorname2 = Config.spielerColVorname2;

	private List<Tableau> konkurrenzList;

	public ExcelSpieler() {
	}

	/**
	 * Das File mit den Spieler-Daten öffnen return > 0 wenn ok, < 0 wenn Fehler
	 */
	public int openFile(String fileName, JTextArea message) {
		// initialisiert die Fehlermeldungen
		fehlerStr = new StringBuffer();

		inputFileName = Config.get(Config.spielerImportDirKey) + "/" + fileName;
		try {
			inputFile = new File(inputFileName);
			inputStream = new FileInputStream(inputFile);
		} catch (Exception ex) {
			Trace.println(1, "Probleme File lesen \n" + inputFileName);
			message.setText("Kann File nicht lesen \n" + inputFileName + "\n");
			return -1;
		}

		try {
			workbook = new HSSFWorkbook(inputStream);
		} catch (IOException ex) {
			message.setText("Probleme mit Workbook");
			return -1;
		}
		sheet0 = workbook.getSheetAt(0);
		// alle daten zuerst löschen

		try {
			tableauEinlesen();
		} catch (Exception ex) {
			message.setText("Tableau einlesen " + ex.getMessage());
			return -1;
		}
		return 1;
	}

	/**
	 * Das File vom System löschen
	 * 
	 * @param message
	 * @return
	 */
	public int deleteFile(JTextArea message) {
		try {
			inputFile.delete();
			message.setText("File gelöscht \n" + inputFileName);
		} catch (Exception ex) {
			Trace.println(1, "Probleme File löschen " + inputFileName);
			message.setText("Kann File nicht löschen " + inputFileName);
			return -1;
		}
		return 1;
	}

	/**
	 * Der Iterator über alle Zeilen
	 * 
	 * @return
	 */
	public Iterator<Row> getIterator() {
		return sheet0.iterator();
	}

	/**
	 * Die maximale Anzahl von Zeilen.
	 * 
	 * @return
	 */
	public int getLastRowNr() {
		return sheet0.getLastRowNum();
	}

	/**
	 * Damit nicht immer auf Tabeleau zugegriffen werden muss Die Tableau werden
	 * ohne Space abgespeichert
	 */
	private void tableauEinlesen() throws Exception {
		List<Tableau> tableauList = TableauData.instance().readAllTableau();
		konkurrenzList = new ArrayList<>();
		Iterator<Tableau> iter = tableauList.iterator();
		Tableau tableau;
		while (iter.hasNext()) {
			tableau = iter.next();
			konkurrenzList.add(tableau);
		}
	}

	/**
	 * Die Zeile mit dem Index suchen
	 * 
	 * @param lRow
	 */
	private void getIndexRow(Row lRow) {
		Cell cell = lRow.getCell(0);
		if (cell.getStringCellValue().compareToIgnoreCase(mKonkurrenz) == 0) {
			// Zeile mit Index gefunden
			Trace.println(4, "ExcelSpieler Indexzeile gefunden");
			indexRowFound = true;
			setColNr(lRow);
		}
	}

	/**
	 * Die Kolonnen-Nummer setzen TODO: wenn nicht gefunden, dann Fehlermeldung
	 * 
	 * @param lRow
	 */
	private void setColNr(Row lRow) {
		Iterator<Cell> cellIterator = lRow.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (cell.getStringCellValue().compareToIgnoreCase(mKonkurrenz) == 0) {
				colKonkurrenz = cell.getColumnIndex();
			} else if (cell.getStringCellValue().compareToIgnoreCase(mName1) == 0) {
				colName1 = cell.getColumnIndex();
			} else if (cell.getStringCellValue().compareToIgnoreCase(mVorname1) == 0) {
				colVorname1 = cell.getColumnIndex();
			} else if (cell.getStringCellValue().compareToIgnoreCase(mName2) == 0) {
				colName2 = cell.getColumnIndex();
			} else if (cell.getStringCellValue().compareToIgnoreCase(mVorname2) == 0) {
				colVorname2 = cell.getColumnIndex();
			}
		}
		checkColSet();
	}

	/**
	 * Überprüft, ob alle cols gesetzt sind.
	 */
	private void checkColSet() {
		if (colKonkurrenz < 0) {
			addFehlerStr(mKonkurrenz);
		}
		if (colName1 < 0) {
			addFehlerStr(mName1);
		}
		if (colName1 < 0) {
			addFehlerStr(mName1);
		}
		if (colVorname1 < 0) {
			addFehlerStr(mVorname1);
		}
		if (colName2 < 0) {
			addFehlerStr(mName2);
		}
		if (colVorname2 < 0) {
			addFehlerStr(mVorname2);
		}
	}

	/**
	 * Den Fehlerstring zusammenstellen
	 * 
	 * @param indexStr
	 */
	private void addFehlerStr(String indexStr) {
		fehlerStr.append("Spalte: '");
		fehlerStr.append(indexStr);
		fehlerStr.append("' nicht gefunden \n");
		colAllSet = false;
	}

	/**
	 * Wird vom Prozess aufgerufen, einlesen einer Zeile.
	 */
	public void readLine(Row lRow) {
		// zuerst die Colnummern setzen
		if (colKonkurrenz < 0) {
			getIndexRow(lRow);
		} else {
			if (colAllSet) {
				readRowDetails(lRow);
			}
		}
	}

	/**
	 * Eine Zeile mit Spielerdaten vom excel file lesen
	 * 
	 * @param lRow
	 */
	private void readRowDetails(Row lRow) {
		try {
			int spielerId = addSpieler(lRow.getCell(colName1).getStringCellValue(),
					lRow.getCell(colVorname1).getStringCellValue());
			String konkurrenz = lRow.getCell(colKonkurrenz).getStringCellValue();
			addSpielerTableau(spielerId, konkurrenz);

			String name2 = lRow.getCell(colName2).getStringCellValue();
			if (!name2.isBlank()) {
				// wenn ein Doppelpartner vorhanden
				spielerId = addSpieler(name2, lRow.getCell(colVorname2).getStringCellValue());
				addSpielerTableau(spielerId, konkurrenz);
			}
		} catch (Exception ex) {
			Trace.println(3, "ExcelSpieler einlesen " + ex.toString());
			fehler = true;
		}
	}

	/**
	 * Einen Spieler dazufügen, falls noch nicht in der DB
	 * 
	 * @param spielerName
	 * @throws Exception
	 */
	private int addSpieler(String name, String vorname) throws Exception {
		Spieler spieler = new Spieler();
		spieler.setName(name.trim());
		spieler.setVorName(vorname.trim());
		Trace.println(4, "ExcelSpieler.addSpieler: " + spieler.getName() + " " + spieler.getVorName());
		return SpielerData.instance().add(spieler);
	}

	/**
	 * Die Beziehung Spieler / Tableau sichern.
	 * 
	 * @param spielerID
	 * @param konkurrenz
	 */
	private void addSpielerTableau(int spielerID, String konkurrenz) {
		int tableauId = getTableauId(konkurrenz);
		if (tableauId >= 0) {
			try {
				SpielerTableauData.instance().add(spielerID, tableauId);
			} catch (Exception ex) {
				Trace.println(3, "Fehler bei Tableau zuteilen: " + ex.toString());
				fehler = true;
			}
		}
	}

	/**
	 * Die Id bestimmen
	 * 
	 * @param konkSuche    nach der gesucht wird
	 * @param tableauList
	 * @param tableauName, wenn > 0 muss dieser Name in der TableauBezeichung
	 *                     vorhanden sein. @return, -1 wenn nicht gefunden, sonst
	 *                     die ID
	 */
	private int getTableauId(String konkSuche) {
		Trace.print(5, "ExcelSpieler.getTableauId: " + konkSuche);
		int id = -1;

		Iterator<Tableau> iter = konkurrenzList.iterator();
		Tableau tableau;
		while (iter.hasNext()) {
			tableau = iter.next();
			if (stringGleich(tableau.getKonkurrenz(), konkSuche)) {
				id = tableau.getId();
				break;
			}
		}
		if (id < 0) {
			Trace.println(5, " <== Tableau nicht gefunden");
		} else {
			Trace.println(5, "");
		}
		return id;
	}

	/**
	 * Vergleicht 2 Strings
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	private boolean stringGleich(String str1, String str2) {
		// Doppelte Leerzeichen eliminieren
		str1 = leerzeichenLoeschen(str1);
		str2 = leerzeichenLoeschen(str2);

		int lenMin = Math.min(str1.length(), str2.length());
		int lenMax = Math.max(str1.length(), str2.length());

		int i = 0;
		while (i < lenMin) {
			char c1 = str1.charAt(i);
			char c2 = str2.charAt(i);

			if (c1 != c2) {
				break;
			}
			i++;
		}

		if (i < lenMax - 2) {
			// nicht indetisch, wenn nicht alles verglichen
			return false;
		}
		return true;
	}

	/**
	 * Wenn mehrere Leerzeichen nacheinander, dann diese löschen.
	 * 
	 * @param str
	 * @return
	 */
	String leerzeichenLoeschen(String str) {
		StringBuffer str2 = new StringBuffer(36);
		int i = 0;
		int leer = 0;
		while (i < str.length()) {
			int ch = str.charAt(i);
			switch (ch) {
			case 32:
				if (i == leer + 1) {
					// nicht übernehme
				} else {
					str2.append(str.charAt(i));
				}
				leer = i;
				break;
			case 160:
				// spezielle Space von Unicode
				if (i == leer + 1) {
					// nichts machen
				} else {
					// beim ersten ein Leezeichen einfügen
					str2.append(" ");
				}
				leer = i;
				break;
			default:
				str2.append((char) ch);
			}
			i++;
		}
		return str2.toString();
	}

	/**
	 * Wird aufgerufen wenn die letzte Zeile verarbeitet ist.
	 */
	public String readEnd() {
		try {
			if (workbook != null) {
				workbook.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException ex) {
			// nix tun
		}

		if (!indexRowFound) {
			fehlerStr.append("Zeile mit Spaltenbezeichnungen nicht gefunden '");
			fehlerStr.append(mKonkurrenz);
			fehlerStr.append("' \n");
		}

		if (fehler) {
			fehlerStr.append("Fehler beim Einlesen, siehe Trace \n");
		}

		if (fehlerStr.isEmpty()) {
			return ("Alles gelesen, siehe auch Trace");
		}
		return fehlerStr.toString();
	}

}
