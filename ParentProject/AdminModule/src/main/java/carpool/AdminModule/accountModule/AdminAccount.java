package carpool.AdminModule.accountModule;


import org.json.JSONException;
import org.json.JSONObject;

import carpool.AdminModule.configurations.EnumConfig.AdminPrivilege;
import carpool.AdminModule.configurations.EnumConfig.AdminStatus;
import carpool.HttpServer.configurations.EnumConfig.Gender;

public class AdminAccount {

	private String name;
	private String reference;
	private String password;
	private Gender gender;
	private int accountId;
	private AdminPrivilege privilege;
	private AdminStatus status;
	private String address;
	
	/*
	 * for normal construction
	 */
	public AdminAccount(String name,String password, Gender gender,AdminPrivilege privilege, AdminStatus status, String address){
		this.name = name;
		this.password = password;		
		this.gender = gender;
		this.privilege = privilege;
		this.status = status;
		this.address = address;			
	}
	
	/*
	 * for SQL getting construction
	 */
	public AdminAccount(int accountId,String name, String reference,String password, Gender gender,AdminPrivilege privilege, AdminStatus status, String address){
		this.accountId = accountId;
		this.name = name;
		this.password = password;
		this.reference = reference;
		this.gender = gender;
		this.privilege = privilege;
		this.status = status;
		this.address = address;			
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public AdminPrivilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(AdminPrivilege privilege) {
		this.privilege = privilege;
	}

	public AdminStatus getStatus() {
		return status;
	}

	public void setStatus(AdminStatus status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public JSONObject toJSON(){
		JSONObject jsonMessage = new JSONObject();

		try {
			jsonMessage.put("name", this.getName());
			jsonMessage.put("reference", this.getReference());
			jsonMessage.put("address", this.getAddress());
			//TODO

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonMessage;
	}
	
	public boolean equals(AdminAccount account){
		return this.accountId==account.getAccountId() && this.getName().equals(account.getName()) &&
				this.getGender().code == account.getGender().code && this.getAddress().equals(account.getAddress()) &&
				this.getPassword().equals(account.getPassword()) && this.getPrivilege().code==account.getPrivilege().code &&
				this.getReference().equals(account.getReference()) && this.getStatus().code==account.getStatus().code;
	}
}
