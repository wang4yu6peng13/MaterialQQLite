package com.wyp.materialqqlite.ui;

import com.material.widget.CircularProgress;
import com.material.widget.FloatingEditText;

import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.LoginAccountList;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.qqclient.QQClient;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQCallBackMsg;
import com.wyp.materialqqlite.qqclient.protocol.protocoldata.QQLoginResultCode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class VerifyCodeActivity extends AbsActivity{
	//private TextView m_txtCancel;
	//private Button m_btnFinish;
	private ImageView m_imgVC;
	private CircularProgress m_prgLogining;
	private FloatingEditText m_edtVC;
	private QQClient m_QQClient;

         private SharedPreferences sp;
         private int color_theme;

	private Handler m_Handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			m_prgLogining.setVisibility(View.GONE);
			switch (msg.what) {
			case QQCallBackMsg.LOGIN_RESULT:
				if (msg.arg1 == QQLoginResultCode.SUCCESS) {	// 登录成功
					LoginAccountList accountList = AppData.getAppData().getLoginAccountList();
			    	int nPos = accountList.add(m_QQClient.getQQNum(), 
							m_QQClient.getQQPwd(), m_QQClient.getLoginStatus(), true, true);
			    	accountList.setLastLoginUser(nPos);
			    	
			    	String strAppPath = AppData.getAppData().getAppPath();
			    	String strFileName = strAppPath + "LoginAccountList.dat"; 
			    	accountList.saveFile(strFileName);

					m_QQClient.setNullCallBackHandler(m_Handler);
					startActivity(new Intent(VerifyCodeActivity.this, MainActivity.class));
					finish();
				} else if (msg.arg1 == QQLoginResultCode.FAILED) {	// 登录失败
					Toast.makeText(getBaseContext(), 
							R.string.login_failed, Toast.LENGTH_LONG).show();
					m_QQClient.setNullCallBackHandler(m_Handler);
					startActivity(new Intent(VerifyCodeActivity.this, LoginActivity.class));
					finish();
				} else if (msg.arg1 == QQLoginResultCode.PASSWORD_ERROR) {	// 密码错误
					Toast.makeText(getBaseContext(), 
							R.string.id_or_pwd_err, Toast.LENGTH_LONG).show();
					m_QQClient.setNullCallBackHandler(m_Handler);
					startActivity(new Intent(VerifyCodeActivity.this, LoginActivity.class));
					finish();
				} else if (msg.arg1 == QQLoginResultCode.NEED_VERIFY_CODE) {	// 需要输入验证码
					byte[] bytData = m_QQClient.getVerifyCodePic();
					Bitmap bmp = BitmapFactory.decodeByteArray(bytData, 0, bytData.length);
					m_imgVC.setImageBitmap(bmp);
				} else if (msg.arg1 == QQLoginResultCode.VERIFY_CODE_ERROR) {	// 验证码错误
					Toast.makeText(getBaseContext(), 
							R.string.vc_err, Toast.LENGTH_LONG).show();
                    m_edtVC.setText("");
					byte[] bytData = m_QQClient.getVerifyCodePic();
					Bitmap bmp = BitmapFactory.decodeByteArray(bytData, 0, bytData.length);
					m_imgVC.setImageBitmap(bmp);
				} 
				
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verifycode);
	}

    @Override
    public void setUpViews() {
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        sp = getSharedPreferences("theme", MODE_PRIVATE);
        color_theme = sp.getInt("color", -12627531);

		m_QQClient = AppData.getAppData().getQQClient();
		m_QQClient.setCallBackHandler(m_Handler);
		
	//	m_txtCancel = (TextView)findViewById(R.id.vc_txtCancel);
	//	m_btnFinish = (Button)findViewById(R.id.vc_btnFinish);
		m_imgVC = (ImageView)findViewById(R.id.vc_imgVC);
		m_prgLogining = (CircularProgress)findViewById(R.id.vc_prgLogining);
		m_edtVC = (FloatingEditText)findViewById(R.id.vc_edtVC);


        m_edtVC.setNormalColor(color_theme);
        m_edtVC.setHighlightedColor(color_theme);
        m_prgLogining.setColor(color_theme);

	//	m_txtCancel.setOnClickListener(this);
	//	m_btnFinish.setOnClickListener(this);
	//	m_edtVC.addTextChangedListener(this);
		
		byte[] bytData = m_QQClient.getVerifyCodePic();
		Bitmap bmp = BitmapFactory.decodeByteArray(bytData, 0, bytData.length);
		m_imgVC.setImageBitmap(bmp);

        mToolbar.setNavigationIcon(R.drawable.qqicon);

        mToolbar.inflateMenu(R.menu.menu_ok);

        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(onMenuItemClick);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_QQClient.setCallBackHandler(null);
                startActivity(new Intent(VerifyCodeActivity.this, LoginActivity.class));
                finish();
            }
		});
		// Navigation Icon 要設定在 setSupoortActionBar 才有作用
		// 否則會出現 back button
	}

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.ok:
                    String strVC = m_edtVC.getText().toString();
                    if (strVC.length() < 4)
                        return false;
                    m_prgLogining.setVisibility(View.VISIBLE);
                    m_QQClient.setVerifyCode(strVC);
                    m_QQClient.login();
                    break;

            }
            return true;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return true;
    }


}
