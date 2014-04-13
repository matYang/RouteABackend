package carpool.HttpServer.serviceTest;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.carpoolDAO.CarpoolDaoMessage;
import carpool.HttpServer.carpoolDAO.CarpoolDaoTransaction;
import carpool.HttpServer.carpoolDAO.CarpoolDaoUser;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.DayTimeSlot;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.configurations.EnumConfig.MessageType;
import carpool.HttpServer.configurations.EnumConfig.PaymentMethod;
import carpool.HttpServer.configurations.EnumConfig.TransactionType;
import carpool.HttpServer.dbservice.MessageDaoService;
import carpool.HttpServer.dbservice.TransactionDaoService;
import carpool.HttpServer.dbservice.UserDaoService;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.model.Location;
import carpool.HttpServer.model.Message;
import carpool.HttpServer.model.Transaction;
import carpool.HttpServer.model.User;

public class TransactionDaoServiceTest {

	@Test
	public void test() throws LocationNotFoundException {
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
		//Users
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new Location(departureLocation), Gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		User customer =  new User("fangyuan", "fangyuanlucky", new Location(arrivalLocation), Gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		
				
		//Messages
		Message message=new Message(provider.getUserId(),false
				, new Location(departureLocation),time,timeSlot,2 , priceList,new Location(arrivalLocation),
				time,timeSlot, 2,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		TransactionType tD = EnumConfig.TransactionType.fromInt(1);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,tD);
		Transaction transaction2 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,tD);
		Transaction transaction3 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,tD);
		//Test
		try{
			transaction = TransactionDaoService.createNewTransaction(transaction);
			message = MessageDaoService.getMessageById(transaction.getMessageId());
			assertTrue(message.getDeparture_seatsBooked() == 1 || message.getArrival_seatsBooked() == 1);
			transaction = TransactionDaoService.getUserTransactionById(transaction.getProviderId(), provider.getUserId());
			
			transaction2 = TransactionDaoService.createNewTransaction(transaction2);
			//message = MessageDaoService.getMessageById(transaction.getMessageId());
			//assertTrue(message.getDeparture_seatsBooked() == 1 || message.getArrival_seatsBooked() == 1);
			TransactionDaoService.cancelTransaction(transaction2.getTransactionId(), customer.getUserId());
			transaction3 = TransactionDaoService.createNewTransaction(transaction3);
			
			ArrayList<Transaction> transactions = TransactionDaoService.getAllTransactions();
			assertTrue(transactions.size() == 3);
			
			CarpoolDaoTransaction.getAllTranscations();
			
			ArrayList<Transaction> transactions2 = MessageDaoService.getTransactionByMessageId(message.getMessageId());
			assertTrue(transactions2.size() == 3);
			
			ArrayList<Transaction> transactions3 = UserDaoService.getTransactionByUserId(provider.getUserId());
			assertTrue(transactions3.size() == 3);
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		
	}

}
