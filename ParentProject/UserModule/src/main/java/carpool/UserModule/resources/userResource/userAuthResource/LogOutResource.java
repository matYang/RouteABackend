package carpool.UserModule.resources.userResource.userAuthResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.HttpServer.common.Validator;
import carpool.HttpServer.configurations.EnumConfig;
import carpool.HttpServer.dbservice.*;
import carpool.HttpServer.exception.PseudoException;
import carpool.HttpServer.exception.auth.DuplicateSessionCookieException;
import carpool.HttpServer.exception.auth.SessionEncodingException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.factory.JSONFactory;
import carpool.HttpServer.model.*;
import carpool.UserModule.resources.PseudoResource;
import carpool.UserModule.resources.dianmingResource.DMResource;




public class LogOutResource extends PseudoResource{


	@Put
	public Representation logoutAuthentication(Representation entity){
		int id = -1;

		try {
			this.checkEntity(entity);
			id = Integer.parseInt(this.getReqAttr("id"));
			
			this.closeAuthenticationSession(id);
			setStatus(Status.SUCCESS_OK);
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
			return this.doException(e);
		}

		Representation result = new JsonRepresentation(new JSONObject());

        this.addCORSHeader();
        return result;
	}

	
}

