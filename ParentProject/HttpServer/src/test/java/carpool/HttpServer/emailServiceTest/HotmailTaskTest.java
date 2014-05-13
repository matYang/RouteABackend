package carpool.HttpServer.emailServiceTest;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.HttpServer.asyncTask.emailTask.HotmailEmailTask;
import carpool.HttpServer.asyncTask.emailTask.PseudoEmailTask;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.carpoolDAO.CarpoolDaoUser;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.dbservice.EmailDaoService;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.model.Location;
import carpool.HttpServer.model.User;

public class HotmailTaskTest {

	@Test
	public void test() {
		PseudoEmailTask emailTask = new HotmailEmailTask("teacgersgone@hotmail.com",EmailEvent.forgotPassword, ServerConfig.domainName+"/#forgetPassword/lalala");
		emailTask.execute();
	}

}
