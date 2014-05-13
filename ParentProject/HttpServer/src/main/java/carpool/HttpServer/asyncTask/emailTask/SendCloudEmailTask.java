package carpool.HttpServer.asyncTask.emailTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.EmailConfig;
import carpool.HttpServer.configurations.EnumConfig.EmailEvent;
import carpool.HttpServer.interfaces.PseudoAsyncTask;

public class SendCloudEmailTask implements PseudoAsyncTask{
	
	private List<BasicNameValuePair> payload;

	
	public SendCloudEmailTask(String receiver, EmailEvent event, String url){
		
		JSONObject substitutionsPair = new JSONObject();
		ArrayList<String> toArr = new ArrayList<String>();
		toArr.add(receiver);
		JSONArray toArrJson = new JSONArray(toArr);
		substitutionsPair.put("to", toArrJson);
		ArrayList<String> subArr = new ArrayList<String>();
		subArr.add(url);
		JSONArray subArrJson = new JSONArray(subArr);
		JSONObject subCombineObject = new JSONObject();
		subCombineObject.put("%URLTARGET%", subArrJson);
		substitutionsPair.put("sub", subCombineObject);
		
		
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("api_user", EmailConfig.sendCloud_user));
		nvps.add(new BasicNameValuePair("api_key", EmailConfig.sendCloud_key));
		nvps.add(new BasicNameValuePair("html", "html"));
		nvps.add(new BasicNameValuePair("to", receiver));
		nvps.add(new BasicNameValuePair("substitution_vars", substitutionsPair.toString()));
		nvps.add(new BasicNameValuePair("fromname", "RouteA"));
		
		switch (event){
			case activeateAccount:
				nvps.add(new BasicNameValuePair("from", "auth@routea.ca"));
				nvps.add(new BasicNameValuePair("subject", "请激活您的账户"));
				nvps.add(new BasicNameValuePair("template_invoke_name", "account_activation"));
				break;
			
			case forgotPassword:
				nvps.add(new BasicNameValuePair("from", "auth@routea.ca"));
				nvps.add(new BasicNameValuePair("subject", "取回您的密码"));
				nvps.add(new BasicNameValuePair("template_invoke_name", "account_forgetPass"));
				break;
				
			case notification:
				nvps.add(new BasicNameValuePair("from", "info@routea.ca"));
				nvps.add(new BasicNameValuePair("subject", "最新动态提示"));
				nvps.add(new BasicNameValuePair("template_invoke_name", "account_notification"));
				break;
				
			default:
				DebugLog.d("Email Task handling non-existing email event, fatal error, abort");
				throw new RuntimeException();
		}
		
		this.payload = nvps;
	}
	

	public boolean execute(){
		return send();
	}
	
	public boolean send(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(EmailConfig.sendCloud_url);
		try {
			httpost.setEntity(new UrlEncodedFormEntity(this.payload, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			DebugLog.d(e);
			return false;
		} finally{
			httpost.releaseConnection();
		}

		
	}

}
