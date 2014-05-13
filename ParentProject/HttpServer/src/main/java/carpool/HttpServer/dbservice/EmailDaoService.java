package carpool.HttpServer.dbservice;

import carpool.HttpServer.aliyun.AliyunMain;
import carpool.HttpServer.asyncRelayExecutor.ExecutorProvider;
import carpool.HttpServer.asyncTask.emailTask.HotmailEmailTask;
import carpool.HttpServer.asyncTask.emailTask.PseudoEmailTask;
import carpool.HttpServer.asyncTask.emailTask.SESEmailTask;
import carpool.HttpServer.asyncTask.emailTask.SendCloudEmailTask;
import carpool.HttpServer.aws.AwsMain;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.carpoolDAO.*;
import carpool.HttpServer.encryption.EmailCrypto;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.factory.AuthFactory;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.User;

public class EmailDaoService {


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
			/*
			PseudoEmailTask emailTask;
			if (ServerConfig.configurationMap.get("env").equals("prod")){
				emailTask = new HotmailEmailTask(newEmail, EmailEvent.activeateAccount, "http://"+ServerConfig.domainName+"/#emailActivation/"+encryptedEmailKey);
			}
			else if (ServerConfig.configurationMap.get("env").equals("test")){
				emailTask = new SESEmailTask(newEmail, EmailEvent.activeateAccount, "http://"+ServerConfig.domainName+"/#emailActivation/"+encryptedEmailKey);
			}
			else{
				emailTask = new HotmailEmailTask(newEmail, EmailEvent.activeateAccount, "http://"+ServerConfig.domainName+"/#emailActivation/"+encryptedEmailKey);
			}
			*/
			SendCloudEmailTask emailTask = new SendCloudEmailTask(newEmail, EmailEvent.activeateAccount, "http://"+ServerConfig.domainName+"/#emailActivation/"+encryptedEmailKey);
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
			
			SendCloudEmailTask emailTask = new SendCloudEmailTask(email, EmailEvent.forgotPassword, ServerConfig.domainName+"/#forgetPassword/"+encryptedEmailKey);
			ExecutorProvider.executeRelay(emailTask);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//TODO currently not sending the notification text
	public static boolean sendNotificationEmail(String email, String notificationText){
		SendCloudEmailTask emailTask = new SendCloudEmailTask(email, EmailEvent.notification, "");
		ExecutorProvider.executeRelay(emailTask);
		return true;
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
