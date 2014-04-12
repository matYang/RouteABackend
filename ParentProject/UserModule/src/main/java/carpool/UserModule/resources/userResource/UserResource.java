package carpool.UserModule.resources.userResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.common.Validator;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;


public class UserResource extends PseudoResource{


	protected User parseJSON(Representation entity) throws ValidationException, LocationNotFoundException{
		JSONObject jsonUser = null;
		User user = null;
		
		try {
			jsonUser = (new JsonRepresentation(entity)).getJsonObject();
			
			String password = jsonUser.getString("password");
			String email = jsonUser.getString("email");
			Location location = new Location(jsonUser.getJSONObject("location"));
			Gender g = EnumConfig.Gender.fromInt(jsonUser.getInt("gender"));
			Calendar birthday = DateUtility.castFromAPIFormat(jsonUser.getString("birthday"));
			String name = jsonUser.getString("name");
			
			
			//if email is used, do not register
			if (!EmailDaoService.isEmailAvailable(email)){
				throw new ValidationException("该邮箱已被注册");
			}
			
			user = new User(password, email, location, g);
			user.setBirthday(birthday);
			user.setName(name);
		} catch (JSONException|IOException e) {
			throw new ValidationException("无效数据格式");
		}
		return user;
	}
	
	@Get
	/**
	 * Retrieve all users from server. This API is intended solely for testing
	 */
	public Representation getAllUsers() throws LocationNotFoundException {

		ArrayList<User> allUsers = UserDaoService.getAllUsers();
		JSONArray jsonArray = JSONFactory.toJSON(allUsers);
		
		Representation result = new JsonRepresentation(jsonArray);

		this.addCORSHeader();
		return result;
	}
	

	@Post
	public Representation createUser(Representation entity) {
		
		JSONObject newJsonUser = new JSONObject();
		User creationFeedBack = null;
		
		try{
			this.checkEntity(entity);
			User newUser = parseJSON(entity);
			
			if (newUser.validate_create()){
				creationFeedBack = UserDaoService.createNewUser(newUser);

				DebugLog.d("@Post::resources::createUser: available: " + creationFeedBack.getEmail() + " id: " +  creationFeedBack.getUserId());
				
				EmailDaoService.sendActivationEmail(creationFeedBack.getUserId(), creationFeedBack.getEmail());
				newJsonUser = JSONFactory.toJSON(creationFeedBack);

			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}

		} catch(PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		}

		Representation result = new JsonRepresentation(newJsonUser);
		
		this.addCORSHeader(); 
		return result;
	}

}
