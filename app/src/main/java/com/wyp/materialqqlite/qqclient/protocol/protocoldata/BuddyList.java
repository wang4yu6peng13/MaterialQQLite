package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

public class BuddyList {
	public ArrayList<BuddyTeamInfo> m_arrBuddyTeamInfo = new ArrayList<BuddyTeamInfo>();
	
	public void reset() {
		m_arrBuddyTeamInfo.clear();
	}
	
	public int getBuddyTeamCount() {
		return m_arrBuddyTeamInfo.size();
	}
	
	public BuddyTeamInfo getBuddyTeam(int nTeamIndex) {
		if (nTeamIndex >= 0 && nTeamIndex < m_arrBuddyTeamInfo.size())
			return m_arrBuddyTeamInfo.get(nTeamIndex);
		else
			return null;
	}
	
	public BuddyTeamInfo getBuddyTeamByIndex(int nIndex) {
		for (int i = 0; i < m_arrBuddyTeamInfo.size(); i++)
		{
			BuddyTeamInfo buddyTeamInfo = m_arrBuddyTeamInfo.get(i);
			if (buddyTeamInfo != null && nIndex == buddyTeamInfo.m_nIndex)
				return buddyTeamInfo;
		}
		return null;
	}
	
	public int getBuddyCount(int nTeamIndex) {
		BuddyTeamInfo buddyTeamInfo = getBuddyTeam(nTeamIndex);
		if (buddyTeamInfo != null)
			return buddyTeamInfo.getBuddyCount();
		else
			return 0;
	}
	
	public int getOnlineBuddyCount(int nTeamIndex) {
		BuddyTeamInfo buddyTeamInfo = getBuddyTeam(nTeamIndex);
		if (buddyTeamInfo != null)
			return buddyTeamInfo.getOnlineBuddyCount();
		else
			return 0;
	}
	
	public BuddyInfo getBuddy(int nTeamIndex, int nIndex) {
		BuddyTeamInfo buddyTeamInfo = getBuddyTeam(nTeamIndex);
		if (buddyTeamInfo != null)
			return buddyTeamInfo.getBuddy(nIndex);
		else
			return null;
	}
	
	public BuddyInfo getBuddy(int nQQUin) {
		for (int i = 0; i < m_arrBuddyTeamInfo.size(); i++)
		{
			BuddyTeamInfo buddyTeamInfo = m_arrBuddyTeamInfo.get(i);
			if (buddyTeamInfo != null)
			{
				for (int j = 0; j < buddyTeamInfo.m_arrBuddyInfo.size(); j++)
				{
					BuddyInfo buddyInfo = buddyTeamInfo.m_arrBuddyInfo.get(j);
					if (buddyInfo != null && buddyInfo.m_nQQUin == nQQUin)
						return buddyInfo;
				}
			}
		}
		return null;
	}
	
	public boolean setOnlineBuddyList(OnlineBuddyListResult result) {
		if (null == result)
			return false;

		for (int i = 0; i < result.m_arrOnlineBuddyInfo.size(); i++)
		{
			OnlineBuddyInfo onlineBuddyInfo = result.m_arrOnlineBuddyInfo.get(i);
			if (onlineBuddyInfo != null)
			{
				BuddyInfo buddyInfo = getBuddy(onlineBuddyInfo.m_nUin);
				if (buddyInfo != null)
				{
					buddyInfo.m_nClientType = onlineBuddyInfo.m_nClientType;
					buddyInfo.m_nStatus = onlineBuddyInfo.m_nStatus;
				}
			}
		}
		return true;
	}
	
	public void sortBuddyTeam() {
		BuddyTeamInfo buddyTeamInfo1, buddyTeamInfo2;
		boolean bExchange;
		int nCount;

		nCount = m_arrBuddyTeamInfo.size();
		for (int i = 0; i < nCount-1; i++)
		{
			bExchange = false;
			for (int j = nCount-1; j > i; j--)
			{
				buddyTeamInfo1 = m_arrBuddyTeamInfo.get(j-1);
				buddyTeamInfo2 = m_arrBuddyTeamInfo.get(j);
				if (buddyTeamInfo1 != null && buddyTeamInfo2 != null)
				{
					if (buddyTeamInfo2.m_nSort < buddyTeamInfo1.m_nSort)
					{
						m_arrBuddyTeamInfo.set(j-1, buddyTeamInfo2);
						m_arrBuddyTeamInfo.set(j, buddyTeamInfo1);
						bExchange = true;
					}
				}
			}
			if (!bExchange)
				break;
		}
	}
	
	public void sortBuddy() {
		for (int i = 0; i < m_arrBuddyTeamInfo.size(); i++)
		{
			BuddyTeamInfo buddyTeamInfo = m_arrBuddyTeamInfo.get(i);
			if (buddyTeamInfo != null)
				buddyTeamInfo.sort();
		}
	}
	
	public boolean addBuddyTeam(BuddyTeamInfo buddyTeamInfo) {
		if (null == buddyTeamInfo)
			return false;
		m_arrBuddyTeamInfo.add(buddyTeamInfo);
		return true;
	}
}
