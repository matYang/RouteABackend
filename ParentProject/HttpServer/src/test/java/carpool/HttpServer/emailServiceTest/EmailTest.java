package carpool.HttpServer.emailServiceTest;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.HttpServer.asyncTask.relayTask.EmailRelayTask;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;


public class EmailTest {


	@Test
	public void testSend(){
		CarpoolDaoBasic.clearBothDatabase();
		try{			
			String To = "xiongchuhan@hotmail.com";
			String Body = "This email was sent through the Hotmail SMTP interface by using Java.";
			String Subject = "assignment";
			EmailRelayTask ert = new EmailRelayTask(To,Subject,Body,"plain");		
			ert.execute();
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSpecialCases(){
		CarpoolDaoBasic.clearBothDatabase();
		try{			
			String To = "xiongchuhan@hotmail.com";
			String Body = "<h1>Click it to search using Google <p><div><a href=\"www.google.com.hk\">google</a></div></p></h1>";
			String Subject = "Google Test";
			EmailRelayTask ert = new EmailRelayTask(To,Subject,Body,"html");		
			ert.execute();
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}

		try{			
			String To = "x";
			String Body = "This email was sent through the Hotmail SMTP interface by using Java.";
			String Subject = "failTest";
			EmailRelayTask ert = new EmailRelayTask(To,Subject,Body,"plain");		
			ert.execute();
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}

		try{			
			String To = "fyseason@163.com";
			String Body = "This email was sent through the Hotmail SMTP interface by using Java.";
			String Subject = "Test";
			EmailRelayTask ert = new EmailRelayTask(To,Subject,Body,"plain");		
			ert.execute();
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		

	}



}
