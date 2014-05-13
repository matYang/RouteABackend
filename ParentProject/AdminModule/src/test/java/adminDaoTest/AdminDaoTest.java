package adminDaoTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.AdminModule.adminDAO.AdminAccountDAO;
import carpool.AdminModule.configurations.EnumConfig.AdminPrivilege;
import carpool.AdminModule.configurations.EnumConfig.AdminStatus;
import carpool.AdminModule.exception.AdminAccountNotFound;
import carpool.AdminModule.model.AdminAccount;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EnumConfig.Gender;

public class AdminDaoTest {

	@Test
	public void testAdd(){
		CarpoolDaoBasic.clearBothDatabase();
		String name = "Harry";
		String address = "pogai";
		String password = "xnm";
		Gender gender = Gender.male;
		AdminPrivilege privilege = AdminPrivilege.business;
		AdminStatus status = AdminStatus.activated;
		String phone = "123456";
		String email = "sadf@route.ca";
		Calendar birthday = DateUtility.getCurTimeInstance();
		String idNum = "23423fsf_dsfsdf/.,lsdf2";
		String imgPath = "dsfdsfdsew324sdfdsf24t34tretqwerwftgergt3r4423rfdsf";
		AdminAccount aa = new AdminAccount(name,password,gender,privilege,status,address,phone,email,birthday,idNum,imgPath);
		try{
			AdminAccountDAO.addAdminAccountToDatabases(aa);
		}catch(Exception ex){
			ex.printStackTrace();
			DebugLog.d(ex);
			fail();
		}
	}

	@Test
	public void testGet() throws AdminAccountNotFound{
		CarpoolDaoBasic.clearBothDatabase();
		String name = "Harry";
		String address = "pogai";
		String password = "xnm";
		Gender gender = Gender.male;
		AdminPrivilege privilege = AdminPrivilege.business;
		AdminStatus status = AdminStatus.activated;
		String phone = "123456";
		String email = "sadf@route.ca";
		Calendar birthday = DateUtility.getCurTimeInstance();
		String idNum = "23423fsf_dsfsdf/.,lsdf2";
		String imgPath = "dsfdsfdsew324sdfdsf24t34tretqwerwftgergt3r4423rfdsf";
		AdminAccount aa = new AdminAccount(name,password,gender,privilege,status,address,phone,email,birthday,idNum,imgPath);
		AdminAccount ac = null;
		try{
			ac = AdminAccountDAO.addAdminAccountToDatabases(aa);
		}catch(Exception ex){
			ex.printStackTrace();
			DebugLog.d(ex);
			fail();
		}

		if(ac.getAddress().equals(aa.getAddress())&&ac.getGender().code==aa.getGender().code&&
				ac.getName().equals(aa.getName())&&ac.getPassword().equals(aa.getPassword())&&
				ac.getStatus().code==aa.getStatus().code){
			//Passed;
		}else{
			fail();
		}

		AdminAccount ac2 = AdminAccountDAO.getAdminAccountFromDatabases(ac.getAccountId());
		if(ac2.equals(ac)){
			//Passed;
		}else{
			fail();
		}

		AdminAccount ac3 = AdminAccountDAO.getAdminAccountFromDatabases(ac.getReference());
		if(ac3.equals(ac)){
			//Passed;
		}else{
			fail();
		}

		AdminAccount ac4 = AdminAccountDAO.getAdminAccountByEmail(email);
		if(ac4.equals(ac)){
			//Passed;
		}else{
			fail();
		}

		String name2 = "Matt";
		String address2 = "jjburst";
		String password2 = "sdfr";
		Gender gender2 = Gender.both;
		AdminPrivilege privilege2 = AdminPrivilege.economy;
		AdminStatus status2 = AdminStatus.activated;
		String phone2 = "1345677";
		String email2 = "admin@route.ca";
		Calendar birthday2 = DateUtility.getCurTimeInstance();
		String idNum2 = "23dsfgg4_dsfsdf/.,lsdf2";
		String imgPath2 = "dsfdsfdse48434324553453453452dfgefg4ergt34tretqwerwftgergt3r4423rfdsf";
		AdminAccount aa2 = new AdminAccount(name2,password2,gender2,privilege2,status2,address2,phone2,email2,birthday2,idNum2,imgPath2);
		try{
			ac2 = AdminAccountDAO.addAdminAccountToDatabases(aa2);
		}catch(Exception ex){
			ex.printStackTrace();
			DebugLog.d(ex);
			fail();
		}

		ArrayList<AdminAccount> alist = new ArrayList<AdminAccount>();
		alist = AdminAccountDAO.getAllAdminAccounts();
		if(alist.size() == 2 && alist.get(0).equals(ac) && alist.get(1).equals(ac2)){
			//Passed;
		}else{
			fail();
		}
	}

	@Test
	public void testUpdate() throws AdminAccountNotFound{
		CarpoolDaoBasic.clearBothDatabase();
		String name = "Harry";
		String address = "pogai";
		String password = "xnm";
		Gender gender = Gender.male;
		AdminPrivilege privilege = AdminPrivilege.business;
		AdminStatus status = AdminStatus.activated;
		String phone = "123456";
		String email = "sadf@route.ca";
		Calendar birthday = DateUtility.getCurTimeInstance();
		String idNum = "23423fsf_dsfsdf/.,lsdf2";
		String imgPath = "dsfdsfdsew324sdfdsf24t34tretqwerwftgergt3r4423rfdsf";
		AdminAccount aa = new AdminAccount(name,password,gender,privilege,status,address,phone,email,birthday,idNum,imgPath);
		AdminAccount ac = null;
		AdminAccount ac2 = null;
		try{
			ac = AdminAccountDAO.addAdminAccountToDatabases(aa);			
		}catch(Exception ex){
			ex.printStackTrace();
			DebugLog.d(ex);
			fail();
		}

		ac.setAddress("newAddress");
		ac.setGender(Gender.female);
		String n = "sdfd3904-3424r3+34,./d";
		ac.setName(n);
		ac.setStatus(AdminStatus.deactivated);
		AdminAccountDAO.updateAdminAccountInDatabases(ac);

		ac2 = AdminAccountDAO.getAdminAccountFromDatabases(ac.getAccountId());
		if(ac.equals(ac2)&&ac2.getName().equals(n)){
			//Passed;
		}else{
			fail();
		}
	}

	@Test
	public void testDelete() throws AdminAccountNotFound{
		CarpoolDaoBasic.clearBothDatabase();
		String name = "Harry";
		String address = "pogai";
		String password = "xnm";
		Gender gender = Gender.male;
		AdminPrivilege privilege = AdminPrivilege.business;
		AdminStatus status = AdminStatus.activated;
		String phone = "123456";
		String email = "sadf@route.ca";
		Calendar birthday = DateUtility.getCurTimeInstance();
		String idNum = "23423fsf_dsfsdf/.,lsdf2";
		String imgPath = "dsfdsfdsew324sdfdsf24t34tretqwerwftgergt3r4423rfdsf";
		AdminAccount aa = new AdminAccount(name,password,gender,privilege,status,address,phone,email,birthday,idNum,imgPath);
		AdminAccount ac = null;
		AdminAccount ac2 = null;
		try{
			ac = AdminAccountDAO.addAdminAccountToDatabases(aa);			
		}catch(Exception ex){
			ex.printStackTrace();
			DebugLog.d(ex);
			fail();
		}

		ac.setAddress("newAddress");
		ac.setGender(Gender.female);
		String n = "sdfd3904-3424r3+34,./d";
		ac.setName(n);
		ac.setStatus(AdminStatus.deactivated);
		AdminAccountDAO.updateAdminAccountInDatabases(ac);

		ac2 = AdminAccountDAO.getAdminAccountFromDatabases(ac.getAccountId());
		if(ac.equals(ac2)&&ac2.getName().equals(n)){
			//Passed;
		}else{
			fail();
		}

		AdminAccountDAO.deleteAdminAccountFromDatabases(ac.getAccountId());
		try{
			AdminAccountDAO.getAdminAccountFromDatabases(ac.getAccountId());
		}catch(Exception ex){
			if(ex instanceof AdminAccountNotFound){
				//Passed;
			}else{				
				ex.printStackTrace();
				DebugLog.d(ex);
				fail();
			}
		}
	}

}
