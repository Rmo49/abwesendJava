package com.rmo.abwesend.util;

import java.util.Date;
import java.util.List;

import com.rmo.abwesend.model.Match;
import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;

/**
 * Die mails generieren und in ein File schreiben, Einstieg mit mailGenerate.
 * @author ruedi
 */
public class MailGenerator {

    private boolean mAnAlle;
    private boolean mKeineAbwesenheit;
    private String mSpielerName;
    private String mTextBetreff;
    private String mTextMessage;
    private String mTextMessageNew;
    private String mToAdresse;
    private String mTestToAdress;
    private MailToFile mailToFile = null;

    private boolean fehler = false;

    /**
     * Konstruktor für Mails senden
     * @param betreff
     * @param message
     * @param writeInFile nur in File schreiben
     * @param testToAdress testAdresse, alles an diese Adresse senden
     * @param fromAdress ab dieser Adresse senden
     */
    public MailGenerator(String betreff, String message, String testToAdress, boolean anAlle, boolean keineAbwesenheit) {
    	mTextBetreff = betreff;
    	mTextMessage = message;
    	mTestToAdress = testToAdress;
    	mAnAlle = anAlle;
    	mKeineAbwesenheit = keineAbwesenheit;
    }

	/**
	 * Check ob die Adresse des Spielers mit der toAdresse übereinstimmt
	 */
	public boolean isSameToAdress(int spielerId, String toAdress) {
		Spieler spieler = null;
		try {
			spieler = SpielerData.instance().read(spielerId);
			return (spieler.getEmail().compareToIgnoreCase(toAdress) == 0);
		}
		catch (Exception ex) {
			Trace.println(4, "CheckAdress, Fehler: " + ex.getMessage());
		}
		return false;
	}

    /**
	 * Eine mail an einen Spieler senden, die Liste enthält alle seine Matches
     * @param matches alle matches eines Spielers
     * @param test wenn in Test-Mode
     * @param toAdressTest Adress wenn in Test
     */
	public void generateMail(Spieler spieler) {
		mailTextName(spieler);
		if (mTestToAdress.length() > 0) {
			// gesetzte Adresse wird nachträglich ersetzt, wenn test
			mToAdresse = mTestToAdress;
		}
		else {
			mToAdresse = spieler.getEmail();
		}
		writeToFile();
	}


    /**
	 * Eine mail an einen Spieler generieren, die Liste enthält alle seine Matches
     * @param matches alle matches eines Spielers
     * @param test wenn in Test-Mode
     * @param toAdressTest Adress wenn in Test
     */
	public void generateMail(List<Match> matches) {
		mailTextFuellen(matches);
		if (mTestToAdress.length() > 0) {
			// gesetzte Adresse wird nachträglich ersetzt, wenn test
			mToAdresse = mTestToAdress;
		}
		writeToFile();
	}


	/**
	 * Wird aufgerufen wenn die letzte Zeile verarbeitet ist.
	 */
	public String readEnd() {
		if (fehler) {
			return("Probleme beim senden, siehe Trace");
		}
		else {
			return("Alles generiert. \n siehe: " + Config.sMailToSendPath);
		}
	}

	/**
	 * Den Taoke Vorname im Text ersetzen mit Name des Spielers.
	 * @param matches
	 */
	private void mailTextName(Spieler spieler) {
		mSpielerName = spieler.getName() + " " + spieler.getVorName();
		mTextMessageNew = mTextMessage.replace("<Vorname>", spieler.getVorName());
	}

	/**
	 * Die Tokens im Text ersetzen mit Name und Matches
	 * @param matches
	 */
	private void mailTextFuellen(List<Match> matches) {
		Spieler spieler = null;
		try {
			spieler = SpielerData.instance().read(matches.get(0).getSpielerId());
			mToAdresse = spieler.getEmail();
			mSpielerName = spieler.getName() + " " + spieler.getVorName();
		}
		catch (Exception ex) {
			Trace.println(4, "Mail versenden, Fehler: " + ex.getMessage());
			fehler = true;
		}
		mTextMessageNew = mTextMessage.replace("<Vorname>", spieler.getVorName());

		StringBuffer sBuffer = new StringBuffer(100);
		if (! mAnAlle) {
			for (Match match: matches) {
				Date datum = null;
				try {
					datum = Config.sdfDb.parse(match.getDatum());
				}
				catch (Exception ex) {
					// nix tun sollte nicht passieren
				}
				sBuffer.append(Config.sdfTagName.format(datum));
				if (match.getSpielTyp().compareTo("E") == 0) {
					sBuffer.append(" Einzel");
				}
				else {
					sBuffer.append(" Doppel");
				}
				sBuffer.append("\n");
			}
			mTextMessageNew = mTextMessageNew.replace("<Spiele>", sBuffer.toString());
		}
	}

	/**
	 * Die mail in den Trace schreiben
	 */
	private void writeToFile() {
		if (mailToFile == null) {
			mailToFile = new MailToFile(Config.sMailToSendPath);
		}
		mailToFile.println(mSpielerName);
		mailToFile.println(mToAdresse);
		mailToFile.println(mTextBetreff);
		mailToFile.println(mTextMessageNew);
		mailToFile.println("--------------------");

	}

	public void closeFile() {
		mailToFile.close();
	}
}
