package com.wyp.materialqqlite.qqclient.task;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetQQNumResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class QQNumTask extends HttpTask {

	public static final int OP_TYPE_BUDDY = 0;	// 获取好友号码
	public static final int OP_TYPE_GROUP = 1;	// 获取群号码
	public static final int OP_TYPE_SESS = 2;		// 获取群成员号码

	private ArrayList<QQNum_Param> m_arrParam;
	
	class QQNum_Param {
		int nType;			// 操作类型
		int nGroupCode;		// 群代码
		int nQQUin;			// 好友Uin或者群成员Uin
	};
	
	public QQNumTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
		m_arrParam = new ArrayList<QQNum_Param>();
	}
	
	public boolean getBuddyNum(int nQQUin) {
		QQNum_Param param = new QQNum_Param();
		param.nType = OP_TYPE_BUDDY;
		param.nGroupCode = 0;
		param.nQQUin = nQQUin;
		m_arrParam.add(param);
		return true;
	}
	
	public boolean getGroupNum(int nGroupCode) {
		QQNum_Param param = new QQNum_Param();
		param.nType = OP_TYPE_GROUP;
		param.nGroupCode = nGroupCode;
		param.nQQUin = 0;
		m_arrParam.add(param);
		return true;
	}
	
	public boolean getGMemberNum(int nGroupCode, int nQQUin) {
		QQNum_Param param = new QQNum_Param();
		param.nType = OP_TYPE_SESS;
		param.nGroupCode = nGroupCode;
		param.nQQUin = nQQUin;
		m_arrParam.add(param);
		return true;
	}
	
	public boolean addParam(int nType, int nGroupCode, int nQQUin) {
		QQNum_Param param = new QQNum_Param();
		param.nType = nType;
		param.nGroupCode = nGroupCode;
		param.nQQUin = nQQUin;
		m_arrParam.add(param);
		return true;
	}
	
	public void delAllItems() {
		m_arrParam.clear();
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser) {
			delAllItems();
			return;
		}
		
		try {
			for (int i = 0; i < m_arrParam.size(); i++) {
				QQNum_Param param = m_arrParam.get(i);

				GetQQNumResult result = new GetQQNumResult();

				if (OP_TYPE_BUDDY == param.nType)	{	// 获取好友号码
					boolean bRet = QQProtocol.getQQNum(m_httpClient, 
							true, param.nQQUin, m_QQUser.m_LoginResult2.m_strVfWebQq, result);
					if (m_bCancel) {
						delAllItems();
						return;
					}

					if (!bRet || result.m_nRetCode != 0) 
						result = null;

					sendMessage(QQCallBackMsg.UPDATE_BUDDY_NUMBER, 0, 0, result);
				}
				else if (OP_TYPE_SESS == param.nType) {	// 获取群成员号码
					boolean bRet = QQProtocol.getQQNum(m_httpClient, true, param.nQQUin, 
						m_QQUser.m_LoginResult2.m_strVfWebQq, result);
					if (m_bCancel) {
						delAllItems();
						return;
					}

					if (!bRet || result.m_nRetCode != 0)
						result = null;

					sendMessage(QQCallBackMsg.UPDATE_GMEMBER_NUMBER, param.nGroupCode, 0, result);
				}
				else if (OP_TYPE_GROUP == param.nType) {	// 获取群号码
					boolean bRet = QQProtocol.getQQNum(m_httpClient, false, param.nGroupCode, 
						m_QQUser.m_LoginResult2.m_strVfWebQq, result);
					if (m_bCancel) {
						delAllItems();
						return;
					}

					if (!bRet || result.m_nRetCode != 0)
						result = null;
					
					sendMessage(QQCallBackMsg.UPDATE_GROUP_NUMBER, param.nGroupCode, 0, result);
				}
			}

			delAllItems();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
