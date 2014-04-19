package carpool.HttpServer.location;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.HttpServer.dbservice.LocationDaoService;
import carpool.HttpServer.exception.location.LocationException;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;

public class LocationTest {
	
	@Test
	public void testLoadDefaults() throws LocationNotFoundException{
		try {
			LocationDaoService.init();
		} catch (LocationException e) {
			e.printStackTrace();
			fail();
		} catch (ValidationException e) {
			fail();
			e.printStackTrace();
		}
	}

}
