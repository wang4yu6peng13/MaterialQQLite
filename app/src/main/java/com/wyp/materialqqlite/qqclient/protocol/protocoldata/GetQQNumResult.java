package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class GetQQNumResult {
	public int m_nRetCode;
	public int m_nQQUin;			// 内部QQ号码
	public int m_nQQNum;			// QQ号码
	
	public void reset() {
		m_nRetCode = 0;
		m_nQQUin = 0;
		m_nQQNum = 0;
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
			m_nQQNum = json.optInt("account");
			m_nQQUin = json.optInt("uin");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
