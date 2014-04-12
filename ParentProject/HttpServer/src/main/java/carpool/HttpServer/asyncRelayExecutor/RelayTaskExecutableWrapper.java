package carpool.HttpServer.asyncRelayExecutor;

import carpool.HttpServer.interfaces.PseudoAsyncTask;

public class RelayTaskExecutableWrapper implements Runnable{
	
	private PseudoAsyncTask task;
	
	public RelayTaskExecutableWrapper(PseudoAsyncTask task){
		this.task = task;
	}
		 
	public void run(){
		this.task.execute();
	}

}
