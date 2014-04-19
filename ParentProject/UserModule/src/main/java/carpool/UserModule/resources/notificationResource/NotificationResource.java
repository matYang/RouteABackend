package carpool.UserModule.resources.notificationResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONArray;

import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.message.MessageNotFoundException;
import carpool.HttpServer.exception.transaction.TransactionNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;


public class NotificationResource extends PseudoResource{

	@Get
	/**
	 * Retrieve all notifications from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllNotifications() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException {

		ArrayList<Notification> allNotifications = NotificationDaoService.getAllNotifications();
		JSONArray jsonArray = new JSONArray();
		
		if (allNotifications == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allNotifications);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);
		this.addCORSHeader();
		return result;
	}


}
