package carpool.HttpServer.dbservice;

import java.sql.Connection;
import java.util.*;


import carpool.HttpServer.common.*;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.VerificationState;
import carpool.HttpServer.carpoolDAO.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.message.MessageNotFoundException;
import carpool.HttpServer.exception.transaction.TransactionNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.model.*;
import carpool.HttpServer.model.identityVerification.DriverVerification;
import carpool.HttpServer.model.identityVerification.PassengerVerification;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.HttpServer.model.representation.UserSearchRepresentation;

public class UserDaoService{


	/*****
	 *  User's CRUD
	 * @throws LocationNotFoundException 
	 *****/

	public static ArrayList<User> getAllUsers() throws LocationNotFoundException{
		return CarpoolDaoUser.getAllUsers();
	}

	public static User getUserById(int id,Connection...connections) throws UserNotFoundException, LocationNotFoundException{
		return CarpoolDaoUser.getUserById(id,connections);
	}


	public static User createNewUser(User newUser) throws ValidationException{
		User user = CarpoolDaoUser.addUserToDatabase(newUser);
		//No exception, then create the userSHfile on AWS
		FileService.initializeFileForUser(user.getUserId());

		return user;
	}



	public static User updateUser(User user) throws UserNotFoundException, ValidationException{

		CarpoolDaoUser.UpdateUserInDatabase(user);
		return user;		
	}


	public static void deleteUser(int id) throws UserNotFoundException{
		CarpoolDaoUser.deleteUserFromDatabase(id);
	}


	/*****
	 *  The follows are user's passwords related
	 * @throws LocationNotFoundException 
	 *****/

	public static boolean changePassword(int userId, String oldPassword, String newPassword) throws UserNotFoundException, ValidationException, LocationNotFoundException{
		if(oldPassword.equals(newPassword)){
			throw new ValidationException("新密码不应该和旧密码相同");
		}
		User user = CarpoolDaoUser.getUserById(userId);
		user.setPassword(oldPassword, newPassword);
		try {
			CarpoolDaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			throw new ValidationException("操作失败，请稍后再试");
		}

	}

	public static boolean resetUserPassword(int userId, String newPassword) throws UserNotFoundException{
		try {
			User user = CarpoolDaoUser.getUserById(userId);
			user.setPassword(user.getPassword(), newPassword);
			CarpoolDaoUser.UpdateUserInDatabase(user);
			user = CarpoolDaoUser.getUserById(userId);
			if(user.isPasswordCorrect(newPassword)){
				return true;
			}
		} catch (Exception e) {
			DebugLog.d(e);
		}
		return false;
	}


	/*****
	 * The follows are user relations used separately on API 
	 *****/

	public static boolean watchUser(int userId, int targetUserId) throws UserNotFoundException{

		try {
			CarpoolDaoUser.addToSocialList(userId, targetUserId);
			//send followed Notification
			Notification n = new Notification(EnumConfig.NotificationEvent.watched, targetUserId);
			n.setInitUserId(userId);
			NotificationDaoService.sendNotification(n);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public static boolean deWatchUser(int userId, int targetUserId) throws UserNotFoundException{
		try {
			CarpoolDaoUser.deleteFromSocialList(userId, targetUserId);
			//send notifications
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public static ArrayList<User> getWatchedUsers(int id) throws UserNotFoundException, LocationNotFoundException{
		return CarpoolDaoUser.getSocialListOfUser(id);
	}

	public static boolean isUserWatched(int userId, int targetUserId){
		return CarpoolDaoUser.hasUserInSocialList(userId, targetUserId);
	}

	public static ArrayList<User> searchForUser(UserSearchRepresentation uSR, boolean isLoggedIn, int userId) throws LocationNotFoundException{
		ArrayList<User> searchResult = CarpoolDaoUser.searchForUser(uSR);
		if (isLoggedIn){
			//maybe log the userSearch
		}
		return searchResult;
	}

	public static ArrayList<Message> getHistoryMessageByUserId(int id) throws UserNotFoundException, LocationNotFoundException{
		return CarpoolDaoUser.getUserMessageHistory(id);
	}


	public static ArrayList<Transaction> getTransactionByUserId(int id) throws UserNotFoundException, MessageNotFoundException, LocationNotFoundException{
		return CarpoolDaoTransaction.getAllTransactionByUserId(id);
	}

	public static ArrayList<Notification> getNotificationByUserId(int userId) throws UserNotFoundException, MessageNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		return CarpoolDaoNotification.getByUserId(userId,false);
	}

	public static ArrayList<Notification> getUncheckedNotificationByUserId(int userId) throws UserNotFoundException, MessageNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		return NotificationDaoService.sortNotifications(CarpoolDaoNotification.getByUserId(userId,true));
	}
	
	public static ArrayList<Letter> getUncheckedLettersByUserId(int userId) throws UserNotFoundException, LocationNotFoundException{
		return LetterDaoService.getUncheckedLettersByUserId(userId);
	}

	public static void updateUserSearch(SearchRepresentation userSearch, int id) throws PseudoException {
		User user = getUserById(id);
		user.setSearchRepresentation(userSearch);
		updateUser(user);
	}
	
	
	public static void addDriverVerification(int userId, DriverVerification verification) throws UserNotFoundException, LocationNotFoundException, ValidationException{
		User user = getUserById(userId);
		//if user has pending or verified verification, do not start new verification
		if (user.getDriverVerification() != null && (user.getDriverVerification().getState() == VerificationState.pending || user.getDriverVerification().getState() == VerificationState.verified)){
			throw new ValidationException("已有等待审核或已经通过的司机认证");
		}
		DriverVerification driverVerification = CarpoolDaoDriver.addDriverToDatabases(verification);
		user.setDriverVerificationId(driverVerification.getVerificationId());
		updateUser(user);
	}
	
	public static void addPassengerVerification(int userId, PassengerVerification verification) throws UserNotFoundException, LocationNotFoundException, ValidationException{
		User user = getUserById(userId);
		if (user.getPassengerVerification() != null && (user.getPassengerVerification().getState() == VerificationState.pending || user.getPassengerVerification().getState() == VerificationState.verified)){
			throw new ValidationException("已有等待审核或已经通过的乘客认证");
		}
		PassengerVerification passengerVerification = CarpoolDaoPassenger.addPassengerToDatabases(verification);
		user.setPassengerVerificationId(passengerVerification.getVerificationId());
		updateUser(user);
	}



}
