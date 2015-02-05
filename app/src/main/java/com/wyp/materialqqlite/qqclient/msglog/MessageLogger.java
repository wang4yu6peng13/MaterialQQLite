package com.wyp.materialqqlite.qqclient.msglog;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageLogger {
	private SQLiteDatabase m_DB;
	
	synchronized public boolean open(String strPath) {
		try {
			close();
			m_DB = SQLiteDatabase.openOrCreateDatabase(strPath, null);
			if (m_DB != null) {
				// 创建好友消息表
				m_DB.execSQL("CREATE TABLE IF NOT EXISTS [tb_BuddyMsg] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [uin] INTEGER, [nickname] TEXT, [time] INTEGER, [sendflag] INTEGER, [content] TEXT)");

				// 创建群消息表
				m_DB.execSQL("CREATE TABLE IF NOT EXISTS [tb_GroupMsg] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [groupnum] INTEGER, [uin] INTEGER, [nickname] TEXT, [time] INTEGER, [content] TEXT)");

				// 创建临时会话(群成员)消息表
				m_DB.execSQL("CREATE TABLE IF NOT EXISTS [tb_SessMsg] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [uin] INTEGER, [nickname] TEXT, [time] INTEGER, [sendflag] INTEGER, [content] TEXT)");
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
								
		return false;
	}
	
	synchronized public void close() {
		if (m_DB != null && m_DB.isOpen())
			m_DB.close();
	}
	
	synchronized public boolean isOpen() {
		return (m_DB != null && m_DB.isOpen());
	}
	
	// 写入一条好友消息记录
	synchronized public boolean writeBuddyMsgLog(int nQQNum, 
			String strNickName, int nTime, boolean bSendFlag, String strContent) {
		BuddyMsgLog msgLog = new BuddyMsgLog();
		msgLog.m_nQQNum = nQQNum;
		msgLog.m_strNickName = strNickName;
		msgLog.m_nTime = nTime;
		msgLog.m_bSendFlag = bSendFlag;
		msgLog.m_strContent = strContent;
		return writeBuddyMsgLog(msgLog);
	}

	// 写入一条群消息记录
	synchronized public boolean writeGroupMsgLog(int nGroupNum, 
			int nQQNum, String strNickName, int nTime, String strContent) {
		GroupMsgLog msgLog = new GroupMsgLog();
		msgLog.m_nGroupNum = nGroupNum;
		msgLog.m_nQQNum = nQQNum;
		msgLog.m_strNickName = strNickName;
		msgLog.m_nTime = nTime;
		msgLog.m_strContent = strContent;
		return writeGroupMsgLog(msgLog);
	}

	// 写入一条临时会话(群成员)消息记录
	synchronized public boolean writeSessMsgLog(int nQQNum, 
			String strNickName, int nTime, boolean bSendFlag, String strContent) {
		SessMsgLog msgLog = new SessMsgLog();
		msgLog.m_nQQNum = nQQNum;
		msgLog.m_strNickName = strNickName;
		msgLog.m_nTime = nTime;
		msgLog.m_bSendFlag = bSendFlag;
		msgLog.m_strContent = strContent;
		return writeSessMsgLog(msgLog);
	}

	// 写入一条好友消息记录
	synchronized public boolean writeBuddyMsgLog(BuddyMsgLog msgLog) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		ContentValues values = new ContentValues();
		values.put("uin", msgLog.m_nQQNum);
		values.put("nickname", msgLog.m_strNickName);
		values.put("time", msgLog.m_nTime);
		values.put("sendflag", msgLog.m_bSendFlag);
		values.put("content", msgLog.m_strContent);
		
		long lRowId = m_DB.insert("tb_BuddyMsg", null, values);
		return (lRowId != -1) ? true : false;
	}

	// 写入一条群消息记录
	synchronized public boolean writeGroupMsgLog(GroupMsgLog msgLog) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		ContentValues values = new ContentValues();
		values.put("groupnum", msgLog.m_nGroupNum);
		values.put("uin", msgLog.m_nQQNum);
		values.put("nickname", msgLog.m_strNickName);
		values.put("time", msgLog.m_nTime);
		values.put("content", msgLog.m_strContent);
		
		long lRowId = m_DB.insert("tb_GroupMsg", null, values);
		return (lRowId != -1) ? true : false;
	}

	// 写入一条临时会话(群成员)消息记录
	synchronized public boolean writeSessMsgLog(SessMsgLog msgLog) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		ContentValues values = new ContentValues();
		values.put("uin", msgLog.m_nQQNum);
		values.put("nickname", msgLog.m_strNickName);
		values.put("time", msgLog.m_nTime);
		values.put("sendflag", msgLog.m_bSendFlag);
		values.put("content", msgLog.m_strContent);
		
		long lRowId = m_DB.insert("tb_SessMsg", null, values);
		return (lRowId != -1) ? true : false;
	}

	// 读出最后一条好友消息记录
	synchronized public BuddyMsgLog readLastBuddyMsgLog(int nQQNum) {
		if (null == m_DB || !m_DB.isOpen())
			return null;
		
		String strSql = "SELECT * FROM [tb_BuddyMsg] WHERE [uin]=? ORDER BY [time] DESC LIMIT 0,1";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nQQNum)});
		
		BuddyMsgLog msgLog = new BuddyMsgLog();
		while (cursor.moveToNext()) {
            msgLog.m_nID = cursor.getInt(cursor.getColumnIndex("id"));
            msgLog.m_nQQNum = cursor.getInt(cursor.getColumnIndex("uin"));
			msgLog.m_strNickName = cursor.getString(cursor.getColumnIndex("nickname"));
			msgLog.m_nTime = cursor.getInt(cursor.getColumnIndex("time"));
			msgLog.m_bSendFlag = (cursor.getInt(cursor.getColumnIndex("sendflag")) != 0 ? true : false);
			msgLog.m_strContent = cursor.getString(cursor.getColumnIndex("content"));
        }
		cursor.close();
        
		return msgLog;
	}

	// 读出最后一条群消息记录
	synchronized public GroupMsgLog readLastGroupMsgLog(int nGroupNum) {
		if (null == m_DB || !m_DB.isOpen())
			return null;
		
		String strSql = "SELECT * FROM [tb_GroupMsg] WHERE [groupnum]=? ORDER BY [time] DESC LIMIT 0,1";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nGroupNum)});
		
		GroupMsgLog msgLog = new GroupMsgLog();
		while (cursor.moveToNext()) {
            msgLog.m_nID = cursor.getInt(cursor.getColumnIndex("id"));
            msgLog.m_nGroupNum = cursor.getInt(cursor.getColumnIndex("groupnum"));
            msgLog.m_nQQNum = cursor.getInt(cursor.getColumnIndex("uin"));
			msgLog.m_strNickName = cursor.getString(cursor.getColumnIndex("nickname"));
			msgLog.m_nTime = cursor.getInt(cursor.getColumnIndex("time"));
			msgLog.m_strContent = cursor.getString(cursor.getColumnIndex("content"));
        }
		cursor.close();
        
		return msgLog;
	}
	
	// 读出最后一条临时会话(群成员)消息记录
	synchronized public SessMsgLog readLastSessMsgLog(int nQQNum) {
		if (null == m_DB || !m_DB.isOpen())
			return null;
		
		String strSql = "SELECT * FROM [tb_SessMsg] WHERE [uin]=? ORDER BY [time] DESC LIMIT 0,1";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nQQNum)});
		
		SessMsgLog msgLog = new SessMsgLog();
		while (cursor.moveToNext()) {
            msgLog.m_nID = cursor.getInt(cursor.getColumnIndex("id"));
            msgLog.m_nQQNum = cursor.getInt(cursor.getColumnIndex("uin"));
			msgLog.m_strNickName = cursor.getString(cursor.getColumnIndex("nickname"));
			msgLog.m_nTime = cursor.getInt(cursor.getColumnIndex("time"));
			msgLog.m_bSendFlag = (cursor.getInt(cursor.getColumnIndex("sendflag")) != 0 ? true : false);
			msgLog.m_strContent = cursor.getString(cursor.getColumnIndex("content"));
        }
		cursor.close();
        
		return msgLog;
	}
	
	// 读出一条或多条好友消息记录
	synchronized public int readBuddyMsgLog(int nQQNum, 
			int nOffset, int nRows, List<BuddyMsgLog> arrMsgLog) {
		if (null == m_DB || !m_DB.isOpen() || null == arrMsgLog)
			return 0;
				
//		int nMsgCnt = readBuddyMsgLogCount(nQQNum, nOffset, nRows);
//		if (nMsgCnt <= 0)
//			return 0;
		
		String strSql = "";
		if (0 == nOffset && 0 == nRows)
			strSql = "SELECT * FROM [tb_BuddyMsg] WHERE [uin]=? ORDER BY [time]";
		else
			strSql = "SELECT * FROM [tb_BuddyMsg] WHERE [uin]=? ORDER BY [time] LIMIT ?,?";

		Cursor cursor = null;
		if (0 == nOffset && 0 == nRows) {
			cursor = m_DB.rawQuery(strSql, 
					new String[]{String.valueOf(nQQNum)});
		} else {
			cursor = m_DB.rawQuery(strSql, 
					new String[]{String.valueOf(nQQNum), 
					String.valueOf(nOffset), String.valueOf(nRows)});
		}
		
		while (cursor.moveToNext()) {
            BuddyMsgLog msgLog = new BuddyMsgLog();
            msgLog.m_nID = cursor.getInt(cursor.getColumnIndex("id"));
            msgLog.m_nQQNum = cursor.getInt(cursor.getColumnIndex("uin"));
			msgLog.m_strNickName = cursor.getString(cursor.getColumnIndex("nickname"));
			msgLog.m_nTime = cursor.getInt(cursor.getColumnIndex("time"));
			msgLog.m_bSendFlag = (cursor.getInt(cursor.getColumnIndex("sendflag")) != 0 ? true : false);
			msgLog.m_strContent = cursor.getString(cursor.getColumnIndex("content"));
			arrMsgLog.add(msgLog);
        }
		cursor.close();
        
		return arrMsgLog.size();
	}

	// 读出一条或多条群消息记录
	synchronized public int readGroupMsgLog(int nGroupNum, 
			int nOffset, int nRows, List<GroupMsgLog> arrMsgLog) {
		if (null == m_DB || !m_DB.isOpen() || null == arrMsgLog)
			return 0;
		
//		int nMsgCnt = readGroupMsgLogCount(nGroupNum, nOffset, nRows);
//		if (nMsgCnt <= 0)
//			return 0;
		
		String strSql = "";
		if (0 == nOffset && 0 == nRows)
			strSql = "SELECT * FROM [tb_GroupMsg] WHERE [groupnum]=? ORDER BY [time]";
		else
			strSql = "SELECT * FROM [tb_GroupMsg] WHERE [groupnum]=? ORDER BY [time] LIMIT ?,?";

		Cursor cursor = null;
		if (0 == nOffset && 0 == nRows) {
			cursor = m_DB.rawQuery(strSql, 
					new String[]{String.valueOf(nGroupNum)});
		} else {
			cursor = m_DB.rawQuery(strSql, 
					new String[]{String.valueOf(nGroupNum), 
					String.valueOf(nOffset), String.valueOf(nRows)});
		}
		
		while (cursor.moveToNext()) {
            GroupMsgLog msgLog = new GroupMsgLog();
            msgLog.m_nID = cursor.getInt(cursor.getColumnIndex("id"));
            msgLog.m_nGroupNum = cursor.getInt(cursor.getColumnIndex("groupnum"));
            msgLog.m_nQQNum = cursor.getInt(cursor.getColumnIndex("uin"));
			msgLog.m_strNickName = cursor.getString(cursor.getColumnIndex("nickname"));
			msgLog.m_nTime = cursor.getInt(cursor.getColumnIndex("time"));
			msgLog.m_strContent = cursor.getString(cursor.getColumnIndex("content"));
			arrMsgLog.add(msgLog);
        }
		cursor.close();
        
		return arrMsgLog.size();
	}

	// 读出一条或多条临时会话(群成员)消息记录
	synchronized public int readSessMsgLog(int nQQNum, 
			int nOffset, int nRows, List<SessMsgLog> arrMsgLog) {
		if (null == m_DB || !m_DB.isOpen() || null == arrMsgLog)
			return 0;
		
//		int nMsgCnt = readSessMsgLogCount(nQQNum, nOffset, nRows);
//		if (nMsgCnt <= 0)
//			return 0;
		
		String strSql = "";
		if (0 == nOffset && 0 == nRows)
			strSql = "SELECT * FROM [tb_SessMsg] WHERE [uin]=? ORDER BY [time]";
		else
			strSql = "SELECT * FROM [tb_SessMsg] WHERE [uin]=? ORDER BY [time] LIMIT ?,?";

		Cursor cursor = null;
		if (0 == nOffset && 0 == nRows) {
			cursor = m_DB.rawQuery(strSql, 
					new String[]{String.valueOf(nQQNum)});
		} else {
			cursor = m_DB.rawQuery(strSql, 
					new String[]{String.valueOf(nQQNum), 
					String.valueOf(nOffset), String.valueOf(nRows)});
		}
		
		while (cursor.moveToNext()) {
            SessMsgLog msgLog = new SessMsgLog();
            msgLog.m_nID = cursor.getInt(cursor.getColumnIndex("id"));
            msgLog.m_nQQNum = cursor.getInt(cursor.getColumnIndex("uin"));
			msgLog.m_strNickName = cursor.getString(cursor.getColumnIndex("nickname"));
			msgLog.m_nTime = cursor.getInt(cursor.getColumnIndex("time"));
			msgLog.m_bSendFlag = (cursor.getInt(cursor.getColumnIndex("sendflag")) != 0 ? true : false);
			msgLog.m_strContent = cursor.getString(cursor.getColumnIndex("content"));
			arrMsgLog.add(msgLog);
        }
		cursor.close();
        
		return arrMsgLog.size();
	}

	// 获取好友消息记录数
	synchronized public int getBuddyMsgLogCount(int nQQNum) {
		if (null == m_DB || !m_DB.isOpen())
			return 0;
		
		int nCount = 0;
		String strSql = "SELECT COUNT(*) FROM [tb_BuddyMsg] WHERE [uin]=?";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nQQNum)});
		if (cursor.getCount() == 1) {
			cursor.move(1);
			nCount = cursor.getInt(0);
		}
		cursor.close();
		return nCount;		
	}

	// 获取群消息记录数
	synchronized public int getGroupMsgLogCount(int nGroupNum) {
		if (null == m_DB || !m_DB.isOpen())
			return 0;
		
		int nCount = 0;
		String strSql = "SELECT COUNT(*) FROM [tb_GroupMsg] WHERE [groupnum]=?";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nGroupNum)});
		if (cursor.getCount() == 1) {
			cursor.move(1);
			nCount = cursor.getInt(0);
		}
		cursor.close();
		return nCount;
	}

	// 获取临时会话(群成员)消息记录数
	synchronized public int getSessMsgLogCount(int nQQNum) {
		if (null == m_DB || !m_DB.isOpen())
			return 0;
		
		int nCount = 0;
		String strSql = "SELECT COUNT(*) FROM [tb_SessMsg] WHERE [uin]=?";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nQQNum)});
		if (cursor.getCount() == 1) {
			cursor.move(1);
			nCount = cursor.getInt(0);
		}
		cursor.close();
		return nCount;
	}

	// 删除所有好友消息记录
	synchronized public boolean delAllBuddyMsgLog() {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_BuddyMsg", null, null);
		return ((nRowCnt != 0) ? true : false);
	}

	// 删除所有群消息记录
	synchronized public boolean delAllGroupMsgLog() {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_GroupMsg", null, null);
		return ((nRowCnt != 0) ? true : false);		
	}

	// 删除所有临时会话(群成员)消息记录
	synchronized public boolean delAllSessMsgLog() {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_SessMsg", null, null);
		return ((nRowCnt != 0) ? true : false);
	}

	// 删除指定好友的所有消息记录
	synchronized public boolean delBuddyMsgLog(int nQQNum) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_BuddyMsg", 
				"uin=?", new String[]{String.valueOf(nQQNum)});
		return ((nRowCnt != 0) ? true : false);		
	}

	// 删除指定群的所有消息记录
	synchronized public boolean delGroupMsgLog(int nGroupNum) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_GroupMsg", 
				"groupnum=?", new String[]{String.valueOf(nGroupNum)});
		return ((nRowCnt != 0) ? true : false);
	}

	// 删除指定临时会话(群成员)的所有消息记录
	synchronized public boolean delSessMsgLog(int nQQNum) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_SessMsg", 
				"uin=?", new String[]{String.valueOf(nQQNum)});
		return ((nRowCnt != 0) ? true : false);
	}

	// 删除指定ID的好友消息记录
	synchronized public boolean delBuddyMsgLogByID(int nID) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_BuddyMsg", 
				"id=?", new String[]{String.valueOf(nID)});
		return ((nRowCnt != 0) ? true : false);
	}

	// 删除指定ID的群消息记录
	synchronized public boolean delGroupMsgLogByID(int nID) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_GroupMsg", 
				"id=?", new String[]{String.valueOf(nID)});
		return ((nRowCnt != 0) ? true : false);
	}

	// 删除指定ID的临时会话(群成员)消息记录
	synchronized public boolean delSessMsgLogByID(int nID) {
		if (null == m_DB || !m_DB.isOpen())
			return false;
		
		int nRowCnt = m_DB.delete("tb_SessMsg", 
				"id=?", new String[]{String.valueOf(nID)});
		return ((nRowCnt != 0) ? true : false);
	}

	synchronized public int readBuddyMsgLogCount(int nQQNum, 
			int nOffset, int nRows) {
		if (null == m_DB || !m_DB.isOpen())
			return 0;
		
		int nCount = 0;
		String strSql = "SELECT COUNT(*) FROM (SELECT * FROM [tb_BuddyMsg] WHERE [uin]=? ORDER BY [time] LIMIT ?,?)";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nQQNum), 
				String.valueOf(nOffset), String.valueOf(nRows)});
		if (cursor.getCount() == 1) {
			cursor.move(1);
			nCount = cursor.getInt(0);
		}
		cursor.close();
		return nCount;
	}

	synchronized public int readGroupMsgLogCount(int nGroupNum, 
			int nOffset, int nRows) {
		if (null == m_DB || !m_DB.isOpen())
			return 0;
		
		int nCount = 0;
		String strSql = "SELECT COUNT(*) FROM (SELECT * FROM [tb_GroupMsg] WHERE [groupnum]=? ORDER BY [time] LIMIT ?,?)";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nGroupNum), 
				String.valueOf(nOffset), String.valueOf(nRows)});
		if (cursor.getCount() == 1) {
			cursor.move(1);
			nCount = cursor.getInt(0);
		}
		cursor.close();
		return nCount;
	}

	synchronized public int readSessMsgLogCount(int nQQNum, 
			int nOffset, int nRows) {
		if (null == m_DB || !m_DB.isOpen())
			return 0;
		
		int nCount = 0;
		String strSql = "SELECT COUNT(*) FROM (SELECT * FROM [tb_SessMsg] WHERE [uin]=? ORDER BY [time] LIMIT ?,?)";
		
		Cursor cursor = m_DB.rawQuery(strSql, 
				new String[]{String.valueOf(nQQNum), 
				String.valueOf(nOffset), String.valueOf(nRows)});
		if (cursor.getCount() == 1) {
			cursor.move(1);
			nCount = cursor.getInt(0);
		}
		cursor.close();
		return nCount;
	}
	
	synchronized public int readAllGroupLastMsg(List<GroupMsgLog> arrMsgLog) {
		if (null == m_DB || !m_DB.isOpen() || null == arrMsgLog)
			return 0;
				
		String strSql = "SELECT * FROM [tb_GroupMsg] GROUP BY [groupnum] HAVING time=max(time)";

		Cursor cursor = m_DB.rawQuery(strSql, null);
		
		while (cursor.moveToNext()) {
            GroupMsgLog msgLog = new GroupMsgLog();
            msgLog.m_nID = cursor.getInt(cursor.getColumnIndex("id"));
            msgLog.m_nGroupNum = cursor.getInt(cursor.getColumnIndex("groupnum"));
            msgLog.m_nQQNum = cursor.getInt(cursor.getColumnIndex("uin"));
			msgLog.m_strNickName = cursor.getString(cursor.getColumnIndex("nickname"));
			msgLog.m_nTime = cursor.getInt(cursor.getColumnIndex("time"));
			msgLog.m_strContent = cursor.getString(cursor.getColumnIndex("content"));
			arrMsgLog.add(msgLog);
        }
		cursor.close();
        
		return arrMsgLog.size();
	}
}
