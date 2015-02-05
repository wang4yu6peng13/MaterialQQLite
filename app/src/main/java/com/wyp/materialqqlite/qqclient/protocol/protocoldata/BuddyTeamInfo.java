package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

public class BuddyTeamInfo {
	public int m_nIndex;		// 索引
	public int m_nSort;			// 排列顺序
	public String m_strName;	// 分组名称
	public ArrayList<BuddyInfo> m_arrBuddyInfo = new ArrayList<BuddyInfo>();
	
	public void reset() {
		m_nIndex = 0;
		m_nSort = 0;
		m_strName = "";
		m_arrBuddyInfo.clear();
	}
	
	public int getBuddyCount() {
		return m_arrBuddyInfo.size();
	}
	
	public int getOnlineBuddyCount() {
		int nCount = 0;
		for (int i = 0; i < m_arrBuddyInfo.size(); i++)
		{
			BuddyInfo buddyInfo = m_arrBuddyInfo.get(i);
			if (buddyInfo != null && buddyInfo.m_nStatus != QQStatus.OFFLINE)
				nCount++;
		}
		return nCount;
	}
	
	public BuddyInfo getBuddy(int nIndex) {
		if (nIndex >= 0 && nIndex < m_arrBuddyInfo.size())
			return m_arrBuddyInfo.get(nIndex);
		else
			return null;
	}
	
	public void sort() {
		BuddyInfo buddyInfo1, buddyInfo2;
		boolean bExchange;
		int nCount;

		nCount = m_arrBuddyInfo.size();
		for (int i = 0; i < nCount-1; i++)
		{
			bExchange = false;
			for (int j = nCount-1; j > i; j--)
			{
				buddyInfo1 = m_arrBuddyInfo.get(j-1);
				buddyInfo2 = m_arrBuddyInfo.get(j);
				if (buddyInfo1 != null && buddyInfo2 != null)
				{
					if (buddyInfo2.m_nStatus < buddyInfo1.m_nStatus)
					{
						m_arrBuddyInfo.set(j-1, buddyInfo2);
						m_arrBuddyInfo.set(j, buddyInfo1);
						bExchange = true;
					}
				}
			}
			if (!bExchange)
				break;
		}
	}
}
