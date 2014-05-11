package carpool.UserModule.resources.dianmingResource;

import java.util.ArrayList;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONArray;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.HttpServer.resources.PseudoResource;


public class DMAutoMatchResource extends PseudoResource{        

    @Get
    /**
    * @return a list of messages that are auto-matched to the target message
    */
    public Representation getMessageAutoMatching() {
        int curMsgId = -1;
        int curUserId = -1;
        JSONArray response = new JSONArray();
        
        try {
            curMsgId = Integer.parseInt(this.getReqAttr("id"));
            curUserId = Integer.parseInt(this.getQueryVal("userId"));
            
            this.validateAuthentication(curUserId);
                    
            ArrayList<Message> matchingMessages = MessageDaoService.autoMatching(curMsgId, curUserId);
            if (matchingMessages != null){
                response = JSONFactory.toJSON(matchingMessages);
            }
            else{
                setStatus(Status.CLIENT_ERROR_CONFLICT);
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
