package carpool.HttpServer.dbservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import carpool.HttpServer.asyncRelayExecutor.ExecutorProvider;
import carpool.HttpServer.asyncTask.relayTask.LetterRelayTask;
import carpool.HttpServer.carpoolDAO.CarpoolDaoLetter;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig.LetterDirection;
import carpool.HttpServer.configurations.EnumConfig.LetterType;
import carpool.HttpServer.exception.letter.LetterNotFoundException;
import carpool.HttpServer.exception.letter.LetterOwnerNotMatchException;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.model.Letter;
import carpool.HttpServer.model.User;



public class LetterDaoService{


	public static ArrayList<Letter> getAllLetters() throws UserNotFoundException, LocationNotFoundException{

		return CarpoolDaoLetter.getAllLetters();
	}


	public static ArrayList<Letter> getUserLetters(int curUserId, int targetUserId, LetterType type, LetterDirection direction) throws UserNotFoundException, LocationNotFoundException, LetterNotFoundException{
		ArrayList<Letter> letters = CarpoolDaoLetter.getUserLetters(curUserId, targetUserId, type, direction);
		checkLetter(curUserId, targetUserId);
		return letters;
	}

	public static ArrayList<User> getLetterUsers(int userId) throws UserNotFoundException, LocationNotFoundException{

		return CarpoolDaoLetter.getLetterUsers(userId);
	}


	public static Letter getLetterById(int letterId) throws LetterNotFoundException, UserNotFoundException, LocationNotFoundException{

		return CarpoolDaoLetter.getLetterById(letterId);
	}


	public static Letter sendLetter(Letter letter) throws UserNotFoundException, LocationNotFoundException{
		Letter createdLetter = CarpoolDaoLetter.addLetterToDatabases(letter);
		if (createdLetter != null){
			LetterRelayTask lTask = new LetterRelayTask(letter);
			ExecutorProvider.executeRelay(lTask);
			//TODO
			//new notificaiton
			//notificationDaoService send notification
		}
		return createdLetter;
	}


	public static void checkLetter(int userId, int targetUserId){
		CarpoolDaoLetter.checkLetter(userId, targetUserId);
	}



	public static void deleteLetter(int letterId) throws LetterNotFoundException, LetterOwnerNotMatchException{
		CarpoolDaoLetter.deleteLetter(letterId);
	}

	public static ArrayList<Letter> sortLetters(ArrayList<Letter> list){
		Collections.sort(list, new Comparator<Letter>() {
			@Override public int compare(final Letter letter1, final Letter letter2) {
				return DateUtility.toSQLDateTime(letter1.getSend_time()).compareTo(DateUtility.toSQLDateTime(letter2.getSend_time()));
			}
		});
		return list;
	}

	public static ArrayList<Letter> getUncheckedLettersByUserId(int userId) throws UserNotFoundException, LocationNotFoundException{
		return sortLetters(CarpoolDaoLetter.getUncheckedLettersByUserId(userId));
	}
}

