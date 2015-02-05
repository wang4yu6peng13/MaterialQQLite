package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

public class GroupList {
	public ArrayList<GroupInfo> m_arrGroupInfo = new ArrayList<GroupInfo>();
	
	public void reset() {
		m_arrGroupInfo.clear();
	}
	
	// 获取群总数
	public int getGroupCount() {
		return m_arrGroupInfo.size();
	}
	
	// 获取群信息(根据索引)
	public GroupInfo getGroup(int nIndex) {
		if (nIndex >= 0 && nIndex < m_arrGroupInfo.size())
			return m_arrGroupInfo.get(nIndex);
		else
			return null;
	}
	
	// 获取群信息(根据群代码)
	public GroupInfo getGroupByCode(int nGroupCode) {
		for (int i = 0; i < m_arrGroupInfo.size(); i++)
		{
			GroupInfo groupInfo = m_arrGroupInfo.get(i);
			if (groupInfo != null && groupInfo.m_nGroupCode == nGroupCode)
				return groupInfo;
		}
		return null;
	}
	
	// 获取群信息(根据群Id)
	public GroupInfo getGroupById(int nGroupId) {
		for (int i = 0; i < m_arrGroupInfo.size(); i++)
		{
			GroupInfo groupInfo = m_arrGroupInfo.get(i);
			if (groupInfo != null && groupInfo.m_nGroupId == nGroupId)
				return groupInfo;
		}
		return null;
	}
	
	// 获取群索引(根据群代码)
	public int getGroupIndexByCode(int nGroupCode) {
		for (int i = 0; i < m_arrGroupInfo.size(); i++) {
			GroupInfo groupInfo = m_arrGroupInfo.get(i);
			if (groupInfo != null && groupInfo.m_nGroupCode == nGroupCode)
				return i;
		}
		return -1;
	}
	
	// 根据群代码和群成员QQUin获取群成员信息
	public BuddyInfo getGroupMemberByCode(int nGroupCode, int nQQUin) {
		GroupInfo groupInfo = getGroupByCode(nGroupCode);
		if (groupInfo != null)
			return groupInfo.getMemberByUin(nQQUin);
		else
			return null;
	}
	
	public BuddyInfo getGroupMemberByNum(int nGroupCode, int nQQNum) {
		GroupInfo groupInfo = getGroupByCode(nGroupCode);
		if (groupInfo != null)
			return groupInfo.getMemberByNum(nQQNum);
		else
			return null;
	}
	
	// 根据群Id和群成员QQUin获取群成员信息
	public BuddyInfo getGroupMemberById(int nGroupId, int nQQUin) {
		GroupInfo groupInfo = getGroupById(nGroupId);
		if (groupInfo != null)
			return groupInfo.getMemberByUin(nQQUin);
		else
			return null;
	}
	
	// 添加群
	public boolean addGroup(GroupInfo groupInfo) {
		if (null == groupInfo)
			return false;
		m_arrGroupInfo.add(groupInfo);
		return true;
	}
	
	// 由群Id获取群代码
	public int getGroupCodeById(int nGroupId) {
		GroupInfo groupInfo = getGroupById(nGroupId);
		return ((groupInfo != null) ? groupInfo.m_nGroupCode : 0);
	}
	
	// 由群代码获取群Id
	public int getGroupIdByCode(int nGroupCode) {
		GroupInfo groupInfo = getGroupByCode(nGroupCode);
		return ((groupInfo != null) ? groupInfo.m_nGroupId : 0);
	}
}
