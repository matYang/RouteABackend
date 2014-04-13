package carpool.AdminModule.resources.adminResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONObject;

import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.MessageState;
import carpool.HttpServer.configurations.EnumConfig.TransactionState;
import carpool.HttpServer.configurations.EnumConfig.UserState;
import carpool.HttpServer.dbservice.admin.AdminService;
import carpool.HttpServer.exception.PseudoException;
import carpool.AdminModule.resources.PseudoResource;



public class AdminStateChangeResource extends PseudoResource{
	

    @Get
    public Representation updateTransaction(Representation entity) {
    	String access_admin = "";
    	String moduleName = "";
    	int id = -1;
        int stateIndex = -1;
        
		try {
			
			access_admin = this.getQueryVal("access_admin");
			moduleName = this.getQueryVal("moduleName");
			id = Integer.parseInt(this.getQueryVal("id"));
			stateIndex = Integer.parseInt(this.getQueryVal("stateIndex"));
			
			if (!access_admin.equals(EnumConfig.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("invalid authorization value");
				
			}
			
			if (moduleName.equalsIgnoreCase("user")){
				AdminService.changeUserState(id, UserState.fromInt(stateIndex));
			}
			else if (moduleName.equalsIgnoreCase("message")){
				AdminService.changeMessageState(id, MessageState.fromInt(stateIndex));
			}
			else if (moduleName.equalsIgnoreCase("transaction")){
				AdminService.changeTransactionState(id, TransactionState.fromInt(stateIndex));
			}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(new JSONObject());
        this.addCORSHeader();
        return result;
    }
    
    String access_admin = "";
    
}
