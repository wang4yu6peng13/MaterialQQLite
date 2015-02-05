package com.wyp.materialqqlite.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wyp.materialqqlite.R;

public class NewsListAdapter extends BaseAdapter {
    private Context m_Context = null;
    private List<NewsListItem> m_arrData;
    
    public NewsListAdapter(Context context, List<NewsListItem> data) {
    	m_Context = context;
    	m_arrData = data;
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
        return NewsListItem.TYPE_COUNT;
    }  
       
    @Override 
    public int getItemViewType(int position) {
    	if (m_arrData != null) {
    		NewsListItem data = m_arrData.get(position);
    		if (data != null)
    			return data.m_nType;
    	}
        return 0;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	
    	NewsListItem data = m_arrData.get(position);
    	if (null == data)
    		return convertView;
    	
    	ViewHolder holder = null;
    	switch (data.m_nType) {
		case NewsListItem.TYPE_CONTENT:
    		if (null == convertView) {
    			convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.news_list_item, parent, false);
        		
        		holder = new ViewHolder();
        		holder.m_layoutBg = (LinearLayout)
        				convertView.findViewById(R.id.news_item_bg);
        		holder.m_imgIcon = (ImageView)
                		convertView.findViewById(R.id.news_item_imgIcon);
                holder.m_txtTitle = (TextView)
                		convertView.findViewById(R.id.news_item_txtTitle);
                holder.m_txtDesc = (TextView)
                		convertView.findViewById(R.id.news_item_txtDesc);
                
                convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder)convertView.getTag();
    		}
			break;
			
		case NewsListItem.TYPE_MARGIN_L:
    		if (null == convertView) {
    			convertView = LayoutInflater.from(m_Context).inflate(
        				R.layout.list_item_margin_l, parent, false);
    		}
			break;
			
		case NewsListItem.TYPE_MARGIN_S:
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
		
		if (NewsListItem.TYPE_CONTENT == data.m_nType) {
			if (data.m_nIconResId != 0)
				holder.m_imgIcon.setBackgroundResource(data.m_nIconResId);
			else
				holder.m_imgIcon.setVisibility(View.GONE);
			holder.m_txtTitle.setText(data.m_strTitle);
		}
		
        return convertView;
    }
    
    private boolean isFirstSubItem(int nPos) {
    	if (nPos < 0 || nPos >= m_arrData.size())
    		return false;
    	
    	NewsListItem data = m_arrData.get(nPos);
    	if (data.m_nType != NewsListItem.TYPE_CONTENT)
    		return false;
    	
    	if (nPos == 0)
    		return true;
    	
    	data = m_arrData.get(nPos-1);
    	return (data.m_nType != NewsListItem.TYPE_CONTENT);
    }
    
    private boolean isLastSubItem(int nPos) {
    	if (nPos < 0 || nPos >= m_arrData.size())
    		return false;
    	
    	NewsListItem data = m_arrData.get(nPos);
    	if (data.m_nType != NewsListItem.TYPE_CONTENT)
    		return false;
    	
    	if (nPos == m_arrData.size()-1)
    		return true;
    	
    	data = m_arrData.get(nPos+1);
    	return (data.m_nType != NewsListItem.TYPE_CONTENT);
    }
    
    private boolean isSingleSubItem(int nPos) {
    	return isFirstSubItem(nPos) && isLastSubItem(nPos);
    }
    
    private class ViewHolder {
    	LinearLayout m_layoutBg;
        ImageView m_imgIcon;
        TextView m_txtTitle;
        TextView m_txtDesc;
    }
}
