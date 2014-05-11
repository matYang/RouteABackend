package carpool.UserModule.resources.userResource;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.HttpServer.dbservice.UserDaoService;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.resources.PseudoResource;

public class UserIsWatchedResource extends PseudoResource{
	
	@Get
	public Representation isUserWatched(){
		int id = -1;
        int intendedUserId = -1;
        Boolean isUserWatched = false;
        JSONObject response = new JSONObject();
        
        try {
			//id from which is list is to be retrieved from
        	id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			//make sure the current user has logged in
			this.validateAuthentication(id);
			
        	isUserWatched = UserDaoService.isUserWatched(id, intendedUserId);
        	response = JSONFactory.toJSON(isUserWatched);
        	
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

}
