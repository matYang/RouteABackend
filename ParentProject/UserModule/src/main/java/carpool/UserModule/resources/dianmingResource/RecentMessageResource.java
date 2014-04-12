package carpool.UserModule.resources.dianmingResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONArray;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;


public class RecentMessageResource extends PseudoResource{

	@Get
	/**
	 * Retrieve all messages from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getRecentMessages() throws LocationNotFoundException {
		
		ArrayList<Message> recentMessages = MessageDaoService.getRecentMessages();
		JSONArray jsonArray = new JSONArray();

		jsonArray = JSONFactory.toJSON(recentMessages);
		DebugLog.d(jsonArray.toString());
		setStatus(Status.SUCCESS_OK);
		
		
		Representation result = new JsonRepresentation(jsonArray);
		this.addCORSHeader();
		return result;
	}


}
