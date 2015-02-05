package com.wyp.materialqqlite.ui;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyp.materialqqlite.FaceInfo;
import com.wyp.materialqqlite.FaceList;
import com.wyp.materialqqlite.ImageCache;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.ContentType;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageSender;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SessMessage;


public class MsgAdapter extends BaseAdapter {
    private Context m_Context;
    private MessageList m_msgList;
	private FaceList m_faceList;
	private QQClient m_QQClient;
	private ImageCache m_imgCache;
    private onRightItemClickListener m_ritemClickListener;
    private int m_cxAvatar, m_cyAvatar;
    private int m_pxAvatarRound;
    private int m_cxFace, m_cyFace;
    
    public MsgAdapter(Context context, MessageList msgList, FaceList faceList) {
        m_Context = context;
        m_msgList = msgList;
        m_faceList = faceList;
        m_imgCache = new ImageCache();
        m_QQClient = AppData.getAppData().getQQClient();
        m_ritemClickListener = null;
        m_cxAvatar = (int)context.getResources().getDimension(R.dimen.msgList_cxAvatar);
        m_cyAvatar = (int)context.getResources().getDimension(R.dimen.msgList_cyAvatar);
        m_pxAvatarRound = (int)context.getResources().getDimension(R.dimen.pxAvatarRound);
        m_cxFace = (int)context.getResources().getDimension(R.dimen.msgList_cxFace);
        m_cyFace = (int)context.getResources().getDimension(R.dimen.msgList_cyFace);
    }

    @Override
    public int getCount() {
    	if (m_msgList != null)
    		return m_msgList.getMsgSenderCount();
    	else
    		return 0;
    }

    @Override
    public Object getItem(int position) {
    	if (m_msgList != null)
    		return m_msgList.getMsgSender(position);
    	else
    		return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(m_Context)
            		.inflate(R.layout.msg_list_item, parent, false);
            
            holder = new ViewHolder();
            
            holder.m_imgAvatar = (ImageView) convertView.findViewById(R.id.msglistitem_imgAvatar);
            holder.m_txtName = (TextView)convertView.findViewById(R.id.msglistitem_txtName);
            holder.m_txtDesc = (TextView)convertView.findViewById(R.id.msglistitem_txtDesc);
            holder.m_txtTime = (TextView)convertView.findViewById(R.id.msglistitem_txtTime);
            holder.m_txtUnreadMsgCnt = (TextView)convertView.findViewById(R.id.msglistitem_txtUnreadMsgCnt);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        if (null == holder || null == m_msgList)
        	return convertView;
        
        MessageSender msgSender = m_msgList.getMsgSender(position);
        if (null == msgSender)
        	return convertView;
        
        holder.m_txtName.setText(msgSender.m_strName);
        if (msgSender.m_nUnreadMsgCnt > 0) {
        	if (msgSender.m_nUnreadMsgCnt > 99) {
        		holder.m_txtUnreadMsgCnt.setText("99+");
        	} else {
        		holder.m_txtUnreadMsgCnt.setText(String.valueOf(msgSender.m_nUnreadMsgCnt));	
        	}
        	holder.m_txtUnreadMsgCnt.setVisibility(View.VISIBLE);	
        } else {
        	holder.m_txtUnreadMsgCnt.setText("");
        	holder.m_txtUnreadMsgCnt.setVisibility(View.GONE);
        }
                
        if (MessageSender.BUDDY == msgSender.m_nType) {
        	Bitmap bmp = getBuddyHeadPic(msgSender.m_nQQUin);
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.h001);			
        	
        	BuddyMessage buddyMsg = (BuddyMessage)msgSender.m_objLastMsg;
        	if (buddyMsg != null) {
        		SpannableStringBuilder strContent = 
        				getSpanStrBuilder(m_Context, buddyMsg.m_arrContent);
        		holder.m_txtDesc.setText(strContent);
        		
        		String strTime = Utils.formatTime(buddyMsg.m_nTime);
                holder.m_txtTime.setText(strTime);
        	} else {
        		holder.m_txtDesc.setText("");
        		holder.m_txtTime.setText("");
        	}
        } else if (MessageSender.GROUP == msgSender.m_nType) {
        	Bitmap bmp = getGroupHeadPic(msgSender.m_nGroupCode);
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.list_grouphead_normal);
        	
        	GroupMessage groupMsg = (GroupMessage)msgSender.m_objLastMsg;
        	if (groupMsg != null) {
        		SpannableStringBuilder strContent = 
        				getSpanStrBuilder(m_Context, groupMsg.m_arrContent);
        		if (strContent != null) {
        			if (msgSender.m_nQQUin != m_QQClient.getUserInfo().m_nQQUin) {
        				GroupList groupList = m_QQClient.getGroupList();
                		BuddyInfo buddyInfo = groupList.getGroupMemberByCode(msgSender.m_nGroupCode, groupMsg.m_nSendUin);
                		if (buddyInfo != null) {
                			if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
                				strContent.insert(0, buddyInfo.m_strGroupCard + ":");
                			else 
                				strContent.insert(0, buddyInfo.m_strNickName + ":");
                		}
            		}
        			holder.m_txtDesc.setText(strContent);
        		} else {
        			holder.m_txtDesc.setText("");
        		}
        		
        		String strTime = Utils.formatTime(groupMsg.m_nTime);
                holder.m_txtTime.setText(strTime);
        	} else {
        		holder.m_txtDesc.setText("");
        		holder.m_txtTime.setText("");
        	}
        } else if (MessageSender.SESS == msgSender.m_nType) {
			Bitmap bmp = getSessHeadPic(msgSender.m_nGroupCode, msgSender.m_nQQUin);
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.h001);
        	
        	SessMessage sessMsg = (SessMessage)msgSender.m_objLastMsg;
        	if (sessMsg != null) {
        		SpannableStringBuilder strContent = 
        				getSpanStrBuilder(m_Context, sessMsg.m_arrContent);
        		holder.m_txtDesc.setText(strContent);
        		
        		String strTime = Utils.formatTime(sessMsg.m_nTime);
                holder.m_txtTime.setText(strTime);
        	} else {
        		holder.m_txtDesc.setText("");
        		holder.m_txtTime.setText("");
        	}
        } else if (MessageSender.SYSTEM == msgSender.m_nType) {
        	
        }
        
//        if (holder.item_right.getVisibility() == View.VISIBLE) {
//        	holder.item_right.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (m_ritemClickListener != null) {
//                    	m_ritemClickListener.onRightItemClick(v, position);
//                    }
//                }
//            });	
//        }
        
        return convertView;
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
				spanStrBuilder.append("[图片]");
			}
		}
		return spanStrBuilder;
	}

	private Bitmap getBuddyHeadPic(int nQQUin) {
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
		task.execute("");
		return null;
	}

	private Bitmap getGroupHeadPic(int nGroupCode) {
    	GroupList groupList = m_QQClient.getGroupList();
    	GroupInfo groupInfo = groupList.getGroupByCode(nGroupCode);
    	if (null == groupInfo) {
    		return null;
    	}
    	
		if (0 == groupInfo.m_nGroupNumber) {
			m_QQClient.updateGroupNum(groupInfo.m_nGroupCode);
			return null;
		}
		
		Bitmap bmp = m_imgCache.get(groupInfo.m_nGroupNumber);
		if (bmp != null) {
			return bmp;
		}
		
		String strFileName = m_QQClient.getGroupHeadPicFullName(groupInfo.m_nGroupNumber);
		File file = new File(strFileName);
		if (!file.exists()) {
			m_QQClient.updateGroupHeadPic(groupInfo.m_nGroupCode, groupInfo.m_nGroupNumber);
			return null;
		}
		
		LoadImageTask task = new LoadImageTask();
		task.m_strKey = String.valueOf(groupInfo.m_nGroupNumber);
		task.m_strFileName = strFileName;
		task.execute("");
		return null;
	}

	private Bitmap getSessHeadPic(int nGroupCode, int nQQUin) {
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
		task.execute("");
		return null;
	}

    static class ViewHolder {
    	ImageView m_imgAvatar;
    	TextView m_txtName;
        TextView m_txtDesc;
        TextView m_txtTime;
        TextView m_txtUnreadMsgCnt;
    }
    
    public void setOnRightItemClickListener(onRightItemClickListener listener){
    	m_ritemClickListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }

    private class LoadImageTask extends AsyncTask<String, Integer, Boolean> {
    	public String m_strKey = "";
    	public String m_strFileName = "";
    	public Bitmap m_Bitmap = null;
    	
        @Override  
        protected Boolean doInBackground(String... params) {
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
        }  
        
        @Override
        protected void onPostExecute(Boolean result) {
        	if (result) {
        		m_imgCache.put(m_strKey, m_Bitmap);
        		MsgAdapter.this.notifyDataSetChanged();
        	}
        }
	}
}
