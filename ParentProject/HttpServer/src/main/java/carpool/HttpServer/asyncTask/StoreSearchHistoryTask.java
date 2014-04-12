package carpool.HttpServer.asyncTask;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.dbservice.FileService;
import carpool.HttpServer.interfaces.PseudoAsyncTask;
import carpool.HttpServer.model.representation.SearchRepresentation;


public class StoreSearchHistoryTask implements PseudoAsyncTask {
	
	private int userId;
	private SearchRepresentation sr;
	
	public StoreSearchHistoryTask(SearchRepresentation sr, int userId){
		this.userId = userId;
		this.sr = sr;
	}
	
	
	@Override
	public boolean execute() {
		return storeSearchHistoryTask();
	}
	
	
	public boolean storeSearchHistoryTask(){
		try {
			FileService.storeSearchRepresentation(sr, userId);
		} catch (IOException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}	
		return true;
	}

}
