package carpool.HttpServer.asyncTask.relayTask;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.Map.Entry;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EmailConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.interfaces.PseudoAsyncTask;


public class EmailRelayTask implements PseudoAsyncTask{

	private static final String smtpServer = "smtp.live.com";
	private static final String sender = "huaixuesheng@hotmail.com";
	private static final String password = "password11";
	
	private String receiver;
	private String subject;
	private String body;

	
	public EmailRelayTask(String receiver, EmailEvent event, String payload){
		this.receiver = receiver;
		Entry<String, String> entry = EmailConfig.emailEventMap.get(event);
		if (entry == null){
			DebugLog.d("SESRelay Fatal: null entry from emailEventMap with given evt");
			throw new RuntimeException();
		}
		this.subject = entry.getKey();
		this.body = entry.getValue().replaceAll(EmailConfig.htmlTemplateURLTarget, payload);
	}
	
	

	public boolean execute(){
		return send();
	}


	/**
	 * @param receiver email address of the receiver
	 * @param subject
	 * @param body
	 * Send an email
	 **/
	public boolean send(){
		
			Properties props = System.getProperties();
			props.put("mail.smtp.host", smtpServer);
			props.put("mail.smtp.user", sender);
			props.put("mail.smtp.password", password);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			
			try {
				
				msg.setFrom(new InternetAddress(sender));
				msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(receiver, false));
				msg.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
				msg.setContent(this.body,"text/html");
				msg.setHeader("X-Mailer", "LOTONtechEmail");
				msg.setSentDate(new Date());
				
				Transport transport;
				transport = session.getTransport("smtp");
				
				try{
					transport.connect(smtpServer,sender, password);
					transport.sendMessage(msg, msg.getAllRecipients());
				} catch (Exception e){
					e.printStackTrace();
					DebugLog.d(e);
				} finally{
					transport.close();
				}
				
			} catch (AddressException e) {
				e.printStackTrace();
				DebugLog.d(e);
				return false;
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
				DebugLog.d(e);
				return false;
			} catch (MessagingException e) {
				e.printStackTrace();
				DebugLog.d(e);
				return false;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				DebugLog.d(e);
				return false;
			} 
			
			return true;
	}

}
