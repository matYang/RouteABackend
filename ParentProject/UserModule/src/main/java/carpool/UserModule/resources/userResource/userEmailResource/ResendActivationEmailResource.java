package carpool.UserModule.resources.userResource.userEmailResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONObject;

import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.resources.PseudoResource;


public class ResendActivationEmailResource extends PseudoResource{

	@Get
	public Representation reSendUserEmail(){
		int userId = -1;
        boolean isActivated = false;
        JSONObject response = new JSONObject(); 
        boolean isSent = false;;
        
        try {
        	userId = Integer.parseInt(this.getReqAttr("id"));
        	
        	isActivated = EmailDaoService.isUserEmailActivated(userId);
        	
        	//if not activated already, assuming without activation login is impossible and does not need to be checked
        	if (!isActivated){
        		isSent = EmailDaoService.reSendActivationEmail(userId);
        		if (isSent){
        			setStatus(Status.SUCCESS_OK);
        		}
        		else{
        			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        		}
        		response = JSONFactory.toJSON(isSent);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_CONFLICT);
        	}
        	
		} catch(PseudoException e){
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
