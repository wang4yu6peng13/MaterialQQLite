package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wyp.materialqqlite.Utils;

public class GroupMessage {
	public int m_nMsgId;
	public int m_nMsgId2;
	public int m_nFromUin;
	public int m_nToUin;
	public int m_nMsgType;
	public int m_nReplyIp;
	public int m_nGroupCode;
	public int m_nSendUin;
	public int m_nSeq;
	public int m_nInfoSeq;
	public int m_nTime;
	public ArrayList<Content> m_arrContent = new ArrayList<Content>();
	
	public static boolean isType(String strType) {
		return strType.equals("group_message");
	}
	
	public void reset() {
		m_nMsgId = 0;
		m_nMsgId2 = 0;
		m_nFromUin = 0;
		m_nToUin = 0;
		m_nMsgType = 0;
		m_nReplyIp = 0;
		m_nGroupCode = 0;
		m_nSendUin = 0;
		m_nSeq = 0;
		m_nInfoSeq = 0;
		m_nTime = 0;
		m_arrContent.clear();
	}
	
	public boolean parse(JSONObject json) {
		try {
			reset();
			
			m_nMsgId = json.optInt("msg_id");
			m_nMsgId2 = json.optInt("msg_id2");
			m_nFromUin = json.optInt("from_uin");
			m_nToUin = json.optInt("to_uin");
			m_nMsgType = json.optInt("msg_type");
			m_nReplyIp = json.optInt("reply_ip");
			m_nTime = json.optInt("time");
			m_nGroupCode = json.optInt("group_code");
			m_nSendUin = json.optInt("send_uin");
			m_nSeq = json.optInt("seq");
			m_nInfoSeq = json.optInt("info_seq");

			JSONArray json2 = json.optJSONArray("content");
			for (int i = 0; i < json2.length(); ++i)
			{
				Object o = json2.opt(i);
				if (o instanceof String) {
					String strText = (String)o;
					if (i == json2.length() - 1)					// 去除消息最后多余的一个空格
					{
						int nCount2 = strText.length();
						if (nCount2 > 0 && (strText.charAt(nCount2 - 1) == ' '))
							strText = strText.substring(0, nCount2 - 1);
					}

					strText.replaceAll("\r", "\r\n");

					Content content = new Content();
					content.m_nType = ContentType.CONTENT_TYPE_TEXT;
					content.m_strText = strText;
					m_arrContent.add(content);
				} else if (o instanceof JSONArray) {
					JSONArray json3 = (JSONArray)o;
					
					String strValue = json3.optString(0);	// 内容类型
					if (strValue.equals("font"))			// 字体信息
					{
						Content content = new Content();
						content.m_nType = ContentType.CONTENT_TYPE_FONT_INFO;

						JSONObject json4 = (JSONObject)json3.optJSONObject(1);
						content.m_FontInfo.m_nSize = json4.optInt("size");	// 字体大小
						content.m_FontInfo.m_clrText = Utils.HexStrToRGB(json4.optString("color"));	// 字体颜色

						content.m_FontInfo.m_bBold = (json4.optJSONArray("style").optInt(0) != 0);		// 字体风格(加粗)
						content.m_FontInfo.m_bItalic = (json4.optJSONArray("style").optInt(1) != 0);	// 字体风格(倾斜)
						content.m_FontInfo.m_bUnderLine = (json4.optJSONArray("style").optInt(2) != 0);	// 字体风格(下划线)
						
						content.m_FontInfo.m_strName = json4.optString("name");	// 字体名称
						
						m_arrContent.add(content);
					}
					else if (strValue.equals("face"))	// 系统表情
					{
						Content content = new Content();
						content.m_nType = ContentType.CONTENT_TYPE_FACE;
						content.m_nFaceId = json3.optInt(1);
						m_arrContent.add(content);
					}
					else if (strValue.equals("cface"))	// 自定义表情
					{
						Content content = new Content();
						content.m_nType = ContentType.CONTENT_TYPE_CUSTOM_FACE;
						JSONObject json4 = json3.optJSONObject(1);
						content.m_CFaceInfo.m_strName = json4.optString("name");
						content.m_CFaceInfo.m_nFileId = json4.optInt("file_id");
						content.m_CFaceInfo.m_strKey = json4.optString("key");
						content.m_CFaceInfo.m_strServer = json4.optString("server");
						m_arrContent.add(content);
					}
					else if (strValue.equals("cface_idx"))	// 未知
					{
						
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
