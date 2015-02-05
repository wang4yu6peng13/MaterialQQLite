package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class QQCallBackMsg {
	public final static int LOGIN_RESULT = 100;			// 登录返回消息
	public final static int LOGOUT_RESULT = 101;			// 注销返回消息
	public final static int UPDATE_USER_INFO = 102;		// 更新用户信息
	public final static int UPDATE_BUDDY_LIST = 103;		// 更新好友列表消息
	public final static int UPDATE_GROUP_LIST = 104;		// 更新群列表消息
	public final static int UPDATE_RECENT_LIST = 105;		// 更新最近联系人列表消息
	public final static int BUDDY_MSG = 106;				// 好友消息
	public final static int GROUP_MSG = 107;				// 群消息
	public final static int SESS_MSG = 108;				// 临时会话消息
	public final static int STATUS_CHANGE_MSG = 109;		// 好友状态改变消息
	public final static int KICK_MSG = 110;				// 被踢下线消息
	public final static int SYS_GROUP_MSG = 111;			// 群系统消息
	public final static int UPDATE_BUDDY_NUMBER = 112;	// 更新好友号码
	public final static int UPDATE_GMEMBER_NUMBER = 113;	// 更新群成员号码
	public final static int UPDATE_GROUP_NUMBER = 114;	// 更新群号码
	public final static int UPDATE_BUDDY_SIGN = 115;		// 更新好友个性签名
	public final static int UPDATE_GMEMBER_SIGN = 116;	// 更新群成员个性签名
	public final static int UPDATE_BUDDY_INFO = 117;		// 更新好友信息
	public final static int UPDATE_GMEMBER_INFO = 118;	// 更新群成员信息
	public final static int UPDATE_GROUP_INFO = 119;		// 更新群信息
	public final static int UPDATE_C2CMSGSIG = 120;		// 更新临时会话信令
	public final static int UPDATE_GROUPFACESIG = 121;	// 更新群表情信令
	public final static int UPDATE_BUDDY_HEADPIC = 122;	// 更新好友头像
	public final static int UPDATE_GMEMBER_HEADPIC = 123;	// 更新群成员头像
	public final static int UPDATE_GROUP_HEADPIC = 124;	// 更新群头像
	public final static int CHANGE_STATUS_RESULT = 125;	// 改变在线状态返回消息
	
	public final static int INTERNAL_GETBUDDYDATA = 400;
	public final static int INTERNAL_GETGROUPDATA = 401;
	public final static int INTERNAL_GETGMEMBERDATA = 402;
	public final static int INTERNAL_GROUPID2CODE = 403;
}
