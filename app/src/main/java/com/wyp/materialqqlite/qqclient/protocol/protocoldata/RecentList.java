package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

public class RecentList {
	public int m_nRetCode;
	public ArrayList<RecentInfo> m_arrRecentInfo = new ArrayList<RecentInfo>();
	
	public void reset() {
		m_nRetCode = 0;
		m_arrRecentInfo.clear();
	}
	
	public boolean addRecent(RecentInfo recentInfo) {
		if (null == recentInfo)
			return false;
		m_arrRecentInfo.add(recentInfo);
		return true;
	}
	
	public int getRecentCount() {
		return m_arrRecentInfo.size();
	}
	
	public RecentInfo getRecent(int nIndex) {
		if (nIndex >= 0 && nIndex < m_arrRecentInfo.size())
			return m_arrRecentInfo.get(nIndex);
		else
			return null;
	}
}
