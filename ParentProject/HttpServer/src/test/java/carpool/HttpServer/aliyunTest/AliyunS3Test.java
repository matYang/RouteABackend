package carpool.HttpServer.aliyunTest;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import redis.clients.jedis.Jedis;

import carpool.HttpServer.aliyun.aliyunMain;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.carpoolDAO.CarpoolDaoMessage;
import carpool.HttpServer.carpoolDAO.CarpoolDaoUser;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.DatabaseConfig;
import carpool.HttpServer.configurations.EnumConfig.DayTimeSlot;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.configurations.EnumConfig.MessageType;
import carpool.HttpServer.configurations.EnumConfig.PaymentMethod;
import carpool.HttpServer.configurations.ImageConfig;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.model.Location;
import carpool.HttpServer.model.Message;
import carpool.HttpServer.model.User;
import carpool.HttpServer.model.representation.SearchRepresentation;

public class AliyunS3Test {
	
	@Test
	public void testCreateUserFile(){
		int userId = 1;
		aliyunMain.createUserFile(userId);		
	}



	@Test
	public void testUploadImg() throws IOException{
		//CarpoolDaoBasic.clearBothDatabase();
		int userId = 1;
		String userProfile = ImageConfig.profileImgPrefix;
		String imgSize = ImageConfig.imgSize_m;
		String imgName = userProfile+imgSize+userId;
		File file = new File(ServerConfig.pathToSearchHistoryFolder+imgName+".png");
		aliyunMain.uploadImg(userId, file, imgName, ServerConfig.AliyunProfileBucket,false);
	}

	@Test
	public void testUploadSearchFile() throws IOException, LocationNotFoundException{
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
		Location departureLocation2= new Location(province,city1,region1,"Test12","Test111",lat1,lng1,arrival_Id*2);
		Location arrivalLocation2 = new Location(province,city2,region2,"Test22","Test222",lat2,lng2,departure_Id+arrival_Id);
		long dm = departureLocation.getMatch();
		long am = arrivalLocation.getMatch();
		long dm2 = departureLocation2.getMatch();
		long am2 = arrivalLocation2.getMatch();

		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = DateUtility.getCurTimeInstance();
		dt2.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at2 = DateUtility.getCurTimeInstance();
		at2.add(Calendar.DAY_OF_YEAR, 2);		

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);			
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(2);
		int userId=user.getUserId();

		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation SR2 = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot2,timeSlot2);
		SearchRepresentation SR3 = new SearchRepresentation(true,dm,am,dt,at,type,timeSlot3,timeSlot3);
		SearchRepresentation SR4 = new SearchRepresentation(true,dm,am2,dt2,at2,type,timeSlot2,timeSlot2);
		SearchRepresentation SR5 = new SearchRepresentation(false,dm,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR6 = new SearchRepresentation(false,dm,am2,dt2,at2,type,timeSlot3,timeSlot3);

		Jedis redis = CarpoolDaoBasic.getJedis();
		String rediskey = DatabaseConfig.redisSearchHistoryPrefix+userId;
		int upper = DatabaseConfig.redisSearchHistoryUpbound;
		//For this test, we set the upper to be 6

		aliyunMain.storeSearchHistory(SR, userId);			
		aliyunMain.storeSearchHistory(SR2, userId);
		aliyunMain.storeSearchHistory(SR3, userId);
		aliyunMain.storeSearchHistory(SR4, userId);
		aliyunMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==5){
			//Pass
		}else{
			fail();
		}
		aliyunMain.storeSearchHistory(SR5, userId);

		aliyunMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==1){
			//Pass
		}else{
			fail();
		}
		aliyunMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==2){
			//Pass
		}else{
			fail();
		}
		aliyunMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==3){
			//Pass
		}else{
			fail();
		}
		aliyunMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==4){
			//Pass
		}else{
			fail();
		}
		aliyunMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==5){
			//Pass
		}else{
			fail();
		}
		aliyunMain.storeSearchHistory(SR5, userId);

		aliyunMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==1){
			//Pass
		}else{
			fail();
		}   
		// Try to save more than upper bound
		redis.lpush(rediskey, SR.toSerializedString());
		redis.lpush(rediskey, SR2.toSerializedString());
		redis.lpush(rediskey, SR3.toSerializedString());
		redis.lpush(rediskey, SR4.toSerializedString());
		redis.lpush(rediskey, SR5.toSerializedString());
		redis.lpush(rediskey, SR6.toSerializedString());
		aliyunMain.storeSearchHistory(SR5, userId);
		
		if(redis.lrange(rediskey, 0,upper).size()==0){
			//Pass
		}else{
			fail();
		}   
		CarpoolDaoBasic.returnJedis(redis);
	}
	
	@Test
	public void testGetSearchHistory() throws IOException, LocationNotFoundException{

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
		Location departureLocation2= new Location(province,city1,region1,"Test12","Test111",lat1,lng1,arrival_Id*2);
		Location arrivalLocation2 = new Location(province,city2,region2,"Test22","Test222",lat2,lng2,departure_Id+arrival_Id);
		long dm = departureLocation.getMatch();
		long am = arrivalLocation.getMatch();
		long dm2 = departureLocation2.getMatch();
		long am2 = arrivalLocation2.getMatch();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = DateUtility.getCurTimeInstance();
		dt2.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at2 = DateUtility.getCurTimeInstance();
		at2.add(Calendar.DAY_OF_YEAR, 2);		

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);			
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(2);
		int userId=user.getUserId();

		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation SR2 = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot2,timeSlot2);
		SearchRepresentation SR3 = new SearchRepresentation(true,dm,am,dt,at,type,timeSlot3,timeSlot3);
		SearchRepresentation SR4 = new SearchRepresentation(true,dm2,am2,dt2,at2,type,timeSlot2,timeSlot2);
		SearchRepresentation SR5 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR6 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		// In this case, we use 6 to be the upper bound
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		int pre = aliyunMain.getUserSearchHistory(userId).size();

		aliyunMain.storeSearchHistory(SR, userId);
		aliyunMain.storeSearchHistory(SR2, userId);
		aliyunMain.storeSearchHistory(SR3, userId);
		aliyunMain.storeSearchHistory(SR4, userId);
		aliyunMain.storeSearchHistory(SR5, userId);

		String rediskey = DatabaseConfig.redisSearchHistoryPrefix+userId;
		int upper = DatabaseConfig.redisSearchHistoryUpbound;
		Jedis jedis = CarpoolDaoBasic.getJedis();
		int storage = jedis.lrange(rediskey, 0, upper-1).size();
		CarpoolDaoBasic.returnJedis(jedis);
		list = aliyunMain.getUserSearchHistory(userId);
		if(list.size()==(pre+storage)){
			//Passed;
		}else{
			fail();
		}
		aliyunMain.storeSearchHistory(SR6, userId);
		list = aliyunMain.getUserSearchHistory(userId);
		if(list.size()==(pre+storage+1)){
			//Passed;			
		}else{			
			fail();
		}

		aliyunMain.storeSearchHistory(SR6, userId);
		list = aliyunMain.getUserSearchHistory(userId);
		if(list.size()==(pre+storage+2)){
			//Passed;			
		}else{			
			fail();
		}		
		
	}
	
	@Test
	public void testGetCleanUpUserSearchHistory() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		Jedis redis = CarpoolDaoBasic.getJedis();
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
		Location departureLocation2= new Location(province,city1,region1,"Test12","Test111",lat1,lng1,arrival_Id*2);
		Location arrivalLocation2 = new Location(province,city2,region2,"Test22","Test222",lat2,lng2,departure_Id+arrival_Id);
		long dm = departureLocation.getMatch();
		long am = arrivalLocation.getMatch();
		long dm2 = departureLocation2.getMatch();
		long am2 = arrivalLocation2.getMatch();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);
		User user2 =  new User("fruitJ", "xiongchuhanplace@hotmail.com", departureLocation, Gender.female);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = DateUtility.getCurTimeInstance();
		dt2.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at2 = DateUtility.getCurTimeInstance();
		at2.add(Calendar.DAY_OF_YEAR, 2);		

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);			
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(2);
		int userId=user.getUserId();
		int userId2=user2.getUserId();
		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		Message message2=new Message(userId2,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation SR2 = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot2,timeSlot2);
		SearchRepresentation SR3 = new SearchRepresentation(true,dm,am,dt,at,type,timeSlot3,timeSlot3);
		SearchRepresentation SR4 = new SearchRepresentation(true,dm2,am2,dt2,at2,type,timeSlot2,timeSlot2);
		SearchRepresentation SR5 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR6 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR7 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR8 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR9 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR10 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR11 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR12 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR13 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR14 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);

		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();

		aliyunMain.storeSearchHistory(SR, userId);
		aliyunMain.storeSearchHistory(SR2, userId2);//user2
		aliyunMain.storeSearchHistory(SR3, userId);
		aliyunMain.storeSearchHistory(SR4, userId2);//user2
		aliyunMain.storeSearchHistory(SR5, userId);
		aliyunMain.storeSearchHistory(SR6, userId2);//user2
		aliyunMain.storeSearchHistory(SR7, userId);
		aliyunMain.storeSearchHistory(SR8, userId2);//user2
		aliyunMain.storeSearchHistory(SR9, userId);
		aliyunMain.storeSearchHistory(SR10, userId2);//user2
		aliyunMain.storeSearchHistory(SR11, userId);
		aliyunMain.storeSearchHistory(SR12, userId2);//user2
		aliyunMain.storeSearchHistory(SR13, userId);
		aliyunMain.storeSearchHistory(SR14, userId2);//user2
		int preuser = aliyunMain.getUserSearchHistory(userId).size();
		int preuser2 = aliyunMain.getUserSearchHistory(userId2).size();
		aliyunMain.migrateAlltheUsersSearchHistory();
		//Test for user
		String rediskey = DatabaseConfig.redisSearchHistoryPrefix+userId;		
		int storage = redis.lrange(rediskey, 0,redis.llen(rediskey)-1).size();
		if(storage==0){
			//Passed;
		}else{
			fail();
		}
		//Test for user2
		rediskey = DatabaseConfig.redisSearchHistoryPrefix+userId2;		
		storage = redis.lrange(rediskey, 0,redis.llen(rediskey)-1).size();
		if(storage==0){
			//Passed;
		}else{
			fail();
		}

		list = aliyunMain.getUserSearchHistory(userId);
		if(list.size()==preuser){
			//Passed;
		}else{
			fail();
		}

		list = aliyunMain.getUserSearchHistory(userId2);
		if(list.size()==preuser2){
			//Passed;
		}else{
			fail();
		}
		
		CarpoolDaoBasic.returnJedis(redis);

	}
	
	@Test
	public void testUpLoadDriverAndPassengerImgs(){
		CarpoolDaoBasic.clearBothDatabase();
		//Driver 1
		int userId = 1;
		String driver_1_Profile = ImageConfig.driverVerificationImgPrefix;
		String imgSize = ImageConfig.imgSize_m;
		String imgName = driver_1_Profile+imgSize+userId;
		File file = new File(ServerConfig.pathToSearchHistoryFolder+imgName+".png");
		aliyunMain.uploadImg(userId, file, imgName, ServerConfig.DriverVerificationBucket,false);
		
		//Driver 2
		int user2Id = 2;
		String driver_2_Profile = ImageConfig.driverVerificationImgPrefix;		
		imgName = driver_2_Profile+imgSize+user2Id;
		file = new File(ServerConfig.pathToSearchHistoryFolder+imgName+".png");
		aliyunMain.uploadImg(user2Id, file, imgName, ServerConfig.DriverVerificationBucket,false);
		
		//Passenger Front
		int user3Id = 3;
		String driver_3_Profile = ImageConfig.passengerVerificationFrontImgPrefix;		
		imgName = driver_3_Profile+imgSize+user3Id;
		file = new File(ServerConfig.pathToSearchHistoryFolder+imgName+".png");
		aliyunMain.uploadImg(user3Id, file, imgName, ServerConfig.PassengerVerificationBucket,false);
		
		//Passenger Back
		driver_3_Profile = ImageConfig.passengerVerificationBackImgPrefix;		
		imgName = driver_3_Profile+imgSize+user3Id;
		file = new File(ServerConfig.pathToSearchHistoryFolder+imgName+".png");
		aliyunMain.uploadImg(user3Id, file, imgName, ServerConfig.PassengerVerificationBucket,false);
		
	}
}
