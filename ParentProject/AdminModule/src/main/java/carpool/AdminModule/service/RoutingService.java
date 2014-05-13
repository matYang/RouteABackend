package carpool.AdminModule.service;


import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.AdminModule.resources.AdminRoutineResource;
import carpool.AdminModule.resources.AdminStatResource;
import carpool.AdminModule.resources.AdminStateChangeResource;
import carpool.AdminModule.resources.AdminVerificationResource;
import carpool.AdminModule.resources.adminAccount.AdminAccountChangeInfoResource;
import carpool.AdminModule.resources.adminAccount.AdminAccountResource;
import carpool.AdminModule.resources.adminAccount.AdminAccountResourceId;

/**
 * This class is the collection of our routes, it is the only Application attached to the default host
 * **/
public class RoutingService extends Application {

	public RoutingService() {
		super();
	}

	public RoutingService(Context context) {
		super(context);
	}

	//@Override
	public synchronized Restlet createInboundRoot(){
		DebugLog.d("initiaing router::RoutingService");
		Router router = new Router(getContext());


		/** --------------------- APIs for Administrator ------------------ **/
		String adminServicePrefix = "/admin";
		//   API for single messages:  /api/v1.0/admin/*

		String StateChangeResourcePrefix = "/stateChange";
		//	API for admin state changes actions on user/message/transaction: /api/v1.0/admin/stateChange
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + StateChangeResourcePrefix, AdminStateChangeResource.class);
		String RoutineResourcePrefix = "/routine";
		//	API for admin to force routine tasks to take place: /api/v1.0/admin/routine
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + RoutineResourcePrefix, AdminRoutineResource.class);
		String StatAnalysisPrefix = "/stat";
		//	API for admin to analyze statistic of data service: /api/v1.0/admin/stat		
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + StatAnalysisPrefix + "/{type}", AdminStatResource.class);
		String VerificationPrefix = "/verification";
		//	API for admin to verification management: /api/v1.0/admin/verification		
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + VerificationPrefix, AdminVerificationResource.class);

		/** ---------------------- APIs for AdminAccount ---------------------**/
		String adminAccountServicePrefix = "/account";
		//  API for adminAccount: /api/v1.0/admin/account
		
		//  API for Get/Post/Delete adminAccount
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + adminAccountServicePrefix, AdminAccountResource.class);
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + adminAccountServicePrefix + "/{id}", AdminAccountResourceId.class);
		String adminAccountChangeInfoPrefix = "/change";
		//  API for Put adminAccount: /api/v1.0/admin/account/change/:id
		router.attach(ServerConfig.applicationPrefix + ServerConfig.versionPrefix + adminServicePrefix + adminAccountServicePrefix + adminAccountChangeInfoPrefix + "/{id}", AdminAccountChangeInfoResource.class);
		
		return router;
	}


}
