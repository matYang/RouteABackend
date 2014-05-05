package carpool.UserModule.resources.userResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.HttpServer.dbservice.UserDaoService;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.auth.AccountAuthenticationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.User;
import carpool.HttpServer.model.representation.UserSearchRepresentation;
import carpool.UserModule.resources.PseudoResource;

public class UserSearchUserResource extends PseudoResource{
	
	@Get
	public Representation searchForUsers(){
		
		JSONArray response = new JSONArray();
		
		try {
			String srStr = this.getQueryVal("userSearchRepresentation");
			int userId = Integer.parseInt(this.getQueryVal("userId"));
			
			boolean login = false;
			try{
				this.validateAuthentication(userId);
				login = true;
			}
			catch (AccountAuthenticationException e){
				login = false;
			}
			
			if (srStr != null){
				UserSearchRepresentation userSearchRepresentation = new UserSearchRepresentation(srStr);

				ArrayList<User> searchResult = new ArrayList<User>();
				searchResult = UserDaoService.searchForUser(userSearchRepresentation, login, userId);
				response = JSONFactory.toJSON(searchResult);
			}
			
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
