package carpool.UserModule.resources.userResource.userAuthResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONObject;

import carpool.HttpServer.exception.PseudoException;
import carpool.UserModule.resources.PseudoResource;




public class LogOutResource extends PseudoResource{


	@Put
	public Representation logoutAuthentication(Representation entity){
		int id = -1;

		try {
			this.checkEntity(entity);
			id = Integer.parseInt(this.getReqAttr("id"));
			
			this.closeAuthenticationSession(id);
			setStatus(Status.SUCCESS_OK);
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
			return this.doException(e);
		}

		Representation result = new JsonRepresentation(new JSONObject());

        this.addCORSHeader();
        return result;
	}

	
}

