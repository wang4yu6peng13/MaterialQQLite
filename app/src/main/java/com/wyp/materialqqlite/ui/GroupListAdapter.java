package com.wyp.materialqqlite.ui;

import java.io.File;

import com.wyp.materialqqlite.ImageCache;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupListAdapter extends BaseAdapter {
    private Context m_Context;
    private GroupList m_groupList;
    private ImageCache m_imgCache;
    private QQClient m_QQClient;
    private int m_cxAvatar, m_cyAvatar;
    private int m_pxAvatarRound;
    
    public GroupListAdapter(Context context, GroupList groupList) {
    	m_Context = context;
        m_groupList = groupList;
        m_imgCache = new ImageCache();
        m_QQClient = AppData.getAppData().getQQClient();
        m_cxAvatar = (int)context.getResources().getDimension(R.dimen.GList_cxAvatar);
        m_cyAvatar = (int)context.getResources().getDimension(R.dimen.GList_cyAvatar);
        m_pxAvatarRound = (int)context.getResources().getDimension(R.dimen.pxAvatarRound);
    }
    
    @Override
    public int getCount() {
    	if (m_groupList != null)
    		return m_groupList.getGroupCount();
    	else
    		return 0;
    }

    @Override
    public Object getItem(int position) {
    	if (m_groupList != null)
    		return m_groupList.getGroup(position);
    	else
    		return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(m_Context).inflate(R.layout.group_list_item, parent, false);
            holder = new ViewHolder();
            holder.m_imgAvatar = (ImageView)convertView.findViewById(R.id.glistitem_imgAvatar);
            holder.m_txtName = (TextView)convertView.findViewById(R.id.glistitem_txtName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        if (null == holder || null == m_groupList)
        	return convertView;
        
    	GroupInfo groupInfo = m_groupList.getGroup(position);
    	if (groupInfo != null) {
        	Bitmap bmp = getGroupHeadPic(groupInfo.m_nGroupCode);
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.list_grouphead_normal);
            
    		holder.m_txtName.setText(groupInfo.m_strName);
    	}
        
        return convertView;
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

    static class ViewHolder {
    	ImageView m_imgAvatar;
        TextView m_txtName;
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
        		GroupListAdapter.this.notifyDataSetChanged();
        	}
        }
	}
}
