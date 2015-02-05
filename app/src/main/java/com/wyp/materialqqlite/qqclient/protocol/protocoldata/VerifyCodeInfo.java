package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import com.wyp.materialqqlite.Utils;

public class VerifyCodeInfo {
	
	public int m_nNeedVerify;
	public String m_strVerifyCode;
	public String m_strVCType;
	public byte[] m_bytPtUin;
				
	public boolean parse(byte[] bytData) {
		// ptui_checkVC('0','!C4P', '\x00\x00\x00\x00\x32\x87\x00\x6c');
		// ptui_checkVC('1','7DiMADc6_jzM06uHBKkRiYSvoG4jEiHa','\x00\x00\x00\x00\x32\x87\x00\x6c', '');
		
		String strData = null;
		
		try {
			strData = new String(bytData, "UTF-8");
			System.out.println(strData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		String strTemp1 = null, strTemp2 = null, strTemp3 = null;
		
		int nPos1 = strData.indexOf("ptui_checkVC('");
		if (nPos1 != -1)
		{
			nPos1 += "ptui_checkVC('".length();
			int nPos2 = strData.indexOf("','", nPos1);
			if (nPos2 != -1)
			{
				strTemp1 = strData.substring(nPos1, nPos2);
				if (!Utils.isEmptyStr(strTemp1))
					m_nNeedVerify = (int)Long.parseLong(strTemp1);

				nPos1 = nPos2 + "','".length();
				nPos2 = strData.indexOf("','", nPos1);
				if (nPos2 != -1)
				{
					strTemp2 = strData.substring(nPos1, nPos2);

					nPos1 = nPos2 + "','".length();
					nPos2 = strData.indexOf("',", nPos1);
					if (nPos2 != -1)
						strTemp3 = strData.substring(nPos1, nPos2);
				}
			}
		}

		if (m_nNeedVerify == 0)		// 不需要验证码
		{
			m_strVCType = "";
			m_strVerifyCode = strTemp2;
		}
		else
		{
			m_strVCType = strTemp2;
			m_strVerifyCode = "";
		}

		strTemp3 = strTemp3.toLowerCase();
		strTemp3 = strTemp3.replaceAll("\\\\x", ""); 
		 
		char arrChar[] = strTemp3.toCharArray();
		int nLen = strTemp3.length() / 2;
		m_bytPtUin = new byte[nLen];
		
		for (int i = 0; i < nLen; i++)
		{
			int pos = i * 2;
			m_bytPtUin[i] = (byte)(charToByte(arrChar[pos]) << 4 | charToByte(arrChar[pos+1]));
		}		
		return true;
	}
	
	private byte charToByte(char c) {
	    return (byte) "0123456789abcdef".indexOf(c);  
	}
}
