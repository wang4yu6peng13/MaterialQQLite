package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class BuddyInfoResult {
	public int m_nRetCode;
	public int m_nQQUin;			// 内部QQ号码
	public String m_strNickName;	// 昵称
	public int m_nFace;				// 头像
	public String m_strGender;		// 性别
	public int m_nShengXiao;		// 生肖
	public int m_nConstel;			// 星座
	public int m_nBlood;			// 血型
	public String m_strBirthday;	// 生日
	public String m_strCountry;		// 国家
	public String m_strProvince;	// 省份
	public String m_strCity;		// 城市
	public String m_strPhone;		// 电话
	public String m_strMobile;		// 手机
	public String m_strEmail;		// 邮箱
	public String m_strOccupation;	// 职业
	public String m_strCollege;		// 毕业院校
	public String m_strHomepage;	// 个人主页
	public String m_strPersonal;	// 个人说明
	public boolean m_bAllow;
	public int m_nRegTime;
	public int m_nStat;
	public int m_nVipInfo;
	public int m_nClientType;		// 客户端类型：1-QQ客户端 41-WebQQ
	public String m_strToken;
	
	public void reset() {
		m_nRetCode = 0;
		m_nQQUin = 0;
		m_strNickName = "";
		m_nFace = 0;
		m_strGender = "";
		m_nShengXiao = 0;
		m_nConstel = 0;
		m_nBlood = 0;
		m_strBirthday = "";
		m_strCountry = "";
		m_strProvince = "";
		m_strCity = "";
		m_strPhone = "";
		m_strMobile = "";
		m_strEmail = "";
		m_strOccupation = "";
		m_strCollege = "";
		m_strHomepage = "";
		m_strPersonal = "";
		m_bAllow = false;
		m_nRegTime = 0;
		m_nStat = 0;
		m_nVipInfo = 0;
		m_nClientType = 0;
		m_strToken = "";	
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
			
			m_nFace = json.optInt("face");
			
			JSONObject json2 = json.optJSONObject("birthday");
			int nYear = json2.optInt("year");
			int nMonth = json2.optInt("month");
			int nDay = json2.optInt("day");
			m_strBirthday = nYear + "年" + nMonth + "月" + nDay + "日";
			
			m_strOccupation = json.optString("occupation");
			m_strPhone = json.optString("phone");
			m_bAllow = ((json.optInt("allow") != 0) ? true : false);
			m_strCollege = json.optString("college");
			m_nRegTime = json.optInt("reg_time");
			m_nQQUin = json.optInt("uin");
			m_nConstel = json.optInt("constel");
			m_nBlood = json.optInt("blood");
			m_strHomepage = json.optString("homepage");
			m_nStat = json.optInt("stat");
			m_nVipInfo = json.optInt("vip_info");
			m_strCountry = json.optString("country");
			m_strCity = json.optString("city");
			m_strPersonal = json.optString("personal");
			m_strNickName = json.optString("nick");
			m_nShengXiao = json.optInt("shengxiao");
			m_strEmail = json.optString("email");
			m_strProvince = json.optString("province");
			m_strGender = json.optString("gender");
			m_strMobile = json.optString("mobile");
			m_nClientType = json.optInt("client_type");
			m_strToken = json.optString("token");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
