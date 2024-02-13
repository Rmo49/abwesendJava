package com.rmo.abwesend.model;

import java.util.ArrayList;
import java.util.List;

import com.rmo.abwesend.util.Config;

/**
 * Daten eines Spielers. Die Abwesenheit ist eine Liste durch ';' getrennt.
 * @author ruedi
 *
 */
public class Spieler {

	// Attribute
	private int id = -1; // die unique ID
	private String name = null;
	private String vorName = null;
	private String email = null;
	private List<String> abwesendList;
	private List<String> spieleList;
	private List<Tableau> tableauList;

	/**
	 * Standard Konstruktor, damit wie Bean behandelt werden kann.
	 */
	public Spieler() {
		abwesendList = new ArrayList<>(Config.turnierMaxTage);
		tableauList = new ArrayList<>(5);
		for (int i = 0; i < Config.turnierMaxTage; i++) {
			abwesendList.add(" ");
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int iD) {
		this.id = iD;
	}

	public void setId(String id) {
		this.id = Integer.getInteger(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVorName() {
		return vorName;
	}

	public void setVorName(String vorName) {
		this.vorName = vorName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Den ganzen Array in String
	 */
	public String getAbwesendAsString() {
		if (abwesendList == null) {
			return " ";
		}
		return makeString(abwesendList);
	}

	/**
	 * Den Wert an einer bestimmten Position.
	 *
	 * @param dayNr
	 *            0..max
	 * @return String an der gesuchten Position.
	 */
	public String getAbwesendAt(int dayNr) {
		try {
			return abwesendList.get(dayNr);
		} catch (IndexOutOfBoundsException ex) {
			return " ";
		}
	}

	/**
	 * Gibt alle Tage als Liste zurück.
	 *
	 * @return
	 */
	public List<String> getAbwesendList() {
		return abwesendList;
	}


	/**
	 * Prüft, ob ein Eintrag in Abwesenheiten
	 *
	 * @return true wenn Abwsenheiten eingetragen
	 */
	public boolean hasAbwesenheit() {
		String abw;
		for (String element : abwesendList) {
			abw = element;
			if (! abw.isBlank()) {
				return true;
			}
		}
		return false;
	}



	/**
	 * Liste von Tage, die übernommen werden soll.
	 *
	 * @param list
	 *            String-Werte getrennt durch ";"
	 */
	public void setAbwesendList(String list) {
		abwesendList = makeList(list);
	}

	/**
	 * Ganze Liste wird übernommen
	 *
	 * @param list
	 */
	public void setAbwesendList(List<String> list) {
		this.abwesendList = list;
	}

	/**
	 * Den Wert an einem bestimmten Tag setzen.
	 *
	 * @param pos
	 * @param text
	 */
	public void setAbwesendAt(int pos, String text) {
		abwesendList.set(pos, text);
	}

	/**
	 * Den leeren Array in String speichern
	 */
//	public String getSpieleAsString() {
//		if (spieleList == null) {
//			return " ";
//		}
//		return ";;";
//	}

	/**
	 * Den Wert an einer bestimmten Position
	 * Wenn falsche Position dann wird Leerstring zurückgegeben.
	 *
	 * @param pos  0..max
	 * @return String an der gesuchten Position, oder Leerstring
	 */
	public String getSpielAt(int pos) {
		try {
			return spieleList.get(pos);
		} catch (IndexOutOfBoundsException ex) {
			return " ";
		}
	}

	/**
	 * Gibt alle Tage als Liste zurück.
	 *
	 * @return
	 */
	public List<String> getSpieleList() {
		return spieleList;
	}

	/**
	 * Liste von Tage, die übernommen werden soll.
	 *
	 * @param list
	 *            String-Werte getrennt durch ";"
	 */
	public void setSpieleList(String list) {
		spieleList = makeList(list);
	}

	/**
	 * Ganze Liste wird übernommen
	 *
	 * @param list
	 */
//	public void setSpieleList(List<String> list) {
//		this.spieleList = list;
//	}

	/**
	 * Den Wert an einem bestimmten Tag dazufügen.
	 *
	 * @param pos
	 * @param text
	 */
	public void setSpielAt(int pos, String text, boolean add) {
		if (spieleList == null) {
			initSpieleList();
		}
		String listText = spieleList.get(pos);
		if (add && listText != null && listText.length() > 1) {
			listText = listText + "," + text;
		}
		else {
			listText = text;
		}
		spieleList.set(pos, listText);
	}

	/**
	 * Die Liste der Tableau, wo der Spieler mitmacht.
	 *
	 * @return
	 */
	public List<Tableau> getTableauList() {
		return tableauList;
	}

	/**
	 * Die Liste der Tableau, wo der Spieler mitmacht.
	 *
	 * @param tableauList
	 */
	public void setTableauList(List<Tableau> tableauList) {
		this.tableauList = tableauList;
	}

	/**
	 * Die Spiele-Liste anlegen
	 * @param list
	 * @return
	 */
	private void initSpieleList() {
		spieleList = new ArrayList<>(Config.turnierMaxTage);
		for (int i = 0; i < Config.turnierMaxTage; i++) {
			spieleList.add(i, "");
		}
	}

	/**
	 * Convert String zu Liste.
	 * @param list
	 * @return
	 */
	private List<String> makeList(String list) {
		List<String> newList = new ArrayList<>(Config.turnierMaxTage);
		String[] stringArr = new String[Config.turnierMaxTage];
		stringArr = list.split(";");
		for (int i = 0; i < Config.turnierMaxTage; i++) {
			if (i < stringArr.length) {
				newList.add(i, stringArr[i].trim());
			} else {
				newList.add(i, "");
			}
		}
		return newList;
	}

	/**
	 * Convert eine Liste in einen String.
	 * @param list
	 * @return
	 */
	private String makeString(List<String> list) {
		StringBuffer buffer = new StringBuffer(Config.turnierMaxTage * 10);
		for (String element : list) {
			buffer.append(element);
			buffer.append(";");
		}
		return buffer.toString();
	}

}
