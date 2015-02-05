package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecentListResult {
	public int m_nRetCode;
	public ArrayList<RecentInfo> m_arrRecentInfo = new ArrayList<RecentInfo>();;
	
	public void reset() {
		m_nRetCode = 0;
		m_arrRecentInfo.clear();
	}
	
	public boolean parse(byte[] bytData) {
		try {
			reset();
			
			if (bytData == null || bytData.length <= 0)
				return false;
			
			String strData = new String(bytData, "UTF-8");
			System.out.println(strData);
			
			JSONObject json = new JSONObject(strData);
			m_nRetCode = json.optInt("retcode");
			
			JSONArray json2 = json.optJSONArray("result");
			for (int i = 0; i < json2.length(); i++) {
				json = json2.optJSONObject(i);
				
				RecentInfo recentInfo = new RecentInfo();
				recentInfo.reset();

				recentInfo.m_nQQUin = json.optInt("uin");
				recentInfo.m_nType = json.optInt("type");

				m_arrRecentInfo.add(recentInfo);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
