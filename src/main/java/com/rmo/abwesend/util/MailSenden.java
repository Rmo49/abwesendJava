package com.rmo.abwesend.util;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.rmo.abwesend.model.Match;
import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;

/**
 * Die mails versenden, Einstieg mit mailSenden.
 * Die Kontrolle wird in das File MailControl geschrieben.
 * @author ruedi
 *
 */
public class MailSenden {
	
    final String username = "cm@tcallschwil.ch";
    final String password = "club2007meistertca"; 
    // hosts IP address 
    final String host = "imap.mail.hostpoint.ch";  
    final String hostSmtp = "asmtp.mailstation.ch";  
    // TLS Port
    final String tlsPort = "587";
    
    private String mTextBetreff;
    private String mTextMessage;
    private String mTextMessageNew;
    private String mToAdresse;
    private boolean mWriteInFile;
    private String mTestToAdress;

    private boolean fehler = false;
    
    /**
     * Konstruktor für Mails senden
     * @param betreff
     * @param message
     * @param writeInFile nur in File schreiben
     * @param testToAdress testAdresse, alles an diese Adresse senden
     * @param fromAdress ab dieser Adresse senden
     */
    public MailSenden(String betreff, String message, boolean writeInFile,
    		String testToAdress) {
    	mTextBetreff = betreff;
    	mTextMessage = message;
    	mWriteInFile = writeInFile;
    	mTestToAdress = testToAdress;
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
	public void mailSenden(List<Match> matches) {
		mailTextFuellen(matches);		
		if (mWriteInFile) {
			if (mTestToAdress.length() > 5) {
				sendTest();
				return;
			}
			sendInFile();
			return;
		}
		sendDirekt();
	}
	
	
	/**
	 * Wird aufgerufen wenn die letzte Zeile verarbeitet ist.
	 */
	public String readEnd() {
		if (fehler) {
			return("Probleme beim senden, siehe Trace");
		}
		else {
			return("Alles versendet. \n siehe: " + Config.sMailControl);
		}
	}

	
	
	/**
	 * Die Tokens im Text replacen.
	 * @param matches
	 */
	private void mailTextFuellen(List<Match> matches) {
		Spieler spieler = null;
		try {
			spieler = SpielerData.instance().read(matches.get(0).getSpielerId());
			mToAdresse = spieler.getEmail();
		}
		catch (Exception ex) {
			Trace.println(4, "Mail versenden, Fehler: " + ex.getMessage());
			fehler = true;
		}
		mTextMessageNew = mTextMessage.replace("<Vorname>", spieler.getVorName());
		
		StringBuffer sb = new StringBuffer(100);
		for (Match match: matches) {
			Date datum = null;
			try {
				datum = Config.sdfDb.parse(match.getDatum());
			}
			catch (Exception ex) {
				// nix tun sollte nicht passieren
			}
			sb.append(Config.sdfTagName.format(datum));
			if (match.getSpielTyp().compareTo("E") == 0) {
				sb.append(" Einzel");
			}
			else {
				sb.append(" Doppel");
			}
			sb.append("\n");
		}
		mTextMessageNew = mTextMessageNew.replace("<Spiele>", sb.toString());
	}
    
	/**
	 * Die in den Trace schreiben
	 */
	private void sendInFile() {
		MailControl.println(0, mToAdresse);
		MailControl.println(0, mTextBetreff);
		MailControl.println(0, mTextMessageNew);
		MailControl.println(0, "--------------");

	}
	
	
	/**
	 * Hier wird das mail testhalber versendet, immer an die gleiche Adresse
	 * (siehe mTestToAdress)
	 */
    private void sendTest() {
    	
    	writeControl();
    	
        // Get system properties 
        Properties props = getProperties();        
          
        // debug einschalten
        props.put("mail.debug", "true");
  
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
              
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mTestToAdress));
            message.setSubject(mTextBetreff); 
            message.setText(mTextMessageNew);
  
            Transport.send(message);         //send Message          
       }
        catch (MessagingException e) {
        	MailControl.println(0, "SendMail, Fehler beim senden; " + e.getMessage());
        	fehler = true;
        }
    }
        
    
	/**
	 * Hier wird das mail dirket versendet
	 */
    private void sendDirekt() {
    	
    	writeControl();
    	
    	if (mToAdresse == null || mToAdresse.length() < 6) {
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
            message.setText(mTextMessageNew);
  
            Transport.send(message);         //send Message
            
         // Copy message to "Sent Items" folder as read
//            Store store = session.getStore(imapProtocol);
//            store.connect(host, user, pass);
//            Folder folder = store.getFolder(folderName);
//            folder.open(Folder.READ_WRITE);
//            message.setFlag(Flag.SEEN, true);
//            folder.appendMessages(new Message[] {message});
//            store.close();
        }
        catch (MessagingException e) {
        	MailControl.println(2, "SendMail, Fehler beim senden; " + e.getMessage());
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
        MailControl.println(0, mTextMessageNew);
        MailControl.println(0, "------------");
    }
    
   /**
     * Die Properties setzen
     */
    private Properties getProperties() {
        Properties props = new Properties();
        
        // enable authentication
        props.put("mail.smtp.host", Config.get(Config.emailHostImap));
          
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
