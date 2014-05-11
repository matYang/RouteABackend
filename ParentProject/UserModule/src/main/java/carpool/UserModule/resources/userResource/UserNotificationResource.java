package carpool.UserModule.resources.userResource;

import java.util.ArrayList;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONArray;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.HttpServer.resources.PseudoResource;



public class UserNotificationResource extends PseudoResource{	

    @Get 
    /**
     * @return  the full notifications of the target user
     */
    public Representation getNotificationByUserId() {
    	int id = -1;
    	int intendedUserId = -1;
        JSONArray response = new JSONArray();
        
        try {
        	id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("userId"));
			
			this.validateAuthentication(id);
			
        	ArrayList<Notification> historyNotifications = UserDaoService.getNotificationByUserId(intendedUserId);
        	if (historyNotifications != null){
                response = JSONFactory.toJSON(historyNotifications);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_CONFLICT);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch (Exception e) {
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }
    
}
