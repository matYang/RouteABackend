package carpool.HttpServer.configurations;

public class DatabaseConfig {
	

	public static final String jdbcUri = ServerConfig.configurationMap.get("jdbcUri");
	public static final String redisUri = ServerConfig.configurationMap.get("redisUri");

	
	//redis related
	public static final String key_emailActivationAuth = "ea";
	public static final String key_forgetPasswordAuth = "fp";
	public static final String redisSeperator = "+";
	public static final String redisSeperatorRegex = "\\+";
	public static final long session_updateThreshold = 259200000l;		//3 days
	public static final long session_expireThreshold = 604800000l;		//7 days
	public static final long emailActivation_expireThreshold = 604800000l;		//7 days
	public static final long forgetPassword_expireThreshold = 604800000l;		//7 days
	public static final int session_sequenceLength = 15;
	public static final int emailActivation_sequenceLength = 15;
	public static final int forgetPassword_sequenceLength = 30;
	public static final String redisSearchHistoryPrefix = "usrSRH";
	public static final int redisSearchHistoryUpbound = Integer.parseInt(ServerConfig.configurationMap.get("redisSearchHistoryUpbound"));
	
	
	/*sql*/	
	public static final String UserSRDeparture = "UserSRDeparture";
	public static final String UserSRArrival = "UserSRArrival";
	public static final String DatabasesDeparture = "DatabasesDeparture";
	public static final String DatabasesArrival = "DatabasesArrival";
	public static final String sqlPass = ServerConfig.configurationMap.get("sqlPass");

	
	

}
