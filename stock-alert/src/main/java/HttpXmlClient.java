import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpXmlClient {
	private static Logger log = Logger.getLogger(HttpXmlClient.class);

	public static String post(String url, Map params) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
//        HttpHost proxy = new HttpHost("10.23.20.143", 8080);    
//        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);   
		String body = null;
		log.info("yfapp http post:" + url);
		HttpPost post = postForm(url, params);
		post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
				"UTF-8");
		body = invoke(httpclient, post);
		httpclient.getConnectionManager().shutdown();
		return body;
	}

	public static String get(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
//        HttpHost proxy = new HttpHost("10.23.20.143", 8080);    
//        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);   
		String body = null;
		log.info("yfapp http get:" + url);
		HttpGet get = new HttpGet(url);
		body = invoke(httpclient, get);
		httpclient.getConnectionManager().shutdown();

		return body;
	}

	private static String invoke(DefaultHttpClient httpclient,
			HttpUriRequest httpost) {
		HttpResponse response = sendRequest(httpclient, httpost);
		String body = paseResponse(response);
		return body;
	}

	private static String paseResponse(HttpResponse response) {
		log.debug("get response from http server..");
		HttpEntity entity = response.getEntity();
		log.info("response status: " + response.getStatusLine());
		String body = null;
		try {
			body = EntityUtils.toString(entity);
			log.info(body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient,
			HttpUriRequest httpost) {
		log.info("execute sendRequest...");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private static HttpPost postForm(String url, Map<String, String> params) {
		HttpPost httpost = new HttpPost(url);
		List nvps = new ArrayList();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			String value = null;
			try {
				//value = new String(params.get(key).getBytes("utf-8"), "GBK");
				value = new String(params.get(key).getBytes("utf-8"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			nvps.add(new BasicNameValuePair(key, value));
		}
		try {
			log.info("set utf-8 form entity to httppost");
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpost;
	}
}