package carpool.UserModule.resources.dianmingResource;

import java.util.ArrayList;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.json.JSONArray;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.auth.AccountAuthenticationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.UserModule.resources.PseudoResource;


public class DMSearchResource extends PseudoResource{

	@Get
	public Representation searchMessages() {
		
		JSONArray response = new JSONArray();
		
		try {
			String srStr = this.getPlainQueryVal("searchRepresentation");
			int userId = Integer.parseInt(this.getQueryVal("userId"));
			
			DebugLog.d("SearchMessage received searchRepresentation: " + srStr);
			
			boolean login = false;
			try{
				this.validateAuthentication(userId);
				login = true;
				
				if (userId <= 0){
					login = false;
				}
			}
			catch (AccountAuthenticationException e){
				login = false;
			}
			
			SearchRepresentation sr = srStr != null ? new SearchRepresentation(srStr) : ServerConfig.getDefaultSearchRepresentation();
			
			//no need to valdiate location anymore, as an id will only have match or no-match
			ArrayList<Message> searchResult = new ArrayList<Message>();
			searchResult = MessageDaoService.primaryMessageSearch(sr, login, userId);
			response = JSONFactory.toJSON(searchResult);
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e){
			return this.doException(e);
		}
		
		Representation result = new JsonRepresentation(response);
		this.addCORSHeader();
		return result;
	}

	

}
