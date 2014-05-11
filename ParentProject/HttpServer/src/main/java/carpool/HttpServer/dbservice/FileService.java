package carpool.HttpServer.dbservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.HttpServer.aliyun.aliyunMain;
import carpool.HttpServer.aws.*;
import carpool.HttpServer.configurations.ServerConfig;


public class FileService {

	public static void initializeFileForUser(int userId){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			aliyunMain.createUserFile(userId);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			AwsMain.createUserFile(userId);
		}
		else{
			AwsMain.createUserFile(userId);
		}
	}

	public static void storeSearchRepresentation(SearchRepresentation sr, int userId) throws IOException{
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			aliyunMain.storeSearchHistory(sr, userId);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			AwsMain.storeSearchHistory(sr, userId);
		}
		else{
			AwsMain.storeSearchHistory(sr, userId);
		}
	}

	public static boolean migrateAlltheUsersSearchHistory(){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return aliyunMain.migrateAlltheUsersSearchHistory();
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.migrateAlltheUsersSearchHistory();
		}
		else{
			return AwsMain.migrateAlltheUsersSearchHistory();
		}

	}

	public static ArrayList<SearchRepresentation> getUserSearchHistory(int userId){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return aliyunMain.getUserSearchHistory(userId);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.getUserSearchHistory(userId);
		}
		else{
			return AwsMain.getUserSearchHistory(userId);
		}
	}


	public static String uploadUserProfileImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return aliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.AliyunProfileBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.ProfileBucket);
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.ProfileBucket);
		}
	}


	public static String uploadDriverVerificationLicenseImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return aliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.DriverVerificationBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.DriverVerificationBucket);
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.DriverVerificationBucket);
		}
	}

	public static String uploadPassengerVerificationLicenseFrontImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return aliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.PassengerVerificationBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
	}

	public static String uploadPassengerVerificationLicenseBackImg(int userId, File file, String baseFileName){
		if (ServerConfig.configurationMap.get("env").equals("prod")){
			return aliyunMain.uploadImg(userId, file, baseFileName, ServerConfig.PassengerVerificationBucket);
		}
		else if (ServerConfig.configurationMap.get("env").equals("test")){
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
		else{
			return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
		}
	}

}
