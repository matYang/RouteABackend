package adminDaoTest;

import static org.junit.Assert.*;

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

		AdminAccountDAO.deleteAdminAccountFromDatabases(ac);
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
