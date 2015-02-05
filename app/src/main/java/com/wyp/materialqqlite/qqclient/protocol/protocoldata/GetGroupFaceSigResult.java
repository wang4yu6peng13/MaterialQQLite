package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class GetGroupFaceSigResult {
	public int m_nRetCode;
	public int m_nReply;
	public String m_strGFaceKey;
	public String m_strGFaceSig;
	
	public void reset() {
		m_nRetCode = 0;
		m_nReply = 0;
		m_strGFaceKey = "";
		m_strGFaceSig = "";
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
			m_nReply = json.optInt("reply");
			m_strGFaceKey = json.optString("gface_key");
			m_strGFaceSig = json.optString("gface_sig");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
