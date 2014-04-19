package carpool.UserModule.resources.generalResource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.ValidationConfig;
import carpool.UserModule.resources.PseudoResource;


public class FeedBackResource extends PseudoResource{
	
	private static final String fileName = "feedBack.txt";

	public static void writeFile(String message){
		BufferedWriter bw = null;

		try {
		    bw = new BufferedWriter(new FileWriter(fileName, true));
		    bw.write(message);
		    bw.newLine();
		    bw.flush();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		} finally { // always close the file
		    if (bw != null) {
		        try {
		            bw.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}

	@Post
	public String createFeedback(Representation entity) {
		if (entity != null && entity.getSize() < ValidationConfig.max_feedBackLength){
			String jsonMessage = "";
			try {
				jsonMessage = (new JsonRepresentation(entity)).getText() + "\n";
			} catch (IOException e) {
				e.printStackTrace();
			}
			writeFile(jsonMessage);
			DebugLog.d("Feedback received: " + jsonMessage);
		}
		else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		this.addCORSHeader();
		setStatus(Status.SUCCESS_OK);
		return null;
	}

}
