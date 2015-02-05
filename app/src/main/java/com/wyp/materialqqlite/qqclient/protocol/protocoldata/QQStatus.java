package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class QQStatus {
	public static final int ONLINE = 10;	// 我在线上
	public static final int CALLME = 60;	// Q我吧
	public static final int AWAY = 30;	// 离开
	public static final int BUSY = 50;	// 忙碌
	public static final int SILENT = 70;	// 请匆打扰
	public static final int HIDDEN = 80;	// 隐身
	public static final int OFFLINE = 90;	// 离线
    
	public static final String STR_ONLINE	= "online";		// 我在线上
	public static final String STR_CALLME	= "callme";		// Q我吧
	public static final String STR_AWAY = "away";			// 离开
	public static final String STR_BUSY = "busy";			// 忙碌
	public static final String STR_SILENT	= "silent";		// 请匆打扰
	public static final String STR_HIDDEN	= "hidden";		// 隐身
	public static final String STR_OFFLINE = "offline";	// 离线

	public static int convertToQQStatus(String str) {
    	if (str.equals(STR_ONLINE))
    		return ONLINE;
    	else if (str.equals(STR_CALLME))
    		return CALLME;
    	else if (str.equals(STR_AWAY))
    		return AWAY;
    	else if (str.equals(STR_BUSY))
    		return BUSY;
    	else if (str.equals(STR_SILENT))
    		return SILENT;
    	else if (str.equals(STR_HIDDEN))
    		return HIDDEN;
    	else if (str.equals(STR_OFFLINE))
    		return OFFLINE;
    	else
    		return OFFLINE; 
    }

	public static String convertToQQStatusStr(int nStatus) {
		switch (nStatus) {
    	case ONLINE: return STR_ONLINE;	// 我在线上
    	case CALLME: return STR_CALLME;	// Q我吧
    	case AWAY: return STR_AWAY;		// 离开
    	case BUSY: return STR_BUSY;		// 忙碌
    	case SILENT: return STR_SILENT;	// 请匆打扰
    	case HIDDEN: return STR_HIDDEN;	// 隐身
    	case OFFLINE: return STR_OFFLINE;	// 离线
    	default: return "unknown";
    	}
    }
}
