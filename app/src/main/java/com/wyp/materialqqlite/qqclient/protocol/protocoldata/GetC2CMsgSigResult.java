package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class GetC2CMsgSigResult {
	public int m_nRetCode;
	public int m_nType;
	public String m_strValue;
	public int m_nFlags;
	public int m_nGroupId;
	public int m_nQQUin;

	public void reset() {
		m_nRetCode = 0;
		m_nType = 0;
		m_strValue = "";
		m_nFlags = 0;
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
			
			m_nType = json.optInt("type");
			m_strValue = json.optString("value");
			m_nFlags = 0;	// 暂时不解析"flags":{"text":1,"pic":1,"file":1,"audio":1,"video":1}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
