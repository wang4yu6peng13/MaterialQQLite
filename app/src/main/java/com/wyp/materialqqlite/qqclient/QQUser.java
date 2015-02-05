package com.wyp.materialqqlite.qqclient;

/**
 * Created by WYP on 2015/1/29.
 */


        import android.os.Handler;
        import android.os.Message;

        import com.wyp.materialqqlite.Utils;
        import com.wyp.materialqqlite.qqclient.msglog.MessageLogger;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyList;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.LoginResult_1;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.LoginResult_2;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageList;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQStatus;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.RecentList;
        import com.wyp.materialqqlite.qqclient.protocol.protocoldata.VerifyCodeInfo;

public class QQUser {
    public String m_strQQNum = "";
    public String m_strQQPwd = "";
    public int m_nQQUin;
    public int m_nStatus;
    public int m_nLoginStatus;
    public byte[] m_VerifyCodePic;
    public String m_strVerifyCode = "";
    public VerifyCodeInfo m_VerifyCodeInfo;
    public LoginResult_1 m_LoginResult1;
    public LoginResult_2 m_LoginResult2;
    private Handler m_handlerProxy;
    private Handler m_handlerCallBack;
    String m_strUserFolder = "";
    public BuddyInfo m_UserInfo;
    public BuddyList m_BuddyList;
    public GroupList m_GroupList;
    RecentList m_RecentList;
    public MessageList m_MsgList;
    public MessageLogger m_MsgLogger;
    public InternalData m_internalData;
    private Object m_objLock1;
    private Object m_objLock2;

    public class InternalData {
        public int m_nQQNum;
        public String m_strNickName;
        public int m_nGroupNum;
        public int m_nGroupCode;
        public boolean m_bHasGroupInfo;
    }

    public QQUser() {
        m_strQQNum = "";
        m_strQQPwd = "";
        m_nQQUin = 0;
        m_nStatus = QQStatus.OFFLINE;
        m_nLoginStatus = QQStatus.ONLINE;
        m_VerifyCodePic = null;
        m_strVerifyCode = "";
        m_VerifyCodeInfo = new VerifyCodeInfo();
        m_LoginResult1 = new LoginResult_1();
        m_LoginResult2 = new LoginResult_2();
        m_handlerProxy = null;
        m_handlerCallBack = null;
        m_strUserFolder = "";
        m_UserInfo = new BuddyInfo();
        m_BuddyList = new BuddyList();
        m_GroupList = new GroupList();
        m_RecentList = new RecentList();
        m_MsgList = new MessageList();
        m_MsgLogger = new MessageLogger();
        m_internalData = new InternalData();
        m_objLock1 = new Object();
        m_objLock2 = new Object();
    }

    public void reset() {
//		m_strQQNum = "";
//		m_strQQPwd = "";
//		m_nQQUin;
//		m_nStatus;
//		m_nLoginStatus;
//		m_VerifyCodePic = null;
//		m_strVerifyCode = "";
//		m_VerifyCodeInfo
//		m_LoginResult1
//		m_LoginResult2
//		private Handler m_handlerProxy;
//		private Handler m_handlerCallBack;
//		String m_strUserFolder = "";
        m_UserInfo.reset();
        m_BuddyList.reset();
        m_GroupList.reset();
        m_RecentList.reset();
        m_MsgList.reset();
        m_MsgLogger.close();
//		public InternalData m_internalData;
//		private Object m_objLock1;
//		private Object m_objLock2;
    }

    public void setProxyHandler(Handler handler) {
        synchronized (m_objLock1) {
            m_handlerProxy = handler;
        }
    }

    public void setNullProxyHandler(Handler handler) {
        if (null == handler)
            return;

        synchronized (m_objLock1) {
            // 只有代理Handler相同的情况下才允许置空
            if (handler == m_handlerProxy) {
                m_handlerProxy.removeCallbacksAndMessages(null);
                m_handlerProxy = null;
            }
        }
    }

    public boolean sendProxyMsg(int nMsgId,
                                int nArg1, int nArg2, Object obj) {
        synchronized (m_objLock1) {
            if (m_handlerProxy != null) {
                Message msg = m_handlerProxy.obtainMessage();
                msg.what = nMsgId;
                msg.arg1 = nArg1;
                msg.arg2 = nArg2;
                msg.obj = obj;
                return m_handlerProxy.sendMessage(msg);
            }
            return false;
        }
    }

    public boolean sendProxyMsg(int nMsgId,
                                int nArg1, int nArg2, Object obj, boolean bWait) {
        boolean bRet = false;
        try {
            synchronized (m_objLock1) {
                if (m_handlerProxy != null) {
                    Message msg = m_handlerProxy.obtainMessage();
                    msg.what = nMsgId;
                    msg.arg1 = nArg1;
                    msg.arg2 = nArg2;
                    msg.obj = obj;
                    bRet = m_handlerProxy.sendMessage(msg);

                    if (bWait)
                        m_objLock1.wait();
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bRet;
    }

    public void notifyProxyMsg() {
        synchronized (m_objLock1) {
            m_objLock1.notify();
        }
    }

    public void setCallBackHandler(Handler handler) {
        synchronized (m_objLock2) {
            m_handlerCallBack = handler;
        }
    }

    public void setNullCallBackHandler(Handler handler) {
        if (null == handler)
            return;

        synchronized (m_objLock2) {
            if (handler == m_handlerCallBack) {
                m_handlerCallBack.removeCallbacksAndMessages(null);
                m_handlerCallBack = null;
            }
        }
    }

    public boolean sendCallBackMsg(int nMsgId,
                                   int nArg1, int nArg2, Object obj) {
        synchronized (m_objLock2) {
            if (m_handlerCallBack != null) {
                Message msg = m_handlerCallBack.obtainMessage();
                msg.what = nMsgId;
                msg.arg1 = nArg1;
                msg.arg2 = nArg2;
                msg.obj = obj;
                return m_handlerCallBack.sendMessage(msg);
            }
            return false;
        }
    }

    public boolean isNullCallBack() {
        synchronized (m_objLock2) {
            return (null == m_handlerCallBack);
        }
    }

    // 获取用户文件夹存放路径
    public String getUserFolder() {
        return m_strUserFolder;
    }

    // 设置用户文件夹存放路径
    public void setUserFolder(String strPath) {
        m_strUserFolder = strPath;
    }

    // 获取个人文件夹存放路径
    public String getPersonalFolder(int nUserNum) {
        if (0 == nUserNum)
            nUserNum = m_nQQUin;

        return m_strUserFolder + Utils.getUInt(nUserNum) + "/";
    }

    // 获取好友头像图片存放路径
    public String getBuddyHeadPicFolder(int nUserNum) {
        return getPersonalFolder(nUserNum) + "HeadImage/Buddy/";
    }

    // 获取群头像图片存放路径
    public String getGroupHeadPicFolder(int nUserNum) {
        return getPersonalFolder(nUserNum) + "HeadImage/Group/";
    }

    // 获取群成员头像图片存放路径
    public String getSessHeadPicFolder(int nUserNum) {
        return getPersonalFolder(nUserNum) + "HeadImage/Sess/";
    }

    // 获取聊天图片存放路径
    public String getChatPicFolder(int nUserNum) {
        return getPersonalFolder(nUserNum) + "ChatImage/";
    }

    // 获取用户头像图片全路径文件名
    public String getUserHeadPicFullName(int nUserNum) {
        if (0 == nUserNum)
            nUserNum = m_nQQUin;

        return getPersonalFolder(nUserNum) + "HeadImage/Buddy/" + Utils.getUInt(nUserNum) + ".png";
    }

    // 获取好友头像图片全路径文件名
    public String getBuddyHeadPicFullName(int nQQNum) {
        return getPersonalFolder(0) + "HeadImage/Buddy/" + Utils.getUInt(nQQNum) + ".png";
    }

    // 获取群头像图片全路径文件名
    public String getGroupHeadPicFullName(int nGroupNum) {
        return getPersonalFolder(0) + "HeadImage/Group/" + Utils.getUInt(nGroupNum) + ".png";
    }

    // 获取群成员头像图片全路径文件名
    public String getSessHeadPicFullName(int nQQNum) {
        return getPersonalFolder(0) + "HeadImage/Sess/" + Utils.getUInt(nQQNum) + ".png";
    }

    // 获取聊天图片全路径文件名
    public String getChatPicFullName(String strFileName) {
        if (null == strFileName || strFileName.length() <= 0)
            return "";
        return getChatPicFolder(m_nQQUin) + strFileName;
    }

    // 获取消息记录全路径文件名
    public String getMsgLogFullName(int nUserNum) {
        return getPersonalFolder(nUserNum) + "Msg.db";
    }
}