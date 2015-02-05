package com.wyp.materialqqlite.qqclient.task;

import java.io.File;

import org.apache.http.client.HttpClient;

import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.ContentType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class ChatPicTask extends HttpTask {
	public static final int OP_TYPE_BUDDY_PIC = 0;	// 获取好友聊天图片
	public static final int OP_TYPE_GROUP_PIC = 1;	// 获取群聊天图片
	public static final int OP_TYPE_SESS_PIC = 2;		// 获取群成员聊天图片，WebQQ协议目前不支持临时会话发送/接收自定义表情

	public int m_nType;
	public Object m_objMsg;
	
	public ChatPicTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient 
				|| null == m_QQUser || null == m_objMsg) {
			return;
		}
		
		try {
			if (OP_TYPE_BUDDY_PIC == m_nType)	// 好友图片
			{
				BuddyMessage objMsg = (BuddyMessage)m_objMsg;

				int nCount = objMsg.m_arrContent.size();
				for (int i = 0; i < nCount; i++)
				{
					Content content = objMsg.m_arrContent.get(i);
					if (null == content)
						continue;
					
					if (ContentType.CONTENT_TYPE_CUSTOM_FACE == content.m_nType)	// 自定义表情
					{
						byte[] bufPic = QQProtocol.getBuddyChatPic(m_httpClient, 
								objMsg.m_nMsgId, content.m_CFaceInfo.m_strName, 
								objMsg.m_nFromUin, QQProtocol.WEBQQ_CLIENT_ID, 
								m_QQUser.m_LoginResult2.m_strPSessionId);
						if (bufPic != null && bufPic.length > 0)
							savePic(content.m_CFaceInfo.m_strName, bufPic);
					}
					else if (ContentType.CONTENT_TYPE_OFF_PIC == content.m_nType)	// 离线图片
					{
						byte[] bufPic = QQProtocol.getBuddyOffChatPic(m_httpClient, 
							content.m_CFaceInfo.m_strName, objMsg.m_nFromUin, 
							QQProtocol.WEBQQ_CLIENT_ID, m_QQUser.m_LoginResult2.m_strPSessionId);
						if (bufPic != null && bufPic.length > 0)
							savePic(content.m_CFaceInfo.m_strName, bufPic);
					}
				}

				sendMessage(QQCallBackMsg.BUDDY_MSG, objMsg.m_nFromUin, 0, objMsg);
			}
			else if (OP_TYPE_GROUP_PIC == m_nType)	// 群图片
			{
				GroupMessage objMsg = (GroupMessage)m_objMsg;

				int nCount = objMsg.m_arrContent.size();
				for (int i = 0; i < nCount; i++)
				{
					Content content = objMsg.m_arrContent.get(i);
					if (null == content)
						continue;
					
					if (ContentType.CONTENT_TYPE_CUSTOM_FACE == content.m_nType)
					{
						String[] strTemp = content.m_CFaceInfo.m_strServer.split(":");
						
						String strServer = strTemp[0];
						int nPort = Integer.parseInt(strTemp[1]);

						byte[] bufPic = QQProtocol.getGroupChatPic(m_httpClient, 
								objMsg.m_nGroupCode, objMsg.m_nSendUin, strServer, 
								nPort, content.m_CFaceInfo.m_nFileId, 
								content.m_CFaceInfo.m_strName, 
								m_QQUser.m_LoginResult2.m_strVfWebQq);
						if (bufPic != null && bufPic.length > 0)
							savePic(content.m_CFaceInfo.m_strName, bufPic);
					}
				}

				sendMessage(QQCallBackMsg.GROUP_MSG, objMsg.m_nGroupCode, 0, objMsg);
			}
			else if (OP_TYPE_SESS_PIC == m_nType)
			{
				// WebQQ协议目前不支持临时会话发送/接收自定义表情
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	// 保存图片
	private boolean savePic(String strFileName, byte[] bytData) {
		if (null == strFileName || strFileName.length() <= 0
			|| null == bytData || bytData.length <= 0)
			return false;

		String strFullName = m_QQUser.getChatPicFullName(strFileName);

		String strPath = strFullName.substring(0, strFullName.lastIndexOf("/"));

		File dir = new File(strPath);
		if (!dir.exists())
			dir.mkdirs();
		
		Utils.writeFile(strFullName, bytData);
		
		return true;
	}
}
