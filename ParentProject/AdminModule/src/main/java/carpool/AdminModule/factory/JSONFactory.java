package carpool.AdminModule.factory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.AdminModule.model.AdminAccount;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.DatabaseConfig;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.configurations.EnumConfig.PaymentMethod;
import carpool.HttpServer.interfaces.*;




//a class that will make JSONObject or JSONArray based on given classes
//serves as the converter on API data return
public class JSONFactory {

	public static JSONObject toJSON(PseudoModel obj){
		if (obj == null){
			DebugLog.d("JSONFactory::toJSON_Model receving null obj");
			return new JSONObject();
		}
		else if (obj instanceof AdminAccount){
			return ((AdminAccount)obj).toJSON();
		}
		else{
			return new JSONObject();
		}

	}

	public static JSONObject toJSON(String s){
		JSONObject json = new JSONObject();
		try {
			json.put("val", s);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static JSONObject toJSON(boolean b){
		JSONObject json = new JSONObject();
		try {
			json.put("val", b);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static JSONObject toJSON(Gender g){
		JSONObject json = new JSONObject();
		try {
			json.put("val", g);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}


	public static JSONObject toJSON(PaymentMethod p){
		JSONObject json = new JSONObject();
		try {
			json.put("val", p);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static JSONObject toJSON(int i){
		JSONObject json = new JSONObject();
		try {
			json.put("val", i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static JSONObject toJSON(long i){
		JSONObject json = new JSONObject();
		try {
			json.put("val", i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static JSONObject toJSON(Entry<Long, Integer> entry) {
		JSONObject json = new JSONObject();
		try {
			json.put("searchNum", entry.getValue());
			json.put("Default Location Id", entry.getKey());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static JSONArray toJSON(ArrayList<? extends PseudoModel> objs){
		ArrayList<JSONObject> temps = new ArrayList<JSONObject>();
		if (objs == null){
			DebugLog.d("JSONFactory::toJSON_ArrayList receving null objs");
			return new JSONArray();
		}
		for (int i = 0; i < objs.size(); i++){
			if (objs.get(i) != null){
				JSONObject jsonResult = toJSON(objs.get(i));
				temps.add(jsonResult);

			}
		}
		return new JSONArray(temps);
	}

	public static JSONArray toJSON(HashMap<String, ArrayList<Entry<Long,Integer>>> entireMap) {		
		ArrayList<JSONArray> array = new ArrayList<JSONArray>();
		if (entireMap == null){
			DebugLog.d("JSONFactory::toJSON_ArrayList receving null objs");
			return new JSONArray();
		}

		if (entireMap.get(DatabaseConfig.UserSRDeparture) != null){
			JSONArray jsonArray = toJSONArray(entireMap.get(DatabaseConfig.UserSRDeparture));
			array.add(jsonArray);				
		}
		if (entireMap.get(DatabaseConfig.UserSRArrival) != null){
			JSONArray jsonArray = toJSONArray(entireMap.get(DatabaseConfig.UserSRArrival));
			array.add(jsonArray);				
		}
		if (entireMap.get(DatabaseConfig.DatabasesDeparture) != null){
			JSONArray jsonArray = toJSONArray(entireMap.get(DatabaseConfig.DatabasesDeparture));
			array.add(jsonArray);				
		}
		if (entireMap.get(DatabaseConfig.DatabasesArrival) != null){
			JSONArray jsonArray = toJSONArray(entireMap.get(DatabaseConfig.DatabasesArrival));
			array.add(jsonArray);				
		}

		return new JSONArray(array);
	}

	public static JSONArray toJSONArray(ArrayList<Entry<Long, Integer>> arrayList) {
		ArrayList<JSONObject> temps = new ArrayList<JSONObject>();
		if (arrayList == null){
			DebugLog.d("JSONFactory::toJSON_ArrayList receving null objs");
			return new JSONArray();
		}
		for (int i = 0; i < arrayList.size(); i++){
			if (arrayList.get(i) != null){
				JSONObject jsonResult = toJSON(arrayList.get(i));
				temps.add(jsonResult);

			}
		}
		return new JSONArray(temps);
	}	


}
