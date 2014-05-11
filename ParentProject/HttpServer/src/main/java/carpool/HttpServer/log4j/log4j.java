package carpool.HttpServer.log4j;

import org.apache.log4j.PropertyConfigurator;

import carpool.HttpServer.configurations.ServerConfig;


public class log4j {
		
	public static void configure(){				
		PropertyConfigurator.configure(ServerConfig.resourcePrefix + "log4j.properties");
	}
	
}
