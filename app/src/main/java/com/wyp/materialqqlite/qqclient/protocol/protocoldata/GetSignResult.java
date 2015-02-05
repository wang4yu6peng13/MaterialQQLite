package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONArray;
import org.json.JSONObject;

public class GetSignResult {
	public int m_nRetCode;
	public int m_nQQUin;			// 内部QQ号码
	public String m_strSign;		// 个性签名
	
	public void reset() {
		m_nRetCode = 0;
		m_nQQUin = 0;
		m_strSign = "";
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
			
			JSONArray json2 = json.optJSONArray("result");
			json = json2.optJSONObject(0);
			
			m_nQQUin = json.optInt("uin");
			m_strSign = json.optString("lnick");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
