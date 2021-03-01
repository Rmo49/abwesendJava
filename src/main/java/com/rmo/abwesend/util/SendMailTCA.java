package com.rmo.abwesend.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailTCA { 
	  
    public static void main(String[] args) { 
          
        final String username = "cm@tcallschwil.ch";    
        final String password = "club2007meistertca"; 
          
        // hosts IP address 
        final String host = "imap.mail.hostpoint.ch";  
        final String hostSmtp = "asmtp.mailstation.ch";  
        // TLS Port
        final String tlsPort = "587";
  
        // Get system properties 
        Properties props = new Properties();              
          
        // enable authentication
        props.put("mail.smtp.host", host);
          
        // enable STARTTLS 
        props.put("mail.smtp.starttls.enable", "true");     
          
        // Setup mail server 
        props.put("mail.smtp.host", hostSmtp);      
          
        // TLS Port 
        props.put("mail.smtp.port", tlsPort);   
        
        // habe das noch eingefügt vom 
        props.put("mail.smtp.auth", "true");
        
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
                                         
                return new PasswordAuthentication(username,  
                                                 password); 
            } 
          }); 
  
        try { 
              
            // compose the message 
            // javax.mail.internet.MimeMessage class is  
            // mostly used for abstraction. 
            Message message = new MimeMessage(session);     
              
            // header field of the header. 
            message.setFrom(new InternetAddress(username));  
              
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ruedi@nomadus.ch"));
            message.setSubject("hello"); 
            message.setText("Yo it has been sent");
  
            Transport.send(message);         //send Message 
  
            System.out.println("Done"); 
  
        } catch (MessagingException e) { 
            throw new RuntimeException(e); 
        } 
    } 
} 
