package carpool.HttpServer.asyncTask.emailTask;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EmailConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;


public class SESEmailTask extends PseudoEmailTask{
	
	public SESEmailTask(String receiver, EmailEvent event, String payload) {
		super(receiver, event, payload);
	}
	

	@Override
	public boolean send(){

		try{
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", EmailConfig.SMTP_PORT); 
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");

			// Create a Session object to represent a mail session with the specified properties. 
			Session session = Session.getDefaultInstance(props);

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(EmailConfig.SMTP_FROM));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(this.receiver));
			msg.setSubject(this.subject);
			msg.setContent(this.body,"text/html");
			
			Transport transport = session.getTransport();

			try{
				transport.connect(EmailConfig.SMTP_HOST, EmailConfig.SMTP_USERNAME, EmailConfig.SMTP_PASSWORD);

				transport.sendMessage(msg, msg.getAllRecipients());
			} catch (Exception e) {
				e.printStackTrace();
				DebugLog.d(e);
			} finally{
				transport.close();        	
			}

		}catch (Exception e) {
			e.printStackTrace();
			DebugLog.d(e);
		}
		
		return true;
	}

}
