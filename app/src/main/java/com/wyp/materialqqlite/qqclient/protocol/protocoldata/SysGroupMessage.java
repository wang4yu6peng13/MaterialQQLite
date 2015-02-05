package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class SysGroupMessage {
	public int m_nMsgId;
	public int m_nMsgId2;
	public int m_nFromUin;
	public int m_nToUin;
	public int m_nMsgType;
	public int m_nReplyIp;
	public String m_strSubType;
	public int m_nGroupCode;
	public int m_nGroupNumber;
	public int m_nAdminUin;
	public String m_strMsg;
	public int m_nOpType;
	public int m_nOldMember;
	public String m_strOldMember;
	public String m_strAdminUin;
	public String m_strAdminNickName;
	public int m_nTime;
	
	public static boolean isType(String strType) {
		return strType.equals("sys_g_msg");
	}
	
	void reset() {
		m_nMsgId = 0;
		m_nMsgId2 = 0;
		m_nFromUin = 0;
		m_nToUin = 0;
		m_nMsgType = 0;
		m_nReplyIp = 0;
		m_strSubType = "";
		m_nGroupCode = 0;
		m_nGroupNumber = 0;
		m_nAdminUin = 0;
		m_strMsg = "";
		m_nOpType = 0;
		m_nOldMember = 0;
		m_strOldMember = "";
		m_strAdminUin = "";
		m_strAdminNickName = "";
		m_nTime = 0;
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
			m_strSubType = json.optString("type");
			m_nTime = (int)System.currentTimeMillis() / 1000;

			if (m_strSubType.equals("group_request_join_agree")	// 加群同意通知或拒绝加群通知	
					|| m_strSubType.equals("group_request_join_deny")) {
				m_nGroupCode = json.optInt("gcode");
				m_nGroupNumber = json.optInt("t_gcode");
				m_nAdminUin = json.optInt("admin_uin");
				m_strMsg = json.optString("msg");
				return true;
			} else if (m_strSubType.equals("group_leave")) { // 被移出群通知
				m_nGroupCode = json.optInt("gcode");
				m_nGroupNumber = json.optInt("t_gcode");
				m_nOpType = json.optInt("op_type");
				m_nOldMember = json.optInt("old_member");
				m_strOldMember = json.optString("t_old_member");
				m_nAdminUin = json.optInt("admin_uin");
				m_strAdminUin = json.optString("t_admin_uin");
				m_strAdminNickName = json.optString("admin_nick");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
