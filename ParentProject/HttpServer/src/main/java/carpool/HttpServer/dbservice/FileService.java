package carpool.HttpServer.dbservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.HttpServer.aws.*;
import carpool.HttpServer.configurations.ServerConfig;


public class FileService {
	
	public static void initializeFileForUser(int userId){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
		}
		else{
			AwsMain.createUserFile(userId);
		}
	}

	public static void storeSearchRepresentation(SearchRepresentation sr, int userId) throws IOException{
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
		}
		else{
			AwsMain.storeSearchHistory(sr, userId);
		}
	}
	
	public static boolean migrateAlltheUsersSearchHistory(){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
			return false;
		}
		else{
			return AwsMain.migrateAlltheUsersSearchHistory();
		}
	}
	
	public static ArrayList<SearchRepresentation> getUserSearchHistory(int userId){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
			return null;
		}
		else{
			return AwsMain.getUserSearchHistory(userId);
		}
	}


	public static String uploadUserProfileImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
			return null;
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.ProfileBucket);
		}
	}
	
	
	public static String uploadDriverVerificationLicenseImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
			return null;
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.DriverVerificationBucket);
		}
	}
	
	public static String uploadPassengerVerificationLicenseFrontImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
			return null;
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
	}
	
	public static String uploadPassengerVerificationLicenseBackImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			//TODO use OSS sdk
			return null;
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
	}
	
}
