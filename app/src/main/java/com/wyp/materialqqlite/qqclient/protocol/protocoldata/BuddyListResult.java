package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class BuddyListResult {
	public int m_nRetCode;
	public ArrayList<BuddyTeamInfo> m_arrBuddyTeamInfo = new ArrayList<BuddyTeamInfo>();
	
	public void reset() {
		m_nRetCode = 0;
		m_arrBuddyTeamInfo.clear();
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
			JSONArray json2 = json.optJSONArray("categories");
			for (int i = 0; i < json2.length(); i++) {
				BuddyTeamInfo buddyTeamInfo = new BuddyTeamInfo();
				buddyTeamInfo.reset();
				
				JSONObject json3 = (JSONObject)json2.opt(i);
				buddyTeamInfo.m_nIndex = json3.optInt("index");
				buddyTeamInfo.m_nSort = json3.optInt("sort");
				buddyTeamInfo.m_strName = json3.optString("name");

				m_arrBuddyTeamInfo.add(buddyTeamInfo);
			}
			
			BuddyTeamInfo buddyTeamInfo = getBuddyTeamByInnerIndex(0);
			if (null == buddyTeamInfo) {
				buddyTeamInfo = new BuddyTeamInfo();
				buddyTeamInfo.m_nIndex = 0;
				buddyTeamInfo.m_nSort = 0;
				buddyTeamInfo.m_strName = "我的好友";
				m_arrBuddyTeamInfo.add(buddyTeamInfo);
			}
			sortBuddyTeam();
			
			json2 = json.optJSONArray("friends");
			for (int i = 0; i < json2.length(); i++) {
				BuddyInfo buddyInfo = new BuddyInfo();
				buddyInfo.reset();

				JSONObject json3 = (JSONObject)json2.opt(i);
				buddyInfo.m_nQQUin = json3.optInt("uin");
				buddyInfo.m_nTeamIndex = json3.optInt("categories");
				
				buddyTeamInfo = getBuddyTeamByInnerIndex(buddyInfo.m_nTeamIndex);
				if (buddyTeamInfo != null)
					buddyTeamInfo.m_arrBuddyInfo.add(buddyInfo);
			}
			
			json2 = json.optJSONArray("marknames");
			for (int i = 0; i < json2.length(); i++) {
				JSONObject json3 = (JSONObject)json2.opt(i);
				int nQQUin = json3.optInt("uin");
				
				BuddyInfo buddyInfo = getBuddy(nQQUin);
				if (buddyInfo != null) {
					buddyInfo.m_strMarkName = json3.optString("markname");
				}
			}
			
			json2 = json.optJSONArray("vipinfo");
			for (int i = 0; i < json2.length(); i++) {
				JSONObject json3 = (JSONObject)json2.opt(i);
				int nQQUin = json3.optInt("u");
				
				BuddyInfo buddyInfo = getBuddy(nQQUin);
				if (buddyInfo != null) {
					buddyInfo.m_bIsVip = ((json3.optInt("is_vip") != 0) ? true : false);
					buddyInfo.m_nVipLevel = json3.optInt("vip_level");
				}
			}
			
			json2 = json.optJSONArray("info");
			for (int i = 0; i < json2.length(); i++) {
				JSONObject json3 = (JSONObject)json2.opt(i);
				int nQQUin = json3.optInt("uin");
				
				BuddyInfo buddyInfo = getBuddy(nQQUin);
				if (buddyInfo != null) {
					buddyInfo.m_nFace = json3.optInt("face");
					buddyInfo.m_nFlag = json3.optInt("flag");
					buddyInfo.m_strNickName = json3.optString("nick");
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getBuddyTeamCount() {
		return m_arrBuddyTeamInfo.size();
	}
	
	public BuddyTeamInfo getBuddyTeam(int nTeamIndex) {
		if (nTeamIndex >= 0 && nTeamIndex < (int)m_arrBuddyTeamInfo.size())
			return m_arrBuddyTeamInfo.get(nTeamIndex);
		else
			return null;
	}
	
	public BuddyTeamInfo getBuddyTeamByInnerIndex(int nInnerTeamIndex) {
		for (int i = 0; i < m_arrBuddyTeamInfo.size(); i++)
		{
			BuddyTeamInfo buddyTeamInfo = m_arrBuddyTeamInfo.get(i);
			if (buddyTeamInfo != null && nInnerTeamIndex == buddyTeamInfo.m_nIndex)
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
}
