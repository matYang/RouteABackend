package carpool.HttpServer.awsTest;
import static org.junit.Assert.fail;
import org.junit.Test;
import carpool.HttpServer.aws.AwsSES;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;


public class AwsSESTest {
	
	@Test
	public void testSend(){
		CarpoolDaoBasic.clearBothDatabase();
		try{
			String From = "info@routea.ca";
			String To = "xiongchuhan@hotmail.com";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "assignment";
			AwsSES.sendEmail(From,To,Body,Subject,"plain");			
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
			String From = "info@routea.ca";
			String To = "xiongchuhan@hotmail.com";
			String Body = "<h1>Click it to search using Google <p><div><a href=\"www.google.ca\">google</a></div></p></h1>";
			String Subject = "Test21";
			AwsSES.sendEmail(From,To,Body,Subject,"html");
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "x";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test22";
			AwsSES.sendEmail(From,To,Body,Subject,"plain");
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "fyseason@163.com";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test23";
			AwsSES.sendEmail(From,To,Body,Subject,"plain");
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		
		
	}

}
