package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class GroupListResult {
	public int m_nRetCode;
	public ArrayList<GroupInfo> m_arrGroupInfo = new ArrayList<GroupInfo>();
	
	public void reset() {
		m_nRetCode = 0;
		m_arrGroupInfo.clear();
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
			
			json = json.optJSONObject("result");
			JSONArray json2 = json.optJSONArray("gnamelist");
			for (int i = 0; i < json2.length(); i++) {
				JSONObject json3 = json2.optJSONObject(i);
				
				GroupInfo groupInfo = new GroupInfo();
				groupInfo.reset();
				
				groupInfo.m_nFlag = json3.optInt("flag");
				groupInfo.m_strName = json3.optString("name");
				groupInfo.m_nGroupId = json3.optInt("gid");
				groupInfo.m_nGroupCode = json3.optInt("code");

				m_arrGroupInfo.add(groupInfo);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// 获取群总数
	public int getGroupCount() {
		return m_arrGroupInfo.size();
	}
	
	// 根据索引获取群信息
	public GroupInfo getGroup(int nIndex) {
		if (nIndex >= 0 && nIndex < m_arrGroupInfo.size())
			return m_arrGroupInfo.get(nIndex);
		else
			return null;
	}
}
