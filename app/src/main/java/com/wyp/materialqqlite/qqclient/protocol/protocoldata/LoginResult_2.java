package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class LoginResult_2 {
	public int m_nRetCode;
	public int m_nUIN;
	public int m_nCIP;
	public int m_nIndex;
	public int m_nPort;
	public int m_nStatus;
	public String m_strVfWebQq;
	public String m_strPSessionId;
	public int m_nUser_State;
	public int m_f;
	public int m_nServerTime;
	public int m_dwTickCount;
	
	public boolean parse(byte[] bytData) {
		try {
			if (bytData == null || bytData.length <= 0)
				return false;

			String strData = new String(bytData, "UTF-8");
			System.out.println(strData);
			
			JSONObject json = new JSONObject(strData);
			
			m_nRetCode = json.optInt("retcode");
			
            json = json.optJSONObject("result");
            
            m_nUIN = json.optInt("uin");
            m_nCIP = json.optInt("cip");
            m_nIndex = json.optInt("index");
            m_nPort = json.optInt("port");
            
            String strValue = json.optString("status");
            m_nStatus = QQStatus.convertToQQStatus(strValue);

            m_strVfWebQq = json.optString("vfwebqq");
            m_strPSessionId = json.optString("psessionid");

            m_nUser_State = json.optInt("user_state");
            m_f = json.optInt("f");
            
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
