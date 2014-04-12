package carpool.HttpServer.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.HelperOperator;
import carpool.HttpServer.common.Validator;
import carpool.HttpServer.configurations.ImageConfig;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.configurations.EnumConfig.UserSearchState;
import carpool.HttpServer.configurations.EnumConfig.UserState;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.interfaces.PseudoModel;
import carpool.HttpServer.interfaces.PseudoValidatable;
import carpool.HttpServer.model.identityVerification.DriverVerification;
import carpool.HttpServer.model.identityVerification.PassengerVerification;
import carpool.HttpServer.model.representation.SearchRepresentation;


public class User implements PseudoModel, PseudoValidatable, Comparable<User>{

	/******
	 *  the following stores user's account informations
	 ******/
	private int userId;
	private String password;
	private String name;
	private String email;
	private String phone;
	private String qq;
	private Gender gender;
	private Calendar birthday;
	private String imgPath;
	private long location_Id;
	private Location location;
	private long match_Id;
	private Calendar lastLogin;
	private Calendar creationTime;

	/*****
	 * the followings are for user's relations
	 ******/
	private ArrayList<Message> historyList;
	private ArrayList<Message> watchList;
	private ArrayList<User> socialList;
	private ArrayList<Transaction> transactionList;
	private ArrayList<Notification> notificationList;


	/*****
	 * the followings are user's state information
	 *****/
	private boolean emailActivated;
	private boolean phoneActivated;
	private boolean emailNotice;
	private boolean phoneNotice;
	private UserState state;
	private SearchRepresentation searchRepresentation;


	/*****
	 * the following stores user's statistics
	 *****/
	private int level;
	private int averageScore;
	private int totalTranscations;


	private int passengerVerificationId;
	private int driverVerificationId;
	private PassengerVerification passengerVerification;
	private DriverVerification driverVerification;


	/*****
	 * the follows are user's account information
	 *****/
	private String accountId;
	private String accountPass;
	private String accountToken;
	private BigDecimal accountValue;


	/*****
	 * protected constructor to disallow raw initialization and serialization, but allow easier testing
	 *****/
	protected User(){}


	/*****
	 * Constructor for user registration
	 *****/
	public User(String password, String email, Location location, Gender g) {
		super();
		this.userId = -1;
		this.password = password;
		this.email = email;
		this.location = location;
		this.location_Id = location.getId();
		this.match_Id = location.getMatch();
		//dummy fills
		this.name = "无名氏";
		this.phone = "";
		this.qq = "";
		this.gender = g;
		this.birthday = DateUtility.getCurTimeInstance();
		this.imgPath = g == Gender.female ? ImageConfig.img_default_avatar_female : ImageConfig.img_default_avatar_male;
		this.lastLogin = DateUtility.getCurTimeInstance();
		this.creationTime = DateUtility.getCurTimeInstance();

		this.historyList = new ArrayList<Message>();
		this.watchList = new ArrayList<Message>();
		this.socialList = new ArrayList<User>();
		this.transactionList = new ArrayList<Transaction>();
		this.notificationList = new ArrayList<Notification>();

		this.emailActivated = false;
		this.phoneActivated = false;
		this.emailNotice = false;
		this.phoneNotice = false;
		this.state = EnumConfig.UserState.normal;
		this.searchRepresentation = ServerConfig.getDefaultSearchRepresentation();

		this.level = 0;
		this.averageScore = 0;
		this.totalTranscations = 0;

		this.passengerVerificationId = -1;
		this.driverVerificationId = -1;
		this.passengerVerification = null;
		this.driverVerification = null;

		this.accountId = "";
		this.accountPass = "";
		this.accountToken = "";
		this.accountValue = BigDecimal.valueOf(0l);

	}


	/*****
	 * full constructor used for SQL retrieval
	 *****/
	public User(int userId, String password, String name, String email,
			String phone, String qq, EnumConfig.Gender gender, Calendar birthday,
			String imgPath, Location location, Calendar lastLogin,
			Calendar creationTime, boolean emailActivated,
			boolean phoneActivated, boolean emailNotice, boolean phoneNotice,
			EnumConfig.UserState state, SearchRepresentation searchRepresentation,
			int level, int averageScore, int totalTranscations,
			int passengerVerificationId, int driverVerificationId,
			PassengerVerification passengerVerification,
			DriverVerification driverVerification, String accountId,
			String accountPass, String accountToken, BigDecimal accountValue, long match_Id) {
		super();
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.qq = qq;
		this.gender = gender;
		this.birthday = birthday;
		this.imgPath = imgPath;
		this.location_Id = location.getId();
		this.location = location;
		this.match_Id = match_Id;
		this.lastLogin = lastLogin;
		this.creationTime = creationTime;
		this.emailActivated = emailActivated;
		this.phoneActivated = phoneActivated;
		this.emailNotice = emailNotice;
		this.phoneNotice = phoneNotice;
		this.state = state;
		this.searchRepresentation = searchRepresentation;
		this.level = level;
		this.averageScore = averageScore;
		this.totalTranscations = totalTranscations;
		this.passengerVerificationId = passengerVerificationId;
		this.driverVerificationId = driverVerificationId;
		this.passengerVerification = passengerVerification;
		this.driverVerification = driverVerification;
		this.accountId = accountId;
		this.accountPass = accountPass;
		this.accountToken = accountToken;
		this.accountValue = accountValue;

		this.historyList = new ArrayList<Message>();
		this.watchList = new ArrayList<Message>();
		this.socialList = new ArrayList<User>();
		this.transactionList = new ArrayList<Transaction>();
		this.notificationList = new ArrayList<Notification>();

	}



	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getQq() {
		return qq;
	}


	public void setQq(String qq) {
		this.qq = qq;
	}


	public int getAge() {
		Calendar c_age = DateUtility.getCurTimeInstance();
		return c_age.get(Calendar.YEAR) - this.birthday.get(Calendar.YEAR);
	}


	public Gender getGender() {
		return gender;
	}


	public void setGender(Gender gender) {
		this.gender = gender;
	}


	public Calendar getBirthday() {
		return birthday;
	}


	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}


	public String getImgPath() {
		return imgPath;
	}


	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
		this.location_Id = location.getId();
	}

	public Calendar getLastLogin() {
		return lastLogin;
	}


	public void setLastLogin(Calendar lastLogin) {
		this.lastLogin = lastLogin;
	}


	public Calendar getCreationTime() {
		return creationTime;
	}



	public ArrayList<Message> getHistoryList() {
		return historyList;
	}


	public void setHistoryList(ArrayList<Message> historyList) {
		this.historyList = historyList;
	}


	public ArrayList<Message> getWatchList() {
		return watchList;
	}


	public void setWatchList(ArrayList<Message> watchList) {
		this.watchList = watchList;
	}


	public ArrayList<User> getSocialList() {
		return socialList;
	}


	public void setSocialList(ArrayList<User> socialList) {
		this.socialList = socialList;
	}


	public ArrayList<Transaction> getTransactionList() {
		return transactionList;
	}


	public void setTransactionList(ArrayList<Transaction> transactionList) {
		this.transactionList = transactionList;
	}


	public ArrayList<Notification> getNotificationList() {
		return notificationList;
	}


	public void setNotificationList(ArrayList<Notification> notificationList) {
		this.notificationList = notificationList;
	}


	public boolean isEmailActivated() {
		return emailActivated;
	}


	public void setEmailActivated(boolean emailActivated) {
		this.emailActivated = emailActivated;
	}


	public boolean isPhoneActivated() {
		return phoneActivated;
	}


	public void setPhoneActivated(boolean phoneActivated) {
		this.phoneActivated = phoneActivated;
	}


	public boolean isEmailNotice() {
		return emailNotice;
	}


	public void setEmailNotice(boolean emailNotice) {
		this.emailNotice = emailNotice;
	}


	public boolean isPhoneNotice() {
		return phoneNotice;
	}


	public void setPhoneNotice(boolean phoneNotice) {
		this.phoneNotice = phoneNotice;
	}


	public UserState getState() {
		return state;
	}


	public void setState(UserState state) {
		this.state = state;
	}


	public SearchRepresentation getSearchRepresentation() {
		return searchRepresentation;
	}


	public void setSearchRepresentation(SearchRepresentation searchRepresentation) {
		this.searchRepresentation = searchRepresentation;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getAverageScore() {
		return averageScore;
	}


	public void setAverageScore(int averageScore) {
		this.averageScore = averageScore;
	}


	public int getTotalTranscations() {
		return totalTranscations;
	}


	public void setTotalTranscations(int totalTranscations) {
		this.totalTranscations = totalTranscations;
	}



	public int getPassengerVerificationId() {
		return passengerVerificationId;
	}


	public void setPassengerVerificationId(int passengerVerificationId) {
		this.passengerVerificationId = passengerVerificationId;
	}


	public int getDriverVerificationId() {
		return driverVerificationId;
	}


	public void setDriverVerificationId(int driverVerificationId) {
		this.driverVerificationId = driverVerificationId;
	}


	public PassengerVerification getPassengerVerification() {
		return passengerVerification;
	}


	public void setPassengerVerification(PassengerVerification passengerVerification) {
		this.passengerVerification = passengerVerification;
	}


	public DriverVerification getDriverVerification() {
		return driverVerification;
	}


	public void setDriverVerification(DriverVerification driverVerification) {
		this.driverVerification = driverVerification;
	}


	public String getAccountId() {
		return accountId;
	}


	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}


	public String getAccountPass() {
		return accountPass;
	}


	public void setAccountPass(String accountPass) {
		this.accountPass = accountPass;
	}


	public String getAccountToken() {
		return accountToken;
	}


	public void setAccountToken(String accountToken) {
		this.accountToken = accountToken;
	}

	public long getLocation_Id() {
		return location_Id;
	}

	public void setLocation_Id(long location_Id) {
		this.location_Id = location_Id;
	}

	public long getMatch_Id() {
		return match_Id;
	}

	public void setMatch_Id(long match_Id) {
		this.match_Id = match_Id;
	}

	public BigDecimal getAccountValue() {
		return accountValue;
	}


	public void setAccountValue(BigDecimal accountValue) {
		this.accountValue = accountValue;
	}


	public boolean isPasswordCorrect(String password) {
		return this.password.equals(password);
	}


	public void setPassword(String oldPassword, String newPassword) throws ValidationException{
		if (isPasswordCorrect(oldPassword)){
			this.password = newPassword;
		}
		else{
			throw new ValidationException("Password Incorrect");
		}
	}


	/** 
	 * check if user can be logged in
	 * Precondition: email has been activated, and state is in normal
	 */
	public boolean isAbleToLogin(){
		return this.isEmailActivated() && this.state == EnumConfig.UserState.normal;
	}


	@Override
	public JSONObject toJSON(){
		JSONObject jsonUser = new JSONObject();
		try {

			jsonUser.put("userId", this.getUserId());
			jsonUser.put("name", this.getName());
			jsonUser.put("email", this.getEmail());
			jsonUser.put("phone", this.getPhone());
			jsonUser.put("qq", this.getQq());
			jsonUser.put("age", this.getAge());
			jsonUser.put("gender", this.getGender().code);
			jsonUser.put("birthday", DateUtility.castToAPIFormat(this.getBirthday()));
			jsonUser.put("imgPath", this.getImgPath());
			jsonUser.put("location", this.location.toJSON());
			jsonUser.put("lastLogin", DateUtility.castToAPIFormat(this.getLastLogin()));
			jsonUser.put("creationTime", DateUtility.castToAPIFormat(this.getCreationTime()));

			jsonUser.put("historyList", JSONFactory.toJSON(this.getHistoryList()));
			jsonUser.put("watchList", JSONFactory.toJSON(this.getWatchList()));
			jsonUser.put("socialList", JSONFactory.toJSON(this.getSocialList()));
			jsonUser.put("transactionList", JSONFactory.toJSON(this.getTransactionList()));
			jsonUser.put("notificationList", JSONFactory.toJSON(this.getNotificationList()));

			jsonUser.put("emailActivated", this.isEmailActivated());
			jsonUser.put("phoneActivated", this.isPhoneActivated());
			jsonUser.put("emailNotice", this.isEmailNotice());
			jsonUser.put("phoneNotice", this.isPhoneNotice());

			jsonUser.put("state", this.getState().code);
			jsonUser.put("searchRepresentation", this.getSearchRepresentation().toJSON());
			jsonUser.put("level", this.getLevel());
			jsonUser.put("averageScore", this.getAverageScore());
			jsonUser.put("totalTranscations", this.getTotalTranscations());

			jsonUser.put("passengerVerificationId", this.getPassengerVerificationId());
			jsonUser.put("driverVerificationId", this.getDriverVerificationId());
			jsonUser.put("passengerVerification", this.getPassengerVerification() == null ? new JSONObject() : this.getPassengerVerification().toJSON());
			jsonUser.put("driverVerification", this.getDriverVerification() == null ? new JSONObject() : this.getDriverVerification().toJSON());

			jsonUser.put("accountId", this.getAccountId());
			jsonUser.put("accountPass", this.getAccountPass());
			jsonUser.put("accountToken", this.getAccountToken());
			jsonUser.put("accountValue", this.getAccountValue());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonUser;
	}




	@Override
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", name="
				+ name + ", email=" + email + ", phone=" + phone + ", qq=" + qq
				+ ", gender=" + gender + ", birthday=" + birthday
				+ ", imgPath=" + imgPath + ", location_Id=" + location_Id
				+ ", location=" + location + ", match_Id=" + match_Id
				+ ", lastLogin=" + lastLogin + ", creationTime=" + creationTime
				+ ", historyList=" + historyList + ", watchList=" + watchList
				+ ", socialList=" + socialList + ", transactionList="
				+ transactionList + ", notificationList=" + notificationList
				+ ", emailActivated=" + emailActivated + ", phoneActivated="
				+ phoneActivated + ", emailNotice=" + emailNotice
				+ ", phoneNotice=" + phoneNotice + ", state=" + state
				+ ", searchRepresentation=" + searchRepresentation + ", level="
				+ level + ", averageScore=" + averageScore
				+ ", totalTranscations=" + totalTranscations
				+ ", passengerVerificationId=" + passengerVerificationId
				+ ", driverVerificationId=" + driverVerificationId
				+ ", passengerVerification=" + passengerVerification
				+ ", driverVerification=" + driverVerification + ", accountId="
				+ accountId + ", accountPass=" + accountPass
				+ ", accountToken=" + accountToken + ", accountValue="
				+ accountValue + "]";
	}


	//override Comparator, by default users will be sorted in alphabetical order of their names
	@Override
	public int compareTo(User anotherUser) {
		return this.getName().compareTo(anotherUser.getName());
	}

	public boolean equals(User newUser) throws ValidationException{  

		boolean d_verificationEqualFlag = false;
		boolean p_verificationEqualFlag = false;

		if(this.passengerVerification==null) p_verificationEqualFlag = newUser.passengerVerification==null;    	
		if(this.driverVerification==null) d_verificationEqualFlag = newUser.driverVerification==null;
		if(this.passengerVerification!=null && newUser.passengerVerification!=null)
			p_verificationEqualFlag = this.passengerVerification.equals(newUser.passengerVerification);
		if(this.driverVerification!=null && newUser.driverVerification!=null)
			d_verificationEqualFlag = this.driverVerification.equals(newUser.driverVerification);

		return newUser !=null && this.userId == newUser.getUserId() 
				&& this.name.equals(newUser.getName())
				&& this.gender.equals(newUser.getGender())
				&& this.location.equals(newUser.getLocation())
				&& this.emailActivated==newUser.emailActivated
				&& this.emailNotice==newUser.emailNotice
				&& this.phoneActivated==newUser.phoneActivated
				&& this.phoneNotice== newUser.phoneNotice
				&& this.password.equals(newUser.getPassword())
				&& this.isAbleToLogin()==newUser.isAbleToLogin()
				&& this.validate()==newUser.validate()
				&& this.accountToken.equals(newUser.getAccountToken())
				&& this.accountValue.toString().equals(newUser.getAccountValue().toString())
				&& this.getAge()==newUser.getAge()
				&& this.averageScore==newUser.getAverageScore()
				&& this.birthday.getTime().toString().equals(newUser.getBirthday().getTime().toString())
				&& this.creationTime.getTime().toString().equals(newUser.getCreationTime().getTime().toString())				
				&& p_verificationEqualFlag
				&& d_verificationEqualFlag
				&& this.gender.code==newUser.getGender().code
				&& HelperOperator.isArrayListEqual(this.historyList, newUser.getHistoryList())
				&& this.imgPath.equals(newUser.getImgPath())
				&& this.lastLogin.getTime().toString().equals(newUser.getLastLogin().getTime().toString())
				&& this.level==newUser.getLevel()
				&& this.phone.equals(newUser.getPhone())
				&& this.qq.equals(newUser.getQq())
				&& this.state.code==newUser.getState().code
				&& HelperOperator.isArrayListEqual(this.watchList, newUser.getWatchList())
				&& HelperOperator.isArrayListEqual(this.socialList, newUser.getSocialList());


	}
	@Override
	public boolean validate() throws ValidationException{
		//TODO remove true
		return true || Validator.isEmailFormatValid(this.email) && Validator.isNameFormatValid(this.name) && Validator.isPasswordFormatValid(this.password) && Validator.isPhoneFormatValid(this.phone) && Validator.isQqFormatValid(this.qq);
	}

	public boolean validate_create() throws ValidationException{
		if (!Validator.isEmailFormatValid(this.email)){
			throw new ValidationException("邮箱格式不正确");
		}
		else if (!Validator.isPasswordFormatValid(this.password)){
			throw new ValidationException("密码格式不正确");
		}
		return true;
	}

	public String getPassword() {
		return  this.password;
	}

}

