package com.wyp.materialqqlite.qqclient.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import org.apache.http.client.HttpClient;

//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//
//import com.wyp.materialqqlite.qqclient.QQManager;

import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.protocol.QQProtocol;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfoResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyListResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetSignResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupListResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.OnlineBuddyListResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQLoginResultCode;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.RecentListResult;

public class LoginTask extends HttpTask {

	public TaskManager m_recvMsgTaskMgr;
	
	public LoginTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
	}
	
	// 写数据
	private void writeFile(String fileName, byte[] bytData) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
	        fos.write(bytData);
	        fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String readFile(String filePath) {
		try {
			String encoding="GBK";
			File file=new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),encoding);//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String strText = bufferedReader.readLine();
				read.close();
				return strText;
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	@Override
	public void doTask() {
		if (null == m_httpClient || null == m_QQUser)
			return;
		
		try {
			boolean bRet;
			
			if (Utils.isEmptyStr(m_QQUser.m_strVerifyCode)) {	// 验证码为空
				bRet = QQProtocol.checkVerifyCode(m_httpClient,	// 检测是否需要输入验证码
					m_QQUser.m_strQQNum, QQProtocol.WEBQQ_APP_ID, m_QQUser.m_VerifyCodeInfo);
				if (!bRet || m_bCancel) {
					sendLoginResultMsg(QQLoginResultCode.FAILED);
					return;
				}

				if (m_QQUser.m_VerifyCodeInfo.m_nNeedVerify == 1) {	// 需要验证码
					// 获取验证码图片
					m_QQUser.m_VerifyCodePic = QQProtocol.getVerifyCodePic(
							m_httpClient, QQProtocol.WEBQQ_APP_ID, 
							m_QQUser.m_strQQNum, m_QQUser.m_VerifyCodeInfo.m_strVCType);
					if (!bRet || m_bCancel) {
						sendLoginResultMsg(QQLoginResultCode.FAILED);
						return;
					}
					
					//writeFile("D:/abc.png", m_QQUser.m_VerifyCodePic);
					//m_QQUser.m_strVerifyCode = readFile("D:/vc.txt");
					
					sendLoginResultMsg(QQLoginResultCode.NEED_VERIFY_CODE);
					return;
				}
				else	// 不需要验证码
				{
					m_QQUser.m_strVerifyCode = m_QQUser.m_VerifyCodeInfo.m_strVerifyCode;
				}
			}

			bRet = QQProtocol.login1(m_httpClient, m_QQUser.m_strQQNum,		// 第一次登录
				m_QQUser.m_strQQPwd, m_QQUser.m_strVerifyCode, 
				m_QQUser.m_VerifyCodeInfo.m_bytPtUin,
				QQProtocol.WEBQQ_APP_ID, m_QQUser.m_LoginResult1);
			if (!bRet || m_bCancel) {
				m_QQUser.m_strVerifyCode = null;
				m_QQUser.m_VerifyCodePic = null;
				sendLoginResultMsg(QQLoginResultCode.FAILED);
				return;
			}

			m_QQUser.m_strVerifyCode = null;
			m_QQUser.m_VerifyCodePic = null;
			
			if (m_QQUser.m_LoginResult1.m_nRetCode != 0) {		// 登录失败
				if (m_QQUser.m_LoginResult1.m_nRetCode == 4) {	// 验证码错误
					m_QQUser.m_VerifyCodePic = QQProtocol.getVerifyCodePic(m_httpClient,	// 获取验证码图片
							QQProtocol.WEBQQ_APP_ID, m_QQUser.m_strQQNum, 
							m_QQUser.m_VerifyCodeInfo.m_strVCType);
					if (!bRet || m_bCancel) {
						sendLoginResultMsg(QQLoginResultCode.FAILED);
						return;
					}
					
					sendLoginResultMsg(QQLoginResultCode.VERIFY_CODE_ERROR);
					return;
				}
				else if (m_QQUser.m_LoginResult1.m_nRetCode == 3) {		// 密码错误
					sendLoginResultMsg(QQLoginResultCode.PASSWORD_ERROR);
					return;
				}
				else {	// 未知错误
					sendLoginResultMsg(QQLoginResultCode.FAILED);
					return;
				}
			}
			
			bRet = QQProtocol.login2(m_httpClient,	// 第二次登录 
					m_QQUser.m_nLoginStatus, m_QQUser.m_LoginResult1.m_strPtWebQq, 
					QQProtocol.WEBQQ_CLIENT_ID, m_QQUser.m_LoginResult2);
			if (!bRet || m_QQUser.m_LoginResult2.m_nRetCode != 0 || m_bCancel) {
				sendLoginResultMsg(QQLoginResultCode.FAILED);
				return;
			}

			BuddyInfoResult buddyInfoResult = getUserInfo();		// 获取用户信息
			if (m_bCancel) {
				sendLoginResultMsg(QQLoginResultCode.FAILED);
				return;
			}

			GetSignResult getSignResult = getUserSign();		// 获取用户签名
			if (m_bCancel) {
				sendLoginResultMsg(QQLoginResultCode.FAILED);
				return;
			}

			BuddyListResult buddyListResult = getBuddyList();		// 获取好友列表
			if (m_bCancel) {
				sendLoginResultMsg(QQLoginResultCode.FAILED);
				return;
			}

			GroupListResult groupListResult = getGroupList();		// 获取群列表
			if (m_bCancel) {
				sendLoginResultMsg(QQLoginResultCode.FAILED);
				return;
			}

			RecentListResult recentListResult = getRecentList();	// 获取最近联系人列表
			if (m_bCancel) {
				sendLoginResultMsg(QQLoginResultCode.FAILED);
				return;
			}

			bRet = startPollTask();					// 启动轮循消息任务

			m_QQUser.m_nStatus = m_QQUser.m_LoginResult2.m_nStatus;
			sendLoginResultMsg(QQLoginResultCode.SUCCESS);

			sendMessage(QQCallBackMsg.UPDATE_BUDDY_INFO, 0, 0, buddyInfoResult);
			sendMessage(QQCallBackMsg.UPDATE_BUDDY_SIGN, 0, 0, getSignResult);
			sendMessage(QQCallBackMsg.UPDATE_BUDDY_LIST, 0, 0, buddyListResult);
			sendMessage(QQCallBackMsg.UPDATE_GROUP_LIST, 0, 0, groupListResult);
			sendMessage(QQCallBackMsg.UPDATE_RECENT_LIST, 0, 0, recentListResult);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	// 获取用户信息
	private BuddyInfoResult getUserInfo() {
		BuddyInfoResult result = new BuddyInfoResult();
		
		boolean bRet = QQProtocol.getBuddyInfo(m_httpClient, m_QQUser.m_nQQUin, 
			m_QQUser.m_LoginResult2.m_strVfWebQq, result);
		if (!bRet || result.m_nRetCode != 0 || m_bCancel)
			return null;

		return result;
	}

	// 获取用户签名
	private GetSignResult getUserSign() {
		GetSignResult result = new GetSignResult();
		
		boolean bRet = QQProtocol.getQQSign(m_httpClient, m_QQUser.m_nQQUin, 
			m_QQUser.m_LoginResult2.m_strVfWebQq, result);
		if (!bRet || result.m_nRetCode != 0 || m_bCancel) 
			return null;

		return result;
	}

	// 获取好友列表
	private BuddyListResult getBuddyList() {
		BuddyListResult result1 = new BuddyListResult();

		boolean bRet = QQProtocol.getBuddyList(m_httpClient, 
			m_QQUser.m_nQQUin, m_QQUser.m_LoginResult1.m_strPtWebQq, 
			m_QQUser.m_LoginResult2.m_strVfWebQq, result1);
		if (!bRet || result1.m_nRetCode != 0 || m_bCancel)
			return null;

		OnlineBuddyListResult result2 = new OnlineBuddyListResult();
		bRet = QQProtocol.getOnlineBuddyList(m_httpClient, QQProtocol.WEBQQ_CLIENT_ID, 
			m_QQUser.m_LoginResult2.m_strPSessionId, result2);
		if (!bRet || result2.m_nRetCode != 0 || m_bCancel) {
			result2.reset();
			result1.reset();
			return null;
		}

		result1.setOnlineBuddyList(result2);
		result1.sortBuddy();
		result2.reset();

		return result1;
	}

	// 获取群列表
	private GroupListResult getGroupList() {
		GroupListResult result = new GroupListResult();

		boolean bRet = QQProtocol.getGroupList(m_httpClient, 
				m_QQUser.m_nQQUin, m_QQUser.m_LoginResult1.m_strPtWebQq, 
				m_QQUser.m_LoginResult2.m_strVfWebQq, result);
		if (!bRet || result.m_nRetCode != 0 || m_bCancel)
			return null;

		return result;
	}

	// 获取最近联系人列表
	private RecentListResult getRecentList() {
		RecentListResult result = new RecentListResult();

		boolean bRet = QQProtocol.getRecentList(m_httpClient, 
			m_QQUser.m_LoginResult2.m_strVfWebQq, QQProtocol.WEBQQ_CLIENT_ID,
			m_QQUser.m_LoginResult2.m_strPSessionId, result);
		if (!bRet || result.m_nRetCode != 0 || m_bCancel)
			return null;

		return result;
	}
	
	// 启动轮询任务
	private boolean startPollTask() {
		String strTaskName = "PollTask";
		PollTask task = new PollTask(strTaskName, m_httpClient.getHttpClient());
		task.m_QQUser = m_QQUser;
		task.m_recvMsgTaskMgr = m_recvMsgTaskMgr;
		return m_taskMgr.addTask(task);
	}

	private void sendLoginResultMsg(int nRetCode) {
		if (m_bCancel)
			nRetCode = QQLoginResultCode.USER_CANCEL_LOGIN;
		
		sendMessage(QQCallBackMsg.LOGIN_RESULT, nRetCode, 0, null);
	}
}
