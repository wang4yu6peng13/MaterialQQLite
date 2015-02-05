package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class OnlineBuddyInfo {
	public int m_nUin;
	public int m_nStatus;		// 在线状态
	public int m_nClientType;	// 客户端类型
	
	public void reset() {
		m_nUin = 0;
		m_nStatus = QQStatus.OFFLINE;
		m_nClientType = 0;
	}
}
