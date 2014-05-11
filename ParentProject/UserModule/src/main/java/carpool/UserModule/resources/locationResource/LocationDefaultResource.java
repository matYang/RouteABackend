package carpool.UserModule.resources.locationResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.HttpServer.dbservice.LocationDaoService;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.representation.DefaultLocationRepresentation;
import carpool.HttpServer.resources.PseudoResource;

public class LocationDefaultResource extends PseudoResource{
	
	@Get
	public Representation getDefaultLocation() {
		
		ArrayList<DefaultLocationRepresentation> defaultLocations = new ArrayList<DefaultLocationRepresentation>();
		
		try{
			defaultLocations = LocationDaoService.getDefaultLocations();
		} catch (Exception e){
			return this.doException(e);
		}
		
		JSONArray jsonArray = JSONFactory.toJSON(defaultLocations);
		
		Representation result = new JsonRepresentation(jsonArray);
				
		this.addCORSHeader(); 
		return result;
	}

}
