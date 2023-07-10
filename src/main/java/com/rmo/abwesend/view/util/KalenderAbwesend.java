package com.rmo.abwesend.view.util;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.util.Config;

/**
 * Die Tabelle mit den Abwesenheiten anzeigen, wird in den Views verwendet.
 * Zeigt auch die Matches.
 *
 * @author Ruedi
 */
public class KalenderAbwesend extends KalenderBase {

	private static final long serialVersionUID = 2541526615271492523L;
	private List<String> oldTextList = null; // zur Kontrolle ob etwas geändert
	private List<JTextField> inputTextList = null; // für die Eingabe der Abwesenheiten
	private boolean spieleAnzeigen = false;

	/**
	 * Konstruktor, initialisiert den Grid, setzt den Header. Version 1 ist der
	 * Header in der Liste (scrolllt mit) Version 2 für grosse Liste, Header stabil)
	 */
	public KalenderAbwesend(boolean alleTage) {
		rowNr = 0;
		init(alleTage);
	}

	/**
	 *
	 * @param pSpieler
	 * @param mitSpiele
	 * @return -1 wenn schon vorhanden, sonst Länge der Liste
	 */
	public int addSpieler(Spieler pSpieler, boolean mitSpiele) {
		spieleAnzeigen = mitSpiele;

		// alle Spiele von Tabelle in Spieler übertragen
		if (mitSpiele) {
			pSpieler = setMatches(pSpieler);
		}
		// pos ist die Position in der Kontroll-Liste
		return addSpielerInList(pSpieler);
	}

	/**
	 * Den Spieler zusätzlich anzeigen
	 *
	 * @param mitSpiele
	 */
	public void showSpieler(Spieler pSpieler, boolean mitSpiele, boolean alleTage) {
		rowNr++;
		showSeparator(rowNr);
		rowNr++;
		showAbwesenheit(pSpieler, rowNr);
//			showDeleteButton(rowNr);
		rowNr++;
		showRectangle(pSpieler, rowNr);
		rowNr++;
		if (mitSpiele) {
			showGames(pSpieler, rowNr);
			rowNr++;
		}
	}

	/**
	 * Separator zeichnen
	 *
	 * @param rowNr
	 */
	private void showSeparator(int rowNr) {
		kalenderPanel.add(new JLabel(""), getConstraintFirst(0, rowNr));
		int colNr = 1;
		for (int i = Config.showBeginNumber; i < Config.showEndNumber; i++) {
			kalenderPanel.add(new JSeparator(SwingConstants.HORIZONTAL), getConstraintNext(colNr, rowNr));
			colNr++;
		}
	}

	/**
	 * Name und die Abwesenheiten anzeigen
	 *
	 * @param pSpieler
	 * @param rowNr
	 */
	private void showAbwesenheit(Spieler pSpieler, int rowNr) {
		// zuerst Vorname des Spielers auf erster col
		JLabel name = new JLabel(pSpieler.getVorName());
		kalenderPanel.add(name, getConstraintFirst(0, rowNr));

		// die Abwesenheit in Zahlen
		int colNr = 1;
		for (int i = posVon; i < posBis; i++) {
			JLabel abwesend = new JLabel(pSpieler.getAbwesendAt(i));
			kalenderPanel.add(abwesend, getConstraintNext(colNr, rowNr));
			colNr++;
		}
	}

	/**
	 * Visuelle anzeige der Abwesenheiten mit Rectangle
	 *
	 * @param pSpieler
	 * @param rowNr
	 */
	private void showRectangle(Spieler pSpieler, int rowNr) {
		kalenderPanel.add(new JLabel(pSpieler.getName() + " " + pSpieler.getVorName().charAt(0)),
				getConstraintFirst(0, rowNr));

		// Die visuelle Anzeige von Abwesenheit und Spiele
		try {
			int colNr = 0;
			for (int i = posVon; i < posBis; i++) {
				showNextRect(colNr, 0, isWeekend[i], pSpieler.getAbwesendAt(i), pSpieler.getSpielAt(i));
				colNr++;
			}
		} catch (Exception ex) {
			CmUtil.alertError("AbwesendKalender", "Fehler beim rekursiv lesen");
		}
	}

	/**
	 * Alle Rectangle darstellen (Abwesenheit und Match), wird rekrusiv aufgerufen
	 *
	 * @param colNr     die Spalten-Nummer die erste = 0
	 * @param posStart	ab dieser Position zeichnen, wir in der Rekursion erhöht
	 * @param isWeekend true wenn Weekend
	 * @param abwTime	String mit Abwesenheit
	 * @param matches	String mit Matches
	 */
	private void showNextRect(int colNr, int posStart, boolean isWeekend, String abwTime, String matches) {
		int posStartAbw = getPosStartAbw(abwTime, posStart, isWeekend);
		int posEnd = 0; // das allgemeine Ende eine Rectangles ob Abw. oder Match

		if (posStartAbw == 0) {
			if (getPosEndAbw(abwTime, isWeekend) == 0) {
				// wenn kein Eintrag
				posStartAbw = (int) RECT_WIDTH + 10;
				posEnd = (int) RECT_WIDTH + 10;
			}
		}
		int posStartMatch = getPosStartMatch(matches, posStart, isWeekend);
		// Abwesenheit zuerst zeichnen wenn vor Match
		if (posStartAbw < posStartMatch) {
			if (posStartAbw <= RECT_WIDTH) {
				posEnd = getPosEndAbw(abwTime, isWeekend);
				if (posEnd > posStartMatch) {
					// Platz für Match, diesen immer anzeigen
					posEnd = posStartMatch;
				}
				addRect(colNr, posStartAbw, posEnd, COLOR_ABW, rowNr);
			}
		} else {
			// Match zeichnen, wenn innerhalb Rectangle
			if (posStartMatch < RECT_WIDTH) {
				posEnd = posStartMatch + RECT_WIDTH_MATCH;
				if (matches.startsWith("E")) {
					addRect(colNr, posStartMatch, posEnd, COLOR_MATCH_E, rowNr);
				}
				else {
					addRect(colNr, posStartMatch, posEnd, COLOR_MATCH_D, rowNr);
				}
			} else {
				posEnd = (int) RECT_WIDTH + 10;
			}
		}
		if (posEnd < RECT_WIDTH) {
			// wenn noch nicht fertig mit zeichnen in einem Rectangle, ab posEnd
			showNextRect(colNr, posEnd, isWeekend, abwTime, matches);
		}
	}

	/**
	 * Geplante Spiele anzeigen
	 *
	 * @param pSpieler
	 * @param rowNr
	 */
	private void showGames(Spieler pSpieler, int rowNr) {
		rowNr++;
		if (spieleAnzeigen) {
			kalenderPanel.add(new JLabel("Spiele:"), getConstraintFirst(0, rowNr));

			for (int i = posVon; i < posBis; i++) {
				kalenderPanel.add(new JLabel(pSpieler.getSpielAt(i)),
						getConstraintNext(i - Config.showBeginNumber + 1, rowNr));
			}
		}
	}

	/**
	 * Die Input-Felder dazufügen, immer für alle Tage.
	 *
	 * @param grid
	 * @param row
	 */
	public void addInputFields(Spieler pSpieler) {
		kalenderPanel.add(new JLabel("Abwesend:"), getConstraintFirst(0, rowNr));
		oldTextList = new ArrayList<>(Config.turnierMaxTage);
		inputTextList = new ArrayList<>(Config.turnierMaxTage);
		int colNr = 1;
		for (int i = 0; i < Config.turnierMaxTage; i++) {
			oldTextList.add(pSpieler.getAbwesendAt(i));

			JTextField tagText = new JTextField();
			tagText.setSize((int) RECT_WIDTH - 10, RECT_HIGTH);
			tagText.setText(pSpieler.getAbwesendAt(i));
			tagText.getDocument().addDocumentListener(new InputFieldChangeListener());

			if (i >= posVon && i < posBis) {
				kalenderPanel.add(tagText, getConstraintNext(colNr, rowNr));
				colNr++;
			}
			inputTextList.add(i, tagText);
		}
		rowNr++;
	}

	/**
	 * Die Liste mit den Input Feldern
	 *
	 * @return
	 */
	public List<JTextField> getInputList() {
		return inputTextList;
	}

	/**
	 * Alle Spieler in Kurzform anzeigen
	 *
	 * @param mitSpiele
	 */
	public void showAllSpielerShort() {
		Spieler pSpieler = null;
		rowNr = 1;
		for (int pos = 0; pos < getSpielerListSize(); pos++) {
			pSpieler = getSpielerAt(pos);
			rowNr++;
			showSeparator(rowNr);
			rowNr++;
			showRectangle(pSpieler, rowNr);
		}
	}

	/**
	 * Die Position der Abwesenheit berechnen
	 *
	 * @param abwesendZeit
	 * @param startPos     ab dieser Position berechnen
	 * @return grosse Zahl wenn keine Abwesenheit
	 */
	private int getPosStartAbw(String abwesendZeit, double startPos, boolean isWeekend) {
		if (abwesendZeit == null || abwesendZeit.length() < 1) {
			return (int) RECT_WIDTH + 10;
		}
		if (startPos == 0) {
			if ((abwesendZeit.compareTo("0") == 0) || abwesendZeit.startsWith("-")) {
				return 0;
			}
		}
		if (abwesendZeit.endsWith("-")) {
			double pos = getPos(abwesendZeit.substring(0, abwesendZeit.length() - 1), isWeekend, false);
			if (pos >= startPos) {
				return (int) pos;
			}
		}
		return (int) RECT_WIDTH + 10;
	}

	/**
	 * Die Position der Abwesenheit berechnen
	 *
	 * @param abwesendZeit
	 * @param startPos
	 * @return
	 */
	private int getPosEndAbw(String abwesendZeit, boolean isWeekend) {
		if (abwesendZeit.compareTo("0") == 0) {
			return (int) RECT_WIDTH;
		}
		if (abwesendZeit.startsWith("-")) {
			return (int) getPos(abwesendZeit.substring(1, abwesendZeit.length()), isWeekend, false);
		}
		if (abwesendZeit.endsWith("-")) {
			return (int) RECT_WIDTH;
		}
		return -1;
	}

	/**
	 * Die Position des Matches berechnen, wenn kein Match, dann wir grosse Zahl
	 * zurückgegeben
	 *
	 * @param abwesendZeit
	 * @param startPos
	 * @return
	 */
	private int getPosStartMatch(String matchZeit, double startPos, boolean isWeekend) {
		if (matchZeit.length() > 1) {
			int startText = 0;
			double pos = 0;
			// wenn etwas eingetragen
			while (matchZeit.length() > startText + 5) {
				String zeit = matchZeit.substring(startText + 1, startText + 6);
				pos = getPos(zeit, isWeekend, true);
				if (pos < startPos) {
					startText += 7;
				} else {
					break;
				}
			}
			// wenn nix gefunden, dann pos grosse Zahl
			if (pos < startPos) {
				pos = RECT_WIDTH + 10;
			}
			// wenn die Zeit ausserhalb der Anzeigen, dann zurücksetzen
			else {
				if (pos >= RECT_WIDTH) {
					pos = RECT_WIDTH - RECT_WIDTH_MATCH;
				}
			}
			return (int) pos;
		} else {
			return (int) RECT_WIDTH + 10;
		}
	}

	private void addRect(int colNr, int startPos, int endPos, Color color, int rowNr) {
		int width = endPos - startPos;
		kalenderPanel.add(new RectPanel(startPos, width, color), getConstraintRect(colNr + 1, rowNr));
	}

	/**
	 * Die Zeit als Nummer, es werden nur die vollen Stunden berücksichtigt. Wenn
	 * keine gültige zeit dann -1. Wenn zeit ausserhalb Bereich, dann 0
	 *
	 * @param zeit
	 * @param isWeekend
	 * @isMatch wenn die Zeit eines Matches gelesen werden soll
	 * @return die Zeit als Nummer
	 */
	private double getPos(String zeit, boolean isWeekend, boolean isMatch) {
		String std[] = zeit.split("[:.]");
		int stunde = -1;
		if (std.length == 0) {
			return stunde;
		}
		try {
			stunde = Integer.parseInt(std[0]);
		} catch (NumberFormatException ex) {
			// nichts wird angezeigt
		}
		if (isWeekend) {
			double startStd = stunde - Config.weekendBegin;
			if (startStd > 0) {
				double width = RECT_WIDTH * (startStd / Config.weekendDauer);
				if (width > RECT_WIDTH) {
					width = RECT_WIDTH;
				}
				return width;
			}
			return 0;
		}
		double endStd = stunde - Config.weekBegin;
		if (endStd > 0) {
			double endPos = RECT_WIDTH * (endStd / Config.weekDauer);
			return endPos > RECT_WIDTH ? RECT_WIDTH : endPos;
		}
		return 0;
	}

	// create a panel that you can draw on.
	class RectPanel extends JPanel {
		private static final long serialVersionUID = -1528962361868364694L;
		private int mX = 0;
		private int mWidht = 0;
		private Color mColor;

		RectPanel(int x, int width, Color color) {
			mX = x;
			mWidht = width;
			mColor = color;
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(mColor);
			g.fillRect(mX, 0, mWidht, RECT_HIGTH);
		}
	}

	/**
	 * Der Listener für JTextField wenn etwas geändert
	 *
	 * @author ruedi
	 *
	 */
	class InputFieldChangeListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			setInputFieldChanged(e.getDocument().getLength());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			setInputFieldChanged(e.getDocument().getLength());
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			setInputFieldChanged(e.getDocument().getLength());
		}

	}

	// --------- Property Change Support für InputFields -------------
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public void setInputFieldChanged(int newValue) {
		// value immer ändern (ist irelevant), damit ein event abgeschickt wird.
		this.pcs.firePropertyChange("InputFiled", newValue, ++newValue);
	}
}
