package carpool.HttpServer.carpoolDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.NotificationState;
import carpool.HttpServer.configurations.EnumConfig.NotificationStateChangeActon;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.message.MessageNotFoundException;
import carpool.HttpServer.exception.notification.NotificationNotFoundException;
import carpool.HttpServer.exception.transaction.TransactionNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.model.Message;
import carpool.HttpServer.model.Notification;
import carpool.HttpServer.model.Transaction;
import carpool.HttpServer.model.User;

public class CarpoolDaoNotification {

	public static Notification addNotificationToDatabase(Notification notification){
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String query="INSERT INTO carpoolDAONotification(target_UserId,origin_UserId,origin_MessageId,origin_TransactionId,notificationState,historyDeleted,creationTime,notificationEvent)values(?,?,?,?,?,?,?,?)";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, notification.getTargetUserId());
			stmt.setInt(2, notification.getInitUserId());
			stmt.setInt(3, notification.getMessageId());
			stmt.setInt(4, notification.getTransactionId());
			stmt.setInt(5, notification.getState().code);
			stmt.setInt(6, notification.isHistoryDeleted() ? 1 : 0);
			stmt.setString(7, DateUtility.toSQLDateTime(notification.getCreationTime()));
			stmt.setInt(8, notification.getNotificationEvent().code);
			stmt.executeUpdate();	 
			rs = stmt.getGeneratedKeys();
			rs.next();
			notification.setNotificationId(rs.getInt(1));
		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return notification;
	}

	public static ArrayList<Notification> addNotificationsToDatabase(ArrayList<Notification> notifications,Connection...connections){
		String query="INSERT INTO carpoolDAONotification(target_UserId,origin_UserId,origin_MessageId,origin_TransactionId,notificationState,historyDeleted,creationTime,notificationEvent)values(?,?,?,?,?,?,?,?)";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			for(Notification n:notifications){
				stmt.setInt(1, n.getTargetUserId());
				stmt.setInt(2, n.getInitUserId());
				stmt.setInt(3, n.getMessageId());
				stmt.setInt(4, n.getTransactionId());
				stmt.setInt(5, n.getState().code);
				stmt.setInt(6, n.isHistoryDeleted() ? 1 : 0);
				stmt.setString(7, DateUtility.toSQLDateTime(n.getCreationTime()));
				stmt.setInt(8, n.getNotificationEvent().code);
				stmt.executeUpdate();	 
				rs = stmt.getGeneratedKeys();
				rs.next();
				n.setNotificationId(rs.getInt(1));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 
		return notifications;

	}
	public static void updateNotificationInDatabase(Notification notification) throws NotificationNotFoundException{
		String query = "UPDATE carpoolDAONotification SET target_UserId=?,origin_UserId=?,origin_MessageId=?,origin_TransactionId=?,notificationState = ?,historyDeleted = ?,creationTime = ?,notificationEvent=? where notification_Id =?";
		PreparedStatement stmt = null;
		Connection conn = null;

		try{	
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, notification.getTargetUserId());
			stmt.setInt(2, notification.getInitUserId());
			stmt.setInt(3, notification.getMessageId());
			stmt.setInt(4, notification.getTransactionId());
			stmt.setInt(5, notification.getState().code);
			stmt.setInt(6, notification.isHistoryDeleted() ? 1 : 0);
			stmt.setString(7, DateUtility.toSQLDateTime(notification.getCreationTime()));
			stmt.setInt(8, notification.getNotificationEvent().code);
			stmt.setInt(9, notification.getNotificationId());			 			
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new NotificationNotFoundException();
			} 

		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 		

	}

	public static Notification getNotificationById(int notificationId) throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		String query="select * from carpoolDAONotification where notification_Id=?";
		Notification notification = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, notificationId);
			rs = stmt.executeQuery();
			if(rs.next()){
				notification = createNotificationByResultSet(rs,"ForOneNotification",conn);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return notification;
	}

	public static void deleteNotification(int notificationId) throws NotificationNotFoundException{
		String query = "UPDATE carpoolDAONotification SET historyDeleted = 1 where notification_Id =?";
		PreparedStatement stmt = null;
		Connection conn = null;

		try{			
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, notificationId);				 			
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new NotificationNotFoundException();
			} 

		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}	finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 

	}

	public static ArrayList<Notification> getAllNotifications() throws MessageNotFoundException, UserNotFoundException, 
	TransactionNotFoundException, LocationNotFoundException{
		ArrayList<Notification> list = new ArrayList<Notification>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();
		ArrayList<Integer> tlist = new ArrayList<Integer>();
		String query = "select * from carpoolDAONotification";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("origin_UserId"));
				milist = addIds(milist,rs.getInt("origin_MessageId"));
				tlist = addIds(tlist,rs.getInt("origin_TransactionId"));
				list.add(createNotificationByResultSet(rs));
			}
			list = FillNotification(ilist,milist,tlist,list,conn);
		}catch(SQLException e){
			e.printStackTrace();
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 

		return list;	
	}

	private static ArrayList<Notification> FillNotification(
			ArrayList<Integer> ilist, ArrayList<Integer> milist,
			ArrayList<Integer> tlist, ArrayList<Notification> list,Connection...connections) throws LocationNotFoundException {
		HashMap<Integer,User> userMap = new HashMap<Integer,User>();
		HashMap<Integer,Message> msgMap = new HashMap<Integer,Message>();
		HashMap<Integer,Transaction> tranMap = new HashMap<Integer,Transaction>();

		if(ilist.size()>0){
			userMap = CarpoolDaoTransaction.getUsersHashMap(ilist,connections);
		}

		if(milist.size()>0){
			msgMap = CarpoolDaoTransaction.getMsgHashMap(milist,connections);
		}		

		if(tlist.size()>0){
			tranMap = getTranMap(tlist,connections);
		}

		for(int i=0;i<list.size();i++){
			list.get(i).setInitUser(userMap.get(list.get(i).getInitUserId()));
			list.get(i).setMessage(msgMap.get(list.get(i).getMessageId()));
			list.get(i).setTransaction(tranMap.get(list.get(i).getTransactionId()));
		}
		return list;	

	}	

	private static HashMap<Integer,Transaction> getTranMap(ArrayList<Integer> list,Connection...connections) throws LocationNotFoundException{
		HashMap<Integer,Transaction> map = new HashMap<Integer,Transaction>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		String query = "SELECT * FROM carpoolDAOTransaction where ";
		for(int i=0;i<list.size()-1;i++){
			query += "transaction_Id = ? OR ";
		}
		query += "transaction_Id = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);

			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			rs = stmt.executeQuery();

			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("provider_Id"));
				milist = addIds(milist,rs.getInt("customer_Id"));
				tlist.add(CarpoolDaoTransaction.createTransactionByResultSet(rs,conn));
			}
			CarpoolDaoTransaction.fillTransactions(ilist, milist, tlist,conn);
			int ind=0;
			while(ind<tlist.size()){
				map.put(tlist.get(ind).getTransactionId(), tlist.get(ind));
				ind++;
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 

		return map;
	}


	public static void modifyNotificationByIdList(ArrayList<Integer> idList, int userId, NotificationStateChangeActon action) throws NotificationNotFoundException{
		String query;
		PreparedStatement stmt = null;
		Connection conn = null;

		if(idList.size()<=0)return;

		if(action == NotificationStateChangeActon.check){
			query = "UPDATE carpoolDAONotification SET notificationState = ? where (target_UserId = ? and notification_Id = ?)";
		}else if(action == NotificationStateChangeActon.delete){
			query = "DELETE from carpoolDAONotification  where (target_UserId = ? and notification_Id = ?) ";
		}else return;

		for(int i = 1;i < idList.size();i++){
			query += " or (target_UserId = ? and notification_Id = ?)";
		}

		if(action == NotificationStateChangeActon.check){
			try {		
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, NotificationState.read.code);
				for(int k = 0;k < idList.size()*2;k += 2){
					stmt.setInt(k+2, userId);
					stmt.setInt(k+3, idList.get(k/2));
				}
				int recordsAffected = stmt.executeUpdate();
				if(recordsAffected==0){
					throw new NotificationNotFoundException();
				} 
			} catch (SQLException e) {				
				e.printStackTrace();
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, null,true);
			} 

		}else{
			try {		
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query);				
				for(int k = 0;k < idList.size()*2;k += 2){
					stmt.setInt(k+1, userId);
					stmt.setInt(k+2, idList.get(k/2));
				}
				int recordsAffected = stmt.executeUpdate();
				if(recordsAffected==0){
					throw new NotificationNotFoundException();
				} 
			} catch (SQLException e) {				
				e.printStackTrace();
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, null,true);
			} 
		}

	}

	private static ArrayList<Integer> addIds(ArrayList<Integer> ilist, int id) {		
		if(id !=-1 && !ilist.contains(id)){
			ilist.add(id);
		}
		return ilist;
	}

	public static ArrayList<Notification> getByUserId(int userId, boolean onlyGetUnchecked) throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		ArrayList<Notification> list = new ArrayList<Notification>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();
		ArrayList<Integer> tlist = new ArrayList<Integer>();
		String query = onlyGetUnchecked ? "select * from carpoolDAONotification where target_UserId = ? AND notificationState = 0 AND historyDeleted = 0;" : "select * from carpoolDAONotification where target_UserId = ? AND historyDeleted = 0;";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1,userId);
			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("origin_UserId"));
				milist = addIds(milist,rs.getInt("origin_MessageId"));
				tlist = addIds(tlist,rs.getInt("origin_TransactionId"));
				list.add(createNotificationByResultSet(rs));
			}

			list = FillNotification(ilist,milist,tlist,list,conn);

		}catch(SQLException e){
			e.printStackTrace();
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return list;

	}

	private static Notification createNotificationByResultSet(ResultSet rs,String str,Connection...connections) throws SQLException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException {
		User origin = rs.getInt("origin_UserId")==-1 ? null : CarpoolDaoUser.getUserById(rs.getInt("origin_UserId"),connections);
		Message msg = rs.getInt("origin_MessageId")==-1 ? null : CarpoolDaoMessage.getMessageById(rs.getInt("origin_MessageId"),connections);
		Transaction transaction = rs.getInt("origin_TransactionId")==-1 ? null : CarpoolDaoTransaction.getTransactionById(rs.getInt("origin_TransactionId"),connections);
		Notification notification = null;
		notification = new Notification(rs.getInt("notification_Id"), EnumConfig.NotificationEvent.fromInt(rs.getInt("notificationEvent")),rs.getInt("target_UserId"),
				rs.getInt("origin_UserId"),rs.getInt("origin_MessageId"),rs.getInt("origin_TransactionId"),EnumConfig.NotificationState.fromInt(rs.getInt("notificationState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));
		notification.setInitUser(origin);
		notification.setMessage(msg);
		notification.setTransaction(transaction);
		return notification;
	}

	private static Notification createNotificationByResultSet(ResultSet rs) throws SQLException{
		return new Notification(rs.getInt("notification_Id"), EnumConfig.NotificationEvent.fromInt(rs.getInt("notificationEvent")),rs.getInt("target_UserId"),
				rs.getInt("origin_UserId"),rs.getInt("origin_MessageId"),rs.getInt("origin_TransactionId"),EnumConfig.NotificationState.fromInt(rs.getInt("notificationState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));

	}

}
