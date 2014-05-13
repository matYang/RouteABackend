package carpool.AdminModule.model;


import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.AdminModule.configurations.EnumConfig.AdminPrivilege;
import carpool.AdminModule.configurations.EnumConfig.AdminStatus;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig.Gender;

public class AdminAccount {


	private String reference;
	private String password;
	private int accountId;

	private AdminPrivilege privilege;
	private AdminStatus status;

	private String name;
	private Gender gender;
	private String address;

	private String phone;
	private String email;
	private Calendar birthday;
	private String idNum;
	private String imgPath;
	private Calendar creationTime;


	/*
	 * for normal construction
	 */
	public AdminAccount(String name,String password, Gender gender,AdminPrivilege privilege, AdminStatus status, 
			String address,String phone,String email,Calendar birthday,String idNum,String imgPath){
		this.name = name;
		this.password = password;		
		this.gender = gender;
		this.privilege = privilege;
		this.status = status;
		this.address = address;			
		this.phone = phone;
		this.email = email;
		this.birthday = birthday;
		this.idNum = idNum;
		this.imgPath = imgPath;
		this.creationTime = DateUtility.getCurTimeInstance();
	}

	/*
	 * for SQL getting construction
	 */
	public AdminAccount(int accountId,String name, String reference,String password, Gender gender,AdminPrivilege privilege,
			AdminStatus status, String address,String phone,String email,Calendar birthday,String idNum,String imgPath,Calendar creationTime){
		this.accountId = accountId;
		this.name = name;
		this.password = password;
		this.reference = reference;
		this.gender = gender;
		this.privilege = privilege;
		this.status = status;
		this.address = address;
		this.phone = phone;
		this.email = email;
		this.birthday = birthday;
		this.idNum = idNum;
		this.imgPath = imgPath;
		this.creationTime = creationTime;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Calendar getBirthday() {
		return birthday;
	}

	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Calendar getCreationTime() {
		return this.creationTime;
	}

	public JSONObject toJSON(){
		JSONObject jsonMessage = new JSONObject();

		try {
			jsonMessage.put("name", this.getName());
			jsonMessage.put("accountId", this.getAccountId());
			jsonMessage.put("reference", this.getReference());
			jsonMessage.put("idNum", this.getIdNum());
			jsonMessage.put("address", this.getAddress());
			jsonMessage.put("email", this.getEmail());
			jsonMessage.put("phone", this.getPhone());
			jsonMessage.put("gender", this.getGender().code);
			jsonMessage.put("privilege", this.getPrivilege().code);
			jsonMessage.put("status", this.getStatus().code);
			jsonMessage.put("imgPath", this.getImgPath());
			jsonMessage.put("birthday", DateUtility.castToAPIFormat(this.getBirthday()));
			jsonMessage.put("creationTime", this.getCreationTime());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonMessage;
	}

	public boolean equals(AdminAccount account){
		return this.accountId==account.getAccountId() && this.getName().equals(account.getName()) &&
				this.getGender().code == account.getGender().code && this.getAddress().equals(account.getAddress()) &&
				this.getPassword().equals(account.getPassword()) && this.getPrivilege().code==account.getPrivilege().code &&
				this.getReference().equals(account.getReference()) && this.getStatus().code==account.getStatus().code &&
				this.getPhone().equals(account.getPhone())&&this.getEmail().equals(account.getEmail())&&this.getIdNum().equals(account.getIdNum())&&
				this.getBirthday().getTime().toString().equals(account.getBirthday().getTime().toString())&&this.getImgPath().equals(account.getImgPath())&&
				this.getCreationTime().getTime().toString().equals(account.getCreationTime().getTime().toString());
	}
}
