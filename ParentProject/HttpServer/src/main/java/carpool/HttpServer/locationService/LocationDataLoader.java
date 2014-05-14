package carpool.HttpServer.locationService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import carpool.HttpServer.common.DebugLog;

public class LocationDataLoader {
	
	private static final String pathToFile = "LocationData";
	private static final ArrayList<String> locationData = new ArrayList<String>();
	
	public static void load(){
		
		DebugLog.d("Starting to load locationData: " + pathToFile);
		BufferedReader br = null;
		locationData.clear(); 
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(pathToFile));
 
			while ((sCurrentLine = br.readLine()) != null && sCurrentLine.length() > 0) {
				locationData.add(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		StaticDataLoaderDaoService.storeLocationData(locationData);
		DebugLog.d("locationData loaded succesfully");
	}	
}
