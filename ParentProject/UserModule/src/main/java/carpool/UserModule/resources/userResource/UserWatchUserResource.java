package carpool.UserModule.resources.userResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONObject;

import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;


public class UserWatchUserResource extends PseudoResource{  

    @Get 
    public Representation getWatchedUsers() {
        int id = -1;
        int intendedUserId = -1;
        ArrayList<User> watchedUsers = new ArrayList<User>();
        JSONArray response = new JSONArray();
        
        try {
        	//id of the current logged in user
			id = Integer.parseInt(this.getReqAttr("id"));
			//id from which is list is to be retrieved from
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			//make sure the current user has logged in
			this.validateAuthentication(id);
			
        	watchedUsers = UserDaoService.getWatchedUsers(intendedUserId);
        	if (watchedUsers != null){
        		response = JSONFactory.toJSON(watchedUsers);
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
	public Representation watchUser(Representation entity) {
		int id = -1;
		int targetUserId = -1;
		JSONObject response = new JSONObject();
		boolean watched = false;
		boolean deWatched = false;

		try {
			this.checkEntity(entity);

			id = Integer.parseInt(this.getReqAttr("id"));
			JSONObject temp = (new JsonRepresentation(entity)).getJsonObject();
			targetUserId = temp.getInt("targetUserId");
			String action = temp.getString("action");

			this.validateAuthentication(id);
			if (action.equals("watch")){
				watched = UserDaoService.watchUser(id, targetUserId);
				if (watched) {
					setStatus(Status.SUCCESS_OK);
				} else {
					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
			else if(action.equals("dewatch")){
				deWatched = UserDaoService.deWatchUser(id, targetUserId);
				
				if (deWatched) {
					setStatus(Status.SUCCESS_OK);
				} else {
					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
			else{
				throw new ValidationException("Invalid action");
			}

		} catch (PseudoException e) {
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch (Exception e) {
			return this.doException(e);
		}

		Representation result =  new JsonRepresentation(response);
		this.addCORSHeader();
        return result;
    }


}
