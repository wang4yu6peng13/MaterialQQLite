package com.wyp.materialqqlite.qqclient.task;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetGroupFaceSigResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class GroupFaceSigTask extends HttpTask {
	
	public GroupFaceSigTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser) {
			return;
		}
		
		try {
			GetGroupFaceSigResult result = new GetGroupFaceSigResult();
			boolean bRet = QQProtocol.getGroupFaceSignal(m_httpClient, 
					QQProtocol.WEBQQ_CLIENT_ID, 
					m_QQUser.m_LoginResult2.m_strPSessionId, result);
			if (!bRet || result.m_nRetCode != 0)
				result = null;
			sendMessage(QQCallBackMsg.UPDATE_GROUPFACESIG, 0, 0, result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
