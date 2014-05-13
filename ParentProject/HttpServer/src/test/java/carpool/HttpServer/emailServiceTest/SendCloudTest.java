package carpool.HttpServer.emailServiceTest;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import carpool.HttpServer.asyncRelayExecutor.ExecutorProvider;
import carpool.HttpServer.asyncTask.emailTask.HotmailEmailTask;
import carpool.HttpServer.asyncTask.emailTask.PseudoEmailTask;
import carpool.HttpServer.asyncTask.emailTask.SendCloudEmailTask;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.factory.JSONFactory;

public class SendCloudTest {

	//@Test
	public void test() throws ClientProtocolException, IOException {
		//test single
		
		String url = "https://sendcloud.sohu.com/webapi/mail.send_template.xml";
		HttpClient httpclient = new DefaultHttpClient();
		// httpclient = wrapHttpClient(httpclient);
		HttpPost httpost = new HttpPost(url);
		
		
		JSONObject substitutionsPair = new JSONObject();
		ArrayList<String> toArr = new ArrayList<String>();
		toArr.add("uwse@me.com");
		JSONArray toArrJson = new JSONArray(toArr);
		substitutionsPair.put("to", toArrJson);
		ArrayList<String> subArr = new ArrayList<String>();
		subArr.add("lalala");
		JSONArray subArrJson = new JSONArray(subArr);
		JSONObject subCombineObject = new JSONObject();
		subCombineObject.put("%URLTARGET%", subArrJson);
		substitutionsPair.put("sub", subCombineObject);
		
		
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("api_user", "postmaster@prod.sendcloud.org"));
		nvps.add(new BasicNameValuePair("api_key", "A4L7ARbK"));
		nvps.add(new BasicNameValuePair("from", "auth@routea.ca"));
		nvps.add(new BasicNameValuePair("to", "uwse@me.com"));
		nvps.add(new BasicNameValuePair("subject", "This is a test"));
		nvps.add(new BasicNameValuePair("html", "html"));
		nvps.add(new BasicNameValuePair("template_invoke_name", "account_activation"));
		nvps.add(new BasicNameValuePair("fromname", "Matthew Yang"));
		nvps.add(new BasicNameValuePair("substitution_vars", substitutionsPair.toString()));
		
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

		HttpResponse response = httpclient.execute(httpost);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
			String result = EntityUtils.toString(response.getEntity());
			System.out.println(result);
		} else {
			System.err.println("error");
		}
	}
	
	@Test
	public void massTest(){
		SendCloudEmailTask emailTask = new SendCloudEmailTask("uwse@me.com", EmailEvent.activeateAccount, "http://"+ServerConfig.domainName+"/#emailActivation");
		for (int i = 0; i < 150; i++){
			ExecutorProvider.executeRelay(emailTask);
		}
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
