package carpool.HttpServer.interfaces;

import org.json.JSONObject;

public interface PseudoRepresentation extends PseudoModel{
	
	public String toSerializedString();
	
	public JSONObject toJSON();

}
