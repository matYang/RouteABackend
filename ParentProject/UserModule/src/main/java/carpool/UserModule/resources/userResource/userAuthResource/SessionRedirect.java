package carpool.UserModule.resources.userResource.userAuthResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.json.JSONObject;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.HttpServer.resources.PseudoResource;

public class SessionRedirect extends PseudoResource{
	
		
	@Get
	public Representation sessionRedirect(Representation entity){
		DebugLog.d("SessionDirect:: Enter session redirect");
		
		User user = null;
		JSONObject jsonObject = new JSONObject();
		String sessionString = "";
		

		try {
			sessionString = this.getSessionString();
			
			DebugLog.d("session redirect receving session string: " + sessionString);
			
			user = AuthDaoService.getUserFromSession(sessionString);
			//if able to login, return toBarUser with valid id, front end will redirect to use session mode
			if (user != null && user.isAbleToLogin()){
				
				jsonObject = JSONFactory.toJSON(user);
			}
			//if not, retun defeault user, front end will detect invalid id==-1 and will use non-session
			else{				
				long arrival_Id = 2;
				String province = "Ontario";			
				String city = "Waterloo";				
				String region = "Downtown UW"; 
				Double lat = 32.123212;
				Double lng = 23.132123;				
				Location defaultLocation= new Location(province,city,region,"Test1","Test11",lat,lng,arrival_Id);				
				jsonObject = JSONFactory.toJSON(new User("","",defaultLocation, Gender.both));
			}
		
		}  catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		}  catch (Exception e) {
			return this.doException(e);
		}

		Representation result = new JsonRepresentation(jsonObject);
        
        this.addCORSHeader();
        return result;
	}

	
}

