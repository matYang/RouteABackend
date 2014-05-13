package carpool.HttpServer.emailServiceTest;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.carpoolDAO.CarpoolDaoUser;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.dbservice.EmailDaoService;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.model.Location;
import carpool.HttpServer.model.User;

public class EmailTaskTest {
	
	@Test
	public void testActivateEmail(){
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
		
		User user =  new User("xch93318yeah", "uwse@me.com", departureLocation, Gender.both);		
		String email = "uwse@me.com";
		user.setEmail(email);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			fail();
			e.printStackTrace();
		}
		String newEmail = "uwse@me.com";
		if(EmailDaoService.sendActivationEmail(user.getUserId(), newEmail)){
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			fail();
		}
	}
	
	@Test
	public void testForgotPassword(){
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
		
		User user =  new User("xch93318yeah", "teachersgone@hotmail.com", departureLocation, Gender.both);		
		String email = "teachersgone@hotmail.com";
		user.setEmail(email);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			fail();
			e.printStackTrace();
		}
		
		try {
			if(EmailDaoService.sendForgotPasswordEmail(email)){
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				fail();
			}
		} catch (UserNotFoundException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	
}
