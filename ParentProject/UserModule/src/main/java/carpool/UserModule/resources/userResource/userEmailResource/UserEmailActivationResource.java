package carpool.UserModule.resources.userResource.userEmailResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONObject;

import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.encryption.EmailCrypto;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;



public class UserEmailActivationResource extends PseudoResource{

	@Get
	public Representation activiateUserEmail(){
		int userId = -1;
		String authCode = "";
        User user = null;
        JSONObject response = new JSONObject(); 

        
        try {
        	String encryptedKey = this.getPlainQueryVal("key");
        	encryptedKey = java.net.URLEncoder.encode(encryptedKey, "utf-8");
        	String[] decodedKey = EmailCrypto.decrypt(encryptedKey);
        	
        	userId = Integer.parseInt(decodedKey[0]);
        	authCode = decodedKey[1];
        	
        	//activate email anyways
        	user = EmailDaoService.activateUserEmail(userId, authCode);
        	
        	if (user != null && user.isEmailActivated() && user.isAbleToLogin()){
        		this.closeAuthenticationSession(userId);
        		this.addAuthenticationSession(userId);
  
        		setStatus(Status.SUCCESS_OK);
        		response = JSONFactory.toJSON(user);
        	}
        	else if (user == null){
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
        	else if (!user.isEmailActivated()){
        		//alert: authCode has been expired
        		response = JSONFactory.toJSON(user);
        		throw new ValidationException("Email Authcode expired");
        	}
        	else if (!user.isAbleToLogin()){
        		response = JSONFactory.toJSON(user);
        		throw new ValidationException("User can not log in");
        	}
        	
		} catch (UserNotFoundException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        }  catch (Exception e) {
			return this.doException(e);
		}
        
        
        Representation result = new JsonRepresentation(response);

        this.addCORSHeader();
        return result;
		
	}

}
