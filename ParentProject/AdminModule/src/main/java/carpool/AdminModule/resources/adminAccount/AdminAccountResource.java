package carpool.AdminModule.resources.adminAccount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import carpool.AdminModule.configurations.EnumConfig.AdminPrivilege;
import carpool.AdminModule.configurations.EnumConfig.AdminStatus;
import carpool.AdminModule.dbservice.AdminDaoService;
import carpool.AdminModule.model.AdminAccount;
import carpool.AdminModule.factory.JSONFactory;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.dbservice.EmailDaoService;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.HttpServer.resources.PseudoResource;

public class AdminAccountResource extends PseudoResource{

	@Get	
	public Representation getAllAdminAccount(){
		ArrayList<AdminAccount> alist = AdminDaoService.getAllAdminAccounts();
		JSONArray jsonArray = JSONFactory.toJSON(alist);

		Representation result = new JsonRepresentation(jsonArray);

		this.addCORSHeader();
		return result;
	}

	@Post
	public Representation createAdminAccount(Representation entity){

		JSONObject newJsonAccount = new JSONObject();
		AdminAccount creationFeedBack = null;

		try{
			this.checkEntity(entity);
			AdminAccount aaccount = parseJSON(entity);

			creationFeedBack = AdminDaoService.createNewAdminAccount(aaccount);

			DebugLog.d("@Post::resources::createAdminAccount: available: " + creationFeedBack.getEmail() + " id: " +  creationFeedBack.getAccountId());

			EmailDaoService.sendActivationEmail(creationFeedBack.getAccountId(), creationFeedBack.getEmail());
			newJsonAccount = JSONFactory.toJSON(creationFeedBack);
		}catch(PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		}

		Representation result = new JsonRepresentation(newJsonAccount);
		this.addCORSHeader(); 
		return result;
	}

	protected AdminAccount parseJSON(Representation entity) throws ValidationException{
		JSONObject jsonAccount = null;
		AdminAccount aaccount = null;

		try{
			jsonAccount = (new JsonRepresentation(entity)).getJsonObject();

			String name = jsonAccount.getString("name");
			int id = jsonAccount.getInt("accountId");
			String reference = jsonAccount.getString("reference");
			String password = jsonAccount.getString("password");
			String address = jsonAccount.getString("address");
			String phone = jsonAccount.getString("phone");
			String email = jsonAccount.getString("email");
			String idNum = jsonAccount.getString("idNum");
			String imgPath = jsonAccount.getString("imgPath");
			AdminPrivilege privilege = AdminPrivilege.fromInt(jsonAccount.getInt("privilege"));
			AdminStatus status = AdminStatus.fromInt(jsonAccount.getInt("status"));
			Gender gender = Gender.fromInt(jsonAccount.getInt("gender"));
			Calendar birthday = DateUtility.castFromAPIFormat(jsonAccount.getString("birthday"));	
			Calendar creationTime = DateUtility.castFromAPIFormat(jsonAccount.getString("creationTime"));

			aaccount = new AdminAccount(id,name,reference,password,gender,privilege,status,address,phone,email,birthday,idNum,imgPath,creationTime);
		}catch (JSONException|IOException e) {
			throw new ValidationException("无效数据格式");
		}
		return aaccount;
	}
}
