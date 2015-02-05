package com.wyp.materialqqlite.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;

import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.HomeWatcher;
import com.wyp.materialqqlite.HomeWatcher.OnHomePressedListener;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class GroupFragment extends Fragment
	implements OnItemClickListener, OnHomePressedListener {
	//private TextView m_txtBack;
	//private ImageButton m_btnAdd;
	//private LinearLayout m_searchBar;
	private ListView m_lvGroupList;
    private PullRefreshLayout swipeRefreshLayout_group;
	private GroupListAdapter m_glistAdapter;
	private QQClient m_QQClient;
	private GroupList m_groupList;
	private HomeWatcher mHomeWatcher;

	private Handler m_Handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case QQCallBackMsg.UPDATE_GROUP_LIST:
				m_glistAdapter.notifyDataSetChanged();
				break;
			case QQCallBackMsg.UPDATE_GROUP_NUMBER: {
				int nGroupCode = msg.arg1;
				//ListView actualListView = m_lvGroupList.getRefreshableView();
				int nIndex = m_groupList.getGroupIndexByCode(nGroupCode);
				if (nIndex != -1) {
					if (nIndex >= m_lvGroupList.getFirstVisiblePosition()
							&& nIndex <=m_lvGroupList.getLastVisiblePosition()) {
						m_glistAdapter.notifyDataSetChanged();
					}
				}
				break;	
			}
			case QQCallBackMsg.UPDATE_GROUP_HEADPIC: {
				int nGroupCode = msg.arg1;
				//ListView actualListView = m_lvGroupList.getRefreshableView();
				int nIndex = m_groupList.getGroupIndexByCode(nGroupCode);
				if (nIndex != -1) {
					if (nIndex >= m_lvGroupList.getFirstVisiblePosition()
							&& nIndex <=m_lvGroupList.getLastVisiblePosition()) {
						m_glistAdapter.notifyDataSetChanged();
					}
				}
				break;
			}
			default:
				break;
			}
		}
	};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group,
                null);
        m_lvGroupList =( ListView)view.findViewById(R.id.group_lvGList);
        swipeRefreshLayout_group= (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_group);
    //    m_headerBar = (LinearLayout)inflater.inflate(R.layout.buddy_list_header, null);
    //    mListView.getRefreshableView().addHeaderView(m_headerBar);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



	@Override
	public void onResume() {
		super.onResume();
		m_QQClient.setCallBackHandler(m_Handler);
		AppData.getAppData().cancelNotify(1);
		mHomeWatcher = new HomeWatcher(getActivity());
		mHomeWatcher.setOnHomePressedListener(this);
		mHomeWatcher.startWatch();
	}
	
	@Override
    public void onStop(){
        super.onStop();
        m_QQClient.setNullCallBackHandler(m_Handler);
        mHomeWatcher.setOnHomePressedListener(null);
		mHomeWatcher.stopWatch();
    }

	
	private void initView() {
        m_QQClient = AppData.getAppData().getQQClient();
        m_QQClient.setCallBackHandler(m_Handler);
        m_groupList = m_QQClient.getGroupList();
		
	//	m_txtBack = (TextView)findViewById(R.id.group_txtBack);
	//	m_btnAdd = (ImageButton)findViewById(R.id.group_btnAdd);
	//	m_lvGroupList = (PullToRefreshListView)findViewById(R.id.group_lvGList);
		
	//	m_searchBar = (LinearLayout)((LayoutInflater)getSystemService(
    //            Context.LAYOUT_INFLATER_SERVICE)).inflate(
    //            R.layout.searchbar, null);
		//ListView actualListView = m_lvGroupList.getRefreshableView();
	//	actualListView.addHeaderView(m_searchBar);

		swipeRefreshLayout_group.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute();
                swipeRefreshLayout_group.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout_group.setRefreshing(false);
                        //refresh();
                        m_glistAdapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });

	//	m_lvGroupList.getLoadingLayoutProxy().setPullLabel("下拉刷新");
	//	m_lvGroupList.getLoadingLayoutProxy().setReleaseLabel("释放立即刷新");
	//	m_lvGroupList.getLoadingLayoutProxy().setRefreshingLabel("正在刷新...");
	//	m_lvGroupList.getLoadingLayoutProxy().setLastUpdatedLabel("");
		
		m_glistAdapter = new GroupListAdapter(getActivity(), m_groupList);
		m_lvGroupList.setAdapter(m_glistAdapter);
		m_lvGroupList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, 
			View view, int position, long id) {
		// TODO Auto-generated method stub
	//	ListView actualListView = m_lvGroupList.getRefreshableView();
		int nHeaderCnt = m_lvGroupList.getHeaderViewsCount();
		int nPos = position - nHeaderCnt;
		GroupInfo groupInfo = m_groupList.getGroup(nPos);
		if (null == groupInfo)
			return;
		
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("type", ChatActivity.IS_GROUP);
		bundle.putInt("useruin", m_QQClient.getUserInfo().m_nQQUin);
        bundle.putString("username", m_QQClient.getUserInfo().m_strNickName);
        bundle.putInt("groupcode", groupInfo.m_nGroupCode);
        bundle.putInt("groupid", groupInfo.m_nGroupId);
    	bundle.putInt("groupnum", groupInfo.m_nGroupNumber);
    	bundle.putString("groupname", groupInfo.m_strName);
    	bundle.putInt("qquin", 0);
        bundle.putInt("qqnum", 0);
        bundle.putString("buddyname", "");
        intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onHomePressed() {
		String strTicker = getString(R.string.bgrun);
		String strTitle = getString(R.string.app_name);
		String strText = getString(R.string.nonewmsg);
		AppData.getAppData().showNotify(1, getActivity(),
				strTicker, strTitle, strText);
	}

	@Override
	public void onHomeLongPressed() {
		// do nothing
	}

	private class GetDataTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			//m_lvGroupList.onRefreshComplete();
swipeRefreshLayout_group.setRefreshing(false);
			super.onPostExecute(result);
		}
	}
}
