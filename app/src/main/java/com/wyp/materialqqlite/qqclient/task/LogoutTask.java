package com.wyp.materialqqlite.qqclient.task;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.LogoutResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQStatus;

public class LogoutTask extends HttpTask {
	
	public LogoutTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser) {
			return;
		}
		
		try {
			LogoutResult result = new LogoutResult();
			boolean bRet = QQProtocol.logout(m_httpClient, 
					QQProtocol.WEBQQ_CLIENT_ID, 
					m_QQUser.m_LoginResult2.m_strPSessionId, result);
			m_QQUser.m_nStatus = QQStatus.OFFLINE;
			if (!bRet || result.m_nRetCode != 0)
				sendMessage(QQCallBackMsg.LOGOUT_RESULT, 0, 0, null);
			else
				sendMessage(QQCallBackMsg.LOGOUT_RESULT, 1, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
