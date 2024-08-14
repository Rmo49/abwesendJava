package com.rmo.abwesend.view.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerKurz;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.Trace;
import com.rmo.abwesend.view.MainFrame;

/**
 * Zeigt die Liste der Spieler und Selektions-Möglichkeiten. In einem
 * Eingabefeld kann der Name (oder Anfangsbuchstaben) eines Spielers eingegeben
 * werden. Nach dem 2 Buchstaben wird eine entsprechende Liste angezeigt. Gibt
 * die SpielerID bekannt, wenn ein Spieler selektiert.
 *
 * @author Ruedi
 *
 */
public class SpielerSelektieren {

	private final int LISTVIEW_WIDTH = 150;
	private final int LISTVIEW_HEIGHT = 600;
	private MainFrame mainFrame;

	// der Panel, der alles zusammenhält
	private JPanel paneLeft;

	// Die Anzeige der Tableau
	private DefaultComboBoxModel<Tableau> tableauListModel;
	private JComboBox<Tableau> tableauListView;
	private int selectedTableauIndex = 0;

	private JTextField suchField;

	private SpielerTableModel spielerData;
	// Die Anzeige der gewählten Spieler
	private JTable spielerTable;
	private int selectedSpielerIndex = -1;
	private int selectedSpielerId = -1;
	private JLabel anzahlSpieler;
	private JButton btnZusaetzlich;

//	private PropertyChangeListener myListener;

	public SpielerSelektieren(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		spielerData = new SpielerTableModel();
		spielerData.readAllData();
	}

	/**
	 * Eingabefeld und Liste der Spieler werden zurückgegeben.
	 *
	 * @return
	 */
	public JPanel getPanel() {
		if (paneLeft != null) {
			return paneLeft;
		}
		paneLeft = new JPanel();
		paneLeft.setLayout(new BoxLayout(paneLeft, BoxLayout.PAGE_AXIS));
		// initialisieren, da bei SpielerListe verwendet
		anzahlSpieler = new JLabel();

		paneLeft.add(addSelectionTableau());

		JPanel flowPane = new JPanel(new FlowLayout());
		flowPane.add(new JLabel("Name:"));
		suchField = new JTextField();
		suchField.setPreferredSize(new Dimension(LISTVIEW_WIDTH, Config.textFieldHeigth));
//		suchField.setBackground(Config.colorSpieler);
		flowPane.add(suchField);
		paneLeft.add(flowPane);

		paneLeft.add(addSpielerList());
		paneLeft.add(addButtons());
		// Anzeige mal erstellen
		setupListener();
		return paneLeft;
	}

	/**
	 * Die Selektion des Tableaux
	 * 
	 * @return
	 */
	private JComponent addSelectionTableau() {
		JPanel flowPane = new JPanel(new FlowLayout());

		JLabel labelTableau = new JLabel("Tableau: ");
		labelTableau.setBackground(Config.colorTable);
		labelTableau.setOpaque(true);
		flowPane.add(labelTableau);
		// --- Tableau Combobox
		tableauListModel = new DefaultComboBoxModel<>();
		tableauListView = new JComboBox<>(tableauListModel);
		tableauListView.setRenderer(new ComboBoxRenderer());
		// die Anzahl der angezeigten Tableau je nach Window-Höhe
		tableauListView.setMaximumRowCount(Config.showTableauBox);

		try {
			Collection<Tableau> allTableau = TableauData.instance().readAllTableau();
			tableauListModel.addElement(new Tableau(-1, " ", "1", "SwissTennis"));
			Iterator<Tableau> iter = allTableau.iterator();
			while (iter.hasNext()) {
				tableauListModel.addElement(iter.next());
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Datenbank lesen", JOptionPane.ERROR_MESSAGE);
		}

		tableauListView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedTableauIndex = tableauListView.getSelectedIndex();
				setupSpielerList();
				mainFrame.setEnable();
			}
		});

		/**
		 * Wenn der Focus verloren wird die letzte selektion wieder setzen
		 */
		tableauListView.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
//				System.out.println("tableau: focus lost");
//				tableauListView.setSelectedIndex(selectedTableauIndex);
			}

			@Override
			public void focusGained(FocusEvent e) {
//				System.out.println("tableau: focus gained");
			}
		});

		flowPane.add(tableauListView);
		return flowPane;
	}

	/**
	 * Die Liste mit den Spielern
	 * 
	 * @return
	 */
	private JComponent addSpielerList() {
		spielerTable = new JTable(spielerData);
		JScrollPane scroll = new JScrollPane(spielerTable);
		Dimension dim = new Dimension(LISTVIEW_WIDTH + 30, LISTVIEW_HEIGHT);
		scroll.setPreferredSize(dim);
		addTableListener(spielerTable);

		return scroll;
	}

	/**
	 * Listener um die ID des selektierten Spielers zu sichern.
	 * 
	 * @param spielerTable
	 */
	private void addTableListener(JTable table) {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				selectedSpielerIndex = table.getSelectedRow();
				if (selectedSpielerIndex >= 0) {
					// die Id des Spieler sichern
					selectedSpielerId = spielerData.getId(selectedSpielerIndex);
				} else {
					selectedSpielerId = -1;
				}
				mainFrame.setEnable();
			}
		});
	}

	/**
	 * , Wenn Tableau selektiert, die Tabelle mit den Spielern neu einlesen
	 */
	private void setupSpielerList() {
		List<SpielerKurz> tableauNames = null;
		if (selectedTableauIndex <= 0) {
			// wenn leeres Feld selektiert
			tableauNames = spielerData.getAllSpieler();
		} else {
			// da erstes leer, ist die Liste um eine Position versetzt.
			Tableau tableau = tableauListModel.getElementAt(selectedTableauIndex);
			try {
				List<Integer> spielerIdList = SpielerTableauData.instance().readAllSpieler(tableau.getId());
				tableauNames = new ArrayList<>();
				for (Integer spielerId : spielerIdList) {
					SpielerKurz spk = spielerData.getSpieler(spielerId.intValue());
					if (spk != null) {
						tableauNames.add(spk);
					}
				}
				tableauNames.sort(spielerKurzComparator);
			} catch (Exception ex) {
				CmUtil.alertError("Probleme bei Tableau lesen.", ex);
			}
		}
		// Anzeige mit allen Namen erstellen
		spielerData.setTableauSpielerData(tableauNames);
		spielerData.setSuchSpielerData("");
		spielerData.fireTableDataChanged();

		anzahlSpieler.setText(spielerData.getAnzahlSpieler());
		anzahlSpieler.repaint();
		suchField.setText("");
	}

	/**
	 * Der Comparator für das Soriteren der Namen
	 */
	Comparator<SpielerKurz> spielerKurzComparator = new Comparator<SpielerKurz>() {
		@Override
		public int compare(SpielerKurz o1, SpielerKurz o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	/**
	 * Der Comparator für das Soriteren der Namen
	 */
	Comparator<Spieler> spielerComparator = new Comparator<Spieler>() {
		@Override
		public int compare(Spieler o1, Spieler o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	/**
	 * Die Buttons unterhalb der Liste.
	 * 
	 * @return
	 */
	private JComponent addButtons() {
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.Y_AXIS));

		anzahlSpieler = new JLabel();
		anzahlSpieler.setPreferredSize(new Dimension(30, 10));
		anzahlSpieler.setText(spielerData.getAnzahlSpieler());
		anzahlSpieler.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnPane.add(anzahlSpieler);
		btnPane.add(Box.createRigidArea(new Dimension(0, 5)));

		btnZusaetzlich = new JButton("zusätzlich anzeigen");
		btnZusaetzlich.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnZusaetzlich.setBackground(Config.colorSpieler);
		btnZusaetzlich.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedSpielerId >= 0) {
					mainFrame.showSpielerNext(selectedSpielerId);
				}
			}
		});
		btnPane.add(btnZusaetzlich);

		return btnPane;
	}

	/**
	 * Wenn an Aenderungen interessiert. Listener von der gleichen Klasse nur einmal
	 * dazufügen.
	 *
	 * @param newListener
	 */
	public void addChangeListener(PropertyChangeListener newListener) {
//		myListener = newListener;
	}

	/**
	 * Der Listener für die Eingabe der Namen.
	 */
	private void setupListener() {
		// wenn etwas eingegeben im Such Feld
		suchField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				String sucheName = suchField.getText();
				// die folgenden zeilen nur bei JDK 1.8
//				if (Config.javaVersion <= 8) {
				if (Character.isLetter(e.getKeyChar())) {
					sucheName = sucheName + e.getKeyChar();
				}
//				}
				spielerData.setSuchSpielerData(sucheName.toLowerCase());
				anzahlSpieler.setText(spielerData.getAnzahlSpieler());
				anzahlSpieler.repaint();
				spielerData.fireTableDataChanged();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// nix tun
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// nix tun
			}
		});

		// Doppelklick in der Liste
		spielerTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2 && selectedSpielerId >= 0) {
					mainFrame.showSpielerNext(selectedSpielerId);
				}
			}
		});

	}

	/**
	 * Die Spieler eines Tableau
	 * 
	 * @param index index wie er in der angezeigten Liste steht
	 * @return
	 */
	public List<Spieler> getSpielerOfTableau(int index) {
		List<Spieler> spielerList = null;
		Tableau tableau = tableauListView.getItemAt(index - 1);
		try {
			List<Integer> spielerIdList = SpielerTableauData.instance().readAllSpieler(tableau.getId());
			spielerList = new ArrayList<>();
			for (Integer spielerId : spielerIdList) {
				Spieler lSpieler = SpielerData.instance().read(spielerId.intValue());
				spielerList.add(lSpieler);
			}
			spielerList.sort(spielerComparator);
		} catch (Exception ex) {
//			alertError("Probleme bei Tableau lesen.", ex);
		}
		return spielerList;
	}

	/**
	 * Für die Anzeige der Tableau
	 * 
	 * @author ruedi
	 *
	 */
	private class ComboBoxRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 8295447589224463493L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Tableau tableau = (Tableau) value;
			label.setText(tableau.getBezeichnung());
			return label;
		}
	}

	/**
	 * Refresh the List after change, will read the new List again.
	 */
	public void refreshAfterChange() {
		// bestehende Liste löschen, damit die wieder neu eingelesen werden.
		spielerData.removeAllData();
		spielerData.readAllData();
		if (selectedTableauIndex > 0) {
			// wenn ein Tableau ausgewählt, dann nur diese Spieler anzeigen
			setupSpielerList();
		}
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public int getSelectedTableauIndex() {
		return selectedTableauIndex;
	}

//	private void setSelectedSpielerId(int id) {
//		selectedSpielerId = id;
//		selectedSpielerField.setText(String.valueOf(id));
//	}

	/**
	 * Die spielerID des selektierten Spielers
	 * 
	 * @return ID, oder -1 wenn nichts selektiert
	 */
	public int getSelectedSpielerId() {
		return selectedSpielerId;
	}

	public void setBtnEnalble(boolean enable) {
		btnZusaetzlich.setEnabled(enable);
	}

//--- Model der Spieler Kurz Daten ------------------------------------

	private class SpielerTableModel extends DefaultTableModel { // AbstractTableModel {

		private static final long serialVersionUID = -8873369194794951026L;
		// Alle Namen, ist die Basis, diese wird anfänglich angezeigt, von der DB
		// gelesen
		private List<SpielerKurz> spielerAll;
		// die Spieler die von Tableau Anzeige selektiert wurden.
		private List<SpielerKurz> spielerTableau;
		// die Spieler die angezeigt werden
		private List<SpielerKurz> spielerAnzeige;

		/**
		 * Konvertiert Map in Array
		 */
		public SpielerTableModel() {
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			if (spielerAnzeige != null) {
				return spielerAnzeige.size();
			} else {
				return 0;
			}
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Name";
			}
			return "";
		}

		/*
		 * if (col == 0) return Integer.valueOf(0).getClass(); else return new
		 * String().getClass(); }
		 */

		/**
		 * Gibt den Wert an der Koordinate row / col zurück.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			SpielerKurz spk = spielerAnzeige.get(row);
			switch (col) {
			case 0:
				return spk.getName();
			}
			return "";
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// only read
		}

		@Override
		public void fireTableDataChanged() {
			// Auto-generated method stub
			super.fireTableDataChanged();
		}

		/**
		 * Die ID des Tupels von der row
		 * 
		 * @param row
		 * @return
		 */
		public int getId(int row) {
			SpielerKurz spk = spielerAnzeige.get(row);
			return spk.getId();
		}

		/**
		 * Die Anzahl Spieler in der Liste
		 * 
		 * @return
		 */
		public String getAnzahlSpieler() {
			return Integer.toString(spielerAnzeige.size());

		}

		/**
		 * Ein Spieler aus spielerAll lesen
		 * 
		 * @param spielerId
		 * @return
		 */
		public SpielerKurz getSpieler(int spielerId) {
			for (SpielerKurz spk : spielerAll) {
				if (spk.getId() == spielerId) {
					return spk;
				}
			}
			return null;
		}

		/**
		 * Alle Spieler setzen, wenn nichts übergeben
		 *
		 */
		private void setAnzeigeAllData() {
			// alte Daten löschen, wenn bereits vorhanden
			if (spielerAnzeige != null && spielerAnzeige.size() > 0) {
				spielerAnzeige.clear();
			} else {
				spielerAnzeige = new ArrayList<>();
			}
			for (SpielerKurz idName : spielerAll) {
				// add all, wenn nichts selektiert
				spielerAnzeige.add(idName);
			}
		}

		/**
		 * Wenn Tableau selektiert, wird eine Liste übergeben, alle Spieler von diesem
		 * Tableau.
		 */
		public void setTableauSpielerData(List<SpielerKurz> tableauNames) {
			// alte Daten löschen
			if (spielerTableau != null && spielerTableau.size() > 0) {
				spielerTableau.clear();
			} else {
				spielerTableau = new ArrayList<>();
			}
			for (SpielerKurz idName : tableauNames) {
				spielerTableau.add(idName);
			}
		}

		/**
		 * Spieler mit dem gesuchten Namen in die "spielerListe" schreiben
		 *
		 * @param suchName Teil eines Namens, wenn < 1 dann werden alle Namen
		 *                 übernommen.
		 */
		private void setSuchSpielerData(String suchName) {
			// alte Daten löschen
			if (spielerAnzeige != null && spielerAnzeige.size() > 0) {
				// alte Daten löschen, wenn bereits vorhanden
				spielerAnzeige.clear();
			} else {
				spielerAnzeige = new ArrayList<>();
			}
			// alle gesuchten von tableau kopieren
			for (SpielerKurz idName : spielerTableau) {
				if (suchName.length() > 0) {
					if (idName.name.toLowerCase().contains(suchName)) {
						spielerAnzeige.add(idName);
					}
				} else {
					// add all, wenn nichts selektiert
					spielerAnzeige.add(idName);
				}
			}
		}

		/**
		 * Alle daten löschen
		 */
		public void removeAllData() {
			spielerAll = null;
		}

		/**
		 * Alle Spieler von der DB lesen, ist die Basis für alle Daten
		 */
		public void readAllData() {
			// wenn Liste noch nicht vorhanden, dann einlesen
			if (spielerAll == null) {
				spielerAll = new ArrayList<>();
			}
			// Alle daten von der DB lesen
			try {
				spielerAll = SpielerData.instance().readAllKurz();
				setTableauSpielerData(spielerAll);
				setAnzeigeAllData();
			} catch (Exception ex) {
				Trace.println(3, "Fehler beim Spieler lesen. " + ex.getMessage());
			}
			fireTableDataChanged();
		}

		/**
		 * Getter
		 * 
		 * @return
		 */
		public List<SpielerKurz> getAllSpieler() {
			return spielerAll;
		}

	}
}
