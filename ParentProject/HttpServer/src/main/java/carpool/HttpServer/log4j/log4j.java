package carpool.HttpServer.log4j;

import org.apache.log4j.PropertyConfigurator;


public class log4j {
		
	public static void configure(){				
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
	
}
