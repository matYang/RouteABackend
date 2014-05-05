package carpool.HttpServer.model.representation;

import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.configurations.EnumConfig.Gender;


public class UserSearchRepresentation {

	private String name;
	private Gender gender;
	private long location_Id;
	
	
	public UserSearchRepresentation(String name, Gender g,long l){
		this.name = name;
		this.gender = g;
		this.location_Id = l;
	}
	
	public	UserSearchRepresentation(String serializedUSR){
		String[] strs = serializedUSR.split(ServerConfig.urlSeperatorRegx);
		this.name = strs[0];
		this.gender = carpool.HttpServer.configurations.EnumConfig.Gender.fromInt(Integer.parseInt(strs[1]));
		this.location_Id =Long.parseLong(strs[2]);
	}
	
	public String getName(){
		return this.name;
		
	}
	
	public Gender getGender(){
		return this.gender;
	}
	public long getLocationId(){
		return this.location_Id;
	}
	
	public String toSerializedString(){
		return this.name + ServerConfig.urlSeperator + this.gender.code + ServerConfig.urlSeperator + this.location_Id;
	}

	
	
}
