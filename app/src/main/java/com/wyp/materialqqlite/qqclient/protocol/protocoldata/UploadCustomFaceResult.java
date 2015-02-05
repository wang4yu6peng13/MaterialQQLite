package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import org.json.JSONObject;

public class UploadCustomFaceResult {
	public int m_nRetCode;
	public String m_strRemoteFileName;
	
	public void reset() {
		m_nRetCode = 0;
		m_strRemoteFileName = "";
	}
	
	public boolean parse(byte[] bytData) {
		try {
			reset();
			
			if (bytData == null || bytData.length <= 0)
				return false;
			
			String strData = new String(bytData, "UTF-8");
			System.out.println(strData);
			
			String strStart = "parent.EQQ.View.ChatBox.uploadCustomFaceCallback(";
			String strEnd = ");</script></head>";

			int nPos = strData.indexOf(strStart);
			if (nPos == -1)
				return false;
			nPos += strStart.length();
			int nPos2 = strData.indexOf(strEnd, nPos);
			if (nPos2 == -1)
				return false;
			strData = strData.substring(nPos, nPos2);
			strData.replaceAll("'", "\"");
			
			JSONObject json = new JSONObject(strData);
			m_nRetCode = json.optInt("ret");
			m_strRemoteFileName = json.optString("msg");
			if (4 == m_nRetCode) {
				nPos = m_strRemoteFileName.indexOf(' ');
				if (nPos != -1)
					m_strRemoteFileName = m_strRemoteFileName.substring(0, nPos);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
