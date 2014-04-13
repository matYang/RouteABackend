package carpool.HttpServer.configurations;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig.DayTimeSlot;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.configurations.EnumConfig.MessageType;
import carpool.HttpServer.model.Location;
import carpool.HttpServer.model.representation.SearchRepresentation;

public class ServerConfig {
	private static final String ENV_VAR_KEY = "C_MAINSERVER_ENV";
	private static final String ENV_REMOTE = "REMOTE";
	public static final boolean isOnLocal;
	
	static{
		String value = System.getenv(ENV_VAR_KEY);
		if (value != null && value.equals(ENV_REMOTE)){
			isOnLocal = false;
		} else{
			isOnLocal = true;
		}
		
	}
	
	public static final int max_recents = 10;
	public static final String domainName = isOnLocal ? "localhost:8015" : "www.routea.ca";
	public static final boolean cookieEnabled = false;
	public static final String cookie_userSession = "userSessionCookie";
	public static final int cookie_maxAge = 5184000; //2 month
	
	
	public static final String urlSeperator = "+";
	public static final String urlSeperatorRegx = "\\+";

	public static final String pathToSearchHistoryFolder = "srHistory/";
	public static final String searchHistoryFileSufix = "_sr.txt";
	
	
	public static final String AccessKeyID="AKIAIE53WCAFSYLUGH2A";
	public static final String SecretKey="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";
	
	/*API level constants*/
	public static final int category_DM = 0;
	public static final String applicationPrefix = "/api";
	public static final String versionPrefix = "/v1.0";
	
	public static final SearchRepresentation getDefaultSearchRepresentation(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr =  sdf.format(DateUtility.getCurTimeInstance().getTime());		
		long departureMatch_Id = 1;
		long arrivalMatch_Id = 2;		
		return new SearchRepresentation("false" + ServerConfig.urlSeperator + departureMatch_Id + ServerConfig.urlSeperator + arrivalMatch_Id + ServerConfig.urlSeperator + dateStr + ServerConfig.urlSeperator + dateStr + ServerConfig.urlSeperator  + "0" + ServerConfig.urlSeperator + "0" + ServerConfig.urlSeperator + "0"+ServerConfig.urlSeperator + dateStr);
	}


	public static final String timeZoneIdNY = "America/New_York";
	public static final String timeZoneIdCH = "asia/shanghai";
	public static final String timeZoneStandard = "UTC";
	
	/* AWS Bucket*/
	public static final String ProfileBucket = "Badstudent";
	public static final String DriverVerificationBucket = "DriverVerification";
	public static final String PassengerVerificationBucket = "PassengerVerification";
	
	/* */
	
	
}
