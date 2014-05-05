package carpool.HttpServer.clean;


import java.util.Calendar;
import java.util.Date;

import carpool.HttpServer.cleanRoutineTask.MessageCleaner;
import carpool.HttpServer.cleanRoutineTask.RedisCleaner;
import carpool.HttpServer.cleanRoutineTask.TransactionCleaner;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.exception.location.LocationNotFoundException;


public class Clean{

	public static Calendar dateToCalendar(Date date){ 
		Calendar calendar = DateUtility.getCurTimeInstance();
		calendar.setTime(date);
		return calendar;
	}

	
	public void cleanSchedules() throws LocationNotFoundException{
		MessageCleaner.Clean();
		TransactionCleaner.Clean();
		RedisCleaner.Clean();
	}


}


