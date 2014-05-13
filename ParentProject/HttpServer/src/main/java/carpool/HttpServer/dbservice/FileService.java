package carpool.HttpServer.dbservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.HttpServer.aliyun.AliyunMain;
import carpool.HttpServer.aws.*;
import carpool.HttpServer.configurations.ServerConfig;


public class FileService {

	public static void initializeFileForUser(int userId){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			AliyunMain.createUserFile(userId);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			AwsMain.createUserFile(userId);
		}
		else{
			AliyunMain.createUserFile(userId);
		}
	}

	public static void storeSearchRepresentation(SearchRepresentation sr, int userId) throws IOException{
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			AliyunMain.storeSearchHistory(sr, userId);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			AwsMain.storeSearchHistory(sr, userId);
		}
		else{
			AliyunMain.storeSearchHistory(sr, userId);
		}
	}

	public static boolean migrateAlltheUsersSearchHistory(){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return AliyunMain.migrateAlltheUsersSearchHistory();
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.migrateAlltheUsersSearchHistory();
		}
		else{
			return AliyunMain.migrateAlltheUsersSearchHistory();
		}

	}

	public static ArrayList<SearchRepresentation> getUserSearchHistory(int userId){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return AliyunMain.getUserSearchHistory(userId);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.getUserSearchHistory(userId);
		}
		else{
			return AliyunMain.getUserSearchHistory(userId);
		}
	}


	public static String uploadUserProfileImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.AliyunProfileBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.ProfileBucket);
		}
		else{
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.AliyunProfileBucket);
		}
	}


	public static String uploadDriverVerificationLicenseImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.DriverVerificationBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.DriverVerificationBucket);
		}
		else{
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.DriverVerificationBucket);
		}
	}

	public static String uploadPassengerVerificationLicenseFrontImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.PassengerVerificationBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
		else{
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.PassengerVerificationBucket);
		}
	}

	public static String uploadPassengerVerificationLicenseBackImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.PassengerVerificationBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
		else{
			return AliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.PassengerVerificationBucket);
		}
	}

}
