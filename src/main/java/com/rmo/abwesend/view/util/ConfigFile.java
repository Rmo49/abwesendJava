package com.rmo.abwesend.view.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.rmo.abwesend.util.Config;

/**
 * Die Trace von der DB anzeigen (wer wann eingelogged hat.
 *
 * @author Ruedi
 *
 */
public class ConfigFile {

	private JPanel panel;

	private JTextArea textArea;

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
		panel.add(addTextArea(), BorderLayout.CENTER);
		return panel;
	}

	private JComponent addTopNode() {
		JPanel panel = new JPanel();
		JLabel labelTitel = new JLabel("Config von File");
		labelTitel.setFont(Config.fontTitel);
		panel.add(labelTitel);

		JButton btnShow = new JButton("Refresh");
		btnShow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTrace();
			}
		});
		panel.add(btnShow);

		return panel;
	}

	private JComponent addTextArea() {
		textArea = new JTextArea();
        textArea.setEditable(false); // Verhindert Bearbeitung
        textArea.setPreferredSize(new Dimension(150, 600));
        
		JScrollPane pane = new JScrollPane(textArea);
		panel.add(pane, BorderLayout.CENTER);
		panel.revalidate();
		panel.repaint();
		return pane;
	}


	private void refreshTrace() {
        // Pfad zur Textdatei
        String fileName = Config.sPath + "/" + Config.sConfigFileName;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder inhalt = new StringBuilder();
            
            String zeile;
            zeile = "ConfigFile Pfad  : " + fileName;
            inhalt.append(zeile).append("\n");

            // die DB einstellung
            zeile = "Aktuelle Einstellung zur DB";
            inhalt.append(zeile).append("\n");
            zeile = "dbUrl: " + Config.dbUrl;
            inhalt.append(zeile).append("\n");
            zeile = "dbPort: " + Config.dbPort;
            inhalt.append(zeile).append("\n");
            zeile = "dbName: " + Config.dbName;
            inhalt.append(zeile).append("\n");
                  
            while ((zeile = reader.readLine()) != null) {
                inhalt.append(zeile).append("\n");
            }
            textArea.setText(inhalt.toString());
            
        } catch (IOException e) {
            textArea.setText("Fehler beim Lesen der Datei: " + e.getMessage());
        }

	}

}
