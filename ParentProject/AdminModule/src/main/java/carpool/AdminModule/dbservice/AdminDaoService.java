package carpool.AdminModule.dbservice;

import java.util.ArrayList;

import carpool.AdminModule.adminDAO.AdminAccountDAO;
import carpool.AdminModule.exception.AdminAccountNotFound;
import carpool.AdminModule.model.AdminAccount;

public class AdminDaoService {

	public static ArrayList<AdminAccount> getAllAdminAccounts(){
		return AdminAccountDAO.getAllAdminAccounts();
	}
	
	public static AdminAccount createNewAdminAccount(AdminAccount a){
		return AdminAccountDAO.addAdminAccountToDatabases(a);
	}
	
	public static AdminAccount getAdminAccountById(int id) throws AdminAccountNotFound{
		return AdminAccountDAO.getAdminAccountFromDatabases(id);
	}
	
	public static AdminAccount getAdminAccountByEmail(String email) throws AdminAccountNotFound{
		return AdminAccountDAO.getAdminAccountByEmail(email);
	}
	
	public static AdminAccount getAdminAccountByReference(String reference) throws AdminAccountNotFound{
		return AdminAccountDAO.getAdminAccountFromDatabases(reference);
	}
	
	public static void updateAdminAccount(AdminAccount a) throws AdminAccountNotFound{
		AdminAccountDAO.updateAdminAccountInDatabases(a);
	}
	
	public static void deleteAdminAccount(int id){
		AdminAccountDAO.deleteAdminAccountFromDatabases(id);
	}
}
