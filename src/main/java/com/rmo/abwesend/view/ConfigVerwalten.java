package com.rmo.abwesend.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.Trace;
import com.rmo.abwesend.view.util.CmUtil;

/**
 * Die Config-Werte werden in einer Tabelle dargestellt.
 * @author Ruedi
 *
 */
public class ConfigVerwalten extends BasePane {

	private Dimension EINGABE_FELD = new Dimension(400, 20);
	// die daten der Tabelle
	private ConfigTableModel configTableData = new ConfigTableModel();
	private JTable configTable = new JTable(configTableData);

	private int selectedRow;
	private JTextField configWert;

	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel labelTitel = new javax.swing.JLabel("Steuer-Werte der Applikation, die für alle Benutzer gelten.");
		labelTitel.setFont(Config.fontTitel);
		panel.add(labelTitel, BorderLayout.PAGE_START);

		panel.add(addConfigTable(), BorderLayout.CENTER);
		panel.add(addConfigEnter(), BorderLayout.PAGE_END);
		return panel;
	}

	private JComponent addConfigTable() {
		JScrollPane scrollPane = new JScrollPane(configTable);

		configTable.setFillsViewportHeight(true);
		TableColumnModel columnModel = configTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(150);
		columnModel.getColumn(1).setPreferredWidth(600);

		addTableListener(configTable);
		return scrollPane;
	}

	/**
	 * Listener der Tabelle zuordnen
	 * @param spielerTable
	 */
	private void addTableListener(JTable table) {
		table.getSelectionModel().addListSelectionListener(
		        new ListSelectionListener() {
		            @Override
					public void valueChanged(ListSelectionEvent event) {
		                int viewRow = table.getSelectedRow();
		                if (viewRow >= 0) {
		                	// den Wert zum ändern übertragen
		                	selectedRow = viewRow;
		                	String wert = (String) configTableData.getValueAt(selectedRow, 1);
							configWert.setText(wert);
		                }
		            }
		        }
		);
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
		if (CmUtil.passwordOk()) {
			configTable.setValueAt(configWert.getText(), selectedRow, 1);
			configTable.repaint();
			configTableData.saveAll();
		}
		else {
			CmUtil.alertWarning("Wert speichern", "Passwort falsch!");
		}
	}


//--- Model der Config Daten--------------------------------
	private class ConfigTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -3737621216184526263L;

		private Map<String, String> keyValueMap;
		private String[] keys;
		private String[] values;

		/**
		 * Konvertiert Map in Array
		 */
		public ConfigTableModel() {
			keyValueMap = Config.getKeyValueMap();
			keys 	= new String[keyValueMap.size()];
			values 	= new String[keyValueMap.size()];
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
				Config.getKeyValueMap().put(keys[i], values[i]);
			}
			try {
				Config.saveConfigData();
			} catch (Exception ex) {
				Trace.println(1, ex.getMessage());
			}
		}

	}


}
