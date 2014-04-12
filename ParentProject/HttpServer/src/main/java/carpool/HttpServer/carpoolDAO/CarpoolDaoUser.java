package carpool.HttpServer.carpoolDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.common.Parser;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.encryption.SessionCrypto;

import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.exception.identityVerification.identityVerificationNotFound;
import carpool.HttpServer.exception.location.LocationNotFoundException;

import carpool.HttpServer.exception.user.UserNotFoundException;

import carpool.HttpServer.model.Location;


import carpool.HttpServer.model.Message;
import carpool.HttpServer.model.User;
import carpool.HttpServer.model.identityVerification.DriverVerification;
import carpool.HttpServer.model.identityVerification.PassengerVerification;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.HttpServer.model.representation.UserSearchRepresentation;


public class CarpoolDaoUser {

	public static ArrayList<User> searchForUser(UserSearchRepresentation usr) throws LocationNotFoundException{
		ArrayList<User> ulist = new ArrayList<User>();
		String name = usr.getName();
		Gender Gender = usr.getGender();
		long location_Id = usr.getLocationId();

		String query = "SELECT * FROM carpoolDAOUser WHERE name REGEXP ? AND gender LIKE ? AND match_Id LIKE ?;";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setString(1, name);
			stmt.setInt(2,Gender.code);
			stmt.setLong(3, location_Id);
			rs = stmt.executeQuery();			
			while(rs.next()){									
				ulist.add(createUserByResultSet(rs,conn));
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return ulist;

	}

	public static User addUserToDatabase(User user) throws ValidationException{	
		Connection conn = CarpoolDaoBasic.getSQLConnection();
		DriverVerification driver = null;
		PassengerVerification passenger = null;
		try{
			if(user.getDriverVerificationId()>0){
				driver = CarpoolDaoDriver.getDriverVerificationById(user.getDriverVerificationId(),conn);
			}
			if(user.getPassengerVerificationId()>0){
				passenger = CarpoolDaoPassenger.getPassengerVerificationById(user.getPassengerVerificationId(),conn);
			}

		}catch(identityVerificationNotFound ex){
			if(driver==null){
				user.setDriverVerification(null);
				user.setDriverVerificationId(-1);
			}
			if(passenger==null){
				user.setPassengerVerification(null);
				user.setPassengerVerificationId(-1);
			}			
		}

		user.setLocation(CarpoolDaoLocation.addLocationToDatabases(user.getLocation(),conn));
		user.setLocation_Id(user.getLocation().getId());
		String query = "INSERT INTO carpoolDAOUser (password,name,email,phone,qq,gender,birthday,"+
				"imgPath,location_Id,lastLogin,creationTime,"+"emailActivated,phoneActivated,emailNotice,phoneNotice,state,searchRepresentation,"+
				"level,averageScore,totalTranscations,accountId,accountPass,accountToken,accountValue,match_Id,driverVerification_Id,passengerVerification_Id)"+
				" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		PreparedStatement stmt = null;	
		ResultSet rs = null;

		try{			
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, SessionCrypto.encrypt(user.getPassword()));
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPhone());
			stmt.setString(5, user.getQq());			
			stmt.setInt(6, user.getGender().code);
			stmt.setString(7,  DateUtility.toSQLDateTime(user.getBirthday()));
			stmt.setString(8, user.getImgPath());
			stmt.setLong(9, user.getLocation_Id());			
			stmt.setString(10,  DateUtility.toSQLDateTime(user.getLastLogin()));
			stmt.setString(11, DateUtility.toSQLDateTime(user.getCreationTime()));
			stmt.setInt(12, user.isEmailActivated() ? 1:0);
			stmt.setInt(13, user.isPhoneActivated() ? 1:0);
			stmt.setInt(14, user.isEmailNotice() ? 1:0);
			stmt.setInt(15, user.isPhoneNotice() ? 1:0);
			stmt.setInt(16, user.getState().code);
			stmt.setString(17, user.getSearchRepresentation().toSerializedString());
			stmt.setInt(18, user.getLevel());
			stmt.setInt(19, user.getAverageScore());
			stmt.setInt(20, user.getTotalTranscations());			
			stmt.setString(21, user.getAccountId());
			stmt.setString(22, user.getAccountPass());
			stmt.setString(23, user.getAccountToken());		
			stmt.setString(24, user.getAccountValue().toString());
			stmt.setLong(25, user.getLocation().getMatch());
			stmt.setLong(26, user.getDriverVerificationId());
			stmt.setLong(27, user.getPassengerVerificationId());
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			user.setUserId(rs.getInt(1));
		}catch(SQLException e){
			if(e.getMessage().contains("Duplicate")){
				throw new ValidationException("一部分账户内容与其他账户冲突");
			}else{
				e.printStackTrace();
				DebugLog.d(e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			DebugLog.d(e);
			throw new ValidationException("创建用户失败，账户信息错误");
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return user;
	}

	public static void deleteUserFromDatabase(int id) throws UserNotFoundException{
		String query = "DELETE from WatchList where User_userId = '" + id +"'";
		String query2 = "DELETE from carpoolDAOUser where userId = '" + id + "'";
		String query3 = "DELETE from carpoolDAOMessage where ownerId = '" + id +"'";
		String query4 = "DELETE from SocialList where mainUser = '" + id +"'";
		String query5 = "DELETE FROM carpoolDAOTransaction WHERE provider_Id="+id+" OR customer_Id = "+id;

		Statement stmt = null;
		Connection conn = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.createStatement();

			stmt.addBatch(query);
			stmt.addBatch(query2);
			stmt.addBatch(query3);
			stmt.addBatch(query4);
			stmt.addBatch(query5);
			if(stmt.executeBatch()[1]==0){
				throw new UserNotFoundException();
			}
		} catch(SQLException e){
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 
	}

	public static void UpdateUserInDatabase(User user) throws UserNotFoundException, ValidationException{
		Connection conn = CarpoolDaoBasic.getSQLConnection();

		user.setLocation(CarpoolDaoLocation.addLocationToDatabases(user.getLocation(),conn));
		user.setLocation_Id(user.getLocation().getId());
		String query = "UPDATE carpoolDAOUser SET password=?,name=?,email=?,phone=?,qq=?,gender=?,birthday=?," +
				"imgPath=?,location_Id=?,lastLogin=?,"+
				"creationTime=?,emailActivated = ?,phoneActivated = ?,emailNotice = ?,phoneNotice = ?,state = ?,searchRepresentation = ?," +
				"level=?,averageScore=?,totalTranscations=?,accountId=?,accountPass=?,accountToken=?,accountValue=?,match_Id=?,driverVerification_Id=?,passengerVerification_Id=? WHERE userId = ?";

		PreparedStatement stmt = null;

		try{			
			stmt = conn.prepareStatement(query);

			stmt.setString(1, SessionCrypto.encrypt(user.getPassword()));
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPhone());
			stmt.setString(5, user.getQq());			
			stmt.setInt(6, user.getGender().code);
			stmt.setString(7,  DateUtility.toSQLDateTime(user.getBirthday()));
			stmt.setString(8, user.getImgPath());
			stmt.setLong(9, user.getLocation_Id());			
			stmt.setString(10,  DateUtility.toSQLDateTime(user.getLastLogin()));
			stmt.setString(11, DateUtility.toSQLDateTime(user.getCreationTime()));
			stmt.setInt(12, user.isEmailActivated() ? 1:0);
			stmt.setInt(13, user.isPhoneActivated() ? 1:0);
			stmt.setInt(14, user.isEmailNotice() ? 1:0);
			stmt.setInt(15, user.isPhoneNotice() ? 1:0);
			stmt.setInt(16, user.getState().code);
			stmt.setString(17, user.getSearchRepresentation().toSerializedString());
			stmt.setInt(18, user.getLevel());
			stmt.setInt(19, user.getAverageScore());
			stmt.setInt(20, user.getTotalTranscations());			
			stmt.setString(21, user.getAccountId());
			stmt.setString(22, user.getAccountPass());
			stmt.setString(23, user.getAccountToken());		
			stmt.setString(24, user.getAccountValue().toString());
			stmt.setLong(25, user.getLocation().getMatch());	
			stmt.setLong(26, user.getDriverVerificationId());
			stmt.setLong(27, user.getPassengerVerificationId());
			stmt.setInt(28,user.getUserId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new UserNotFoundException();
			}
		} catch(SQLException e){
			DebugLog.d(e);
		} catch (Exception e) {
			throw new ValidationException("更改用户信息失败，账户信息错误");
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		}  	
	}

	public static ArrayList<User> getAllUsers() throws LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser";
		ArrayList<User> users = new ArrayList<User>();

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			while(rs.next()){
				users.add(createUserByResultSet(rs,conn));
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return users;
	}


	public static User getUserById(int id,Connection...connections) throws UserNotFoundException, LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser WHERE userId = ?";
		User user = null;
		PreparedStatement stmt = null;
		Connection conn = CarpoolDaoBasic.getConnection(connections);
		ResultSet rs = null;
		try{		
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if(rs.next()){
				user = createUserByResultSet(rs,conn);
			}else{
				throw new UserNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 

		return user;
	}


	public static User getUserByEmail(String email) throws UserNotFoundException, LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser WHERE email = ?";
		User user = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setString(1, email);
			rs = stmt.executeQuery();
			if(rs.next()){
				user = createUserByResultSet(rs,conn);
			}else{
				throw new UserNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 

		return user;
	}



	public static ArrayList<User> getUserWhoWatchedUser(int userId) throws LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser JOIN SocialList ON (User.userId = SocialList.mainUser AND SocialList.subUser = ?)";
		ArrayList<User> users = new ArrayList<User>();
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, userId);
			rs = stmt.executeQuery();
			while(rs.next()){
				users.add(createUserByResultSet(rs,conn));
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return users;
	}

	protected static User createUserByResultSet(ResultSet rs,Connection...connections) throws SQLException, LocationNotFoundException {
		User user = null;
		Location location = CarpoolDaoLocation.getLocationById(rs.getLong("location_Id"),connections);
		DriverVerification driver = null;
		PassengerVerification passenger = null;
		/*
		try{	
			driver = CarpoolDaoDriver.getDriverVerificationById(rs.getInt("driverVerification_Id"),connections);		
			passenger = CarpoolDaoPassenger.getPassengerVerificationById(rs.getInt("passengerVerification_Id"),connections);	

		}catch(identityVerificationNotFound ex){}
		*/
		try {
			int pvId = rs.getInt("passengerVerification_Id");
			int dvId = rs.getInt("driverVerification_Id");
			if (pvId > 0){
				passenger = CarpoolDaoPassenger.getPassengerVerificationById(pvId,connections);	
			}
			if (dvId > 0){
				driver = CarpoolDaoDriver.getDriverVerificationById(dvId,connections);
			}
			user = new User(rs.getInt("userId"),SessionCrypto.decrypt(rs.getString("password")), rs.getString("name"),
					rs.getString("email"),rs.getString("phone"),rs.getString("qq"),EnumConfig.Gender.fromInt(rs.getInt("gender")),
					DateUtility.DateToCalendar(rs.getTimestamp("birthday")),rs.getString("imgPath"),location,
					DateUtility.DateToCalendar(rs.getTimestamp("lastLogin")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
					rs.getBoolean("emailActivated"),rs.getBoolean("phoneActivated"),rs.getBoolean("emailNotice"),rs.getBoolean("phoneNotice"),
					EnumConfig.UserState.fromInt(rs.getInt("state")),new SearchRepresentation(rs.getString("searchRepresentation")),
					rs.getInt("level"),rs.getInt("averageScore"),rs.getInt("totalTranscations"),pvId,dvId,passenger,driver,
					rs.getString("accountId"),rs.getString("accountPass"),rs.getString("accountToken"),new BigDecimal(rs.getString("accountValue")),rs.getLong("match_Id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}



	public static boolean hasUserInSocialList(int mainUser, int subUser,Connection...connections){
		String query = "SELECT COUNT(*) AS total FROM SocialList WHERE mainUser =" +mainUser+ " AND subUser ="+subUser+";";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			if(rs.next()){
				if(rs.getInt("total")==1){
					return true;
				}else{
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 
		return false;
	}


	public static void addToSocialList(int user,int subUser) {
		Connection conn = CarpoolDaoBasic.getSQLConnection();
		PreparedStatement stmt = null;	
		String query = "SET foreign_key_checks = 0;INSERT INTO SocialList (mainUser,subUser) VALUES(?,?);SET foreign_key_checks = 1;";
		if(!hasUserInSocialList(user,subUser,conn)){						
			try{
				stmt = conn.prepareStatement(query);

				stmt.setInt(1, user);
				stmt.setInt(2, subUser);
				stmt.executeUpdate();		
			}catch(SQLException e){
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, null,true);
			} 
		}
		CarpoolDaoBasic.closeResources(conn, stmt, null,true);
	}	

	public static void deleteFromSocialList(int user,int subUser){
		String query = "SET foreign_key_checks = 0;DELETE FROM SocialList WHERE mainUser =? AND subUser = ?;SET foreign_key_checks = 1;";
		Connection conn = CarpoolDaoBasic.getSQLConnection();
		PreparedStatement stmt = null;	
		if(hasUserInSocialList(user,subUser,conn)){					
			try{				
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, user);
				stmt.setInt(2, subUser);
				stmt.executeUpdate();	

			}catch(SQLException e){
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, null,true);
			} 

		}
		CarpoolDaoBasic.closeResources(conn, stmt, null,true);

	}


	public static ArrayList<User> getSocialListOfUser(int user) throws LocationNotFoundException{
		ArrayList<User> slist = new ArrayList<User>();
		String query = "SELECT * FROM carpoolDAOUser JOIN SocialList ON (carpoolDAOUser.userId = SocialList.subUser AND SocialList.mainUser = ?);";
		PreparedStatement stmt = null;
		Connection conn = null; 
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, user);
			rs = stmt.executeQuery();
			while(rs.next()){
				slist.add(CarpoolDaoUser.createUserByResultSet(rs,conn));
			}
		} catch (SQLException e) {
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return slist;
	}



	public static ArrayList<Message> getUserMessageHistory(int user) throws UserNotFoundException, LocationNotFoundException{
		ArrayList<Message> mlist = new ArrayList<Message>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query ="SELECT * FROM carpoolDAOMessage WHERE ownerId = ?";
		PreparedStatement stmt = null;
		Connection conn = null; 
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, user);
			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = CarpoolDaoMessage.addIds(ilist,rs.getInt("ownerId"));
				mlist.add(CarpoolDaoMessage.createMessagesByResultSetList(rs,conn));
			}
			mlist = CarpoolDaoMessage.getUsersForMessages(ilist, mlist,conn);
		} catch (SQLException e) {
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return mlist;
	}


}
