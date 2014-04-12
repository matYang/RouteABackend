package carpool.HttpServer.SRStorageTest;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.HttpServer.asyncRelayExecutor.ExecutorProvider;
import carpool.HttpServer.asyncTask.StoreSearchHistoryTask;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.carpoolDAO.CarpoolDaoUser;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig.DayTimeSlot;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.configurations.EnumConfig.MessageState;
import carpool.HttpServer.configurations.EnumConfig.MessageType;
import carpool.HttpServer.configurations.EnumConfig.PaymentMethod;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.model.Location;
import carpool.HttpServer.model.User;
import carpool.HttpServer.model.representation.SearchRepresentation;

public class SRStorageTest {
	 
	@Test	
	public void testSRStorage(){
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		long dm = departureLocation.getMatch();
		long am = arrivalLocation.getMatch();
		//Users
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new Location(departureLocation), Gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
	   User user2 =  new User("fangyuan&lucyWang", "xiongchuhan@hotmail.com", new Location(arrivalLocation), Gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);	
		Calendar at2 = DateUtility.getCurTimeInstance();	
		at2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -2);	
				
		MessageType type = MessageType.fromInt(0);	
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		//SRs
		SearchRepresentation sr = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation sr2 = new SearchRepresentation(true,am,dm,dt2,at2,type,timeSlot,timeSlot);
		//Execute	
		StoreSearchHistoryTask ssht = new StoreSearchHistoryTask(sr,user.getUserId());
		ExecutorProvider.executeRelay(ssht);
		ssht = new StoreSearchHistoryTask(sr2,user2.getUserId());
		ExecutorProvider.executeRelay(ssht);
		
	}
	
	
	
}
