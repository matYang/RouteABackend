package carpool.UserModule.resources.userResource.userEmailResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONObject;

import carpool.HttpServer.common.Validator;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.factory.JSONFactory;
import carpool.UserModule.resources.PseudoResource;

public class UserEmailResource extends PseudoResource{

	
	@Get 
    /**used when user registers email or tries to change the email later on
     * @return  true or false, true if email available, false if the user name has already been taken
     */
    public Representation verifyEmail(){
		boolean isFormatCorrect = false;
        boolean isAvailable = false;
        JSONObject jsonObject = new JSONObject();
        String email = "";
        
        try {
        	email = this.getQueryVal("email");
        	isFormatCorrect = Validator.isEmailFormatValid(email);
        	if (isFormatCorrect){
        		isAvailable = EmailDaoService.isEmailAvailable(email);
        		
        		jsonObject = JSONFactory.toJSON(isAvailable);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        	}
        	
		} catch (Exception e) {
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader();
        return result;
    }

}
