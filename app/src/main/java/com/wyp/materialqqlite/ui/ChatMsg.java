package com.wyp.materialqqlite.ui;

import java.util.List;

import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;

public class ChatMsg {
	public static final int TYPE_COUNT = 4;
	public static final int LEFT_B = 0;
	public static final int LEFT_G = 1;
	public static final int RIGHT = 2;
	public static final int TIME = 3;
	
	public int m_nType;				// 聊天消息类型
	public int m_nQQUin;			// 好友Uin
	public int m_nBubble;			// 气泡索引
	public int m_nColor;			// 文字颜色
	public int m_nLinkColor;		// 超链接颜色
	public int m_nMsgLogId;			// 消息记录ID
	public String m_strName;		// 好友名称
	public List<Content> m_arrContent;	// 消息内容
	public int m_nTime;				// 消息发送时间
}
