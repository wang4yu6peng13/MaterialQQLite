package com.wyp.materialqqlite.qqclient.task;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.ChangeStatusResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class ChangeStatusTask extends HttpTask {
	public int m_nStatus;
	
	public ChangeStatusTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser) {
			return;
		}
		
		try {
			ChangeStatusResult result = new ChangeStatusResult();
			boolean bRet = QQProtocol.changeStatus(m_httpClient, 
					m_nStatus, QQProtocol.WEBQQ_CLIENT_ID, 
					m_QQUser.m_LoginResult2.m_strPSessionId, result);
			if (!bRet || result.m_nRetCode != 0)
				sendMessage(QQCallBackMsg.CHANGE_STATUS_RESULT, 0, 0, null);
			else
				sendMessage(QQCallBackMsg.CHANGE_STATUS_RESULT, 1, m_nStatus, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
