package carpool.HttpServer.smsTest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

public class WangjianTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://gbk.sms.webchinese.cn"); 
		
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("Uid", "routea"));
		nvps.add(new BasicNameValuePair("Key", "a221a629eacbddc0720c"));
		nvps.add(new BasicNameValuePair("smsMob", "15050878666"));
		nvps.add(new BasicNameValuePair("smsText", "您的验证码：Simmmmon"));
		
		post.setEntity(new UrlEncodedFormEntity(nvps, "gbk"));
		
		post.addHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");

		HttpResponse response = client.execute(post);
		
		
		Header[] headers = response.getAllHeaders();
		System.out.println("statusCode:"+response.getStatusLine().getStatusCode());
		for(Header h : headers){
			System.out.println(h.toString());
		}
		String result = new String(response.getEntity().toString().getBytes("gbk")); 
		System.out.println(result);


		post.releaseConnection();

	}

}
