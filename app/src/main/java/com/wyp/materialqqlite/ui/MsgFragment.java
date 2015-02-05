package com.wyp.materialqqlite.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.widget.PullRefreshLayout;
import com.wyp.materialqqlite.FaceList;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.Utils;

import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.msglog.MessageLogger;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GetQQNumResult;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageSender;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;

public class MsgFragment extends Fragment implements  OnItemClickListener {
    //public class MsgFragment extends Fragment implements OnClickListener, OnItemClickListener {
//	private CheckBox m_imgLight;
//	private PullToRefreshListView m_lvMsg;

    private ListView m_lvMsg;

    private PullRefreshLayout layout;
	private MsgAdapter m_msgAdapter;
	private QQClient m_QQClient;
	private MessageList m_msgList;
//	private LinearLayout m_searchBar;

	private FaceList m_faceList;				// 表情列表	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_msg,
				container, false);

        return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) { 
		super.onActivityCreated(savedInstanceState);
   //     refresh();
		initView();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	public void onTabChange() {
		if (m_msgAdapter != null)
			m_msgAdapter.notifyDataSetChanged();
	}
	
	private void initView() {
		m_QQClient = AppData.getAppData().getQQClient();
		m_msgList = m_QQClient.getMessageList();
		
	//	m_imgLight = (CheckBox)getActivity().findViewById(R.id.msg_imgLight);
	//	m_lvMsg = (PullToRefreshListView)getActivity().findViewById(R.id.msg_lvMsg);
        m_lvMsg = (ListView)getActivity().findViewById(R.id.msg_lvMsg);
        layout=(PullRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout_msg);

	//	m_imgLight.setOnClickListener(this);
		
		layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
				new GetDataTask().execute();
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setRefreshing(false);
                       refresh();
                    }
                }, 1000);
			}
		});


	//	m_lvMsg.getLoadingLayoutProxy().setPullLabel("下拉刷新");
	//	m_lvMsg.getLoadingLayoutProxy().setReleaseLabel("释放立即刷新");
	//	m_lvMsg.getLoadingLayoutProxy().setRefreshingLabel("正在刷新...");
	//	m_lvMsg.getLoadingLayoutProxy().setLastUpdatedLabel("");
		
//		m_searchBar = (LinearLayout)((LayoutInflater)getActivity().getSystemService(
//                Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                R.layout.searchbar, null);
		//ListView actualListView = m_lvMsg.getRefreshableView();
//		actualListView.addHeaderView(m_searchBar);
		//m_lvMsg.getLoadingLayoutProxy().setLoadingDrawable(R.drawable.progressbar_anim);
		
//		ListView actualListView = m_lvMsg.getRefreshableView();
		
		m_faceList = AppData.getAppData().getFaceList();

		m_msgAdapter = new MsgAdapter(getActivity(), m_msgList, m_faceList);
		m_msgAdapter.setOnRightItemClickListener(new MsgAdapter.onRightItemClickListener() {
            @Override
            public void onRightItemClick(View v, int position) {
            	Toast.makeText(getActivity(), "删除第  " + (position+1)+" 对话记录",
            			Toast.LENGTH_SHORT).show();
            }
        });
        
		m_lvMsg.setAdapter(m_msgAdapter);
        m_lvMsg.setOnItemClickListener(this);
        
		MessageLogger msgLogger = m_QQClient.getMsgLogger();
		if (!msgLogger.isOpen()) {
			String strPath = m_QQClient.getMsgLogFullName(0);
			msgLogger.open(strPath);
		}
        refresh();
	}

    private void refresh() {
        m_msgAdapter.notifyDataSetChanged();


    }

    /*
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.msg_imgLight:
                //m_imgLight.setImageResource(R.drawable.skin_conversation_title_right_btn_selected);
                break;
            default:
                break;
            }
        }
    */
	@Override
	public void onItemClick(AdapterView<?> parent, 
			View view, int position, long id) {
		// TODO Auto-generated method stub
		//ListView actualListView = m_lvMsg.getRefreshableView();
		int nHeaderCnt = m_lvMsg.getHeaderViewsCount();
		int nPos = position - nHeaderCnt;
		MessageSender msgSender = m_msgList.getMsgSender(nPos);
		if (msgSender != null) {
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			Bundle bundle = new Bundle();
			if (MessageSender.BUDDY == msgSender.m_nType) {
				bundle.putInt("type", ChatActivity.IS_BUDDY);
				bundle.putInt("useruin", m_QQClient.getUserInfo().m_nQQUin);
	            bundle.putString("username", m_QQClient.getUserInfo().m_strNickName);
	            bundle.putInt("groupcode", 0);
	            bundle.putInt("groupid", 0);
	            bundle.putInt("groupnum", 0);
	            bundle.putInt("qquin", msgSender.m_nQQUin);
	            BuddyList buddyList = m_QQClient.getBuddyList();
	            BuddyInfo buddyInfo = buddyList.getBuddy(msgSender.m_nQQUin);
	            if (buddyInfo != null) {
	            	bundle.putInt("qqnum", buddyInfo.m_nQQNum);
	            	if (!Utils.isEmptyStr(buddyInfo.m_strMarkName))
	            		bundle.putString("buddyname", buddyInfo.m_strMarkName);
	            	else
	            		bundle.putString("buddyname", buddyInfo.m_strNickName);
	            } else {
	            	bundle.putInt("qqnum", 0);
		            bundle.putString("buddyname", "");
	            }
			} else if (MessageSender.GROUP == msgSender.m_nType) {
				bundle.putInt("type", ChatActivity.IS_GROUP);
				bundle.putInt("useruin", m_QQClient.getUserInfo().m_nQQUin);
	            bundle.putString("username", m_QQClient.getUserInfo().m_strNickName);
	            bundle.putInt("groupcode", msgSender.m_nGroupCode);
	            GroupList groupList = m_QQClient.getGroupList();
	            GroupInfo groupInfo = groupList.getGroupByCode(msgSender.m_nGroupCode);
	            if (groupInfo != null) {
	            	bundle.putInt("groupid", groupInfo.m_nGroupId);
	            	bundle.putInt("groupnum", groupInfo.m_nGroupNumber);
	            	bundle.putString("groupname", groupInfo.m_strName);
	            } else {
	            	bundle.putInt("groupid", 0);
	            	bundle.putInt("groupnum", 0);
	            	bundle.putString("groupname", "");
	            }
	            bundle.putInt("qquin", 0);
	            bundle.putInt("qqnum", 0);
	            bundle.putString("buddyname", "");	            
			} else if (MessageSender.SESS == msgSender.m_nType) {
				bundle.putInt("type", ChatActivity.IS_SESS);
				bundle.putInt("useruin", m_QQClient.getUserInfo().m_nQQUin);
	            bundle.putString("username", m_QQClient.getUserInfo().m_strNickName);
	            bundle.putInt("groupcode", msgSender.m_nGroupCode);
	            bundle.putInt("qquin", msgSender.m_nQQUin);
	            GroupList groupList = m_QQClient.getGroupList();
	            GroupInfo groupInfo = groupList.getGroupByCode(msgSender.m_nGroupCode);
	            if (groupInfo != null) {
	            	bundle.putInt("groupid", groupInfo.m_nGroupId);
	            	bundle.putInt("groupnum", groupInfo.m_nGroupNumber);
	            	bundle.putString("groupname", groupInfo.m_strName);
	            	BuddyInfo buddyInfo = groupInfo.getMemberByUin(msgSender.m_nQQUin);
	            	if (buddyInfo != null) {
		            	bundle.putInt("qqnum", buddyInfo.m_nQQNum);
		            	if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
		            		bundle.putString("buddyname", buddyInfo.m_strGroupCard);
		            	else
		            		bundle.putString("buddyname", buddyInfo.m_strNickName);
		            } else {
		            	bundle.putInt("qqnum", 0);
			            bundle.putString("buddyname", "");
		            }
	            } else {
	            	bundle.putInt("groupid", 0);
	            	bundle.putInt("groupnum", 0);
	            	bundle.putString("groupname", "");
	            	bundle.putInt("qqnum", 0);
		            bundle.putString("buddyname", "");
	            }
			}
	        
	        intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case QQCallBackMsg.UPDATE_RECENT_LIST:	// 更新最近联系人列表
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.UPDATE_BUDDY_NUMBER:	// 更新好友号码
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.UPDATE_GROUP_NUMBER:	// 更新群号码
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.UPDATE_GMEMBER_NUMBER:// 更新群成员号码
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.UPDATE_BUDDY_HEADPIC:// 更新好友头像
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.UPDATE_GROUP_HEADPIC:// 更新群头像
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.UPDATE_GMEMBER_HEADPIC:// 更新群成员头像
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.BUDDY_MSG:			// 好友消息
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.GROUP_MSG:			// 群消息
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.SESS_MSG:			// 群成员消息
			m_msgAdapter.notifyDataSetChanged();
			break;
		case QQCallBackMsg.SYS_GROUP_MSG:		// 系统群消息
			m_msgAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
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
			//m_lvMsg.onRefreshComplete();
            layout.setRefreshing(false);
			super.onPostExecute(result);
		}
	}
}
