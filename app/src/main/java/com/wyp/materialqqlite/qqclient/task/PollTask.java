package com.wyp.materialqqlite.qqclient.task;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;

public class PollTask extends HttpTask {
	public TaskManager m_recvMsgTaskMgr;
	private long m_lCount = 0;
	
	public PollTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient 
				|| null == m_QQUser || null == m_recvMsgTaskMgr) {
			return;
		}
		
		try {
			while (true) {
				if (m_bCancel)
					break;

				byte[] bytMsgData = QQProtocol.poll(m_httpClient, 
						QQProtocol.WEBQQ_CLIENT_ID, 
						m_QQUser.m_LoginResult2.m_strPSessionId);
				if (null == bytMsgData || bytMsgData.length <= 0)
					continue;

				String strTaskName = "RecvMsgTask_" + m_lCount;
				RecvMsgTask2 task = new RecvMsgTask2(strTaskName, m_httpClient.getHttpClient());
				task.m_QQUser = m_QQUser;
				task.m_msgData = bytMsgData;
				m_recvMsgTaskMgr.addTask(task);
				m_lCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
