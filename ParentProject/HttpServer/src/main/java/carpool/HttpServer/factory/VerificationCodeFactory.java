package carpool.HttpServer.factory;

import org.apache.commons.lang3.RandomStringUtils;


public class VerificationCodeFactory {
	
	public static String newSMSVerificationCode(){
		String authCode = RandomStringUtils.randomAlphanumeric(6);
		return authCode.toUpperCase();
	}

}
