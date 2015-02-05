package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class SetSignResult {
	public int m_nRetCode;
	public int m_nResult;
	
	public void reset() {
		m_nRetCode = 0;
		m_nResult = 0;
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
			m_nResult = json.optInt("result");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
