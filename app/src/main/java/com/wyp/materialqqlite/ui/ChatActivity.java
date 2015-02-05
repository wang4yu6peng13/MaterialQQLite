package com.wyp.materialqqlite.ui;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.baoyz.widget.PullRefreshLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.BubbleInfo;
import com.wyp.materialqqlite.BubbleManager;
import com.wyp.materialqqlite.FaceInfo;
import com.wyp.materialqqlite.FaceList;
import com.wyp.materialqqlite.HomeWatcher;
import com.wyp.materialqqlite.HomeWatcher.OnHomePressedListener;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.SwipeBackActivity;
import com.wyp.materialqqlite.SwipeBackLayout;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.QQUtils;
import com.wyp.materialqqlite.qqclient.msglog.BuddyMsgLog;
import com.wyp.materialqqlite.qqclient.msglog.GroupMsgLog;
import com.wyp.materialqqlite.qqclient.msglog.MessageLogger;
import com.wyp.materialqqlite.qqclient.msglog.SessMsgLog;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.Content;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.GroupMessage;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.MessageList;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.SessMessage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ChatActivity extends SwipeBackActivity
        implements OnClickListener, OnItemClickListener,
        OnPageChangeListener, OnHomePressedListener {
    public static final int IS_BUDDY = 0;		// 好友聊天窗口
    public static final int IS_GROUP = 1;		// 群聊天窗口
    public static final int IS_SESS = 2;		// 临时会话聊天窗口

  //  private TextView m_txtName;					// 好友名称或群名称标签
    private String m_txtName;
    private ListView m_lvMsg;		// 聊天消息列表框
    private PullRefreshLayout swipeRefreshLayout_chat;

    private ImageButton m_btnFace;				// “表情”按钮
//    private ImageButton m_btnMore;				// “更多”按钮
    private EditText m_edtMsg;					// 消息编辑框
    private Button m_btnSend;					// “发送”按钮
    private View m_faceBar;						// 表情栏
    private ViewPager m_vpFace;					// 表情页容器
    private LinearLayout m_dotBar;				// 圆点栏

    private ChatMsgAdapter m_chatMsgAdapter;	// 聊天消息适配器

    private List<ChatMsg> m_lnkChatMsg;			// 聊天消息链表
    private List<View> m_arrFacePageView;		// 表情页数组
    private List<ImageView> m_arrDotView;		// 圆点图片数组

    private QQClient m_QQClient;
    private MessageList m_msgList;				// 消息列表
    private MessageLogger m_msgLogger;			// 消息记录
    private FaceList m_faceList;				// 表情列表
    private BubbleManager m_bubbleMgr;			// 气泡管理器
    private HomeWatcher mHomeWatcher;

    private int m_nMsgLogOffset = 0;	// 读取消息记录的位移(从哪条消息记录开始读取)
    private int m_nMsgLogRows = 15;	// 一次读取消息记录的条数
    private long m_lIntervalTime = 4*60;	// 两条消息间隔时间(大于此间隔时间则显示一次时间)
    private int m_nCurFacePage = 0;	// 当前表情页
    private int m_cxFace, m_cyFace;

    private int m_nType = 0;			// 聊天窗口类型
    private int m_nUserUin = 0;		// 用户Uin
    private String m_strUserName = "";	// 用户名称
    private int m_nGroupCode = 0;		// 群代码
    private int m_nGroupId = 0;		// 群ID
    private int m_nGroupNum = 0;		// 群号码
    private String m_strGroupName = "";// 群名称
    private int m_nQQUin = 0;			// 好友Uin
    private int m_nQQNum = 0;			// 好友号码
    private String m_strBuddyName = "";	// 好友名称
    private Toolbar toolbar;
    private LinearLayout chat_inputbar;
    private SharedPreferences sp;
    private int color_theme;


    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QQCallBackMsg.UPDATE_BUDDY_NUMBER:	// 更新好友号码
                    if (m_nType != IS_BUDDY || msg.arg1 != m_nQQUin)
                        return;
                    m_chatMsgAdapter.notifyDataSetChanged();
                    break;
                case QQCallBackMsg.UPDATE_GMEMBER_NUMBER:// 更新群成员号码
                    if (m_nType == IS_GROUP && msg.arg1 == m_nGroupCode) {
                        m_chatMsgAdapter.notifyDataSetChanged();
                    } else if (m_nType == IS_SESS &&
                            msg.arg1 == m_nGroupCode && msg.arg2 == m_nQQUin) {
                        m_chatMsgAdapter.notifyDataSetChanged();
                    }
                    break;
                case QQCallBackMsg.UPDATE_BUDDY_HEADPIC:// 更新好友头像
                    if (m_nType != IS_BUDDY || msg.arg1 != m_nQQUin)
                        return;
                    m_chatMsgAdapter.notifyDataSetChanged();
                    break;
                case QQCallBackMsg.UPDATE_GMEMBER_HEADPIC:// 更新群成员头像
                    if (m_nType == IS_GROUP && msg.arg1 == m_nGroupCode) {
                        m_chatMsgAdapter.notifyDataSetChanged();
                    } else if (m_nType == IS_SESS &&
                            msg.arg1 == m_nGroupCode && msg.arg2 == m_nQQUin) {
                        m_chatMsgAdapter.notifyDataSetChanged();
                    }
                    break;
                case QQCallBackMsg.BUDDY_MSG: {			// 好友消息
                    if (m_nType != IS_BUDDY || msg.arg1 != m_nQQUin || null == msg.obj)
                        return;

                    BuddyMessage buddyMsg = (BuddyMessage)msg.obj;

                    m_msgList.emptyBuddyUnreadMsgCount(m_nQQUin);

                    BuddyList buddyList = m_QQClient.getBuddyList();
                    BuddyInfo buddyInfo = buddyList.getBuddy(m_nQQUin);

                    // 与最后一条消息的发送时间间隔m_lIntervalTime秒则显示一条时间
                    long lLastTime = getLastMsgLogTime();
                    long lTime = buddyMsg.m_nTime;
                    if (lTime - lLastTime > m_lIntervalTime) {
                        ChatMsg time = new ChatMsg();
                        time.m_nType = ChatMsg.TIME;
                        time.m_nTime = (int)lTime;
                        m_lnkChatMsg.add(time);
                    }

                    ChatMsg chatMsg = new ChatMsg();
                    chatMsg.m_nType = ChatMsg.LEFT_B;
                    chatMsg.m_nQQUin = m_nQQUin;
                    if (buddyInfo != null)
                        chatMsg.m_nBubble = buddyInfo.m_nQQNum % BubbleManager.MAX_COUNT;
                    if (chatMsg.m_nBubble != 0) {
                        BubbleInfo bubbleInfo = m_bubbleMgr.getBubbleInfo(ChatActivity.this, chatMsg.m_nBubble);
                        if (bubbleInfo != null) {
                            chatMsg.m_nColor = bubbleInfo.m_nColor;
                            chatMsg.m_nLinkColor = bubbleInfo.m_nLinkColor;
                        }
                    }
                    chatMsg.m_nMsgLogId = 0;
                    chatMsg.m_strName = m_strBuddyName;
                    chatMsg.m_arrContent = buddyMsg.m_arrContent;
                    chatMsg.m_nTime = buddyMsg.m_nTime;

                    m_lnkChatMsg.add(chatMsg);
                    m_chatMsgAdapter.notifyDataSetChanged();
                    break;
                }
                case QQCallBackMsg.GROUP_MSG: {		// 群消息
                    if (m_nType != IS_GROUP || msg.arg1 != m_nGroupCode || null == msg.obj)
                        return;

                    GroupMessage groupMsg = (GroupMessage)msg.obj;

                    m_msgList.emptyGroupUnreadMsgCount(m_nGroupCode);

                    GroupList groupList = m_QQClient.getGroupList();
                    BuddyInfo buddyInfo = groupList.getGroupMemberByCode(m_nGroupCode, groupMsg.m_nSendUin);

                    // 与最后一条消息的发送时间间隔m_lIntervalTime秒则显示一条时间
                    long lLastTime = getLastMsgLogTime();
                    long lTime = groupMsg.m_nTime;
                    if (lTime - lLastTime > m_lIntervalTime) {
                        ChatMsg time = new ChatMsg();
                        time.m_nType = ChatMsg.TIME;
                        time.m_nTime = (int)lTime;
                        m_lnkChatMsg.add(time);
                    }

                    ChatMsg chatMsg = new ChatMsg();
                    chatMsg.m_nType = ChatMsg.LEFT_G;
                    chatMsg.m_nQQUin = groupMsg.m_nSendUin;
                    chatMsg.m_nMsgLogId = 0;
                    if (buddyInfo != null) {
                        chatMsg.m_nBubble = buddyInfo.m_nQQNum % BubbleManager.MAX_COUNT;
                        if (!Utils.isEmptyStr(buddyInfo.m_strGroupCard))
                            chatMsg.m_strName = buddyInfo.m_strGroupCard;
                        else
                            chatMsg.m_strName = buddyInfo.m_strNickName;
                    } else
                        chatMsg.m_strName = "";
                    if (chatMsg.m_nBubble != 0) {
                        BubbleInfo bubbleInfo = m_bubbleMgr.getBubbleInfo(ChatActivity.this, chatMsg.m_nBubble);
                        if (bubbleInfo != null) {
                            chatMsg.m_nColor = bubbleInfo.m_nColor;
                            chatMsg.m_nLinkColor = bubbleInfo.m_nLinkColor;
                        }
                    }
                    chatMsg.m_arrContent = groupMsg.m_arrContent;
                    chatMsg.m_nTime = groupMsg.m_nTime;
                    m_lnkChatMsg.add(chatMsg);

                    m_chatMsgAdapter.notifyDataSetChanged();
                    break;
                }
                case QQCallBackMsg.SESS_MSG: {			// 群成员消息
                    if (m_nType != IS_SESS || msg.arg1 != m_nGroupCode ||
                            msg.arg2 != m_nQQUin || null == msg.obj)
                        return;

                    SessMessage sessMsg = (SessMessage)msg.obj;

                    m_msgList.emptySessUnreadMsgCount(m_nGroupCode, m_nQQUin);

                    GroupList groupList = m_QQClient.getGroupList();
                    BuddyInfo buddyInfo = groupList.getGroupMemberByCode(m_nGroupCode, m_nQQUin);

                    // 与最后一条消息的发送时间间隔m_lIntervalTime秒则显示一条时间
                    long lLastTime = getLastMsgLogTime();
                    long lTime = sessMsg.m_nTime;
                    if (lTime - lLastTime > m_lIntervalTime) {
                        ChatMsg time = new ChatMsg();
                        time.m_nType = ChatMsg.TIME;
                        time.m_nTime = (int)lTime;
                        m_lnkChatMsg.add(time);
                    }

                    ChatMsg chatMsg = new ChatMsg();
                    chatMsg.m_nType = ChatMsg.LEFT_B;
                    chatMsg.m_nQQUin = m_nQQUin;
                    if (buddyInfo != null)
                        chatMsg.m_nBubble = buddyInfo.m_nQQNum % BubbleManager.MAX_COUNT;
                    if (chatMsg.m_nBubble != 0) {
                        BubbleInfo bubbleInfo = m_bubbleMgr.getBubbleInfo(ChatActivity.this, chatMsg.m_nBubble);
                        if (bubbleInfo != null) {
                            chatMsg.m_nColor = bubbleInfo.m_nColor;
                            chatMsg.m_nLinkColor = bubbleInfo.m_nLinkColor;
                        }
                    }
                    chatMsg.m_nMsgLogId = 0;
                    chatMsg.m_strName = "";
                    chatMsg.m_arrContent = sessMsg.m_arrContent;
                    chatMsg.m_nTime = sessMsg.m_nTime;
                    m_lnkChatMsg.add(chatMsg);

                    m_chatMsgAdapter.notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        sp=getSharedPreferences("theme",MODE_PRIVATE);
        color_theme=sp.getInt("color",-12627531);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(color_theme);
        initData();		// 初始化数据
        initView();		// 初始化视图
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_QQClient.setCallBackHandler(m_Handler);
        AppData.getAppData().cancelNotify(1);
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
    }

    @Override
    public void onStop() {
        super.onStop();
        m_QQClient.setNullCallBackHandler(m_Handler);
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 初始化数据
    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            m_nType = bundle.getInt("type");
            m_nUserUin = bundle.getInt("useruin");
            m_strUserName = bundle.getString("username");
            m_nGroupCode = bundle.getInt("groupcode");
            m_nGroupId = bundle.getInt("groupid");
            m_nGroupNum = bundle.getInt("groupnum");
            m_strGroupName = bundle.getString("groupname");
            m_nQQUin = bundle.getInt("qquin");
            m_nQQNum = bundle.getInt("qqnum");
            m_strBuddyName = bundle.getString("buddyname");
        }

        m_bubbleMgr = AppData.getAppData().getBubbleMgr();
        m_bubbleMgr.loadBubbleConfig(this);

        m_QQClient = AppData.getAppData().getQQClient();
        m_QQClient.setCallBackHandler(m_Handler);
        m_msgList = m_QQClient.getMessageList();
        m_msgLogger = m_QQClient.getMsgLogger();
        if (!m_msgLogger.isOpen()) {
            String strPath = m_QQClient.getMsgLogFullName(0);
            m_msgLogger.open(strPath);
        }

        m_cxFace = (int)getResources().getDimension(R.dimen.cxFace);
        m_cyFace = (int)getResources().getDimension(R.dimen.cyFace);

        m_faceList = AppData.getAppData().getFaceList();

        if (IS_BUDDY == m_nType) {
            m_nMsgLogOffset = m_msgLogger.getBuddyMsgLogCount(m_nQQNum);
            m_msgList.emptyBuddyUnreadMsgCount(m_nQQUin);
        } else if (IS_GROUP == m_nType) {
            m_nMsgLogOffset = m_msgLogger.getGroupMsgLogCount(m_nGroupNum);
            m_msgList.emptyGroupUnreadMsgCount(m_nGroupCode);
        } else if (IS_SESS == m_nType) {
            m_nMsgLogOffset = m_msgLogger.getSessMsgLogCount(m_nQQNum);
            m_msgList.emptySessUnreadMsgCount(m_nGroupCode, m_nQQUin);
        }
    }

    // 初始化视图
    @SuppressLint("NewApi")
    private void initView() {
    //    m_txtName = (TextView)findViewById(R.id.chat_txtName);
        m_lvMsg = (ListView)findViewById(R.id.chat_lvMsg);
        swipeRefreshLayout_chat= (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout_chat);
        m_btnFace = (ImageButton)findViewById(R.id.chat_btnFace);
    //    m_btnMore = (ImageButton)findViewById(R.id.chat_btnMore);
        m_edtMsg = (EditText) findViewById(R.id.chat_edtMsg);
        m_btnSend = (Button) findViewById(R.id.chat_btnSend);
        m_faceBar = findViewById(R.id.chat_facebar);
        m_vpFace = (ViewPager)findViewById(R.id.chat_vpFace);
        m_dotBar = (LinearLayout) findViewById(R.id.chat_dotbar);



        m_btnFace.setOnClickListener(this);
        m_edtMsg.setOnClickListener(this);
        m_btnSend.setOnClickListener(this);

        if (m_nType != IS_GROUP)
          //  m_txtName.setText(m_strBuddyName);
            m_txtName=m_strBuddyName;
        else
         //   m_txtName.setText(m_strGroupName);
        m_txtName=m_strGroupName;

        initChatMsgListView();	// 初始化聊天消息列表框
        initFaceBar();			// 初始化表情栏

        m_nCurFacePage = 0;
        m_vpFace.setCurrentItem(1);

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        toolbar.setNavigationIcon(R.drawable.icon);
        toolbar.setTitle(m_txtName);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chat_inputbar= (LinearLayout) findViewById(R.id.chat_inputbar);

        toolbar.setBackgroundColor(color_theme);
        chat_inputbar.setBackgroundColor(color_theme);

        // 去除 ListView 上下边界蓝色或黄色阴影
//		ListView actualListView = m_lvMsg.getRefreshableView();
//		if (Integer.parseInt(Build.VERSION.SDK) >= 9) {
//			actualListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        }
    }

    // 初始化聊天消息列表框
    private void initChatMsgListView() {
        m_lnkChatMsg = new LinkedList<ChatMsg>();
        m_chatMsgAdapter = new ChatMsgAdapter(this, m_nGroupCode, m_lnkChatMsg);
        m_lvMsg.setAdapter(m_chatMsgAdapter);

   //     m_lvMsg.getLoadingLayoutProxy().setPullLabel("");
   //     m_lvMsg.getLoadingLayoutProxy().setReleaseLabel("");
   //     m_lvMsg.getLoadingLayoutProxy().setRefreshingLabel("");
   //     m_lvMsg.getLoadingLayoutProxy().setLastUpdatedLabel("");
   //     m_lvMsg.getLoadingLayoutProxy().setQQMode(1);

       // m_lvMsg.setOnRefreshListener(this);
        swipeRefreshLayout_chat.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ReadMsgLogTask().execute();
                swipeRefreshLayout_chat.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout_chat.setRefreshing(false);
                        //refresh();
                        m_chatMsgAdapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });

        List<Object> arrMsgLog = readMsgLog();
        updateChatMsgListView(arrMsgLog);
    }

    // 初始化表情选择栏
    private void initFaceBar() {
        m_arrFacePageView = new ArrayList<View>();

        // 左侧添加空页
        View nullView1 = new View(this);
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        m_arrFacePageView.add(nullView1);

        // 中间添加表情页
        for (int i = 0; i < m_faceList.getPageCount(); i++) {
            GridView view = new GridView(this);
            List<FaceInfo> facePage = m_faceList.getFacePage(i);
            FaceAdapter adapter = new FaceAdapter(this, facePage);
            view.setAdapter(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            m_arrFacePageView.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(this);
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        m_arrFacePageView.add(nullView2);

        ViewPagerAdapter adapter = new ViewPagerAdapter(m_arrFacePageView);
        m_vpFace.setAdapter(adapter);
        
        m_vpFace.setOnPageChangeListener(this);

        initDotBar();	// 初始化圆点栏
    }




    // 初始化圆点栏
    private void initDotBar() {
        m_arrDotView = new ArrayList<ImageView>();

        if (m_arrFacePageView.size() <= 3)
            return;

        for (int i = 0; i < m_arrFacePageView.size()-2; i++) {
            ImageView imgView = new ImageView(this);
            imgView.setBackgroundResource(R.drawable.common_indicator_nor);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            params.leftMargin = (int)getResources().getDimension(R.dimen.dotLeftMargin);
            params.rightMargin = (int)getResources().getDimension(R.dimen.dotRightMargin);
//			params.width = 7;
//			params.height = 7;
            m_dotBar.addView(imgView, params);
            m_arrDotView.add(imgView);
        }
    }

    // 发送消息
    private void send() {
        if (m_edtMsg.getText().length() <= 0)
            return;

        String contString = m_edtMsg.getText().toString();
        String strMsg = "";
        int nStart = 0;

        ImageSpan[] imgSpans = m_edtMsg.getText().getSpans(0, m_edtMsg.getText().length(), ImageSpan.class);
        if (imgSpans.length > 0) {
            for (ImageSpan span : imgSpans) {
                int start = m_edtMsg.getText().getSpanStart(span);
                int end = m_edtMsg.getText().getSpanEnd(span);
                String str = contString.substring(nStart, start);
                str = str.replace("/", "//");
                strMsg += str;
                str = contString.substring(start, end);
                strMsg += str;
                nStart = end;
            }

            if (nStart < contString.length()) {
                String str = contString.substring(nStart, contString.length());
                str = str.replace("/", "//");
                strMsg += str;
            }
        } else {
            strMsg = contString;
            strMsg = strMsg.replace("/", "//");
        }

        strMsg += "/o[\"宋体,10,000000,0,0,0\"]";

        if (strMsg.length() > 0) {
            // 与最后一条消息的发送时间间隔m_lIntervalTime秒则显示一条时间
            long lLastTime = getLastMsgLogTime();
            long lTime = System.currentTimeMillis() / 1000;
            if (lTime - lLastTime > m_lIntervalTime) {
                ChatMsg time = new ChatMsg();
                time.m_nType = ChatMsg.TIME;
                time.m_nTime = (int)lTime;
                m_lnkChatMsg.add(time);
            }

            List<Content> arrContent = new ArrayList<Content>();
            QQUtils.createMsgContent(strMsg, arrContent);

            ChatMsg chatMsg = new ChatMsg();
            chatMsg.m_nType = ChatMsg.RIGHT;
            chatMsg.m_nQQUin = m_nUserUin;
            chatMsg.m_nBubble = AppData.getAppData().getBubbleMgr().getUserBubble();
            if (chatMsg.m_nBubble != 0) {
                BubbleInfo bubbleInfo = m_bubbleMgr.getBubbleInfo(ChatActivity.this, chatMsg.m_nBubble);
                if (bubbleInfo != null) {
                    chatMsg.m_nColor = bubbleInfo.m_nColor;
                    chatMsg.m_nLinkColor = bubbleInfo.m_nLinkColor;
                }
            }
            chatMsg.m_nMsgLogId = 0;
            chatMsg.m_strName = m_strUserName;
            chatMsg.m_arrContent = arrContent;
            chatMsg.m_nTime = (int)lTime;
            m_lnkChatMsg.add(chatMsg);

            m_chatMsgAdapter.notifyDataSetChanged();
        //    ListView actualListView = m_lvMsg.getRefreshableView();
            m_lvMsg.setSelection(m_lvMsg.getCount() - 1);
            m_edtMsg.setText("");

            if (IS_BUDDY == m_nType) {
                m_QQClient.sendBuddyMsg(m_nQQUin, (int)lTime, strMsg);
            } else if (IS_GROUP == m_nType) {
                m_QQClient.sendGroupMsg(m_nGroupId, (int)lTime, strMsg);
            } else {
                m_QQClient.sendSessMsg(m_nGroupId, m_nQQUin, (int)lTime, strMsg);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            if (Utils.ptInView(m_lvMsg, x, y)) {
                if (m_faceBar.getVisibility() == View.VISIBLE) {
                    m_faceBar.setVisibility(View.GONE);
                }
                Utils.hideInputMethod(this, m_edtMsg);
            } else if (Utils.ptInView(m_edtMsg, x, y)) {
                if (m_faceBar.getVisibility() == View.VISIBLE) {
                    m_faceBar.setVisibility(View.GONE);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && m_faceBar.getVisibility() == View.VISIBLE) {
            m_faceBar.setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_btnSend:		// “发送”按钮
                send();
                break;
            case R.id.chat_btnFace:		// “表情”按钮
                Utils.hideInputMethod(this, m_edtMsg);

                if (m_faceBar.getVisibility() == View.VISIBLE) {
                    m_faceBar.setVisibility(View.GONE);
                } else {
                    m_faceBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {
        GridView gridView = (GridView)parent;
        FaceInfo faceInfo = (FaceInfo)gridView.getItemAtPosition(position);
        if (faceInfo.m_nResId == R.drawable.delete_button) {	// “删除”按钮
            final KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
            m_edtMsg.onKeyDown(KeyEvent.KEYCODE_DEL, keyEventDown);
        } else {
            m_edtMsg.requestFocus();
            int nIndex = m_edtMsg.getSelectionStart();
            SpannableString spanStr = Utils.getSysFace(
                    this, faceInfo, m_cxFace, m_cyFace);
            if (spanStr != null)
                m_edtMsg.getText().insert(nIndex, spanStr);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        m_nCurFacePage = arg0;

        if (0 == arg0) {
            m_vpFace.setCurrentItem(arg0 + 1);
        } else if (arg0 == m_arrFacePageView.size() - 1){
            m_vpFace.setCurrentItem(arg0 - 1);
        } else {
            setSelDot(m_nCurFacePage - 1);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    private void setSelDot(int nSelIndex) {
        for (int i = 0; i < m_arrDotView.size(); i++) {
            if (nSelIndex == i) {
                m_arrDotView.get(i).setBackgroundResource(R.drawable.common_indicator_checked);
            } else {
                m_arrDotView.get(i).setBackgroundResource(R.drawable.common_indicator_nor);
            }
        }
    }

//    @Override
//    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        // Do work to refresh the list here.
//        new ReadMsgLogTask().execute();
//    }

    private long getFirstMsgLogTime() {
        if (null == m_lnkChatMsg || m_lnkChatMsg.size() <= 0)
            return 0;

        ChatMsg chatMsg = m_lnkChatMsg.get(0);
        if (null == chatMsg)
            return 0;

        return Utils.getUInt(chatMsg.m_nTime);
    }

    private long getLastMsgLogTime() {
        if (null == m_lnkChatMsg || m_lnkChatMsg.size() <= 0)
            return 0;

        ChatMsg chatMsg = m_lnkChatMsg.get(m_lnkChatMsg.size()-1);
        if (null == chatMsg)
            return 0;

        return Utils.getUInt(chatMsg.m_nTime);
    }

    private List<Object> readMsgLog() {
        if (null == m_msgLogger || m_nMsgLogOffset <= 0)
            return null;

        if (m_nMsgLogOffset - m_nMsgLogRows > 0)
            m_nMsgLogOffset -= m_nMsgLogRows;
        else
            m_nMsgLogOffset = 0;

        List<Object> arrObj = new ArrayList<Object>();

        if (IS_BUDDY == m_nType) {
            List<BuddyMsgLog> arrMsgLog = new ArrayList<BuddyMsgLog>();
            m_msgLogger.readBuddyMsgLog(m_nQQNum,
                    m_nMsgLogOffset, m_nMsgLogRows, arrMsgLog);

            for (int i = 0; i < arrMsgLog.size(); i++) {
                arrObj.add(arrMsgLog.get(i));
            }
            arrMsgLog.clear();
        } else if (IS_GROUP == m_nType) {
            ArrayList<GroupMsgLog> arrMsgLog = new ArrayList<GroupMsgLog>();
            m_msgLogger.readGroupMsgLog(m_nGroupNum,
                    m_nMsgLogOffset, m_nMsgLogRows, arrMsgLog);

            for (int i = 0; i < arrMsgLog.size(); i++) {
                arrObj.add(arrMsgLog.get(i));
            }
            arrMsgLog.clear();
        } else if (IS_SESS == m_nType) {
            List<SessMsgLog> arrMsgLog = new ArrayList<SessMsgLog>();
            m_msgLogger.readSessMsgLog(m_nQQNum,
                    m_nMsgLogOffset, m_nMsgLogRows, arrMsgLog);

            for (int i = 0; i < arrMsgLog.size(); i++) {
                arrObj.add(arrMsgLog.get(i));
            }
            arrMsgLog.clear();
        }

        return arrObj;
    }

    private void updateChatMsgListView(List<Object> arrMsgLog) {
        if (null == arrMsgLog
                || arrMsgLog.size() <= 0
                || null == m_lnkChatMsg
                || null == m_chatMsgAdapter)
            return;

        int nSize = m_lnkChatMsg.size();

        long lFirstTime = getFirstMsgLogTime();

        // 先删除第一条消息上面的时间
        if (m_lnkChatMsg.size() > 0)
            m_lnkChatMsg.remove(0);

        if (IS_BUDDY == m_nType) {
            for (int i = arrMsgLog.size()-1; i >= 0; i--) {
                BuddyMsgLog msgLog = (BuddyMsgLog)arrMsgLog.get(i);

                // 两条消息相隔4分钟则加入一条时间显示
                if (lFirstTime != 0 &&
                        lFirstTime - msgLog.m_nTime > m_lIntervalTime) {
                    ChatMsg time = new ChatMsg();
                    time.m_nType = ChatMsg.TIME;
                    time.m_nTime = (int)lFirstTime;
                    m_lnkChatMsg.add(0, time);
                }
                lFirstTime = msgLog.m_nTime;

                List<Content> arrContent = new ArrayList<Content>();
                QQUtils.createMsgContent(msgLog.m_strContent, arrContent);

                ChatMsg chatMsg = new ChatMsg();
                if (!msgLog.m_bSendFlag) {	// 好友发的消息
                    chatMsg.m_nType = ChatMsg.LEFT_B;
                    chatMsg.m_nQQUin = m_nQQUin;
                    chatMsg.m_nBubble = msgLog.m_nQQNum % BubbleManager.MAX_COUNT;
                }
                else {	// 用户自己发的消息
                    chatMsg.m_nType = ChatMsg.RIGHT;
                    chatMsg.m_nQQUin = m_nUserUin;
                    chatMsg.m_nBubble = AppData.getAppData().getBubbleMgr().getUserBubble();
                }
                if (chatMsg.m_nBubble != 0) {
                    BubbleInfo bubbleInfo = m_bubbleMgr.getBubbleInfo(ChatActivity.this, chatMsg.m_nBubble);
                    if (bubbleInfo != null) {
                        chatMsg.m_nColor = bubbleInfo.m_nColor;
                        chatMsg.m_nLinkColor = bubbleInfo.m_nLinkColor;
                    }
                }
                chatMsg.m_nMsgLogId = msgLog.m_nID;
                chatMsg.m_strName = msgLog.m_strNickName;
                chatMsg.m_arrContent = arrContent;
                chatMsg.m_nTime = msgLog.m_nTime;
                m_lnkChatMsg.add(0, chatMsg);
            }
            arrMsgLog.clear();
        } else if (IS_GROUP == m_nType) {
            for (int i = arrMsgLog.size()-1; i >= 0; i--) {
                GroupMsgLog msgLog = (GroupMsgLog)arrMsgLog.get(i);

                // 两条消息相隔4分钟则加入一条时间显示
                if (lFirstTime != 0 &&
                        lFirstTime - msgLog.m_nTime > m_lIntervalTime) {
                    ChatMsg time = new ChatMsg();
                    time.m_nType = ChatMsg.TIME;
                    time.m_nTime = (int)lFirstTime;
                    m_lnkChatMsg.add(0, time);
                }
                lFirstTime = msgLog.m_nTime;

                List<Content> arrContent = new ArrayList<Content>();
                QQUtils.createMsgContent(msgLog.m_strContent, arrContent);

                ChatMsg chatMsg = new ChatMsg();
                if (m_nUserUin != msgLog.m_nQQNum) {	// 群成员发的消息
                    chatMsg.m_nType = ChatMsg.LEFT_G;
                    GroupList groupList = m_QQClient.getGroupList();
                    BuddyInfo buddyInfo = groupList.getGroupMemberByNum(m_nGroupCode, msgLog.m_nQQNum);
                    if (buddyInfo != null)
                        chatMsg.m_nQQUin = buddyInfo.m_nQQUin;
                    chatMsg.m_nBubble = msgLog.m_nQQNum % BubbleManager.MAX_COUNT;
                }
                else {	// 用户自己发的消息
                    chatMsg.m_nType = ChatMsg.RIGHT;
                    chatMsg.m_nQQUin = m_nUserUin;
                    chatMsg.m_nBubble = AppData.getAppData().getBubbleMgr().getUserBubble();
                }
                if (chatMsg.m_nBubble != 0) {
                    BubbleInfo bubbleInfo = m_bubbleMgr.getBubbleInfo(ChatActivity.this, chatMsg.m_nBubble);
                    if (bubbleInfo != null) {
                        chatMsg.m_nColor = bubbleInfo.m_nColor;
                        chatMsg.m_nLinkColor = bubbleInfo.m_nLinkColor;
                    }
                }
                chatMsg.m_nMsgLogId = msgLog.m_nID;
                chatMsg.m_strName = msgLog.m_strNickName;
                chatMsg.m_arrContent = arrContent;
                chatMsg.m_nTime = msgLog.m_nTime;
                m_lnkChatMsg.add(0, chatMsg);
            }
            arrMsgLog.clear();
        } else if (IS_SESS == m_nType) {
            for (int i = arrMsgLog.size()-1; i >= 0; i--) {
                SessMsgLog msgLog = (SessMsgLog)arrMsgLog.get(i);

                // 两条消息相隔4分钟则加入一条时间显示
                if (lFirstTime != 0 &&
                        lFirstTime - msgLog.m_nTime > m_lIntervalTime) {
                    ChatMsg time = new ChatMsg();
                    time.m_nType = ChatMsg.TIME;
                    time.m_nTime = (int)lFirstTime;
                    m_lnkChatMsg.add(0, time);
                }
                lFirstTime = msgLog.m_nTime;

                List<Content> arrContent = new ArrayList<Content>();
                QQUtils.createMsgContent(msgLog.m_strContent, arrContent);

                ChatMsg chatMsg = new ChatMsg();
                if (!msgLog.m_bSendFlag) {	// 好友发的消息
                    chatMsg.m_nType = ChatMsg.LEFT_B;
                    chatMsg.m_nQQUin = m_nQQUin;
                    chatMsg.m_nBubble = msgLog.m_nQQNum % BubbleManager.MAX_COUNT;
                }
                else {	// 用户自己发的消息
                    chatMsg.m_nType = ChatMsg.RIGHT;
                    chatMsg.m_nQQUin = m_nUserUin;
                    chatMsg.m_nBubble = AppData.getAppData().getBubbleMgr().getUserBubble();
                }
                if (chatMsg.m_nBubble != 0) {
                    BubbleInfo bubbleInfo = m_bubbleMgr.getBubbleInfo(ChatActivity.this, chatMsg.m_nBubble);
                    if (bubbleInfo != null) {
                        chatMsg.m_nColor = bubbleInfo.m_nColor;
                        chatMsg.m_nLinkColor = bubbleInfo.m_nLinkColor;
                    }
                }
                chatMsg.m_nMsgLogId = msgLog.m_nID;
                chatMsg.m_strName = msgLog.m_strNickName;
                chatMsg.m_arrContent = arrContent;
                chatMsg.m_nTime = msgLog.m_nTime;
                m_lnkChatMsg.add(0, chatMsg);
            }
            arrMsgLog.clear();
        } else {
            return;
        }

        // 第一条消息的上面总是显示时间
        ChatMsg time = new ChatMsg();
        time.m_nType = ChatMsg.TIME;
        time.m_nTime = (int)lFirstTime;
        m_lnkChatMsg.add(0, time);

        if (nSize <= 0) {
            m_chatMsgAdapter.notifyDataSetChanged();
        //    ListView actualListView = m_lvMsg.getRefreshableView();
            m_lvMsg.setSelection(m_lvMsg.getCount() - 1);
        } else {
        //    ListView actualListView = m_lvMsg.getRefreshableView();
            int nHeaderCnt = m_lvMsg.getHeaderViewsCount();
            int nPos =m_lvMsg.getFirstVisiblePosition()+nHeaderCnt+1;
            View view =m_lvMsg.getChildAt(nPos);
            int nTop = (null == view) ? 0 : view.getTop();
            nPos += (m_lnkChatMsg.size() - nSize);

            m_chatMsgAdapter.notifyDataSetChanged();

            m_lvMsg.setSelectionFromTop(nPos, nTop);
        }
    }

    private class ReadMsgLogTask extends AsyncTask<Void, Void, Integer> {
        private List<Object> m_arrMsgLog;

        @Override
        protected Integer doInBackground(Void... params) {
            long nStartTime = System.currentTimeMillis();

            m_arrMsgLog = readMsgLog();

            long nEndTime = System.currentTimeMillis();
            if (nEndTime - nStartTime < 1000*1) {	// 读取时间少于1秒则再延时1秒
                try {
                    Thread.sleep(1000*1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
           // m_lvMsg.onRefreshComplete();
            swipeRefreshLayout_chat.setRefreshing(false);
            updateChatMsgListView(m_arrMsgLog);
        }
    }

    @Override
    public void onHomePressed() {
        String strTicker = getString(R.string.bgrun);
        String strTitle = getString(R.string.app_name);
        String strText = getString(R.string.nonewmsg);
        AppData.getAppData().showNotify(1, this,
                strTicker, strTitle, strText);
    }

    @Override
    public void onHomeLongPressed() {
        // do nothing
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}