package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

import com.wyp.materialqqlite.Utils;
import org.apache.http.cookie.Cookie;

import java.util.List;

public class VerifyCodeInfo {

    public String m_strLoginSig;
    public String m_strVerifySession;
    public int m_nNeedVerify;
    public String m_strVerifyCode;
    public String m_strVCType;
    public String m_strPtUin;

    public boolean parse(byte[] bytData, List<Cookie> cookies) {
        for(Cookie cookie : cookies)
        {
            System.out.println(cookie);

            if (cookie.getName().equals("ptvfsession"))
                m_strVerifySession = cookie.getValue();
        }

        // ptui_checkVC('0','!NRX','\x00\x00\x00\x00\xb5\x34\x4d\x5e','7484d5a92cb6836f897e76ee268fa80a0a7b18f15a7de39a3ba87c47a194140fbc7df832d12da33950314531e1df2716','0');
        // ptui_checkVC('1','x8q5LZ7wvEahePcdJeDOPVtNBwCSl4fe','\x00\x00\x00\x00\xae\xf3\xfe\x16','','0');

        String strData = null;

        try {
            strData = new String(bytData, "UTF-8");
            System.out.println(strData);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String strTemp1 = null, strTemp2 = null, strTemp3 = null;

        int nPos1 = strData.indexOf("ptui_checkVC('");
        if (nPos1 != -1) {
            nPos1 += "ptui_checkVC('".length();
            int nPos2 = strData.indexOf("'", nPos1);
            if (nPos2 != -1) {
                strTemp1 = strData.substring(nPos1, nPos2);

                nPos1 = nPos2 + "'".length();
                nPos1 = strData.indexOf("'", nPos1);
                if (nPos1 != -1) {
                    nPos1 += "'".length();
                    nPos2 = strData.indexOf("'", nPos1);
                    if (nPos2 != -1) {
                        strTemp2 = strData.substring(nPos1, nPos2);

                        nPos1 = nPos2 + "'".length();
                        nPos1 = strData.indexOf("'", nPos1);
                        if (nPos1 != -1) {
                            nPos1 += "'".length();
                            nPos2 = strData.indexOf("'", nPos1);
                            if (nPos2 != -1) {
                                strTemp3 = strData.substring(nPos1, nPos2);
                            }
                        }
                    }
                }
            }
        }

        if (!Utils.isEmptyStr(strTemp1))
            m_nNeedVerify = (int)Long.parseLong(strTemp1);

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

        m_strPtUin = strTemp3;

        return true;
    }

    private byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }
}