package carpool.UserModule.resources.userResource;

import java.util.ArrayList;
import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import carpool.HttpServer.dbservice.FileService;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.representation.SearchRepresentation;
import carpool.HttpServer.resources.PseudoResource;

public class UserSearchHistoryResource extends PseudoResource{

	@Get
	public Representation getUserSearchHistory(){
		int userId = -1;
		ArrayList<SearchRepresentation> searchHistory = new ArrayList<SearchRepresentation>();
		JSONArray resultArr = new JSONArray();		
		try{
			userId = Integer.parseInt(this.getAttribute("id"));			
			searchHistory = FileService.getUserSearchHistory(userId);
			resultArr = JSONFactory.toJSON(searchHistory);

		}  catch (Exception e) {
			return this.doException(e);
		}		

		Representation response = new JsonRepresentation(resultArr);
		addCORSHeader();
		return response;
	}
}
