package carpool.HttpServer.clearDatabase;

import org.junit.Test;

import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;

public class ClearDatabase {

	@Test
	public void test() {
		CarpoolDaoBasic.clearBothDatabase();
	}

}
