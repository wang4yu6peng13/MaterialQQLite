package com.wyp.materialqqlite.ui;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.LoginAccountInfo;
import com.wyp.materialqqlite.LoginAccountList;
import com.wyp.materialqqlite.QQService;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQLoginResultCode;

public class SplashActivity extends Activity {
    private QQClient m_QQClient;
    private LoginAccountList m_accountList;

    private Handler m_hService = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (2 == msg.what) {		// 已经登录则直接进主窗口
                showMainActivity(3*1000);
            } else if (1 == msg.what) {	// 初始化成功
                LoginAccountInfo account = m_accountList.getLastLoginAccountInfo();
                if (account != null && account.m_bAutoLogin) {	// 有默认帐号则直接登录
                    m_QQClient.setCallBackHandler(m_Handler);
                    m_QQClient.setUser(account.m_strUser, account.m_strPwd);
                    m_QQClient.setLoginStatus(account.m_nStatus);
                    m_QQClient.login();
                } else {	// 否则跳转到登录窗口
                    showLoginActivity(3*1000);
                }
            } else {					// 初始化失败
                Toast.makeText(getBaseContext(),
                        R.string.qqservice_init_err, Toast.LENGTH_LONG).show();
                m_QQClient.setNullCallBackHandler(m_Handler);
                QQService.stopQQService(SplashActivity.this);
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
                    if (msg.arg1 == QQLoginResultCode.SUCCESS) {	// 登录成功
                        showMainActivity(0);
                    } else if (msg.arg1 == QQLoginResultCode.FAILED) {	// 登录失败
                        Toast.makeText(getBaseContext(),
                                R.string.login_failed, Toast.LENGTH_LONG).show();
                        showLoginActivity(0);
                    } else if (msg.arg1 == QQLoginResultCode.PASSWORD_ERROR) {	// 密码错误
                        Toast.makeText(getBaseContext(),
                                R.string.id_or_pwd_err, Toast.LENGTH_LONG).show();
                        showLoginActivity(0);
                    } else if (msg.arg1 == QQLoginResultCode.NEED_VERIFY_CODE
                            || msg.arg1 == QQLoginResultCode.VERIFY_CODE_ERROR) {	// 需要输入验证码
                        showVerifyCodeActivity(0);
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
        setContentView(R.layout.activity_splash);

        m_QQClient = AppData.getAppData().getQQClient();
        m_accountList = AppData.getAppData().getLoginAccountList();
        QQService.startQQService(this, m_hService);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        m_QQClient.setNullCallBackHandler(m_Handler);
    }

    private void showMainActivity(long delayMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                m_QQClient.setNullCallBackHandler(m_Handler);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, delayMillis);
    }

    private void showLoginActivity(long delayMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                m_QQClient.setNullCallBackHandler(m_Handler);

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                LoginAccountInfo account = m_accountList.getLastLoginAccountInfo();
                if (account != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("qq_num", account.m_strUser);
                    bundle.putString("qq_pwd", account.m_strPwd);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                finish();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, delayMillis);
    }

    private void showVerifyCodeActivity(long delayMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                m_QQClient.setNullCallBackHandler(m_Handler);
                startActivity(new Intent(SplashActivity.this, VerifyCodeActivity.class));
                finish();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, delayMillis);
    }
}
