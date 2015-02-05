package com.wyp.materialqqlite.qqclient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.msglog.BuddyMsgLog;
import com.wyp.materialqqlite.qqclient.msglog.GroupMsgLog;
import com.wyp.materialqqlite.qqclient.msglog.SessMsgLog;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.ContentType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SessMessage;

public class QQUtils {
	// 写入一条好友消息记录
	public static void writeBuddyMsgLog(QQUser qquser, int nQQNum, 
			String strNickName, boolean bSelf, BuddyMessage msg) {
		if (null == qquser || null == qquser.m_MsgLogger
				|| 0 == nQQNum || null == msg)
			return;

		if (Utils.isEmptyStr(strNickName))
			strNickName = String.valueOf(nQQNum);

		String strContent = formatContent(msg.m_arrContent);

		if (!qquser.m_MsgLogger.isOpen()) {
			String strFullName = qquser.getMsgLogFullName(qquser.m_nQQUin);
			
			String strPath = strFullName.substring(0, strFullName.lastIndexOf("/"));

			File dir = new File(strPath);
			if (!dir.exists())
				dir.mkdirs();
			
			qquser.m_MsgLogger.open(strFullName);
		}
		
		qquser.m_MsgLogger.writeBuddyMsgLog(nQQNum, strNickName, 
			msg.m_nTime, bSelf, strContent);
	}

	// 写入一条群消息记录
	public static void writeGroupMsgLog(QQUser qquser, 
			int nGroupNum, int nQQNum, String strNickName, GroupMessage msg) {
		if (null == qquser || null == qquser.m_MsgLogger
				|| 0 == nGroupNum || null == msg)
			return;

		if (Utils.isEmptyStr(strNickName))
			strNickName = String.valueOf(nQQNum);

		String strContent = formatContent(msg.m_arrContent);

		if (!qquser.m_MsgLogger.isOpen()) {
			String strFullName = qquser.getMsgLogFullName(qquser.m_nQQUin);
			
			String strPath = strFullName.substring(0, strFullName.lastIndexOf("/"));

			File dir = new File(strPath);
			if (!dir.exists())
				dir.mkdirs();

			qquser.m_MsgLogger.open(strFullName);
		}
		
		qquser.m_MsgLogger.writeGroupMsgLog(nGroupNum, nQQNum, 
			strNickName, msg.m_nTime, strContent);
	}

	// 写入一条临时会话(群成员)消息记录
	public static void writeSessMsgLog(QQUser qquser, int nQQNum, 
			String strNickName, boolean bSelf, SessMessage msg) {
		if (null == qquser || null == qquser.m_MsgLogger
				|| 0 == nQQNum || null == msg)
			return;

		if (Utils.isEmptyStr(strNickName))
			strNickName = String.valueOf(nQQNum);

		String strContent = formatContent(msg.m_arrContent);

		if (!qquser.m_MsgLogger.isOpen()) {
			String strFullName = qquser.getMsgLogFullName(qquser.m_nQQUin);
			
			String strPath = strFullName.substring(0, strFullName.lastIndexOf("/"));

			File dir = new File(strPath);
			if (!dir.exists())
				dir.mkdirs();

			qquser.m_MsgLogger.open(strFullName);
		}
		
		qquser.m_MsgLogger.writeSessMsgLog(nQQNum, strNickName, msg.m_nTime, bSelf, strContent);
	}
	
	// "/f["系统表情id"] /c["自定义表情文件名"] /o["字体名称，大小，颜色，加粗，倾斜，下划线"]"
	public static String formatContent(ArrayList<Content> arrContent) {
		String strMsg = "";

		for (int i = 0; i < arrContent.size(); i++) {
			Content content = arrContent.get(i);
			if (null == content)
				continue;

			if (ContentType.CONTENT_TYPE_FONT_INFO == content.m_nType) {
					String strColor = Utils.RGBToHexStr(content.m_FontInfo.m_clrText);

					strMsg += "/o[\"";
					strMsg += content.m_FontInfo.m_strName;
					strMsg += ",";
					strMsg += content.m_FontInfo.m_nSize;
					strMsg += ",";
					strMsg += strColor;
					strMsg += ",";
					strMsg += (content.m_FontInfo.m_bBold ? "1" : "0");
					strMsg += ",";
					strMsg += (content.m_FontInfo.m_bItalic ? "1" : "0");
					strMsg += ",";
					strMsg += (content.m_FontInfo.m_bUnderLine ? "1" : "0");
					strMsg += "\"]";
			} else if (ContentType.CONTENT_TYPE_TEXT == content.m_nType) {
				String strText = new String(content.m_strText);
				strText = strText.replace("/", "//");
				strMsg += strText;
			} else if (ContentType.CONTENT_TYPE_FACE == content.m_nType) {
				strMsg += "/f[\"";
				strMsg += content.m_nFaceId;
				strMsg += "\"]";
			} else if (ContentType.CONTENT_TYPE_CUSTOM_FACE == content.m_nType
					|| ContentType.CONTENT_TYPE_OFF_PIC == content.m_nType) {
				if (!Utils.isEmptyStr(content.m_CFaceInfo.m_strName)) {
					strMsg += "/c[\"";
					strMsg += content.m_CFaceInfo.m_strName;
					strMsg += "\"]";
				}
			}
		}

		return strMsg;
	}
	
	private static int handleFontInfo(String strMsg, 
			int nPos, List<Content> arrContent) {
		String strTemp = Utils.getBetweenString(strMsg, nPos+2, "[\"", "\"]");
		if (!Utils.isEmptyStr(strTemp)) {
			String[] str = strTemp.split(",");
			if (str.length != 6)
				return -1;
			
			Content content = new Content();
			content.m_nType = ContentType.CONTENT_TYPE_FONT_INFO;
			content.m_FontInfo.m_strName = str[0];
			content.m_FontInfo.m_nSize = Integer.parseInt(str[1]);
			content.m_FontInfo.m_clrText = Utils.HexStrToRGB(str[2]);
			content.m_FontInfo.m_bBold = str[3].endsWith("0") ? false : true;
			content.m_FontInfo.m_bItalic = str[4].endsWith("0") ? false : true;
			content.m_FontInfo.m_bUnderLine = str[5].endsWith("0") ? false : true;
			arrContent.add(content);

			nPos = strMsg.indexOf("\"]", nPos+2);
			if (nPos != -1)
				return nPos + 1;
		}
		return -1;
	}
	
	private static int handleSysFaceId(String strMsg, 
			int nPos, List<Content> arrContent) {
		int nFaceId = Utils.getBetweenInt(strMsg, nPos, "[\"", "\"]", -1);
		if (nFaceId != -1) {
			Content content = new Content();
			content.m_nType = ContentType.CONTENT_TYPE_FACE;
			content.m_nFaceId = nFaceId;
			arrContent.add(content);

			nPos = strMsg.indexOf("\"]", nPos+2);
			if (nPos != -1)
				return nPos + 1;
		}
		return -1;
	}
	
	private static int handleCustomPic(String strMsg, 
			int nPos, List<Content> arrContent) {
		String strFileName = Utils.getBetweenString(strMsg, nPos, "[\"", "\"]");
		if (!Utils.isEmptyStr(strFileName)) {
			Content content = new Content();
			content.m_nType = ContentType.CONTENT_TYPE_CUSTOM_FACE;
			content.m_CFaceInfo.m_strName = strFileName;
			arrContent.add(content);

			nPos = strMsg.indexOf("\"]", nPos+2);
			if (nPos != -1)
				return nPos + 1;
		}
		return -1;
	}

	public static boolean createMsgContent(String strMsg, 
			List<Content> arrContent) {
		String strText = "";

		if (Utils.isEmptyStr(strMsg))
			return false;

		for (int i = 0; i < strMsg.length(); i++) {
			char ch = strMsg.charAt(i);
			if (ch == '/') {
				if (i+1 >= strMsg.length()) {
					strText += ch;
					break;
				}
			
				char ch2 = strMsg.charAt(i+1);
				
				if (ch2 == '/') {
					strText += ch;
					i++;
					continue;
				}
				else {
					if (!Utils.isEmptyStr(strText)) {
						Content content = new Content();
						content.m_nType = ContentType.CONTENT_TYPE_TEXT;
						content.m_strText = strText;
						arrContent.add(content);
						strText = "";
					}
					
					if (ch2 == 'o') {
						int nPos = handleFontInfo(strMsg, i, arrContent);
						if (nPos != -1) {
							i = nPos;
							continue;
						}
					}
					else if (ch2 == 'f') {
						int nPos = handleSysFaceId(strMsg, i, arrContent);
						if (nPos != -1) {
							i = nPos;
							continue;
						}
					}
					else if (ch2 == 'c') {
						int nPos = handleCustomPic(strMsg, i, arrContent);
						if (nPos != -1) {
							i = nPos;
							continue;
						}
					}
				}
			}
			strText += ch;
		}

		if (!Utils.isEmptyStr(strText)) {
			Content content = new Content();
			content.m_nType = ContentType.CONTENT_TYPE_TEXT;
			content.m_strText = strText;
			arrContent.add(content);
			strText = "";
		}

		return true;
	}
	
	public static BuddyMessage createBuddyMessage(BuddyMsgLog msgLog) {
		if (null == msgLog)
			return null;
		BuddyMessage buddyMsg = new BuddyMessage();
		createMsgContent(msgLog.m_strContent, buddyMsg.m_arrContent);
		buddyMsg.m_nTime = msgLog.m_nTime;
		return buddyMsg;
	}
	
	public static GroupMessage createGroupMessage(GroupMsgLog msgLog) {
		if (null == msgLog)
			return null;
		GroupMessage groupMsg = new GroupMessage();
		createMsgContent(msgLog.m_strContent, groupMsg.m_arrContent);
		groupMsg.m_nTime = msgLog.m_nTime;
		return groupMsg;
	}
	
	public static SessMessage createSessMessage(SessMsgLog msgLog) {
		if (null == msgLog)
			return null;
		SessMessage sessMsg = new SessMessage();
		createMsgContent(msgLog.m_strContent, sessMsg.m_arrContent);
		sessMsg.m_nTime = msgLog.m_nTime;
		return sessMsg;
	}
	
	public static BuddyMessage createBuddyMessage(
			int nTime, String strMsg) {
		BuddyMessage buddyMsg = new BuddyMessage();
		buddyMsg.m_nTime = nTime;
		createMsgContent(strMsg, buddyMsg.m_arrContent);
		return buddyMsg;
	}
	
	public static GroupMessage createGroupMessage(
			int nTime, String strMsg) {
		GroupMessage groupMsg = new GroupMessage();
		groupMsg.m_nTime = nTime;
		createMsgContent(strMsg, groupMsg.m_arrContent);
		return groupMsg;
	}
	
	public static SessMessage createSessMessage(
			int nTime, String strMsg) {
		SessMessage sessMsg = new SessMessage();
		sessMsg.m_nTime = nTime;
		createMsgContent(strMsg, sessMsg.m_arrContent);
		return sessMsg;
	}	
}
