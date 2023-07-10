package com.rmo.abwesend.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.rmo.abwesend.model.DbConnection;
import com.rmo.abwesend.model.TennisDataBase;
import com.rmo.abwesend.util.Config;
import com.rmo.abwesend.view.util.DbActions;
import com.rmo.abwesend.view.util.MailVersenden;
import com.rmo.abwesend.view.util.MailVersendenSwt;
import com.rmo.abwesend.view.util.SpielerExport;
import com.rmo.abwesend.view.util.SpielerImport;
import com.rmo.abwesend.view.util.SpielerSelektieren;
import com.rmo.abwesend.view.util.TraceDbAnzeigen;
import com.rmo.abwesend.view.util.VonBisDatum;

/**
 * Das zentrale Frame, das alles zusammenhält.
 *
 * @author ruedi
 *
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = -6961823307482158632L;
	private MainFrame mainFrame;
	private SpielerSelektieren mSpielerSelect = null;
	private VonBisDatum vonBisDatum = null;

	private JPanel panelLeft = null; // die linke Seite mit Spieler oder anderes Menu
	private JComponent panelCenter = null; // das Zentrum
	// Anzeige der Abwesenheiten, wenn schon existiert wird Spieler angehängt
	private AbwesendSpieler mAbwAnzeigen = null;

	private JMenuBar menuBar;
	private JMenuItem menuAbwAnzeigen;
	private JMenuItem menuAbwTableau;
	private JMenuItem menuAbwEintagen;
	private JMenuItem menuSpielerTableau;
	private JMenuItem menuMatchEinlesen;
	private JMenuItem menuTurnierPage;
	private JMenu menuSetup;

	public MainFrame(String title) {
		initView(title);
		mainFrame = this;
		setEnable();
	}

	/**
	 * Den Hauptscreen aufbauen
	 *
	 * @param primaryStage
	 */
	public void initView(String title) {
//		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle(title);

		// das Menu
//		this.getContentPane().add(BorderLayout.PAGE_START, initMenu());
		this.setJMenuBar(initMenuBar());

		if (TennisDataBase.dbExists()) {
			// den linke Panel mit den Spielern
			if (DbConnection.isConnected()) {
				mSpielerSelect = new SpielerSelektieren(this);
				setPaneLeft(mSpielerSelect.getPanel());
			}

			// das Zentrum
			mAbwAnzeigen = new AbwesendSpieler();
			// this.getContentPane().add(mAbwAnzeigen.getPanel(), BorderLayout.CENTER);

			// den Bereich unten
			vonBisDatum = new VonBisDatum();
			this.getContentPane().add(vonBisDatum.getPanel(this), BorderLayout.PAGE_END);
		}

		// --- Size und Location of Screen
		Dimension dim = new Dimension();
		dim.setSize(Config.windowWidth, Config.windowHeight);
		this.setSize(dim);
		this.setPreferredSize(dim);
		Point p = new Point();
		p.setLocation(Config.windowX, Config.windowY);
		this.setLocation(p);
	}

	/**
	 * Das Hauptmenu anzeigen, mit Action Listener
	 *
	 * @return
	 */
	private JMenuBar initMenuBar() {
		menuBar = new JMenuBar();

		menuAbwAnzeigen = new JMenuItem("Spieler anzeigen");
		menuAbwAnzeigen.setBackground(Config.colorSpieler);
		menuAbwAnzeigen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		menuAbwAnzeigen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaneLeft(mSpielerSelect.getPanel());
				mAbwAnzeigen.addShowSpielerFirst(mSpielerSelect.getSelectedSpielerId(), false);
				setPaneCenter(mAbwAnzeigen.getPanel());
			}
		});
		menuBar.add(menuAbwAnzeigen);

		menuAbwTableau = new JMenuItem("Tableau anzeigen");
		menuAbwTableau.setToolTipText("Alle Spieler eines Tableau anzeigen");
		menuAbwTableau.setBackground(Config.colorTable);
		menuAbwTableau.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		menuAbwTableau.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaneLeft(mSpielerSelect.getPanel());
				AbwesendTableau abwTableau = new AbwesendTableau(mSpielerSelect);
				if (mSpielerSelect.getSelectedTableauIndex() >= 0) {
					abwTableau.showTableauAbwesend(mSpielerSelect.getSelectedTableauIndex());
					setPaneCenter(abwTableau.getPanel());
				}
			}
		});
		menuBar.add(menuAbwTableau);

		menuAbwEintagen = new JMenuItem("Abwesend eintragen");
		menuAbwEintagen.setToolTipText("Abwesenheiten eines Spielers eintragen");
		menuAbwEintagen.setBackground(Config.colorSpieler);
		menuAbwEintagen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		menuAbwEintagen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				setPaneLeft(mSpielerSelect.getPanel());
				AbwesendEintragen abwEintragen = new AbwesendEintragen(mSpielerSelect, mainFrame);
				if (abwEintragen.addShowSpieler(mSpielerSelect.getSelectedSpielerId())) {
					setPaneCenter(abwEintragen.getPanel());
				}
			}
		});
		menuBar.add(menuAbwEintagen);

		menuSpielerTableau = new JMenuItem("Spieler verwalten");
		menuSpielerTableau.setToolTipText(
				"Tableau eines Spielers selektieren, \nNeue Spieler aufnehmen, Name des Spielers ändern, Spieler löschen");
		menuSpielerTableau.setBackground(Config.colorSpieler);
		menuSpielerTableau.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		menuSpielerTableau.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				setPaneLeft(mSpielerSelect.getPanel());
				SpielerVerwalten spVerwalten = new SpielerVerwalten(mSpielerSelect);
				setPaneCenter(spVerwalten.getPanel());
			}
		});
		menuBar.add(menuSpielerTableau);

		menuTurnierPage = new JMenuItem("TCA Clubturnier");
		menuTurnierPage.setToolTipText("Die Seite von Swisstennis aufrufen");
		menuTurnierPage.setBackground(Config.colorTCA);
		menuTurnierPage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		menuTurnierPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SwissTennisLesen(mainFrame).startSeiteLesen();
			}
		});
		menuBar.add(menuTurnierPage);

		// --------- Setup menu ----------------------------------------------------
		menuSetup = new JMenu("Setup");

		menuMatchEinlesen = new JMenuItem("Spiele laden");
		menuMatchEinlesen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwissTennisLesen spieleEinlesen = new SwissTennisLesen(mainFrame);
				setPaneCenter(spieleEinlesen.getPanel());
			}
		});
		menuSetup.add(menuMatchEinlesen);

		JMenuItem menuMailVersenden = new JMenuItem("Mail versenden");
		menuMailVersenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MailVersenden mailVersenden = new MailVersenden(mainFrame);
				setPaneCenter(mailVersenden.getPanel());
			}
		});
		menuSetup.add(menuMailVersenden);

		JMenuItem menuMailVersendenSwt = new JMenuItem("Mail versenden SWT");
		menuMailVersendenSwt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Display display = new Display();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(1, false));
				MailVersendenSwt mailSwt = new MailVersendenSwt(shell, SWT.NONE);
				shell.pack();
				shell.open();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				display.dispose();
			}
		});
		menuSetup.add(menuMailVersendenSwt);

		JMenuItem menuTableauVerwalten = new JMenuItem("Tableaux verwalten");
		menuTableauVerwalten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TableauVerwalten tabVerwalten = new TableauVerwalten();
				setPaneCenter(tabVerwalten.getPanel());
			}
		});
		menuSetup.add(menuTableauVerwalten);

		JMenuItem menuSpielerEinlesen = new JMenuItem("Daten einlesen");
		menuSpielerEinlesen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpielerImport spielerEinlesen = new SpielerImport();
				setPaneCenter(spielerEinlesen.getPanel());
			}
		});
		menuSetup.add(menuSpielerEinlesen);

		JMenuItem menuUserVerwalten = new JMenuItem("Benutzer verwalten ");
		menuUserVerwalten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BenutzerVerwalten userVerwalten = new BenutzerVerwalten();
				setPaneCenter(userVerwalten.getPanel());
			}
		});
		menuSetup.add(menuUserVerwalten);

		JMenuItem menuSpielerExport = new JMenuItem("Spieler exportieren");
		menuSpielerExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpielerExport spielerExport = new SpielerExport();
				setPaneCenter(spielerExport.getPanel());
			}
		});
		menuSetup.add(menuSpielerExport);

		JMenuItem menuDbActions = new JMenuItem("Datenbank");
		menuDbActions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DbActions dbActions = new DbActions();
				setPaneCenter(dbActions.getPanel());
			}
		});
		menuSetup.add(menuDbActions);

		JMenuItem menuConfig = new JMenuItem("Config data");
		menuConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigVerwalten configVerwalten = new ConfigVerwalten();
				setPaneCenter(configVerwalten.getPanel());
			}
		});
		menuSetup.add(menuConfig);

		JMenuItem menuTrace = new JMenuItem("Trace");
		menuTrace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TraceDbAnzeigen traceDb = new TraceDbAnzeigen();
				setPaneCenter(traceDb.getPanel());
			}
		});
		menuSetup.add(menuTrace);

		menuBar.add(menuSetup);

		menuBar.setMargin(new Insets(4, 4, 4, 4));
		menuBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return menuBar;
	}

	/**
	 * Den ersten Spieler anzeigen
	 */
	public void showSpielerFirst(int spielerId) {
		mAbwAnzeigen.addShowSpielerFirst(spielerId, false);
		setPaneCenter(mAbwAnzeigen.getPanel());
	}

	/**
	 * Weiterer Spieler anzeigen, annahme es sei schon ein Spieler vorhanden
	 *
	 * @param spielerId
	 */
	public void showSpielerNext(int spielerId) {
		mAbwAnzeigen.addShowSpielerNext(spielerId);
		setPaneCenter(mAbwAnzeigen.getPanel());
	}

	/**
	 * Setzt die linke Seite (LINE_START) vom Border Layout
	 */
	public void setPaneLeft(JPanel panel) {
		if (panelLeft != null) {
			this.remove(panelLeft);
		}
		panelLeft = panel;
		this.getContentPane().add(BorderLayout.LINE_START, panel);
//		panel.repaint();
		panel.revalidate();
	}

	/**
	 * Setzt die Mitte vom Border Layout (das Zentrum)
	 */
	public void setPaneCenter(JComponent panel) {
		if (panelCenter != null) {
			this.remove(panelCenter);
		}
		panelCenter = panel;

		this.getContentPane().add(BorderLayout.CENTER, panel);
		panel.repaint();
		panel.revalidate();
	}

	public Rectangle getCenterSize() {
		return panelCenter.getBounds();
	}

	/**
	 * Die Menu aktiv / passiv setzen
	 */
	public void setEnable() {
		// wenn keine Verbindung zur DB, dann nur Setup anzeigen
		if (!TennisDataBase.dbExists() || !DbConnection.isConnected()) {
			menuAbwTableau.setEnabled(false);
			menuAbwAnzeigen.setEnabled(false);
			menuAbwEintagen.setEnabled(false);
			menuSpielerTableau.setEnabled(false);
			menuTurnierPage.setEnabled(false);
			menuSetup.setEnabled(true);
			return;
		}

		// wenn Speichern ausstehend bei Abwesend eintragen

		if (mSpielerSelect.getSelectedTableauIndex() > 0) {
			menuAbwTableau.setEnabled(true);
		} else {
			menuAbwTableau.setEnabled(false);
		}
		if (mSpielerSelect.getSelectedSpielerId() >= 0) {
			menuAbwAnzeigen.setEnabled(true);
			menuAbwEintagen.setEnabled(true);
			menuSpielerTableau.setEnabled(true);
			mSpielerSelect.setBtnEnalble(true);
		} else {
			menuAbwAnzeigen.setEnabled(false);
			menuAbwEintagen.setEnabled(false);
			menuSpielerTableau.setEnabled(true);
			mSpielerSelect.setBtnEnalble(false);
		}
	}

}
