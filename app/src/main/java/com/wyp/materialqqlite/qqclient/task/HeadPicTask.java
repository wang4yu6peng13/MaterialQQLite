package com.wyp.materialqqlite.qqclient.task;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class HeadPicTask extends HttpTask {

	public static final int OP_TYPE_BUDDY = 0;	// 获取好友头像
	public static final int OP_TYPE_GROUP = 1;	// 获取群头像
	public static final int OP_TYPE_SESS = 2;		// 获取群成员头像

	private ArrayList<HeadPic_Param> m_arrParam;
	
	class HeadPic_Param {
		int nType;			// 操作类型
		int nGroupCode;		// 群代码
		int nGroupNum;		// 群号码
		int nQQUin;			// 好友Uin
		int nQQNum;			// 好号码
	};
	
	public HeadPicTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
		m_arrParam = new ArrayList<HeadPic_Param>();
	}
	
	public boolean getBuddyHeadPic(int nQQUin, int nQQNum) {
		if (0 == nQQUin || 0 == nQQNum)
			return false;
		
		HeadPic_Param param = new HeadPic_Param();
		param.nType = OP_TYPE_BUDDY;
		param.nGroupCode = 0;
		param.nGroupNum = 0;
		param.nQQUin = nQQUin;
		param.nQQNum = nQQNum;
		m_arrParam.add(param);
		return true;
	}

	public boolean getGroupHeadPic(int nGroupCode, int nGroupNum) {
		if (0 == nGroupCode || 0 == nGroupNum)
			return false;
		
		HeadPic_Param param = new HeadPic_Param();
		param.nType = OP_TYPE_GROUP;
		param.nGroupCode = nGroupCode;
		param.nGroupNum = nGroupNum;
		param.nQQUin = 0;
		param.nQQNum = 0;
		m_arrParam.add(param);
		return true;
	}

	public boolean getGMemberHeadPic(int nGroupCode, int nQQUin, int nQQNum) {
		if (0 == nGroupCode || 0 == nQQUin || 0 == nQQNum)
			return false;
		
		HeadPic_Param param = new HeadPic_Param();
		param.nType = OP_TYPE_SESS;
		param.nGroupCode = nGroupCode;
		param.nGroupNum = 0;
		param.nQQUin = nQQUin;
		param.nQQNum = nQQNum;
		m_arrParam.add(param);
		return true;
	}
	
	public boolean addParam(int nType, int nGroupCode, 
			int nGroupNum, int nQQUin, int nQQNum) {
		HeadPic_Param param = new HeadPic_Param();
		param.nType = nType;
		param.nGroupCode = nGroupCode;
		param.nGroupNum = nGroupNum;
		param.nQQUin = nQQUin;
		param.nQQNum = nQQNum;
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
				HeadPic_Param param = m_arrParam.get(i);

				boolean bIsBuddy;
				int nQQUin;
				if (OP_TYPE_GROUP == param.nType) {	// 群头像
					bIsBuddy = false;
					nQQUin = param.nGroupCode;
				}
				else {	// 好友或群成员头像
					bIsBuddy = true;
					nQQUin = param.nQQUin;
				}

				byte[] bufPic = QQProtocol.getHeadPic(m_httpClient, 
						bIsBuddy, nQQUin, m_QQUser.m_LoginResult2.m_strVfWebQq);
				if (bufPic != null)
					savePic(param, bufPic);

				if (m_bCancel) {
					delAllItems();
					return;
				}

				if (OP_TYPE_BUDDY == param.nType)	// 好友
					sendMessage(QQCallBackMsg.UPDATE_BUDDY_HEADPIC, param.nQQUin, 0, null);
				else if (OP_TYPE_GROUP == param.nType)	// 群
					sendMessage(QQCallBackMsg.UPDATE_GROUP_HEADPIC, param.nGroupCode, 0, null);
				else if (OP_TYPE_SESS == param.nType)		// 群成员
					sendMessage(QQCallBackMsg.UPDATE_GMEMBER_HEADPIC, param.nGroupCode, param.nQQUin, null);
			}

			delAllItems();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	boolean savePic(HeadPic_Param param, byte[] bytData) {
		if (null == param || null == bytData || bytData.length <= 0)
			return false;

		String strFullName;
		if (OP_TYPE_BUDDY == param.nType)			// 好友
			strFullName = m_QQUser.getBuddyHeadPicFullName(param.nQQNum);
		else if (OP_TYPE_GROUP == param.nType)		// 群
			strFullName = m_QQUser.getGroupHeadPicFullName(param.nGroupNum);
		else if (OP_TYPE_SESS == param.nType)		// 群成员
			strFullName = m_QQUser.getSessHeadPicFullName(param.nQQNum);
		else
			return false;

		String strPath = strFullName.substring(0, strFullName.lastIndexOf("/"));

		File dir = new File(strPath);
		if (!dir.exists())
			dir.mkdirs();
		
		Utils.writeFile(strFullName, bytData);
		
		return true;
	}
}
