package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

public class GroupInfo {
	public int m_nGroupCode;		// 群代码
	public int m_nGroupId;			// 群ID
	public int m_nGroupNumber;		// 群号码
	public String m_strName;		// 群名称
	public String m_strMemo;		// 群公告
	public String m_strFingerMemo;	// 群简介
	public int m_nOwnerUin;			// 群拥有者Uin
	public int m_nCreateTime;		// 群创建时间
	public int m_nFace;				// 群头像
	public int m_nLevel;			// 群等级
	public int m_nClass;			// 群分类索引
	public int m_nOption;
	public int m_nFlag;
	public boolean m_bHasGroupNumber;
	public boolean m_bHasGroupInfo;
	public ArrayList<BuddyInfo> m_arrMember = new ArrayList<BuddyInfo>();	// 群成员
	
	public void reset() {
		m_nGroupCode = 0;
		m_nGroupId = 0;
		m_nGroupNumber = 0;
		m_strName = "";
		m_strMemo = "";
		m_strFingerMemo = "";
		m_nOwnerUin = 0;
		m_nCreateTime = 0;
		m_nFace = 0;
		m_nLevel = 0;
		m_nClass = 0;
		m_nOption = 0;
		m_nFlag = 0;
		m_bHasGroupNumber = m_bHasGroupInfo = false;

		delAllMember();
	}
	
	// 获取群成员总人数
	public int getMemberCount() {
		return m_arrMember.size();
	}
	
	// 获取群成员在线人数
	public int getOnlineMemberCount() {
		int nCount = 0;
		for (int i = 0; i < m_arrMember.size(); i++)
		{
			BuddyInfo buddyInfo = m_arrMember.get(i);
			if (buddyInfo != null && buddyInfo.m_nStatus != QQStatus.OFFLINE)
				nCount++;
		}
		return nCount;
	}
	
	// 根据索引获取群成员信息
	public BuddyInfo getMember(int nIndex) {
		if (nIndex < 0 || nIndex >= m_arrMember.size())
			return null;

		return m_arrMember.get(nIndex);
	}
	
	// 根据QQUin获取群成员信息
	public BuddyInfo getMemberByUin(int nQQUin) {
		for (int i = 0; i < m_arrMember.size(); i++)
		{
			BuddyInfo buddyInfo = m_arrMember.get(i);
			if (buddyInfo != null && buddyInfo.m_nQQUin == nQQUin)
				return buddyInfo;
		}
		return null;
	}
	
	// 根据QQNum获取群成员信息
	public BuddyInfo getMemberByNum(int nQQNum) {
		for (int i = 0; i < m_arrMember.size(); i++)
		{
			BuddyInfo buddyInfo = m_arrMember.get(i);
			if (buddyInfo != null && buddyInfo.m_nQQNum == nQQNum)
				return buddyInfo;
		}
		return null;
	}
	
	// 对群成员列表按在线状态进行排序
	public void sort() {
		BuddyInfo buddyInfo1, buddyInfo2;
		boolean bExchange;
		int nCount;

		nCount = m_arrMember.size();
		for (int i = 0; i < nCount-1; i++)
		{
			bExchange = false;
			for (int j = nCount-1; j > i; j--)
			{
				buddyInfo1 = m_arrMember.get(j-1);
				buddyInfo2 = m_arrMember.get(j);
				if (buddyInfo1 != null && buddyInfo2 != null)
				{
					if (buddyInfo2.m_nStatus < buddyInfo1.m_nStatus)
					{
						m_arrMember.set(j-1, buddyInfo2);
						m_arrMember.set(j, buddyInfo1);
						bExchange = true;
					}
				}
			}
			if (!bExchange)
				break;
		}
	}
	
	// 添加群成员
	public boolean addMember(BuddyInfo buddyInfo) {
		if (null == buddyInfo)
			return false;
		m_arrMember.add(buddyInfo);
		return true;
	}
	
	// 删除所有群成员
	public void delAllMember() {
		m_arrMember.clear();
	}
	
	// 设置群号码
	public void setGroupNumber(GetQQNumResult result) {
		if (result != null)
		{
			m_nGroupNumber = result.m_nQQNum;
			m_bHasGroupNumber = true;
		}
	}
	
	// 设置群信息
	public boolean setGroupInfo(GroupInfoResult result) {
		if (null == result)
			return false;

		m_nGroupCode = result.m_nGroupCode;
		m_nGroupId = result.m_nGroupId;
		m_strName = result.m_strName;
		m_strMemo = result.m_strMemo;
		m_strFingerMemo = result.m_strFingerMemo;
		m_nOwnerUin = result.m_nOwnerUin;
		m_nCreateTime = result.m_nCreateTime;
		m_nFace = result.m_nFace;
		m_nLevel = result.m_nLevel;
		m_nClass = result.m_nClass;
		m_nOption = result.m_nOption;
		m_nFlag = result.m_nFlag;

		delAllMember();
		for (int i = 0; i < result.m_arrMember.size(); i++) {
			BuddyInfo buddyInfo = result.m_arrMember.get(i);
			if (buddyInfo != null)
				m_arrMember.add(buddyInfo);
		}
		result.m_arrMember.clear();
		sort();
		m_bHasGroupInfo = true;

		return true;
	}
	
	public boolean isHasGroupNumber() {
		return m_bHasGroupNumber;
	}
	
	public boolean isHasGroupInfo() {
		return m_bHasGroupInfo;
	}
}
