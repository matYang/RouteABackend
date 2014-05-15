package carpool.HttpServer.staticDataLoaderTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.junit.Test;

import carpool.HttpServer.locationService.CatDataLoader;
import carpool.HttpServer.locationService.LocationDataLoader;
import carpool.HttpServer.locationService.PDataLoader;
import carpool.HttpServer.locationService.StaticDataLoaderDaoService;

public class DataLoaderTest {

	@Test
	public void catMapTest() {
		CatDataLoader.load();
		LinkedHashMap<String, ArrayList<String>> catDataMap = StaticDataLoaderDaoService.getCatDataMap();
		System.out.println(catDataMap);
	}
	
	@Test
	public void locationMapTest() {
		LocationDataLoader.load();
		LinkedHashMap<String, ArrayList<String>> locationDataMap = StaticDataLoaderDaoService.getLocationDataMap();
		System.out.println(locationDataMap);
	}
	
	@Test
	public void pListTest() {
		PDataLoader.load();
		List<String> pDataList = StaticDataLoaderDaoService.getPDataList();
		System.out.println(pDataList);
	}
	
	@Test
	public void catJsonTest() {
		CatDataLoader.load();
		JSONArray catDataArr = StaticDataLoaderDaoService.getCatDataJSON();
		System.out.println(catDataArr);
	}
	
	@Test
	public void locationJsonTest() {
		LocationDataLoader.load();
		JSONArray locationDataArr = StaticDataLoaderDaoService.getLocationDataJSON();
		System.out.println(locationDataArr);
	}
	
	@Test
	public void pJsonTest() {
		PDataLoader.load();
		JSONArray pDataArr = StaticDataLoaderDaoService.getPDataJSON();
		System.out.println(pDataArr);
	}

}
