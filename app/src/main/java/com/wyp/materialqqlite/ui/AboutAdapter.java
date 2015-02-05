package com.wyp.materialqqlite.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wyp.materialqqlite.R;

import java.util.ArrayList;
import java.util.List;

public class AboutAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    public static final List<String> about_list = new ArrayList<String>();//为条目提供数据
    public AboutAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        about_list.add(context.getResources().getString(R.string.readme));
        about_list.add(context.getResources().getString(R.string.opensource));
        about_list.add(context.getResources().getString(R.string.developers));
        about_list.add(context.getResources().getString(R.string.crashlog));
    }
    @Override
    public int getCount() {
        return about_list.size();  //条目数量
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.about_listitem, null);
        }

        TextView text = (TextView) convertView.findViewById(R.id.about_list_item); //设置条目的文字说明
        text.setText(about_list.get(position));
        return convertView;
    }
}
