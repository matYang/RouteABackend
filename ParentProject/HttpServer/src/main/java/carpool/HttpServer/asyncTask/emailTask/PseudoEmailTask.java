package carpool.HttpServer.asyncTask.emailTask;

import java.util.Map.Entry;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EmailConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.interfaces.PseudoAsyncTask;

public class PseudoEmailTask implements PseudoAsyncTask{
	
	protected String receiver;
	protected String subject;
	protected String body;

	
	public PseudoEmailTask(String receiver, EmailEvent event, String payload){
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
	
	public boolean send(){
		return false;
	}
	
}
