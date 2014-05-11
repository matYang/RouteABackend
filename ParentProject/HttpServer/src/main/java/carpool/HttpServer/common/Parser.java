package carpool.HttpServer.common;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

public class Parser {
	
	//Pricelist: Numb-numb-
	public static String listToString(ArrayList<?> list){
		String serializedList = null;
		for(int i=0; i <list.size(); i++){
			if (serializedList == null){
				serializedList = "";
			}
			serializedList += list.get(i).toString() +"-";
		}
		return serializedList;
	}
	
	public static ArrayList<?> stringToList(String listString, Object optionFlag){
		String[] strArray = listString != null ? listString.split("-") : null;
		if (optionFlag instanceof Integer){
			ArrayList<Integer> intList = new ArrayList<Integer>();

			for (int i = 0; strArray != null && i < strArray.length; i++){
				intList.add(new Integer(strArray[i]));
			}
			return intList;
		}
		else if (optionFlag instanceof String){
			ArrayList<String> strList = new ArrayList<String>();
			for (int i = 0; strArray != null && i < strArray.length; i++){
				strList.add(strArray[i]);
			}
			return strList;
		}
		
		return null;
	}
	
	public static ArrayList<Integer> parseIntegerList(JSONArray jsonList){
		ArrayList<Integer> list = new ArrayList<Integer>();   
		try {
			if (jsonList != null) { 
				for (int i = 0; i < jsonList.length(); i++){ 
					list.add(new Integer(jsonList.getInt(i)));
				}  
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
			
	
	
}

