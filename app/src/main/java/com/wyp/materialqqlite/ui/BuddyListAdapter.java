package com.wyp.materialqqlite.ui;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyp.materialqqlite.ImageCache;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyTeamInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQStatus;


public class BuddyListAdapter extends BaseExpandableListAdapter {
    private Context m_Context;
    private BuddyList m_BuddyList;
    private ImageCache m_imgCache;
    private QQClient m_QQClient;
    private int m_cxAvatar, m_cyAvatar;
    private int m_pxAvatarRound;

    public BuddyListAdapter(Context context, BuddyList buddyList) {
    	m_Context = context;
        m_BuddyList = buddyList;
        m_imgCache = new ImageCache();
        m_QQClient = AppData.getAppData().getQQClient();
        m_cxAvatar = (int)context.getResources().getDimension(R.dimen.BList_cxAvatar);
        m_cyAvatar = (int)context.getResources().getDimension(R.dimen.BList_cyAvatar);
        m_pxAvatarRound = (int)context.getResources().getDimension(R.dimen.pxAvatarRound);
    }

    public void setData(BuddyList buddyList) {
    	m_BuddyList = buddyList;
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
    	if (m_BuddyList != null)
    		return m_BuddyList.getBuddyTeamCount();
    	else
    		return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        if (m_BuddyList != null)
    		return m_BuddyList.getBuddyCount(groupPosition);
    	else
    		return 0;
    }

    @Override
    public BuddyTeamInfo getGroup(int groupPosition) {
        // TODO Auto-generated method stub
    	if (m_BuddyList != null)
    		return m_BuddyList.getBuddyTeam(groupPosition);
    	else
    		return null;
    }

    @Override
    public BuddyInfo getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
    	if (m_BuddyList != null)
    		return m_BuddyList.getBuddy(groupPosition, childPosition);
    	else
    		return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
    	
    	ViewHolder1 holder = null;
        if (null == convertView) {
        	convertView = LayoutInflater.from(m_Context)
        			.inflate(R.layout.buddy_list_item_1, parent, false);
            
            holder = new ViewHolder1();
            holder.m_imgArrow = (ImageView)convertView.findViewById(R.id.blistitem1_imgArrow);
            holder.m_txtTeamName = (TextView)convertView.findViewById(R.id.blistitem1_txtTeamName);
            holder.m_txtBuddyCnt = (TextView)convertView.findViewById(R.id.blistitem1_txtBuddyCnt);
            convertView.setTag(holder);
        } else {
        	holder = (ViewHolder1)convertView.getTag();
        }
        
        if (null == holder)
        	return convertView;
        
        Boolean oldExpanded = (Boolean)holder.m_imgArrow.getTag();
        if (null == oldExpanded || oldExpanded != isExpanded) {
            if (isExpanded) {
            	holder.m_imgArrow.setImageResource(
            			R.drawable.skin_indicator_expanded);
            }else{
            	holder.m_imgArrow.setImageResource(
            			R.drawable.skin_indicator_unexpanded);
            }
            holder.m_imgArrow.setTag(isExpanded);
        }
        
    	if (m_BuddyList != null) {
    		BuddyTeamInfo buddyTeamInfo = m_BuddyList.getBuddyTeam(groupPosition);
            if (buddyTeamInfo != null) {
            	holder.m_txtTeamName.setText(buddyTeamInfo.m_strName);
            	String strText = buddyTeamInfo.getOnlineBuddyCount() + "/" + buddyTeamInfo.getBuddyCount();
            	holder.m_txtBuddyCnt.setText(strText);
            }
    	}
        
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
    	
    	ViewHolder2 holder = null;
        if (null == convertView) {
        	convertView = LayoutInflater.from(m_Context)
        			.inflate(R.layout.buddy_list_item_2, parent, false);
            
            holder = new ViewHolder2();
            holder.m_imgAvatar = (ImageView)convertView.findViewById(R.id.blistitem2_imgAvatar);
            holder.m_txtName = (TextView)convertView.findViewById(R.id.blistitem2_txtName);
            holder.m_txtDesc = (TextView)convertView.findViewById(R.id.blistitem2_txtDesc);
            holder.m_imgstatus = (ImageView)convertView.findViewById(R.id.friend_State);
            convertView.setTag(holder);
        } else {
        	holder = (ViewHolder2)convertView.getTag();
        }
        
        if (null == holder || null == m_BuddyList)
        	return convertView;
        
    	BuddyInfo buddyInfo = m_BuddyList.getBuddy(groupPosition, childPosition);
    	if (null == buddyInfo)
    		return convertView;
		
		Bitmap bmp = getBuddyHeadPic(buddyInfo.m_nQQUin);
		if (bmp != null)
			holder.m_imgAvatar.setImageBitmap(bmp);
		else
			holder.m_imgAvatar.setImageResource(R.drawable.h001);
		
		if (buddyInfo.m_nStatus != QQStatus.OFFLINE) {
            holder.m_imgAvatar.setAlpha(255);
            holder.m_imgstatus.setImageResource(R.drawable.status_online_btn_2);
        }else{
			holder.m_imgAvatar.setAlpha(120);
        holder.m_imgstatus.setImageResource(R.drawable.status_offline);}
		// 设置昵称
		if (!Utils.isEmptyStr(buddyInfo.m_strMarkName))
			holder.m_txtName.setText(buddyInfo.m_strMarkName);
		else
			holder.m_txtName.setText(buddyInfo.m_strNickName);
		
		// 设置个性签名
		String strText = "";
		if (buddyInfo.m_nStatus != QQStatus.OFFLINE) {
            strText = "[在线]";

        }else {
            strText = "[离线请留言]";

        }
		if (buddyInfo.isHasQQSign())
			strText += buddyInfo.m_strSign;
		else
			m_QQClient.updateBuddySign(buddyInfo.m_nQQUin);
		
		holder.m_txtDesc.setText(strText);
        
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        /*很重要：实现ChildView点击事件，必须返回true*/
        return true;
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

    private class ViewHolder1 {
    	ImageView m_imgArrow;
        TextView m_txtTeamName;
        TextView m_txtBuddyCnt;
    }

    private class ViewHolder2 {
        ImageView m_imgAvatar;
        TextView m_txtName;
        TextView m_txtDesc;
        ImageView m_imgstatus;
    }

    class LoadImageTask extends AsyncTask<String, Integer, Boolean> {
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
        		BuddyListAdapter.this.notifyDataSetChanged();
        	}
        }
	}
}
