package com.wyp.materialqqlite;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.RemoteViews;

import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.ui.MainActivity;

public class AppData {
    private static AppData m_appData;
    private final static Object syncLock = new Object();
    private QQClient m_QQClient;
    private LoginAccountList m_accoutnList;		// 帐户列表
    private FaceList m_faceList;				// 表情列表
    private BubbleManager m_bubbleMgr;			// 气泡管理器
    private NotificationManager m_notifyMgr;	// 通知栏管理器
    private Notification m_notify;				// 通知
    private boolean m_bShowNotify;				// 是否显示通知
    private String m_strAppPath;
    private Handler m_hService;
    private boolean m_bServiceInit;


    private AppData() {
    	m_QQClient = new QQClient();
    	m_accoutnList = new LoginAccountList();
    	m_faceList = new FaceList();
    	m_bubbleMgr = new BubbleManager();
    }
   
    public static AppData getAppData(){
        if (m_appData == null) {
            synchronized (syncLock) {
                if (m_appData == null) {
                	m_appData = new AppData();
                }
            }
        }
       
        return m_appData;
    }
    
    public QQClient getQQClient() {
    	return m_QQClient;
    }
    
    public Handler getServiceHandler() {
    	return m_hService;
    }
    
    public void setServiceHandler(Handler handler) {
    	m_hService = handler;
    }
    
    public FaceList getFaceList() {
    	return m_faceList;
    }

    public LoginAccountList getLoginAccountList() {
    	return m_accoutnList;
    }
    
    public BubbleManager getBubbleMgr() {
    	return m_bubbleMgr;
    }
    
    public String getAppPath() {
    	return m_strAppPath;
    }
    
    public void setAppPath(String strAppPath) {
    	m_strAppPath = strAppPath;
    }
    
    public boolean isShowNotify() {
    	return m_bShowNotify;
    }
        
	public void showNotify(int nId, Context context, 
			String tickerText, String strTitle, String strText) {
		if (null == m_notifyMgr) {
			m_notifyMgr = (NotificationManager)context.getSystemService(
	        		android.content.Context.NOTIFICATION_SERVICE);
			if (null == m_notifyMgr)
				return;
		}

		int icon = R.drawable.notifyicon;//R.drawable.notify_newmessage;
		long when = System.currentTimeMillis();
		
		if (null == m_notify) {
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.notifybar);
			contentView.setImageViewResource(R.id.notify_icon,
					R.drawable.qqicon);
			contentView.setImageViewResource(R.id.notify_smallicon,
					R.drawable.notifyicon);
			
			m_notify = new Notification(icon, tickerText, when);
			m_notify.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;//常驻



			m_notify.contentView = contentView;
			
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			m_notify.contentIntent = contentIntent;
		}
		
		m_notify.tickerText = tickerText;
		m_notify.contentView.setTextViewText(R.id.notify_title, strTitle);
		m_notify.contentView.setTextViewText(R.id.notify_text, strText);
		m_notify.contentView.setLong(R.id.notify_time, "setTime", when);
		m_notifyMgr.notify(nId, m_notify);
		m_bShowNotify = true;
	}
		
	public void cancelNotify(int nId) {
		if (m_notifyMgr != null)
			m_notifyMgr.cancel(nId);
		m_bShowNotify = false;
	}
	
	public boolean isQQServiceInit() {
		return m_bServiceInit;
	}
	
	public void setQQServiceInit(boolean bServiceInit) {
		m_bServiceInit = bServiceInit;
	}
}
