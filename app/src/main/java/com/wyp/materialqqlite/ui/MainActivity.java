package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.afollestad.materialdialogs.ThemeSingleton;
import com.kyleduo.switchbutton.SwitchButton;

import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.ExitApplication;
import com.wyp.materialqqlite.HomeWatcher;
import com.wyp.materialqqlite.HomeWatcher.OnHomePressedListener;
import com.wyp.materialqqlite.LoginAccountInfo;
import com.wyp.materialqqlite.LoginAccountList;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.Utility;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.BuddyInfo;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQStatus;


import java.io.File;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AbsActivity implements MaterialTabListener, OnHomePressedListener {
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    MaterialTabHost tabHost;
    private Resources res;

    private QQClient m_QQClient;
    private TextView m_txtUnreadMsgCnt;

    private MsgFragment m_fragmentMsg;
    private ContactsFragment m_fragmentContacts;

    //private SettingFragment m_fragmentSetting;
    //    private int m_cxAvatar, m_cyAvatar= (int)getResources().getDimension(R.dimen.msgList_cyAvatar);
//    private int m_pxAvatarRound=(int)getResources().getDimension(R.dimen.pxAvatarRound);
    boolean FlagSelect = false;
    boolean QQOnline = true;

    private int m_nCurSelTab = 0;
    private HomeWatcher mHomeWatcher;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawerLayout;
    private SwitchButton mToggleSb;
    private ImageView m_imgAvatar;
    private ImageView m_imgState;
    private TextView m_txtName;
    private TextView m_txtSign;
    private int m_cxAvatar, m_cyAvatar;
    private int m_pxAvatarRound;

    private TextView selectcolor,aboutfeed;

    private SharedPreferences sp;
    private int color_theme;
    public int statusBarHeight = 0;
//    private SystemBarTintManager tintManager;
    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QQCallBackMsg.BUDDY_MSG:
                case QQCallBackMsg.GROUP_MSG:
                case QQCallBackMsg.SESS_MSG:
                case QQCallBackMsg.SYS_GROUP_MSG:
                    updateUnreadMsgCount();
                    updateMydata();
                    break;
                case QQCallBackMsg.LOGOUT_RESULT:		// 注销返回
                    showLoginActivity();
                    break;
            }

            switch (m_nCurSelTab) {
                case 0:
                    if (m_fragmentMsg != null)
                        m_fragmentMsg.handleMessage(msg);
                    break;

                case 1:
                    if (m_fragmentContacts != null)
                        m_fragmentContacts.handleMessage(msg);
                    break;

    //            case 3:
    //                if (m_fragmentSetting != null)
    //                    m_fragmentSetting.handleMessage(msg);
    //                break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** 当SDK >= 19 且 不是Chrome浏览器 时启用透明状态栏 */
        if (Build.VERSION.SDK_INT >= 19 && !Utility.isChrome()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            statusBarHeight = Utility.getStatusBarHeight(getApplicationContext());
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }
    @Override
    public void setUpViews() {
        View statusHeaderView = findViewById(R.id.statusHeaderView);
        statusHeaderView.getLayoutParams().height = statusBarHeight;
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));

        sp=getSharedPreferences("theme",MODE_PRIVATE);
        color_theme=sp.getInt("color", -12627531);
        ThemeSingleton.get().positiveColor = color_theme;
        ThemeSingleton.get().neutralColor = color_theme;
        ThemeSingleton.get().negativeColor = color_theme;

        ExitApplication.getInstance().addActivity(this);
        initView();

        res = this.getResources();


        mToolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        // 實作 drawer toggle 並放入 toolbar
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();

        mDrawerLayout.setDrawerListener(drawerToggle);
        mToolbar.setOnMenuItemClickListener(onMenuItemClick);

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);
        // init view pager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });
        // insert all tabs from pagerAdapter data
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setIcon(getIcon(i))
                            .setTabListener(this)
            );
        }


        tabHost.setPrimaryColor(color_theme);
        //  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color_theme));



    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.exit_current:
                    showexitcurrentdialog();
                    break;
                case R.id.exit_app:
                    showexitappdialog();
                    break;
            }
            return true;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
// when the tab is clicked the pager swipe content to the tab position
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_QQClient.setCallBackHandler(m_Handler);
        //    setCurSelTab(m_nCurSelTab);
        pager.setCurrentItem(m_nCurSelTab);
        updateUnreadMsgCount();
        AppData.getAppData().cancelNotify(1);
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_QQClient.setNullCallBackHandler(m_Handler);
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //    closeExitDlg();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String strTicker = getString(R.string.bgrun);
            String strTitle = getString(R.string.app_name);
            String strText = getString(R.string.nonewmsg);
            AppData.getAppData().showNotify(1, this,
                    strTicker, strTitle, strText);
            moveTaskToBack(true);    // true对任何Activity都适用
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        m_QQClient = AppData.getAppData().getQQClient();
        m_QQClient.setCallBackHandler(m_Handler);


        m_txtUnreadMsgCnt = (TextView) findViewById(R.id.main_txtUnreadMsgCnt);

        m_imgAvatar = (ImageView)
                findViewById(R.id.setting_item_imgAvatar);
        m_imgState = (ImageView)
                findViewById(R.id.setting_item_imgState);
        m_txtName = (TextView)
                findViewById(R.id.setting_item_txtName);
        m_txtSign = (TextView)
                findViewById(R.id.setting_item_txtSign);
        mToggleSb = (SwitchButton) findViewById(R.id.switch_qqstatus);

    //    switchnotify= (SwitchButton) findViewById(R.id.switch_notify);

        m_cxAvatar = (int)getResources().getDimension(R.dimen.msgList_cxAvatar);
        m_cyAvatar = (int)getResources().getDimension(R.dimen.msgList_cyAvatar);
        m_pxAvatarRound = (int)getResources().getDimension(R.dimen.pxAvatarRound);

        selectcolor= (TextView) findViewById(R.id.select_color);
        selectcolor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomColorChooser();
            }
        });
        aboutfeed= (TextView) findViewById(R.id.aboutandfeedback_drawer);
        aboutfeed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_about = new Intent();
                intent_about.setClass(MainActivity.this, AboutActivity.class);
                startActivity(intent_about);
            }
        });


        updateMydata();
       


        //      m_fragmentMgr = getSupportFragmentManager();
        //      setCurSelTab(0);
    }

    private void updateMydata() {
        Bitmap bmp = null;

        BuddyInfo buddyInfo = m_QQClient.getUserInfo();
        String strFileName = m_QQClient.
                getBuddyHeadPicFullName(m_QQClient.getUserInfo().m_nQQNum);
        File file = new File(strFileName);
        if (!file.exists()) {
            m_QQClient.updateBuddyHeadPic(buddyInfo.m_nQQUin, buddyInfo.m_nQQNum);
        } else {
            bmp = BitmapFactory.decodeFile(strFileName);
            if (bmp != null) {
                bmp = Utils.zoomImg(bmp, m_cxAvatar, m_cyAvatar);
                bmp = Utils.getRoundedCornerBitmap(bmp, m_pxAvatarRound);
            }
        }

        if (bmp != null)
            m_imgAvatar.setImageBitmap(bmp);
        else
            m_imgAvatar.setImageResource(R.drawable.h001);

        int nStatus = m_QQClient.getStatus();
        if (nStatus != QQStatus.HIDDEN) {
            m_imgState.setImageResource(R.drawable.status_online_btn_2);
            mToggleSb.setChecked(mToggleSb.isChecked());
        } else {
            m_imgState.setImageResource(R.drawable.status_invisible_btn_2);
            mToggleSb.setChecked(!mToggleSb.isChecked());
        }
        if (!Utils.isEmptyStr(buddyInfo.m_strNickName))
            m_txtName.setText(buddyInfo.m_strNickName);
        else
            m_txtName.setText("");
        if (!buddyInfo.isHasQQSign()) {
            m_QQClient.updateBuddySign(buddyInfo.m_nQQUin);
        } else {
            m_txtSign.setText(buddyInfo.m_strSign);
        }

        mToggleSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // mListenerFinish.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                if (isChecked) {
                    m_QQClient.changeStatus(QQStatus.ONLINE);
                    m_imgState.setImageResource(R.drawable.status_online_btn_2);
                } else {
                    m_QQClient.changeStatus(QQStatus.HIDDEN);
                    m_imgState.setImageResource(R.drawable.status_invisible_btn_2);
                }
            }
        });

    }

    static int selectedColorIndex =-1;//sp.getInt("index",0);//    -1;
    private void showCustomColorChooser() {
        new ColorChooserDialog().show(this, selectedColorIndex, new ColorChooserDialog.Callback() {
            @Override
            public void onColorSelection(int index, int color, int darker) {
                selectedColorIndex = index;

                 SharedPreferences.Editor editor = sp.edit();
                 editor.putInt("index", index);
                 editor.putInt("color",color);
                 editor.commit();

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                tabHost.setPrimaryColor(color);
               // tabHost.setAccentColor(color);
  //              tintManager.setStatusBarTintColor(color);
                View statusHeaderView = findViewById(R.id.statusHeaderView);
                statusHeaderView.setBackgroundColor(color);
                ThemeSingleton.get().positiveColor = color;
                ThemeSingleton.get().neutralColor = color;
                ThemeSingleton.get().negativeColor = color;
          //      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
          //          getWindow().setStatusBarColor(darker);
            }
        });
    }

    View positiveAction;

    private void showexitappdialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.exitqqapp)
                .customView(R.layout.dialog_customview)
                .positiveText(R.string.exitdlg2_exit)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //    Toast.makeText(getApplicationContext(), "Password: " + passwordInput.getText().toString(), Toast.LENGTH_SHORT).show();
                        if (!FlagSelect) {
                            AppData.getAppData().cancelNotify(1);
                            m_QQClient.logout();
                            ExitApplication.getInstance().exit();
                        } else {
                            String strTicker = getString(R.string.bgrun);
                            String strTitle = getString(R.string.app_name);
                            String strText = getString(R.string.nonewmsg);
                            AppData.getAppData().showNotify(1, MainActivity.this,
                                    strTicker, strTitle, strText);
                        }
                        MainActivity.this.finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);


        // Toggling the show password CheckBox will mask or unmask the password input EditText
        ((CheckBox) dialog.getCustomView().findViewById(R.id.checkbox_exit)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                // passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
                if (!isChecked) FlagSelect = false;
                else FlagSelect = true;
            }
        });

        dialog.show();
        //      positiveAction.setEnabled(false); // disabled by default
    }

    private void showexitcurrentdialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.exit_cur_account)
                .positiveText(R.string.exitdlg2_exit)  // the default is 'Accept', this line could be left out
                .negativeText(R.string.cancel)  // leaving this line out will remove the negative button
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //Toast.makeText(getApplicationContext(), "Positive!", Toast.LENGTH_SHORT).show();
                        LoginAccountList accountList = AppData.getAppData().getLoginAccountList();
                        LoginAccountInfo accountInfo = accountList.getLastLoginAccountInfo();
                        accountInfo.m_bAutoLogin = false;

                        String strAppPath = AppData.getAppData().getAppPath();
                        String strFileName = strAppPath + "LoginAccountList.dat";
                        accountList.saveFile(strFileName);

                        AppData.getAppData().cancelNotify(1);
                        m_QQClient.logout();

                        if (!m_QQClient.logout()) {
                            showLoginActivity();
                        }

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        //Toast.makeText(getApplicationContext(), "Negative…", Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    private void showLoginActivity() {
        MainActivity.this.finish();

        Intent intent = new Intent(this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("qq_num", m_QQClient.getQQNum());
        bundle.putString("qq_pwd", m_QQClient.getQQPwd());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            Fragment mFragemnt = null;
            switch (num) {
                case 0:
                    mFragemnt = new MsgFragment();
                    break;
                case 1:
                    mFragemnt = new ContactsFragment();
                    break;
                case 2:
                    mFragemnt = new GroupFragment();
                    break;
   //             case 3:
   //                 mFragemnt = new SettingFragment();
   //                 break;
            }
            //m_nCurSelTab=num;
            return mFragemnt;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "tab 1";
                case 1:
                    return "tab 2";
                case 2:
                    return "tab 3";
    //            case 3:
    //                return "tab 4";
                default:
                    return null;
            }
        }
    }

    /*
    * It doesn't matter the color of the icons, but they must have solid colors
    */
    private Drawable getIcon(int position) {
        switch (position) {
            case 0:
                return res.getDrawable(R.drawable.ic_messenger_black_48dp);
            case 1:
                return res.getDrawable(R.drawable.ic_person_black_48dp);
            case 2:
                return res.getDrawable(R.drawable.ic_people_black_48dp);
   //         case 3:
   //             return res.getDrawable(R.drawable.ic_launcher);
        }
        return null;
    }

    // 更新未读消息总数标签
    private void updateUnreadMsgCount() {
        int nCount = m_QQClient.getMessageList().getUnreadMsgCount();
        if (nCount > 0) {
            String strText;
            if (nCount > 99)
                strText = "99+";
            else
                strText = String.valueOf(nCount);
            m_txtUnreadMsgCnt.setText(strText);
            m_txtUnreadMsgCnt.setVisibility(View.VISIBLE);
        } else {
            m_txtUnreadMsgCnt.setText("");
            m_txtUnreadMsgCnt.setVisibility(View.GONE);
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

}


