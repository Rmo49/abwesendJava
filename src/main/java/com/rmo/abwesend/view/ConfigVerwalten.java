package com.rmo.abwesend.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.Trace;

/**
 * Die Config-Werte werden in einer Tabelle dargestellt.
 *
 * @author Ruedi
 *
 */
public class ConfigVerwalten extends BasePane {

	private final int COL1WIDTH = 150;
	private final int COL2WIDTH = 600;
	private Dimension EINGABE_FELD = new Dimension(400, 20);
	// die daten der Tabelle
	private ConfigTableModel configTableData = new ConfigTableModel();
	private JTable configTable = new JTable(configTableData);

	// die Tabelle für die Zeiten pro Tag
	private ZeitTableModel zeitTableData;
	private JTable zeitTable;
	// Datumsformat für die Anzeige
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("E d.M.");


	private int selectedRowConfig = -1;
	private int selectedRowZeit = -1;
	private int selectedColZeit = -1;
	private JTextField configWert;

	/*
	 * Der Panel, wir von dem Parent aufgerufen
	 */
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel labelTitel = new javax.swing.JLabel("Steuer-Werte der Applikation, die für alle Benutzer gelten.");
		labelTitel.setFont(Config.fontTitel);
		panel.add(labelTitel, BorderLayout.PAGE_START);

		panel.add(addTables(), BorderLayout.CENTER);
		panel.add(addConfigEnter(), BorderLayout.PAGE_END);
		return panel;
	}

	/**
	 * Initialisiert die beiden Tabellen
	 *
	 * @return
	 */
	private JComponent addTables() {
		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.add(addConfigTable());
		listPane.add(addZeitTable());
		return listPane;
	}

	/**
	 * Die Configwert von der DB
	 *
	 * @return
	 */
	private JComponent addConfigTable() {
		JScrollPane scrollPane = new JScrollPane(configTable);

		configTable.setFillsViewportHeight(true);
		TableColumnModel columnModel = configTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(COL1WIDTH);
		columnModel.getColumn(1).setPreferredWidth(COL2WIDTH);

		addListenerConfigTable(configTable);
		return scrollPane;
	}

	/**
	 * Die Tabelle mit den Zeitangaben
	 *
	 * @return
	 */
	private JComponent addZeitTable() {
		zeitTableData = new ZeitTableModel();
		zeitTable = new JTable(zeitTableData);
		JScrollPane scrollPane = new JScrollPane(zeitTable);

		zeitTable.setFillsViewportHeight(true);
		addListenerZeitTable(zeitTable);

		return scrollPane;
	}

	/**
	 * Listener der Config-Tabelle, setzt wert in das Feld zum ändern
	 *
	 * @param spielerTable
	 */
	private void addListenerConfigTable(JTable table) {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				int viewRow = table.getSelectedRow();
				if (viewRow >= 0) {
					// den Wert zum ändern übertragen
					selectedRowZeit = -1;
					selectedRowConfig = viewRow;
					String wert = (String) configTableData.getValueAt(selectedRowConfig, 1);
					configWert.setText(wert);
				}
			}
		});
	}

	/**
	 * Listener der Zeit-Tabelle, setzt wert in das Feld zum ändern
	 *
	 * @param spielerTable
	 */
	private void addListenerZeitTable(JTable table) {
		table.setCellSelectionEnabled(true);
	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				handleZeitEvent(table, event);
			}
		});

	    // Muss auch ColumnModel mit Listener, wenn auf gleicher Zeile selektiert
	    // dann wird kein Listener aufgerufen
	    table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				handleZeitEvent(table, event);
			}
	    });
	}

	/**
	 * Wenn Listener aufgerufen, werden hier Werte gesetzt
	 * @param event
	 */
	private void handleZeitEvent(JTable table, ListSelectionEvent event) {
		selectedRowZeit = table.getSelectedRow();
		selectedColZeit = table.getSelectedColumn();
		if (selectedRowZeit >= 0) {
			// den Wert zum ändern übertragen
			selectedRowConfig = -1;
			String wert = "0";
			if (selectedRowZeit == 0) {
				wert = String.valueOf(Config.zeitStart[selectedColZeit]);
			}
			if (selectedRowZeit == 1) {
				wert = String.valueOf(Config.zeitEnde[selectedColZeit]);
			}
			configWert.setText(wert);
		}
	}


	/**
	 * Textfeld und JButton um die Aenderungen zu speichern.
	 *
	 * @return
	 */
	private JComponent addConfigEnter() {
		JPanel panel = new JPanel(new FlowLayout());
		JLabel label = new JLabel("Config Wert ändern");
		panel.add(label);

		configWert = new JTextField();
		configWert.setPreferredSize(EINGABE_FELD);
		panel.add(configWert);

		JButton btnSpeichern = new JButton("Speichern");
		panel.add(btnSpeichern);

		btnSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});
		panel.add(btnSpeichern);
		return panel;
	}

	/**
	 * Speichern (oder ändern) des eingegebenen Wertes
	 */
	@Override
	protected void saveData() {
//		if (CmUtil.passwordOk()) {
			if (selectedRowConfig >= 0) {
				configTable.setValueAt(configWert.getText(), selectedRowConfig, 1);
				configTable.repaint();
			}
			if (selectedRowZeit >= 0) {
				setZeitWerte(Integer.valueOf(configWert.getText()), selectedRowZeit, selectedColZeit);
				zeitTable.repaint();
			}
			configTableData.saveAll();
//		} else {
//			CmUtil.alertWarning("Wert speichern", "Passwort falsch!");
//		}
	}

	/**
	 * Die Werte in der Config speichern.
	 * @param wert
	 * @param row
	 * @param col
	 */
	private void setZeitWerte(int wert, int row, int col) {
		if (row == 0) {
			Config.zeitStart[col] = wert;
		}
		if (row == 1) {
			Config.zeitEnde[col] = wert;
		}
	}

//------------------------------------------------
	/*
	 * Model der Config Daten
	 */
	private class ConfigTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -3737621216184526263L;

		private Map<String, String> keyValueMap;
		private String[] keys;
		private String[] values;

		/**
		 * Konvertiert Map in Arrays (keys, values) die lokal gehalten werden.
		 */
		public ConfigTableModel() {
			keyValueMap = Config.getKeyValueMap();
			keys = new String[keyValueMap.size()];
			values = new String[keyValueMap.size()];
			int index = 0;
			for (Map.Entry<String, String> mapEntry : keyValueMap.entrySet()) {
				keys[index] = mapEntry.getKey();
				values[index] = mapEntry.getValue();
				index++;
			}
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return keyValueMap.size();
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Key";
			case 1:
				return "Value";
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
			switch (col) {
			case 0:
				return keys[row];
			case 1:
				return values[row];
			}
			return "";
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// Auto-generated method stub
			if (columnIndex == 0) {
				keys[rowIndex] = (String) aValue;
			}
			if (columnIndex == 1) {
				values[rowIndex] = (String) aValue;
			}
		}

		/**
		 * In der DB sichern
		 */
		public void saveAll() {
			// sichern in der DB
			Config.getKeyValueMap().clear();

			for (int i = 0; i < keys.length; i++) {
				if (keys[i] == Config.zeitStartKey) {
					values[i] = getZeitStartStr();
				}
				if (keys[i] == Config.zeitEndeKey) {
					values[i] = getZeitEndeStr();
				}
				Config.getKeyValueMap().put(keys[i], values[i]);
			}
			try {
				Config.saveConfigData();
			} catch (Exception ex) {
				Trace.println(1, ex.getMessage());
			}
		}
	}

	/**
	 * Den Array in einen String konvertieren
	 * @return
	 */
	private String getZeitStartStr() {
		StringBuffer buffer = new StringBuffer(60);
		for (int i : Config.zeitStart) {
			buffer.append(i);
			buffer.append(";");
		}
		return buffer.toString();
	}

	private String getZeitEndeStr() {
		StringBuffer buffer = new StringBuffer(60);
		for (int i : Config.zeitEnde) {
			buffer.append(i);
			buffer.append(";");
		}
		return buffer.toString();
	}



	// -----------------------------------------------------
	/*
	 * Die Tabellendaten für die Zeiteingaben pro Tag
	 */
	private class ZeitTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 5830419333659899060L;

		private String[] header;

		public ZeitTableModel() {
			super();
			header = initHeader();
		}

		private String[] initHeader() {
			header = new String[Config.turnierMaxTage];
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(Config.turnierBeginDatum);
			calendar.setLenient(true);
			for (int i = 0; i < header.length; i++) {
				header[i] = dateFormat.format(calendar.getTime());
				// einen Tag weiter
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			return header;
		}

		@Override
		public String getColumnName(int column) {
			return header[column];
		}

		@Override
		public int getColumnCount() {
			return Config.turnierMaxTage;
		}

		@Override
		public int getRowCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int row, int col) {
			switch (row) {
			case 0:
				return Config.zeitStart[col];
			case 1:
				return Config.zeitEnde[col];
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			setZeitWerte((int) aValue, rowIndex, columnIndex);
		}

	}

}
