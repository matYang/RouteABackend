package carpool.AdminModule.resources.adminResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.VerificationType;
import carpool.HttpServer.dbservice.admin.AdminService;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.identityVerification.DriverVerification;
import carpool.HttpServer.model.identityVerification.PassengerVerification;
import carpool.AdminModule.resources.PseudoResource;

public class AdminVerificationResource extends PseudoResource {
	
	@Get
    public Representation updateTransaction(Representation entity) {
    	String access_admin = "";
    	int typeIndex = 0;
        JSONArray verificationArray = new JSONArray();
        
		try {
			access_admin = this.getQueryVal("access_admin");
			typeIndex = Integer.parseInt(this.getQueryVal("typeIndex"));
			
			if (!access_admin.equals(EnumConfig.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("Invalid authorization value");
				
			}
			
			VerificationType verificationType = VerificationType.fromInt(typeIndex);
			if (verificationType == VerificationType.driver){
				ArrayList<DriverVerification> verifications = AdminService.getPendingDriverVerification();
				verificationArray = JSONFactory.toJSON(verifications);
			}
			else if (verificationType == VerificationType.passenger){
				ArrayList<PassengerVerification> verifications = AdminService.getPendingPassengerVerification();
				verificationArray = JSONFactory.toJSON(verifications);
			}
			
		} catch(PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(verificationArray);
        this.addCORSHeader();
        return result;
    }
	
	
	@Post
	public Representation verify(Representation entity) {
		JSONObject request = new JSONObject();
		String access_admin = "";
		int typeIndex = -1;
		int verificationId = -1;
		boolean isVerified = false;

		try {
			this.checkEntity(entity);
			request = (new JsonRepresentation(entity)).getJsonObject();
			access_admin = request.getString("access_admin");
			typeIndex = request.getInt("typeIndex");
			verificationId = request.getInt("verificationId");
			isVerified = request.getBoolean("isVerified");
			
			if (!access_admin.equals(EnumConfig.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("Invalid authorization value");
			}
			
			VerificationType verificationType = VerificationType.fromInt(typeIndex);
			if (verificationType == VerificationType.driver){
				//TODO reviewer ids
				if (!isVerified){
					AdminService.rejectDriverVerification(verificationId, 1);
				}
				else{
					AdminService.verifyDriverVerification(verificationId, DateUtility.castFromAPIFormat(request.getString("issueDate")), DateUtility.castFromAPIFormat(request.getString("expireDate")), 1);
				}
			}
			else if (verificationType == VerificationType.passenger){
				if (!isVerified){
					AdminService.rejectPassengerVerification(verificationId, 1);
				}
				else{
					AdminService.verifyPassengerVerification(verificationId, DateUtility.castFromAPIFormat(request.getString("expireDate")), 1);
				}
			}

		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
			return this.doException(e);
		}
		
		setStatus(Status.SUCCESS_OK);
		Representation result = new StringRepresentation("SUCCESS", MediaType.TEXT_PLAIN);
		this.addCORSHeader();
		return result;
	}

}
