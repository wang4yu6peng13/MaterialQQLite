package com.wyp.materialqqlite.qqclient.task;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfoResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfoResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class InfoTask extends HttpTask {
	public static final int OP_TYPE_GETBUDDYINFO = 0;		// 获取好友信息
	public static final int OP_TYPE_GETGMEMBERINFO = 1;	// 获取群成员信息
	public static final int OP_TYPE_GETGROUPINFO = 2;		// 获取群信息
	public static final int OP_TYPE_SETBUDDYINFO = 3;		// 设置好友信息

	public int m_nType;			// 操作类型
	public int m_nGroupCode;	// 群代码
	public int m_nQQUin;		// 好友Uin或者群成员Uin
	
	public InfoTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser) {
			return;
		}
		
		try {
			if (OP_TYPE_GETBUDDYINFO == m_nType)	// 获取好友资料
			{
				BuddyInfoResult result = new BuddyInfoResult();
				
				boolean bRet = QQProtocol.getBuddyInfo(m_httpClient, 
						m_nQQUin, m_QQUser.m_LoginResult2.m_strVfWebQq, result);
				if (!bRet || result.m_nRetCode != 0)
					result = null;
					
				sendMessage(QQCallBackMsg.UPDATE_BUDDY_INFO, 0, 0, result);
			}
			else if (OP_TYPE_GETGMEMBERINFO == m_nType)		// 获取群成员资料
			{
				BuddyInfoResult result = new BuddyInfoResult();
				
				boolean bRet = QQProtocol.getStrangerInfo(m_httpClient, 
						m_nQQUin, m_QQUser.m_LoginResult2.m_strVfWebQq, result);
				if (!bRet || result.m_nRetCode != 0)
					result = null;
					
				sendMessage(QQCallBackMsg.UPDATE_GMEMBER_INFO, m_nGroupCode, 0, result);
			}
			else if (OP_TYPE_GETGROUPINFO == m_nType)		// 获取群资料
			{
				GroupInfoResult result = new GroupInfoResult();
				
				boolean bRet = QQProtocol.getGroupInfo(m_httpClient, 
						m_nGroupCode, m_QQUser.m_LoginResult2.m_strVfWebQq, result);
				if (!bRet || result.m_nRetCode != 0)
					result = null;
					
				sendMessage(QQCallBackMsg.UPDATE_GROUP_INFO, 0, 0, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
