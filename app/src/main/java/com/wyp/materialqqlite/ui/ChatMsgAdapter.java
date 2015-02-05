package com.wyp.materialqqlite.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.FaceInfo;
import com.wyp.materialqqlite.FaceList;
import com.wyp.materialqqlite.ImageCache;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.ContentType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;

public class ChatMsgAdapter extends BaseAdapter 
	implements OnClickListener {

	private Context m_Context;
	private int m_nGroupCode;
	private List<ChatMsg> m_lnkChatMsg;
	private QQClient m_QQClient;
	private FaceList m_faceList;
	private ImageCache m_imgCache;
	private String m_strChatPicPath;
    private int m_cxAvatar, m_cyAvatar;
    private int m_pxAvatarRound;
	private int m_cxFace, m_cyFace;
	private int m_cxCFace, m_cyCFace;
	private int m_roundPx_L, m_roundPx_S;
	
	public ChatMsgAdapter(Context context, 
			int nGroupCode, List<ChatMsg> lnkChatMsg) {
		m_Context = context;
		m_nGroupCode = nGroupCode;
		m_lnkChatMsg = lnkChatMsg;
		m_QQClient = AppData.getAppData().getQQClient();
		m_faceList = AppData.getAppData().getFaceList();
		m_imgCache = new ImageCache();
		m_strChatPicPath = m_QQClient.getChatPicFolder(0);
	
        m_cxAvatar = (int)context.
        		getResources().getDimension(R.dimen.chat_cxAvatar);
        m_cyAvatar = (int)context.
        		getResources().getDimension(R.dimen.chat_cyAvatar);
        m_pxAvatarRound = (int)context.
        		getResources().getDimension(R.dimen.pxAvatarRound);

		m_cxFace = (int)context.
				getResources().getDimension(R.dimen.cxFace);
		m_cyFace = (int)context.
				getResources().getDimension(R.dimen.cyFace);
		
		m_cxCFace = (int)context.
				getResources().getDimension(R.dimen.cxCFace);
		m_cyCFace = (int)context.
				getResources().getDimension(R.dimen.cyCFace);
		
		m_roundPx_L = (int)context.
				getResources().getDimension(R.dimen.chatPic_RoundPx_L);
		m_roundPx_S = (int)context.
				getResources().getDimension(R.dimen.chatPic_RoundPx_S);
	}

	public int getCount() {
		if (m_lnkChatMsg != null)
			return m_lnkChatMsg.size();
		else
			return 0;
	}

	public Object getItem(int position) {
		if (m_lnkChatMsg != null)
			return m_lnkChatMsg.get(position);
		else
			return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		if (m_lnkChatMsg != null) {
			ChatMsg chatMsg = m_lnkChatMsg.get(position);
			if (chatMsg != null)
				return chatMsg.m_nType;
		}
		return 0;
	}

	public int getViewTypeCount() {
		return ChatMsg.TYPE_COUNT;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMsg chatMsg = m_lnkChatMsg.get(position);
		if (null == chatMsg)
			return convertView;
		
		ViewHolder holder = null;
		if (null == convertView) {
			if (ChatMsg.LEFT_B == chatMsg.m_nType) {
				convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.chat_listitem_left_b, parent, false);
			} else if (ChatMsg.LEFT_G == chatMsg.m_nType) {
				convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.chat_listitem_left_g, parent, false);
			} else if (ChatMsg.RIGHT == chatMsg.m_nType) {
				convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.chat_listitem_right, parent, false);
			} else if (ChatMsg.TIME == chatMsg.m_nType) {
				convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.chat_listitem_time, parent, false);
			}
			
			if (convertView != null) {
				holder = new ViewHolder();
				holder.m_imgAvatar = (ImageView)convertView
						.findViewById(R.id.chatlistitem_imgAvatar);
				holder.m_txtName = (TextView)convertView
						.findViewById(R.id.chatlistitem_txtName);
				holder.m_txtContent = (TextView)convertView
						.findViewById(R.id.chatlistitem_txtContent);
				holder.m_txtTime = (TextView)convertView
						.findViewById(R.id.chatlistitem_txtTime);
				
				if (holder.m_txtContent != null) {
					holder.m_txtContent.setHighlightColor(Color.TRANSPARENT);
//					android:textColorLink
//					android:textColorHighlight
				}
				
				convertView.setTag(holder);
			}
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if (null == holder)
			return convertView;

		if (ChatMsg.LEFT_B == chatMsg.m_nType) {
			Bitmap bmp;
			if (m_nGroupCode != 0)
				bmp = getSessHeadPic(m_nGroupCode, chatMsg.m_nQQUin);
			else
				bmp = getBuddyHeadPic(chatMsg.m_nQQUin);
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.h001);
			
			setBubble(holder.m_txtContent, chatMsg);
			
			SpannableStringBuilder strContent = 
					getSpanStrBuilder(m_Context, chatMsg.m_arrContent);
			holder.m_txtContent.setText(strContent);
		} else if (ChatMsg.LEFT_G == chatMsg.m_nType) {
			Bitmap bmp = getSessHeadPic(m_nGroupCode, chatMsg.m_nQQUin);
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.h001);
			holder.m_imgAvatar.setTag(position);
			holder.m_imgAvatar.setOnClickListener(this);
			
			setBubble(holder.m_txtContent, chatMsg);
			
			holder.m_txtName.setText(chatMsg.m_strName + ":");
			
			SpannableStringBuilder strContent = 
					getSpanStrBuilder(m_Context, chatMsg.m_arrContent);
			holder.m_txtContent.setMovementMethod(LinkMovementMethod.getInstance());
			holder.m_txtContent.setText(strContent);
		} else if (ChatMsg.RIGHT == chatMsg.m_nType) {
			Bitmap bmp = getBuddyHeadPic(chatMsg.m_nQQUin);
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.h001);
			
			setBubble(holder.m_txtContent, chatMsg);
			
			SpannableStringBuilder strContent = 
					getSpanStrBuilder(m_Context, chatMsg.m_arrContent);
			holder.m_txtContent.setText(strContent);
		} else if (ChatMsg.TIME == chatMsg.m_nType) {
			String strTime = formatTime(chatMsg.m_nTime);
			holder.m_txtTime.setText(strTime);
		}

		return convertView;
	}
	
	private String formatTime(int nTime) {
		Calendar time = Calendar.getInstance();
    	time.setTimeInMillis(Utils.getUInt(nTime)*1000);
    	
    	if (Utils.isToday(nTime)) {	// 今天
    		SimpleDateFormat dateFmt = 
    				new SimpleDateFormat("H:mm", Locale.getDefault());
        	return dateFmt.format(time.getTime());
    	} else if (Utils.isYesterday(nTime)) {	// 昨天
    		SimpleDateFormat dateFmt = 
    				new SimpleDateFormat("昨天 H:mm", Locale.getDefault());
        	return dateFmt.format(time.getTime());
    	} else if (Utils.isThisYear(nTime)) {	// 今年
    		SimpleDateFormat dateFmt = 
    				new SimpleDateFormat("MM-dd H:mm", Locale.getDefault());
        	return dateFmt.format(time.getTime());
    	} else {
    		SimpleDateFormat dateFmt = 
    				new SimpleDateFormat("yyyy-MM-dd H:mm", Locale.getDefault());
        	return dateFmt.format(time.getTime());
    	}
	}

	private SpannableStringBuilder getSpanStrBuilder(
			Context context, List<Content> arrContent) {
		if (null == context || null == arrContent)
			return null;
		
		SpannableStringBuilder spanStrBuilder = new SpannableStringBuilder();
		
		for (int i = 0; i < arrContent.size(); i++) {
			Content content = arrContent.get(i);
			if (null == content)
				continue;
			
			if (content.m_nType == ContentType.CONTENT_TYPE_TEXT) {	// 文字
				spanStrBuilder.append(content.m_strText);
			} else if (content.m_nType == ContentType.CONTENT_TYPE_FACE) {	// 表情
				FaceInfo faceInfo = m_faceList.getFaceInfoById(content.m_nFaceId);
				
				SpannableString spanStr = Utils.getSysFace(
						m_Context, faceInfo, m_cxFace, m_cyFace);
				if (spanStr != null)
					spanStrBuilder.append(spanStr);
			} else if (content.m_nType == ContentType.CONTENT_TYPE_CUSTOM_FACE) {	// 图片
				if (spanStrBuilder.length() > 0)
					spanStrBuilder.append("\n");
								
				String strFileName = m_strChatPicPath + content.m_CFaceInfo.m_strName;
				Bitmap bmp = m_imgCache.get(content.m_CFaceInfo.m_strName);
            	if (null == bmp) {
                	File file = new File(strFileName);
                	if (!file.exists()) {
                		
                	} else {
                		LoadImageTask task = new LoadImageTask();
                		task.m_strKey = content.m_CFaceInfo.m_strName;
                		task.m_strFileName = strFileName;
                		task.m_bIsHeadPic = false;
                		task.execute("");
                	}
                	SpannableString spanStr = Utils.getCustomFace(
                			m_Context, R.drawable.aio_image_default_round, content.m_CFaceInfo.m_strName);
            		if (spanStr != null)
						spanStrBuilder.append(spanStr);
            	} else {
                    ClickableSpan clickSpan = new ImageClickSpan(content.m_CFaceInfo.m_strName);
            		SpannableString spanStr = Utils.getCustomFace(
            				m_Context, bmp, content.m_CFaceInfo.m_strName, clickSpan);
            		if (spanStr != null)
						spanStrBuilder.append(spanStr);
            	}
            	
            	if (i != arrContent.size()-1)
            		spanStrBuilder.append("\n");
			}
		}
		return spanStrBuilder;
	}
	
	private Bitmap getBuddyHeadPic(int nQQUin) {
		if (0 == nQQUin)
			return null;
		
		BuddyInfo buddyInfo;
		if (nQQUin == m_QQClient.getUserInfo().m_nQQUin) {
			buddyInfo = m_QQClient.getUserInfo();
		} else {
			BuddyList buddyList = m_QQClient.getBuddyList();
	    	buddyInfo = buddyList.getBuddy(nQQUin);	
		}
    	if (null == buddyInfo) {
    		return null;
    	}
    	
		if (0 == buddyInfo.m_nQQNum) {	// 没有号码先更新号码
			m_QQClient.updateBuddyNum(buddyInfo.m_nQQUin);
			return null;
		}
		
		Bitmap bmp = m_imgCache.get(buddyInfo.m_nQQNum);
		if (bmp != null) {
			return bmp;
		}

		String strFileName = m_QQClient.getBuddyHeadPicFullName(buddyInfo.m_nQQNum);
		File file = new File(strFileName);
		if (!file.exists()) {
			m_QQClient.updateBuddyHeadPic(buddyInfo.m_nQQUin, buddyInfo.m_nQQNum);
			return null;
		}
		
		LoadImageTask task = new LoadImageTask();
		task.m_strKey = String.valueOf(buddyInfo.m_nQQNum);
		task.m_strFileName = strFileName;
		task.m_bIsHeadPic = true;
		task.execute("");
		return null;
	}

	private Bitmap getSessHeadPic(int nGroupCode, int nQQUin) {
		if (0 == nGroupCode || 0 == nQQUin)
			return null;
		
    	GroupList groupList = m_QQClient.getGroupList();
    	BuddyInfo buddyInfo = groupList.getGroupMemberByCode(nGroupCode, nQQUin);
    	if (null == buddyInfo) {
    		return null;
    	}
    	
		if (0 == buddyInfo.m_nQQNum) {
			m_QQClient.updateGroupMemberNum(nGroupCode, nQQUin);
			return null;
		}

		Bitmap bmp = m_imgCache.get(buddyInfo.m_nQQNum);
		if (bmp != null) {
			return bmp;
		}

		String strFileName = m_QQClient.getSessHeadPicFullName(buddyInfo.m_nQQNum);
		File file = new File(strFileName);
		if (!file.exists()) {
			m_QQClient.updateGroupMemberHeadPic(nGroupCode, nQQUin, buddyInfo.m_nQQNum);
			return null;
		}
		
		LoadImageTask task = new LoadImageTask();
		task.m_strKey = String.valueOf(buddyInfo.m_nQQNum);
		task.m_strFileName = strFileName;
		task.m_bIsHeadPic = true;
		task.execute("");
		return null;
	}
	
	private void setBubble(TextView txtContent, ChatMsg chatMsg) {		
		Integer nOldBubble = (Integer)txtContent.getTag();
		if (null == nOldBubble || nOldBubble != chatMsg.m_nBubble) {
			int left = txtContent.getPaddingLeft();
	        int right = txtContent.getPaddingRight();
	        int top = txtContent.getPaddingTop();
	        int bottom = txtContent.getPaddingBottom();

	        boolean bIsUser = (ChatMsg.RIGHT == chatMsg.m_nType);
			boolean bUseDefBubble = true;
			
			if (chatMsg.m_nBubble != 0) {				
				//
			}
			
			if (bUseDefBubble) {
				if (bIsUser) {
					txtContent.setBackgroundResource(R.drawable.btn_style7);
					txtContent.setTextColor(0xFFFFFFFF);
                    txtContent.setTextIsSelectable(true);
					txtContent.setLinkTextColor(0xFF0000FF);
				} else {
					txtContent.setBackgroundResource(R.drawable.btn_style6);
					txtContent.setTextColor(0xFF000000);
                    txtContent.setTextIsSelectable(true);
					txtContent.setLinkTextColor(0xFF0000FF);
				}
				txtContent.setTag(0);
			}
			
			txtContent.setPadding(left, top, right, bottom);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chatlistitem_imgAvatar:
			int nPos = (Integer)v.getTag();
			ChatMsg chatMsg = m_lnkChatMsg.get(nPos);
			if (chatMsg != null && m_nGroupCode != 0 && chatMsg.m_nQQUin != 0) {
				if (chatMsg.m_nType == ChatMsg.LEFT_G) {
					Intent intent = new Intent(m_Context, ChatActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("type", ChatActivity.IS_SESS);
					bundle.putInt("useruin", m_QQClient.getUserInfo().m_nQQUin);
		            bundle.putString("username", m_QQClient.getUserInfo().m_strNickName);
		            bundle.putInt("groupcode", m_nGroupCode);
		            bundle.putInt("qquin", chatMsg.m_nQQUin);
		            GroupList groupList = m_QQClient.getGroupList();
		            GroupInfo groupInfo = groupList.getGroupByCode(m_nGroupCode);
		            if (groupInfo != null) {
		            	bundle.putInt("groupid", groupInfo.m_nGroupId);
		            	bundle.putInt("groupnum", groupInfo.m_nGroupNumber);
		            	bundle.putString("groupname", groupInfo.m_strName);
		            	BuddyInfo buddyInfo = groupInfo.getMemberByUin(chatMsg.m_nQQUin);
		            	if (buddyInfo != null) {
			            	bundle.putInt("qqnum", buddyInfo.m_nQQNum);
			            	if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
			            		bundle.putString("buddyname", buddyInfo.m_strGroupCard);
			            	else
			            		bundle.putString("buddyname", buddyInfo.m_strNickName);
			            } else {
			            	bundle.putInt("qqnum", 0);
				            bundle.putString("buddyname", "");
			            }
		            } else {
		            	bundle.putInt("groupid", 0);
		            	bundle.putInt("groupnum", 0);
		            	bundle.putString("groupname", "");
		            	bundle.putInt("qqnum", 0);
			            bundle.putString("buddyname", "");
		            }
		            intent.putExtras(bundle);
					m_Context.startActivity(intent);
				}
			}
			break;
		}
	}

	class ViewHolder {
		public ImageView m_imgAvatar;
		public TextView m_txtName;
		public TextView m_txtContent;
		public TextView m_txtTime;
	}

	private class ImageClickSpan extends ClickableSpan 
		implements OnClickListener {
		private String m_strUrl;
		
        public ImageClickSpan(String strUrl) {
        	super();
        	m_strUrl = strUrl;
        }
        
        @Override    
        public void onClick(View v) {
        	int nCurIndex = 0;
        	ArrayList<String> arrUrl = new ArrayList<String>();
        	for (int i = 0; i < m_lnkChatMsg.size(); i++) {
        		ChatMsg chatMsg = m_lnkChatMsg.get(i);
        		if (null == chatMsg || null == chatMsg.m_arrContent)
        			continue;
        		for (int j = 0; j < chatMsg.m_arrContent.size(); j++) {
        			Content content = chatMsg.m_arrContent.get(j);
        			if (null == content)
        				continue;
        			if (ContentType.CONTENT_TYPE_CUSTOM_FACE == content.m_nType) {
        				arrUrl.add(content.m_CFaceInfo.m_strName);
        				if (m_strUrl.equals(content.m_CFaceInfo.m_strName))
        					nCurIndex = arrUrl.size()-1;
        			}
        		}
        	}
        	
        	Intent intent = new Intent(m_Context, PicViewerActivity.class);
        	intent.putStringArrayListExtra("urls", arrUrl);
        	intent.putExtra("curindex", nCurIndex);
        	m_Context.startActivity(intent);
        }
     }
	
    private class LoadImageTask extends AsyncTask<String, Integer, Boolean> {
    	public String m_strKey = "";
    	public String m_strFileName = "";
    	public boolean m_bIsHeadPic = false;
    	public Bitmap m_Bitmap = null;
    	
        @Override  
        protected Boolean doInBackground(String... params) {
        	if (m_bIsHeadPic) {
        		File file = new File(m_strFileName);
            	int nTime = (int)(file.lastModified() / 1000);
            	if (!Utils.isToday(nTime)) {
            		file.delete();
            		return true;
            	}
            	
            	m_Bitmap = BitmapFactory.decodeFile(m_strFileName);
            	if (m_Bitmap != null) {
            		m_Bitmap = Utils.zoomImg(m_Bitmap, m_cxAvatar, m_cyAvatar);        		
            		m_Bitmap = Utils.getRoundedCornerBitmap(m_Bitmap, m_pxAvatarRound);
            	}
            	return m_Bitmap != null;
        	} else {
            	m_Bitmap = BitmapFactory.decodeFile(m_strFileName);
            	if (m_Bitmap != null) {
            		int cx = m_cxCFace;
            		int cy = m_cyCFace;

            		int cxBmp = m_Bitmap.getWidth();
            		int cyBmp = m_Bitmap.getHeight();
            		
            		int roundPx;
            		if (cxBmp > cx || cyBmp > cy) {
            			if (cxBmp < cx)
            				cx = cxBmp;
            			if (cyBmp < cy)
            				cy = cyBmp;
            			m_Bitmap = Utils.zoomImg(m_Bitmap, cx, cy);
            			roundPx = m_roundPx_L;
            		} else {
            			roundPx = m_roundPx_S;
            		}
            		m_Bitmap = Utils.getRoundedCornerBitmap(m_Bitmap, roundPx);
            	}
            	return m_Bitmap != null;	
        	}        	
        }  
        
        @Override
        protected void onPostExecute(Boolean result) {
        	if (result) {
        		m_imgCache.put(m_strKey, m_Bitmap);
        		ChatMsgAdapter.this.notifyDataSetChanged();
        	}
        }
	}
}
