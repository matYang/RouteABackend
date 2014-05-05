package carpool.UserModule.resources.transactionResource;

import java.util.ArrayList;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;



public class TransactionResource extends PseudoResource{

	//passes received json into message
	//note that this parseJSON
	protected Transaction parseJSON(JSONObject jsonTransaction){

		Transaction transaction = null;
		try {
			transaction = new Transaction(jsonTransaction.getInt("providerId"), jsonTransaction.getInt("customerId"), jsonTransaction.getInt("messageId"), EnumConfig.PaymentMethod.values()[jsonTransaction.getInt("paymentMethod")], 
					jsonTransaction.getString("customerNote"), jsonTransaction.getString("providerNote"),DateUtility.castFromAPIFormat(jsonTransaction.getString("departure_time")), EnumConfig.DayTimeSlot.values()[jsonTransaction.getInt("departure_timeSlot")], jsonTransaction.getInt("departure_seatsBooked"),
					EnumConfig.TransactionType.values()[jsonTransaction.getInt("transactionType")]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return transaction;
	}
	
	
	@Get
	/**
	 * Retrieve all transactions from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllTransactions() {
		
		ArrayList<Transaction> allTransactions;
		try {
			allTransactions = TransactionDaoService.getAllTransactions();
		} catch (PseudoException e) {
			this.addCORSHeader();
			return this.doPseudoException(e);
		}
		
		JSONArray jsonArray = new JSONArray();
		
		if (allTransactions == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allTransactions);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);
		this.addCORSHeader();
		return result;
	}

	

	@Post
	public Representation createTransaction(Representation entity) {
		
		int id = -1;
        JSONObject newJsonTransaction = new JSONObject();
        
		try {
			this.checkEntity(entity);
			JSONObject jsonTransaction = (new JsonRepresentation(entity)).getJsonObject();
			id = jsonTransaction.getInt("userId");
			this.validateAuthentication(id);
			
	        Transaction transaction = parseJSON(jsonTransaction);
	        if (transaction != null){
	        	if (transaction.getProviderId() == id || transaction.getCustomerId() == id){
		        	//check the state of the message, and if the transaction matches the message
	        		Message message = MessageDaoService.getMessageById(transaction.getMessageId());
	        		if (message.validate() && message.isOpen()){
	        			Transaction creationFeedBack = TransactionDaoService.createNewTransaction(transaction);
			                newJsonTransaction = JSONFactory.toJSON(creationFeedBack);
			                setStatus(Status.SUCCESS_OK);

	        		}
	        		else{
	        			setStatus(Status.CLIENT_ERROR_CONFLICT);
		            }
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_CONFLICT);
	        	}
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
			
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
