package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;


import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.Utility;

public class ReadmeActivity extends AbsActivity {

    public int statusBarHeight = 0;
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
        setContentView(R.layout.activity_readme);


    }

    @Override
    public void setUpViews() {
        View statusHeaderView = findViewById(R.id.statusHeaderView);
        statusHeaderView.getLayoutParams().height = statusBarHeight;
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        WebView webView= (WebView) findViewById(R.id.webview_readme);
        webView.loadUrl(" file:///android_asset/readme.html ");



    }



}
