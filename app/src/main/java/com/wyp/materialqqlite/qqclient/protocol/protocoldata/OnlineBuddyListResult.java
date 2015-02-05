package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class OnlineBuddyListResult {
	public int m_nRetCode;
	public ArrayList<OnlineBuddyInfo> m_arrOnlineBuddyInfo = new ArrayList<OnlineBuddyInfo>();
	
	public void reset() {
		m_nRetCode = 0;
		m_arrOnlineBuddyInfo.clear();
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
				OnlineBuddyInfo onlineBuddyInfo = new OnlineBuddyInfo();
				onlineBuddyInfo.reset();
				
				json = json2.optJSONObject(i);
				onlineBuddyInfo.m_nUin = json.optInt("uin");
				String strStatus = json.optString("status");
				onlineBuddyInfo.m_nStatus = QQStatus.convertToQQStatus(strStatus);
				onlineBuddyInfo.m_nClientType = json.optInt("client_type");
				
				m_arrOnlineBuddyInfo.add(onlineBuddyInfo);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
