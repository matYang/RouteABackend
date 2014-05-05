package carpool.UserModule.resources.userResource.userAuthResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONObject;

import carpool.HttpServer.common.Validator;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.encryption.EmailCrypto;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;



public class ForgetPasswordResource extends PseudoResource{

	@Get
	public Representation forgetPassword(){
        JSONObject response = new JSONObject(); 
        boolean isSent = false;
        String email = "";
        
        try {
        	email = this.getQueryVal("email");
        	
        	//check the email format first
        	if (Validator.isEmailFormatValid(email)){
        		//if the email format is valid, check if this email has been registered
        		if (!EmailDaoService.isEmailAvailable(email)){
        			//this will need a translation from email to id, another SQL query, wonder if could be improved
            		isSent = EmailDaoService.sendForgotPasswordEmail(email);
            		if (isSent){
            			setStatus(Status.SUCCESS_OK);
            		}
            		else{
            			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
            		}
            	}
            	else{
            		setStatus(Status.CLIENT_ERROR_CONFLICT);
            	}
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        	}
		} 

        catch (PseudoException e){
        	this.addCORSHeader();
			return this.doPseudoException(e);
        } 
        catch (Exception e) {
			return this.doException(e);
		}
        response = JSONFactory.toJSON(isSent);
        Representation result = new JsonRepresentation(response);
        
        this.addCORSHeader();
        return result;
	}
	
	@Post
	public Representation findPassword(Representation entity){
		boolean passwordChanged = false;
		boolean isValid = false;
		JSONObject jsonString = null;
		int userId = -1;
		String newPassword = "";
		String confirmNewPassword = "";
		String authCode = "";
		User user = null;
		JSONObject jsonResponse = new JSONObject();
		
		try {
			this.checkEntity(entity);

			jsonString = (new JsonRepresentation(entity)).getJsonObject();
			
			String key = jsonString.getString("key");
			key = java.net.URLEncoder.encode(key, "utf-8");
			String[] keys = EmailCrypto.decrypt(key);
			userId = Integer.parseInt(keys[0]);
			authCode = keys[1];
			
			newPassword = jsonString.getString("newPassword");
			confirmNewPassword = jsonString.getString("confirmNewPassword");
			
			isValid = AuthDaoService.isResetPasswordValid(userId, authCode);
			if (isValid){
				if (Validator.isPasswordFormatValid(newPassword) && newPassword.equals(confirmNewPassword)){
					passwordChanged = UserDaoService.resetUserPassword(userId, newPassword);
					
					if (passwordChanged){
						//the only thing to check here is for email, try combine them into one
						user = UserDaoService.getUserById(userId);
						
						if (user.isAbleToLogin()){
							
							this.closeAuthenticationSession(userId);
				            this.addAuthenticationSession(userId);
							
							jsonResponse = JSONFactory.toJSON(user);
						}
						else{
							jsonResponse = JSONFactory.toJSON(user);
							throw new ValidationException("User can not log in");
						}
					}
					else{
						setStatus(Status.SERVER_ERROR_INTERNAL);
					}
				}
				else{
					setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
				}
			}
			else{
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}

			
			
		} catch (PseudoException e) {
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch (Exception e) {
			return this.doException(e);
		}
		

		Representation result = new JsonRepresentation(jsonResponse);
        this.addCORSHeader();
        return result;
	}


}
