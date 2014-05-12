package carpool.AdminModule.adminDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.RandomStringUtils;
import carpool.AdminModule.configurations.EnumConfig.AdminPrivilege;
import carpool.AdminModule.configurations.EnumConfig.AdminStatus;
import carpool.AdminModule.exception.AdminAccountNotFound;
import carpool.AdminModule.model.AdminAccount;

import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.configurations.ServerConfig;


public class AdminAccountDAO {

	public static AdminAccount addAdminAccountToDatabases(AdminAccount adminacc){

		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "INSERT INTO adminAccount(name,address,reference,gender,status,password,privilege," +
				"phone,email,idNum,imgPath,birthday,creationTime)"+"values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		String reference = RandomStringUtils.randomAlphanumeric(ServerConfig.AdminRefLength);
		adminacc.setReference(reference);

		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, adminacc.getName());
			stmt.setString(2, adminacc.getAddress());
			stmt.setString(3, adminacc.getReference());
			stmt.setInt(4, adminacc.getGender().code);
			stmt.setInt(5, adminacc.getStatus().code);
			stmt.setString(6, adminacc.getPassword());
			stmt.setInt(7, adminacc.getPrivilege().code);
			stmt.setString(8, adminacc.getPhone());
			stmt.setString(9, adminacc.getEmail());
			stmt.setString(10, adminacc.getIdNum());
			stmt.setString(11, adminacc.getImgPath());
			stmt.setString(12, DateUtility.toSQLDateTime(adminacc.getBirthday()));
			stmt.setString(13, DateUtility.toSQLDateTime(adminacc.getCreationTime()));
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			adminacc.setAccountId(rs.getInt(1));
		}
		catch(SQLException e){
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}
		return adminacc;
	}

	public static void updateAdminAccountInDatabases(AdminAccount adminacc) throws AdminAccountNotFound{

		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "UPDATE adminAccount SET name=?,address=?,reference=?,password=?,gender=?,status=?,privilege=?," +
				"phone=?,email=?,idNum=?,imgPath=?,birthday=?,creationTime=? where accountId=?";

		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, adminacc.getName());
			stmt.setString(2, adminacc.getAddress());
			stmt.setString(3, adminacc.getReference());
			stmt.setString(4, adminacc.getPassword());
			stmt.setInt(5, adminacc.getGender().code);
			stmt.setInt(6, adminacc.getStatus().code);			
			stmt.setInt(7, adminacc.getPrivilege().code);
			stmt.setString(8, adminacc.getPhone());
			stmt.setString(9, adminacc.getEmail());
			stmt.setString(10, adminacc.getIdNum());
			stmt.setString(11, adminacc.getImgPath());
			stmt.setString(12, DateUtility.toSQLDateTime(adminacc.getBirthday()));
			stmt.setString(13, DateUtility.toSQLDateTime(adminacc.getCreationTime()));
			stmt.setInt(14, adminacc.getAccountId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new AdminAccountNotFound();
			}			
		}
		catch(SQLException e){
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}	
	}

	public static void deleteAdminAccountFromDatabases(AdminAccount adminacc){
		String query = "DELETE from adminAccount where accountId = ?";
		PreparedStatement stmt = null;
		Connection conn = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, adminacc.getAccountId());
			stmt.executeUpdate();	
		}catch (SQLException e) {
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 
	}

	public static AdminAccount getAdminAccountFromDatabases(String reference) throws AdminAccountNotFound{

		String query = "SELECT * FROM adminAccount where reference = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		AdminAccount adminacc = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, reference);
			rs = stmt.executeQuery();
			if(rs.next()){
				adminacc = createAdminAccountByResultSet(rs);
			}else{
				throw new AdminAccountNotFound();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}	
		return adminacc;
	}

	public static AdminAccount getAdminAccountFromDatabases(int accountId) throws AdminAccountNotFound{

		String query = "SELECT * FROM adminAccount where accountId = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		AdminAccount adminacc = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, accountId);
			rs = stmt.executeQuery();
			if(rs.next()){
				adminacc = createAdminAccountByResultSet(rs);
			}else{
				throw new AdminAccountNotFound();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}	
		return adminacc;
	}

	private static AdminAccount createAdminAccountByResultSet(ResultSet rs) throws SQLException {
		return new AdminAccount(rs.getInt("accountId"),rs.getString("name"),rs.getString("reference"),rs.getString("password"),
				Gender.fromInt(rs.getInt("gender")),AdminPrivilege.fromInt(rs.getInt("privilege")),
				AdminStatus.fromInt(rs.getInt("status")),rs.getString("address"),rs.getString("phone"),rs.getString("email"),
				DateUtility.DateToCalendar(rs.getTimestamp("birthday")),rs.getString("idNum"),rs.getString("imgPath"),
				DateUtility.DateToCalendar(rs.getTimestamp("creationTime")));
	}

}
