package com.rmo.abwesend.view.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.rmo.abwesend.model.TraceDb;
import com.rmo.abwesend.model.TraceDbData;
import com.rmo.abwesend.util.Config;

/**
 * Die Trace von der DB anzeigen (wer wann eingelogged hat.
 * 
 * @author Ruedi
 *
 */
public class UserDbAnzeigen {

	private JPanel panel;
	private TraceTableModel traceModel;
	private JTable traceTable;

	private JFormattedTextField abDatum;

//	public UserDbAnzeigen(MainFrame main) {
//		mainFrame = main;
//	}

	/**
	 * Der Panel mit allen Infos.
	 * 
	 * @return
	 */
	public JPanel getPanel() {
		panel = new JPanel(new BorderLayout());
		panel.add(addTopNode(), BorderLayout.PAGE_START);
//		panel.add(addTraceTable(), BorderLayout.CENTER);
		return panel;
	}

	private JComponent addTopNode() {
		JPanel panel = new JPanel();
		JLabel labelTitel = new JLabel("Wer wann eingelogged hat.  ");
		labelTitel.setFont(Config.fontTitel);
		panel.add(labelTitel);

		panel.add(new JLabel("  ab Datum: "));

		abDatum = new JFormattedTextField(Config.sdfDatum);
		abDatum.setPreferredSize(Config.datumFeldSize);
		abDatum.setMaximumSize(Config.datumFeldSize);

		LocalDate now = LocalDate.now();
		now = now.minusDays(10);
		Date date = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
		abDatum.setValue(date);
		panel.add(abDatum);

		JButton btnShow = new JButton("Anzeigen");
		btnShow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addTraceTable();
			}
		});
		panel.add(btnShow);

		return panel;
	}

	private void addTraceTable() {
		traceModel = new TraceTableModel();
		traceModel.addAllData(abDatum);

		traceTable = new JTable(traceModel);
		TableColumnModel columnModel = traceTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(150);
		columnModel.getColumn(1).setPreferredWidth(600);

		JScrollPane pane = new JScrollPane(traceTable);
		panel.add(pane, BorderLayout.CENTER);
		panel.revalidate();
		panel.repaint();
	}

//-----------------------------------------------

	class TraceTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -4908347092194566862L;

		// die daten der Tabelle
		private List<TraceDb> traceList = null;

		@Override
		public int getRowCount() {
			return traceList.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TraceDb trace = traceList.get(rowIndex);
			if (columnIndex == 0) {
				return trace.getDatum();
			} else {
				return trace.getWert();
			}
		}

		/**
		 * Alle Zeilen einlesen
		 */
		public void addAllData(JFormattedTextField abDatum) {
			// data
			Date abDate = (Date) abDatum.getValue();
			try {
				traceList = TraceDbData.instance().readAll(abDate);
			} catch (Exception ex) {
				CmUtil.alertError("TraceDb", ex.getMessage());
			}
		}

	}
}
