package carpool.HttpServer.asyncRelayExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import carpool.HttpServer.asyncTask.StoreSearchHistoryTask;
import carpool.HttpServer.asyncTask.emailTask.HotmailEmailTask;
import carpool.HttpServer.asyncTask.emailTask.SESEmailTask;
import carpool.HttpServer.asyncTask.emailTask.SendCloudEmailTask;
import carpool.HttpServer.asyncTask.relayTask.LetterRelayTask;
import carpool.HttpServer.asyncTask.relayTask.NotificationRelayTask;
import carpool.HttpServer.interfaces.PseudoAsyncTask;


public class ExecutorProvider {
	private static final int threadPool_max_notifiaction = 20;
	private static final int threadPool_max_email = 20;
	private static final int threadPool_max_letter = 20;
	private static final int threadPool_max_sr = 10;
	private static final ExecutorService notificationExecutor = Executors.newFixedThreadPool(threadPool_max_notifiaction);
	private static final ExecutorService emailExecutor = Executors.newFixedThreadPool(threadPool_max_email);
	private static final ExecutorService letterExecutor = Executors.newFixedThreadPool(threadPool_max_letter);
	private static final ExecutorService srExecutor = Executors.newFixedThreadPool(threadPool_max_sr);
	
	public static void executeRelay (PseudoAsyncTask task){
		RelayTaskExecutableWrapper executableTask = new RelayTaskExecutableWrapper(task);
		if (task instanceof NotificationRelayTask){
			notificationExecutor.submit(executableTask);
		}
		else if (task instanceof SESEmailTask || task instanceof HotmailEmailTask || task instanceof SendCloudEmailTask){
			emailExecutor.submit(executableTask);
		}
		else if(task instanceof StoreSearchHistoryTask){
			srExecutor.submit(executableTask);
		}
		else if (task instanceof LetterRelayTask){
			letterExecutor.submit(executableTask);
		}
		
	}

}
