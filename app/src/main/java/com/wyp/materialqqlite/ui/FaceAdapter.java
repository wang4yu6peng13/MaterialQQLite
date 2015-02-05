package com.wyp.materialqqlite.ui;

import java.util.List;

import com.wyp.materialqqlite.FaceInfo;
import com.wyp.materialqqlite.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class FaceAdapter extends BaseAdapter {

	private Context m_Context;
	private List<FaceInfo> m_facePage = null;

    public FaceAdapter(Context context, List<FaceInfo> facePage) {
        m_Context = context;
        m_facePage = facePage;
    }

    @Override
    public int getCount() {
        if (m_facePage != null)
        	return m_facePage.size();
        else
        	return 0;
    }

    @Override
    public Object getItem(int position) {
    	if (m_facePage != null)
        	return m_facePage.get(position);
        else
        	return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
        	convertView = LayoutInflater.from(m_Context).inflate(
    				R.layout.face_listitem, parent, false);
        	
            holder = new ViewHolder();
            holder.m_imgFace = (ImageView)convertView
            		.findViewById(R.id.facelistitem_imgFace);
            
            convertView.setTag(holder);
        } else {
        	holder = (ViewHolder)convertView.getTag();
        }
        
        FaceInfo faceInfo = m_facePage.get(position);
        if (faceInfo != null) {
        	if (faceInfo.m_nResId != 0) {
        		holder.m_imgFace.setImageResource(faceInfo.m_nResId);
        	}
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView m_imgFace;
    }
}