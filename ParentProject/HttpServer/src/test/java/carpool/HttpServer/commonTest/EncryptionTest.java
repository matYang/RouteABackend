package carpool.HttpServer.commonTest;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.encryption.EmailCrypto;
import carpool.HttpServer.encryption.ImgCrypto;
import carpool.HttpServer.encryption.SessionCrypto;



public class EncryptionTest {
	@Test
	public void emailCryptoTest(){
		CarpoolDaoBasic.clearBothDatabase();
		int id = 213213;
		String authCode = "%few%#gHUIBHFJ&^NFgdfgdJHGFHJ%";
		
		String encryptedString = EmailCrypto.encrypt(id, authCode);
		String[] decryptedStrings = EmailCrypto.decrypt(encryptedString);
		
		assertTrue( Integer.valueOf(decryptedStrings[0]) == id );
		assertTrue( decryptedStrings[1].equals(authCode));
	}
	
	@Test
	public void ImgCryptoTest(){
		CarpoolDaoBasic.clearBothDatabase();
		String testImgName = "83_--3498as2-re";
		
		String encryptedString = null;
		try {
			encryptedString = ImgCrypto.encrypt(testImgName);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
//		Common.d("Test_ImgCrypto::" + encryptedString);
		assertTrue(!encryptedString.equals(testImgName));
	}
	
	@Test
	public void SessionCryptoTest(){
		CarpoolDaoBasic.clearBothDatabase();
		String testSessionKey = "fds$@#tfsdu8YUFG3t+_1~j9";
		String encryptedString = "";
		String decryptedString = "";

		
		try {
			//taking approximately 3.3ms each iteration
			//taking approximately 1.7ms for single decryption, important here as decryption is required for all cookie checking
			for (int i = 0; i < 10000; i++){
				encryptedString = SessionCrypto.encrypt(testSessionKey);
				decryptedString = SessionCrypto.decrypt(encryptedString);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(!encryptedString.equals(""));
		assertTrue(!decryptedString.equals(""));
		assertTrue(testSessionKey.equals(decryptedString));
	}
}
