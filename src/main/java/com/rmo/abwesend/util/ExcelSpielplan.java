package com.rmo.abwesend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.rmo.abwesend.model.Match;
import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.view.SwissTennisLesen;

/**
 * Das Excel-File in dem die Daten vom Spielplan gespeichert sind.
 * @author Ruedi
 *
 */
public class ExcelSpielplan {

//	private SwissTennisLesen myParent;

	private File inputFile;
	private	FileInputStream inputStream;
	private String inputFileName;
	private Workbook workbook;
	private Sheet sheet0;

	private Date mDatumZeit;
	private Date mDatum;
	private Date mZeit;
	private String spielerName1;
	private String spielerName2;
	private StringBuffer fehler;

	public ExcelSpielplan() {
	}

	/**
	 * Das File mit den Match-Daten öffnen
	 */
	public int openFile(String fileName, JTextArea message) {
		// initialisiert die Fehlermeldungen
		fehler = new StringBuffer();

		inputFileName = Config.get(Config.planDirKey) + "/" + fileName;
		try {
		   inputFile = new File(inputFileName);
		   inputStream = new FileInputStream(inputFile);
	   }
	   catch (Exception ex) {
		   Trace.println(1, "Probleme File lesen \n" + inputFileName);
		   message.setText("Kann File nicht lesen \n"+ inputFileName);
		   return -1;
	   	}

		try {
			workbook = new HSSFWorkbook(inputStream);
		}
		catch (IOException ex) {
			message.setText("Probleme mit Workbook");
			return -1;
		}
		sheet0 = workbook.getSheetAt(0);
		// alle daten zuerst löschen

		return 1;
	}

	/**
	 * Das File vom System löschen
	 * @param message
	 * @return
	 */
	public int deleteFile(JTextArea message) {
		try {
			inputFile.delete();
			message.setText("File gelöscht \n" + inputFileName);
		}
		catch (Exception ex) {
		   Trace.println(1, "Probleme File löschen " + inputFileName);
		   message.setText("Kann File nicht löschen "+ inputFileName);
		   return -1;
	   	}
		return 1;
	}


	/**
	 * Lesen der Daten vom Exel-File. Startet einen neuen Thread.
	 * @param progress
	 * @throws Exception
	 */
	public void readExcelFile(JProgressBar progress, SwissTennisLesen parent) throws Exception {
		Trace.println(1, "Start readExcelFile()");
//		myParent = parent;

		fehler = new StringBuffer();
//	    progress.progressProperty().bind(readFileTask.progressProperty());

//	    readFileTask.messageProperty().addListener(new ChangeListener<String>() {
//            @Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                parent.getMessage().setText(newValue);
//            }
//		});

//	    final Thread thread = new Thread(readFileTask, "read-excel");
//	    thread.setDaemon(true);
//	    thread.start();
	}



	/**
	 * Der Iterator über alle Zeilen
	 * @return
	 */
	public Iterator<Row> getIterator() {
		return sheet0.iterator();
	}

	/**
	 * Die maximale Anzahl von Zeilen.
	 * @return
	 */
	public int getLastRowNr() {
		return sheet0.getLastRowNum();
	}

	/**
	 * Wird vom Prozess aufgerufen, einlesen einer Zeile.
	 */
	public void readLine(Row lRow) {
		// für Progress berechnen
		if (lRow.getRowNum() < Config.planRowStart) {
		}
		else {
			if (readRow(lRow)) {
				addMatches();
			}
			else {
				Trace.println(3, "Unglültiger Wert in Datei: " + inputFile.getName());
//				throw new Exception("Unglültiger Wert in Tabelle");
			}
		}
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
		}
		catch (IOException ex) {
			// nix tun
		}

		if (fehler == null) {
			fehler = new StringBuffer();
			fehler.append("Fehler readEnd");
		}
		if (fehler.length() > 1) {
			return("Spieler nicht gefunden \n" + fehler.toString());
		}
		else {
			return("\n Alles gelesen");
		}
    }


	/**
	 * Eine Zeile mit Spieldaten vom excel file lesen
	 * @param lRow
	 */
	private boolean readRow(Row lRow) {
        Iterator<Cell> cellIterator = lRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            // das Datum mit Zeit
            if (cell.getColumnIndex() == Config.planColDatumZeit) {
	            if (cell.getCellType() == CellType.NUMERIC) {
	            	mDatumZeit = cell.getDateCellValue();
	            }
	            else {
	            	addFehler("Fehler, Zeile: " + lRow.getRowNum() + " Spalte: " + Config.planColDatumZeit);
	            	return false;
	            }
            }
            // das Datum
            if (cell.getColumnIndex() == Config.planColDatum) {
	            if (cell.getCellType() == CellType.NUMERIC) {
	            	mDatum = cell.getDateCellValue();
	            	// falls Zeit nicht gesetzt
	            	mZeit = new Date(0);
	            }
	            else {
	            	addFehler("Fehler, Zeile: " + lRow.getRowNum() + " Spalte: " + Config.planColDatum);
	            	return false;
	            }
            }
            // die Zeit
            if (cell.getColumnIndex() == Config.planColZeit) {
	            if (cell.getCellType() == CellType.NUMERIC) {
	            	mZeit = cell.getDateCellValue();
	            }
	            else {
	            	addFehler("Fehler, Zeile: " + lRow.getRowNum() + " Spalte: " + Config.planColZeit);
	            	return false;
	            }
           }
            // Namen
            if (cell.getColumnIndex() == Config.planColName1) {
	            spielerName1 = cell.getStringCellValue();
            }
            if (cell.getColumnIndex() == Config.planColName2) {
	            spielerName2 = cell.getStringCellValue();
            }
        }
        return true;
	}

	/**
	 * Die Matches den einzelnen Spieler zuordnen.
	 */
	private void addMatches() {
		String[] namen = null;
		try {
			if (spielerName1 != null) {
				namen = spielerName1.split(Config.planTrennChar);
				addMatches(namen);
			}
			if (spielerName2 != null) {
				namen = spielerName2.split(Config.planTrennChar);
				addMatches(namen);
			}
			spielerName1 = null;
			spielerName2 = null;
		}
		catch (Exception ex) {
			addFehler(ex.getMessage());
//			Trace.println(3, Config.get(Config.planTrennCharKey) + " " + ex.getMessage());
		}
	}


	/**
	 * Von einer Zeile die Matches eintragen, kann auch Doppel enthalten
	 */
	private void addMatches(String[] namen)  throws Exception {
		String spielTyp ="E";
		if (namen.length > 1) {
			spielTyp = "D";
		}
		addMatchOfSpieler(namen[0], spielTyp);
		if (namen.length > 1) {
			addMatchOfSpieler(namen[1].trim(), spielTyp);
		}
	}

	/**
	 * Von einem Spieler(paar) den Match speichern
	 * @param name
	 */
	private void addMatchOfSpieler(String name, String spielTyp) throws Exception {
		String[] namen = name.trim().split(" ");
		if (namen.length > 2) {
			namen[1] = namen[namen.length-1];
		}
		int spielerId = SpielerData.instance().readId(namen[0].trim(), namen[1].trim());
		if (spielerId >= 0) {
			Match match = new Match();
			match.setSpielerId(spielerId);
			// wenn Datum und Zeit in der gleichen Spalte
			if (Config.planColDatumZeit >= 0) {
				match.setDatum(Config.sdfDb.format(mDatumZeit));
			}
			else {
				String datum = Config.sdfDatum.format(mDatum);
				String zeit = Config.sdfZeit.format(mZeit);
				match.setDatum(datum + " " + zeit);
			}
			match.setSpielTyp(spielTyp);
			MatchData.instance().add(match);
		}
		else {
			addFehler(name);
//			throw new Exception("Spieler nicht gefunden: " + name);
		}
	}

	/**
	 * Fehler String erweitern
	 * @param name
	 */
	private void addFehler(String name) {
		fehler.append(name);
		fehler.append("\n");
	}

}
