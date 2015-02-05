package com.wyp.materialqqlite;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

public class BubbleManager {
	public static final int MAX_COUNT = 80;
	
	private int m_nUserBubble;
	
    public int getUserBubble() {
    	return m_nUserBubble;
    }
    
    public void setUserBubble(int nBubble) {
    	m_nUserBubble = nBubble;
    }

	public String getBubblePicFileName(int nBubble) {
		String strFileName = "bubble_info/";
		strFileName += nBubble;
		strFileName += "/aio_user_bg_nor.9.png";
		return strFileName;
	}
	
	public boolean isExistBubble(Context context, int nBubble) {
		try {
			String strFileName = getBubblePicFileName(nBubble);
			InputStream is = context.getResources().getAssets().open(strFileName);
			if (null == is)
				return false;
			is.close();
						
			return true;
		} catch (Exception e) {
			e.printStackTrace();  
		}
		return false;
	}
    
	public int nextBubble(Context context, int nBubble) {
		nBubble++;
		for (int i = nBubble; i < MAX_COUNT; i++) {
			if (isExistBubble(context, i)) {
				return i;
			}
		}
		return 0;
	}
		
	public Drawable loadBubblePic(Context context, 
			int nBubble, boolean bIsUser) {
		try {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void loadBubbleConfig(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(  
		        "BubbleCfg", Context.MODE_PRIVATE);
		m_nUserBubble = sharedPref.getInt("UserBubble", 0);
	}
	
	public void saveBubbleConfig(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(  
		        "BubbleCfg", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("UserBubble", m_nUserBubble);
		editor.commit();
	}	

	public BubbleInfo getBubbleInfo(Context context, int nBubble) {
		String strFileName = "bubble_info/";
		strFileName += nBubble;
		strFileName += "/config.json";
		String strText = FileUtils.readFromAssets(context, strFileName);

		JSONObject json;
		try {
			json = new JSONObject(strText);
			BubbleInfo bubbleInfo = new BubbleInfo();
			bubbleInfo.m_nId = json.optInt("id");
			bubbleInfo.m_strName = json.optString("name");
			String strValue = json.optString("color");
			bubbleInfo.m_nColor = Utils.HexStrToARGB(strValue, 0xFF000000);
			strValue = json.optString("link_color");
			bubbleInfo.m_nLinkColor = Utils.HexStrToARGB(strValue, 0xFF0000FF);
			return bubbleInfo;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}