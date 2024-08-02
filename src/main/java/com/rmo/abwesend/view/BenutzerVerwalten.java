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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.rmo.abwesend.model.Benutzer;
import com.rmo.abwesend.model.BenutzerData;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.util.Trace;
import com.rmo.abwesend.view.util.CmUtil;
import com.rmo.abwesend.view.util.SpringUtilities;

/**
 * Die User-Werte werden in einer Tabelle dargestellt.
 *
 * @author Ruedi
 *
 */
public class BenutzerVerwalten extends BasePane implements TableModelListener {

//	private Dimension EINGABE_FELD = new Dimension(400, 20);
	// die daten der Tabelle
	private UserTableModel userTableData = new UserTableModel();
	private JTable userTable = new JTable(userTableData);
	private JScrollPane scrollPane;

//	private int selectedRow;
	private JTextField addName;
	private JTextField addPasswort;

	/**
	 * Der Start des Panels.
	 *
	 * @return initialisierte Panle
	 */
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel labelTitel = new javax.swing.JLabel("Alle Benutzer mit ihrem Paswort");
		labelTitel.setFont(Config.fontTitel);
		panel.add(labelTitel, BorderLayout.PAGE_START);

		panel.add(addTopButtons(), BorderLayout.PAGE_START);
		panel.add(addUserTable(), BorderLayout.CENTER);
		panel.add(addEntryForm(), BorderLayout.PAGE_END);
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
				int row = userTable.getSelectedRow();
				// ID sichern
				// Eingabefelderfüllen
				addName.setText((String) userTable.getValueAt(row, 0));
				addPasswort.setText((String) userTable.getValueAt(row, 1));
			}
		});

		btnLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = userTable.getSelectedRow();
				try {
					String name = (String) userTable.getValueAt(row, 0);
					BenutzerData.instance().delete(name);
				} catch (Exception ex) {
					alertError("Löschen von Benutzer", ex);
				}
				userTableData.readAllData();
				scrollPane.repaint();
			}
		});

		btnSpeichernAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userTableData.saveAll();
			}
		});

		return listPane;
	}

	/**
	 * Die Tabellle mit allen Bendutzern.
	 * 
	 * @return
	 */
	private JComponent addUserTable() {
		scrollPane = new JScrollPane(userTable);

		userTable.setFillsViewportHeight(true);
		TableColumnModel columnModel = userTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(150);
		columnModel.getColumn(1).setPreferredWidth(150);

		addTableListener(userTable);
		userTableData.addTableModelListener(this);

		return scrollPane;
	}

	/**
	 * Die Entry Form für ein neues Tableau
	 *
	 * @return
	 */
	private JComponent addEntryForm() {
		JPanel panelEntry = new JPanel(new SpringLayout());
		// zum einfügen in die Tabelle
		JLabel label1 = new JLabel("Name");
		panelEntry.add(label1);
		JLabel label2 = new JLabel("Passwort");
		panelEntry.add(label2);

		addName = new JTextField();
		addName.setPreferredSize(new Dimension(150, Config.textFieldHeigth));
		panelEntry.add(addName);

		addPasswort = new JTextField();
		addPasswort.setPreferredSize(new Dimension(150, Config.textFieldHeigth));
		panelEntry.add(addPasswort);

		SpringUtilities.makeGrid(panelEntry, 2, 2, 5, 0, 5, 0);

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(panelEntry);

		final JButton btnSpeichern = new JButton("Speichern");
		panel.add(btnSpeichern);

		btnSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});

		return panel;
	}

	/**
	 * Listener der Tabelle zuordnen
	 *
	 * @param spielerTable
	 */
	private void addTableListener(JTable table) {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				int viewRow = table.getSelectedRow();
				if (viewRow >= 0) {
					// den Wert zum ändern übertragen
//					int selectedRow = viewRow;
//					String wert = (String) userTableData.getValueAt(selectedRow, 1);
//					userWert.setText(wert);
				}
			}
		});
	}

	/**
	 * Der Inhalt des Models hat sich geändert.
	 * 
	 * @param arg0
	 */
	@Override
	public void tableChanged(TableModelEvent arg0) {
		scrollPane.repaint();
	}

	/**
	 * Speichern (oder ändern) des eingegebenen Wertes
	 */
	@Override
	protected void saveData() {
		if (CmUtil.passwordOk()) {
			if ((addName.getText().length() > 3) && (addPasswort.getText().length() > 3)) {
				Benutzer benutzer = new Benutzer(addName.getText(), addPasswort.getText());
				userTableData.addData(benutzer);

				// nach speichern alles zurücksetzen
				addName.setText("");
				addPasswort.setText("");
			} else {
				CmUtil.alertWarning("Benutzer speichern", "Daten zu kurz");
			}

//			userTable.setValueAt(userWert.getText(), selectedRow, 1);
//			userTable.repaint();
//			scrollPane.repaint();
//			userTableData.saveAll();
		} else {
			CmUtil.alertWarning("Benutzer speichern", "Passwort falsch!");
		}
	}

//--- Model der User Daten--------------------------------
	private class UserTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -3737621216184526263L;

		private List<Benutzer> benutzerList = new ArrayList<>();

		/**
		 * Konstruktor, alle Daten lesen
		 */
		public UserTableModel() {
			readAllData();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return benutzerList.size();
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Name";
			case 1:
				return "Passwort";
			}
			return "";
		}

		/**
		 * Gibt den Wert an der Koordinate row / col zurück.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			Benutzer lBenutzer = benutzerList.get(row);
			switch (col) {
			case 0:
				return lBenutzer.getName();
			case 1:
				return lBenutzer.getPasswort();
			}
			return "";
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// Auto-generated method stub
			Benutzer lBenutzer = benutzerList.get(rowIndex);
			if (columnIndex == 0) {
				lBenutzer.setName((String) aValue);
			}
			if (columnIndex == 1) {
				lBenutzer.setPasswort((String) aValue);
			}
		}

		/**
		 * Einen Benutzer dauzufürgen
		 * 
		 * @param benuter
		 */
		public void addData(Benutzer benutzer) {
			try {
				BenutzerData.instance().add(benutzer);
			} catch (Exception ex) {
				alertError("Benutzer dazufügen: Fehler beim Speichern", ex);
			}
			readAllData();
		}

		/**
		 * Alle Spieler von der DB lesen, ist die Basis für alle Daten
		 */
		public void readAllData() {
			// Liste zuerst,löschen
			if (benutzerList != null) {
				benutzerList = null;
			}
			benutzerList = new ArrayList<>();
			// Alle daten von der DB lesen
			try {
				benutzerList = BenutzerData.instance().readAll(benutzerList);
			} catch (Exception ex) {
				Trace.println(3, "Fehler beim User lesen. " + ex.getMessage());
			}
			fireTableDataChanged();
		}

		/**
		 * In der DB sichern
		 */
		public void saveAll() {
			// sichern in der DB
			try {
				// zuerst alles löschen
				BenutzerData.instance().deleteAll();
				// alle Werte in die DB schreiben
				BenutzerData.instance().addAll(benutzerList);

			} catch (Exception ex) {
				Trace.println(1, ex.getMessage());
			}
		}

	}
}
