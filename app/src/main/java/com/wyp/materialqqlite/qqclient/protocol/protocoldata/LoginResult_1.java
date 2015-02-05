package com.wyp.materialqqlite.qqclient.protocol.protocoldata;


import org.apache.http.cookie.Cookie;
import java.util.List;

public class LoginResult_1 {
	public int m_nRetCode;
	public String m_strCheckSigUrl, m_strMsg, m_strNickName;
	public String m_strPtWebQq;
	public String m_strSKey;
		
	// 写数据
//	private void writeFile(String fileName, String writestr) throws IOException {
//		try {
//			FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
//			byte[] bytes = writestr.getBytes();
//			fout.write(bytes);
//			fout.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public boolean parse(byte[] bytData, List<Cookie> cookies) {
		try {
			if (bytData == null || bytData.length <= 0)
				return false;

			String strData = new String(bytData, "UTF-8");
			System.out.println(strData);
			
			strData = strData.replaceAll("ptuiCB\\(", "");
			strData = strData.replaceAll("\\);", "");
			String[] arrStr = strData.split("',");
			
			if (arrStr.length < 6)
				return false;
			
			for (int i = 0; i < arrStr.length; i++) {
				arrStr[i] = arrStr[i].replaceAll("'", "");
				System.out.println(arrStr[i]);
			}
			
			m_nRetCode = (int)Long.parseLong(arrStr[0]);
			m_strCheckSigUrl = arrStr[2];
			m_strMsg = arrStr[4];
			m_strNickName = arrStr[5];

//			HTTP/1.1 200 OK
//			Date: Tue, 26 Mar 2013 04:08:43 GMT
//			Server: Tencent Login Server/2.0.0
//			P3P: CP="CAO PSA OUR"
//			Set-Cookie: pt2gguin=o0847708268; EXPIRES=Fri, 02-Jan-2020 00:00:00 GMT; PATH=/; DOMAIN=qq.com;
//			Set-Cookie: uin=o0847708268; PATH=/; DOMAIN=qq.com;
//			Set-Cookie: skey=@9Nf6S5Mqa; PATH=/; DOMAIN=qq.com;
//			Set-Cookie: ETK=; PATH=/; DOMAIN=ptlogin2.qq.com;
//			Set-Cookie: ptuserinfo=e5beaee5b098; PATH=/; DOMAIN=ptlogin2.qq.com;
//			Set-Cookie: ptwebqq=b6940e2d89ca07990a9f3edc04c335763a67a97746a573b0afcce74ea46a46e6; PATH=/; DOMAIN=qq.com;
//			Pragma: no-cache
//			Cache-Control: no-cache; must-revalidate
//			Connection: Close
//			Content-Type: application/x-javascript; charset=utf-8
			
			for(Cookie cookie : cookies)
			{
				System.out.println(cookie);
				
				if (cookie.getName().equals("ptwebqq"))
					m_strPtWebQq = cookie.getValue();
				
				if (cookie.getName().equals("skey"))
					m_strSKey = cookie.getValue();
			}
			
			if (m_strPtWebQq != null)
				System.out.println("ptwebqq:" + m_strPtWebQq);
			
			if (m_strSKey != null)
				System.out.println("skey:" + m_strSKey);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
