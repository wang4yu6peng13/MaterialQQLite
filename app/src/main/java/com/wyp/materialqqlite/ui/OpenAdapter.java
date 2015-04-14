package com.wyp.materialqqlite.ui;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wyp.materialqqlite.R;

import java.util.ArrayList;
import java.util.List;

public class OpenAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    public static final List<String> open_list = new ArrayList<String>();//为条目提供数据
    public OpenAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        open_list.add("MingQQ");
        open_list.add("MaterialTabs");
        open_list.add("material-dialogs");
        open_list.add("switchButton");
        open_list.add("MaterialWidget");
        open_list.add("android-PullRefreshLayout");
        open_list.add("SwipeBackLayout");
        open_list.add(context.getResources().getString(R.string.app_name));

    }
    @Override
    public int getCount() {
        return 8;// open_list.size();  //条目数量
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
                    R.layout.open_listitem, null);
        }
        TextView link = (TextView) convertView.findViewById(R.id.open_list_item_link);
        link.setAutoLinkMask(Linkify.WEB_URLS);
        switch (position){
            case 0:
                link.setText("https://github.com/zym2014/MingQQ");
                break;
            case 1:
                link.setText("https://github.com/neokree/MaterialTabs");
                break;
            case 2:
                link.setText("https://github.com/afollestad/material-dialogs");
                break;
            case 3:
                link.setText("https://github.com/kyleduo/SwitchButton");
                break;
            case 4:
                link.setText("https://github.com/keithellis/MaterialWidget");
                break;
            case 5:
                link.setText("https://github.com/baoyongzhang/android-PullRefreshLayout");
                break;
            case 6:
                link.setText("https://github.com/ikew0ng/SwipeBackLayout");
                break;
            case 7:
                link.setText("https://github.com/wang4yu6peng13/MaterialQQLite");
                break;
        }

        TextView text = (TextView) convertView.findViewById(R.id.open_list_item); //设置条目的文字说明
        text.setText(open_list.get(position));
        return convertView;
    }
}
