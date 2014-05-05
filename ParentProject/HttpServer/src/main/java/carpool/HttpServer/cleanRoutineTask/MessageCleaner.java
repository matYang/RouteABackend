package carpool.HttpServer.cleanRoutineTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.carpoolDAO.CarpoolDaoMessage;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.message.MessageNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.model.Message;

public class MessageCleaner extends CarpoolDaoMessage {

	
	public static void Clean() throws LocationNotFoundException{
		
    Calendar currentDate = DateUtility.getCurTimeInstance();
    String ct=DateUtility.toSQLDateTime(currentDate);
    
	String query = "SELECT * from carpoolDAOMessage WHERE((isRoundTrip LIKE ? AND arrival_Time < ?) OR(isRoundTrip LIKE ? AND departure_Time < ?))AND messageState LIKE ?;";
	Connection conn = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try{
		conn = CarpoolDaoBasic.getSQLConnection();
		stmt = conn.prepareStatement(query);
		stmt.setInt(1, 1);			
		stmt.setString(2, ct);							
		stmt.setInt(3, 0);	
		stmt.setString(4, ct);
		stmt.setInt(5, 2);			
		rs = stmt.executeQuery();			
			while(rs.next()){	
				Message msg = CarpoolDaoMessage.createMessageByResultSet(rs, false,conn);
			    msg.setState(EnumConfig.MessageState.fromInt(1));
			    CarpoolDaoMessage.UpdateMessageInDatabase(msg,conn);
				}			
	} catch (SQLException e) {
		e.printStackTrace();
		DebugLog.d(e);
	} catch (UserNotFoundException e) {
		e.printStackTrace();
	} catch (MessageNotFoundException e) {		
		e.printStackTrace();
	} finally{
		CarpoolDaoBasic.closeResources(conn, stmt, rs, true);
	}
  }
	
}
