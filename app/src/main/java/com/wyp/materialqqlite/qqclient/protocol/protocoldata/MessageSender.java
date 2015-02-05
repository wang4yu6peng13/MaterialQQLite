package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class MessageSender {
	public static final int BUDDY = 0;
	public static final int GROUP = 1;
	public static final int SESS = 2;
	public static final int SYSTEM = 3;
	
	public int m_nType;				// 消息发送者类型
	public int m_nGroupCode;		// 群代码
	public int m_nQQUin;			// 好友Uin
	public String m_strName;		// 好友昵称或群名
	public Object m_objLastMsg;		// 最后一条消息
	public int m_nUnreadMsgCnt;		// 未读消息数
}
