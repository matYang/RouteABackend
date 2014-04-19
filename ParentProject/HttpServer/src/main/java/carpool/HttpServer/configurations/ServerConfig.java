package carpool.HttpServer.configurations;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.model.representation.SearchRepresentation;

public class ServerConfig {
	private static final String ENV_VAR_KEY = "RA_MAINSERVER_ENV";
	private static final String ENV_TEST = "RA_TEST";
	private static final String ENV_PROD = "RA_PROD";
	
	//use concurrent hashmap to guarantee thread safety
	public static final Map<String, String> configurationMap = new ConcurrentHashMap<String, String>();
	
	
	static{
		String value = System.getenv(ENV_VAR_KEY);
		System.out.println("Server starting under " + value + " envrionment");
		if (value == null || (!value.equals(ENV_TEST) && !value.equals(ENV_PROD))){
			//local env
			configurationMap.put("env", "local");
			configurationMap.put("jdbcUri", "badstudent.mysql.rds.aliyuncs.com:3306/db19r3708gdzx5d1?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes");
			configurationMap.put("redisUri", "localhost");
			configurationMap.put("domainName", "localhost:8015");
			configurationMap.put("redisSearchHistoryUpbound", "6");
			configurationMap.put("sqlPass", "LIFECENTRICo2o");
			configurationMap.put("sqlUser", "test");
		} 
		else if (value.equals(ENV_TEST)){
			//test env
			configurationMap.put("env", "test");
			configurationMap.put("jdbcUri", "badstudent.cunzg2tyzsud.us-west-2.rds.amazonaws.com:3306/test?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes");
			configurationMap.put("redisUri", "redisserver.ppomgu.0001.usw2.cache.amazonaws.com");
			configurationMap.put("domainName", "www.routea.ca");
			configurationMap.put("redisSearchHistoryUpbound", "50");
			configurationMap.put("sqlPass", "badstudent");
			configurationMap.put("sqlUser", "test");
		}
		else{
			//prod env
			configurationMap.put("env", "prod");
			configurationMap.put("jdbcUri", "badstudent.mysql.rds.aliyuncs.com:3306/db19r3708gdzx5d1?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes");
			configurationMap.put("redisUri", "localhost");
			configurationMap.put("domainName", "www.routea.ca");
			configurationMap.put("redisSearchHistoryUpbound", "50");
			configurationMap.put("sqlPass", "LIFECENTRICo2o");
			configurationMap.put("sqlUser", "db19r3708gdzx5d1");
		}
		
	}
	
	public static final int max_recents = 10;
	public static final String domainName = configurationMap.get("domainName");
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
