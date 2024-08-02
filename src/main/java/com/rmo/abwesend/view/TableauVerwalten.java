package com.rmo.abwesend.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.view.util.SpringUtilities;

/**
 * Die Tableaux-Liste verwalten, CRUD-Buttons anzeigen und entsprechende
 * Aktionen.
 * 
 * @author Ruedi
 *
 */
public class TableauVerwalten extends BasePane {

	// die daten der Tabelle
	private TableauTableModel tableauTableData;
	private JTable tableauTable;
	private Tableau tableauAnzeige;
	private int tempId = -1;
	private JTextField addBezeichnung;
	private JTextField addPosition;
	private JTextField addKonkurrenz;
	private JScrollPane tableScrollPane;

	public TableauVerwalten() {
	}

	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(addTopButtons(), BorderLayout.PAGE_START);
		panel.add(addTableauTable(), BorderLayout.CENTER);
		panel.add(addEntryForm(), BorderLayout.PAGE_END);
		return panel;
	}

	/**
	 * Die Anzeige aller Tableaux als Tabelle mit Position in der Liste.
	 * 
	 * @return
	 */
	private JComponent addTableauTable() {
		tableauTableData = new TableauTableModel();
		tableauTable = new JTable(tableauTableData);
		tableScrollPane = new JScrollPane(tableauTable);
		tableauTable.setFillsViewportHeight(true);

		TableColumnModel columnModel = tableauTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(20);
		columnModel.getColumn(1).setPreferredWidth(150);
		columnModel.getColumn(2).setPreferredWidth(150);
		columnModel.getColumn(3).setPreferredWidth(20);

		// Daten einlesen
		tableauTableData.readAllData();
		return tableScrollPane;
	}

	/**
	 * Die Entry Form für ein neues Tableau
	 * 
	 * @return
	 */
	private JComponent addEntryForm() {
		JPanel panelEntry = new JPanel(new SpringLayout());
		// zum einfügen in die Tabelle
		JLabel label1 = new JLabel("Position");
		panelEntry.add(label1);
		JLabel label2 = new JLabel("Bezeichnung");
		panelEntry.add(label2);
		JLabel label3 = new JLabel("Konkurrenz (SwissTennis)");
		panelEntry.add(label3);

		addPosition = new JTextField();
		addPosition.setPreferredSize(new Dimension(30, Config.textFieldHeigth));
		addPosition.setMaximumSize(new Dimension(30, Config.textFieldHeigth));
		panelEntry.add(addPosition);

		addBezeichnung = new JTextField();
		addBezeichnung.setPreferredSize(new Dimension(150, Config.textFieldHeigth));
		panelEntry.add(addBezeichnung);

		addKonkurrenz = new JTextField();
		addKonkurrenz.setPreferredSize(new Dimension(200, Config.textFieldHeigth));
		addKonkurrenz.setMinimumSize(new Dimension(200, Config.textFieldHeigth));

		panelEntry.add(addKonkurrenz);

		SpringUtilities.makeGrid(panelEntry, 2, 3, 5, 0, 5, 0);

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(panelEntry);

		final JButton btnSpeichern = new JButton("Speichern");
		panel.add(btnSpeichern);

		btnSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tableauAnzeige == null) {
					tableauAnzeige = new Tableau();
				}
				tableauAnzeige.setId(tempId);
				tableauAnzeige.setPosition(addPosition.getText());
				tableauAnzeige.setBezeichnung(addBezeichnung.getText());
				tableauAnzeige.setKonkurrenz(addKonkurrenz.getText());
				tableauTableData.add(tableauAnzeige);

				// nach speichern alles zurücksetzen
				tempId = -1;
				addPosition.setText("");
				addBezeichnung.setText("");
				addKonkurrenz.setText("");
				tableScrollPane.repaint();
			}
		});

		return panel;
	}

	/**
	 * Textfeld und JButton um die Aenderungen zu speichern.
	 * 
	 * @return
	 */
	private JComponent addTopButtons() {
		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

		JPanel panel1 = new JPanel();
		JLabel label = new JLabel("Eintrag zuerst selektieren, dann löschen");
		panel1.add(label);
		listPane.add(panel1);

		JPanel panel2 = new JPanel();
		JButton btnAendern = new JButton("Ändern");
		panel2.add(btnAendern);

		JButton btnLoeschen = new JButton("Löschen");
		panel2.add(btnLoeschen);

		JButton btnSpeichernAll = new JButton("Alles Speichern");
		panel2.add(btnSpeichernAll);
		listPane.add(panel2);

		btnAendern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = tableauTable.getSelectedRow();
				// ID sichern
				tempId = tableauTableData.getIdAt(row);
				// Eingabefelderfüllen
				addPosition.setText((String) tableauTableData.getValueAt(row, 0));
				addBezeichnung.setText((String) tableauTableData.getValueAt(row, 1));
				addKonkurrenz.setText((String) tableauTableData.getValueAt(row, 2));
			}
		});

		btnLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = tableauTable.getSelectedRow();
				try {
					TableauData.instance().delete(tableauTableData.getIdAt(row));
				} catch (Exception ex) {
					alertError("Löschen von Tableau", ex);
				}
				tableauTableData.readAllData();
				tableScrollPane.repaint();
			}
		});

		btnSpeichernAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableauTableData.saveAll();
			}
		});

		return listPane;
	}

	/**
	 * Speichern (oder ändern) des eingegebenen Wertes
	 */
	@Override
	protected void saveData() {
		tableauTableData.saveAll();
	}

//--- Model der Tableau Daten ------------------------------------

	private class TableauTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -3737621216184523363L;

		private List<Tableau> tableauListe;

		/**
		 * Konvertiert Map in Array
		 */
		public TableauTableModel() {
//			this.addTableModelListener(new MyListener());
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return tableauListe.size();
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Position";
			case 1:
				return "Bezeichnung";
			case 2:
				return "Konkurrenz (Swiss Tennis)";
			case 3:
				return "ID";
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
			Tableau t = tableauListe.get(row);
			switch (col) {
			case 0:
				return t.getPosition();
			case 1:
				return t.getBezeichnung();
			case 2:
				return t.getKonkurrenz();
			case 3:
				return t.getId();

			}
			return "";
		}

		/**
		 * Die ID ist nicht veränderbar.
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex > 0) {
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Tableau t = tableauListe.get(rowIndex);
			if (columnIndex == 1) {
				t.setPosition((String) aValue);
			}
			if (columnIndex == 2) {
				t.setBezeichnung((String) aValue);
			}
			if (columnIndex == 3) {
				t.setKonkurrenz((String) aValue);
			}
		}

		/**
		 * Gibt die ID der zeile zurück
		 * 
		 * @return
		 */
		public int getIdAt(int row) {
			if (row >= 0) {
				Tableau tableau = tableauListe.get(row);
				return tableau.getId();
			}
			return row;
		}

		/**
		 * Alle daten (nochmals) einlesen
		 */
		public void readAllData() {
			try {
				if (tableauListe != null) {
					tableauListe = null;
				}
				tableauListe = new ArrayList<>(); // statt remove
				tableauListe.addAll(TableauData.instance().readAllTableau());
			} catch (Exception ex) {
				alertError("ReadAll Tableau", ex);
			}
		}

		/**
		 * Einen neue Eintrag dazufügen
		 * 
		 * @param tableau
		 */
		public void add(Tableau tableau) {
			try {
				TableauData.instance().add(tableau);
				readAllData();
			} catch (Exception ex) {
				alertError("Add Tableau", ex);
			}
		}

		/**
		 * In der DB sichern
		 */
		public void saveAll() {
			// sichern in der DB
			try {
				for (Tableau element : tableauListe) {
					TableauData.instance().add(element);
				}
			} catch (Exception ex) {
				alertError("SaveAll Tableau", ex);
			}
		}

	}

}
