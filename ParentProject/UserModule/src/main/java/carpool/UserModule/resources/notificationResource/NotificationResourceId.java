package carpool.UserModule.resources.notificationResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONObject;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;


public class NotificationResourceId extends PseudoResource{
	

    @Get 
    /**
     * @return  get the notification from a user
     */
    public Representation getNotificationById() {
    	int id = -1;
        JSONArray response = new JSONArray();
        
        try {
        	//notificationId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			this.validateAuthentication(id);
			
        	ArrayList<Notification> notifications = UserDaoService.getNotificationByUserId(id);
        	if (notifications != null){
        		response = JSONFactory.toJSON(notifications);
                setStatus(Status.SUCCESS_OK);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }
    

    @Put 
    public Representation checkNotification(Representation entity) {
        int userId = -1;
        int notificationId = -1;
        JSONObject response = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
			notificationId = Integer.parseInt(this.getReqAttr("id"));
			userId = (new JsonRepresentation(entity)).getJsonObject().getInt("userId");
			
			this.validateAuthentication(userId);
				
			Notification n = NotificationDaoService.checkNotification(notificationId, userId);
			response = JSONFactory.toJSON(n);
			setStatus(Status.SUCCESS_OK);
			DebugLog.d("Check notification with id: " + notificationId);

		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}

		this.addCORSHeader();
        return new JsonRepresentation(response);
    }
    
    
    @Delete
    public Representation deleteNotification() {
    	//TODO authentication
    	int notificationId = -1;
		try {
			notificationId = Integer.parseInt(this.getReqAttr("id"));

			NotificationDaoService.deleteNotification(notificationId);
			setStatus(Status.SUCCESS_OK);
			DebugLog.d("DeleteNotification with id: " + notificationId);
			
        } catch (PseudoException e){
        	this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
		
		this.addCORSHeader();
		return new JsonRepresentation(new JSONObject());
    }

}
