package com.wyp.materialqqlite.qqclient.task;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.QQHttpClient;
import com.wyp.materialqqlite.qqclient.QQUser;

public class HttpTask extends Task {
	protected QQHttpClient m_httpClient = null;
	public QQUser m_QQUser = null;
	
	public HttpTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName);
		m_httpClient = new QQHttpClient(httpClient);
	}

	@Override
	public void cancelTask() {
		super.cancelTask();
		if (m_httpClient != null)
			m_httpClient.closeRequest();
	}
	
	protected boolean sendMessage(int nMsgId, 
			int nArg1, int nArg2, Object obj) {
		if (m_QQUser != null)
			return m_QQUser.sendProxyMsg(nMsgId, nArg1, nArg2, obj);
		else
			return false;
	}
	
	protected boolean sendMessage(int nMsgId, 
			int nArg1, int nArg2, Object obj, boolean bWait) {
		if (m_QQUser != null)
			return m_QQUser.sendProxyMsg(nMsgId, nArg1, nArg2, obj, bWait);
		else
			return false;
	}
}
