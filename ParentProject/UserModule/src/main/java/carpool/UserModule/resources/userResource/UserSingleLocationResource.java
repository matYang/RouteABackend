package carpool.UserModule.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.auth.DuplicateSessionCookieException;
import carpool.HttpServer.exception.auth.SessionEncodingException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;


public class UserSingleLocationResource extends PseudoResource{


	public static Location parseJSON(JSONObject JOlocation){
		Location location = new Location(JOlocation);
        return location;
	}

	@Put
	public Representation changeLocation(Representation entity) {
		int userId = -1;
		JSONObject response = new JSONObject();
		Location location = null;

		try {
			this.checkEntity(entity);
			
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			location = parseJSON((new JsonRepresentation(entity)).getJsonObject());
			if (location != null){
				
				User user = UserDaoService.getUserById(userId);
				user.setLocation(location);
				user = UserDaoService.updateUser(user);
				
				response = user.toJSON();
				setStatus(Status.SUCCESS_OK);

			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}

		} catch (PseudoException e){
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
