package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.LinkedList;

import com.wyp.materialqqlite.Utils;

public class MessageList {
	private LinkedList<MessageSender> m_lnkMsgSender = new LinkedList<MessageSender>();
	
	public void reset() {
		m_lnkMsgSender.clear();
	}
	
	public boolean addMsgSender(MessageSender msgSender) {
		if (null == msgSender)
			return false;

		if (MessageSender.BUDDY == msgSender.m_nType) {
			if (getBuddyMsgSender(msgSender.m_nQQUin) != null)
				return false;
		} else if (MessageSender.GROUP == msgSender.m_nType) {
			if (getGroupMsgSender(msgSender.m_nGroupCode) != null)
				return false;
		} else if (MessageSender.SESS == msgSender.m_nType) {
			if (getSessMsgSender(msgSender.m_nGroupCode, msgSender.m_nQQUin) != null)
				return false;
		} else if (MessageSender.SYSTEM == msgSender.m_nType) {
			if (getSystemMsgSender() != null)
				return false;
		}
		
		m_lnkMsgSender.add(msgSender);
		return true;
	}

	// 获取消息发送者总数
	public int getMsgSenderCount() {
		return m_lnkMsgSender.size();
	}
	
	// 获取消息发送者(根据索引)
	public MessageSender getMsgSender(int nIndex) {
		if (nIndex >= 0 && nIndex < m_lnkMsgSender.size())
			return m_lnkMsgSender.get(nIndex);
		else
			return null;
	}
	
	public MessageSender getBuddyMsgSender(int nQQUin) {
		for (int i = 0; i < m_lnkMsgSender.size(); i++) {
			MessageSender msgSender = m_lnkMsgSender.get(i);
			if (msgSender != null && 
					MessageSender.BUDDY == msgSender.m_nType &&
					nQQUin == msgSender.m_nQQUin) {
				return msgSender;
			}
		}
		return null;		
	}
	
	public MessageSender getGroupMsgSender(int nGroupCode) {
		for (int i = 0; i < m_lnkMsgSender.size(); i++) {
			MessageSender msgSender = m_lnkMsgSender.get(i);
			if (msgSender != null && 
					MessageSender.GROUP == msgSender.m_nType &&
					nGroupCode == msgSender.m_nGroupCode) {
				return msgSender;
			}
		}
		return null;		
	}
	
	public MessageSender getSessMsgSender(int nGroupCode, int nQQUin) {
		for (int i = 0; i < m_lnkMsgSender.size(); i++) {
			MessageSender msgSender = m_lnkMsgSender.get(i);
			if (msgSender != null && 
					MessageSender.SESS == msgSender.m_nType &&
					nGroupCode == msgSender.m_nGroupCode &&
					nQQUin == msgSender.m_nQQUin) {
				return msgSender;
			}
		}
		return null;		
	}

	public MessageSender getSystemMsgSender() {
		for (int i = 0; i < m_lnkMsgSender.size(); i++) {
			MessageSender msgSender = m_lnkMsgSender.get(i);
			if (msgSender != null && 
					MessageSender.SYSTEM == msgSender.m_nType) {
				return msgSender;
			}
		}
		return null;		
	}
	
	// 获取未读消息总数
	public int getUnreadMsgCount() {
		int nUnreadMsgCnt = 0;
		for (int i = 0; i < m_lnkMsgSender.size(); i++) {
			MessageSender msgSender = m_lnkMsgSender.get(i);
			if (msgSender != null) {
				nUnreadMsgCnt += msgSender.m_nUnreadMsgCnt;
			}
		}
		return nUnreadMsgCnt;
	}
	
	public boolean addBuddyMsg(int nQQUin, 
			String strName, BuddyMessage buddyMsg) {
		MessageSender msgSender = getBuddyMsgSender(nQQUin);
		if (null == msgSender) {
			msgSender = new MessageSender();
			msgSender.m_nType = MessageSender.BUDDY;
			msgSender.m_nGroupCode = 0;
			msgSender.m_nQQUin = nQQUin;
			msgSender.m_strName = strName;
			msgSender.m_nUnreadMsgCnt = 0;
		} else {
			m_lnkMsgSender.remove(msgSender);
		}

		msgSender.m_objLastMsg = buddyMsg;
		msgSender.m_nUnreadMsgCnt++;
		m_lnkMsgSender.add(0, msgSender);
		return true;
	}
	
	public boolean addGroupMsg(int nGroupCode, 
			String strName, GroupMessage groupMsg) {
		MessageSender msgSender = getGroupMsgSender(nGroupCode);
		if (null == msgSender) {
			msgSender = new MessageSender();
			msgSender.m_nType = MessageSender.GROUP;
			msgSender.m_nGroupCode = nGroupCode;
			msgSender.m_nQQUin = 0;
			msgSender.m_strName = strName;
			msgSender.m_nUnreadMsgCnt = 0;
		} else {
			m_lnkMsgSender.remove(msgSender);
		}
		
		msgSender.m_objLastMsg = groupMsg;
		msgSender.m_nUnreadMsgCnt++;
		m_lnkMsgSender.add(0, msgSender);
		return true;
	}
	
	public boolean addSessMsg(int nGroupCode, 
			int nQQUin, String strName, SessMessage sessMsg) {
		MessageSender msgSender = getSessMsgSender(nGroupCode, nQQUin);
		if (null == msgSender) {
			msgSender = new MessageSender();
			msgSender.m_nType = MessageSender.SESS;
			msgSender.m_nGroupCode = nGroupCode;
			msgSender.m_nQQUin = nQQUin;
			msgSender.m_strName = strName;
			msgSender.m_nUnreadMsgCnt = 0;
		} else {
			m_lnkMsgSender.remove(msgSender);
		}
		
		msgSender.m_objLastMsg = sessMsg;
		msgSender.m_nUnreadMsgCnt++;
		m_lnkMsgSender.add(0, msgSender);
		return true;
	}
	
	public boolean addSystemMsg(
			int nGroupCode, SysGroupMessage sysGroupMsg) {
		MessageSender msgSender = getSystemMsgSender();
		if (null == msgSender) {
			msgSender = new MessageSender();
			msgSender.m_nType = MessageSender.SYSTEM;
			msgSender.m_nGroupCode = nGroupCode;
			msgSender.m_nQQUin = 0;
			msgSender.m_strName = "验证消息";
			msgSender.m_nUnreadMsgCnt = 0;
		} else {
			m_lnkMsgSender.remove(msgSender);
		}

		msgSender.m_objLastMsg = sysGroupMsg;
		msgSender.m_nUnreadMsgCnt++;
		m_lnkMsgSender.add(0, msgSender);
		return true;
	}
	
	// 清空指定好友未读消息记数
	public void emptyBuddyUnreadMsgCount(int nQQUin) {
		MessageSender msgSender = getBuddyMsgSender(nQQUin);
		if (msgSender != null)
			msgSender.m_nUnreadMsgCnt = 0;
	}
	
	// 清空指定群未读消息记数
	public void emptyGroupUnreadMsgCount(int nGroupCode) {
		MessageSender msgSender = getGroupMsgSender(nGroupCode);
		if (msgSender != null)
			msgSender.m_nUnreadMsgCnt = 0;
	}
	
	// 清空指定群成员未读消息记数
	public void emptySessUnreadMsgCount(int nGroupCode, int nQQUin) {
		MessageSender msgSender = getSessMsgSender(nGroupCode, nQQUin);
		if (msgSender != null)
			msgSender.m_nUnreadMsgCnt = 0;
	}
	
	// 清空未读系统消息记数
	public void emptySystemUnreadMsgCount() {
		MessageSender msgSender = getSystemMsgSender();
		if (msgSender != null)
			msgSender.m_nUnreadMsgCnt = 0;
	}
}
