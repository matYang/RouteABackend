package carpool.HttpServer.carpool.test.dao;

import static org.junit.Assert.fail;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.carpoolDAO.CarpoolDaoMessage;
import carpool.HttpServer.carpoolDAO.CarpoolDaoTransaction;
import carpool.HttpServer.carpoolDAO.CarpoolDaoUser;
import carpool.HttpServer.cleanRoutineTask.TransactionCleaner;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.DayTimeSlot;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.configurations.EnumConfig.MessageType;
import carpool.HttpServer.configurations.EnumConfig.PaymentMethod;
import carpool.HttpServer.configurations.EnumConfig.TransactionState;
import carpool.HttpServer.configurations.EnumConfig.TransactionType;
import carpool.HttpServer.dbservice.TransactionDaoService;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.model.Location;
import carpool.HttpServer.model.Message;
import carpool.HttpServer.model.Transaction;
import carpool.HttpServer.model.User;

public class CarpoolTransactionTest {	

	@Test
	public void testAddTransaction() throws LocationNotFoundException{
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
		User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca",departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		User customer =  new User("fangyuan", "fangyuanlucky", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = PaymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		TransactionType transactiontype = TransactionType.fromInt(0);

		//Messages
		Message message=new Message(provider.getUserId(),false
				, new Location(departureLocation),time,timeSlot,1 , priceList,new Location(arrivalLocation),
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);		
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,transactiontype);

		//Test
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);

		}catch(Exception e){
			e.printStackTrace();

		}
	}

	@Test
	public void testReadTransaction() throws LocationNotFoundException{
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

		Calendar dt = DateUtility.getCurTimeInstance();
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod p =EnumConfig.PaymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);


		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),false
				, new Location(departureLocation),dt,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at,timeSlot, aseats,priceList,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);	
		TransactionType ttype = TransactionType.fromInt(0);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);

		// Test
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());			
			if(transaction.getProvider().equals(provider)&&transaction.getCustomer().equals(customer)&&transaction.getDeparture_location().equals(message.getDeparture_Location())&&transaction.getArrival_location().equals(message.getArrival_Location())&&transaction.getCustomerNote().equals("cNote")&&transaction.getProviderNote().equals("pNote")&&transaction.getPaymentMethod().equals(p)&&transaction.getTotalPrice()==1){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		Transaction t2 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(t2);
			t2 = CarpoolDaoTransaction.getTransactionById(t2.getTransactionId());		
			ArrayList<Transaction> tlist = new ArrayList<Transaction>();		
			tlist = CarpoolDaoTransaction.getAllTranscations();	
			if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
				//Passed;

			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Transaction> tlist = new ArrayList<Transaction>();
			tlist = CarpoolDaoTransaction.getAllTransactionByMessageId(message.getMessageId());
			if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
				//Passed;				
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Transaction> tlist = new ArrayList<Transaction>();
			tlist = CarpoolDaoTransaction.getAllTransactionByUserId(provider.getUserId());
			if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
				//Passed;				
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Transaction> tlist = new ArrayList<Transaction>();
			tlist = CarpoolDaoTransaction.getAllTransactionByUserId(customer.getUserId());
			if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
				//Passed;				
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		ArrayList<Integer> alist = new ArrayList<Integer>();
		alist.add(10);
		Message message2=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message2 = CarpoolDaoMessage.addMessageToDatabase(message2);
		ttype = TransactionType.fromInt(1);
		transaction = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);
		try{
			transaction = CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			if(transaction.getProvider().equals(provider)&& transaction.getCustomer().equals(customer)&&transaction.getMessage().equals(message2)&&transaction.getTotalPrice()==10){
				//Passed;
			}else{

			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	@Test
	public void testUpdateTransaction() throws LocationNotFoundException{
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
		User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		User customer =  new User("fangyuan", "fangyuanlucky", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}

		Calendar dt = DateUtility.getCurTimeInstance();
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod p =EnumConfig.PaymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		ArrayList<Integer> alist = new ArrayList<Integer>();
		alist.add(10);	

		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);	
		TransactionType ttype = TransactionType.fromInt(0);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());		

		}catch(Exception e){
			e.printStackTrace();
			fail();
		}

		transaction.setCustomerEvaluation(100);
		transaction.setType(TransactionType.fromInt(1));
		transaction.setCustomerNote("Good");

		//Test
		try{
			CarpoolDaoTransaction.updateTransactionInDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());			
			if(transaction.getCustomerEvaluation()==100&&transaction.getType().equals(TransactionType.fromInt(1))&&transaction.getProvider().equals(provider)&&transaction.getCustomer().equals(customer)&&transaction.getDeparture_location().equals(message.getDeparture_Location())&&transaction.getArrival_location().equals(message.getArrival_Location())&&transaction.getCustomerNote().equals("Good")&&transaction.getProviderNote().equals("pNote")&&transaction.getPaymentMethod().equals(p)&&transaction.getTotalPrice()==10){

			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		

	}

	@Test
	public void testTransactionCleaner() throws LocationNotFoundException{    	
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
		User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		User customer =  new User("fangyuan", "fangyuanlucky", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}

		Calendar dt = DateUtility.getCurTimeInstance();		
		dt.set(Calendar.HOUR_OF_DAY, dt.get(Calendar.HOUR_OF_DAY)+1);
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);

		Calendar dt2 = DateUtility.getCurTimeInstance();
		dt2.add(Calendar.DAY_OF_YEAR, -1);
		dt2.add(Calendar.SECOND, 5);
		Calendar at2 = DateUtility.getCurTimeInstance();
		at2.add(Calendar.DAY_OF_YEAR, 0);

		Calendar dt3 = DateUtility.getCurTimeInstance();
		dt3.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at3 = DateUtility.getCurTimeInstance();
		at3.add(Calendar.DAY_OF_YEAR, 3);

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod p =EnumConfig.PaymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		ArrayList<Integer> alist = new ArrayList<Integer>();
		alist.add(10);

		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);	
		Message message2=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt2,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at2,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message2 = CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt3,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at3,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message3 = CarpoolDaoMessage.addMessageToDatabase(message3);


		TransactionType ttype = TransactionType.departure;
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction.setState(TransactionState.init);// This should pass the test(aboutToStart)
		Transaction transaction2 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction2.setState(TransactionState.init);
		Transaction transaction3 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction3.setState(TransactionState.finished);
		Transaction transaction4 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction4.setState(TransactionState.aboutToStart);// This should pass the test(finished)
		Transaction transaction5 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction5.setState(TransactionState.init);
		Transaction transaction6 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction6.setState(TransactionState.aboutToStart);// This should pass the test(finished)
		Transaction transaction7 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote", null,timeSlot,dseats,ttype);
		transaction7.setState(TransactionState.aboutToStart);// This should pass the test(finished)
		Transaction transaction8 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction8.setState(TransactionState.finished);
		Transaction transaction9 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction9.setState(TransactionState.init);
		Transaction transaction10 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction10.setState(TransactionState.finished);

		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction2);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction3);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction4);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction5);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction6);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction7);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction8);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction9);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction10);
			//Test
			TransactionCleaner.Clean();
			ArrayList<TransactionState> list = new ArrayList<TransactionState>();
			list.add(CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction2.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction3.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction4.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction5.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction6.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction7.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction8.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction9.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction10.getTransactionId()).getState());
			if(list !=null && list.size()==10 && list.get(0)==TransactionState.aboutToStart && list.get(1)==TransactionState.init &&list.get(2)==TransactionState.finished && list.get(3)==TransactionState.finished && list.get(4)==TransactionState.init && list.get(5)==TransactionState.finished && list.get(6)==TransactionState.finished&& list.get(7)==TransactionState.finished && list.get(8)==TransactionState.init&&list.get(9)==TransactionState.finished){
				//Passed;				
			}else{				
				fail();					
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}


	} 

	@Test
	public void testSortTransactions() throws LocationNotFoundException{
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
		User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		User customer =  new User("fangyuan", "fangyuanlucky", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}

		Calendar dt = DateUtility.getCurTimeInstance();		
		dt.set(Calendar.HOUR_OF_DAY, dt.get(Calendar.HOUR_OF_DAY)+1);
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);

		Calendar dt2 = DateUtility.getCurTimeInstance();
		dt2.add(Calendar.DAY_OF_YEAR, -1);		
		Calendar at2 = DateUtility.getCurTimeInstance();
		
		Calendar dt3 = DateUtility.getCurTimeInstance();
		dt3.add(Calendar.DAY_OF_YEAR, 3);
		Calendar at3 = DateUtility.getCurTimeInstance();
		at3.add(Calendar.DAY_OF_YEAR, -3);

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod p =EnumConfig.PaymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		ArrayList<Integer> alist = new ArrayList<Integer>();
		alist.add(10);

		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);	
		Message message2=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt2,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at2,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message2 = CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(provider.getUserId(),true
				, new Location(departureLocation),dt3,timeSlot,dseats , priceList,new Location(arrivalLocation),
				at3,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message3 = CarpoolDaoMessage.addMessageToDatabase(message3);


		TransactionType ttype = TransactionType.departure;
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction.setState(TransactionState.init);
		Transaction transaction2 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction2.setState(TransactionState.init);
		Transaction transaction3 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction3.setState(TransactionState.finished);
		Transaction transaction4 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction4.setState(TransactionState.aboutToStart);
		Transaction transaction5 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction5.setState(TransactionState.init);
		Transaction transaction6 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction6.setState(TransactionState.aboutToStart);
		Transaction transaction7 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote", null,timeSlot,dseats,ttype);
		transaction7.setState(TransactionState.aboutToStart);
		Transaction transaction8 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction8.setState(TransactionState.finished);
		Transaction transaction9 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction9.setState(TransactionState.init);
		Transaction transaction10 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction10.setState(TransactionState.finished);
		
		ArrayList<Transaction> testResult = new ArrayList<Transaction>();
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			tlist.add(transaction);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction2);
			tlist.add(transaction2);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction3);
			tlist.add(transaction3);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction4);
			tlist.add(transaction4);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction5);
			tlist.add(transaction5);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction6);
			tlist.add(transaction6);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction7);
			tlist.add(transaction7);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction8);
			tlist.add(transaction8);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction9);
			tlist.add(transaction9);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction10);
			tlist.add(transaction10);
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		testResult = TransactionDaoService.sortTransactions(tlist);
		if(testResult.size()==tlist.size() && testResult.get(0).equals(transaction2)
		   && testResult.get(1).equals(transaction4)&&testResult.get(2).equals(transaction9)
		   && testResult.get(3).equals(transaction10)&&testResult.get(4).equals(transaction)
		   && testResult.get(5).equals(transaction7)&&testResult.get(6).equals(transaction8)
		   && testResult.get(7).equals(transaction3)&&testResult.get(8).equals(transaction5)
		   && testResult.get(9).equals(transaction6)){
			//Passed;
		}else{
			fail();
		}
	}


} 
