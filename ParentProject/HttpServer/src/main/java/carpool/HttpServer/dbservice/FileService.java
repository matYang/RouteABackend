package carpool.HttpServer.dbservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.HttpServer.aws.*;
import carpool.HttpServer.configurations.ServerConfig;


public class FileService {
	
	public static void initializeFileForUser(int userId){
		AwsMain.createUserFile(userId);
	}

	public static void storeSearchRepresentation(SearchRepresentation sr, int userId) throws IOException{
		AwsMain.storeSearchHistory(sr, userId);
	}


	public static String uploadUserProfileImg(int userId, File file, String baseFileName){
		return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.ProfileBucket);
	}
	
	
	public static String uploadDriverVerificationLicenseImg(int userId, File file, String baseFileName){
		return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.DriverVerificationBucket);
	}
	
	public static String uploadPassengerVerificationLicenseFrontImg(int userId, File file, String baseFileName){
		return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
	}
	
	public static String uploadPassengerVerificationLicenseBackImg(int userId, File file, String baseFileName){
		return AwsMain.uploadImg(userId, file, baseFileName,ServerConfig.PassengerVerificationBucket);
	}
}