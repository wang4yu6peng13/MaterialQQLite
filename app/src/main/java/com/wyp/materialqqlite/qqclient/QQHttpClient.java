package com.wyp.materialqqlite.qqclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

public class QQHttpClient {
	public final static int REQ_METHOD_GET = 0;
	public final static int REQ_METHOD_POST = 1;
	private HttpClient m_httpClient = null;
	private HttpGet m_httpGet = null;
	private HttpPost m_httpPost = null;
	private HttpResponse m_httpResp = null;
		
	public QQHttpClient(HttpClient httpClient) {
		m_httpClient = httpClient;
	}
		
	public boolean openRequest(String url, int nReqMethod) {
		closeRequest();
		
		if (nReqMethod == REQ_METHOD_GET) {
			m_httpGet = new HttpGet(url);
		} else if (nReqMethod == REQ_METHOD_POST) {
			m_httpPost = new HttpPost(url);
		} else {
			return false;
		}
		
		return true;
	}
	
	public void addHeader(String name, String value) {
		if (m_httpGet != null) {
			m_httpGet.addHeader(name, value);
		} else if (m_httpPost != null) {
			m_httpPost.addHeader(name, value);
		} 
	}
	
	public void setEntity(HttpEntity entity) {
		if (m_httpPost != null) {
			m_httpPost.setEntity(entity);
		}
	}
	
	public void sendRequest() {
		if (null == m_httpClient)
			return;
		
		try {
			if (m_httpGet != null) {
				m_httpResp = m_httpClient.execute(m_httpGet);
			} else if (m_httpPost != null) {
				m_httpResp = m_httpClient.execute(m_httpPost);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public int getRespCode() {
		if (m_httpResp != null)
			return m_httpResp.getStatusLine().getStatusCode();
		else
			return 0;
	}
	
	public Header[] getRespHeader() {
		if (m_httpResp != null)
			return m_httpResp.getAllHeaders();
		else
			return null;
	}
	
	public List<Cookie> getCookies() {
		if (m_httpClient != null)
			return ((DefaultHttpClient)m_httpClient).getCookieStore().getCookies();
		else
			return null;
	}
	
	public byte[] getRespBodyData() {
		try {
			if (m_httpResp != null) {
				InputStream is;
				is = m_httpResp.getEntity().getContent();
				byte[] bytData = InputStreamToByte(is);
				is.close();
				return bytData;
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void closeRequest() {
		if (m_httpGet != null)
			m_httpGet.abort();
		
		if (m_httpPost != null)
			m_httpPost.abort();
		
		m_httpResp = null;
		m_httpGet = null;
		m_httpPost = null;
	}
	
	public HttpClient getHttpClient() {
		return m_httpClient;
	}
	
	private byte[] InputStreamToByte(InputStream is) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		byte[] buf = new byte[1024 * 4];
		byte data[] = null;
		
		try {
			while ((ch = is.read(buf)) != -1) {
				out.write(buf, 0, ch);
			}
			data = out.toByteArray();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		
		return data;
	} 
}
