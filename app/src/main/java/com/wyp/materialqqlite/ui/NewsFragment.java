package com.wyp.materialqqlite.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.BubbleManager;
import com.wyp.materialqqlite.R;

public class NewsFragment extends Fragment 
	implements OnItemClickListener {
	private ListView m_ListView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_news, 
				container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) { 
		super.onActivityCreated(savedInstanceState);
		initView();
	}
		
	private void initView() {
		m_ListView = (ListView)getActivity().findViewById(R.id.news_listview);
		
		int[] arrIconResId = new int[] {
        		0,
        		R.drawable.qq_leba_list_seek_feeds,
        		0,
        		R.drawable.qq_leba_list_seek_gamecenter, 
        		R.drawable.qq_leba_list_seek_member,
                R.drawable.qq_leba_list_seek_read, 
                R.drawable.qq_leba_list_seek_yingyongbao, 
                R.drawable.qq_leba_list_seek_individuation,
                0,
                R.drawable.qq_leba_list_seek_folder, 
                R.drawable.qq_leba_list_seek_life, 
                R.drawable.qq_leba_list_seek_neighbour,
                R.drawable.qq_leba_list_seek_saosao,
                0,
                R.drawable.qq_leba_list_seek_news,
                0
        };
        
    	String[] arrItemText = getResources().getStringArray(R.array.NewsListItemTextArray);
    	
    	List<NewsListItem> arrData = new ArrayList<NewsListItem>();
    	
        for (int i = 0; i < arrItemText.length; i++) {
        	NewsListItem data = new NewsListItem();
        	if (0 == i || i == arrItemText.length-1) {
        		data.m_nType = NewsListItem.TYPE_MARGIN_S;	
        	} else {
        		data.m_nType = ((arrIconResId[i] != 0) ? 
        				NewsListItem.TYPE_CONTENT : NewsListItem.TYPE_MARGIN_L);	
        	}
        	data.m_nIconResId = arrIconResId[i];
        	data.m_strTitle = arrItemText[i];
            arrData.add(data);
        }
        
        NewsListAdapter adapter = new NewsListAdapter(getActivity(), arrData);
        m_ListView.setAdapter(adapter);
        
        m_ListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, 
			View view, int position, long id) {
		// TODO Auto-generated method stub
		int nHeaderCnt = m_ListView.getHeaderViewsCount();
		int nPos = position - nHeaderCnt;
		if (7 == nPos) {	// “气泡、主题、表情”
			BubbleManager bubbleMgr = AppData.getAppData().getBubbleMgr();
			int nBubble = bubbleMgr.getUserBubble();
			nBubble = bubbleMgr.nextBubble(getActivity(), nBubble);
			bubbleMgr.setUserBubble(nBubble);
			bubbleMgr.saveBubbleConfig(getActivity());
			String strTip = getResources().getString(R.string.switch_user_bubble);
			strTip += nBubble;
			Toast.makeText(getActivity(), 
					strTip, Toast.LENGTH_LONG).show();
		}
	}
}
