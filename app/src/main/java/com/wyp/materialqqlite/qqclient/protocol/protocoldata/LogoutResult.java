package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;


public class LogoutResult {
	
	public int m_nRetCode;
	public String m_strResult;
	
	public boolean parse(byte[] bytData) {
		try {
			if (bytData == null || bytData.length <= 0)
				return false;

			String strData = new String(bytData, "UTF-8");
			System.out.println(strData);
			
			JSONObject json = new JSONObject(strData);
			m_nRetCode = json.optInt("retcode");
			m_strResult = json.optString("result");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
