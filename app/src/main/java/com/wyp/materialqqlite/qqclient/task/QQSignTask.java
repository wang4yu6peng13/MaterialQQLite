package com.wyp.materialqqlite.qqclient.task;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetSignResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SetSignResult;

public class QQSignTask extends HttpTask {
	public static final int OP_TYPE_GET = 0;		// 获取
	public static final int OP_TYPE_SET = 1;		// 设置

	public int m_nType;			// 操作类型	
	public ArrayList<QQSign_Param_Get> m_arrGetParam;
	public ArrayList<QQSign_Param_Set> m_arrSetParam;
	
	private class QQSign_Param_Get {
		boolean bIsGMember;		// 是群成员还是好友
		int nGroupCode;			// 群代码
		int nQQUin;				// 好友Uin或者群成员Uin
	};

	private class QQSign_Param_Set {
		String m_strSign;		// 需要设置的个性签名
	};
	
	public QQSignTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
		m_arrGetParam = new ArrayList<QQSign_Param_Get>();
		m_arrSetParam = new ArrayList<QQSign_Param_Set>();
	}
	
	public boolean addGetParam(boolean bIsGMember, int nGroupCode, int nQQUin) {
		QQSign_Param_Get param = new QQSign_Param_Get();
		param.bIsGMember = bIsGMember;
		param.nGroupCode = nGroupCode;
		param.nQQUin = nQQUin;
		m_arrGetParam.add(param);
		return true;
	}
	
	public boolean addSetParam(String strSign) {
		if (null == strSign || strSign.length() <= 0)
			return false;

		QQSign_Param_Set param = new QQSign_Param_Set();
		param.m_strSign = strSign;
		m_arrSetParam.add(param);

		return true;
	}
	
	public void delAllItems() {
		m_arrGetParam.clear();
		m_arrSetParam.clear();
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser
				|| null == m_arrGetParam || null == m_arrSetParam) {
			return;
		}
		
		try {
			if (OP_TYPE_GET == m_nType)	// 获取QQ个性签名
			{
				for (int i = 0; i < m_arrGetParam.size(); i++)
				{
					QQSign_Param_Get param = m_arrGetParam.get(i);
					if (null == param)
						continue;
					
					GetSignResult result = new GetSignResult();

					boolean bRet = QQProtocol.getQQSign(m_httpClient, param.nQQUin, 
							m_QQUser.m_LoginResult2.m_strVfWebQq, result);
					if (m_bCancel)
						break;

					if (!bRet || result.m_nRetCode != 0)
						result = null;

					if (!param.bIsGMember)
						sendMessage(QQCallBackMsg.UPDATE_BUDDY_SIGN, 0, 0, result);
					else
						sendMessage(QQCallBackMsg.UPDATE_GMEMBER_SIGN, param.nGroupCode, 0, result);
				}
			}
			else if (OP_TYPE_SET == m_nType)	// 设置QQ个性签名
			{
				SetSignResult result = new SetSignResult();

				for (int i = 0; i < (int)m_arrSetParam.size(); i++)
				{
					QQSign_Param_Set param = m_arrSetParam.get(i);
					if (null == param)
						continue;
					
					boolean bRet = QQProtocol.setQQSign(m_httpClient, 
							param.m_strSign, m_QQUser.m_LoginResult2.m_strVfWebQq, result);
					if (m_bCancel)
						break;
					
					if (!bRet || result.m_nRetCode != 0)
						continue;
				}
			}
			
			delAllItems();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
