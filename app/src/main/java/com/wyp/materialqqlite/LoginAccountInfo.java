package com.wyp.materialqqlite;

public class LoginAccountInfo {
	public String m_strUser = "";		// 用户名
	public String m_strPwd = "";		// 密码
	public int m_nStatus;				// 登录状态
	public boolean m_bRememberPwd;		// 记住密码
	public boolean m_bAutoLogin;		// 自动登录
	
	public String toString() {
		return m_strUser + "," + m_strPwd
				+ "," + m_nStatus 
				+ "," + (m_bRememberPwd?1:0) 
				+ "," + (m_bAutoLogin?1:0) + "\n";
	}
}
