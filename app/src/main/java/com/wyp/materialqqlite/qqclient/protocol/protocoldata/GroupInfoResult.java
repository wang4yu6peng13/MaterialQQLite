package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wyp.materialqqlite.FileUtils;
import com.wyp.materialqqlite.Utils;

public class GroupInfoResult {
	public int m_nRetCode;
	public int m_nGroupCode;		// 群代码
	public int m_nGroupId;			// 群ID
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
	public ArrayList<BuddyInfo> m_arrMember = new ArrayList<BuddyInfo>();	// 群成员
	
	public void reset() {
		m_nRetCode = 0;
		m_nGroupCode = 0;
		m_nGroupId = 0;
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
		m_arrMember.clear();
	}
	
	public boolean parse(byte[] bytData) {
		try {
//			reset();
			
			if (bytData == null || bytData.length <= 0)
				return false;
			
			String strData = new String(bytData, "UTF-8");
			System.out.println(strData);
			
			JSONObject json = new JSONObject(strData);
			m_nRetCode = json.optInt("retcode");
			
			json = json.optJSONObject("result");
			
			JSONObject json2 = json.optJSONObject("ginfo");
			if (json2 != null) {
				m_nFace = json2.optInt("face");
				m_strMemo = json2.optString("memo");
				if (!Utils.isEmptyStr(m_strMemo))
					m_strMemo.replaceAll("\r", "\r\n");
				m_nClass = json2.optInt("class");
				m_strFingerMemo = json2.optString("fingermemo");
				if (!Utils.isEmptyStr(m_strFingerMemo))
					m_strFingerMemo.replaceAll("\r", "\r\n");
				m_nGroupCode = json2.optInt("code");
				m_nCreateTime = json2.optInt("createtime");
				m_nFlag = json2.optInt("flag");
				m_nLevel = json2.optInt("level");
				m_strName = json2.optString("name");
				m_nGroupId = json2.optInt("gid");
				m_nOwnerUin = json2.optInt("owner");
				m_nOption = json2.optInt("option");				
			}
			
			JSONArray json3 = json2.optJSONArray("members");
			if (json3 != null) {
				for (int i = 0; i < json3.length(); i++) {
					JSONObject json4 = json3.optJSONObject(i);
					
					BuddyInfo buddyInfo = new BuddyInfo();
					buddyInfo.m_nQQUin = json4.optInt("muin");
					buddyInfo.m_nGroupFlag = json4.optInt("mflag");
					m_arrMember.add(buddyInfo);
				}				
			}
			
			json3 = json.optJSONArray("stats");
			if (json3 != null) {
				for (int i = 0; i < json3.length(); i++) {
					JSONObject json4 = json3.optJSONObject(i);
					
					int nQQUin = json4.optInt("uin");
					
					BuddyInfo buddyInfo = getMemberByUin(nQQUin);
					if (buddyInfo != null) {
						buddyInfo.m_nClientType = json4.optInt("client_type");
						buddyInfo.m_nStatus = json4.optInt("stat");
					}
				}				
			}
			
			json3 = json.optJSONArray("minfo");
			if (json3 != null) {
				for (int i = 0; i < json3.length(); i++) {
					JSONObject json4 = json3.optJSONObject(i);
					
					int nQQUin = json4.optInt("uin");
					
					BuddyInfo buddyInfo = getMemberByUin(nQQUin);
					if (buddyInfo != null) {
						Object o = json4.opt("nick");
						if (o instanceof String) {
							buddyInfo.m_strNickName = json4.optString("nick");						
						} else if (o instanceof Integer) {
							buddyInfo.m_strNickName = String.valueOf(json4.optInt("nick"));
						} else {
							System.out.println("未知类型");
						}
						
						if (null == buddyInfo.m_strNickName) {
							System.out.println("空的");
						}
						
						buddyInfo.m_strProvince = json4.optString("province");
						buddyInfo.m_strGender = json4.optString("gender");
						buddyInfo.m_strCountry = json4.optString("country");
						buddyInfo.m_strCity = json4.optString("city");
					} else {
						System.out.println("找不到QQUin");
					}
				}				
			} else {
				System.out.println("minfo");
				//Utils.writeFile(FileUtils.getSDCardDir() + "mingqqlog.txt", strData.getBytes());
			}
			
			json3 = json.optJSONArray("cards");
			if (json3 != null) {
				for (int i = 0; i < json3.length(); i++) {
					JSONObject json4 = json3.optJSONObject(i);
					
					int nQQUin = json4.optInt("muin");
					
					BuddyInfo buddyInfo = getMemberByUin(nQQUin);
					if (buddyInfo != null) {
						buddyInfo.m_strGroupCard = json4.optString("card");
					}
				}				
			}
			
			json3 = json.optJSONArray("vipinfo");
			if (json3 != null) {
				for (int i = 0; i < json3.length(); i++) {
					JSONObject json4 = json3.optJSONObject(i);
					
					int nQQUin = json4.optInt("u");
					
					BuddyInfo buddyInfo = getMemberByUin(nQQUin);
					if (buddyInfo != null) {
						buddyInfo.m_nVipLevel = json4.optInt("vip_level");
						buddyInfo.m_bIsVip = (json4.optInt("is_vip") != 0);
					}
				}				
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public BuddyInfo getMemberByUin(int nQQUin) {
		for (int i = 0; i < m_arrMember.size(); i++)
		{
			BuddyInfo buddyInfo = m_arrMember.get(i);
			if (buddyInfo != null && buddyInfo.m_nQQUin == nQQUin)
				return buddyInfo;
		}
		return null;
	}
}
