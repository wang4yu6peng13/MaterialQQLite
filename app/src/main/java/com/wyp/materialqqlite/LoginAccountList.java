package com.wyp.materialqqlite;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginAccountList {
	int m_nLastLoginUser;						// 最后登录用户索引
	List<LoginAccountInfo> m_arrLoginAccount;	// 登录账号列表
	
	public LoginAccountList() {
		m_nLastLoginUser = 0;
		m_arrLoginAccount = new ArrayList<LoginAccountInfo>();
	}
	
	public void reset() {
		m_nLastLoginUser = 0;
		m_arrLoginAccount.clear();
	}
	
	// 加载登录帐号列表文件
	public boolean loadFile(String strFileName) {
		if (Utils.isEmptyStr(strFileName))
			return false;
		
		File file = new File(strFileName);
		if (!file.exists())
			return false;
		
		int nFileLen = (int)file.length();
		if (nFileLen <= 0)
			return false;
		
		reset();
		
		try {
			byte[] bytData = new byte[nFileLen];
			
			FileInputStream in = new FileInputStream(file);  
			in.read(bytData);
			in.close();
			
			decrypt(bytData);
			
			ByteArrayInputStream is = new ByteArrayInputStream(bytData);
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader bufReader = new BufferedReader(reader);
						
			String strLine = bufReader.readLine();
			if (null == strLine)
				return false;
			m_nLastLoginUser = Integer.parseInt(strLine);
			
			while ((strLine = bufReader.readLine()) != null) {
				String[] text = strLine.split(",");
				
				if (text.length != 5)
					continue;

				LoginAccountInfo account = new LoginAccountInfo();
				account.m_strUser = text[0];				
				account.m_strPwd = text[1];
				account.m_nStatus = Integer.parseInt(text[2]);
				account.m_bRememberPwd = (Integer.parseInt(text[3]) != 0) ? true : false;
				account.m_bAutoLogin = (Integer.parseInt(text[4]) != 0) ? true : false;
				
				m_arrLoginAccount.add(account);
			}

			if (m_nLastLoginUser < 0 || m_nLastLoginUser >= m_arrLoginAccount.size())
				m_nLastLoginUser = 0;
			
			bufReader.close();
			
			return true;
		} catch (FileNotFoundException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}
		return false;
	}

	// 保存登录帐号列表文件
	public boolean saveFile(String strFileName) {
		if (Utils.isEmptyStr(strFileName))
			return false;

		int nCount = m_arrLoginAccount.size();
		if (nCount <= 0)
			return false;

		String strPath = strFileName.substring(0, strFileName.lastIndexOf("/"));

		File dir = new File(strPath);
		if (!dir.exists())
			dir.mkdirs();

		String strText = String.valueOf(m_nLastLoginUser) + "\n";
		for (int i = 0; i < nCount; i++) {
			LoginAccountInfo account = m_arrLoginAccount.get(i);
			if (null == account)
				continue;
			strText += account.toString();
		}
		
		byte[] bytData = strText.getBytes();
		encrypt(bytData);
		
		try {
			FileOutputStream os = new FileOutputStream(strFileName);
			os.write(bytData);
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 添加帐号
	public int add(String strUser, String strPwd, 
			int nStatus, boolean bRememberPwd, boolean bAutoLogin) {
		if (Utils.isEmptyStr(strUser))
			return -1;

		int nPos = find(strUser);
		if (nPos != -1) {
			LoginAccountInfo account = getAccountInfo(nPos);
			account.m_strPwd = strPwd;
			account.m_nStatus = nStatus;
			account.m_bRememberPwd = bRememberPwd;
			account.m_bAutoLogin = bAutoLogin;
			return nPos;
		} else {
			LoginAccountInfo account = new LoginAccountInfo();
			account.m_strUser = strUser;
			account.m_strPwd = strPwd;
			account.m_nStatus = nStatus;
			account.m_bRememberPwd = bRememberPwd;
			account.m_bAutoLogin = bAutoLogin;
		
			m_arrLoginAccount.add(account);
			return m_arrLoginAccount.size()-1;
		}
	}

	// 删除帐号
	public boolean del(int nIndex) {
		if (nIndex < 0 || nIndex >= m_arrLoginAccount.size())
			return false;

		m_arrLoginAccount.remove(nIndex);
		return true;
	}

	// 修改帐号
	public boolean modify(int nIndex, String strUser, String strPwd, 
			int nStatus, boolean bRememberPwd, boolean bAutoLogin) {
		if (nIndex < 0 || nIndex >= m_arrLoginAccount.size() || Utils.isEmptyStr(strUser))
			return false;

		LoginAccountInfo account = m_arrLoginAccount.get(nIndex);
		if (null == account)
			return false;

		account.m_strUser = strUser;
		account.m_strPwd = strPwd;
		account.m_nStatus = nStatus;
		account.m_bRememberPwd = bRememberPwd;
		account.m_bAutoLogin = bAutoLogin;
		return true;
	}

	// 清除所有帐号
	public void clear() {
		m_arrLoginAccount.clear();
	}

	// 获取帐号总数
	public int getCount() {
		return m_arrLoginAccount.size();
	}

	// 获取帐号信息
	public LoginAccountInfo getAccountInfo(int nIndex) {
		if (nIndex < 0 || nIndex >= m_arrLoginAccount.size())
			return null;

		return m_arrLoginAccount.get(nIndex);
	}

	// 查找帐号
	public int find(String strUser) {
		if (Utils.isEmptyStr(strUser))
			return -1;

		for (int i = 0; i < m_arrLoginAccount.size(); i++) {
			LoginAccountInfo account = m_arrLoginAccount.get(i);
			if (account != null && account.m_strUser.equals(strUser))
				return i;
		}
		return -1;
	}

	public int getLastLoginUser() {
		return m_nLastLoginUser;
	}

	public LoginAccountInfo getLastLoginAccountInfo() {
		return getAccountInfo(m_nLastLoginUser);
	}

	public void setLastLoginUser(int nPos) {
		m_nLastLoginUser = nPos;
	}
	
	public void setLastLoginUser(String strUser) {
		if (Utils.isEmptyStr(strUser))
			return;

		for (int i = 0; i < m_arrLoginAccount.size(); i++) {
			LoginAccountInfo account = m_arrLoginAccount.get(i);
			if (account != null && account.m_strUser.equals(strUser)) {
				m_nLastLoginUser = i;
				return;
			}
		}
	}

	// 加密
	private void encrypt(byte[] bytData) {
		for (int i = 0; i < bytData.length; i++) {
			bytData[i] = (byte)(bytData[i] ^ 0x88);
		}
	}

	// 解密
	private void decrypt(byte[] bytData) {
		for (int i = 0; i < bytData.length; i++) {
			bytData[i] = (byte)(bytData[i] ^ 0x88);
		}
	}
}
