package carpool.HttpServer.dbservice;

import carpool.HttpServer.asyncRelayExecutor.ExecutorProvider;
import carpool.HttpServer.asyncTask.relayTask.SESRelayTask;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.carpoolDAO.*;
import carpool.HttpServer.encryption.EmailCrypto;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.factory.AuthFactory;
import carpool.HttpServer.model.User;

public class EmailDaoService {

	/**
	 * changes the user's email
	 * 1st: change user's email to newEmail, emailAcvtivated  to false in database, clear userSession in Redis
	 * 2nd: send an activation email to the new Email address
	 * @param userId
	 * @param newEmail
	 * @param sessionString
	 * @return return false if error occurs, eg inside the catch clause
	 * @throws user not found exception if the user id does not exist
	 */
//	public static boolean changeEmail(int userId, String newEmail, String sessionString) throws UserNotFoundException{
//		try {
//			User user = CarpoolDaoUser.getUserById(userId);
//			user.setEmail(newEmail);
//			user.setEmailActivated(false);
//			CarpoolDaoUser.UpdateUserInDatabase(user);
//			AuthDaoService.closeUserSession(sessionString);
//			EmailDaoService.sendActivationEmail(userId, newEmail);
//			return true;
//		} catch (Exception e) {
//			DebugLog.d(e);
//		}
//		return false;
//	}

	/**
	 * @param newEmail  the new email that an activation email should be sent to
	 * store  newUserId - authCode key pair in Redis for fast access, use newUserId as key
	 * use Constants.key_emailActivationAuth + userId as Redis key
	 * @return   return true of successful, return false if unsuccessful
	 */
	public static boolean sendActivationEmail(int userId, String newEmail){
		String authCode = AuthFactory.emailActivation_setAuthCode(userId);	
		String encryptedEmailKey = EmailCrypto.encrypt(userId, authCode);
		
		try {
			SESRelayTask emailTask = new SESRelayTask(newEmail, EmailEvent.activeateAccount, "http://"+ServerConfig.domainName+"/#emailActivation/"+encryptedEmailKey);
			ExecutorProvider.executeRelay(emailTask);
		} catch (Exception e) {
			DebugLog.d(e);
			return false;
		}
		return true;
	}

	/**
	 * when user clicks the activation address, an API call made to server will access this method
	 * change emailActivated field of the user in sql to true
	 */
	public static User activateUserEmail(int userId, String authCode) throws UserNotFoundException{
		if(!AuthFactory.emailActivation_validate(userId, authCode)){
			return null;
		}
		try {
			User user =  CarpoolDaoUser.getUserById(userId);
			user.setEmailActivated(true);
			CarpoolDaoUser.UpdateUserInDatabase(user);
			return user;
		} catch (Exception e) {
			DebugLog.d(e);
		}
		return null;
	}

	/**
	 * checks if the user's email has already been activated
	 */
	public static boolean isUserEmailActivated(int userId) throws UserNotFoundException{
		try {
			User user = CarpoolDaoUser.getUserById(userId);
			return user.isEmailActivated();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * make sure userId exist
	 * resends an activation email to the user
	 */
	public static boolean reSendActivationEmail(int userId) throws LocationNotFoundException{
		User user = null;
		try {
			user = CarpoolDaoUser.getUserById(userId);
		} catch (UserNotFoundException e) {
			DebugLog.d("ReSendActivationEMail:: User does not exsit");
			return false;
		}

		sendActivationEmail(userId, user.getEmail());
		return true;
	}

	/**
	 * send a changedPassWordEmail to the target email, it can be assumed that email passed here is valid and registered
	 */
	public static boolean sendForgotPasswordEmail(String email) throws UserNotFoundException{
		try {
			User user = CarpoolDaoUser.getUserByEmail(email);
			int  userId = user.getUserId();
			String authCode = AuthFactory.forgetPassword_setAuthCode(userId);
			
			String encryptedEmailKey = EmailCrypto.encrypt(userId, authCode);
			SESRelayTask eTask = new SESRelayTask(email, EmailEvent.forgotPassword, ServerConfig.domainName+"/#forgetPassword/"+encryptedEmailKey);
			ExecutorProvider.executeRelay(eTask);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isEmailAvailable(String email) throws LocationNotFoundException{
		try {
			CarpoolDaoUser.getUserByEmail(email);
		} catch (UserNotFoundException e){
			return true;
		}
		return false;
	}

}
