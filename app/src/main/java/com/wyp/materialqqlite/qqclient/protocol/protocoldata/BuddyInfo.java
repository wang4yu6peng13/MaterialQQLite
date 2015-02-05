package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class BuddyInfo {
	public int m_nQQUin;			// 内部QQ号码
	public int m_nQQNum;			// QQ号码
	public int m_nTeamIndex;		// 分组索引
	public String m_strNickName;	// 昵称
	public String m_strMarkName;	// 备注
	public boolean m_bIsVip;		// 是否VIP标志
	public int m_nVipLevel;			// VIP等级
	public int m_nFace;				// 头像
	public int m_nFlag;				// 
	public int m_nStatus;			// 在线状态
	public int m_nClientType;		// 客户端类型：1-QQ客户端 41-WebQQ
	public String m_strGroupCard;	// 群名片
	public int m_nGroupFlag;
	public String m_strSign;		// QQ个性签名
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
	public String m_strToken;
	public String m_strGroupSig;	// 群内会话信令
	public boolean m_bHasQQNum;
	public boolean m_bHasQQSign;
	public boolean m_bHasBuddyInfo;
	
	public void reset() {
		m_nQQUin = 0;
		m_nQQNum = 0;
		m_nTeamIndex = 0;
		m_strNickName = "";
		m_strMarkName = "";
		m_bIsVip = false;
		m_nVipLevel = 0;
		m_nFace = 0;
		m_nFlag = 0;
		m_nStatus = QQStatus.OFFLINE;
		m_nClientType = 0;
		m_strGroupCard = "";
		m_nGroupFlag = 0;
		m_strSign = "";
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
		m_strToken = "";
		m_strGroupSig = "";
		m_bHasQQNum = m_bHasQQSign = m_bHasBuddyInfo = false;
	}
	
	public void setQQNum(GetQQNumResult result) {
		if (result != null) {
			m_nQQNum = result.m_nQQNum;
			m_bHasQQNum = true;	
		}
	}
	
	public void setQQSign(GetSignResult result) {
		if (result != null) {
			m_strSign = result.m_strSign;
			m_bHasQQSign = true;	
		}
	}
	
	public void setBuddyInfo(BuddyInfoResult result) {
		if (null == result)
			return;

		m_nQQUin = result.m_nQQUin;
		m_strNickName = result.m_strNickName;
		m_nFace = result.m_nFace;
		m_strGender = result.m_strGender;
		m_nShengXiao = result.m_nShengXiao;
		m_nConstel = result.m_nConstel;
		m_nBlood = result.m_nBlood;
		m_strBirthday = result.m_strBirthday;
		m_strCountry = result.m_strCountry;
		m_strProvince = result.m_strProvince;
		m_strCity = result.m_strCity;
		m_strPhone = result.m_strPhone;
		m_strMobile = result.m_strMobile;
		m_strEmail = result.m_strEmail;
		m_strOccupation = result.m_strOccupation;
		m_strCollege = result.m_strCollege;
		m_strHomepage = result.m_strHomepage;
		m_strPersonal = result.m_strPersonal;
		m_bAllow = result.m_bAllow;
		m_nRegTime = result.m_nRegTime;
		m_nStat = result.m_nStat;
		m_nVipInfo = result.m_nVipInfo;
		m_nClientType = result.m_nClientType;
		m_strToken = result.m_strToken;
		m_bHasBuddyInfo = true;
	}
	
	public boolean isHasQQNum() {
		return m_bHasQQNum;
	}
	
	public boolean isHasQQSign() {
		return m_bHasQQSign;
	}
	
	public boolean isHasBuddyInfo() {
		return m_bHasBuddyInfo;
	}
	
	public String getDisplayGender() {
		if (m_strGender.equals("male"))
			return "男";
		else if (m_strGender.equals("female"))
			return "女";
		else
			return "未知";	// "unknown"
	}
	
	public String getDisplayShengXiao() {
		String[] cShengXiao = new String[]{"", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
		if (m_nShengXiao >= 0 && m_nShengXiao < 12)
			return cShengXiao[m_nShengXiao];
		else
			return "";
	}
	
	public String getDisplayConstel() {
		String[] cConstel = new String[]{"", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
		if (m_nConstel >= 0 && m_nConstel < 12)
			return cConstel[m_nConstel];
		else
			return "";
	}
	
	public String getDisplayBlood() {
		String[] cBlood = new String[]{"", "A型", "B型", "O型", "AB型", "其它"};
		if (m_nBlood >= 0 && m_nBlood < 5)
			return cBlood[m_nBlood];
		else
			return "";
	}
}
