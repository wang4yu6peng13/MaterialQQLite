package com.wyp.materialqqlite.qqclient.task;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQUtils;
import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.ContentType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetC2CMsgSigResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetGroupFaceSigResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQMsgType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SendBuddyMsgResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SendGroupMsgResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SendSessMsgResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SessMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.UploadCustomFaceResult;

public class SendMsgTask extends HttpTask {
	public QQMsgType m_nMsgType;
	public Object m_objMsg;
	public int m_nGroupNum;
	public int m_nQQNum;
	public String m_strNickName;
	public String m_strGroupSig;
	
	private static int m_nMsgId = 1100001;
	private static String m_strGFaceKey;
	private static String m_strGFaceSig;
	
	public SendMsgTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
	}
	
	public boolean addBuddyMsg(int nToUin, int nTime, String strMsg) {
		if (0 == nToUin || Utils.isEmptyStr(strMsg))
			return false;

		BuddyMessage buddyMsg = new BuddyMessage();

		m_nMsgId++;
		buddyMsg.m_nMsgId = m_nMsgId;
		buddyMsg.m_nTime = nTime;
		buddyMsg.m_nToUin = nToUin;

		QQUtils.createMsgContent(strMsg, buddyMsg.m_arrContent);

		m_objMsg = buddyMsg;
		if (m_QQUser != null)	// 使用到未加锁的主线程数据，不要在主线程之外的线程调用此函数
		{
			BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nToUin);
			if (buddyInfo != null)
				m_nQQNum = buddyInfo.m_nQQNum;
			m_strNickName = m_QQUser.m_UserInfo.m_strNickName;
		}

		return true;
	}
	
	public boolean addGroupMsg(int nGroupId, int nTime, String strMsg) {
		if (0 == nGroupId || Utils.isEmptyStr(strMsg))
			return false;

		GroupMessage groupMsg = new GroupMessage();

		m_nMsgId++;
		groupMsg.m_nMsgId = m_nMsgId;
		groupMsg.m_nTime = nTime;
		groupMsg.m_nToUin = nGroupId;
		if (m_QQUser != null)
			groupMsg.m_nGroupCode = m_QQUser.m_GroupList.getGroupCodeById(nGroupId);

		QQUtils.createMsgContent(strMsg, groupMsg.m_arrContent);

		m_objMsg = groupMsg;
		if (m_QQUser != null)	// 使用到未加锁的主线程数据，不要在主线程之外的线程调用此函数
		{
			GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupById(nGroupId);
			if (groupInfo != null)
			{
				m_nGroupNum = groupInfo.m_nGroupNumber;
				m_nQQNum = m_QQUser.m_nQQUin;
				BuddyInfo buddyInfo = groupInfo.getMemberByUin(m_QQUser.m_nQQUin);
				if (buddyInfo != null)
				{
					if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
						m_strNickName = buddyInfo.m_strGroupCard;
					else
						m_strNickName = buddyInfo.m_strNickName;
				}
			}
		}

		return true;
	}
	
	public boolean addSessMsg(int nGroupId, int nToUin, int nTime, String strMsg) {
		if (0 == nGroupId || 0 == nToUin || Utils.isEmptyStr(strMsg))
			return false;

		SessMessage sessMsg = new SessMessage();
		
		m_nMsgId++;
		sessMsg.m_nMsgId = m_nMsgId;
		sessMsg.m_nTime = nTime;
		sessMsg.m_nToUin = nToUin;
		sessMsg.m_nGroupId = nGroupId;
		
		QQUtils.createMsgContent(strMsg, sessMsg.m_arrContent);

		m_objMsg = sessMsg;
		if (m_QQUser != null)	// 使用到未加锁的主线程数据，不要在主线程之外的线程调用此函数
		{
			m_strNickName = m_QQUser.m_UserInfo.m_strNickName;

			BuddyInfo buddyInfo = m_QQUser.m_GroupList.getGroupMemberById(nGroupId, nToUin);
			if (buddyInfo != null)
			{
				m_nQQNum = buddyInfo.m_nQQNum;
				m_strGroupSig = buddyInfo.m_strGroupSig;
			}
		}

		return true;
	}
			
	// 上传自定义表情
	private boolean uploadCustomFace(String strFileName, UploadCustomFaceResult result) {
		boolean bRet = QQProtocol.uploadCustomFace(m_httpClient, 
				strFileName, m_QQUser.m_LoginResult2.m_strVfWebQq, result);
		if (!bRet || (result.m_nRetCode != 0 && result.m_nRetCode != 4))
			return false;
		return true;
	}
	
	// 发送好友消息
	private boolean sendBuddyMsg() {
		if (null == m_objMsg)
			return false;

		BuddyMessage msg = (BuddyMessage)m_objMsg;
		ArrayList<Content> arrContent = msg.m_arrContent;
		UploadCustomFaceResult uploadCFaceResult = new UploadCustomFaceResult();
		SendBuddyMsgResult sendMsgResult = new SendBuddyMsgResult();
		int nRetry = 3;		// 重试次数
		boolean bRet = false;

		for (int i = 0; i < arrContent.size(); i++)	// 上传自定义表情
		{
			Content content = arrContent.get(i);
			if (content != null && ContentType.CONTENT_TYPE_CUSTOM_FACE == content.m_nType)
			{
				for (int j = 0; j < nRetry; j++)
				{
					bRet = uploadCustomFace(content.m_CFaceInfo.m_strName, uploadCFaceResult);
					if (bRet)	// 上传成功
						break;
				}

				if (!bRet)
					return false;

				content.m_CFaceInfo.m_strRemoteFileName = uploadCFaceResult.m_strRemoteFileName;
			}
		}

		bRet = QQProtocol.sendBuddyMsg(m_httpClient, msg, QQProtocol.WEBQQ_CLIENT_ID, 
				m_QQUser.m_LoginResult2.m_strPSessionId, sendMsgResult);
		if (!bRet || (sendMsgResult.m_nRetCode != 0))
			return false;

		QQUtils.writeBuddyMsgLog(m_QQUser, m_nQQNum, m_strNickName, true, msg);

		return true;
	}
	
	// 发送群消息
	private boolean sendGroupMsg() {
		if (null == m_objMsg)
			return false;

		GroupMessage msg = (GroupMessage)m_objMsg;
		ArrayList<Content> arrContent = msg.m_arrContent;
		UploadCustomFaceResult uploadCFaceResult = new UploadCustomFaceResult();
		GetGroupFaceSigResult sigResult = new GetGroupFaceSigResult();
		SendGroupMsgResult sendMsgResult = new SendGroupMsgResult();
		boolean bHasCustomFace = false;
		int nRetry = 3;		// 重试次数
		boolean bRet = false;

		for (int i = 0; i < arrContent.size(); i++)	// 上传自定义表情
		{
			Content content = arrContent.get(i);
			if (content != null && ContentType.CONTENT_TYPE_CUSTOM_FACE == content.m_nType)
			{
				bHasCustomFace = true;

				for (int j = 0; j < nRetry; j++)
				{
					bRet = uploadCustomFace(content.m_CFaceInfo.m_strName, uploadCFaceResult);
					if (bRet)	// 上传成功
						break;
				}

				if (!bRet)
					return false;

				content.m_CFaceInfo.m_strRemoteFileName = uploadCFaceResult.m_strRemoteFileName;
			}
		}

		if (bHasCustomFace && (Utils.isEmptyStr(m_strGFaceKey) || Utils.isEmptyStr(m_strGFaceSig)))
		{
			bRet = QQProtocol.getGroupFaceSignal(m_httpClient, 
					QQProtocol.WEBQQ_CLIENT_ID, 
					m_QQUser.m_LoginResult2.m_strPSessionId, sigResult);
			if (!bRet || (sigResult.m_nRetCode != 0))
				return false;

			m_strGFaceKey = sigResult.m_strGFaceKey;
			m_strGFaceSig = sigResult.m_strGFaceSig;
		}

		bRet = QQProtocol.sendGroupMsg(m_httpClient, msg, 
				QQProtocol.WEBQQ_CLIENT_ID, m_QQUser.m_LoginResult2.m_strPSessionId, 
				m_strGFaceKey, m_strGFaceSig, sendMsgResult);
		if (!bRet || (sendMsgResult.m_nRetCode != 0))
			return false;

		QQUtils.writeGroupMsgLog(m_QQUser, m_nGroupNum, m_nQQNum, m_strNickName, msg);

		return true;
	}
	
	// 发送群成员消息
	private boolean sendSessMsg() {
		if (null == m_objMsg)
			return false;

		SessMessage msg = (SessMessage)m_objMsg;
		SendSessMsgResult sendMsgResult = new SendSessMsgResult();
		boolean bRet = false;

		if (Utils.isEmptyStr(m_strGroupSig))
		{
			GetC2CMsgSigResult getC2CMsgSigResult = new GetC2CMsgSigResult();
			
			bRet = QQProtocol.getC2CMsgSignal(m_httpClient, 
				msg.m_nGroupId, msg.m_nToUin, QQProtocol.WEBQQ_CLIENT_ID, 
				m_QQUser.m_LoginResult2.m_strPSessionId, getC2CMsgSigResult);
			if (!bRet || getC2CMsgSigResult.m_nRetCode != 0) {
				getC2CMsgSigResult = null;
				return false;
			}
			m_strGroupSig = getC2CMsgSigResult.m_strValue;
			getC2CMsgSigResult.m_nGroupId = msg.m_nGroupId;
			getC2CMsgSigResult.m_nQQUin = msg.m_nToUin;
			sendMessage(QQCallBackMsg.UPDATE_C2CMSGSIG, 0, 0, getC2CMsgSigResult);
		}

		bRet = QQProtocol.sendSessMsg(m_httpClient, msg, 
				m_strGroupSig, QQProtocol.WEBQQ_CLIENT_ID, 
				m_QQUser.m_LoginResult2.m_strPSessionId, sendMsgResult);
		if (!bRet || (sendMsgResult.m_nRetCode != 0))
			return false;

		QQUtils.writeSessMsgLog(m_QQUser, m_nQQNum, m_strNickName, true, msg);

		return true;
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser) {
			return;
		}
		
		try {
			if (QQMsgType.QQ_MSG_TYPE_BUDDY == m_nMsgType) {
				sendBuddyMsg();
			} else if (QQMsgType.QQ_MSG_TYPE_GROUP == m_nMsgType) {
				sendGroupMsg();
			} else if (QQMsgType.QQ_MSG_TYPE_SESS == m_nMsgType) {
				sendSessMsg();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
