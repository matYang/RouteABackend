package carpool.UserModule.resources.userResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.HttpServer.dbservice.UserDaoService;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.Letter;
import carpool.HttpServer.resources.PseudoResource;

public class UserUncheckedLetterResource extends PseudoResource{

    @Get 
    /**
     * @return  the unread letters of the current user
     */
    public Representation getUncheckedNotificationByUserId() {
    	int id = -1;
        JSONArray response = new JSONArray();
        
        try {
        	id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
			
        	ArrayList<Letter> uncheckedLetters = UserDaoService.getUncheckedLettersByUserId(id);
        	if (uncheckedLetters != null){
                response = JSONFactory.toJSON(uncheckedLetters);
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
