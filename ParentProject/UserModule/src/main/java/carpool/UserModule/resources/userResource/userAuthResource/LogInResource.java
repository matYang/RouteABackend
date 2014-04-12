package carpool.UserModule.resources.userResource.userAuthResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.auth.DuplicateSessionCookieException;
import carpool.HttpServer.exception.auth.SessionEncodingException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;
import carpool.UserModule.resources.dianmingResource.DMResource;


public class LogInResource extends PseudoResource{

	
	@Post
	public Representation loginAuthentication(Representation entity){
		JSONObject jsonString = null;
		User user = null;
		JSONObject jsonObject = new JSONObject();
		String email = "";
		String password = "";
		Series<Cookie> cookies = this.getRequest().getCookies();
		

		try {
			this.checkEntity(entity);
			
			jsonString = (new JsonRepresentation(entity)).getJsonObject();
			email = jsonString.getString("email");
			password = jsonString.getString("password");
			
			DebugLog.d("Log in, receving paramters: " + email + " " + password);
			user = AuthDaoService.authenticateUserLogin(email, password);
			
			if (user != null && user.isAbleToLogin()){
				
				//UserCookieResource.validateCookieSession(user.getUserId(), cookies);
					
				this.closeAuthenticationSession(user.getUserId());
	            this.addAuthenticationSession(user.getUserId());
				
				jsonObject = JSONFactory.toJSON(user);
				setStatus(Status.SUCCESS_OK);
			}
			else{
				//if user failed authentication, do not return topBarUser
				if (user == null){
					setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				}
				//if user fails account state validation, eg email not activated, still return topBarUser
				else{
					jsonObject = JSONFactory.toJSON(user);
					throw new ValidationException("User can not log in");
				}
			}
		
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } 
		catch (Exception e) {
			return this.doException(e);
		}
			
		
		Representation result = new JsonRepresentation(jsonObject);

        this.addCORSHeader();
        return result;
	}

	
}
