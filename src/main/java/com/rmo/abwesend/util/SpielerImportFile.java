package com.rmo.abwesend.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;

/**
 * Spieler von einem csv-File (in Text Format) einlesen, das von Swistennis exportiert wurde.
 * @author ruedi
 *
 */
public class SpielerImportFile {

	private boolean fehler = false;
	private int posTableau = 0;
	private int posName = 1;
	private int posVorname = 2;
	private int posNameDoppel = 3;
	private int posVornameDoppel = 4;


	public SpielerImportFile () {

	}

	/**
	 * Einlesen der Spieler Zeile um Zeile.
	 * Wenn true zurück gibt, dann keine Fehler gefunden, sonst Name in Trace
	 *
	 * @param dirName
	 * @param fileName
	 * @param tableauName: Wenn Len > 0, muss in TableauBezeichnung vorhanden sein.
	 * @return
	 * @throws Exception
	 */
	public boolean startSpielerEinlesen(String tableauName) throws Exception {
		Trace.println(3, "SpielerVonFile.startSpielerEinlesen()");
		fehler = false;
		String dirFile = Config.spielerImportDir + "/" + Config.spielerImportFile;
		InputStreamReader fileIn;
		fileIn = new InputStreamReader(new FileInputStream(dirFile), StandardCharsets.UTF_8) ;

		// Tableau-Liste lesen, damit nicht immer Zugriff auf DB und wegen Probleme mit String-split
		List<Tableau> tableauList = TableauData.instance().readAllTableau();
		List<Tableau> tableauList2 = new ArrayList<>();
		if (tableauName.length() > 0) {
			// wenn etwas eingegeben in der Beschränkung der Tableau
			Iterator<Tableau> iter = tableauList.iterator();
			Tableau tableau;
			while (iter.hasNext()) {
				tableau = iter.next();
				if (tableau.getBezeichnung().contains(tableauName)) {
					// löschen des Eintrage
					tableauList2.add(tableau);
				}
			}
		}
		else {
			tableauList2 = tableauList;
		}

		BufferedReader bufferedReader = new BufferedReader(fileIn);
		String line;
		//--- iteration über alle Zeilen
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(Config.spielerImportSplitChar)) {
				String[] lSpielerZeile = line.split(Config.spielerImportSplitChar);
				if (lSpielerZeile.length >= 0) {
					try {
						int spielerId = addSpieler(lSpielerZeile[posName], lSpielerZeile[posVorname]);

						int tableauId = -1;
						if (tableauName.length() > 0) {
							if (lSpielerZeile[posTableau].contains(tableauName)) {
								tableauId = getTableauId(lSpielerZeile[posTableau], tableauList2);
								if (tableauId < 0) {
									fehler = true;
								}
							}
						}
						else {
							tableauId = getTableauId(lSpielerZeile[posTableau], tableauList2);
							if (tableauId < 0) {
								fehler = true;
							}
						}
						if (tableauId >= 0) {
							// Spieler <-> Tableau in die DB schreiben
							addTableau(spielerId, tableauId);
						}

						// den Dopple-Partner lesen
						if (lSpielerZeile.length > posNameDoppel) {
							if ((lSpielerZeile[posNameDoppel].length() > 0) && (lSpielerZeile[posVornameDoppel].length() > 0)) {
								spielerId = addSpieler(lSpielerZeile[posNameDoppel], lSpielerZeile[posVornameDoppel]);
							}
							if (tableauId >= 0) {
								addTableau(spielerId, tableauId);
							}
						}
					}
					catch (Exception ex) {
						Trace.println(3, "Spieler einlesen " + ex.toString());
						fehler = true;
					}
				}
			}
			else {
				Trace.println(5, "In Zeile '" + line + "' Split-Char '" + Config.spielerImportSplitChar + "' nicht vorhanden");
			}
		}
		bufferedReader.close();
		Trace.println(3, "SpielerVonFile.startEinlesen() <<< End");
		return fehler;
	}


	/**
	 * Einen Spieler dazufügen, falls noch nicht in der DB
	 * @param spielerName
	 * @throws Exception
	 */
	private int addSpieler(String name, String vorname) throws Exception {
		Spieler spieler = new Spieler();
		spieler.setName(name.trim());
		spieler.setVorName(vorname.trim());
		Trace.println(4, "SpielerImportFile.addSpieler: " + spieler.getName() + " " + spieler.getVorName());

		return SpielerData.instance().add(spieler);
	}

	/**
	 * Die Id bestimmen
	 * @param konkSuche nach der gesucht wird
	 * @param tableauList
	 * @param tableauName, wenn > 0 muss dieser Name in der TableauBezeichung vorhanden sein.
	 * @return, -1 wenn nicht gefunden, sonst die ID
	 */
	private int getTableauId(String konkSuche, Collection<Tableau> tableauList) {
		Trace.print(5, "SpielerVonFile.getTableauId: " + konkSuche);
		int id = -1;
		// damit die Leerzeichen richtig gesetzt werden.
//		String konkSuche2 = decodeFromUtf8(konkSuche);
//		String[] konkSuch = konkSuche2.split("\\s+");
//		if (konkSuch.length < 2) {
//			return id;
//		}

		Iterator<Tableau> iter = tableauList.iterator();
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
		}
		else {
			Trace.println(5, "");
		}
		return id;
	}

	/**
	 * Die chars -62, -96 in 32 konvertieren
	 * @param utf8
	 * @return java-string
	 */
	/*
	private String decodeFromUtf8(String utf8) {
		byte[] utf8b = utf8.getBytes();
		StringBuffer result = new StringBuffer(utf8.length());
		for (int i = 0; i < utf8b.length; i++) {
			if (utf8b[i] == -62) {
				 // nix machen, überlesen
			}
			else {
				if (utf8b[i] == -96) {
					result.append(' ');
				}
				else {
					char c = (char) utf8b[i];
					result.append(c);
				}
			}
		}
		return result.toString();
	}
	*/

	/**
	 * Vergleicht 2 Strings
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

		if (i < lenMax-2) {
			// nicht indetisch, wenn nicht alles verglichen
			return false;
		}
		return true;
	}

	/**
	 * Wenn mehrere Leerzeichen nacheinander, dann diese löschen.
	 * @param str
	 * @return
	 */
	String leerzeichenLoeschen(String str) {
		StringBuffer str2 = new StringBuffer(36);
		int i = 0;
		int leer = 0;
		while (i < str.length()) {
			int ch = str.charAt(i);
			switch(ch) {
			case 32:
				if (i == leer+1) {
					// nicht übernehme
				}
				else {
					str2.append(str.charAt(i));
				}
				leer = i;
				break;
			case 160:
				// spezielle Space von Unicode
				if (i == leer+1) {
					// nichts machen
				}
				else {
					// beim ersten ein Leezeichen einfügen
					str2.append(" ");
				}
				leer = i;
				break;
			default:
				str2.append((char)ch);
			}
			i++;
		}
		return str2.toString();
	}


	/**
	 * Die Beziehung Spieler / Tableau sichern.
	 * @param spielerID
	 * @param konkurrenz
	 */
	private void addTableau(int spielerID, int tableauId) {
		if (tableauId >= 0) {
			try {
				SpielerTableauData.instance().add(spielerID, tableauId);
			}
			catch (Exception ex) {
				Trace.println(3, "Fehler bei Tableau zuteilen: " + ex.toString());
				fehler = true;
			}
		}
	}



	/**
	 * Einlesen der e-mail Zeile um Zeile.
	 * Dann den Spieler suchen, wenn gefunden, e-mail eintragen
	 * Wenn true zurück gibt, dann keine Fehler gefunden, sonst Name in Trace
	 */
	public boolean startEmailEinlesen(String dirName, String fileName) throws Exception {
		Trace.println(3, "SpielerVonFile.startEmailEinlesen()");
		fehler = false;
		String dirFile = dirName + "/" + fileName;
		InputStreamReader fileIn;
		fileIn = new InputStreamReader(new FileInputStream(dirFile), StandardCharsets.UTF_8) ;

		BufferedReader bufferedReader = new BufferedReader(fileIn);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(Config.spielerImportSplitChar)) {
				String[] lSpielerZeile = line.split(Config.spielerImportSplitChar);
				if (lSpielerZeile.length >= 3) {
					int spielerID = SpielerData.instance().readId(lSpielerZeile[0].trim(), lSpielerZeile[1].trim());
					if (spielerID > 0) {
						Trace.println(4, "id: " + spielerID + " " + lSpielerZeile[0] + " " + lSpielerZeile[1]);
						addEmail(spielerID, lSpielerZeile[2]);
					}
					else {
						Trace.println(4, "nicht gefunden: " + lSpielerZeile[0] + " " + lSpielerZeile[1]);
					}
				}
			}
		}
		bufferedReader.close();
		Trace.println(3, "SpielerVonFile.startEinlesen() <<< End");
		return fehler;
	}

	/**
	 * Email zum Spieler dazufügen
	 * @param spielerName
	 * @throws Exception
	 */
	private void addEmail(int spielerId, String email) throws Exception {
		Spieler spieler = SpielerData.instance().read(spielerId);
		spieler.setEmail(email);
		SpielerData.instance().add(spieler);
	}


}
