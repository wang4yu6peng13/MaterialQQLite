package com.wyp.materialqqlite.qqclient;
/**
 * Created by WYP on 2015/1/29.
 */
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Message;

import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.msglog.BuddyMsgLog;
import com.wyp.materialqqlite.qqclient.msglog.GroupMsgLog;
import com.wyp.materialqqlite.qqclient.msglog.MessageLogger;
import com.wyp.materialqqlite.qqclient.msglog.SessMsgLog;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfoResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyListResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyTeamInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetC2CMsgSigResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetQQNumResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetSignResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfoResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupListResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.KickMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageSender;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQMsgType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQStatus;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.RecentInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.RecentList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.RecentListResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SessMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.StatusChangeMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SysGroupMessage;
import com.wyp.materialqqlite.qqclient.task.ChangeStatusTask;
import com.wyp.materialqqlite.qqclient.task.GroupFaceSigTask;
import com.wyp.materialqqlite.qqclient.task.HeadPicTask;
import com.wyp.materialqqlite.qqclient.task.InfoTask;
import com.wyp.materialqqlite.qqclient.task.LoginTask;
import com.wyp.materialqqlite.qqclient.task.LogoutTask;
import com.wyp.materialqqlite.qqclient.task.QQNumTask;
import com.wyp.materialqqlite.qqclient.task.QQSignTask;
import com.wyp.materialqqlite.qqclient.task.SendMsgTask;
import com.wyp.materialqqlite.qqclient.task.TaskManager;

public class QQClient {
    private static final String USER_AGENT = "Mozilla/4.0 " +
            "(compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR " +
            "1.1.4322; .NET CLR 2.0.50727; InfoPath.2; Alexa Toolbar)";
    private QQUser m_QQUser = new QQUser();
    private HttpClient m_httpClient;
    private TaskManager m_taskMgr;
    private TaskManager m_sendMsgTaskMgr;
    private TaskManager m_recvMsgTaskMgr;
    private long m_lSendMsgCnt = 0;

    // 初始化客户端
    public boolean init() {
        if (null == m_httpClient) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                // 设置一些基本参数
                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                HttpProtocolParams.setUseExpectContinue(params, false);
                HttpProtocolParams.setUserAgent(params, USER_AGENT);
                // 超时设置
                // ConnManagerParams.setTimeout(params, 1000);
                HttpConnectionParams.setConnectionTimeout(params, 5000);// 连接超时(单位：毫秒)
                //HttpConnectionParams.setSoTimeout(params, 30*1000);		// 读取超时(单位：毫秒)

                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", sf, 443));

                // 设置我们的HttpClient支持HTTP和HTTPS两种模式
                //SchemeRegistry schReg = new SchemeRegistry();
                //schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                //schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

                // 使用线程安全的连接管理来创建HttpClient
                ClientConnectionManager connectionMgr = new ThreadSafeClientConnManager(params, schReg);
                m_httpClient = new DefaultHttpClient(connectionMgr, params);
            } catch (KeyStoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CertificateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KeyManagementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        m_taskMgr = new TaskManager();
        m_taskMgr.init(0);
        m_sendMsgTaskMgr = new TaskManager();
        m_sendMsgTaskMgr.init(1);
        m_recvMsgTaskMgr = new TaskManager();
        m_recvMsgTaskMgr.init(1);

        return true;
    }

    // 反初始化客户端
    public void uninit() {
        m_sendMsgTaskMgr.shutdown();
        m_recvMsgTaskMgr.shutdown();
        m_taskMgr.shutdown();
//      由于以下HttpClient关闭连接代码需要在子线程调用，否则会报android.os.NetworkOnMainThreadException，所以这里不作释放了
//		if (m_httpClient != null && m_httpClient.getConnectionManager() != null)
//			m_httpClient.getConnectionManager().shutdown();
        new Thread(){
            @Override
            public void run() {
                if (m_httpClient != null && m_httpClient.getConnectionManager() != null)
                    m_httpClient.getConnectionManager().shutdown();
                m_httpClient = null;
            }
        }.start();
        m_QQUser.reset();
    }

    // 设置QQ号码
    public void setQQNum(String strQQNum) {
        if (!isOffline())
            return;

        m_QQUser.m_strQQNum = strQQNum;
        m_QQUser.m_nQQUin = (int)Long.parseLong(strQQNum);
        m_QQUser.m_UserInfo.m_nQQUin = m_QQUser.m_nQQUin;
        m_QQUser.m_UserInfo.m_nQQNum = m_QQUser.m_nQQUin;
        m_QQUser.m_UserInfo.m_bHasQQNum = true;
    }

    // 设置QQ号码和密码
    public void setQQPwd(String strQQPwd) {
        m_QQUser.m_strQQPwd = strQQPwd;
    }

    // 设置QQ号码和密码
    public void setUser(String strQQNum, String strQQPwd) {
        if (!isOffline())
            return;

        m_QQUser.m_strQQNum = strQQNum;
        m_QQUser.m_strQQPwd = strQQPwd;
        m_QQUser.m_nQQUin =  (int)Long.parseLong(strQQNum);
        m_QQUser.m_UserInfo.m_nQQUin = m_QQUser.m_nQQUin;
        m_QQUser.m_UserInfo.m_nQQNum = m_QQUser.m_nQQUin;
        m_QQUser.m_UserInfo.m_bHasQQNum = true;
    }

    // 设置登录状态
    public void setLoginStatus(int nStatus) {
        m_QQUser.m_nLoginStatus = nStatus;
    }

    // 获取QQ号码
    public String getQQNum() {
        return m_QQUser.m_strQQNum;
    }

    // 获取QQ密码
    public String getQQPwd() {
        return m_QQUser.m_strQQPwd;
    }

    // 获取登录状态
    public int getLoginStatus() {
        return m_QQUser.m_nLoginStatus;
    }

    public void setProxyHandler(Handler handler) {
        m_QQUser.setProxyHandler(handler);
    }

    public void setNullProxyHandler(Handler handler) {
        m_QQUser.setNullProxyHandler(handler);
    }

    public boolean sendProxyMsg(int nMsgId,
                                int nArg1, int nArg2, Object obj) {
        return m_QQUser.sendProxyMsg(nMsgId, nArg1, nArg2, obj);
    }

    public void setCallBackHandler(Handler handler) {
        m_QQUser.setCallBackHandler(handler);
    }

    public void setNullCallBackHandler(Handler handler) {
        m_QQUser.setNullCallBackHandler(handler);
    }

    public boolean sendCallBackMsg(int nMsgId,
                                   int nArg1, int nArg2, Object obj) {
        return m_QQUser.sendCallBackMsg(nMsgId, nArg1, nArg2, obj);
    }

    public boolean isNullCallBack() {
        return m_QQUser.isNullCallBack();
    }

    // 设置验证码
    public void setVerifyCode(String strVerifyCode) {
        m_QQUser.m_strVerifyCode = strVerifyCode;
    }

    // 登录
    public boolean login() {
        if (!isOffline()
                || Utils.isEmptyStr(m_QQUser.m_strQQNum)
                || Utils.isEmptyStr(m_QQUser.m_strQQPwd))
            return false;

        LoginTask task = new LoginTask("LoginTask", m_httpClient);

        task.m_QQUser = m_QQUser;
        task.m_recvMsgTaskMgr = m_recvMsgTaskMgr;

        return m_taskMgr.addTask(task);
    }

    // 注销
    public boolean logout() {
        if (isOffline())
            return false;

        LogoutTask task = new LogoutTask("LogoutTask", m_httpClient);
        task.m_QQUser = m_QQUser;
        return m_taskMgr.addTask(task);
    }

    // 取消登录
    public void cancelLogin() {
        m_taskMgr.delAllTask();
        m_sendMsgTaskMgr.delAllTask();
        m_recvMsgTaskMgr.delAllTask();
    }

    // 改变在线状态
    public void changeStatus(int nStatus) {
        if (isOffline())
            return;

        String strTaskName = "ChangeStatusTask";
        ChangeStatusTask task = new ChangeStatusTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nStatus = nStatus;
        m_taskMgr.addTask(task);
    }

    // 更新好友列表
    public void updateBuddyList() {

    }

    // 更新群列表
    public void updateGroupList() {

    }

    // 更新最近联系人列表
    public void updateRecentList() {

    }

    // 更新好友信息
    public void updateBuddyInfo(int nQQUin) {
        if (isOffline())
            return;

        String strTaskName = "InfoTask_B" + nQQUin;
        InfoTask task = new InfoTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nType = InfoTask.OP_TYPE_GETBUDDYINFO;
        task.m_nGroupCode = 0;
        task.m_nQQUin = nQQUin;
        m_taskMgr.addTask(task);
    }

    // 更新群成员信息
    public void updateGroupMemberInfo(int nGroupCode, int nQQUin) {
        if (isOffline())
            return;

        String strTaskName = "InfoTask_S_" + nGroupCode + "_" + nQQUin;
        InfoTask task = new InfoTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nType = InfoTask.OP_TYPE_GETGMEMBERINFO;
        task.m_nGroupCode = nGroupCode;
        task.m_nQQUin = nQQUin;
        m_taskMgr.addTask(task);
    }

    // 更新群信息
    public void updateGroupInfo(int nGroupCode) {
        if (isOffline())
            return;

        String strTaskName = "InfoTask_G_" + nGroupCode;
        InfoTask task = new InfoTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nType = InfoTask.OP_TYPE_GETGROUPINFO;
        task.m_nGroupCode = nGroupCode;
        task.m_nQQUin = 0;
        m_taskMgr.addTask(task);
    }

    // 更新好友号码
    public void updateBuddyNum(int nQQUin) {
        if (isOffline())
            return;

        String strTaskName = "QQNumTask_B_" + nQQUin;

        QQNumTask task = new QQNumTask(strTaskName, m_httpClient);

        task.m_QQUser = m_QQUser;
        task.getBuddyNum(nQQUin);
        m_taskMgr.addTask(task);
    }

    // 更新群成员号码
    public void updateGroupMemberNum(int nGroupCode, int nQQUin) {
        if (isOffline())
            return;

        String strTaskName = "QQNumTask_S_" + nGroupCode + "_" + nQQUin;
        QQNumTask task = new QQNumTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.getGMemberNum(nGroupCode, nQQUin);
        m_taskMgr.addTask(task);
    }

    // 更新群号码
    public void updateGroupNum(int nGroupCode) {
        if (isOffline())
            return;

        String strTaskName = "QQNumTask_G_" + nGroupCode;
        QQNumTask task = new QQNumTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.getGroupNum(nGroupCode);
        m_taskMgr.addTask(task);
    }

    // 更新好友个性签名
    public void updateBuddySign(int nQQUin) {
        if (isOffline())
            return;

        String strTaskName = "QQSignTask_BG_" + nQQUin;
        QQSignTask task = new QQSignTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nType = QQSignTask.OP_TYPE_GET;
        task.addGetParam(false, 0, nQQUin);
        m_taskMgr.addTask(task);
    }

    // 更新群成员个性签名
    public void updateGroupMemberSign(int nGroupCode, int nQQUin) {
        if (isOffline())
            return;

        String strTaskName = "QQSignTask_SG_" + nGroupCode + "_" + nQQUin;
        QQSignTask task = new QQSignTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nType = QQSignTask.OP_TYPE_GET;
        task.addGetParam(true, nGroupCode, nQQUin);
        m_taskMgr.addTask(task);
    }

    // 修改QQ个性签名
    public void modifyQQSign(String strSign) {
        if (isOffline() || null == strSign)
            return;

        String strTaskName = "QQSignTask_BS";
        QQSignTask task = new QQSignTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nType = QQSignTask.OP_TYPE_SET;
        task.addSetParam(strSign);
        m_taskMgr.addTask(task);
    }

    // 更新好友头像
    public void updateBuddyHeadPic(int nQQUin, int nQQNum) {
        if (isOffline() || 0 == nQQUin || 0 == nQQNum)
            return;

        String strTaskName = "HeadPicTask_B_" + nQQUin;

        HeadPicTask task = new HeadPicTask(strTaskName, m_httpClient);

        task.m_QQUser = m_QQUser;
        task.getBuddyHeadPic(nQQUin, nQQNum);
        m_taskMgr.addTask(task);
    }

    // 更新群成员头像
    public void updateGroupMemberHeadPic(int nGroupCode, int nQQUin, int nQQNum) {
        if (isOffline() || 0 == nGroupCode || 0 == nQQUin || 0 == nQQNum)
            return;

        String strTaskName = "HeadPicTask_S_" + nGroupCode + "_" + nQQUin;

        HeadPicTask task = new HeadPicTask(strTaskName, m_httpClient);

        task.m_QQUser = m_QQUser;
        task.getGMemberHeadPic(nGroupCode, nQQUin, nQQNum);
        m_taskMgr.addTask(task);
    }

    // 更新群头像
    public void updateGroupHeadPic(int nGroupCode, int nGroupNum) {
        if (isOffline() || 0 == nGroupCode || 0 == nGroupNum)
            return;

        String strTaskName = "HeadPicTask_G_" + nGroupCode;

        HeadPicTask task = new HeadPicTask(strTaskName, m_httpClient);

        task.m_QQUser = m_QQUser;
        task.getGroupHeadPic(nGroupCode, nGroupNum);
        m_taskMgr.addTask(task);
    }

    // 更新群表情信令
    public void updateGroupFaceSignal() {
        if (isOffline())
            return;

        String strTaskName = "GroupFaceSigTask";
        GroupFaceSigTask task = new GroupFaceSigTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        m_taskMgr.addTask(task);
    }

    // 发送好友消息
    public boolean sendBuddyMsg(int nToUin, int nTime, String strMsg) {
        if (isOffline())
            return false;

        BuddyMessage buddyMsg = QQUtils.createBuddyMessage(nTime, strMsg);

        String strName = "";
        int nQQUin = nToUin;
        BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nQQUin);
        if (buddyInfo != null) {
            if (!Utils.isEmptyStr(buddyInfo.m_strMarkName))
                strName = buddyInfo.m_strMarkName;
            else
                strName = buddyInfo.m_strNickName;
        }

        m_QQUser.m_MsgList.addBuddyMsg(nQQUin, strName, buddyMsg);
        m_QQUser.m_MsgList.emptyBuddyUnreadMsgCount(nQQUin);

        String strTaskName = "SendMsgTask_" + m_lSendMsgCnt;
        m_lSendMsgCnt++;
        SendMsgTask task = new SendMsgTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nMsgType = QQMsgType.QQ_MSG_TYPE_BUDDY;
        task.addBuddyMsg(nToUin, nTime, strMsg);
        return m_sendMsgTaskMgr.addTask(task);
    }

    // 发送群消息
    public boolean sendGroupMsg(int nGroupId, int nTime, String strMsg) {
        if (isOffline())
            return false;

        GroupMessage groupMsg = QQUtils.createGroupMessage(nTime, strMsg);

        String strName = "";
        int nGroupCode = m_QQUser.m_GroupList.getGroupCodeById(nGroupId);
        GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupByCode(nGroupCode);
        if (groupInfo != null) {
            strName = groupInfo.m_strName;
        }

        m_QQUser.m_MsgList.addGroupMsg(
                nGroupCode, strName, groupMsg);
        m_QQUser.m_MsgList.emptyGroupUnreadMsgCount(nGroupCode);

        String strTaskName = "SendMsgTask_" + m_lSendMsgCnt;
        m_lSendMsgCnt++;
        SendMsgTask task = new SendMsgTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nMsgType = QQMsgType.QQ_MSG_TYPE_GROUP;
        task.addGroupMsg(nGroupId, nTime, strMsg);
        return m_sendMsgTaskMgr.addTask(task);
    }

    // 发送临时会话消息
    public boolean sendSessMsg(int nGroupId, int nToUin, int nTime, String strMsg) {
        if (isOffline())
            return false;

        SessMessage sessMsg = QQUtils.createSessMessage(nTime, strMsg);

        String strName = "";
        int nQQUin = nToUin;
        int nGroupCode = 0;

        BuddyInfo buddyInfo = null;
        GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupById(nGroupId);
        if (groupInfo != null) {
            nGroupCode = groupInfo.m_nGroupCode;
            buddyInfo = groupInfo.getMemberByUin(nQQUin);
            if (buddyInfo != null) {
                if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
                    strName = buddyInfo.m_strGroupCard;
                else
                    strName = buddyInfo.m_strNickName;
            }
        }

        m_QQUser.m_MsgList.addSessMsg(
                nGroupCode, nQQUin, strName, sessMsg);
        m_QQUser.m_MsgList.emptySessUnreadMsgCount(nGroupCode, nQQUin);

        String strTaskName = "SendMsgTask_" + m_lSendMsgCnt;
        m_lSendMsgCnt++;
        SendMsgTask task = new SendMsgTask(strTaskName, m_httpClient);
        task.m_QQUser = m_QQUser;
        task.m_nMsgType = QQMsgType.QQ_MSG_TYPE_SESS;
        task.addSessMsg(nGroupId, nToUin, nTime, strMsg);
        return m_sendMsgTaskMgr.addTask(task);
    }

    // 是否离线状态
    public boolean isOffline() {
        return (QQStatus.OFFLINE == m_QQUser.m_nStatus) ? true : false;
    }
    //
    // 获取在线状态
    public int getStatus() {
        return m_QQUser.m_nStatus;
    }

    // 获取验证码图片
    public byte[] getVerifyCodePic() {
        return m_QQUser.m_VerifyCodePic;
    }


    // 获取用户信息
    public BuddyInfo getUserInfo() {
        return m_QQUser.m_UserInfo;
    }

    // 获取好友列表
    public BuddyList getBuddyList() {
        return m_QQUser.m_BuddyList;
    }

    // 获取群列表
    public GroupList getGroupList() {
        return m_QQUser.m_GroupList;
    }

    // 获取最近联系人列表
    public RecentList getRecentList() {
        return m_QQUser.m_RecentList;
    }

    // 获取消息列表
    public MessageList getMessageList() {
        return m_QQUser.m_MsgList;
    }

    // 获取消息记录管理器
    public MessageLogger getMsgLogger() {
        return m_QQUser.m_MsgLogger;
    }

    // 获取用户文件夹存放路径
    public String getUserFolder() {
        return m_QQUser.getUserFolder();
    }

    // 设置用户文件夹存放路径
    public void setUserFolder(String strPath) {
        m_QQUser.setUserFolder(strPath);
    }

    // 获取个人文件夹存放路径
    public String getPersonalFolder(int nUserNum) {
        return m_QQUser.getPersonalFolder(nUserNum);
    }

    // 获取好友头像图片存放路径
    public String getBuddyHeadPicFolder(int nUserNum) {
        return m_QQUser.getBuddyHeadPicFolder(nUserNum);
    }

    // 获取群头像图片存放路径
    public String getGroupHeadPicFolder(int nUserNum) {
        return m_QQUser.getGroupHeadPicFolder(nUserNum);
    }

    // 获取群成员头像图片存放路径
    public String getSessHeadPicFolder(int nUserNum) {
        return m_QQUser.getSessHeadPicFolder(nUserNum);
    }

    // 获取聊天图片存放路径
    public String getChatPicFolder(int nUserNum) {
        return m_QQUser.getChatPicFolder(nUserNum);
    }

    // 获取用户头像图片全路径文件名
    public String getUserHeadPicFullName(int nUserNum) {
        return m_QQUser.getUserHeadPicFullName(nUserNum);
    }

    // 获取好友头像图片全路径文件名
    public String getBuddyHeadPicFullName(int nQQNum) {
        return m_QQUser.getBuddyHeadPicFullName(nQQNum);
    }

    // 获取群头像图片全路径文件名
    public String getGroupHeadPicFullName(int nGroupNum) {
        return m_QQUser.getGroupHeadPicFullName(nGroupNum);
    }

    // 获取群成员头像图片全路径文件名
    public String getSessHeadPicFullName(int nQQNum) {
        return m_QQUser.getSessHeadPicFullName(nQQNum);
    }

    // 获取聊天图片全路径文件名
    public String getChatPicFullName(String strFileName) {
        return m_QQUser.getChatPicFullName(strFileName);
    }

    // 获取消息记录全路径文件名
    public String getMsgLogFullName(int nUserNum) {
        return m_QQUser.getMsgLogFullName(nUserNum);
    }

//	// 判断是否需要更新好友头像
//	public boolean isNeedUpdateBuddyHeadPic(int nQQNum) {
//
//	}
//
//	// 判断是否需要更新群头像
//	public boolean isNeedUpdateGroupHeadPic(int nGroupNum) {
//
//	}
//
//	// 判断是否需要更新群成员头像
//	public boolean isNeedUpdateSessHeadPic(int nQQNum) {
//
//	}

//	// 获取服务器时间
//	public int GetServerTime() {
//
//	}

    public QQUser getQQUser() {
        return m_QQUser;
    }

    public void handleProxyMsg(Message msg) {
        switch (msg.what) {
            case QQCallBackMsg.LOGIN_RESULT:			// 登录返回消息
            case QQCallBackMsg.LOGOUT_RESULT:			// 注销返回消息
            case QQCallBackMsg.UPDATE_USER_INFO:		// 更新用户信息
            case QQCallBackMsg.UPDATE_BUDDY_HEADPIC:	// 更新好友头像
            case QQCallBackMsg.UPDATE_GMEMBER_HEADPIC:	// 更新群成员头像
            case QQCallBackMsg.UPDATE_GROUP_HEADPIC:	// 更新群头像
                sendCallBackMsg(msg.what, msg.arg1, msg.arg2, msg.obj);
                break;
            case QQCallBackMsg.UPDATE_BUDDY_LIST:		// 更新好友列表消息
                onUpdateBuddyList(msg);
                break;
            case QQCallBackMsg.UPDATE_GROUP_LIST:		// 更新群列表消息
                onUpdateGroupList(msg);
                break;
            case QQCallBackMsg.UPDATE_RECENT_LIST:		// 更新最近联系人列表消息
                onUpdateRecentList(msg);
                break;
            case QQCallBackMsg.BUDDY_MSG:				// 好友消息
                onBuddyMsg(msg);
                break;
            case QQCallBackMsg.GROUP_MSG:				// 群消息
                onGroupMsg(msg);
                break;
            case QQCallBackMsg.SESS_MSG:				// 临时会话消息
                onSessMsg(msg);
                break;
            case QQCallBackMsg.STATUS_CHANGE_MSG:		// 好友状态改变消息
                onStatusChangeMsg(msg);
                break;
            case QQCallBackMsg.KICK_MSG:				// 被踢下线消息
                onKickMsg(msg);
                break;
            case QQCallBackMsg.SYS_GROUP_MSG:			// 群系统消息
                onSysGroupMsg(msg);
                break;
            case QQCallBackMsg.UPDATE_BUDDY_NUMBER:	// 更新好友号码
                onUpdateBuddyNumber(msg);
                break;
            case QQCallBackMsg.UPDATE_GMEMBER_NUMBER:	// 更新群成员号码
                onUpdateGMemberNumber(msg);
                break;
            case QQCallBackMsg.UPDATE_GROUP_NUMBER:	// 更新群号码
                onUpdateGroupNumber(msg);
                break;
            case QQCallBackMsg.UPDATE_BUDDY_SIGN:		// 更新好友个性签名
                onUpdateBuddySign(msg);
                break;
            case QQCallBackMsg.UPDATE_GMEMBER_SIGN:	// 更新群成员个性签名
                onUpdateGMemberSign(msg);
                break;
            case QQCallBackMsg.UPDATE_BUDDY_INFO:		// 更新好友信息
                onUpdateBuddyInfo(msg);
                break;
            case QQCallBackMsg.UPDATE_GMEMBER_INFO:	// 更新群成员信息
                onUpdateGMemberInfo(msg);
                break;
            case QQCallBackMsg.UPDATE_GROUP_INFO:		// 更新群信息
                onUpdateGroupInfo(msg);
                break;
            case QQCallBackMsg.UPDATE_C2CMSGSIG:		// 更新临时会话信令
                onUpdateC2CMsgSig(msg);
                break;
            case QQCallBackMsg.CHANGE_STATUS_RESULT:	// 改变在线状态返回消息
                onChangeStatusResult(msg);
                break;

            case QQCallBackMsg.INTERNAL_GETBUDDYDATA:
                onInternal_GetBuddyData(msg);
                break;
            case QQCallBackMsg.INTERNAL_GETGROUPDATA:
                onInternal_GetGroupData(msg);
                break;
            case QQCallBackMsg.INTERNAL_GETGMEMBERDATA:
                onInternal_GetGMemberData(msg);
                break;
            case QQCallBackMsg.INTERNAL_GROUPID2CODE:
                onInternal_GroupId2Code(msg);
                break;

            default:
                break;
        }
    }

    private void onUpdateBuddyList(Message msg) {
        boolean bSuccess = false;
        if (msg.obj != null) {
            m_QQUser.m_BuddyList.reset();
            BuddyListResult result = (BuddyListResult)msg.obj;
            for (int i = 0; i < result.m_arrBuddyTeamInfo.size(); i++) {
                BuddyTeamInfo buddyTeamInfo = result.m_arrBuddyTeamInfo.get(i);
                if (buddyTeamInfo != null)
                    m_QQUser.m_BuddyList.addBuddyTeam(buddyTeamInfo);
            }
            result.m_arrBuddyTeamInfo.clear();
            bSuccess = true;
        }
        sendCallBackMsg(msg.what, bSuccess ? 1 : 0, 0, null);
    }

    private void onUpdateGroupList(Message msg) {
        boolean bSuccess = false;
        if (msg.obj != null) {
            m_QQUser.m_GroupList.reset();
            GroupListResult result = (GroupListResult)msg.obj;
            for (int i = 0; i < result.m_arrGroupInfo.size(); i++)
            {
                GroupInfo groupInfo = result.m_arrGroupInfo.get(i);
                if (groupInfo != null)
                    m_QQUser.m_GroupList.addGroup(groupInfo);
            }
            result.m_arrGroupInfo.clear();
            bSuccess = true;
        }
        sendCallBackMsg(msg.what, bSuccess ? 1 : 0, 0, null);
    }

    private void onUpdateRecentList(Message msg) {
        boolean bSuccess = false;
        if (msg.obj != null) {
            m_QQUser.m_RecentList.reset();
            RecentListResult result = (RecentListResult)msg.obj;
            for (int i = 0; i < result.m_arrRecentInfo.size(); i++) {
                RecentInfo recentInfo = result.m_arrRecentInfo.get(i);
                if (recentInfo != null)
                    m_QQUser.m_RecentList.addRecent(recentInfo);

                if (0 == recentInfo.m_nType) {	// 好友
                    BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(recentInfo.m_nQQUin);
                    if (buddyInfo != null) {
                        MessageSender msgSender = m_QQUser.m_MsgList.getBuddyMsgSender(buddyInfo.m_nQQUin);
                        if (null == msgSender) {
                            msgSender = new MessageSender();
                            msgSender.m_nType = MessageSender.BUDDY;
                            msgSender.m_nGroupCode = 0;
                            msgSender.m_nQQUin = buddyInfo.m_nQQUin;
                            if (!Utils.isEmptyStr(buddyInfo.m_strMarkName))
                                msgSender.m_strName = buddyInfo.m_strMarkName;
                            else
                                msgSender.m_strName = buddyInfo.m_strNickName;
                            msgSender.m_nUnreadMsgCnt = 0;
                            m_QQUser.m_MsgList.addMsgSender(msgSender);
                        }
                    }
                } else if (1 == recentInfo.m_nType) {	// 群
                    GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupById(recentInfo.m_nQQUin);
                    if (groupInfo != null) {
                        MessageSender msgSender = m_QQUser.m_MsgList.getGroupMsgSender(groupInfo.m_nGroupCode);
                        if (null == msgSender) {
                            msgSender = new MessageSender();
                            msgSender.m_nType = MessageSender.GROUP;
                            msgSender.m_nGroupCode = groupInfo.m_nGroupCode;
                            msgSender.m_nQQUin = 0;
                            msgSender.m_strName = groupInfo.m_strName;
                            msgSender.m_nUnreadMsgCnt = 0;
                            m_QQUser.m_MsgList.addMsgSender(msgSender);
                        }
                    }
                }
            }
            result.m_arrRecentInfo.clear();
            bSuccess = true;
        }
        sendCallBackMsg(msg.what, bSuccess ? 1 : 0, 0, null);
    }

    private void onBuddyMsg(Message msg) {
        if (null == msg.obj)
            return;

        BuddyMessage buddyMsg = (BuddyMessage)msg.obj;

        int nQQUin = buddyMsg.m_nFromUin;
        BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nQQUin);
        if (null == buddyInfo)
            return;

        String strName;
        if (!Utils.isEmptyStr(buddyInfo.m_strMarkName))
            strName = buddyInfo.m_strMarkName;
        else
            strName = buddyInfo.m_strNickName;

        m_QQUser.m_MsgList.addBuddyMsg(nQQUin, strName, buddyMsg);

        sendCallBackMsg(msg.what, nQQUin, 0, msg.obj);
    }

    private void onGroupMsg(Message msg) {
        if (null == msg.obj)
            return;

        GroupMessage groupMsg = (GroupMessage)msg.obj;

        int nGroupCode = groupMsg.m_nGroupCode;
        GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupByCode(nGroupCode);
        if (null == groupInfo)
            return;

        m_QQUser.m_MsgList.addGroupMsg(
                nGroupCode, groupInfo.m_strName, groupMsg);

        sendCallBackMsg(msg.what, nGroupCode, 0, msg.obj);
    }

    private void onSessMsg(Message msg) {
        if (null == msg.obj)
            return;

        SessMessage sessMsg = (SessMessage)msg.obj;

        int nQQUin = sessMsg.m_nFromUin;
        int nGroupId = sessMsg.m_nGroupId;
        int nGroupCode = 0;

        BuddyInfo buddyInfo = null;
        GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupById(nGroupId);
        if (null == groupInfo)
            return;

        nGroupCode = groupInfo.m_nGroupCode;
        buddyInfo = groupInfo.getMemberByUin(nQQUin);
        if (null == buddyInfo) {
            buddyInfo = new BuddyInfo();
            if (buddyInfo != null) {
                buddyInfo.reset();
                buddyInfo.m_nQQUin = sessMsg.m_nFromUin;
                buddyInfo.m_nQQNum = sessMsg.m_nQQNum;
                groupInfo.m_arrMember.add(buddyInfo);
            }
            updateGroupMemberInfo(nGroupCode, sessMsg.m_nFromUin);
        }

        String strName;
        if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
            strName = buddyInfo.m_strGroupCard;
        else
            strName = buddyInfo.m_strNickName;

        m_QQUser.m_MsgList.addSessMsg(
                nGroupCode, nQQUin, strName, sessMsg);

        sendCallBackMsg(msg.what, nGroupCode, nQQUin, msg.obj);
    }

    private void onSysGroupMsg(Message msg) {
        if (null == msg.obj)
            return;

        SysGroupMessage sysGroupMsg = (SysGroupMessage)msg.obj;

        int nGroupCode = sysGroupMsg.m_nGroupCode;

//		m_QQUser.m_MsgList.addSystemMsg(nGroupCode, sysGroupMsg);

//		if (sysGroupMsg.m_strSubType.equals("group_request_join_agree")) {
//			msgSender.m_strLastMsg = "某人已同意你加入群 xx群";
//		} else if (sysGroupMsg.m_strSubType.equals("group_request_join_deny")) {
//			msgSender.m_strLastMsg = "某人把绝你加入群 xx群";
//		} else if (sysGroupMsg.m_strSubType.equals("group_leave")) {
//			msgSender.m_strLastMsg = "某人已把你踢出群 xx群";
//		}

        sendCallBackMsg(msg.what, nGroupCode, 0, msg.obj);
    }

    private void onStatusChangeMsg(Message msg) {
        if (null == msg.obj)
            return;

        StatusChangeMessage statusChangeMsg = (StatusChangeMessage)msg.obj;

        int nQQUin = statusChangeMsg.m_nQQUin;
        BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nQQUin);
        if (buddyInfo != null) {
            buddyInfo.m_nStatus = statusChangeMsg.m_nStatus;
            buddyInfo.m_nClientType = statusChangeMsg.m_nClientType;
            BuddyTeamInfo buddyTeamInfo = m_QQUser.m_BuddyList.getBuddyTeam(buddyInfo.m_nTeamIndex);
            if (buddyTeamInfo != null)
                buddyTeamInfo.sort();
        }
        sendCallBackMsg(msg.what, nQQUin, 0, null);
    }

    private void onKickMsg(Message msg) {
        if (null == msg.obj)
            return;

        KickMessage kickMsg = (KickMessage)msg.obj;

        m_QQUser.m_nStatus = QQStatus.OFFLINE;
        m_taskMgr.shutdown();
        m_sendMsgTaskMgr.shutdown();
        m_recvMsgTaskMgr.shutdown();
        sendCallBackMsg(msg.what, 0, 0, null);
    }

    private void onUpdateBuddyNumber(Message msg) {
        int nQQUin = 0;
        if (msg.obj != null) {
            GetQQNumResult result = (GetQQNumResult)msg.obj;
            nQQUin = result.m_nQQUin;
            BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nQQUin);
            if (buddyInfo != null)
                buddyInfo.setQQNum(result);
            MessageSender msgSender = m_QQUser.m_MsgList.getBuddyMsgSender(nQQUin);
            if (msgSender != null && null == msgSender.m_objLastMsg) {
                BuddyMsgLog msgLog = m_QQUser.m_MsgLogger.
                        readLastBuddyMsgLog(result.m_nQQNum);
                msgSender.m_objLastMsg = QQUtils.createBuddyMessage(msgLog);
            }
        }
        sendCallBackMsg(msg.what, nQQUin, 0, null);
    }

    private void onUpdateGMemberNumber(Message msg) {
        int nGroupCode = msg.arg1;
        int nQQUin = 0;
        if (nGroupCode != 0 && msg.obj != null) {
            GetQQNumResult result = (GetQQNumResult)msg.obj;
            nQQUin = result.m_nQQUin;
            BuddyInfo buddyInfo = m_QQUser.m_GroupList.getGroupMemberByCode(nGroupCode, nQQUin);
            if (buddyInfo != null)
                buddyInfo.setQQNum(result);
            MessageSender msgSender = m_QQUser.m_MsgList.getSessMsgSender(nGroupCode, nQQUin);
            if (msgSender != null && null == msgSender.m_objLastMsg) {
                SessMsgLog msgLog = m_QQUser.m_MsgLogger.
                        readLastSessMsgLog(result.m_nQQNum);
                msgSender.m_objLastMsg = QQUtils.createSessMessage(msgLog);
            }
        }
        sendCallBackMsg(msg.what, nGroupCode, nQQUin, null);
    }

    private void onUpdateGroupNumber(Message msg) {
        int nGroupCode = msg.arg1;
        if (nGroupCode != 0 && msg.obj != null) {
            GetQQNumResult result = (GetQQNumResult)msg.obj;
            GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupByCode(nGroupCode);
            if (groupInfo != null)
                groupInfo.setGroupNumber(result);
            MessageSender msgSender = m_QQUser.m_MsgList.getGroupMsgSender(nGroupCode);
            if (msgSender != null && null == msgSender.m_objLastMsg) {
                GroupMsgLog msgLog = m_QQUser.m_MsgLogger.
                        readLastGroupMsgLog(result.m_nQQNum);
                msgSender.m_objLastMsg = QQUtils.createGroupMessage(msgLog);
            }
        }
        sendCallBackMsg(msg.what, nGroupCode, 0, null);
    }

    private void onUpdateBuddySign(Message msg) {
        int nQQUin = 0;
        if (msg.obj != null) {
            GetSignResult result = (GetSignResult)msg.obj;
            nQQUin = result.m_nQQUin;
            if (m_QQUser.m_UserInfo.m_nQQUin == nQQUin)		// 更新用户个性签名
            {
                m_QQUser.m_UserInfo.setQQSign(result);
            }
            else											// 更新好友个性签名
            {
                BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nQQUin);
                if (buddyInfo != null)
                    buddyInfo.setQQSign(result);
            }
        }
        sendCallBackMsg(msg.what, nQQUin, 0, null);
    }

    private void onUpdateGMemberSign(Message msg) {
        int nGroupCode = msg.arg1;
        int nQQUin = 0;
        if (nGroupCode != 0 && msg.obj != null) {
            GetSignResult result = (GetSignResult)msg.obj;
            nQQUin = result.m_nQQUin;
            BuddyInfo buddyInfo = m_QQUser.m_GroupList.getGroupMemberByCode(nGroupCode, nQQUin);
            if (buddyInfo != null)
                buddyInfo.setQQSign(result);
        }
        sendCallBackMsg(msg.what, nGroupCode, nQQUin, null);
    }

    private void onUpdateBuddyInfo(Message msg) {
        int nQQUin = 0;
        if (msg.obj != null) {
            BuddyInfoResult result = (BuddyInfoResult)msg.obj;
            nQQUin = result.m_nQQUin;

            if (m_QQUser.m_UserInfo.m_nQQUin == nQQUin)	// 更新用户信息
            {
                m_QQUser.m_UserInfo.setBuddyInfo(result);
            }
            else										// 更新好友信息
            {
                BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nQQUin);
                if (buddyInfo != null)
                    buddyInfo.setBuddyInfo(result);
            }
        }
        sendCallBackMsg(msg.what, nQQUin, 0, null);
    }

    private void onUpdateGMemberInfo(Message msg) {
        int nGroupCode = msg.arg1;
        int nQQUin = 0;
        if (nGroupCode != 0 && msg.obj != null) {
            BuddyInfoResult result = (BuddyInfoResult)msg.obj;
            nQQUin = result.m_nQQUin;
            BuddyInfo buddyInfo = m_QQUser.m_GroupList.getGroupMemberByCode(nGroupCode, nQQUin);
            if (buddyInfo != null)
                buddyInfo.setBuddyInfo(result);
        }
        sendCallBackMsg(msg.what, nGroupCode, nQQUin, null);
    }

    private void onUpdateGroupInfo(Message msg) {
        int nGroupCode = 0;
        if (msg.obj != null) {
            GroupInfoResult result = (GroupInfoResult)msg.obj;
            nGroupCode = result.m_nGroupCode;
            GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupByCode(nGroupCode);
            if (groupInfo != null)
                groupInfo.setGroupInfo(result);
        }
        sendCallBackMsg(msg.what, nGroupCode, 0, null);
    }

    private void onUpdateC2CMsgSig(Message msg) {
        int nGroupId = 0;
        int nQQUin = 0;
        if (msg.obj != null) {
            GetC2CMsgSigResult result = (GetC2CMsgSigResult)msg.obj;
            nGroupId = result.m_nGroupId;
            nQQUin = result.m_nQQUin;
            GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupById(nGroupId);
            if (groupInfo != null) {
                BuddyInfo buddyInfo = groupInfo.getMemberByUin(nQQUin);
                if (buddyInfo != null)
                    buddyInfo.m_strGroupSig = result.m_strValue;
            }
        }
        sendCallBackMsg(msg.what, nGroupId, nQQUin, null);
    }

    private void onChangeStatusResult(Message msg) {
        boolean bSuccess = (msg.arg1 != 0 ? true : false);
        int nNewStatus = msg.arg2;

        if (bSuccess)
            m_QQUser.m_nStatus = nNewStatus;

        sendCallBackMsg(msg.what, msg.arg1, msg.arg2, msg.obj);
    }

    private void onInternal_GetBuddyData(Message msg) {
        m_QQUser.m_internalData.m_nQQNum = 0;
        m_QQUser.m_internalData.m_strNickName = null;

        int nQQUin = msg.arg1;

        BuddyInfo buddyInfo = m_QQUser.m_BuddyList.getBuddy(nQQUin);
        if (null == buddyInfo) {
            m_QQUser.notifyProxyMsg();
            return;
        }

        m_QQUser.m_internalData.m_nQQNum = buddyInfo.m_nQQNum;

        if (!Utils.isEmptyStr(buddyInfo.m_strMarkName))
            m_QQUser.m_internalData.m_strNickName = new String(buddyInfo.m_strMarkName);
        else if (buddyInfo.m_strNickName != null)
            m_QQUser.m_internalData.m_strNickName = new String(buddyInfo.m_strNickName);

        m_QQUser.notifyProxyMsg();
    }

    private void onInternal_GetGroupData(Message msg) {
        m_QQUser.m_internalData.m_bHasGroupInfo = false;
        m_QQUser.m_internalData.m_nGroupNum = 0;

        int nGroupCode = msg.arg1;

        GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupByCode(nGroupCode);
        if (null == groupInfo) {
            m_QQUser.notifyProxyMsg();
            return;
        }

        m_QQUser.m_internalData.m_bHasGroupInfo = groupInfo.m_bHasGroupInfo;
        m_QQUser.m_internalData.m_nGroupNum = groupInfo.m_nGroupNumber;

        m_QQUser.notifyProxyMsg();
    }

    private void onInternal_GetGMemberData(Message msg) {
        m_QQUser.m_internalData.m_nQQNum = 0;
        m_QQUser.m_internalData.m_strNickName = null;

        int nGroupCode = msg.arg1;
        int nQQUin = msg.arg2;

        GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupByCode(nGroupCode);
        if (null == groupInfo) {
            m_QQUser.notifyProxyMsg();
            return;
        }

        BuddyInfo buddyInfo = groupInfo.getMemberByUin(nQQUin);
        if (null == buddyInfo) {
            m_QQUser.notifyProxyMsg();
            return;
        }

        m_QQUser.m_internalData.m_nQQNum = buddyInfo.m_nQQNum;

        if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
            m_QQUser.m_internalData.m_strNickName = new String(buddyInfo.m_strGroupCard);
        else if (buddyInfo.m_strNickName != null)
            m_QQUser.m_internalData.m_strNickName = new String(buddyInfo.m_strNickName);

        m_QQUser.notifyProxyMsg();
    }

    private void onInternal_GroupId2Code(Message msg) {
        GroupInfo groupInfo = m_QQUser.m_GroupList.getGroupById(msg.arg1);
        if (groupInfo != null)
            m_QQUser.m_internalData.m_nGroupCode = groupInfo.m_nGroupCode;
        else
            m_QQUser.m_internalData.m_nGroupCode = 0;
        m_QQUser.notifyProxyMsg();
    }
}
