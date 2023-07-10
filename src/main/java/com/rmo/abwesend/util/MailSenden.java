package com.rmo.abwesend.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;

/**
 * Die mails versenden, Einstieg mit mailSenden.
 * Die Kontrolle wird in das File MailControl geschrieben.
 * @author ruedi
 *
 */
public class MailSenden {

    private String mToAdresse;
    private String mTextBetreff;
    private String mTextMessage;


    private boolean fehler = false;

    /**
     * Konstruktor für Mails senden
     */
    public MailSenden() {

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
	 * Eine mail an einen Spieler senden, mails werden vom File gelesen
     */
	public void sendMail(String toAdresse, String betreff, String message) {
    	mToAdresse = toAdresse;
    	if (mToAdresse.equalsIgnoreCase("null")) {
    		return;
    	}
    	mTextBetreff = betreff;
    	mTextMessage = message;
		sendMailOut();
	}


	/**
	 * Wird aufgerufen wenn die letzte Zeile verarbeitet ist.
	 */
	public String readEnd() {
		if (fehler) {
			return("Probleme beim senden, siehe MailControl: " + Config.sMailControlPath);
		}
		else {
			return("\nAlles versendet. \nsiehe: " + Config.sMailControlPath);
		}
	}


	/**
	 * Hier wird das mail dirket versendet
	 */
    private void sendMailOut() {
		Trace.println(5, "sendMailOut()");
    	writeControl();

    	if (mToAdresse == null || mToAdresse.length() < 3) {
    		return;
    	}

        // Get system properties
        Properties props = getProperties();

        // creating Session instance referenced to
        // Authenticator object to pass in
        // Session.getInstance argument
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            //override the getPasswordAuthentication method
            @Override
			protected PasswordAuthentication
                           getPasswordAuthentication() {
                 return new PasswordAuthentication(Config.get(Config.emailUser), Config.get(Config.emailPassword));
            }
          });

        try {
            // compose the message
            // javax.mail.internet.MimeMessage class is
            // mostly used for abstraction.
            Message message = new MimeMessage(session);

            // header field of the header.
            message.setFrom(new InternetAddress(Config.get(Config.emailUser)) );

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mToAdresse));
            message.setSubject(mTextBetreff);
            message.setText(mTextMessage);

            Transport.send(message);         //send Message

        }
        catch (MessagingException e) {
        	MailControl.println(2, "sendMailOut(), Fehler beim senden; " + e.getMessage());
        	MailControl.println(2, "NestedException: " + e.getNextException());
        	fehler = true;
        }
    }

    /**
     * In das File zur Kontrolle schreiben
     */
    private void writeControl() {
    	if (mToAdresse == null) {
    		MailControl.println(0, "keine Adresse vorhanden <<<<<<");
	   	}
	   	else {
	   		MailControl.println(0, mToAdresse);
	   	}
        MailControl.println(0, mTextMessage);
        MailControl.println(0, "------------");
    }

    public void closeFile() {
    	MailControl.closeFile();
    }

   /**
     * Die Properties setzen
     */
    private Properties getProperties() {
//        Properties props = new Properties();
        // Get system properties
        Properties props = System.getProperties();
        // enable authentication
//        props.put("mail.smtp.host", Config.get(Config.emailHostImap));

        // debug einschalten
//        props.put("mail.debug", "true");
        // enable STARTTLS
        props.put("mail.smtp.starttls.enable", "true");

        // Setup mail server
        props.put("mail.smtp.host", Config.get(Config.emailHostSmtp));

        // TLS Port
        props.put("mail.smtp.port", Config.get(Config.emailSmtpPort));

        // habe das noch eingefügt
        props.put("mail.smtp.auth", "true");
        return props;
    }

}
