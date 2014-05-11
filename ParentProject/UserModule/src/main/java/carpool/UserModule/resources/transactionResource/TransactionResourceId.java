package carpool.UserModule.resources.transactionResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONObject;

import carpool.HttpServer.configurations.EnumConfig.TransactionStateChangeAction;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.HttpServer.resources.PseudoResource;



public class TransactionResourceId extends PseudoResource{
	

    @Get 
    public Representation getTransactionById() {
    	int id = -1;
    	int transactionId = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
			transactionId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			this.validateAuthentication(id);
		
        	Transaction transaction = TransactionDaoService.getUserTransactionById(transactionId, id);
        	if (transaction != null){
                jsonObject = JSONFactory.toJSON(transaction);
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
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader(); 
        return result;
    }
    
    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation updateTransaction(Representation entity) {
        int userId = -1;
        int transactionId = -1;
        int stateIndex = -1;
        JSONObject newJsonTransaction = new JSONObject();
        Transaction transaction = null;
        
		try {
			checkEntity(entity);
			
			transactionId = Integer.parseInt(this.getReqAttr("id"));
			JSONObject hashHolder = (new JsonRepresentation(entity)).getJsonObject();
			userId = hashHolder.getInt("userId");
			stateIndex = hashHolder.getInt("stateChangeAction");
			
			this.validateAuthentication(userId);
			
			TransactionStateChangeAction stateChangeAction = TransactionStateChangeAction.fromInt(stateIndex);
			
	        if (stateChangeAction != null){
	        	switch(stateChangeAction){
	        		case cancel:
	        			transaction = TransactionDaoService.cancelTransaction(transactionId, userId);
	        			break;
	        			
	        		 case report:
	        		 	transaction = TransactionDaoService.reportTransaction(transactionId, userId);
	        		 	break;
	        			
	        		case evaluate:
	        			int score = hashHolder.getInt("score");
	        			transaction = TransactionDaoService.evaluateTransaction(transactionId, userId, score);
	        			break;
	        			
	        		default:
	        			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        			break;
	        	}
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	        newJsonTransaction = JSONFactory.toJSON(transaction);

		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(newJsonTransaction);
        this.addCORSHeader(); 
        return result;
    }

}
