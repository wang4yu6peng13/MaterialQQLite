package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class KickMessage {
	public int m_nMsgId;
	public int m_nMsgId2;
	public int m_nFromUin;
	public int m_nToUin;
	public int m_nMsgType;
	public int m_nReplyIp;
	public boolean m_bShowReason;	// 是否显示被踢下线原因
	public String m_strReason;		// 被踢下线原因
		
		
	public static boolean isType(String strType) {
		return strType.equals("kick_message");
	}
	
	public void reset() {
		m_nMsgId = 0;
		m_nMsgId2 = 0;
		m_nFromUin = 0;
		m_nToUin = 0;
		m_nMsgType = 0;
		m_nReplyIp = 0;
		m_bShowReason = false;
		m_strReason = "";
	}
	
	public boolean parse(JSONObject json) {
		try {
			reset();
			
			m_nMsgId = json.optInt("msg_id");
			m_nFromUin = json.optInt("from_uin");
			m_nToUin = json.optInt("to_uin");
			m_nMsgId2 = json.optInt("msg_id2");
			m_nMsgType = json.optInt("msg_type");
			m_nReplyIp = json.optInt("reply_ip");
			m_bShowReason = (json.optInt("show_reason") != 0);
			m_strReason = json.optString("reason");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
