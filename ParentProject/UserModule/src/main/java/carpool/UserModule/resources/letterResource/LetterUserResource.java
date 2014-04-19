package carpool.UserModule.resources.letterResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import carpool.HttpServer.dbservice.LetterDaoService;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.User;
import carpool.UserModule.resources.PseudoResource;

public class LetterUserResource extends PseudoResource{

        
    @Get 
    public Representation getLetterUsers() {
        int userId = -1;
        JSONArray jsonUsers = new JSONArray();
        
        try {
                userId = Integer.parseInt(this.getReqAttr("id"));
                this.validateAuthentication(userId);
                        
                ArrayList<User> users = LetterDaoService.getLetterUsers(userId);
                
                if (users != null){
                    jsonUsers  = JSONFactory.toJSON(users);
                    setStatus(Status.SUCCESS_OK);
                }
                else{
                        setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                }
                        
                } catch (PseudoException e){
                        this.addCORSHeader();
                        return this.doPseudoException(e);
        } catch (Exception e){
                        return this.doException(e);
                }
        
        Representation result = new JsonRepresentation(jsonUsers);
        this.addCORSHeader();
        return result;
    }
}
