package carpool.AdminModule.resources.adminAccount;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;

import carpool.AdminModule.dbservice.AdminDaoService;
import carpool.AdminModule.model.AdminAccount;
import carpool.HttpServer.exception.PseudoException;
import carpool.AdminModule.factory.JSONFactory;
import carpool.HttpServer.resources.PseudoResource;

public class AdminAccountResourceId extends PseudoResource{

	@Get
	public Representation getAdminAccountById(){
		int id = -1;
		int intendedId = -1;
		JSONObject jsonObject = new JSONObject();
		try{
			id = Integer.parseInt(this.getReqAttr("id"));
			String intendedIdString = this.getQueryVal("intendedAdminId");
			intendedId = intendedIdString != null ? Integer.parseInt(this.getQueryVal("intendedAdminId")) : id;

			String ss = this.getSessionString();
			this.validateAuthentication(id);

			AdminAccount aaccount = AdminDaoService.getAdminAccountById(id);
			if (aaccount != null){
				jsonObject = JSONFactory.toJSON(aaccount);
			}
			else{
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}
		}catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		}catch (Exception e) {
			return this.doException(e);
		}

		Representation result = new JsonRepresentation(jsonObject);
		this.addCORSHeader();
		return result;
	}

	@Delete
	public Representation deleteAdminAccount(){
		int id = -1;
		try{
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);

			AdminDaoService.deleteAdminAccount(id);
			setStatus(Status.SUCCESS_OK);
		}catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch(Exception e){
			return this.doException(e);
		}

		this.addCORSHeader();
		return new JsonRepresentation(new JSONObject());
	}
}
