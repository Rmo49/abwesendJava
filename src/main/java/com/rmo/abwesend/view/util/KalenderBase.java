package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.rmo.abwesend.model.Match;
import com.rmo.abwesend.model.MatchData;
import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.util.Config;

/**
 * Die Basisklasse für alle Darstellungen des Kalenders.
 * 
 * @author Ruedi
 *
 */
public class KalenderBase extends JComponent {

	private static final long serialVersionUID = 5865140523599634196L;
	protected final double GRID_HEIGTH = 120;
	protected final double NAMECOL_WIDTH = 80;
	protected final double DELBUTTON_WIDTH = 10;
	protected final double RECT_WIDTH = 50.0;
	protected final int RECT_HIGTH = 16;
	protected final int RECT_WIDTH_MATCH = 7;
	protected final Color COLOR_ABW = Color.PINK;
	protected final Color COLOR_MATCH_D = Color.DARK_GRAY;
	protected final Color COLOR_MATCH_E = Color.BLUE;
	// Datumsformat für die Anzeige
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("E d.M.");

	private JScrollPane scrollPane = null;
	protected JPanel kalenderPanel = null;
	protected GridBagLayout gbl = null;
	// positon für die Anzeige der Abwensenheiten
	protected int dayNrVon;
	protected int dayNrBis;

	private List<Spieler> spielerList = null; // Liste aller Spieler im Kalender

	protected boolean[] isWeekend = new boolean[Config.turnierMaxTage];

	// die zeilen-Nummer im Grid, startet mit 1
	protected int rowNr = 1;

	/**
	 * Version 1 Header stabil in der ersten Zeile Version 2 für grosse Liste?
	 *
	 * @param version
	 */
	protected void init(boolean alleTage) {
		// Array für die Spieler anlgegen
		spielerList = new ArrayList<>();
		setupKalendarPanel(alleTage);
		setupScrollPane();
		rowNr = 1;
	}

	/**
	 * Die position für die Anzeige des Arrays setzen
	 * 
	 * @param alleTage wenn alle Tage angezeigt werden sollen, sonst nur gemäss
	 *                 Anzeige von bis.
	 */
	protected void setPosVonBis(boolean alleTage) {
		if (alleTage) {
			dayNrVon = 0;
			dayNrBis = Config.turnierMaxTage;
		} else {
			dayNrVon = Config.showBeginNumber;
			dayNrBis = Config.showEndNumber;
		}
	}

	/**
	 * Ein GridPane erstellen
	 * 
	 * @return
	 */
	protected JPanel getGrid() {
//		int maxCols = Config.showEndNumber + 2;

		JPanel panel = new JPanel(new GridBagLayout());
		return panel;
	}

	/**
	 * Die Panels für den Kalener-Grid werden angelegt.
	 *
	 * @return
	 */
	public void setupScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(kalenderPanel);
		} else {
			scrollPane.setViewportView(kalenderPanel);
		}
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
	}

	/**
	 * Der Panel für den Kalender-Grid werden angelegt.
	 *
	 * @return
	 */
	public void setupKalendarPanel(boolean alleTage) {
		gbl = new GridBagLayout();
		kalenderPanel = new JPanel(gbl);

		// Header auf der Zeile 0 anzeigen
		setupHeaderGrid(0, alleTage);

		kalenderPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		kalenderPanel.setPreferredSize(new Dimension(700, 200));
	}

	public JComponent getBasePane() {
		return scrollPane;
	}

	/**
	 * Den Header mit Datum zurückgeben.
	 * 
	 * @param grid
	 * @param row
	 */
	protected void setupHeaderGrid(int row, boolean alleTage) {
		JLabel labelDatum = new JLabel("Datum:");
		kalenderPanel.add(labelDatum, getConstraintFirst(0, row));

		GregorianCalendar calendar = new GregorianCalendar();
		if (alleTage) {
			calendar.setTime(Config.turnierBeginDatum);
		} else {
			calendar.setTime(Config.showBeginDatum);
		}
		calendar.setLenient(true);

		setPosVonBis(alleTage);
		int colNr = 1;
		for (int i = dayNrVon; i < dayNrBis; i++) {
			labelDatum = new JLabel(dateFormat.format(calendar.getTime()));
			if ((calendar.get(Calendar.DAY_OF_WEEK) == 7) || (calendar.get(Calendar.DAY_OF_WEEK) == 1)) {
				labelDatum.setBackground(Config.colorWeekend);
				labelDatum.setOpaque(true);
				isWeekend[i] = true;
			} else {
				isWeekend[i] = false;
			}
			kalenderPanel.add(labelDatum, getConstraintNext(colNr, row));
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			colNr++;
		}
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * 
	 * @param row
	 * @return
	 */
	protected GridBagConstraints getConstraintFirst(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.8;
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Den Gridbag der für alle Darstellungen verwendet wird
	 * 
	 * @param row
	 * @return
	 */
	protected GridBagConstraints getConstraintNext(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Den Gridbag der für die Recgtangles verwendet wird
	 * 
	 * @param row
	 * @return
	 */
	protected GridBagConstraints getConstraintRect(int colNr, int rowNr) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = colNr;
		c.gridy = rowNr;
		return c;
	}

	/**
	 * Den Spieler in die Kontroll-Liste einfügen, falls noch nicht vorhanden.
	 * 
	 * @param pSpieler
	 * @return -1 wenn schon vorhanden, sonst Position in der Kontroll-Liste
	 */
	protected int addSpielerInList(Spieler pSpieler) {
		if (spielerList.size() > 0) {
			for (Spieler sp : spielerList) {
				if (sp.getId() == pSpieler.getId()) {
					return -1;
				}
			}
		}
		spielerList.add(pSpieler);
		return spielerList.size();
	}

	/**
	 * Den Spieler von der Position entfernen. return true wenn removed
	 * 
	 * @param position
	 */
	protected boolean removeSpielerFromList(int pos) {
		if (spielerList.size() > 0) {
			spielerList.remove(pos);
			return true;
		}
		return false;
	}

	/**
	 * Alle Spieler von der internen Liste entfernen
	 */
	public void removeAllSpielerFromList() {
		if (spielerList.size() > 0) {
			spielerList.removeAll(spielerList);
		}
	}

	/**
	 * Alle Spieler vom Panel entfernen
	 */
	public void removeAllSpieler() {
		scrollPane.remove(kalenderPanel);
		rowNr = 1;
	}

	/**
	 * Die Anzahl Spieler in der Liste
	 * 
	 * @return
	 */
	protected int getSpielerListSize() {
		return spielerList.size();
	}

	/**
	 * Ein Spieler von der Liste
	 * 
	 * @param pos
	 * @return
	 */
	protected Spieler getSpielerAt(int pos) {
		return spielerList.get(pos);
	}

	/**
	 * Alle Spieler von der SpielerList entfernen.
	 */
	public void clearSpielerList() {
		if (spielerList != null) {
			spielerList.clear();
		}
	}

	/**
	 * Die Spiele in den Array eintragen
	 * 
	 * @param pSpieler
	 * @return
	 */
	protected Spieler setMatches(Spieler pSpieler) {
//		pSpieler.setSpieleList("");
		List<Match> matches = null;
		try {
			matches = MatchData.instance().readAll(pSpieler.getId());
		} catch (Exception ex) {
			// TODO wenn fehler
		}
		if (matches != null && matches.size() > 0) {
			for (Match match : matches) {
				int pos = (int) getPos(match.getDatum());
				if (pos >= 0 && pos < Config.showEndNumber) {
					pSpieler.setSpielAt(pos, match.getSpielTyp() + match.getZeit(), true);
				}
			}
		}
		return pSpieler;
	}

	/**
	 * Die position im Array (0..max)
	 * 
	 * @param datum
	 * @return position im Array
	 */
	private long getPos(String datumText) {
		Date startDate = Config.turnierBeginDatum;
		Date datum = null;
		try {
			datum = Config.sdfDatum.parse(datumText);
		} catch (ParseException ex) {
			return -1;
		}
		long diff = datum.getTime() - startDate.getTime();
		if (diff >= 0) {
			long pos = diff / Config.einTagLong;
			return pos;
		} else {
			return -1;
		}
	}

}
