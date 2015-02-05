package com.wyp.materialqqlite.qqclient.task;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyp.materialqqlite.qqclient.QQUtils;
import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.ContentType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetQQNumResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfoResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.KickMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQMsgType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SessMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.StatusChangeMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SysGroupMessage;

public class RecvMsgTask2 extends HttpTask {
	public byte[] m_msgData;
	
	private HashMap<String, RMT_BUDDY_DATA> m_mapBuddyData;
	private HashMap<String, RMT_GROUP_DATA> m_mapGroupData;
	private HashMap<String, Integer> m_mapGroupId2Code;
	private HashMap<String, RMT_BUDDY_DATA> m_mapGMemberData;
	private static int m_nPreMsgId, m_nPreMsgId2;
	private static long m_lChatPicCnt = 0;

	private class RecvMsg {
		public QQMsgType m_nType;
		public Object m_objMsg;
		
		public int getMsgId() {
			if (null == m_objMsg)
				return 0;

			switch (m_nType)
			{
			case QQ_MSG_TYPE_BUDDY:
				return ((BuddyMessage)m_objMsg).m_nMsgId;
			case QQ_MSG_TYPE_GROUP:
				return ((GroupMessage)m_objMsg).m_nMsgId;
			case QQ_MSG_TYPE_SESS:
				return ((SessMessage)m_objMsg).m_nMsgId;
			default:
				return 0;
			}
		}
		
		public int getMsgId2() {
			if (null == m_objMsg)
				return 0;

			switch (m_nType)
			{
			case QQ_MSG_TYPE_BUDDY:
				return ((BuddyMessage)m_objMsg).m_nMsgId2;
			case QQ_MSG_TYPE_GROUP:
				return ((GroupMessage)m_objMsg).m_nMsgId2;
			case QQ_MSG_TYPE_SESS:
				return ((SessMessage)m_objMsg).m_nMsgId2;
			default:
				return 0;
			}
		}
	
		public int getTime() {
			if (null == m_objMsg)
				return 0;

			switch (m_nType)
			{
			case QQ_MSG_TYPE_BUDDY:
				return ((BuddyMessage)m_objMsg).m_nTime;
			case QQ_MSG_TYPE_GROUP:
				return ((GroupMessage)m_objMsg).m_nTime;
			case QQ_MSG_TYPE_SESS:
				return ((SessMessage)m_objMsg).m_nTime;
			default:
				return 0;
			}
		}
	}

	private class RMT_BUDDY_DATA {
		int nQQNum;
		String strNickName;
	}

	private class RMT_GROUP_DATA {
		boolean bHasGroupInfo;
		int nGroupNum;
	}
	
	public RecvMsgTask2(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
		
		m_mapBuddyData = new HashMap<String, RMT_BUDDY_DATA>();
		m_mapGroupData = new HashMap<String, RMT_GROUP_DATA>();
		m_mapGroupId2Code = new HashMap<String, Integer>();
		m_mapGMemberData = new HashMap<String, RMT_BUDDY_DATA>();
	}
	
	private boolean handleMsg(byte[] bytMsgData) {
		if (null == bytMsgData)
			return false;

		ArrayList<RecvMsg> arrMsg = new ArrayList<RecvMsg>(); 
		boolean bRet = parseMsg(bytMsgData, arrMsg);
		if (!bRet || arrMsg.size() <= 0)
			return false;

		if (arrMsg.size() > 1)
			sortMsgByTime(arrMsg);

		for (int i = 0; i < arrMsg.size(); i++)
		{
			RecvMsg msg = arrMsg.get(i);
			if (null == msg)
				continue;
			
			if (QQMsgType.QQ_MSG_TYPE_BUDDY == msg.m_nType) {
				handleBuddyMsg(msg);
			} else if (QQMsgType.QQ_MSG_TYPE_GROUP == msg.m_nType) {
				handleGroupMsg(msg);
			} else if (QQMsgType.QQ_MSG_TYPE_SESS == msg.m_nType) {
				handleSessMsg(msg);
			} else if (QQMsgType.QQ_MSG_TYPE_STATUSCHANGE == msg.m_nType) {
				handleStatusChangeMsg(msg);
			} else if (QQMsgType.QQ_MSG_TYPE_KICK == msg.m_nType) {
				handleKickMsg(msg);
			} else if (QQMsgType.QQ_MSG_TYPE_SYSGROUP == msg.m_nType) {
				handleSysGroupMsg(msg);
			}
		}

		return true;
	}
	
	private boolean parseMsg(byte[] bytMsgData, ArrayList<RecvMsg> arrMsg) {
		if (null == bytMsgData || bytMsgData.length <= 0 || null == arrMsg)
			return false;

		try {
			String strData = new String(bytMsgData, "UTF-8");
			System.out.println(strData);
			
			JSONObject json = new JSONObject(strData);
			int nRetCode = json.getInt("retcode");

			switch (nRetCode)
			{
			case 0:
				{
					JSONArray json2 = json.getJSONArray("result");
					
					for (int i = 0; i < json2.length(); i++)
					{
						RecvMsg msg = new RecvMsg();
						JSONObject josn3 = json2.getJSONObject(i);
						boolean bRet = parseMsg(josn3, msg);
						if (bRet && !isMsgRepeat(arrMsg, msg))
							arrMsg.add(msg);
					}
				}
				break;

			case 116:
				break;

			default:
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	
	private boolean parseMsg(JSONObject json, RecvMsg msg) {
		String strPollType;
		try {
			strPollType = json.getString("poll_type");
			
			if (BuddyMessage.isType(strPollType))	// 好友消息
			{
				BuddyMessage buddyMsg = new BuddyMessage();

				boolean bRet = buddyMsg.parse(json.getJSONObject("value"));
				if (!bRet)
					return false;

				msg.m_nType = QQMsgType.QQ_MSG_TYPE_BUDDY;
				msg.m_objMsg = buddyMsg;
			}
			else if (GroupMessage.isType(strPollType))	// 群消息
			{
				GroupMessage groupMsg = new GroupMessage();

				boolean bRet = groupMsg.parse(json.getJSONObject("value"));
				if (!bRet)
					return false;

				msg.m_nType = QQMsgType.QQ_MSG_TYPE_GROUP;
				msg.m_objMsg = groupMsg;
			}
			else if (SessMessage.isType(strPollType))	// 临时会话消息
			{
				SessMessage sessMsg = new SessMessage();

				boolean bRet = sessMsg.parse(json.getJSONObject("value"));
				if (!bRet)
					return false;

				msg.m_nType = QQMsgType.QQ_MSG_TYPE_SESS;
				msg.m_objMsg = sessMsg;
			}
			else if (StatusChangeMessage.isType(strPollType))	// 状态改变通知消息
			{
				StatusChangeMessage statusChangeMsg = new StatusChangeMessage();

				boolean bRet = statusChangeMsg.parse(json.getJSONObject("value"));
				if (!bRet)
					return false;

				msg.m_nType = QQMsgType.QQ_MSG_TYPE_STATUSCHANGE;
				msg.m_objMsg = statusChangeMsg;
			}
			else if (KickMessage.isType(strPollType))	// 被踢下线通知消息
			{
				KickMessage kickMsg = new KickMessage();

				boolean bRet = kickMsg.parse(json.getJSONObject("value"));
				if (!bRet)
					return false;

				msg.m_nType = QQMsgType.QQ_MSG_TYPE_KICK;
				msg.m_objMsg = kickMsg;
			}
			else if (SysGroupMessage.isType(strPollType))	// 群系统消息
			{
				SysGroupMessage sysGroupMsg = new SysGroupMessage();
				
				boolean bRet = sysGroupMsg.parse(json.getJSONObject("value"));
				if (!bRet)
					return false;

				msg.m_nType = QQMsgType.QQ_MSG_TYPE_SYSGROUP;
				msg.m_objMsg = sysGroupMsg;
			}
			else
			{
				// Unknown message type
				return false;
			}
			
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean isMsgRepeat(ArrayList<RecvMsg> arrMsg, RecvMsg msg)
	{
		if (null == arrMsg || null == msg)
			return true;

		if (msg.m_nType != QQMsgType.QQ_MSG_TYPE_BUDDY 
			&& msg.m_nType != QQMsgType.QQ_MSG_TYPE_GROUP 
			&& msg.m_nType != QQMsgType.QQ_MSG_TYPE_SESS)
			return false;

		int nMsgId = msg.getMsgId();
		int nMsgId2 = msg.getMsgId2();

		for (int i = 0; i < arrMsg.size(); i++)
		{
			RecvMsg msg2 = arrMsg.get(i);
			if (msg2 != null && msg2.getMsgId() == nMsgId && msg2.getMsgId2() == nMsgId2)
				return true;
		}

		if (m_nPreMsgId == nMsgId && m_nPreMsgId2 == nMsgId2)
			return true;
		
		m_nPreMsgId = nMsgId;
		m_nPreMsgId2 = nMsgId2;

		return false;
	}

	private void sortMsgByTime(ArrayList<RecvMsg> arrMsg) {
		RecvMsg msg1, msg2;
		boolean bExchange;
		int nCount;

		nCount = arrMsg.size();
		for (int i = 0; i < nCount-1; i++)
		{
			bExchange = false;
			for (int j = nCount-1; j > i; j--)
			{
				msg1 = arrMsg.get(j-1);
				msg2 = arrMsg.get(j);
				if (msg1 != null && msg2 != null)
				{
					if (msg2.getTime() < msg1.getTime())
					{
						arrMsg.set(j-1, msg2);
						arrMsg.set(j, msg1);
						bExchange = true;
					}
				}
			}
			if (!bExchange)
				break;
		}
	}
	
	private boolean handleBuddyMsg(RecvMsg recvMsg) {
		if (null == recvMsg || null == recvMsg.m_objMsg)
			return false;

		BuddyMessage msg = (BuddyMessage)recvMsg.m_objMsg;

		int nQQNum = 0;
		String strNickName = "";

		RMT_BUDDY_DATA buddyData = getBuddyData(msg.m_nFromUin);
		if (buddyData != null) {
			nQQNum = buddyData.nQQNum;
			strNickName = buddyData.strNickName;
		}

		QQUtils.writeBuddyMsgLog(m_QQUser, nQQNum, strNickName, false, msg);	// 写入消息记录

		if (isNeedDownloadPic(msg.m_arrContent))	// 需要下载图片
			startChatPicTask(ChatPicTask.OP_TYPE_BUDDY_PIC, msg);
		else
			sendMessage(QQCallBackMsg.BUDDY_MSG, msg.m_nFromUin, 0, msg);

		return true;
	}

	private boolean handleGroupMsg(RecvMsg recvMsg) {
		if (null == recvMsg || null == recvMsg.m_objMsg)
			return false;

		GroupMessage msg = (GroupMessage)recvMsg.m_objMsg;

		int nGroupNum = 0;
		int nQQNum = 0;
		String strNickName = "";

		RMT_GROUP_DATA groupData = getGroupData(msg.m_nGroupCode);
		if (groupData != null)
			nGroupNum = groupData.nGroupNum;

		RMT_BUDDY_DATA buddyData = getGMemberData(msg.m_nGroupCode, msg.m_nSendUin);
		if (buddyData != null) {
			nQQNum = buddyData.nQQNum;
			strNickName = buddyData.strNickName;
		}
		
		if (null == strNickName) {
			System.out.println("空的");
		}

		QQUtils.writeGroupMsgLog(m_QQUser, nGroupNum, nQQNum, strNickName, msg);	// 写入消息记录

		if (isNeedDownloadPic(msg.m_arrContent))	// 需要下载图片
			startChatPicTask(ChatPicTask.OP_TYPE_GROUP_PIC, msg);
		else
			sendMessage(QQCallBackMsg.GROUP_MSG, msg.m_nGroupCode, 0, msg);

		return true;
	}

	private boolean handleSessMsg(RecvMsg recvMsg) {
		if (null == recvMsg || null == recvMsg.m_objMsg)
			return false;

		SessMessage msg = (SessMessage)recvMsg.m_objMsg;

		int nQQNum = 0;
		String strNickName = "";

		int nGroupCode = groupId2Code(msg.m_nGroupId);	// 群标识转换到群代码
		if (nGroupCode != 0) {
			RMT_GROUP_DATA groupData = getGroupData(nGroupCode);	// 确保群信息已获取

			RMT_BUDDY_DATA buddyData = getGMemberData(nGroupCode, msg.m_nFromUin);
			if (buddyData != null) {
				nQQNum = buddyData.nQQNum;
				strNickName = buddyData.strNickName;
			}
		}

		QQUtils.writeSessMsgLog(m_QQUser, nQQNum, strNickName, false, msg);	// 写入消息记录

		if (isNeedDownloadPic(msg.m_arrContent))	// 需要下载图片
			startChatPicTask(ChatPicTask.OP_TYPE_SESS_PIC, msg);
		else	
			sendMessage(QQCallBackMsg.SESS_MSG, nGroupCode, msg.m_nFromUin, msg);

		return true;
	}

	private boolean handleStatusChangeMsg(RecvMsg recvMsg) {
		if (null == recvMsg || null == recvMsg.m_objMsg)
			return false;
		sendMessage(QQCallBackMsg.STATUS_CHANGE_MSG, 0, 0, recvMsg.m_objMsg);
		return true;
	}

	private boolean handleKickMsg(RecvMsg recvMsg) {
		if (null == recvMsg || null == recvMsg.m_objMsg)
			return false;
		sendMessage(QQCallBackMsg.KICK_MSG, 0, 0, recvMsg.m_objMsg);
		return true;
	}

	private boolean handleSysGroupMsg(RecvMsg recvMsg) {
		if (null == recvMsg || null == recvMsg.m_objMsg)
			return false;
		sendMessage(QQCallBackMsg.SYS_GROUP_MSG, 0, 0, recvMsg.m_objMsg);
		return true;
	}
	
	private RMT_BUDDY_DATA getBuddyData(int nQQUin) {
		String strKey = String.valueOf(nQQUin);
		
		RMT_BUDDY_DATA data = m_mapBuddyData.get(strKey);
		if (data != null) {
			if (data.nQQNum != 0 && 
					data.strNickName != null)
				return data;
			else
				m_mapBuddyData.remove(strKey);
		}

		data = new RMT_BUDDY_DATA();
		m_mapBuddyData.put(strKey, data);
		
		sendMessage(QQCallBackMsg.INTERNAL_GETBUDDYDATA, nQQUin, 0, null, true);
		
		data.nQQNum = m_QQUser.m_internalData.m_nQQNum;
		if (m_QQUser.m_internalData.m_strNickName != null)
			data.strNickName = new String(m_QQUser.m_internalData.m_strNickName);

		if (data.nQQNum != 0 && data.strNickName != null)
			return data;

		if (0 == data.nQQNum) {
			GetQQNumResult result = new GetQQNumResult();

			boolean bRet = false;
			int nRetry = 3;
			for (int i = 0; i < nRetry; i++) {
				bRet = QQProtocol.getQQNum(m_httpClient, true, 
						nQQUin, m_QQUser.m_LoginResult2.m_strVfWebQq, result);
				if (bRet && 0 == result.m_nRetCode)
					break;
			}

			if (bRet && 0 == result.m_nRetCode) {
				data.nQQNum = result.m_nQQNum;
				sendMessage(QQCallBackMsg.UPDATE_BUDDY_NUMBER, 0, 0, result);
			}
		}

		if (null == data.strNickName) {
			// 假定昵称一定能够从好友列表获取到，这里不处理
		}

		return data;
	}

	private RMT_GROUP_DATA getGroupData(int nGroupCode) {
		String strKey = String.valueOf(nGroupCode);
		
		RMT_GROUP_DATA data = m_mapGroupData.get(strKey);
		if (data != null) {
			if (data.bHasGroupInfo && data.nGroupNum != 0)
				return data;
			else
				m_mapGroupData.remove(strKey);
		}

		data = new RMT_GROUP_DATA();
		m_mapGroupData.put(strKey, data);
		
		sendMessage(QQCallBackMsg.INTERNAL_GETGROUPDATA, nGroupCode, 0, null, true);

		data.bHasGroupInfo = m_QQUser.m_internalData.m_bHasGroupInfo;
		data.nGroupNum = m_QQUser.m_internalData.m_nGroupNum;
		
		if (data.bHasGroupInfo && data.nGroupNum != 0)
			return data;

		if (!data.bHasGroupInfo) {
			GroupInfoResult result = new GroupInfoResult();

			boolean bRet = false;
			int nRetry = 3;
			for (int i = 0; i < nRetry; i++) {
				bRet = QQProtocol.getGroupInfo(m_httpClient, nGroupCode, 
						m_QQUser.m_LoginResult2.m_strVfWebQq, result);
				if (bRet && 0 == result.m_nRetCode)
					break;
				if (bRet && 6 == result.m_nRetCode)
					nRetry = 3;
			}

			if (bRet && 0 == result.m_nRetCode) {
				data.bHasGroupInfo = true;
			}

			sendMessage(QQCallBackMsg.UPDATE_GROUP_INFO, 0, 0, result);
		}

		if (0 == data.nGroupNum) {
			GetQQNumResult result = new GetQQNumResult();
			
			boolean bRet = false;
			int nRetry = 3;
			for (int i = 0; i < nRetry; i++) {
				bRet = QQProtocol.getQQNum(m_httpClient, false, nGroupCode, 
						m_QQUser.m_LoginResult2.m_strVfWebQq, result);
				if (bRet && 0 == result.m_nRetCode)
					break;
			}

			if (bRet && 0 == result.m_nRetCode) {
				data.nGroupNum = result.m_nQQNum;
				sendMessage(QQCallBackMsg.UPDATE_GROUP_NUMBER, nGroupCode, 0, result);
			}
		}

		return data;
	}

	private RMT_BUDDY_DATA getGMemberData(int nGroupCode, int nQQUin) {		
		String strKey = String.valueOf(nGroupCode) + "_" + String.valueOf(nQQUin);
		
		RMT_BUDDY_DATA data = m_mapGMemberData.get(strKey);
		if (data != null) {
			if (data.nQQNum != 0 && 
					data.strNickName != null)
				return data;
			else
				m_mapGMemberData.remove(strKey);
		}

		data = new RMT_BUDDY_DATA();
		m_mapGMemberData.put(strKey, data);

		sendMessage(QQCallBackMsg.INTERNAL_GETGMEMBERDATA, 
				nGroupCode, nQQUin, null, true);

		data.nQQNum = m_QQUser.m_internalData.m_nQQNum;
		if (m_QQUser.m_internalData.m_strNickName != null)
			data.strNickName = new String(m_QQUser.m_internalData.m_strNickName);
		
		if (data.nQQNum != 0 && 
				data.strNickName != null)
			return data;

		if (0 == data.nQQNum) {
			GetQQNumResult result = new GetQQNumResult();

			boolean bRet = false;
			int nRetry = 3;
			for (int i = 0; i < nRetry; i++) {
				bRet = QQProtocol.getQQNum(m_httpClient, true, nQQUin, 
						m_QQUser.m_LoginResult2.m_strVfWebQq, result);
				if (bRet && 0 == result.m_nRetCode)
					break;
			}

			if (bRet && 0 == result.m_nRetCode) {
				data.nQQNum = result.m_nQQNum;
				sendMessage(QQCallBackMsg.UPDATE_GMEMBER_NUMBER, 
						nGroupCode, 0, result);
			}
		}

		if (null == data.strNickName) {
			// 假定昵称一定能够从好友列表获取到，这里不处理
		}

		return data;
	}
	
	private int groupId2Code(int nGroupId) {
		String strKey = String.valueOf(nGroupId);
		
		Integer nGroupCode = m_mapGroupId2Code.get(strKey);
		if (nGroupCode != null) {
			if (nGroupCode != 0)
				return nGroupCode;
			else
				m_mapGroupId2Code.remove(strKey);
		}

		sendMessage(QQCallBackMsg.INTERNAL_GROUPID2CODE, nGroupId, 0, null, true);
		
		nGroupCode = m_QQUser.m_internalData.m_nGroupCode;
		
		if (nGroupCode != 0) {
			m_mapGroupId2Code.put(strKey, nGroupCode);
			return nGroupCode;
		}
		
		return 0;
	}

	private boolean isNeedDownloadPic(ArrayList<Content> arrContent) {
		for (int i = 0; i < arrContent.size(); i++) {
			Content content = arrContent.get(i);
			if (null == content)
				continue;

			if (ContentType.CONTENT_TYPE_CUSTOM_FACE == content.m_nType 
				|| ContentType.CONTENT_TYPE_OFF_PIC == content.m_nType) {
				String strFullName = m_QQUser.getChatPicFullName(content.m_CFaceInfo.m_strName);
				File file = new File(strFullName);
				if (!file.exists())
					return true;
			}
		}
		return false;
	}

	private boolean startChatPicTask(int nType, Object objMsg) {
		String strTaskName = "ChatPicTask_" + m_lChatPicCnt;
		ChatPicTask task = new ChatPicTask(strTaskName, m_httpClient.getHttpClient());
		task.m_QQUser = m_QQUser;
		task.m_nType = nType;
		task.m_objMsg = objMsg;
		m_taskMgr.addTask(task);
		m_lChatPicCnt++;
		return true;
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient 
				|| null == m_QQUser || null == m_msgData) {
			return;
		}
		
		try {
			handleMsg(m_msgData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		
		m_mapBuddyData.clear();
		m_mapGroupData.clear();
		m_mapGroupId2Code.clear();
		m_mapGMemberData.clear();
		m_nPreMsgId = 0;
		m_nPreMsgId2 = 0;
		m_lChatPicCnt = 0;
	}
}
