package carpool.AdminModule.resources.adminAccount;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Put;

import carpool.AdminModule.adminDAO.AdminAccountDAO;
import carpool.AdminModule.configurations.EnumConfig.AdminPrivilege;
import carpool.AdminModule.configurations.EnumConfig.AdminStatus;
import carpool.AdminModule.dbservice.AdminDaoService;
import carpool.AdminModule.model.AdminAccount;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.common.Validator;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.configurations.EnumConfig.Gender;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.exception.validation.ValidationException;
import carpool.AdminModule.factory.JSONFactory;
import carpool.HttpServer.resources.PseudoResource;

public class AdminAccountChangeInfoResource extends PseudoResource{

	@Put
	public Representation changeInfo(Representation entity){
		int id = -1;
		JSONObject response = new JSONObject();
		JSONObject parsed = new JSONObject();

		try{
			this.checkEntity(entity);

			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);

			parsed = parseJSON(entity);
			if(parsed != null){
				AdminAccount aaccount = AdminDaoService.getAdminAccountById(id);
				aaccount.setAddress(parsed.getString("address"));
				aaccount.setBirthday(DateUtility.castFromAPIFormat(parsed.getString("birthday")));
				aaccount.setEmail(parsed.getString("email"));
				aaccount.setGender(Gender.fromInt(parsed.getInt("gender")));
				aaccount.setIdNum(parsed.getString("idNum"));
				aaccount.setImgPath(parsed.getString("imgPath"));
				aaccount.setName(parsed.getString("name"));
				aaccount.setPassword(parsed.getString("password"));
				aaccount.setPhone(parsed.getString("phone"));
				aaccount.setPrivilege(AdminPrivilege.fromInt(parsed.getInt("privilege")));
				aaccount.setReference(parsed.getString("reference"));
				aaccount.setStatus(AdminStatus.fromInt(parsed.getInt("status")));
				AdminDaoService.updateAdminAccount(aaccount);

				response = JSONFactory.toJSON(aaccount);
				setStatus(Status.SUCCESS_OK);
			}else{
				throw new ValidationException("数据格式不正确");
			}
		}catch (UserNotFoundException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch (Exception e) {
			return this.doException(e);
		}

		Representation result = new JsonRepresentation(response);

		this.addCORSHeader(); 
		return result;
	}

	protected JSONObject parseJSON(Representation entity) throws ValidationException{
		JSONObject obj = null;
		try {
			obj = (new JsonRepresentation(entity)).getJsonObject();
		} catch (JSONException | IOException e) {
			DebugLog.d(e);
			return null;
		}

		String name = "";
		try {
			name = java.net.URLDecoder.decode(obj.getString("name"), "utf-8");
		} catch (UnsupportedEncodingException | JSONException e) {
			DebugLog.d(e);
			throw new ValidationException("姓名格式不正确");
		}
		int gender = obj.getInt("gender");
		String birthday = obj.getString("birthday");

		if (!(name != null && EnumConfig.Gender.values()[gender] != null && birthday != null)){
			throw new ValidationException("必填数据不能为空");
		}

		if (!(Validator.isNameFormatValid(name))){
			throw new ValidationException("姓名格式不正确");
		}

		String phone = obj.getString("phone");
		if (phone != null && phone.length() > 0){
			if (!Validator.isPhoneFormatValid(phone)){
				throw new ValidationException("电话格式不正确");
			}
		}	

		return obj;
	}
}
