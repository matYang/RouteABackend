package carpool.HttpServer.carpoolDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import carpool.HttpServer.configurations.DatabaseConfig;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.dbservice.LocationDaoService;
import carpool.HttpServer.exception.location.LocationException;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CarpoolDaoBasic {
	
	private static JedisPool jedisPool; 
	private static HikariDataSource ds = null;
	
	static {
		JedisPoolConfig jedisConfig = new JedisPoolConfig();
		jedisConfig.setTestOnBorrow(false);
		jedisConfig.setMinIdle(5);
		jedisConfig.setMaxWait(4000l);
		jedisPool = new JedisPool(jedisConfig, DatabaseConfig.redisUri, 6379);
		
		
		HikariConfig sqlConfig = new HikariConfig();
		sqlConfig.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		sqlConfig.addDataSourceProperty("url", "jdbc:mysql://"+DatabaseConfig.jdbcUri);
		sqlConfig.addDataSourceProperty("user", "root");
		sqlConfig.addDataSourceProperty("password", DatabaseConfig.sqlPass);
		sqlConfig.setPoolName("SQLPool");
		sqlConfig.setMaxLifetime(1800000l);
		sqlConfig.setAutoCommit(true);
		sqlConfig.setMinimumPoolSize(10);
		sqlConfig.setMaximumPoolSize(100);
		sqlConfig.setConnectionTimeout(10000l);
		ds = new HikariDataSource(sqlConfig);
		
	}	

    
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
    
    public static void returnJedis(Jedis jedis){
    	jedisPool.returnResource(jedis);
    }
    
    public static boolean shouldConnectionClose(Connection...connections){
    	return connections==null || connections.length==0;
    }
    public static Connection getSQLConnection(){
    	Connection connection;
    	try {
			connection = ds.getConnection();
		} catch (SQLException e) {
			DebugLog.d(e);
			throw new RuntimeException(e.getMessage(), e); 
		} 
		return connection;

    }
    
    public static Connection getConnection(Connection...connections){
    	if(connections.length==0){
    		return getSQLConnection();
    	}else if(connections.length==1&& connections[0] instanceof java.sql.Connection){
    		return connections[0];
    	}else return null;
    }
    
    public static void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs,boolean closeconn){
    	try{
			if (stmt != null)  stmt.close();  
			if (conn != null &&closeconn)  conn.close(); 
			if (rs != null) rs.close();
		} catch (SQLException e){
			DebugLog.d("Exception when closing stmt, rs and conn");
			DebugLog.d(e);
		}
    }
    
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs,boolean closeconn){
    	try{
			if (stmt != null)  stmt.close();  
			if (conn != null && closeconn)  conn.close(); 
			if (rs != null) rs.close();
		} catch (SQLException e){
			DebugLog.d("Exception when closing stmt, rs and conn");
			DebugLog.d(e);
		}
    }
    

    public static void clearBothDatabase(){
    	Jedis jedis = getJedis();
        jedis.flushAll();
        returnJedis(jedis);
        
        Statement stmt = null;
		Connection conn = null;
        String query0 = "SET FOREIGN_KEY_CHECKS=0 ";       
        String query1 = "TRUNCATE TABLE SocialList ";
        String query2 = "TRUNCATE TABLE WatchList ";
        String query3 = "TRUNCATE TABLE carpoolDAONotification ";
        String query4 = "TRUNCATE TABLE carpoolDAOMessage";
        String query5 = "TRUNCATE TABLE carpoolDAOUser";
        String query6 = "TRUNCATE TABLE carpoolDAOTransaction";
        String query7 = "TRUNCATE TABLE carpoolDAOLetter";
        String query8 = "TRUNCATE TABLE carpoolDAOLocation";
        String query9 = "TRUNCATE TABLE carpoolDAODriver";
        String query10 = "TRUNCATE TABLE carpoolDAOPassenger";
        String query11 = "TRUNCATE TABLE defaultLocations";
        String query12 = "SET FOREIGN_KEY_CHECKS=1;";
        try{
        	conn = getSQLConnection();
        	stmt = conn.createStatement();
        			
        	stmt.addBatch(query0);
        	stmt.addBatch(query1);
        	stmt.addBatch(query2);
        	stmt.addBatch(query3);
        	stmt.addBatch(query4);
        	stmt.addBatch(query5);
        	stmt.addBatch(query6);
        	stmt.addBatch(query7);
        	stmt.addBatch(query8);
        	stmt.addBatch(query9);
        	stmt.addBatch(query10);
        	stmt.addBatch(query11);
        	stmt.addBatch(query12);
        	stmt.executeBatch();
        } catch(SQLException e) {
        	DebugLog.d(e);
        } finally {
			try{
				if (stmt != null)  stmt.close();  
	            if (conn != null)  conn.close(); 
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
        }
        
		try {
			LocationDaoService.init();
		} catch (LocationException | ValidationException | LocationNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

    }
    
    
}
