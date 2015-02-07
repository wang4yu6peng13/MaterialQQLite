package com.wyp.materialqqlite.ui;

import java.io.File;
import java.util.List;

import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQStatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingListAdapter extends BaseAdapter {
    private Context m_Context;
    private List<SettingListItem> m_arrData;
    private int m_cxAvatar, m_cyAvatar;
    private int m_pxAvatarRound;

    public SettingListAdapter(Context context, List<SettingListItem> data) {
    	m_Context = context;
    	m_arrData = data;
        m_cxAvatar = (int)context.getResources().getDimension(R.dimen.msgList_cxAvatar);
        m_cyAvatar = (int)context.getResources().getDimension(R.dimen.msgList_cyAvatar);
        m_pxAvatarRound = (int)context.getResources().getDimension(R.dimen.pxAvatarRound);
    }

    @Override
    public int getCount() {
    	if (m_arrData != null)
    		return m_arrData.size();
    	else
    		return 0;
    }

    @Override
    public Object getItem(int position) {
    	if (m_arrData != null && 
    			position >= 0 && position < m_arrData.size())
    		return m_arrData.get(position);
    	else
    		return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override 
    public int getViewTypeCount() {  
        return SettingListItem.TYPE_COUNT;
    }  
       
    @Override 
    public int getItemViewType(int position) {
    	if (m_arrData != null) {
    		SettingListItem data = m_arrData.get(position);
    		if (data != null)
    			return data.m_nType;
    	}
        return 0;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	
    	SettingListItem data = m_arrData.get(position);
    	if (null == data)
    		return convertView;
    	
    	ViewHolder holder = null;
    	switch (data.m_nType) {
		case SettingListItem.TYPE_CONTENT:
    		if (null == convertView) {
    			convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.setting_list_item_0, parent, false);
        		
        		holder = new ViewHolder();
        		holder.m_layoutBg = (LinearLayout)
        				convertView.findViewById(R.id.setting_item_bg);
                holder.m_txtTitle = (TextView)
                		convertView.findViewById(R.id.setting_item_txtTitle);
                holder.m_txtDesc = (TextView)
                		convertView.findViewById(R.id.setting_item_txtDesc);
                
                convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder)convertView.getTag();
    		}
			break;

		case SettingListItem.TYPE_USERINFO:
    		if (null == convertView) {
    			convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.setting_list_item_1, parent, false);
        		
        		holder = new ViewHolder();
        		holder.m_layoutBg = (LinearLayout)
        				convertView.findViewById(R.id.setting_item_bg);
                holder.m_imgAvatar = (ImageView)
                		convertView.findViewById(R.id.setting_item_imgAvatar);
                holder.m_imgState = (ImageView)
                		convertView.findViewById(R.id.setting_item_imgState);
                holder.m_txtName = (TextView)
                		convertView.findViewById(R.id.setting_item_txtName);
                holder.m_txtSign = (TextView)
                        convertView.findViewById(R.id.setting_item_txtSign);
                convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder)convertView.getTag();
    		}
			break;
			
		case SettingListItem.TYPE_MARGIN_M:
    		if (null == convertView) {
    			convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.list_item_margin_m, parent, false);
    		}
			break;
			
		case SettingListItem.TYPE_MARGIN_S:
		default:
			if (null == convertView) {
    			convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.list_item_margin_s, parent, false);
    		}
			break;
		}    		
    	
    	if (null == holder)
			return convertView;
    	
		if (isSingleSubItem(position)) {
			holder.m_layoutBg.setBackgroundResource(R.drawable.listitem_bg_style);
		} else if (isFirstSubItem(position)) {
			holder.m_layoutBg.setBackgroundResource(R.drawable.listitem_bg_top_style);
		} else if (isLastSubItem(position)) {
			holder.m_layoutBg.setBackgroundResource(R.drawable.listitem_bg_bottom_style);
		} else {
			holder.m_layoutBg.setBackgroundResource(R.drawable.listitem_bg_mid_style);
		}
		
		if (SettingListItem.TYPE_CONTENT == data.m_nType) {
//    		StateListDrawable bg = new StateListDrawable();
//    		Drawable normal = m_Context.getResources().getDrawable(R.drawable.skin_setting_strip_middle_unpressed);
//    		//Drawable selected = this.getResources().getDrawable(mImageIds[1]);
//    		Drawable pressed = m_Context.getResources().getDrawable(R.drawable.skin_setting_strip_middle_pressed);
//    		bg.addState(new int[]{android.R.attr.state_pressed}, pressed);
//    		//bg.addState(View.ENABLED_FOCUSED_STATE_SET, selected);
//    		bg.addState(new int[]{}, normal);
//    		holder.m_layoutBg.setBackgroundDrawable(bg);

			holder.m_txtTitle.setText(data.m_strTitle);
			holder.m_txtDesc.setText("");
/*			if (7 == position) {	// 个性签名
				QQClient client = AppData.getAppData().getQQClient();
				BuddyInfo buddyInfo = client.getUserInfo();
				if (!buddyInfo.isHasQQSign()) {
					client.updateBuddySign(buddyInfo.m_nQQUin);
				} else {
					holder.m_txtDesc.setText(buddyInfo.m_strSign);
				}
			}*/
		} else if(SettingListItem.TYPE_USERINFO == data.m_nType) {
			Bitmap bmp = null;
			QQClient client = AppData.getAppData().getQQClient();
			BuddyInfo buddyInfo = client.getUserInfo();
			String strFileName = client.
					getBuddyHeadPicFullName(client.getUserInfo().m_nQQNum);
			File file = new File(strFileName);
			if (!file.exists()) {
	        	client.updateBuddyHeadPic(buddyInfo.m_nQQUin, buddyInfo.m_nQQNum);
			} else {
				bmp = BitmapFactory.decodeFile(strFileName);
	        	if (bmp != null) {
	        		bmp = Utils.zoomImg(bmp, m_cxAvatar, m_cyAvatar);        		
	        		bmp = Utils.getRoundedCornerBitmap(bmp, m_pxAvatarRound);
	        	}
			}
			
			if (bmp != null)
				holder.m_imgAvatar.setImageBitmap(bmp);
			else
				holder.m_imgAvatar.setImageResource(R.drawable.h001);
			
			int nStatus = client.getStatus();
			if (nStatus != QQStatus.HIDDEN)
				holder.m_imgState.setImageResource(R.drawable.status_online_btn_2);
			else
				holder.m_imgState.setImageResource(R.drawable.status_invisible_btn_2);
			
			if (!Utils.isEmptyStr(buddyInfo.m_strNickName))
				holder.m_txtName.setText(buddyInfo.m_strNickName);
			else
				holder.m_txtName.setText("");
            if (!buddyInfo.isHasQQSign()) {
                client.updateBuddySign(buddyInfo.m_nQQUin);
            } else {
                holder.m_txtSign.setText(buddyInfo.m_strSign);
            }
		}
		
        return convertView;
    }
    
    private boolean isFirstSubItem(int nPos) {
    	if (nPos < 0 || nPos >= m_arrData.size())
    		return false;
    	
    	SettingListItem data = m_arrData.get(nPos);
    	if (data.m_nType != SettingListItem.TYPE_CONTENT
    			&& data.m_nType != SettingListItem.TYPE_USERINFO)
    		return false;
    	
    	if (nPos == 0)
    		return true;
    	
    	data = m_arrData.get(nPos-1);
    	return (data.m_nType == SettingListItem.TYPE_MARGIN_S
    			|| data.m_nType == SettingListItem.TYPE_MARGIN_M);
    }
    
    private boolean isLastSubItem(int nPos) {
    	if (nPos < 0 || nPos >= m_arrData.size())
    		return false;
    	
    	SettingListItem data = m_arrData.get(nPos);
    	if (data.m_nType != SettingListItem.TYPE_CONTENT
    			&& data.m_nType != SettingListItem.TYPE_USERINFO)
    		return false;
    	
    	if (nPos == m_arrData.size()-1)
    		return true;
    	
    	data = m_arrData.get(nPos+1);
    	return (data.m_nType == SettingListItem.TYPE_MARGIN_S
    			|| data.m_nType == SettingListItem.TYPE_MARGIN_M);
    }
    
    private boolean isSingleSubItem(int nPos) {
    	return isFirstSubItem(nPos) && isLastSubItem(nPos);
    }
    
    private class ViewHolder {
    	LinearLayout m_layoutBg;
        ImageView m_imgAvatar;
        ImageView m_imgState;
        TextView m_txtName;
        TextView m_txtSign;
        TextView m_txtTitle;
        TextView m_txtDesc;
    }
}
