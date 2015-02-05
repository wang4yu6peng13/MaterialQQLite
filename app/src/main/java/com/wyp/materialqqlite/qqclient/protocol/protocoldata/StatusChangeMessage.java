package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class StatusChangeMessage {
	public int m_nQQUin;
	public int m_nStatus;		// 在线状态
	public int m_nClientType;	// 客户端类型
	
	public static boolean isType(String strType) {
		return strType.equals("buddies_status_change");
	}
	
	public void reset() {
		m_nQQUin = 0;
		m_nStatus = QQStatus.OFFLINE;
		m_nClientType = 0;
	}
	
	public 	boolean parse(JSONObject json) {
		try {
			reset();
			
			m_nQQUin = json.optInt("uin");
			m_nStatus = QQStatus.convertToQQStatus(json.getString("status"));
			m_nClientType = json.optInt("client_type");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
