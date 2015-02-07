package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.material.widget.CircularProgress;
import com.material.widget.FloatingEditText;
import com.material.widget.PaperButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.ExitApplication;
import com.wyp.materialqqlite.LoginAccountList;
import com.wyp.materialqqlite.QQService;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.Utils;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQLoginResultCode;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQStatus;

public class LoginActivity extends Activity implements OnClickListener {
    private Animation my_Translate;        // 位移动画
    private Animation my_Rotate;        // 旋转动画
    private LinearLayout rl, llbackground;
    //	private ImageView m_imgArrow;
    private ImageView m_imgAvatar;
    private FloatingEditText m_edtNum;
    private FloatingEditText m_edtPwd;
    private PaperButton m_btnLogin, aboutbtn;
    private Dialog m_dlgLogining;
    private QQClient m_QQClient;
    private String m_strQQNum, m_strQQPwd;

    private SharedPreferences sp;
    private int color_theme;

    private Handler m_hService = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (2 == msg.what) {        // 已经登录则直接进主窗口

            } else if (1 == msg.what) {    // 初始化成功
                m_QQClient.setUser(m_strQQNum, m_strQQPwd);
                m_QQClient.setLoginStatus(QQStatus.ONLINE);
                m_QQClient.login();
            } else {                    // 初始化失败
                Toast.makeText(getBaseContext(),
                        R.string.qqservice_init_err, Toast.LENGTH_LONG).show();
                m_QQClient.setNullCallBackHandler(m_Handler);
                QQService.stopQQService(LoginActivity.this);
                finish();
            }
        }
    };

    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QQCallBackMsg.LOGIN_RESULT:
                    closeLoginingDlg();
                    if (msg.arg1 == QQLoginResultCode.SUCCESS) {    // 登录成功
                        LoginAccountList accountList = AppData.getAppData().getLoginAccountList();
                        int nPos = accountList.add(m_QQClient.getQQNum(),
                                m_QQClient.getQQPwd(), m_QQClient.getLoginStatus(), true, true);
                        accountList.setLastLoginUser(nPos);

                        String strAppPath = AppData.getAppData().getAppPath();
                        String strFileName = strAppPath + "LoginAccountList.dat";

                        accountList.saveFile(strFileName);

                        m_QQClient.setNullCallBackHandler(null);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else if (msg.arg1 == QQLoginResultCode.FAILED) {    // 登录失败
                        Toast.makeText(getBaseContext(),
                                R.string.login_failed, Toast.LENGTH_LONG).show();
                    } else if (msg.arg1 == QQLoginResultCode.PASSWORD_ERROR) {    // 密码错误
                        Toast.makeText(getBaseContext(),
                                R.string.id_or_pwd_err, Toast.LENGTH_LONG).show();
                    } else if (msg.arg1 == QQLoginResultCode.NEED_VERIFY_CODE
                            || msg.arg1 == QQLoginResultCode.VERIFY_CODE_ERROR) {    // 需要输入验证码
                        m_QQClient.setNullCallBackHandler(null);
                        startActivity(new Intent(LoginActivity.this, VerifyCodeActivity.class));
                        finish();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ExitApplication.getInstance().addActivity(this);

        sp = getSharedPreferences("theme", MODE_PRIVATE);
        color_theme = sp.getInt("color", -12627531);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintAlpha(0);
        //   tintManager.setNavigationBarTintEnabled(true);
        //    tintManager.setStatusBarTintResource(R.color.color_text_icons);


        initView();
        anim();
        rl.startAnimation(my_Translate);    // 载人时的动画

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String strQQNum = bundle.getString("qq_num");
            String strQQPwd = bundle.getString("qq_pwd");
            m_edtNum.setText(strQQNum);
            m_edtPwd.setText(strQQPwd);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_QQClient.setNullCallBackHandler(m_Handler);
    }

    private void initView() {
        m_QQClient = AppData.getAppData().getQQClient();
        m_QQClient.setCallBackHandler(m_Handler);

        rl = (LinearLayout) findViewById(R.id.rl);
        //	m_imgArrow = (ImageView)findViewById(R.id.login_imgDropdownArrow);
        m_imgAvatar = (ImageView) findViewById(R.id.login_imgAvatar);
        m_edtNum = (FloatingEditText) findViewById(R.id.login_edtNum);
        m_edtPwd = (FloatingEditText) findViewById(R.id.login_edtPwd);
        m_btnLogin = (PaperButton) findViewById(R.id.login_btnLogin);
        aboutbtn = (PaperButton) findViewById(R.id.about_btn);

        llbackground = (LinearLayout) findViewById(R.id.llbackground);
   //     llbackground.getBackground().setAlpha(128);

        m_btnLogin.setColor(color_theme);
        m_edtNum.setNormalColor(color_theme);
        m_edtNum.setHighlightedColor(color_theme);
        m_edtPwd.setNormalColor(color_theme);
        m_edtPwd.setHighlightedColor(color_theme);

        //	m_imgArrow.setOnClickListener(this);
        m_btnLogin.setOnClickListener(this);
        aboutbtn.setOnClickListener(this);

        initLoginingDlg();
    }

    private void anim() {
        my_Translate = AnimationUtils.loadAnimation(this, R.anim.my_translate);
        my_Rotate = AnimationUtils.loadAnimation(this, R.anim.my_rotate);
    }

    private int getScreenWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private int getScreenHeight(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    private void initLoginingDlg() {
        m_dlgLogining = new Dialog(this, R.style.dialog);
        m_dlgLogining.setContentView(R.layout.loginingdlg);
        //    CircularProgress cp= (CircularProgress) findViewById(R.id.progress_circular_login);
        //    cp.setColor(color_theme);

        Window win = m_dlgLogining.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();

        int cxScreen = getScreenWidth(this);
        int cyScreen = getScreenHeight(this);

        int cy = (int) getResources().getDimension(R.dimen.cyloginingdlg);
        int lrMargin = (int) getResources().getDimension(R.dimen.loginingdlg_lr_margin);
        int tMargin = (int) getResources().getDimension(R.dimen.loginingdlg_t_margin);

        params.x = -(cxScreen - lrMargin * 2) / 2;
        params.y = (-(cyScreen - cy) / 2) + tMargin;
        params.width = cxScreen;
        params.height = cy;

        m_dlgLogining.setCanceledOnTouchOutside(true);    //设置点击Dialog外部任意区域关闭Dialog
        //m_dlgLogining.setCancelable(false);		// 设置为false，按返回键不能退出
    }

    private void showLoginingDlg() {
        if (m_dlgLogining != null)
            m_dlgLogining.show();
    }

    private void closeLoginingDlg() {
        if (m_dlgLogining != null && m_dlgLogining.isShowing())
            m_dlgLogining.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //	case R.id.login_imgDropdownArrow:
            //		m_imgArrow.startAnimation(my_Rotate);
            //		break;

            case R.id.login_btnLogin:    // “登录”按钮
                m_strQQNum = m_edtNum.getText().toString();
                m_strQQPwd = m_edtPwd.getText().toString();

                if (Utils.isEmptyStr(m_strQQNum)) {
                    Toast.makeText(getBaseContext(),
                            R.string.enter_id, Toast.LENGTH_LONG).show();
                    return;
                }

                if (Utils.isEmptyStr(m_strQQPwd)) {
                    Toast.makeText(getBaseContext(),
                            R.string.enter_pwd, Toast.LENGTH_LONG).show();
                    return;
                }

                if (m_strQQNum.length() > 15) {
                    Toast.makeText(getBaseContext(),
                            R.string.enter_id_toolong, Toast.LENGTH_LONG).show();
                    return;
                }

                QQService.startQQService(this, m_hService);

                showLoginingDlg();

                break;
            case R.id.about_btn:
                Intent intent_about = new Intent();
                intent_about.setClass(this, AboutActivity.class);
                startActivity(intent_about);
                break;
        }
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
